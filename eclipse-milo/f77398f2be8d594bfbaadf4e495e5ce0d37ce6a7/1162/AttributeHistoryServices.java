/*
 * Copyright (c) 2017 Ari Suutari
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.html.
 */

package org.eclipse.milo.opcua.sdk.server.services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.milo.opcua.sdk.server.DiagnosticsContext;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.Session;
import org.eclipse.milo.opcua.sdk.server.api.AttributeHistoryManager.HistoryReadContext;
import org.eclipse.milo.opcua.sdk.server.api.AttributeHistoryManager.HistoryUpdateContext;
import org.eclipse.milo.opcua.sdk.server.api.Namespace;
import org.eclipse.milo.opcua.sdk.server.util.PendingHistoryRead;
import org.eclipse.milo.opcua.sdk.server.util.PendingHistoryUpdate;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.application.services.AttributeHistoryServiceSet;
import org.eclipse.milo.opcua.stack.core.application.services.ServiceRequest;
import org.eclipse.milo.opcua.stack.core.types.builtin.DiagnosticInfo;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryReadDetails;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryReadRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryReadResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryReadResult;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryUpdateDetails;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryUpdateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryUpdateResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryUpdateResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ResponseHeader;
import org.eclipse.milo.opcua.stack.core.util.FutureUtils;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.a;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.l;

public class AttributeHistoryServices implements AttributeHistoryServiceSet {

    private final ServiceMetric historyReadMetric = new ServiceMetric();
    private final ServiceMetric historyUpdateMetric = new ServiceMetric();

    @Override
    public void onHistoryRead(ServiceRequest<HistoryReadRequest, HistoryReadResponse> service) {
        historyReadMetric.record(service);

        HistoryReadRequest request = service.getRequest();

        DiagnosticsContext<HistoryReadValueId> diagnosticsContext = new DiagnosticsContext<>();

        OpcUaServer server = service.attr(ServiceAttributes.SERVER_KEY).get();
        Session session = service.attr(ServiceAttributes.SESSION_KEY).get();

        List<HistoryReadValueId> nodesToRead = l(request.getNodesToRead());

        if (nodesToRead.isEmpty()) {
            service.setServiceFault(StatusCodes.Bad_NothingToDo);
            return;
        }

        if (nodesToRead.size() > server.getConfig().getLimits().getMaxNodesPerRead().longValue()) {
            service.setServiceFault(StatusCodes.Bad_TooManyOperations);
            return;
        }

        if (request.getTimestampsToReturn() == null) {
            service.setServiceFault(StatusCodes.Bad_TimestampsToReturnInvalid);
            return;
        }


        List<PendingHistoryRead> pendingReads = newArrayListWithCapacity(nodesToRead.size());
        List<CompletableFuture<HistoryReadResult>> futures = newArrayListWithCapacity(nodesToRead.size());

        for (HistoryReadValueId id : nodesToRead) {
            PendingHistoryRead pending = new PendingHistoryRead(id);

            pendingReads.add(pending);
            futures.add(pending.getFuture());
        }

        // Group PendingReads by namespace and call read for each.

        Map<UShort, List<PendingHistoryRead>> byNamespace = pendingReads.stream()
            .collect(groupingBy(pending -> pending.getInput().getNodeId().getNamespaceIndex()));

        byNamespace.keySet().forEach(index -> {
            List<PendingHistoryRead> pending = byNamespace.get(index);

            CompletableFuture<List<HistoryReadResult>> future = new CompletableFuture<>();

            HistoryReadContext context = new HistoryReadContext(
                server, session, future, diagnosticsContext);

            server.getExecutorService().execute(() -> {
                Namespace namespace = server.getNamespaceManager().getNamespace(index);

                List<HistoryReadValueId> readValueIds = pending.stream()
                    .map(PendingHistoryRead::getInput)
                    .collect(toList());

                namespace.historyRead(
                    context,
                    (HistoryReadDetails) request.getHistoryReadDetails().decode(),
                    request.getTimestampsToReturn(),
                    readValueIds);
            });

            future.thenAccept(values -> {
                for (int i = 0; i < values.size(); i++) {
                    pending.get(i).getFuture().complete(values.get(i));
                }
            });
        });

        // When all PendingReads have been completed send a HistoryReadResponse with the values.

        FutureUtils.sequence(futures).thenAcceptAsync(values -> {
            ResponseHeader header = service.createResponseHeader();

            DiagnosticInfo[] diagnosticInfos =
                diagnosticsContext.getDiagnosticInfos(nodesToRead);

            HistoryReadResponse response = new HistoryReadResponse(
                header, a(values, HistoryReadResult.class), diagnosticInfos);

            service.setResponse(response);
        }, server.getExecutorService());
    }
    
    @Override
    public void onHistoryUpdate(ServiceRequest<HistoryUpdateRequest, HistoryUpdateResponse> service)
            throws UaException {
        historyUpdateMetric.record(service);

        HistoryUpdateRequest request = service.getRequest();

        DiagnosticsContext<HistoryUpdateDetails> diagnosticsContext = new DiagnosticsContext<>();

        OpcUaServer server = service.attr(ServiceAttributes.SERVER_KEY).get();
        Session session = service.attr(ServiceAttributes.SESSION_KEY).get();

        List<HistoryUpdateDetails> nodesToUpdate = l(request.getHistoryUpdateDetails())
                .stream().map(e -> (HistoryUpdateDetails) e.decode())
                .collect(Collectors.toList());

        if (nodesToUpdate.isEmpty()) {
            service.setServiceFault(StatusCodes.Bad_NothingToDo);
            return;
        }

        if (nodesToUpdate.size() > server.getConfig().getLimits().getMaxNodesPerWrite().intValue()) {
            service.setServiceFault(StatusCodes.Bad_TooManyOperations);
            return;
        }

        List<PendingHistoryUpdate> pendingUpdates = newArrayListWithCapacity(nodesToUpdate.size());
        List<CompletableFuture<HistoryUpdateResult>> futures = newArrayListWithCapacity(nodesToUpdate.size());

        for (HistoryUpdateDetails details : nodesToUpdate) {
            PendingHistoryUpdate pending = new PendingHistoryUpdate(details);

            pendingUpdates.add(pending);
            futures.add(pending.getFuture());
        }

        Map<UShort, List<PendingHistoryUpdate>> byNamespace = pendingUpdates.stream()
            .collect(groupingBy(pending -> pending.getInput().getNodeId().getNamespaceIndex()));

        byNamespace.keySet().forEach(index -> {
            List<PendingHistoryUpdate> pending = byNamespace.get(index);

            CompletableFuture<List<HistoryUpdateResult>> future = new CompletableFuture<>();

            HistoryUpdateContext context = new HistoryUpdateContext(
                server, session, future, diagnosticsContext);

            server.getExecutorService().execute(() -> {
                Namespace namespace = server.getNamespaceManager().getNamespace(index);

                List<HistoryUpdateDetails> updateDetails = pending.stream()
                    .map(PendingHistoryUpdate::getInput)
                    .collect(toList());

                namespace.historyUpdate(context, updateDetails);
            });

            future.thenAccept(statusCodes -> {
                for (int i = 0; i < statusCodes.size(); i++) {
                    pending.get(i).getFuture().complete(statusCodes.get(i));
                }
            });
        });

        FutureUtils.sequence(futures).thenAcceptAsync(values -> {
            ResponseHeader header = service.createResponseHeader();

            DiagnosticInfo[] diagnosticInfos =
                diagnosticsContext.getDiagnosticInfos(nodesToUpdate);

            HistoryUpdateResponse response = new HistoryUpdateResponse(
                header, a(values, HistoryUpdateResult.class), diagnosticInfos);

            service.setResponse(response);
        }, server.getExecutorService());

    }
}

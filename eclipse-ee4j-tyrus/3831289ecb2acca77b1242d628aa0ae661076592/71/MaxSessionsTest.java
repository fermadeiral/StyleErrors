/*
 * Copyright (c) 2014, 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.tyrus.test.e2e.appconfig;

import java.net.URI;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.core.MaxSessions;
import org.glassfish.tyrus.core.TyrusServerEndpointConfig;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.server.TyrusServerConfiguration;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the implementation of a sessions limiter for both programmatic and annotated endpoint.
 * <p/>
 * Tests number of both {@link javax.websocket.CloseReason.CloseCodes#NORMAL_CLOSURE} and {@link
 * javax.websocket.CloseReason.CloseCodes#TRY_AGAIN_LATER} close codes on client-side and the fact that onOpen and
 * onClose method on server-side are not called when the client is refused with {@link
 * javax.websocket.CloseReason.CloseCodes#TRY_AGAIN_LATER}.
 *
 * @author Ondrej Kosatka (ondrej.kosatka at oracle.com)
 */
public class MaxSessionsTest extends TestContainer {

    // Maximal number of open sessions
    public static final int SESSION_LIMIT = 3;

    public static final String PROGRAMMATIC = "/programmatic";
    public static final String ANNOTATED = "/annotated";
    public static final String SERVICE_ENDPOINT_PATH = "/service";

    public MaxSessionsTest() {
        setContextPath("/e2e-test-appconfig");
    }

    public static class ServerDeployApplicationConfig extends TyrusServerConfiguration {
        public ServerDeployApplicationConfig() {
            super(new HashSet<Class<?>>() {
                {
                    add(AnnotatedLimitedSessionsEndpoint.class);
                    add(ServiceEndpoint.class);

                }
            }, new HashSet<ServerEndpointConfig>() {
                {
                    add(TyrusServerEndpointConfig.Builder.create(LimitedSessionsEndpoint.class, PROGRAMMATIC)
                                                         .maxSessions(SESSION_LIMIT)
                                                         .build());
                }
            });
        }
    }

    @ServerEndpoint(value = SERVICE_ENDPOINT_PATH)
    public static class ServiceEndpoint {

        @OnMessage
        public String onMessage(String message) {

            if (message.equals(ANNOTATED)) {
                try {
                    if (AnnotatedLimitedSessionsEndpoint.openLatch.await(2, TimeUnit.SECONDS)
                            && AnnotatedLimitedSessionsEndpoint.closeLatch.await(2, TimeUnit.SECONDS)) {
                        if (!AnnotatedLimitedSessionsEndpoint.forbiddenClose.get()) {
                            return POSITIVE;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return NEGATIVE;
            } else if (message.equals(PROGRAMMATIC)) {
                try {
                    if (LimitedSessionsEndpoint.openLatch.await(2, TimeUnit.SECONDS) && LimitedSessionsEndpoint
                            .closeLatch.await(2, TimeUnit.SECONDS)) {
                        if (!LimitedSessionsEndpoint.forbiddenClose.get()) {
                            return POSITIVE;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return NEGATIVE;
            } else if (message.equals("reset")) {
                AnnotatedLimitedSessionsEndpoint.forbiddenClose.set(false);
                AnnotatedLimitedSessionsEndpoint.openLatch = new CountDownLatch(SESSION_LIMIT);
                AnnotatedLimitedSessionsEndpoint.closeLatch = new CountDownLatch(SESSION_LIMIT);
                LimitedSessionsEndpoint.forbiddenClose.set(false);
                LimitedSessionsEndpoint.openLatch = new CountDownLatch(SESSION_LIMIT);
                LimitedSessionsEndpoint.closeLatch = new CountDownLatch(SESSION_LIMIT);
                return POSITIVE;
            }

            return NEGATIVE;
        }
    }


    /**
     * Annotated endpoint
     */
    @MaxSessions(SESSION_LIMIT)
    @ServerEndpoint(value = ANNOTATED)
    public static class AnnotatedLimitedSessionsEndpoint {

        // onClose (on server-side) should be called only for successfully opened sessions
        public static final AtomicBoolean forbiddenClose = new AtomicBoolean(false);

        public static CountDownLatch openLatch;
        public static CountDownLatch closeLatch;

        @OnOpen
        public void onOpen(Session s) {
            openLatch.countDown();
            System.out.println("Client connected to the server!");
        }

        @OnMessage
        public String onMessage(String message) {
            return message;
        }

        @OnClose
        public void onClose(Session session, CloseReason closeReason) {
            System.out.printf("Server onClose %s, %s%n", session.getId(), closeReason);
            if (closeReason.getCloseCode().getCode() == CloseReason.CloseCodes.TRY_AGAIN_LATER.getCode()) {
                forbiddenClose.set(true);
            } else {
                closeLatch.countDown();
            }
        }
    }

    /**
     * Programmatic endpoint
     */
    public static class LimitedSessionsEndpoint extends Endpoint {

        // onClose (on server-side) should be called only for successfully opened sessions
        public static final AtomicBoolean forbiddenClose = new AtomicBoolean(false);

        public static CountDownLatch openLatch;
        public static CountDownLatch closeLatch;

        @Override
        public void onOpen(Session s, EndpointConfig endpointConfig) {
            openLatch.countDown();
            System.out.println("Client connected to the server!");

        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            System.out.printf("Server onClose %s, %s%n", session.getId(), closeReason);
            if (closeReason.getCloseCode().getCode() == CloseReason.CloseCodes.TRY_AGAIN_LATER.getCode()) {
                forbiddenClose.set(true);
            } else {
                closeLatch.countDown();
            }
        }

    }

    @Test
    public void testAnnotated() throws DeploymentException {
        testSessionLimit(getURI(AnnotatedLimitedSessionsEndpoint.class), ANNOTATED);
    }

    @Test
    public void testProgrammatic() throws DeploymentException {
        testSessionLimit(getURI(PROGRAMMATIC), PROGRAMMATIC);
    }

    public void testSessionLimit(URI uri, String type) throws DeploymentException {


        final CountDownLatch normalCloseLatch = new CountDownLatch(SESSION_LIMIT);
        final CountDownLatch limitCloseLatch = new CountDownLatch(1);

        final Server server = startServer(ServerDeployApplicationConfig.class);

        final int numberOfSessions = SESSION_LIMIT + 1;
        Session[] sessions = new Session[numberOfSessions];

        try {
            final ClientManager client = createClient();

            // service endpoint reset
            testViaServiceEndpoint(client, ServiceEndpoint.class, POSITIVE, "reset");

            //try to create session
            for (int i = 0; i < numberOfSessions; i++) {

                sessions[i] = client.connectToServer(new Endpoint() {
                    @Override
                    public void onOpen(Session session, EndpointConfig config) {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            @Override
                            public void onMessage(String message) {
                                System.out.println(String.format("Client received message: '%s'", message));
                            }
                        });
                    }

                    @Override
                    public void onClose(Session session, CloseReason closeReason) {
                        System.out.println(String.format("Client session closed with reason: '%s'", closeReason));
                        if (closeReason.getCloseCode().getCode() == CloseReason.CloseCodes.TRY_AGAIN_LATER.getCode()) {
                            limitCloseLatch.countDown();
                        } else if (closeReason.getCloseCode().getCode()
                                == CloseReason.CloseCodes.NORMAL_CLOSURE.getCode()) {
                            normalCloseLatch.countDown();
                        }
                    }
                }, ClientEndpointConfig.Builder.create().build(), uri);
            }

            // close opened sessions
            for (int i = 0; i < numberOfSessions; i++) {
                if (i < SESSION_LIMIT) {
                    assertTrue("Session in limit is closed!", sessions[i].isOpen());
                    sessions[i].close();
                } else {
                    assertTrue("Session should be closed just once with close code 1013 - Try Again Later",
                               limitCloseLatch.await(1, TimeUnit.SECONDS));
                    assertFalse("Session should be closed due the limit!", sessions[i].isOpen());
                }
            }

            assertTrue(String.format("Normal closure of session is expected %d times", SESSION_LIMIT),
                       normalCloseLatch.await(1, TimeUnit.SECONDS));

            // onClose (on server-side) should be called only for successfully opened sessions
            testViaServiceEndpoint(client, ServiceEndpoint.class, POSITIVE, type);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }
}

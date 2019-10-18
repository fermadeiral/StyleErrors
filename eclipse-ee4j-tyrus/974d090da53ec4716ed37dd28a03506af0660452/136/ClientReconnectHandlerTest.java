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

package org.glassfish.tyrus.test.standard_config;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class ClientReconnectHandlerTest extends TestContainer {

    @ServerEndpoint("/clientReconnectHandlerTest/disconnectingEndpoint")
    public static class DisconnectingEndpoint {
        @OnMessage
        public void onMessage(Session session, String message) throws IOException {
            session.close();
        }
    }

    @Test
    public void testReconnectDisconnect() throws DeploymentException {
        final Server server = startServer(DisconnectingEndpoint.class);
        try {
            final CountDownLatch onDisconnectLatch = new CountDownLatch(1);

            ClientManager client = createClient();

            ClientManager.ReconnectHandler reconnectHandler = new ClientManager.ReconnectHandler() {

                private final AtomicInteger counter = new AtomicInteger(0);

                @Override
                public boolean onDisconnect(CloseReason closeReason) {
                    final int i = counter.incrementAndGet();
                    if (i <= 3) {
                        System.out.println("### Reconnecting... (reconnect count: " + i + ")");
                        return true;
                    } else {
                        onDisconnectLatch.countDown();
                        return false;
                    }
                }

                @Override
                public long getDelay() {
                    return 0;
                }
            };

            client.getProperties().put(ClientProperties.RECONNECT_HANDLER, reconnectHandler);

            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try {
                        session.getBasicRemote().sendText("Do or do not, there is no try.");
                    } catch (IOException e) {
                        // do nothing.
                    }
                }
            }, ClientEndpointConfig.Builder.create().build(), getURI(DisconnectingEndpoint.class));

            assertTrue(onDisconnectLatch.await(3, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testReconnectConnectFailure() throws DeploymentException {
        final Server server = startServer(DisconnectingEndpoint.class);
        try {
            final CountDownLatch messageLatch = new CountDownLatch(1);

            ClientManager client = createClient();

            ClientManager.ReconnectHandler reconnectHandler = new ClientManager.ReconnectHandler() {

                private final AtomicInteger counter = new AtomicInteger(0);

                @Override
                public boolean onConnectFailure(Exception exception) {
                    final int i = counter.incrementAndGet();
                    if (i <= 3) {
                        System.out.println(
                                "### Reconnecting... (reconnect count: " + i + ") " + exception.getMessage());
                        return true;
                    } else {
                        messageLatch.countDown();
                        return false;
                    }
                }

                @Override
                public long getDelay() {
                    return 0;
                }
            };

            client.getProperties().put(ClientProperties.RECONNECT_HANDLER, reconnectHandler);

            try {
                client.connectToServer(new Endpoint() {
                    @Override
                    public void onOpen(Session session, EndpointConfig config) {
                        try {
                            session.getBasicRemote().sendText("Do or do not, there is no try.");
                        } catch (IOException e) {
                            // do nothing.
                        }
                    }
                }, ClientEndpointConfig.Builder.create().build(), URI.create("ws://invalid.url"));
            } catch (Exception e) {
                //ignore.
            }

            assertTrue(messageLatch.await(3, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }
}

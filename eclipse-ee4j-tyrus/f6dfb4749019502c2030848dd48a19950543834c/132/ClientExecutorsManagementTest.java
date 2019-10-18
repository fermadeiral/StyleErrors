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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests life cycle of executor services managed by the client manager.
 *
 * @author Petr Janouch
 */
public class ClientExecutorsManagementTest extends TestContainer {

    /**
     * Test basic executor services life cycle.
     */
    @Test
    public void testBasicLifecycle() {
        Server server = null;
        try {
            server = startServer(AnnotatedServerEndpoint.class);
            ClientManager clientManager1 = createClient();
            Session session1 = clientManager1
                    .connectToServer(AnnotatedClientEndpoint.class, getURI(AnnotatedServerEndpoint.class));
            ExecutorService executorService1 = clientManager1.getExecutorService();
            ScheduledExecutorService scheduledExecutorService1 = clientManager1.getScheduledExecutorService();

            ClientManager clientManager2 = createClient();
            Session session2 = clientManager2
                    .connectToServer(AnnotatedClientEndpoint.class, getURI(AnnotatedServerEndpoint.class));
            ExecutorService executorService2 = clientManager2.getExecutorService();
            ScheduledExecutorService scheduledExecutorService2 = clientManager2.getScheduledExecutorService();

            Session session3 = clientManager1
                    .connectToServer(AnnotatedClientEndpoint.class, getURI(AnnotatedServerEndpoint.class));
            ExecutorService executorService3 = clientManager1.getExecutorService();
            ScheduledExecutorService scheduledExecutorService3 = clientManager1.getScheduledExecutorService();

            // executors from the same container should be the same
            assertTrue(executorService1 == executorService3);
            assertTrue(scheduledExecutorService1 == scheduledExecutorService3);

            assertTrue(executorService1 != executorService2);
            assertTrue(scheduledExecutorService1 != scheduledExecutorService2);

            assertFalse(executorService1.isShutdown());
            assertFalse(scheduledExecutorService1.isShutdown());

            assertFalse(executorService2.isShutdown());
            assertFalse(scheduledExecutorService2.isShutdown());

            session1.close();
            session2.close();

            assertTrue(executorService2.awaitTermination(5, TimeUnit.SECONDS));
            assertTrue(scheduledExecutorService2.awaitTermination(5, TimeUnit.SECONDS));

            // closing session1 should not close executorService1 and scheduledExecutorService1 as it is still used
            // by session3
            assertFalse(executorService1.isShutdown());
            assertFalse(scheduledExecutorService1.isShutdown());

            session3.close();

            assertTrue(executorService1.awaitTermination(5, TimeUnit.SECONDS));
            assertTrue(scheduledExecutorService1.awaitTermination(5, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    /**
     * Test that different situations that can cause connect to fail will not cause container resources not to be
     * freed.
     * <p/>
     * (Client manager counts active connections. This test tests, that connection failures caused by different
     * situations are registered by the connection counter.)
     */
    @Test
    public void testConnectionFail() {
        Server server = null;
        try {
            server = startServer(AnnotatedServerEndpoint.class);
            ClientManager clientManager = createClient();

            Session session =
                    clientManager.connectToServer(AnnotatedClientEndpoint.class, getURI(AnnotatedServerEndpoint.class));
            try {
                clientManager.connectToServer(FaultyEndpoint.class, getURI(AnnotatedServerEndpoint.class));
                fail();
            } catch (Exception e) {
                // exception is expected
            }

            try {
                clientManager.connectToServer(AnnotatedClientEndpoint.class, URI.create("ws://nonExistentServer.com"));
                fail();
            } catch (Exception e) {
                // exception is expected
            }

            try {
                clientManager.connectToServer(AnnotatedClientEndpoint.class, getURI("/nonExistentEndpoint"));
                fail();
            } catch (Exception e) {
                // exception is expected
            }

            CountDownLatch blockResponseLatch = new CountDownLatch(1);
            HttpServer lazyServer = getLazyServer(blockResponseLatch);
            clientManager.getProperties().put(ClientProperties.HANDSHAKE_TIMEOUT, 2000);
            try {
                clientManager.connectToServer(
                        AnnotatedClientEndpoint.class, URI.create("ws://localhost:8026/lazyServer"));
                fail();
            } catch (Exception e) {
                // exception is expected
            } finally {
                blockResponseLatch.countDown();
                lazyServer.shutdown();
            }

            ExecutorService executorService = clientManager.getExecutorService();
            ScheduledExecutorService scheduledExecutorService = clientManager.getScheduledExecutorService();

            assertFalse(executorService.isShutdown());
            assertFalse(scheduledExecutorService.isShutdown());

            // closing the only successfully established connection should cause the executors to be released
            session.close();

            assertTrue(executorService.awaitTermination(5, TimeUnit.SECONDS));
            assertTrue(scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    /**
     * Test that if executor services have been destroyed, new ones will be created if the client manager creates new
     * connections.
     */
    @Test
    public void testConnectAfterClose() {
        Server server = null;
        try {
            server = startServer(AnnotatedServerEndpoint.class);
            ClientManager clientManager = createClient();

            Session session =
                    clientManager.connectToServer(AnnotatedClientEndpoint.class, getURI(AnnotatedServerEndpoint.class));
            session.close();

            Session session2 =
                    clientManager.connectToServer(AnnotatedClientEndpoint.class, getURI(AnnotatedServerEndpoint.class));

            ExecutorService executorService = clientManager.getExecutorService();
            ScheduledExecutorService scheduledExecutorService = clientManager.getScheduledExecutorService();

            assertFalse(executorService.isShutdown());
            assertFalse(scheduledExecutorService.isShutdown());

            session2.close();

            assertTrue(executorService.awaitTermination(5, TimeUnit.SECONDS));
            assertTrue(scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    /**
     * Test that executor services get destroyed if reconnect is used.
     */
    @Test
    public void testReconnect() {
        Server server = null;
        try {
            server = startServer(ReconnectServerEndpoint.class);
            ClientManager clientManager = createClient();
            clientManager.getProperties().put(ClientProperties.RECONNECT_HANDLER, new ClientManager.ReconnectHandler() {
                private int counter = 0;

                @Override
                public boolean onDisconnect(CloseReason closeReason) {
                    counter++;

                    // reconnect once
                    if (counter < 2) {
                        return true;
                    }

                    return false;
                }

                @Override
                public long getDelay() {
                    return 0;
                }
            });

            final AtomicReference<Session> session = new AtomicReference<Session>();
            // connect once and reconnect once
            final CountDownLatch onOpenLatch = new CountDownLatch(2);
            clientManager.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session s, EndpointConfig config) {
                    session.set(s);
                    onOpenLatch.countDown();
                }
            }, getURI(ReconnectServerEndpoint.class));

            ExecutorService executorService = clientManager.getExecutorService();
            ScheduledExecutorService scheduledExecutorService = clientManager.getScheduledExecutorService();

            // force reconnect
            session.get().getBasicRemote().sendText("Close");
            assertTrue(onOpenLatch.await(5, TimeUnit.SECONDS));

            ExecutorService executorService2 = clientManager.getExecutorService();
            ScheduledExecutorService scheduledExecutorService2 = clientManager.getScheduledExecutorService();

            assertFalse(executorService2.isShutdown());
            assertFalse(scheduledExecutorService2.isShutdown());

            session.get().close();

            assertTrue(executorService.awaitTermination(5, TimeUnit.SECONDS));
            assertTrue(scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS));
            assertTrue(executorService2.awaitTermination(5, TimeUnit.SECONDS));
            assertTrue(scheduledExecutorService2.awaitTermination(5, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    /**
     * Test that calling shut down on client manager does not clash with automatic executors management and does not
     * cause an error when there are still some sessions open.
     */
    @Test
    public void explicitShutDownTest() {
        Server server = null;
        try {
            server = startServer(AnnotatedServerEndpoint.class);
            ClientManager clientManager = createClient();
            Session session =
                    clientManager.connectToServer(AnnotatedClientEndpoint.class, getURI(AnnotatedServerEndpoint.class));
            clientManager.shutdown();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    /**
     * Test that managed executors do not get closed if the last connection is closed.
     */
    @Test
    public void managedExecutorsTest() {
        if (System.getProperty("tyrus.test.host") == null) {
            return;
        }

        Server server = null;
        try {
            server = startServer(ManagedContainerEndpoint.class, AnnotatedServerEndpoint.class);
            ClientManager clientManager = createClient();
            CountDownLatch messageLatch = new CountDownLatch(1);
            Session session = clientManager
                    .connectToServer(new AnnotatedClientEndpoint(messageLatch), getURI(ManagedContainerEndpoint.class));
            session.getBasicRemote().sendText(getURI(AnnotatedServerEndpoint.class).toString());

            assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    private HttpServer getLazyServer(final CountDownLatch blockResponseLatch) throws IOException {
        HttpServer server = HttpServer.createSimpleServer("/lazyServer", "localhost", 8026);
        server.getServerConfiguration().addHttpHandler(
                new HttpHandler() {
                    public void service(Request request, Response response) throws Exception {
                        blockResponseLatch.await(1, TimeUnit.MINUTES);
                    }
                }
        );

        server.start();
        return server;
    }

    @ServerEndpoint("/clientExecutorsEchoEndpoint")
    public static class AnnotatedServerEndpoint {

        @OnOpen
        public void onOpen(Session session) {
            session.setMaxIdleTimeout(0);
        }

    }

    @ClientEndpoint
    public static class AnnotatedClientEndpoint {

        private final CountDownLatch messageLatch;

        AnnotatedClientEndpoint(CountDownLatch messageLatch) {
            this.messageLatch = messageLatch;
        }

        @OnMessage
        public void onMessage(Session session, String message) {
            messageLatch.countDown();
        }
    }

    public static class FaultyEndpoint {
    }

    @ServerEndpoint("/clientExecutorsReconnectEndpoint")
    public static class ReconnectServerEndpoint {

        @OnMessage
        public void onMessage(String message, Session session) throws IOException {
            session.close();
        }
    }

    @ServerEndpoint("/managedContainerEndpoint")
    public static class ManagedContainerEndpoint {

        @OnMessage
        public void onMessage(Session session, String message) throws IOException, DeploymentException {
            // one option for obtaining a container
            Session s = ContainerProvider.getWebSocketContainer()
                                         .connectToServer(AnnotatedClientEndpoint.class, URI.create(message));
            s.close();

            // another option for obtaining a container
            s = session.getContainer().connectToServer(AnnotatedClientEndpoint.class, URI.create(message));
            s.close();

            /* An IllegalStateException is thrown if any of the lifecycle operations are invoked on a managed executor
            service, so if the test gets here it means Tyrus does not try to shut down the managed executor service */
            session.getBasicRemote().sendText("OK");
        }
    }
}

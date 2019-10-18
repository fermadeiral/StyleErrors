/*
 * Copyright (c) 2013, 2017 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.tyrus.test.servlet.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.test.tools.TestContainer;
import org.glassfish.tyrus.tests.servlet.session.IdleTimeoutReceivingEndpoint;
import org.glassfish.tyrus.tests.servlet.session.IdleTimeoutSendingEndpoint;
import org.glassfish.tyrus.tests.servlet.session.IdleTimeoutSendingPingEndpoint;
import org.glassfish.tyrus.tests.servlet.session.ServiceEndpoint;

import org.junit.Test;

import junit.framework.Assert;

/**
 * @author Stepan Kopriva (stepan.kopriva at oracle.com)
 */
public class SessionIdleTimeoutTest extends TestContainer {
    private static final String CONTEXT_PATH = "/session-test";
    private static String messageReceived = "not received.";

    public SessionIdleTimeoutTest() {
        setContextPath(CONTEXT_PATH);
    }

    @Test
    public void testIdleTimeoutRaised() throws DeploymentException {

        final CountDownLatch clientLatch = new CountDownLatch(1);
        final Server server = startServer(IdleTimeoutReceivingEndpoint.class, ServiceEndpoint.class);
        resetServerEndpoints();

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {

                }

                @Override
                public void onClose(javax.websocket.Session session, javax.websocket.CloseReason closeReason) {

                }

                @Override
                public void onError(javax.websocket.Session session, Throwable thr) {
                    thr.printStackTrace();
                }
            }, cec, getURI(IdleTimeoutReceivingEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
            clientLatch.await(IdleTimeoutReceivingEndpoint.TIMEOUT + 100, TimeUnit.MILLISECONDS);

            final CountDownLatch messageLatch = new CountDownLatch(1);

            ClientManager client2 = createClient();
            client2.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String s) {
                            messageReceived = s;
                            messageLatch.countDown();
                        }
                    });
                    try {
                        session.getBasicRemote().sendText("idleTimeoutReceiving");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(javax.websocket.Session session, Throwable thr) {
                    thr.printStackTrace();
                }

            }, cec, getURI(ServiceEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
            messageLatch.await(1, TimeUnit.SECONDS);
            Assert.assertEquals("Latch is not 0", 0, messageLatch.getCount());
            Assert.assertTrue("Received message is 1.", messageReceived.equals("1"));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testNoIdleTimeoutRaisedReceiving() throws DeploymentException {

        final CountDownLatch clientLatch = new CountDownLatch(1);
        final Server server = startServer(IdleTimeoutReceivingEndpoint.class, ServiceEndpoint.class);
        resetServerEndpoints();
        final Timer timer = new Timer();

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(final Session session, EndpointConfig endpointConfig) {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                session.getBasicRemote().sendText("Some text.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0, 300);
                }

                @Override
                public void onClose(javax.websocket.Session session, javax.websocket.CloseReason closeReason) {
                    timer.cancel();
                }

                @Override
                public void onError(javax.websocket.Session session, Throwable thr) {
                    thr.printStackTrace();
                }
            }, cec, getURI(IdleTimeoutReceivingEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
            clientLatch.await(IdleTimeoutReceivingEndpoint.TIMEOUT * 3, TimeUnit.MILLISECONDS);

            final CountDownLatch messageLatch = new CountDownLatch(1);

            ClientManager client2 = createClient();
            client2.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String s) {
                            System.out.println("Received message: " + s);
                            messageReceived = s;
                            messageLatch.countDown();
                        }
                    });
                    try {
                        session.getBasicRemote().sendText("idleTimeoutReceiving");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(javax.websocket.Session session, Throwable thr) {
                    thr.printStackTrace();
                }

            }, cec, getURI(ServiceEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
            messageLatch.await(1, TimeUnit.SECONDS);
            Assert.assertTrue("Received message is 0.", messageReceived.equals("0"));
            Assert.assertEquals("Latch is not 0", 0, messageLatch.getCount());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testNoIdleTimeoutRaisedReceivingPing() throws DeploymentException {

        final CountDownLatch clientLatch = new CountDownLatch(1);
        final Server server = startServer(IdleTimeoutReceivingEndpoint.class, ServiceEndpoint.class);
        final byte[] data = new byte[]{1, 2, 3};
        final Timer timer = new Timer();
        resetServerEndpoints();

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(final Session session, EndpointConfig endpointConfig) {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                session.getBasicRemote().sendPing(ByteBuffer.wrap(data));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0, 300);
                }

                @Override
                public void onClose(javax.websocket.Session session, javax.websocket.CloseReason closeReason) {
                    timer.cancel();
                }

                @Override
                public void onError(javax.websocket.Session session, Throwable thr) {
                    thr.printStackTrace();
                }
            }, cec, getURI(IdleTimeoutReceivingEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
            clientLatch.await(IdleTimeoutReceivingEndpoint.TIMEOUT * 3, TimeUnit.MILLISECONDS);

            final CountDownLatch messageLatch = new CountDownLatch(1);

            ClientManager client2 = createClient();
            client2.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String s) {
                            System.out.println("Received message: " + s);
                            messageReceived = s;
                            messageLatch.countDown();
                        }
                    });
                    try {
                        session.getBasicRemote().sendText("idleTimeoutReceiving");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(javax.websocket.Session session, Throwable thr) {
                    thr.printStackTrace();
                }

            }, cec, getURI(ServiceEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
            messageLatch.await(1, TimeUnit.SECONDS);
            Assert.assertTrue("Received message is 0.", messageReceived.equals("0"));
            Assert.assertEquals("Latch is not 0", 0, messageLatch.getCount());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testIdleTimeoutNotRaisedServerSending() throws DeploymentException {

        final CountDownLatch clientLatch = new CountDownLatch(1);
        final Server server = startServer(IdleTimeoutSendingEndpoint.class, ServiceEndpoint.class);
        resetServerEndpoints();

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client1 = createClient();
            Session clientSession = client1.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {

                        }
                    });
                }

                @Override
                public void onClose(javax.websocket.Session session, javax.websocket.CloseReason closeReason) {

                }

                @Override
                public void onError(javax.websocket.Session session, Throwable thr) {
                    thr.printStackTrace();
                }
            }, cec, getURI(IdleTimeoutSendingEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
            clientLatch.await(IdleTimeoutSendingEndpoint.TIMEOUT * 2, TimeUnit.MILLISECONDS);
            clientSession.getBasicRemote().sendText("Just some text.");

            final CountDownLatch messageLatch = new CountDownLatch(1);

            ClientManager client2 = createClient();
            client2.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String s) {
                            messageReceived = s;
                            messageLatch.countDown();
                        }
                    });
                    try {
                        session.getBasicRemote().sendText("idleTimeoutSending");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(javax.websocket.Session session, Throwable thr) {
                    thr.printStackTrace();
                }

            }, cec, getURI(ServiceEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
            messageLatch.await(1, TimeUnit.SECONDS);
            Assert.assertTrue("Received message is 0.", messageReceived.equals("0"));
            Assert.assertEquals("Latch is not 0", 0, messageLatch.getCount());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testIdleTimeoutNotRaisedServerSendingPing() throws DeploymentException {

        final CountDownLatch clientLatch = new CountDownLatch(1);
        final Server server = startServer(IdleTimeoutSendingPingEndpoint.class, ServiceEndpoint.class);
        resetServerEndpoints();

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client1 = createClient();
            Session clientSession = client1.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {

                        }
                    });
                }

                @Override
                public void onClose(javax.websocket.Session session, javax.websocket.CloseReason closeReason) {

                }

                @Override
                public void onError(javax.websocket.Session session, Throwable thr) {
                    thr.printStackTrace();
                }
            }, cec, getURI(IdleTimeoutSendingPingEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
            clientLatch.await(IdleTimeoutSendingPingEndpoint.TIMEOUT * 2, TimeUnit.MILLISECONDS);
            clientSession.getBasicRemote().sendText("Just some text.");

            final CountDownLatch messageLatch = new CountDownLatch(1);

            ClientManager client2 = createClient();
            client2.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String s) {
                            messageReceived = s;
                            messageLatch.countDown();
                        }
                    });
                    try {
                        session.getBasicRemote().sendText("idleTimeoutSendingPing");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(javax.websocket.Session session, Throwable thr) {
                    thr.printStackTrace();
                }

            }, cec, getURI(ServiceEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
            messageLatch.await(1, TimeUnit.SECONDS);
            Assert.assertTrue("Received message is 0.", messageReceived.equals("0"));
            Assert.assertEquals("Latch is not 0", 0, messageLatch.getCount());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    private void resetServerEndpoints() {
        final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

        try {
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String s) {

                        }
                    });
                    try {
                        session.getBasicRemote().sendText("reset");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Session session, Throwable thr) {
                    thr.printStackTrace();
                }

            }, cec, getURI(ServiceEndpoint.class.getAnnotation(ServerEndpoint.class).value()));
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

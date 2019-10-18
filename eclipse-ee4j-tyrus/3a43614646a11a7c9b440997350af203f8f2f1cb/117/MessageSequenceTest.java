/*
 * Copyright (c) 2015, 2017 Oracle and/or its affiliates. All rights reserved.
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
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MessageSequenceTest extends TestContainer {

    private static final String SENT_MESSAGE = "Always pass on what you have learned.";

    @ServerEndpoint("/messageSequenceTest")
    public static class MessageSequenceTestEndpoint {

        @OnMessage
        public void onMessage(String message, boolean end) throws IOException {
            System.out.println("### text: " + message + " end: " + end);
        }

        @OnMessage
        public void onMessage(ByteBuffer message, boolean end) throws IOException {
            byte[] array = new byte[message.remaining()];
            message.get(array);

            System.out.println("### binary: " + new String(array) + " end: " + end);
        }


        @OnError
        public void onError(Throwable t) {
            System.out.println("### OnError: ");
            t.printStackTrace();
        }

        @OnClose
        public void onClose(CloseReason closeReason) {
            System.out.println("### " + closeReason);
        }
    }

    @Test
    public void testMessageSequenceTextPartialBinaryWhole() throws URISyntaxException, IOException, DeploymentException,
            InterruptedException {
        Server server = startServer(MessageSequenceTestEndpoint.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(1);

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
                        @Override
                        public void onMessage(String message, boolean last) {
                            System.out.println("### " + message + " " + last);
                        }
                    });

                    try {

                        session.getBasicRemote().sendText(SENT_MESSAGE + "1", false);
                        session.getAsyncRemote().sendBinary(ByteBuffer.wrap((SENT_MESSAGE + "2").getBytes())); // ISE

                    } catch (IOException ignored) {
                    } catch (IllegalStateException e) {
                        latch.countDown();
                    }
                }
            }, cec, getURI(MessageSequenceTestEndpoint.class));

            assertTrue(latch.await(3, TimeUnit.SECONDS));

        } catch (DeploymentException e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testMessageSequenceTextPartialTextWhole() throws URISyntaxException, IOException, DeploymentException,
            InterruptedException {
        Server server = startServer(MessageSequenceTestEndpoint.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(1);

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
                        @Override
                        public void onMessage(String message, boolean last) {
                            System.out.println("### " + message + " " + last);
                        }
                    });

                    try {

                        session.getBasicRemote().sendText(SENT_MESSAGE + "1", false);
                        session.getAsyncRemote().sendText(SENT_MESSAGE + "2"); // ISE

                    } catch (IOException ignored) {
                    } catch (IllegalStateException e) {
                        latch.countDown();
                    }
                }
            }, cec, getURI(MessageSequenceTestEndpoint.class));

            assertTrue(latch.await(3, TimeUnit.SECONDS));

        } catch (DeploymentException e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testMessageSequenceTextPartialBinaryPartial() throws URISyntaxException, IOException,
            DeploymentException, InterruptedException {
        Server server = startServer(MessageSequenceTestEndpoint.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(1);

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
                        @Override
                        public void onMessage(String message, boolean last) {
                            System.out.println("### " + message + " " + last);
                        }
                    });

                    try {

                        session.getBasicRemote().sendText(SENT_MESSAGE, false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(SENT_MESSAGE.getBytes()), false); // ISE

                    } catch (IOException ignored) {
                    } catch (IllegalStateException e) {
                        latch.countDown();
                    }
                }
            }, cec, getURI(MessageSequenceTestEndpoint.class));

            assertTrue(latch.await(3, TimeUnit.SECONDS));

        } catch (DeploymentException e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testMessageSequenceTextPartialTextWholeWithShortWait() throws URISyntaxException, IOException,
            DeploymentException, InterruptedException {
        Server server = startServer(MessageSequenceTestEndpoint.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(1);

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
                        @Override
                        public void onMessage(String message, boolean last) {
                            System.out.println("### " + message + " " + last);
                        }
                    });

                    ExecutorService executorService = Executors.newCachedThreadPool();

                    try {

                        // partial message start
                        session.getBasicRemote().sendText(SENT_MESSAGE, false);

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                // ISE after {@value ProtocolHandler#SEND_TIMEOUT}
                                session.getBasicRemote().sendText(SENT_MESSAGE);
                                latch.countDown();
                                return null;
                            }
                        });

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                // should finish the message and unblock "whole message send" from previous callable
                                session.getBasicRemote().sendText(SENT_MESSAGE, true);
                                return null;
                            }
                        });


                    } catch (IOException ignored) {
                    }
                }
            }, cec, getURI(MessageSequenceTestEndpoint.class));

            assertTrue(latch.await(3, TimeUnit.SECONDS));

        } catch (DeploymentException e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testMessageSequenceTextPartialBinaryWholeWithShortWait() throws URISyntaxException, IOException,
            DeploymentException, InterruptedException {
        Server server = startServer(MessageSequenceTestEndpoint.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(1);

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
                        @Override
                        public void onMessage(String message, boolean last) {
                            System.out.println("### " + message + " " + last);
                        }
                    });

                    ExecutorService executorService = Executors.newCachedThreadPool();

                    try {

                        // partial message start
                        session.getBasicRemote().sendText(SENT_MESSAGE, false);

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                // ISE after {@value ProtocolHandler#SEND_TIMEOUT}
                                session.getBasicRemote().sendBinary(ByteBuffer.wrap(SENT_MESSAGE.getBytes()));
                                latch.countDown();
                                return null;
                            }
                        });

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                // should finish the message and unblock "whole message send" from previous callable
                                session.getBasicRemote().sendText(SENT_MESSAGE, true);
                                return null;
                            }
                        });


                    } catch (IOException ignored) {
                    }
                }
            }, cec, getURI(MessageSequenceTestEndpoint.class));

            assertTrue(latch.await(3, TimeUnit.SECONDS));

        } catch (DeploymentException e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testMessageSequenceTextPartialTextWholeWithLongWait() throws URISyntaxException, IOException,
            DeploymentException, InterruptedException {
        Server server = startServer(MessageSequenceTestEndpoint.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(1);

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
                        @Override
                        public void onMessage(String message, boolean last) {
                            System.out.println("### " + message + " " + last);
                        }
                    });

                    ExecutorService executorService = Executors.newCachedThreadPool();

                    try {

                        // partial message start
                        session.getBasicRemote().sendText(SENT_MESSAGE, false);

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                // ISE after {@value ProtocolHandler#SEND_TIMEOUT}
                                try {
                                    session.getBasicRemote().sendText(SENT_MESSAGE);
                                } catch (IllegalStateException e) {
                                    latch.countDown();
                                }
                                return null;
                            }
                        });

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                // long wait, the whole message won't be unblocked in time.
                                Thread.sleep(5000);
                                session.getBasicRemote().sendText(SENT_MESSAGE, true);
                                return null;
                            }
                        });


                    } catch (IOException ignored) {
                    }
                }
            }, cec, getURI(MessageSequenceTestEndpoint.class));

            // must wait longer than {@value ProtocolHandler#SEND_TIMEOUT} + handshake
            assertTrue(latch.await(5, TimeUnit.SECONDS));

        } catch (DeploymentException e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testMessageSequenceTextPartialBinaryWholeWithLongWait() throws URISyntaxException, IOException,
            DeploymentException, InterruptedException {
        Server server = startServer(MessageSequenceTestEndpoint.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(1);

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
                        @Override
                        public void onMessage(String message, boolean last) {
                            System.out.println("### " + message + " " + last);
                        }
                    });

                    ExecutorService executorService = Executors.newCachedThreadPool();

                    try {

                        // partial message start
                        session.getBasicRemote().sendText(SENT_MESSAGE, false);

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                // ISE after {@value ProtocolHandler#SEND_TIMEOUT}
                                try {
                                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(SENT_MESSAGE.getBytes()));
                                } catch (IllegalStateException e) {
                                    latch.countDown();
                                }
                                return null;
                            }
                        });

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                // long wait, the whole message won't be unblocked in time.
                                Thread.sleep(5000);
                                session.getBasicRemote().sendText(SENT_MESSAGE, true);
                                return null;
                            }
                        });


                    } catch (IOException ignored) {
                    }
                }
            }, cec, getURI(MessageSequenceTestEndpoint.class));

            // must wait longer than {@value ProtocolHandler#SEND_TIMEOUT} + handshake
            assertTrue(latch.await(5, TimeUnit.SECONDS));

        } catch (DeploymentException e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testMessageSequenceTextPartialBinaryPartialWithShortWait() throws URISyntaxException, IOException,
            DeploymentException, InterruptedException {
        Server server = startServer(MessageSequenceTestEndpoint.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(1);

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
                        @Override
                        public void onMessage(String message, boolean last) {
                            System.out.println("### " + message + " " + last);
                        }
                    });

                    ExecutorService executorService = Executors.newCachedThreadPool();

                    try {

                        // partial message start
                        session.getBasicRemote().sendText(SENT_MESSAGE, false);

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                session.getBasicRemote().sendBinary(ByteBuffer.wrap(SENT_MESSAGE.getBytes()), false);
                                session.getBasicRemote().sendBinary(ByteBuffer.wrap(SENT_MESSAGE.getBytes()), true);
                                // to verify that ISE was not thrown
                                latch.countDown();
                                return null;
                            }
                        });

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                // long wait, the whole message won't be unblocked in time.
                                Thread.sleep(1000);
                                session.getBasicRemote().sendText(SENT_MESSAGE, true);
                                return null;
                            }
                        });


                    } catch (IOException ignored) {
                    }
                }
            }, cec, getURI(MessageSequenceTestEndpoint.class));

            // must wait longer than {@value ProtocolHandler#SEND_TIMEOUT} + handshake
            assertTrue(latch.await(5, TimeUnit.SECONDS));

        } catch (DeploymentException e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void testMessageSequenceTextPartialBinaryPartialWithLongWait() throws URISyntaxException, IOException,
            DeploymentException, InterruptedException {
        Server server = startServer(MessageSequenceTestEndpoint.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(1);

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
                        @Override
                        public void onMessage(String message, boolean last) {
                            System.out.println("### " + message + " " + last);
                        }
                    });

                    ExecutorService executorService = Executors.newCachedThreadPool();

                    try {

                        // partial message start
                        session.getBasicRemote().sendText(SENT_MESSAGE, false);

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                try {
                                    session.getBasicRemote()
                                           .sendBinary(ByteBuffer.wrap(SENT_MESSAGE.getBytes()), false);
                                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(SENT_MESSAGE.getBytes()), true);
                                } catch (IllegalStateException e) {
                                    latch.countDown();
                                }
                                return null;
                            }
                        });

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                // long wait, the whole message won't be unblocked in time.
                                Thread.sleep(5000);
                                session.getBasicRemote().sendText(SENT_MESSAGE, true);
                                return null;
                            }
                        });


                    } catch (IOException ignored) {
                    }
                }
            }, cec, getURI(MessageSequenceTestEndpoint.class));

            // must wait longer than {@value ProtocolHandler#SEND_TIMEOUT} + handshake
            assertTrue(latch.await(5, TimeUnit.SECONDS));

        } catch (DeploymentException e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    /**
     * <ol>
     * <li>partial text; final = false;</li>
     * <li>schedule following:</li>
     * <ul>
     * <li>- partial binary message pair (complete message)</li>
     * <li>- whole text message, which waits for first partial binary to be sent.</li>
     * </ul>
     * <li>send partial text; final = true</li>
     * </ol>
     * <p/>
     * The other side should receive:
     * <ul>
     * <li>partial text (two partial messages)</li>
     * <li>partial binary (two partial messages)</li>
     * <li>whole text (one message)</li>
     * </ul>
     */
    @Test
    public void testMessageSequenceComplex1() throws URISyntaxException, IOException, DeploymentException,
            InterruptedException {
        Server server = startServer(MessageSequenceTestEndpoint.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(2);

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
                        @Override
                        public void onMessage(String message, boolean last) {
                            System.out.println("### " + message + " " + last);
                        }
                    });

                    ExecutorService executorService = Executors.newCachedThreadPool();
                    final CountDownLatch tmpLatch = new CountDownLatch(1);

                    try {

                        // partial message start
                        session.getBasicRemote().sendText(SENT_MESSAGE, false);

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                session.getBasicRemote().sendBinary(ByteBuffer.wrap(SENT_MESSAGE.getBytes()), false);
                                tmpLatch.countDown();
                                session.getBasicRemote().sendBinary(ByteBuffer.wrap(SENT_MESSAGE.getBytes()), true);
                                latch.countDown();
                                return null;
                            }
                        });

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                session.getBasicRemote().sendText(SENT_MESSAGE, true);
                                return null;
                            }
                        });

                        executorService.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                tmpLatch.await(3, TimeUnit.SECONDS);
                                session.getBasicRemote().sendText(SENT_MESSAGE);
                                latch.countDown();
                                return null;
                            }
                        });


                    } catch (IOException ignored) {
                    }
                }
            }, cec, getURI(MessageSequenceTestEndpoint.class));

            // must wait longer than {@value ProtocolHandler#SEND_TIMEOUT} + handshake
            assertTrue(latch.await(5, TimeUnit.SECONDS));

        } catch (DeploymentException e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }
}

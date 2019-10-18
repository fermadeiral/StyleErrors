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

package org.glassfish.tyrus.test.standard_config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class BufferSizeTest extends TestContainer {

    private volatile CountDownLatch messageLatch;
    private volatile String receivedTextMessage;
    private volatile byte[] receivedBinaryMessage;

    @ServerEndpoint(value = "/endpointbuffersize")
    public static class StringEndpoint {

        public static volatile CloseReason closeReason = null;

        @OnOpen
        public void onOpen(Session session) {
            session.setMaxTextMessageBufferSize(5);
        }

        @OnMessage
        public String doThat(String message) {
            return message;
        }

        @OnClose
        public void onClose(CloseReason c) {
            closeReason = c;
        }
    }

    @Test
    public void testText() throws DeploymentException {
        Server server = startServer(StringEndpoint.class);

        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig clientConfiguration = ClientEndpointConfig.Builder.create().build();
            ClientManager client = createClient();

            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            @Override
                            public void onMessage(String message) {
                                receivedTextMessage = message;
                                messageLatch.countDown();
                            }
                        });

                        session.getBasicRemote().sendText("TES", false);
                        session.getBasicRemote().sendText("T1", true);
                    } catch (IOException e) {
                        // do nothing.
                    }
                }
            }, clientConfiguration, getURI(StringEndpoint.class));

            messageLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());
            assertEquals("TEST1", receivedTextMessage);

            messageLatch = new CountDownLatch(1);

            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    try {
                        session.getBasicRemote().sendText("LON", false);
                        session.getBasicRemote().sendText("G--", true);
                    } catch (IOException e) {
                        // do nothing.
                    }
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    if (closeReason.getCloseCode().equals(CloseReason.CloseCodes.TOO_BIG)) {
                        messageLatch.countDown();
                    } else {
                        System.err.println("Wrong close code: " + closeReason);
                    }
                }
            }, clientConfiguration, getURI(StringEndpoint.class));

            assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @ServerEndpoint(value = "/endpoint22")
    public static class ByteArrayEndpoint {

        public static volatile CloseReason closeReason = null;

        @OnOpen
        public void onOpen(Session session) {
            session.setMaxBinaryMessageBufferSize(5);
        }

        @OnMessage
        public byte[] doThat(byte[] message) {
            return message;
        }

        @OnClose
        public void onClose(CloseReason c) {
            closeReason = c;
        }
    }

    @Test
    public void testBinary() throws DeploymentException {
        Server server = startServer(ByteArrayEndpoint.class);

        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig clientConfiguration = ClientEndpointConfig.Builder.create().build();
            ClientManager client = createClient();

            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                            @Override
                            public void onMessage(byte[] message) {
                                receivedBinaryMessage = message;
                                messageLatch.countDown();
                            }
                        });

                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("TES".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("T1".getBytes()), true);
                    } catch (IOException e) {
                        // do nothing.
                    }
                }
            }, clientConfiguration, getURI(ByteArrayEndpoint.class));

            messageLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());
            assertArrayEquals("TEST1".getBytes(), receivedBinaryMessage);

            messageLatch = new CountDownLatch(1);

            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    try {
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("LON".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("G--".getBytes()), false);
                    } catch (IOException e) {
                        // do nothing.
                    }
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    if (closeReason.getCloseCode().equals(CloseReason.CloseCodes.TOO_BIG)) {
                        messageLatch.countDown();
                    }
                }
            }, clientConfiguration, getURI(ByteArrayEndpoint.class));

            messageLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @ServerEndpoint(value = "/endpoint3")
    public static class ReaderEndpoint {

        public static volatile CloseReason closeReason = null;
        private StringBuffer bufferedMessage;

        @OnOpen
        public void onOpen(Session session) {
            session.setMaxTextMessageBufferSize(5);
        }

        @OnMessage
        public String doThat(Reader message) throws IOException {
            bufferedMessage = new StringBuffer();
            int i;

            while ((i = message.read()) != -1) {
                bufferedMessage.append((char) i);
            }

            return bufferedMessage.toString();
        }

        @OnClose
        public void onClose(CloseReason c) {
            closeReason = c;
        }

    }

    @Test
    public void testReader() throws DeploymentException {
        Server server = startServer(ReaderEndpoint.class);

        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig clientConfiguration = ClientEndpointConfig.Builder.create().build();
            ClientManager client = createClient();

            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            @Override
                            public void onMessage(String message) {
                                receivedTextMessage = message;
                                messageLatch.countDown();
                            }
                        });

                        session.getBasicRemote().sendText("TES", false);
                        session.getBasicRemote().sendText("T1", true);
                    } catch (IOException e) {
                        // do nothing.
                    }
                }
            }, clientConfiguration, getURI(ReaderEndpoint.class));

            messageLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());
            assertEquals("TEST1", receivedTextMessage);

            messageLatch = new CountDownLatch(1);

            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    try {
                        session.getBasicRemote().sendText("LON", false);
                        session.getBasicRemote().sendText("G--", true);
                    } catch (IOException e) {
                        // do nothing.
                    }
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    if (closeReason.getCloseCode().equals(CloseReason.CloseCodes.TOO_BIG)) {
                        messageLatch.countDown();
                    }
                }
            }, clientConfiguration, getURI(ReaderEndpoint.class));

            messageLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }


    @ServerEndpoint(value = "/endpoint4")
    public static class InputStreamEndpoint {

        public static volatile CloseReason closeReason = null;
        private ByteArrayOutputStream byteArrayOutputStream;


        @OnOpen
        public void onOpen(Session session) {
            session.setMaxBinaryMessageBufferSize(5);
        }

        @OnMessage
        public byte[] doThat(InputStream is) throws IOException {
            byteArrayOutputStream = new ByteArrayOutputStream();
            int i;

            while ((i = is.read()) != -1) {
                byteArrayOutputStream.write(i);
            }

            return byteArrayOutputStream.toByteArray();
        }

        @OnClose
        public void onClose(CloseReason c) {
            closeReason = c;
        }
    }

    @Test
    public void testInputStream() throws DeploymentException {
        Server server = startServer(InputStreamEndpoint.class);

        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig clientConfiguration = ClientEndpointConfig.Builder.create().build();
            ClientManager client = createClient();

            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                            @Override
                            public void onMessage(byte[] message) {
                                receivedBinaryMessage = message;
                                messageLatch.countDown();
                            }
                        });

                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("TES".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("T1".getBytes()), true);
                    } catch (IOException e) {
                        // do nothing.
                    }
                }
            }, clientConfiguration, getURI(InputStreamEndpoint.class));

            messageLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());
            assertArrayEquals("TEST1".getBytes(), receivedBinaryMessage);

            messageLatch = new CountDownLatch(1);

            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    try {
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("LON".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("G--".getBytes()), false);
                    } catch (IOException e) {
                        // do nothing.
                    }
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    if (closeReason.getCloseCode().equals(CloseReason.CloseCodes.TOO_BIG)) {
                        messageLatch.countDown();
                    }
                }
            }, clientConfiguration, getURI(InputStreamEndpoint.class));

            messageLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }
}

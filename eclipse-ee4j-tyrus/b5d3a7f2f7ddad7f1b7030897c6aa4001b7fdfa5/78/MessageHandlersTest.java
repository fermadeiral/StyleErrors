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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.core.Utils;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class MessageHandlersTest extends TestContainer {

    @ServerEndpoint("/whole1")
    public static class WholeString {
        @OnMessage
        public String onMessage(String message) {
            return message;
        }
    }

    @ServerEndpoint("/partial1")
    public static class PartialString {

        private StringBuffer sb = new StringBuffer();

        @OnMessage
        public void onMessage(Session session, String message, boolean isLast) throws IOException {
            sb.append(message);

            if (isLast) {
                final String completeMessage = sb.toString();
                sb = new StringBuffer();
                session.getBasicRemote().sendText(completeMessage);
            }
        }
    }

    @Test
    public void clientWholeServerWhole() throws DeploymentException {
        Server server = startServer(WholeString.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            if (message.equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendText("In my experience, there's no such thing as luck.");
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(WholeString.class));

            messageLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientWholeServerPartial() throws DeploymentException {
        Server server = startServer(PartialString.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            if (message.equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendText("In my experience, there's no such thing as luck.");
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(PartialString.class));

            messageLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientPartialServerWhole() throws DeploymentException {
        Server server = startServer(WholeString.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            if (message.equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendText("In my experience", false);
                        session.getBasicRemote().sendText(", there's no such ", false);
                        session.getBasicRemote().sendText("thing as luck.", true);
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(WholeString.class));

            messageLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientPartialServerPartial() throws DeploymentException {
        Server server = startServer(PartialString.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            if (message.equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendText("In my experience", false);
                        session.getBasicRemote().sendText(", there's no such ", false);
                        session.getBasicRemote().sendText("thing as luck.", true);
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(PartialString.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @ServerEndpoint("/whole2")
    public static class WholeByteArray {
        @OnMessage
        public byte[] onMessage(byte[] message) {
            return message;
        }
    }

    @ServerEndpoint("/partial2")
    public static class PartialByteArray {

        private List<byte[]> buffer = new ArrayList<byte[]>();

        @OnMessage
        public void onMessage(Session session, byte[] message, boolean isLast) {
            buffer.add(message);
            if (isLast) {
                try {
                    ByteBuffer b = null;

                    for (byte[] bytes : buffer) {
                        if (b == null) {
                            b = ByteBuffer.wrap(bytes);
                        } else {
                            b = joinBuffers(b, ByteBuffer.wrap(bytes));
                        }
                    }

                    session.getBasicRemote().sendBinary(b);
                } catch (IOException e) {
                    //
                }
                buffer.clear();
            }
        }

        public static ByteBuffer joinBuffers(ByteBuffer bb1, ByteBuffer bb2) {

            final int remaining1 = bb1.remaining();
            final int remaining2 = bb2.remaining();
            byte[] array = new byte[remaining1 + remaining2];
            bb1.get(array, 0, remaining1);
            System.arraycopy(bb2.array(), 0, array, remaining1, remaining2);


            ByteBuffer buf = ByteBuffer.wrap(array);
            buf.limit(remaining1 + remaining2);

            return buf;
        }
    }

    @ServerEndpoint("/whole3")
    public static class WholeByteBuffer {
        @OnMessage
        public byte[] onMessage(byte[] message) {
            return message;
        }
    }

    @ServerEndpoint("/partial3")
    public static class PartialByteBuffer {

        private List<byte[]> buffer = new ArrayList<byte[]>();

        @OnMessage
        public void onMessage(Session session, ByteBuffer message, boolean isLast) {
            buffer.add(message.array());
            if (isLast) {
                try {
                    ByteBuffer b = null;

                    for (byte[] bytes : buffer) {
                        if (b == null) {
                            b = ByteBuffer.wrap(bytes);
                        } else {
                            b = PartialByteArray.joinBuffers(b, ByteBuffer.wrap(bytes));
                        }
                    }

                    session.getBasicRemote().sendBinary(b);
                } catch (IOException e) {
                    //
                }
                buffer.clear();
            }
        }
    }

    private CountDownLatch messageLatch;

    @Test
    public void clientWholeServerWholeByteArray() throws DeploymentException {
        Server server = startServer(WholeByteArray.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                        @Override
                        public void onMessage(byte[] message) {
                            if (new String(message).equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendBinary(
                                ByteBuffer.wrap("In my experience, there's no such thing as luck.".getBytes()));
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(WholeByteArray.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientWholeServerPartialByteArray() throws DeploymentException {
        Server server = startServer(PartialByteArray.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                        @Override
                        public void onMessage(byte[] message) {
                            if (new String(message).equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendBinary(
                                ByteBuffer.wrap("In my experience, there's no such thing as luck.".getBytes()));
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(PartialByteArray.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientPartialServerWholeByteArray() throws DeploymentException {
        Server server = startServer(WholeByteArray.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                        @Override
                        public void onMessage(byte[] message) {
                            if (new String(message).equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("In my experience".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(", there's no such ".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("thing as luck.".getBytes()), true);
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(WholeByteArray.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientPartialServerPartialByteArray() throws DeploymentException {
        Server server = startServer(PartialByteArray.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            final CountDownLatch latch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                        @Override
                        public void onMessage(byte[] message) {
                            if (new String(message).equals("In my experience, there's no such thing as luck.")) {
                                latch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("In my experience".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(", there's no such ".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("thing as luck.".getBytes()), true);
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(PartialByteArray.class));

            latch.await(1, TimeUnit.SECONDS);
            assertEquals(0, latch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientWholeServerWholeByteBuffer() throws DeploymentException {
        Server server = startServer(WholeByteBuffer.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
                        @Override
                        public void onMessage(ByteBuffer message) {
                            if (new String(message.array())
                                    .equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendBinary(
                                ByteBuffer.wrap("In my experience, there's no such thing as luck.".getBytes()));
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(WholeByteBuffer.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientWholeServerPartialByteBuffer() throws DeploymentException {
        Server server = startServer(PartialByteBuffer.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
                        @Override
                        public void onMessage(ByteBuffer message) {
                            if (new String(message.array())
                                    .equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendBinary(
                                ByteBuffer.wrap("In my experience, there's no such thing as luck.".getBytes()));
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(PartialByteBuffer.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientPartialServerWholeByteBuffer() throws DeploymentException {
        Server server = startServer(WholeByteBuffer.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
                        @Override
                        public void onMessage(ByteBuffer message) {
                            if (new String(message.array())
                                    .equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("In my experience".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(", there's no such ".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("thing as luck.".getBytes()), true);
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(WholeByteBuffer.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientPartialServerPartialByteBuffer() throws DeploymentException {
        Server server = startServer(PartialByteBuffer.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
                        @Override
                        public void onMessage(ByteBuffer message) {
                            if (new String(message.array())
                                    .equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("In my experience".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(", there's no such ".getBytes()), false);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("thing as luck.".getBytes()), true);
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(PartialByteBuffer.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @ServerEndpoint("/clientPartialText")
    public static class ClientPartialText {
        @OnMessage
        public void onMessage(Session session, String message) throws IOException {
            session.getBasicRemote().sendText("In my experience", false);
            session.getBasicRemote().sendText(", there's no such ", false);
            session.getBasicRemote().sendText("thing as luck.", true);
        }
    }

    @ServerEndpoint("/clientPartialBinary")
    public static class ClientPartialBinary {
        @OnMessage
        public void onMessage(Session session, String message) throws IOException {
            session.getBasicRemote().sendBinary(ByteBuffer.wrap("In my experience".getBytes()), false);
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(", there's no such ".getBytes()), false);
            session.getBasicRemote().sendBinary(ByteBuffer.wrap("thing as luck.".getBytes()), true);
        }
    }

    @Test
    public void clientReceivePartialTextAsWhole() throws DeploymentException {
        Server server = startServer(ClientPartialText.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            if (message.equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendText("In my experience, there's no such thing as luck.");
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(ClientPartialText.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientReceivePartialBinaryAsWhole() throws DeploymentException {
        Server server = startServer(ClientPartialBinary.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
                        @Override
                        public void onMessage(ByteBuffer message) {
                            if (message.equals(ByteBuffer.wrap("In my experience, there's no such thing as luck."
                                                                       .getBytes()))) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendText("In my experience, there's no such thing as luck.");
                    } catch (IOException e) {
                        // don't care
                    }
                }
            }, cec, getURI(ClientPartialBinary.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @ServerEndpoint("/reader")
    public static class WholeReader {

        public static CountDownLatch receivedMessageLatch = new CountDownLatch(1);


        @OnMessage
        public void onMessage(Session session, Reader reader) throws IOException {
            receivedMessageLatch.countDown();

            StringBuilder sb = new StringBuilder();
            int i;

            while ((i = reader.read()) != -1) {
                sb.append((char) i);
            }

            reader.close();
            session.getBasicRemote().sendText(sb.toString());
        }
    }

    @Test
    public void clientPartialServerWholeReader() throws DeploymentException {
        Server server = startServer(WholeReader.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(2);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {
                boolean first = true;

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            System.out.println("Client received message: " + message);

                            if (message.equals("In my experience, there's no such thing as luck.")) {
                                messageLatch.countDown();
                            }
                            if (first) {
                                try {
                                    WholeReader.receivedMessageLatch = new CountDownLatch(1);
                                    session.getBasicRemote().sendText("In my experience", false);
                                    WholeReader.receivedMessageLatch.await(1, TimeUnit.SECONDS);
                                    WholeReader.receivedMessageLatch = new CountDownLatch(1);
                                    session.getBasicRemote().sendText(", there's no such ", false);
                                    WholeReader.receivedMessageLatch.await(1, TimeUnit.SECONDS);
                                    WholeReader.receivedMessageLatch = new CountDownLatch(1);
                                    session.getBasicRemote().sendText("thing as luck.", true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                first = false;
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendText("In my experience", false);
                        WholeReader.receivedMessageLatch.await(1, TimeUnit.SECONDS);
                        WholeReader.receivedMessageLatch = new CountDownLatch(1);
                        session.getBasicRemote().sendText(", there's no such ", false);
                        WholeReader.receivedMessageLatch.await(1, TimeUnit.SECONDS);
                        WholeReader.receivedMessageLatch = new CountDownLatch(1);
                        session.getBasicRemote().sendText("thing as luck.", true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Session session, Throwable thr) {
                    thr.printStackTrace();
                }
            }, cec, getURI(WholeReader.class));

            messageLatch.await(3, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @ServerEndpoint("/inputstream")
    public static class WholeInputStream {

        public static CountDownLatch receivedMessageLatch = new CountDownLatch(1);

        @OnMessage
        public void onMessage(Session session, InputStream is) throws IOException {
            receivedMessageLatch.countDown();

            ArrayList<Byte> bytes = new ArrayList<Byte>();
            int i;

            while ((i = is.read()) != -1) {
                bytes.add((byte) i);
            }

            byte[] result = new byte[bytes.size()];
            for (int j = 0; j < bytes.size(); j++) {
                result[j] = bytes.get(j);
            }

            is.close();
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(result));
        }
    }

    @Test
    public void clientPartialServerWholeInputStream() throws DeploymentException {
        Server server = startServer(WholeInputStream.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(2);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {
                boolean first = true;
                byte[] buf1 = {1, 2, 3};
                byte[] buf2 = {4, 5, 6};
                byte[] buf3 = {7, 8, 9};
                byte[] result = {1, 2, 3, 4, 5, 6, 7, 8, 9};

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                        @Override
                        public void onMessage(byte[] message) {
                            System.out.println("Client received message: " + Utils.toString(message));
                            assertArrayEquals(result, message);
                            messageLatch.countDown();

                            if (first) {
                                try {
                                    WholeInputStream.receivedMessageLatch = new CountDownLatch(1);
                                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(buf1), false);
                                    WholeInputStream.receivedMessageLatch.await(1, TimeUnit.SECONDS);
                                    WholeInputStream.receivedMessageLatch = new CountDownLatch(1);
                                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(buf2), false);
                                    WholeInputStream.receivedMessageLatch.await(1, TimeUnit.SECONDS);
                                    WholeInputStream.receivedMessageLatch = new CountDownLatch(1);
                                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(buf3), true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                first = false;
                            }
                        }
                    });

                    try {
                        WholeInputStream.receivedMessageLatch = new CountDownLatch(1);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(buf1), false);
                        WholeInputStream.receivedMessageLatch.await(1, TimeUnit.SECONDS);
                        WholeInputStream.receivedMessageLatch = new CountDownLatch(1);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(buf2), false);
                        WholeInputStream.receivedMessageLatch.await(1, TimeUnit.SECONDS);
                        WholeInputStream.receivedMessageLatch = new CountDownLatch(1);
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(buf3), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Session session, Throwable thr) {
                    thr.printStackTrace();
                }
            }, cec, getURI(WholeInputStream.class));

            messageLatch.await(3, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void clientSendStreamServerWholeInputStream() throws DeploymentException {
        Server server = startServer(WholeInputStream.class);

        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            messageLatch = new CountDownLatch(1);
            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {
                byte[] buf1 = {1, 2, 3};
                byte[] buf2 = {4, 5, 6};
                byte[] buf3 = {7, 8, 9};
                byte[] result = {1, 2, 3, 4, 5, 6, 7, 8, 9};

                @Override
                public void onOpen(final Session session, EndpointConfig EndpointConfig) {

                    session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                        @Override
                        public void onMessage(byte[] message) {
                            for (int i = 0; i < result.length; i++) {
                                assertEquals(result[i], message[i]);
                            }

                            messageLatch.countDown();
                        }
                    });

                    try {
                        final OutputStream sendStream = session.getBasicRemote().getSendStream();

                        sendStream.write(buf1);
                        sendStream.write(buf2);
                        sendStream.write(buf3);

                        sendStream.flush();
                        sendStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Session session, Throwable thr) {
                    thr.printStackTrace();
                }
            }, cec, getURI(WholeInputStream.class));

            messageLatch.await(3, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }
}

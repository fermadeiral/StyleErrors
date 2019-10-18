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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.core.extension.ExtendedExtension;
import org.glassfish.tyrus.core.frame.Frame;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class ExtendedExtensionTest extends TestContainer {

    public ExtendedExtensionTest() {
        this.setContextPath("/e2e-test-appconfig");
    }

    public static class ExtendedExtensionApplicationConfig implements ServerApplicationConfig {

        @Override
        public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
            Set<ServerEndpointConfig> endpointConfigs = new HashSet<ServerEndpointConfig>();
            endpointConfigs.add(
                    ServerEndpointConfig.Builder
                            .create(ExtendedExtensionEndpoint.class, "/extendedExtensionEndpoint")
                            .extensions(Collections.<Extension>singletonList(new TestExtendedExtension(1)))
                            .build());
            endpointConfigs.add(
                    ServerEndpointConfig.Builder
                            .create(ExtendedExtensionOrderedEndpoint.class, "/extendedExtensionOrderedEndpoint")
                            .extensions(Arrays.<Extension>asList(
                                    new TestExtendedServerExtension(2, "ext1"),
                                    new TestExtendedServerExtension(3, "ext2")))
                            .build());
            return endpointConfigs;
        }

        @Override
        public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
            return Collections.<Class<?>>emptySet();
        }
    }

    /**
     * {@link org.glassfish.tyrus.test.e2e.appconfig.ExtendedExtensionTest.Constants#MESSAGE} cannot be directly in
     * {@link org.glassfish.tyrus.test.e2e.appconfig.ExtendedExtensionTest}, because {@link
     * org.glassfish.tyrus.test.tools.TestContainer} is not be available at runtime.
     */
    private static class Constants {
        static final byte[] MESSAGE = {'h', 'e', 'l', 'l', 'o'};
    }

    @Test
    public void extendedExtensionTest() throws DeploymentException {

        Server server = startServer(ExtendedExtensionApplicationConfig.class);
        final CountDownLatch messageLatch = new CountDownLatch(1);

        try {
            ArrayList<Extension> extensions = new ArrayList<Extension>();
            final TestExtendedExtension clientExtension = new TestExtendedExtension(0);
            extensions.add(clientExtension);

            final ClientEndpointConfig clientConfiguration =
                    ClientEndpointConfig.Builder.create().extensions(extensions)
                                                .configurator(new LoggingClientEndpointConfigurator()).build();

            ClientManager client = createClient();
            final Session session = client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                        @Override
                        public void onMessage(byte[] message) {
                            System.out.println("client onMessage.");
                            if (Arrays.equals(Constants.MESSAGE, message)) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendObject(Constants.MESSAGE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }, clientConfiguration, getURI("/extendedExtensionEndpoint"));

            assertEquals(1, session.getNegotiatedExtensions().size());
            final Extension extension = session.getNegotiatedExtensions().get(0);
            assertEquals(clientExtension, extension);

            assertTrue(messageLatch.await(3, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    public static class ExtendedExtensionEndpoint extends Endpoint {

        @Override
        public void onOpen(final Session session, EndpointConfig config) {
            print("onOpen " + session);
            session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                @Override
                public void onMessage(byte[] message) {
                    try {
                        print("server onMessage.");

                        if ((message[0] ^ TestExtendedExtension.MASK) == Constants.MESSAGE[0]
                                && (message[1] ^ TestExtendedExtension.MASK) == Constants.MESSAGE[1]
                                && Arrays.equals(Arrays.copyOfRange(message, 2, 4),
                                                 Arrays.copyOfRange(Constants.MESSAGE, 2, 4))) {
                            session.getBasicRemote().sendObject(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (EncodeException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            print("onClose " + session);
        }

        @Override
        public void onError(Session session, Throwable thr) {
            print("onError " + session);
            thr.printStackTrace();
        }

        private void print(String s) {
            System.out.println(this.getClass().getName() + " " + s);
        }
    }

    public static class TestExtendedExtension implements ExtendedExtension {

        public static final byte MASK = 0x55;
        public static final String NAME = "TestExtendedExtension";

        protected final int index;
        private final String name;


        public TestExtendedExtension(int index) {
            this.index = index;
            this.name = NAME;
        }

        public TestExtendedExtension(int index, String name) {
            this.index = index;
            this.name = name;
        }

        @Override
        public Frame processIncoming(ExtendedExtension.ExtensionContext context, Frame frame) {
            if (!frame.isControlFrame()) {
                final byte[] payloadData = frame.getPayloadData();
                payloadData[index] = (byte) (payloadData[index] ^ MASK);
                return Frame.builder(frame).payloadData(payloadData).build();
            } else {
                return frame;
            }
        }

        @Override
        public Frame processOutgoing(ExtendedExtension.ExtensionContext context, Frame frame) {
            if (!frame.isControlFrame()) {
                final byte[] payloadData = frame.getPayloadData();
                payloadData[index] = (byte) (payloadData[index] ^ MASK);
                return Frame.builder(frame).payloadData(payloadData).build();
            } else {
                return frame;
            }
        }

        @Override
        public List<Extension.Parameter> onExtensionNegotiation(ExtendedExtension.ExtensionContext context,
                                                                List<Extension.Parameter> requestedParameters) {
            print("onExtensionNegotiation :: " + context + " :: " + requestedParameters);
            return requestedParameters;
        }

        @Override
        public void onHandshakeResponse(ExtensionContext context, List<Parameter> responseParameters) {
            print("onHandshakeResponse :: " + context + " :: " + responseParameters);
        }

        @Override
        public String getName() {
            print("getName: " + name);
            return name;
        }

        @Override
        public List<Parameter> getParameters() {
            return null;
        }

        @Override
        public void destroy(ExtensionContext context) {
            print("destroy :: " + context);
        }

        private void print(String s) {
            System.out.println("##### " + NAME + " " + s);
        }
    }

    public static class LoggingClientEndpointConfigurator extends ClientEndpointConfig.Configurator {
        @Override
        public void beforeRequest(Map<String, List<String>> headers) {
            System.out.println("##### beforeRequest");
            System.out.println(headers);
            System.out.println();
        }

        @Override
        public void afterResponse(HandshakeResponse hr) {
            System.out.println("##### afterResponse");
            System.out.println(hr.getHeaders());
            System.out.println();
        }
    }

    @Test
    public void extendedExtensionOrderingTest() throws DeploymentException {

        Server server = startServer(ExtendedExtensionApplicationConfig.class);
        final CountDownLatch messageLatch = new CountDownLatch(1);

        try {
            ArrayList<Extension> extensions = new ArrayList<Extension>();
            final TestExtendedExtension clientExtension1 = new TestExtendedClientExtension(0, "ext1");
            final TestExtendedExtension clientExtension2 = new TestExtendedClientExtension(1, "ext2");
            extensions.add(clientExtension1);
            extensions.add(clientExtension2);


            final ClientEndpointConfig clientConfiguration =
                    ClientEndpointConfig.Builder
                            .create().extensions(extensions)
                            .configurator(new LoggingClientEndpointConfigurator()).build();

            ClientManager client = createClient();
            final Session session = client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                        @Override
                        public void onMessage(byte[] message) {
                            System.out.println("client onMessage.");

                            if (Arrays.equals(Constants.MESSAGE, message)) {
                                messageLatch.countDown();
                            }
                        }
                    });

                    try {
                        session.getBasicRemote().sendObject(Constants.MESSAGE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }, clientConfiguration, getURI("/extendedExtensionOrderedEndpoint"));

            assertEquals(2, session.getNegotiatedExtensions().size());
            assertEquals(clientExtension1, session.getNegotiatedExtensions().get(0));
            assertEquals(clientExtension2, session.getNegotiatedExtensions().get(1));

            assertTrue(messageLatch.await(3, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    public static class TestExtendedClientExtension extends TestExtendedExtension {
        public TestExtendedClientExtension(int index, String name) {
            super(index, name);
        }

        @Override
        public Frame processIncoming(ExtensionContext context, Frame frame) {
            if (frame.isControlFrame()) {
                return frame;
            }

            frame = super.processIncoming(context, frame);
            if (index == 0) {
                assertEquals(frame.getPayloadData()[1], (Constants.MESSAGE[1] ^ MASK));
            } else if (index == 1) {
                assertEquals(frame.getPayloadData()[0], Constants.MESSAGE[0]);
            } else {
                throw new IllegalArgumentException();
            }
            return frame;
        }

        @Override
        public Frame processOutgoing(ExtensionContext context, Frame frame) {
            if (frame.isControlFrame()) {
                return frame;
            }

            check(frame);
            return super.processOutgoing(context, frame);
        }

        private void check(Frame frame) {
            if (!Arrays.equals(Arrays.copyOfRange(frame.getPayloadData(), index, Constants.MESSAGE.length),
                               Arrays.copyOfRange(Constants.MESSAGE, index, Constants.MESSAGE.length))) {
                throw new IllegalArgumentException();
            } else {
                for (int i = 0; i < index; i++) {
                    if (frame.getPayloadData()[i] != (Constants.MESSAGE[i] ^ MASK)) {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
    }

    public static class TestExtendedServerExtension extends TestExtendedExtension {
        public TestExtendedServerExtension(int index, String name) {
            super(index, name);
        }

        @Override
        public Frame processIncoming(ExtensionContext context, Frame frame) {
            if (frame.isControlFrame()) {
                return frame;
            }

            check(frame);
            return super.processIncoming(context, frame);
        }

        @Override
        public Frame processOutgoing(ExtensionContext context, Frame frame) {
            if (frame.isControlFrame()) {
                return frame;
            }

            // no junit on server side.
            if (index == 2) {
                if (frame.getPayloadData()[3] != (Constants.MESSAGE[3] ^ MASK)) {
                    throw new IllegalArgumentException();
                }
            } else if (index == 3) {
                if (frame.getPayloadData()[2] != Constants.MESSAGE[2]) {
                    throw new IllegalArgumentException();
                }
            } else {
                throw new IllegalArgumentException();
            }

            frame = super.processOutgoing(context, frame);
            return frame;
        }

        private void check(Frame frame) {
            if (!Arrays.equals(Arrays.copyOfRange(frame.getPayloadData(), index, Constants.MESSAGE.length), Arrays
                    .copyOfRange(Constants.MESSAGE, index, Constants.MESSAGE.length))) {
                throw new IllegalArgumentException();
            } else {
                for (int i = 0; i < index; i++) {
                    if (frame.getPayloadData()[i] != (Constants.MESSAGE[i] ^ MASK)) {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
    }

    public static class ExtendedExtensionOrderedEndpoint extends Endpoint {

        @Override
        public void onOpen(final Session session, EndpointConfig config) {
            print("onOpen " + session);
            session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                @Override
                public void onMessage(byte[] message) {
                    try {
                        print("server onMessage.");

                        if ((message[0] ^ TestExtendedExtension.MASK) == Constants.MESSAGE[0]
                                && (message[1] ^ TestExtendedExtension.MASK) == Constants.MESSAGE[1]
                                && (message[2] ^ TestExtendedExtension.MASK) == Constants.MESSAGE[2]
                                && (message[3] ^ TestExtendedExtension.MASK) == Constants.MESSAGE[3]) {
                            session.getBasicRemote().sendObject(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (EncodeException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            print("onClose " + session);
        }

        @Override
        public void onError(Session session, Throwable thr) {
            print("onError " + session);
            thr.printStackTrace();
        }

        private void print(String s) {
            System.out.println(this.getClass().getName() + " " + s);
        }
    }
}

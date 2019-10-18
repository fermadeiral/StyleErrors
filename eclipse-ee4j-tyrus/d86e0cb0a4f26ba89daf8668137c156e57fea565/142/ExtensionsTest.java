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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.core.TyrusExtension;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@SuppressWarnings("serial")
public class ExtensionsTest extends TestContainer {

    private static final String SENT_MESSAGE = "Always pass on what you have learned.";

    private static final String MULTIPLE_REQUEST_EXTENSION_NAME = "testExtension";

    @ServerEndpoint(value = "/extensionsTest", configurator = MyServerConfigurator.class)
    public static class ExtensionsTestEndpoint {
        @OnOpen
        public void onOpen(Session s) {
            for (Extension extension : s.getNegotiatedExtensions()) {
                if (extension.getName().equals("ext1") || extension.getName().equals("ext2")) {
                    try {
                        s.getBasicRemote().sendText(SENT_MESSAGE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @OnMessage
        public String onMessage(String message) {
            return message;
        }
    }

    public static class MyServerConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested) {
            return requested;
        }
    }

    @Test
    public void testExtensions() throws DeploymentException {
        Server server = startServer(ExtensionsTestEndpoint.class);

        try {
            final List<Extension.Parameter> list1 = new ArrayList<Extension.Parameter>() {
                {
                    add(new TyrusExtension.TyrusParameter("prop1", "val1"));
                    add(new TyrusExtension.TyrusParameter("prop2", "val2"));
                    add(new TyrusExtension.TyrusParameter("prop3", "val3"));
                }
            };

            final List<Extension.Parameter> list2 = new ArrayList<Extension.Parameter>() {
                {
                    add(new TyrusExtension.TyrusParameter("prop1", "val1"));
                    add(new TyrusExtension.TyrusParameter("prop2", "val2"));
                    add(new TyrusExtension.TyrusParameter("prop3", "val3"));
                }
            };

            ArrayList<Extension> extensions = new ArrayList<Extension>();
            extensions.add(new TyrusExtension("ext1", list1));
            extensions.add(new TyrusExtension("ext2", list2));

            final ClientEndpointConfig clientConfiguration =
                    ClientEndpointConfig.Builder.create().extensions(extensions).build();

            final CountDownLatch messageLatch = new CountDownLatch(4);
            ClientManager client = createClient();
            ExtensionsClientEndpoint clientEndpoint = new ExtensionsClientEndpoint(messageLatch);
            client.connectToServer(clientEndpoint, clientConfiguration, getURI(ExtensionsTestEndpoint.class));

            messageLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, messageLatch.getCount());
            assertEquals(SENT_MESSAGE, clientEndpoint.getReceivedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    private static class ExtensionsClientEndpoint extends Endpoint {

        private final CountDownLatch messageLatch;
        private volatile String receivedMessage;

        private ExtensionsClientEndpoint(CountDownLatch messageLatch) {
            this.messageLatch = messageLatch;
        }

        @Override
        public void onOpen(final Session session, EndpointConfig EndpointConfig) {
            try {
                session.addMessageHandler(new MessageHandler.Whole<String>() {
                    @Override
                    public void onMessage(String message) {
                        receivedMessage = message;
                        for (Extension extension : session.getNegotiatedExtensions()) {
                            if (extension.getName().equals("ext1") || extension.getName().equals("ext2")) {
                                messageLatch.countDown();
                            }
                        }
                    }
                });

                session.getBasicRemote().sendText(SENT_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String getReceivedMessage() {
            return receivedMessage;
        }
    }

    @ServerEndpoint(value = "/multipleRequestExtensionsTest", configurator = MultipleRequestExtensionsConfigurator
            .class)
    public static class MultipleRequestExtensionsTestEndpoint {
        @OnOpen
        public void onOpen(Session s) {
            final List<Extension> negotiatedExtensions = s.getNegotiatedExtensions();
            if (negotiatedExtensions.size() == 1 && negotiatedExtensions.get(0).getName()
                                                                        .equals(MULTIPLE_REQUEST_EXTENSION_NAME)) {
                try {
                    s.getBasicRemote().sendText(SENT_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @OnMessage
        public String onMessage(String message) {
            return message;
        }
    }

    public static class MultipleRequestExtensionsConfigurator extends ServerEndpointConfig.Configurator {

        private static final List<Extension> installedExtensions = new ArrayList<Extension>() {
            {
                add(new TyrusExtension(MULTIPLE_REQUEST_EXTENSION_NAME));
            }
        };

        @Override
        public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested) {
            return super.getNegotiatedExtensions(installedExtensions, requested);
        }
    }

    @Test
    public void testMultipleRequestExtensions() throws DeploymentException {
        Server server = startServer(MultipleRequestExtensionsTestEndpoint.class);

        // parameter list is not relevant for this testcase
        final List<Extension.Parameter> parameterList = new ArrayList<Extension.Parameter>() {
            {
                add(new TyrusExtension.TyrusParameter("prop1", "val1"));
                add(new TyrusExtension.TyrusParameter("prop2", "val2"));
                add(new TyrusExtension.TyrusParameter("prop3", "val3"));
            }
        };

        try {
            ArrayList<Extension> extensions = new ArrayList<Extension>();
            extensions.add(new TyrusExtension(MULTIPLE_REQUEST_EXTENSION_NAME, null));
            extensions.add(new TyrusExtension(MULTIPLE_REQUEST_EXTENSION_NAME, parameterList));

            final ClientEndpointConfig clientConfiguration =
                    ClientEndpointConfig.Builder.create().extensions(extensions).build();

            ClientManager client = createClient();
            final CountDownLatch clientLatch = new CountDownLatch(2);
            MultipleRequestExtensionsClientEndpoint clientEndpoint =
                    new MultipleRequestExtensionsClientEndpoint(clientLatch);
            client.connectToServer(clientEndpoint, clientConfiguration, getURI(MultipleRequestExtensionsTestEndpoint
                                                                                       .class));

            clientLatch.await(1, TimeUnit.SECONDS);
            assertEquals(0, clientLatch.getCount());
            assertEquals(SENT_MESSAGE, clientEndpoint.getReceivedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    private static class MultipleRequestExtensionsClientEndpoint extends Endpoint {

        private final CountDownLatch messageLatch;
        private volatile String receivedMessage;

        private MultipleRequestExtensionsClientEndpoint(CountDownLatch messageLatch) {
            this.messageLatch = messageLatch;
        }

        @Override
        public void onOpen(final Session session, EndpointConfig EndpointConfig) {
            try {
                session.addMessageHandler(new MessageHandler.Whole<String>() {
                    @Override
                    public void onMessage(String message) {
                        receivedMessage = message;
                        final List<Extension> negotiatedExtensions = session.getNegotiatedExtensions();
                        if (negotiatedExtensions.size() == 1
                                && negotiatedExtensions.get(0).getName().equals(MULTIPLE_REQUEST_EXTENSION_NAME)) {
                            messageLatch.countDown();
                        }
                    }
                });

                session.getBasicRemote().sendText(SENT_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String getReceivedMessage() {
            return receivedMessage;
        }
    }
}

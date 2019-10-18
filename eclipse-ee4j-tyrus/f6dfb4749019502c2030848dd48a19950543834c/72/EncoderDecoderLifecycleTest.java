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

package org.glassfish.tyrus.test.e2e.appconfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.server.TyrusServerConfiguration;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.junit.Test;

/**
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class EncoderDecoderLifecycleTest extends TestContainer {

    public EncoderDecoderLifecycleTest() {
        this.setContextPath("/e2e-test-appconfig");
    }

    public static class ServerDeployApplicationConfig extends TyrusServerConfiguration {
        public ServerDeployApplicationConfig() {
            super(new HashSet<Class<?>>() {
                {
                    add(MyEndpointAnnotated.class);
                    add(ServiceEndpoint.class);
                }
            }, Collections.<ServerEndpointConfig>emptySet());
        }
    }

    @ServerEndpoint(value = "/servicecodertest")
    public static class ServiceEndpoint {

        public static volatile CountDownLatch closeLatch;

        @OnMessage
        public String onMessage(String message) throws InterruptedException {

            int initialized = 0;
            int destroyed = 0;
            int decCount = MyDecoder.instances.size();
            int encCount = MyEncoder.instances.size();
            int codersCount = decCount + encCount;

            for (MyEncoder enc : MyEncoder.instances) {
                if (enc.initialized.get()) {
                    initialized++;
                }

                if (enc.destroyed.get()) {
                    destroyed++;
                }
            }

            for (MyDecoder dec : MyDecoder.instances) {
                if (dec.initialized.get()) {
                    initialized++;
                }

                if (dec.destroyed.get()) {
                    destroyed++;
                }
            }

            if (message.equals("FirstClient")) {
                if (decCount == 1 && encCount == 1 && initialized == codersCount && destroyed == 0) {
                    return POSITIVE;
                }
            } else if (message.equals("FirstClientClosed")) {
                waitForCloseFrameArrival();
                if (decCount == 1 && encCount == 1 && initialized == codersCount && destroyed == codersCount) {
                    return POSITIVE;
                }
            } else if (message.equals("SecondClient")) {
                if (decCount == 2 && encCount == 2) {
                    return POSITIVE;
                }
            } else if (message.equals("SecondClientClosed")) {
                waitForCloseFrameArrival();
                if (decCount == 2 && encCount == 2 && initialized == codersCount && destroyed == codersCount) {
                    return POSITIVE;
                }
            } else if (message.equals("Cleanup")) {
                MyEncoder.instances.clear();
                MyDecoder.instances.clear();
                return POSITIVE;

            }

            return NEGATIVE;
        }

        private void waitForCloseFrameArrival() throws InterruptedException {
            /* There is a race, since the Session#close just sends a close frame asynchronously and does not wait for
               the connection to be really closed, so in some rare cases the call to the service endpoint can overtake
               the closing handshake completion. */
            closeLatch.await(1, TimeUnit.SECONDS);

            if (closeLatch.getCount() > 0) {
                System.out.println("!!! close frame still not received !!!");
            }
        }
    }

    public static class MyType {
        public final String s;

        public MyType(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("MyType");
            sb.append("{s='").append(s).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static class MyEncoder implements Encoder.Text<MyType> {
        public static final Set<MyEncoder> instances = new HashSet<MyEncoder>();

        public final AtomicBoolean initialized = new AtomicBoolean(false);
        public final AtomicBoolean destroyed = new AtomicBoolean(false);

        @Override
        public String encode(MyType object) throws EncodeException {
            instances.add(this);

            System.out.println("### MyEncoder encode(" + object + ")");
            return object.s;
        }

        @Override
        public void init(EndpointConfig config) {
            if (config != null) {
                initialized.set(true);
            }
        }

        @Override
        public void destroy() {
            destroyed.set(true);
        }
    }

    public static class MyDecoderNotToBeCalled implements Decoder.Text<MyType> {
        @Override
        public boolean willDecode(String s) {
            System.out.println("### MyDecoder111 willDecode(" + s + ")");
            return false;
        }

        @Override
        public MyType decode(String s) throws DecodeException {
            System.out.println("### MyDecoder111 decode(" + s + ")");
            return new MyType(s);
        }

        @Override
        public void init(EndpointConfig config) {
        }

        @Override
        public void destroy() {
        }
    }

    public static class MyDecoder implements Decoder.Text<MyType> {
        public static final Set<MyDecoder> instances = new HashSet<MyDecoder>();
        public static final AtomicInteger counter = new AtomicInteger(0);

        public final AtomicBoolean initialized = new AtomicBoolean(false);
        public final AtomicBoolean destroyed = new AtomicBoolean(false);

        @Override
        public boolean willDecode(String s) {
            counter.incrementAndGet();
            return true;
        }

        @Override
        public MyType decode(String s) throws DecodeException {
            instances.add(this);

            System.out.println("### MyDecoder decode(" + s + ")");
            return new MyType(s);
        }

        @Override
        public void init(EndpointConfig config) {
            if (config != null) {
                initialized.set(true);
            }
        }

        @Override
        public void destroy() {
            destroyed.set(true);
        }
    }

    @ServerEndpoint(value = "/myEndpointAnnotated",
            encoders = {EncoderDecoderLifecycleTest.MyEncoder.class},
            decoders = {MyDecoderNotToBeCalled.class, EncoderDecoderLifecycleTest.MyDecoder.class})
    public static class MyEndpointAnnotated {

        private int lastValue;

        @OnOpen
        public void onOpen() {
            lastValue = MyDecoder.counter.get();
            ServiceEndpoint.closeLatch = new CountDownLatch(1);
        }

        @OnMessage
        public MyType onMessage(MyType message) {
            final int i = MyDecoder.counter.get();

            // TYRUS-210
            if (lastValue != (i - 1)) {
                throw new RuntimeException();
            }
            lastValue = i;

            System.out.println("### MyEndpoint onMessage()");
            return message;
        }

        @OnError
        public void onError(Throwable t) {
            System.out.println("### MyEndpoint onError()");
            t.printStackTrace();
        }

        @OnClose
        public void onClose() {
            ServiceEndpoint.closeLatch.countDown();
        }
    }

    public static class ClientEndpoint extends Endpoint implements MessageHandler.Whole<String> {
        @Override
        public void onOpen(Session session, EndpointConfig config) {
            session.addMessageHandler(this);

            try {
                System.out.println("### ClientEndpoint onOpen()");
                session.getBasicRemote().sendText("test");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMessage(String message) {
            System.out.println("### ClientEndpoint onMessage()");
            messageLatch.countDown();
        }

        @Override
        public void onError(Session session, Throwable thr) {
            System.out.println("### ClientEndpoint onError()");
            thr.printStackTrace();
        }
    }

    static volatile CountDownLatch messageLatch;

    // encoders/decoders per session
    @Test
    public void testAnnotated() throws DeploymentException {
        final Server server = startServer(MyEndpointAnnotated.class, ServiceEndpoint.class);

        try {
            messageLatch = new CountDownLatch(1);

            ClientManager serviceClient = createClient();
            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "Cleanup");

            WebSocketContainer client = ContainerProvider.getWebSocketContainer();
            Session session = client.connectToServer(
                    ClientEndpoint.class, ClientEndpointConfig.Builder.create().build(),
                    getURI("/myEndpointAnnotated"));

            messageLatch.await(5, TimeUnit.SECONDS);

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "FirstClient");

            messageLatch = new CountDownLatch(1);
            session.getBasicRemote().sendText("test");
            messageLatch.await(5, TimeUnit.SECONDS);

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "FirstClient");

            session.close();

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "FirstClientClosed");

            messageLatch = new CountDownLatch(1);
            session = client.connectToServer(ClientEndpoint.class, ClientEndpointConfig.Builder.create().build(),
                                             getURI("/myEndpointAnnotated"));
            messageLatch.await(5, TimeUnit.SECONDS);

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "SecondClient");

            session.close();

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "SecondClientClosed");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            stopServer(server);
        }
    }

    public static class MyEndpointProgrammatic extends Endpoint implements MessageHandler.Whole<MyType> {

        private Session session;
        private int lastValue;

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            System.out.println("### MyEndpointProgrammatic onOpen()");

            this.session = session;
            session.addMessageHandler(this);

            lastValue = MyDecoder.counter.get();
            ServiceEndpoint.closeLatch = new CountDownLatch(1);
        }

        @Override
        public void onMessage(MyType message) {
            final int i = MyDecoder.counter.get();
            // TYRUS-210
            if (lastValue != (i - 1)) {
                throw new RuntimeException();
            }
            lastValue = i;

            System.out.println("### MyEndpointProgrammatic onMessage() " + session);

            try {
                session.getBasicRemote().sendObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Session session, Throwable thr) {
            System.out.println("### MyEndpointProgrammatic onError()");
            thr.printStackTrace();
        }

        @Override
        public void onClose(final Session session, final CloseReason closeReason) {
            ServiceEndpoint.closeLatch.countDown();
        }
    }

    public static class MyApplicationConfiguration implements ServerApplicationConfig {
        @Override
        public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> scanned) {
            return new HashSet<ServerEndpointConfig>() {
                {
                    //noinspection unchecked
                    add(ServerEndpointConfig.Builder
                                .create(MyEndpointProgrammatic.class, "/myEndpoint")
                                .decoders(Arrays.<Class<? extends Decoder>>asList(MyDecoderNotToBeCalled.class,
                                                                                  MyDecoder.class))
                                .encoders(Arrays.<Class<? extends Encoder>>asList(MyEncoder.class)).build());
                }
            };
        }

        @Override
        public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
            return Collections.emptySet();
        }
    }

    // encoders/decoders per session
    @Test
    public void testProgrammatic() throws DeploymentException {

        final Server server = startServer(MyApplicationConfiguration.class, ServerDeployApplicationConfig.class);

        try {
            messageLatch = new CountDownLatch(1);

            ClientManager serviceClient = createClient();

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "Cleanup");

            WebSocketContainer client = ContainerProvider.getWebSocketContainer();
            Session session = client.connectToServer(
                    ClientEndpoint.class, ClientEndpointConfig.Builder.create().build(), getURI("/myEndpoint"));

            messageLatch.await(5, TimeUnit.SECONDS);

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "FirstClient");

            messageLatch = new CountDownLatch(1);
            session.getBasicRemote().sendText("test");
            messageLatch.await(5, TimeUnit.SECONDS);

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "FirstClient");

            session.close();

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "FirstClientClosed");

            messageLatch = new CountDownLatch(1);
            session = client.connectToServer(ClientEndpoint.class, ClientEndpointConfig.Builder.create().build(),
                                             getURI("/myEndpoint"));
            messageLatch.await(5, TimeUnit.SECONDS);

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "SecondClient");

            session.close();

            testViaServiceEndpoint(serviceClient, ServiceEndpoint.class, POSITIVE, "SecondClientClosed");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            stopServer(server);
        }
    }
}

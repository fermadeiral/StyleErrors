/*
 * Copyright (c) 2012, 2017 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.tyrus.test.e2e.non_deployable;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests @PathParam annotation in @OnMessage method signature. Cannot be moved to standard tests due the expected
 * deployment exception.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 * @author Stepan Kopriva (stepan.kopriva at oracle.com)
 */
public class PathParamTest extends TestContainer {

    private CountDownLatch messageLatch;

    private String receivedMessage;

    private static final String SENT_MESSAGE = "Hello World";

    @ServerEndpoint(value = "/pathparam1/{first}/{second}/{third}")
    public static class PathParamTestEndpoint {

        @OnMessage
        public String doThat1(@PathParam("first") String first, @PathParam("second") String second, @PathParam
                ("third") String third, @PathParam("fourth") String fourth, String message, Session peer) {

            if (first != null && second != null && third != null && fourth == null && message != null && peer != null) {
                return message + first + second + third;
            } else {
                return "Error";
            }
        }
    }

    @Test
    public void testPathParam() throws DeploymentException {
        Server server = startServer(PathParamTestEndpoint.class);

        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            @Override
                            public void onMessage(String message) {
                                receivedMessage = message;
                                messageLatch.countDown();
                            }
                        });
                        session.getBasicRemote().sendText(SENT_MESSAGE);
                        System.out.println("Hello message sent.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, cec, getURI("/pathparam1/first/second/third"));
            messageLatch.await(5, TimeUnit.SECONDS);
            Assert.assertEquals(SENT_MESSAGE + "first" + "second" + "third", receivedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @ServerEndpoint(value = "/servicepathparam")
    public static class ServiceEndpoint {

        @OnMessage
        public String onMessage(String message) {
            if (message.equals("PathParamTestBeanError")) {
                if (PathParamTestBeanError.onErrorCalled.get() && PathParamTestBeanError.onErrorThrowable != null) {
                    return POSITIVE;
                }
            }

            return NEGATIVE;
        }
    }

    @ServerEndpoint(value = "/pathparam2/{one}/{two}/")
    public static class PathParamTestBeanError {

        public static AtomicBoolean onErrorCalled = new AtomicBoolean(false);
        public static volatile Throwable onErrorThrowable = null;

        @OnMessage
        public String doThat2(@PathParam("one") String one, @PathParam("two") Integer two, String message, Session
                peer) {

            assertNotNull(one);
            assertNotNull(two);
            assertNotNull(message);
            assertNotNull(peer);

            return message + one + two;
        }

        @OnError
        public void onError(Throwable t) {
            onErrorCalled.set(true);
            onErrorThrowable = t;
        }
    }

    @Test
    public void testPathParamError() throws DeploymentException {
        Server server = startServer(PathParamTestBeanError.class, ServiceEndpoint.class);

        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            @Override
                            public void onMessage(String message) {
                                receivedMessage = message;
                                messageLatch.countDown();
                            }
                        });
                        session.getBasicRemote().sendText(SENT_MESSAGE);
                        System.out.println("Hello message sent.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }, cec, getURI("/pathparam2/first/second/"));
            messageLatch.await(1, TimeUnit.SECONDS);
            testViaServiceEndpoint(client, ServiceEndpoint.class, POSITIVE, "PathParamTestBeanError");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @ServerEndpoint(value = "/pathparam3/{first}/{second}/")
    public static class PathParamTestBeanErrorNotPrimitive {

        @OnMessage
        public String doThat3(@PathParam("first") String first, @PathParam("second") PathParamTest second, String
                message, Session peer) {

            return message + first + second;
        }
    }

    @Test
    public void testPathParamErrorNotPrimitive() throws DeploymentException {
        boolean exceptionThrown = false;
        Server server = null;

        try {
            server = startServer(PathParamTestBeanErrorNotPrimitive.class);
        } catch (Exception e) {
            exceptionThrown = true;
        } finally {
            stopServer(server);
            assertEquals(true, exceptionThrown);
        }
    }


    @ServerEndpoint(value = "/pathparam4/{one}/{second}/{third}/{fourth}/{fifth}/{sixth}/{seventh}/{eighth}")
    public static class PathParamTestEndpointPrimitiveBoxing {

        @OnMessage
        public String doThat4(@PathParam("one") String one, @PathParam("second") Integer second,
                              @PathParam("third") Boolean third, @PathParam("fourth") Long fourth,
                              @PathParam("fifth") Float fifth, @PathParam("sixth") Double sixth,
                              @PathParam("seventh") Character seventh, @PathParam("eighth") Byte eighth, String message,
                              Session peer) {

            if (one != null && second != null && third != null && fourth != null && fifth != null && sixth != null
                    && seventh != null && eighth != null && message != null && peer != null) {
                return message + one + second + third + fourth + fifth + sixth + seventh + eighth;
            } else {
                return "Error";
            }
        }
    }

    @ServerEndpoint(value = "/pathparam5/{first}/{second}/{third}/{fourth}/{fifth}/{sixth}/{seventh}/{eighth}")
    public static class PathParamTestEndpointPrimitives {

        @OnMessage
        public String doThat5(
                @PathParam("first") String first, @PathParam("second") int second, @PathParam("third") boolean third,
                @PathParam("fourth") long fourth, @PathParam("fifth") float fifth,
                @PathParam("sixth") double sixth, @PathParam("seventh") char seventh, @PathParam("eighth") byte eighth,
                String message, Session peer) {
            if (message != null && peer != null) {
                return message + first + second + third + fourth + fifth + sixth + seventh + eighth;
            } else {
                return "Error";
            }
        }

        @OnError
        public void onError(Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void testPathParamPrimitives() throws DeploymentException {
        testPathParamPrimitive(PathParamTestEndpointPrimitives.class, getURI("/pathparam5/first/2/true/4/5/6/c/0"));
    }

    @Test
    public void testPathParamPrimitivesBoxing() throws DeploymentException {
        testPathParamPrimitive(PathParamTestEndpointPrimitiveBoxing.class,
                               getURI("/pathparam4/first/2/true/4/5/6/c/0"));
    }

    public void testPathParamPrimitive(Class<?> testedClass, URI uri) throws DeploymentException {
        Server server = startServer(testedClass);

        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = createClient();
            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            @Override
                            public void onMessage(String message) {
                                receivedMessage = message;
                                messageLatch.countDown();

                            }
                        });
                        session.getBasicRemote().sendText(SENT_MESSAGE);
                        System.out.println("Hello message sent.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, cec, uri);
            messageLatch.await(5, TimeUnit.SECONDS);
            Assert.assertEquals(SENT_MESSAGE + "first" + "2" + "true" + "4" + "5.0" + "6.0" + "c" + "0",
                                receivedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stopServer(server);
        }
    }

    @ServerEndpoint("/pathparam6/{param}")
    public static class PathParamParalelTestEndpoint {

        private String param;

        @OnOpen
        public void onOpen(Session session, @PathParam("param") String param) throws IOException {
            this.param = param;

            session.getBasicRemote().sendText(param);
        }

        @OnError
        public void onError(Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void testPathParamParalel() throws DeploymentException {
        final int CLIENTS = 10;

        Server server = startServer(PathParamParalelTestEndpoint.class);
        List<Callable<Boolean>> clients = new ArrayList<Callable<Boolean>>();

        for (int i = 0; i < CLIENTS; i++) {
            final String param = Integer.toString(i);
            clients.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    final ClientManager client = createClient();
                    final CountDownLatch messageLatch = new CountDownLatch(1);
                    client.connectToServer(new Endpoint() {
                        @Override
                        public void onOpen(Session session, EndpointConfig config) {
                            session.addMessageHandler(new MessageHandler.Whole<String>() {
                                @Override
                                public void onMessage(String message) {
                                    if (message.equals(param)) {
                                        messageLatch.countDown();
                                    }
                                }
                            });
                        }
                    }, getURI("/pathparam6/" + param));

                    assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
                    return true;
                }
            });
        }

        try {
            ExecutorService pool = Executors.newFixedThreadPool(CLIENTS);
            List<Future<Boolean>> r = pool.invokeAll(clients);
            for (Future<Boolean> future : r) {
                future.get(5, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }
}

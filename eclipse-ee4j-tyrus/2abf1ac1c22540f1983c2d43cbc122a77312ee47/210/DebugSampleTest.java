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

package org.glassfish.tyrus.tests.servlet.debug;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.websocket.MessageHandler;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.auth.Credentials;
import org.glassfish.tyrus.core.TyrusWebSocketEngine;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.spi.UpgradeRequest;
import org.glassfish.tyrus.spi.UpgradeResponse;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Does not automatically test anything, it only servers as a sample use of the debug mode or for manual testing of the
 * debug mode.
 *
 * @author Petr Janouch
 */
public class DebugSampleTest extends TestContainer {

    String loggingConfigPath =
            new File(DebugSampleTest.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getPath();

    public DebugSampleTest() {
        setContextPath("/samples-debug");

        System.setProperty("java.util.logging.config.file", loggingConfigPath + "/logging.properties");
    }

    @Test
    public void testMatch() throws DeploymentException, InterruptedException, IOException {

        final Server server =
                startServer(Endpoint1.class, Endpoint2.class, Endpoint3.class, Endpoint4.class, Endpoint5.class,
                            Endpoint6.class);
        final CountDownLatch onOpenLatch = new CountDownLatch(1);

        try {
            final ClientManager client = createClient();

            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    onOpenLatch.countDown();
                }

            }, ClientEndpointConfig.Builder.create().build(), getURI("/endpoint/a/b"));

            assertTrue(onOpenLatch.await(5, TimeUnit.SECONDS));
            // wait for all messages to be logged
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

    @Test
    public void test404() throws DeploymentException, InterruptedException, IOException {

        final Server server =
                startServer(Endpoint1.class, Endpoint2.class, Endpoint3.class, Endpoint4.class, Endpoint5.class,
                            Endpoint6.class);
        final CountDownLatch onOpenLatch = new CountDownLatch(1);

        try {
            final ClientManager client = createClient();

            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    onOpenLatch.countDown();
                }

            }, ClientEndpointConfig.Builder.create().build(), getURI("/endpoint/b"));

            fail();

        } catch (Exception e) {
            // do nothing
        } finally {
            stopServer(server);
            // wait for all messages to be logged
            Thread.sleep(1000);
        }
    }

    @Test
    public void testDeploy() throws DeploymentException, InterruptedException, IOException {

        final Server server =
                startServer(Endpoint1.class, Endpoint2.class, Endpoint3.class, Endpoint4.class, Endpoint5.class,
                            Endpoint6.class);
        final CountDownLatch onOpenLatch = new CountDownLatch(1);

        try {
            final ClientManager client = createClient();

            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    onOpenLatch.countDown();
                }

            }, ClientEndpointConfig.Builder.create().build(), getURI("/endpoint/a/b"));

            assertTrue(onOpenLatch.await(3, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
            // wait for all messages to be logged
            Thread.sleep(1000);
        }
    }

    @Test
    public void testHttpLoggingConfiguration() throws DeploymentException, InterruptedException, IOException {
        System.setProperty("java.util.logging.config.file", loggingConfigPath + "/emptyLogging.properties");
        LogManager.getLogManager().readConfiguration();


        final Server server =
                startServer(Endpoint1.class, Endpoint2.class, Endpoint3.class, Endpoint4.class, Endpoint5.class,
                            Endpoint6.class);
        final CountDownLatch onOpenLatch = new CountDownLatch(1);

        try {
            final ClientManager client = createClient();
            client.getProperties().put(ClientProperties.LOG_HTTP_UPGRADE, true);

            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    onOpenLatch.countDown();
                }

            }, ClientEndpointConfig.Builder.create().build(), getURI("/endpoint/a/b"));

            assertTrue(onOpenLatch.await(3, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
            // wait for all messages to be logged
            Thread.sleep(1000);
            System.setProperty("java.util.logging.config.file", loggingConfigPath + "/logging.properties");
            LogManager.getLogManager().readConfiguration();
        }
    }

    @Test
    public void testClientAuthenticationLogging() throws DeploymentException, InterruptedException, IOException {
        final CountDownLatch onOpenLatch = new CountDownLatch(1);
        HttpServer server = getAuthenticationServer();
        server.start();
        try {
            final ClientManager client = createClient();
            client.getProperties().put(ClientProperties.CREDENTIALS, new Credentials("Petr", "My secret password"));

            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    onOpenLatch.countDown();
                }

            }, ClientEndpointConfig.Builder.create().build(), URI.create("ws://localhost:8025/testAuthentication"));

            assertTrue(onOpenLatch.await(3, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            server.shutdown();
            // wait for all messages to be logged
            Thread.sleep(1000);
        }
    }

    @Test
    public void testMessages() throws DeploymentException, InterruptedException, IOException {
        final Server server = startServer(EchoEndpoint.class);
        final CountDownLatch stringMessageLatch = new CountDownLatch(1);
        final CountDownLatch binaryMessageLatch = new CountDownLatch(1);

        try {
            final ClientManager client = createClient();

            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String m) {
                            stringMessageLatch.countDown();
                        }
                    });

                    session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {

                        @Override
                        public void onMessage(ByteBuffer m) {
                            binaryMessageLatch.countDown();
                        }
                    });

                    try {
                        session.getBasicRemote().sendText("Hello");
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap("Hello".getBytes()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }, ClientEndpointConfig.Builder.create().build(), getURI(EchoEndpoint.class));

            assertTrue(stringMessageLatch.await(3, TimeUnit.SECONDS));
            assertTrue(binaryMessageLatch.await(3, TimeUnit.SECONDS));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
            // wait for all messages to be logged
            Thread.sleep(1000);
        }
    }

    @Test
    public void test404Trace() throws DeploymentException, InterruptedException, IOException {

        getServerProperties().put(TyrusWebSocketEngine.TRACING_TYPE, "ON_DEMAND");
        final Server server =
                startServer(Endpoint1.class, Endpoint2.class, Endpoint3.class, Endpoint4.class, Endpoint5.class,
                            Endpoint6.class);

        try {
            final ClientManager client = createClient();

            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                }
            }, getTraceConfigurator(new CountDownLatch(1)), getURI("/endpoint/b"));

            fail();
        } catch (Exception e) {
            //  do nothing - the exception is expected
        } finally {
            stopServer(server);
            // wait for all messages to be logged
            Thread.sleep(1000);
        }
    }

    @Test
    public void testMatchTrace() throws DeploymentException, InterruptedException, IOException {

        getServerProperties().put(TyrusWebSocketEngine.TRACING_TYPE, "on_demand");
        final Server server =
                startServer(Endpoint1.class, Endpoint2.class, Endpoint3.class, Endpoint4.class, Endpoint5.class,
                            Endpoint6.class);

        CountDownLatch traceHeaderLatch = new CountDownLatch(1);
        try {
            final ClientManager client = createClient();
            client.getProperties().put(ClientProperties.LOG_HTTP_UPGRADE, true);
            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {

                }
            }, getTraceConfigurator(traceHeaderLatch), getURI(Endpoint4.class));

            assertTrue(traceHeaderLatch.await(3, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
            // wait for all messages to be logged
            Thread.sleep(1000);
        }
    }

    @Test
    public void testMatchTraceAll() throws DeploymentException, InterruptedException, IOException {

        getServerProperties().put(TyrusWebSocketEngine.TRACING_TYPE, "ALL");
        final Server server = startServer(
                Endpoint1.class, Endpoint2.class, Endpoint3.class, Endpoint4.class, Endpoint5.class, Endpoint6.class);

        try {
            final ClientManager client = createClient();
            CountDownLatch traceHeaderLatch = new CountDownLatch(1);

            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {

                }
            }, getTraceConfigurator(traceHeaderLatch), getURI(Endpoint4.class));

            assertTrue(traceHeaderLatch.await(5, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
            // wait for all messages to be logged
            Thread.sleep(1000);
        }
    }

    @Test
    public void testErrorTrace() throws DeploymentException, InterruptedException, IOException {

        getServerProperties().put(TyrusWebSocketEngine.TRACING_TYPE, "all");
        final Server server = startServer(
                Endpoint1.class, Endpoint2.class, Endpoint3.class, Endpoint4.class, Endpoint5.class, Endpoint6.class);

        try {
            final ClientManager client = createClient();
            client.getProperties().put(ClientProperties.LOG_HTTP_UPGRADE, true);
            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {

                }
            }, getWebsocketVersion12Configurator(), getURI(Endpoint4.class));

            fail();
        } catch (Exception e) {
            //  do nothing - the exception is expected
        } finally {
            stopServer(server);
            // wait for all messages to be logged
            Thread.sleep(1000);
        }
    }

    private HttpServer getAuthenticationServer() throws IOException {
        HttpServer server = HttpServer.createSimpleServer("/testAuthentication", getHost(), getPort());
        server.getServerConfiguration().addHttpHandler(
                new HttpHandler() {

                    boolean authenticated = false;

                    @Override
                    public void service(Request request, Response response) throws Exception {
                        if (authenticated) {
                            response.setStatus(101);

                            response.addHeader(UpgradeRequest.CONNECTION, UpgradeRequest.UPGRADE);
                            response.addHeader(UpgradeRequest.UPGRADE, UpgradeRequest.WEBSOCKET);

                            String secKey = request.getHeader(HandshakeRequest.SEC_WEBSOCKET_KEY);
                            String key = secKey + UpgradeRequest.SERVER_KEY_HASH;

                            MessageDigest instance;
                            try {
                                instance = MessageDigest.getInstance("SHA-1");
                                instance.update(key.getBytes("UTF-8"));
                                final byte[] digest = instance.digest();
                                String responseKey = Base64.getEncoder().encodeToString(digest);

                                response.addHeader(HandshakeResponse.SEC_WEBSOCKET_ACCEPT, responseKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            authenticated = true;
                            response.setStatus(401);
                            response.addHeader(UpgradeResponse.WWW_AUTHENTICATE, "Basic realm=\"my realm\"");
                            response.addHeader(UpgradeRequest.UPGRADE, UpgradeRequest.WEBSOCKET);
                        }
                    }
                }
        );
        return server;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class DebugTestFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            return record.getMessage() + "\n";
        }
    }

    @ServerEndpoint("/endpoint/{a}/b")
    public static class Endpoint1 {
    }

    @ServerEndpoint("/endpoint/{a}/{b}")
    public static class Endpoint2 {
    }

    @ServerEndpoint("/endpoint/a/{b}")
    public static class Endpoint3 {
    }

    @ServerEndpoint("/endpoint/a/b")
    public static class Endpoint4 {
    }

    @ServerEndpoint("/endpoint/a")
    public static class Endpoint5 {
    }

    @ServerEndpoint("/endpoint/a/a")
    public static class Endpoint6 {
    }

    @ServerEndpoint("/endpoint/echo")
    public static class EchoEndpoint {

        @OnMessage
        public void onMessage(String message, Session session) throws IOException {
            session.getBasicRemote().sendText(message + " (from the server)");
        }

        @OnMessage
        public void onMessage(ByteBuffer message, Session session) {
            session.getAsyncRemote().sendBinary(message);
        }
    }

    private ClientEndpointConfig getTraceConfigurator(final CountDownLatch traceHeaderLatch) {
        return ClientEndpointConfig.Builder.create().configurator(new ClientEndpointConfig.Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                headers.put(UpgradeRequest.ENABLE_TRACING_HEADER, Arrays.asList("Whatever"));
                headers.put(UpgradeRequest.TRACING_THRESHOLD, Arrays.asList("SUMMARY"));
            }

            @Override
            public void afterResponse(HandshakeResponse hr) {
                for (Map.Entry<String, List<String>> header : hr.getHeaders().entrySet()) {
                    if (header.getKey().toLowerCase(Locale.US)
                              .contains(UpgradeResponse.TRACING_HEADER_PREFIX.toLowerCase(Locale.US))) {
                        traceHeaderLatch.countDown();
                    }
                }
            }
        }).build();
    }

    private ClientEndpointConfig getWebsocketVersion12Configurator() {
        return ClientEndpointConfig.Builder.create().configurator(new ClientEndpointConfig.Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                headers.put(HandshakeRequest.SEC_WEBSOCKET_VERSION, Arrays.asList("12"));
            }
        }).build();
    }
}

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

package org.glassfish.tyrus.test.tools;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.server.Server;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Utilities for creating automated tests easily.
 *
 * @author Stepan Kopriva (stepan.kopriva at oracle.com)
 */
public class TestContainer {

    protected static final String POSITIVE = "POSITIVE";
    protected static final String NEGATIVE = "NEGATIVE";
    private String contextPath = "/e2e-test";
    private String defaultHost = "localhost";
    private int defaultPort = 8025;
    private Map<String, Object> serverProperties = new HashMap<String, Object>();

    /**
     * Start embedded server unless "tyrus.test.host" system property is specified.
     *
     * @param endpointClasses websocket endpoints and configs to be deployed.
     * @return new {@link Server} instance or {@code null} if "tyrus.test.host" system property is set.
     * @throws DeploymentException when the server cannot be started.
     */
    protected Server startServer(Class<?>... endpointClasses) throws DeploymentException {
        final String host = System.getProperty("tyrus.test.host");
        if (host == null) {
            final Server server = new Server(defaultHost, getPort(), contextPath, serverProperties, endpointClasses);
            server.start();
            return server;
        } else {
            return null;
        }
    }

    /**
     * Stop the server.
     *
     * @param server to be stopped.
     */
    protected void stopServer(Server server) {
        if (server != null) {
            server.stop();
        }
    }

    protected String getHost() {
        final String host = System.getProperty("tyrus.test.host");
        if (host != null) {
            return host;
        }
        return defaultHost;
    }

    /**
     * Get port used for creating remote endpoint {@link URI}.
     * <p>
     * Can be overridden by {@link TestContainer} descendants.
     *
     * @return port used for creating remote endpoint {@link URI}.
     */
    protected int getPort() {
        final String port = System.getProperty("tyrus.test.port");
        if (port != null) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException nfe) {
                // do nothing
            }
        }
        return defaultPort;
    }

    /**
     * Get the {@link URI} for the {@link ServerEndpoint} annotated class.
     *
     * @param serverClass the annotated class the {@link URI} is computed for.
     * @return {@link URI} which is used to connect to the given endpoint.
     */
    protected URI getURI(Class<?> serverClass) {
        return getURI(serverClass, null);
    }

    /**
     * Get the {@link URI} for the {@link ServerEndpoint} annotated class.
     *
     * @param serverClass the annotated class the {@link URI} is computed for.
     * @param scheme      scheme of newly created {@link URI}. If {@code null}, "ws" will be used.
     * @return {@link URI} which is used to connect to the given endpoint.
     */
    protected URI getURI(Class<?> serverClass, String scheme) {
        String endpointPath = serverClass.getAnnotation(ServerEndpoint.class).value();
        return getURI(endpointPath, scheme);
    }

    /**
     * Get the {@link URI} for the given {@link String} path.
     *
     * @param endpointPath the path the {@link URI} is computed for.
     * @return {@link URI} which is used to connect to the given path.
     */
    protected URI getURI(String endpointPath) {
        return getURI(endpointPath, null);
    }

    /**
     * Get the {@link URI} for the given {@link String} path.
     *
     * @param endpointPath the path the {@link URI} is computed for.
     * @param scheme       scheme of newly created {@link URI}. If {@code null}, "ws" will be used.
     * @return {@link URI} which is used to connect to the given path.
     */
    protected URI getURI(String endpointPath, String scheme) {
        try {
            String currentScheme = scheme == null ? "ws" : scheme;
            int port = getPort();

            if ((port == 80 && "ws".equalsIgnoreCase(currentScheme))
                    || (port == 443 && "wss".equalsIgnoreCase(currentScheme))) {
                port = -1;
            }

            return new URI(currentScheme, null, getHost(), port, contextPath + endpointPath, null, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the {@link ClientManager} instance.
     *
     * @return {@link ClientManager} which can be used to connect to a server
     */
    protected ClientManager createClient() {
        final String clientContainerClassName = System.getProperty("tyrus.test.container.client");
        if (clientContainerClassName != null) {
            return ClientManager.createClient(clientContainerClassName);
        } else {
            return ClientManager.createClient();
        }
    }

    /**
     * Get server properties.
     *
     * @return server properties.
     */
    public Map<String, Object> getServerProperties() {
        return serverProperties;
    }

    /**
     * Set properties.
     *
     * @param properties server properties.
     */
    public void setServerProperties(Map<String, Object> properties) {
        this.serverProperties = properties;
    }

    /**
     * Send message to the service endpoint and compare the received result with the specified one.
     *
     * @param client          client used to send the message.
     * @param serviceEndpoint endpoint to which the message will be sent.
     * @param expectedResult  expected reply.
     * @param message         message to be sent.
     * @throws DeploymentException when the client cannot connect to service endpoint.
     * @throws IOException         if there was a network or protocol problem that
     *                             prevented the client endpoint being connected to its server.
     */
    protected void testViaServiceEndpoint(ClientManager client, Class<?> serviceEndpoint, final String expectedResult,
                                          String message) throws DeploymentException, IOException,
            InterruptedException {
        final MyServiceClientEndpoint myServiceClientEndpoint = new MyServiceClientEndpoint();
        final Session serviceSession = client.connectToServer(myServiceClientEndpoint, getURI(serviceEndpoint));
        serviceSession.getBasicRemote().sendText(message);
        assertTrue(myServiceClientEndpoint.latch.await(2, TimeUnit.SECONDS));
        assertEquals(expectedResult, myServiceClientEndpoint.receivedMessage);
        serviceSession.close();
    }

    /**
     * Sets the context path.
     *
     * @param contextPath the path to be set.
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * Sets the default host.
     *
     * @param defaultHost the host to be set.
     */
    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }

    /**
     * Sets the default port.
     *
     * @param defaultPort default port number.
     */
    public void setDefaultPort(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    @ClientEndpoint
    public static class MyServiceClientEndpoint {

        public final CountDownLatch latch = new CountDownLatch(1);
        public volatile String receivedMessage = null;

        @OnMessage
        public void onMessage(String message) {
            receivedMessage = message;
            latch.countDown();
        }
    }
}

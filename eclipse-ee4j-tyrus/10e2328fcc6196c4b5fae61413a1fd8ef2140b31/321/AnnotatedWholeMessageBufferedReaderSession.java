/*
 * Copyright (c) 2011, 2017 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.tyrus.tests.qa.lifecycle.handlers.text;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.tests.qa.lifecycle.AnnotatedEndpoint;
import org.glassfish.tyrus.tests.qa.lifecycle.LifeCycleDeployment;
import org.glassfish.tyrus.tests.qa.lifecycle.handlers.BufferedReaderSessionImpl;
import org.glassfish.tyrus.tests.qa.tools.SessionController;

/**
 * @author Michal Čonos (michal.conos at oracle.com)
 */
public class AnnotatedWholeMessageBufferedReaderSession {

    @ServerEndpoint(value = LifeCycleDeployment.LIFECYCLE_ENDPOINT_PATH)
    static public class Server extends AnnotatedEndpoint {

        @Override
        public void createLifeCycle() {
            lifeCycle = new BufferedReaderSessionImpl(1024);
        }

        @OnOpen
        @Override
        public void onOpen(Session session, EndpointConfig ec) {
            super.onOpen(session, ec);
            lifeCycle.onServerOpen(session, ec);
            logger.log(Level.INFO, "lifeCycle={0}", lifeCycle.toString());
        }

        @OnMessage
        public void onMessage(Reader message, Session session) throws IOException {
            lifeCycle.onServerMessage(message, session);
        }

        @OnClose
        public void onClose(Session s, CloseReason reason) {
            lifeCycle.onServerClose(s, reason);
        }

        @OnError
        public void onError(Session s, Throwable thr) {
            lifeCycle.onServerError(s, thr);
        }
    }

    @ClientEndpoint
    static public class Client extends AnnotatedEndpoint {

        @Override
        public void createLifeCycle() {
            lifeCycle = new BufferedReaderSessionImpl(1024);
        }

        @OnOpen
        public void onOpen(Session session, EndpointConfig ec) {
            if (this.session == null) {
                this.session = session;
            }
            logger.log(Level.INFO, "ProgrammaticEndpoint: onOpen");
            this.sc = new SessionController(session);
            createLifeCycle();
            lifeCycle.setSessionController(sc);
            lifeCycle.onClientOpen(session, ec);
        }

        @OnMessage
        public void onMessage(Reader message, Session session) throws IOException {
            lifeCycle.onClientMessage(message, session);
        }

        @OnClose
        public void onClose(Session s, CloseReason reason) {
            lifeCycle.onClientClose(s, reason);
        }

        @OnError
        public void onError(Session s, Throwable thr) {
            lifeCycle.onClientError(s, thr);
        }
    }
}

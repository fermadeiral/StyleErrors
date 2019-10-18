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

package org.glassfish.tyrus.tests.servlet.session;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

/**
 * @author Stepan Kopriva (stepan.kopriva at oracle.com)
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@ServerEndpoint(value = "/service")
public class ServiceEndpoint {
    private static final String POSITIVE = "1";
    private static final String NEGATIVE = "0";

    @OnMessage
    public String onMessage(String message) {

        if (message.equals("server")) {
            final CloseServerEndpoint closeServerEndpoint =
                    SingletonConfigurator.getEndpoint(CloseServerEndpoint.class);

            if (closeServerEndpoint.isAddMessageHandlerExceptionThrown()
                    && closeServerEndpoint.isRemoveMessageHandlerExceptionThrown()
                    && closeServerEndpoint.isGetBasicRemoteExceptionThrown()
                    && closeServerEndpoint.isGetAsyncRemoteExceptionThrown()
                    && closeServerEndpoint.isInCloseSendTextExceptionThrown()
                    && !closeServerEndpoint.isInCloseGetTimeoutExceptionThrown()) {
                return POSITIVE;
            } else {
                return NEGATIVE;
            }
        } else if (message.equals("client")) {
            final CloseClientEndpoint closeClientEndpoint =
                    SingletonConfigurator.getEndpoint(CloseClientEndpoint.class);

            return closeClientEndpoint.isInCloseSendTextExceptionThrown() ? POSITIVE : NEGATIVE;
        } else if (message.equals("idleTimeoutReceiving")) {
            final IdleTimeoutReceivingEndpoint idleTimeoutReceivingEndpoint =
                    SingletonConfigurator.getEndpoint(IdleTimeoutReceivingEndpoint.class);

            return idleTimeoutReceivingEndpoint.isOnCloseCalled() ? POSITIVE : NEGATIVE;
        } else if (message.equals("idleTimeoutSending")) {
            final IdleTimeoutSendingEndpoint idleTimeoutSendingEndpoint =
                    SingletonConfigurator.getEndpoint(IdleTimeoutSendingEndpoint.class);

            return idleTimeoutSendingEndpoint.isOnCloseCalled() ? POSITIVE : NEGATIVE;
        } else if (message.equals("idleTimeoutSendingPing")) {
            final IdleTimeoutSendingPingEndpoint idleTimeoutSendingPingEndpoint =
                    SingletonConfigurator.getEndpoint(IdleTimeoutSendingPingEndpoint.class);

            return idleTimeoutSendingPingEndpoint.isOnCloseCalled() ? POSITIVE : NEGATIVE;
        } else if (message.equals("reset")) {
            final IdleTimeoutReceivingEndpoint idleTimeoutReceivingEndpoint =
                    SingletonConfigurator.getEndpoint(IdleTimeoutReceivingEndpoint.class);
            final IdleTimeoutSendingEndpoint idleTimeoutSendingEndpoint =
                    SingletonConfigurator.getEndpoint(IdleTimeoutSendingEndpoint.class);
            final IdleTimeoutSendingPingEndpoint idleTimeoutSendingPingEndpoint =
                    SingletonConfigurator.getEndpoint(IdleTimeoutSendingPingEndpoint.class);

            idleTimeoutReceivingEndpoint.setOnCloseCalled(false);
            idleTimeoutSendingEndpoint.setOnCloseCalled(false);
            idleTimeoutSendingPingEndpoint.setOnCloseCalled(false);
            return "-1";
        } else {
            return "-1";
        }
    }
}

/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.internal.commands;

import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.IClientTest;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent.RegistrationStatus;
import org.eclipse.jubula.client.core.events.AUTServerEvent;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.internal.APICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.StartAUTServerStateMessage;
import org.eclipse.jubula.tools.internal.constants.AUTStartResponse;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 06.08.2004
 *
 */
public class StartAUTServerStateCommand implements APICommand {
    /** the logger */
    private static Logger log = LoggerFactory
        .getLogger(StartAUTServerStateCommand.class);

    /** the message */
    private StartAUTServerStateMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (StartAUTServerStateMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        final int state = m_message.getReason();
        IClientTest clientTest = ClientTest.instance();
        String autServerCouldNotStart = Messages.AUTServerCouldNotStart
                + m_message.getDescription();
        switch (state) {
            case AUTStartResponse.OK: 
                log.info(Messages.AUTServerIsStarting);
                return null;
            case AUTStartResponse.IO:
                // HERE notify error listener -> closing system
                log.error(Messages.NoJavaFound + m_message.getDescription());
                clientTest.fireAUTServerStateChanged(
                    new AUTServerEvent(AUTServerEvent.INVALID_JAVA));
                break;
            case AUTStartResponse.DATA:
            case AUTStartResponse.EXECUTION:
            case AUTStartResponse.SECURITY:
            case AUTStartResponse.INVALID_ARGUMENTS:
            case AUTStartResponse.ERROR:
            case AUTStartResponse.COMMUNICATION:
                log.error(autServerCouldNotStart);
                clientTest.fireAUTServerStateChanged(new AUTServerEvent(
                        AUTServerEvent.COMMUNICATION, autServerCouldNotStart));
                break;
            case AUTStartResponse.AUT_MAIN_NOT_DISTINCT_IN_JAR:
            case AUTStartResponse.AUT_MAIN_NOT_FOUND_IN_JAR:
                log.info(autServerCouldNotStart);
                clientTest.fireAUTServerStateChanged(
                        new AUTServerEvent(AUTServerEvent.NO_MAIN_IN_JAR));
                break;
            case AUTStartResponse.NO_JAR_AS_CLASSPATH:
            case AUTStartResponse.SCANNING_JAR_FAILED:
                log.info(autServerCouldNotStart);
                clientTest.fireAUTServerStateChanged(
                        new AUTServerEvent(AUTServerEvent.INVALID_JAR));
                break;
            case AUTStartResponse.NO_SERVER_CLASS:
                log.error(autServerCouldNotStart);
                clientTest.fireAUTServerStateChanged(
                    new AUTServerEvent(AUTServerEvent.SERVER_NOT_INSTANTIATED));
                break;
            case AUTStartResponse.DOTNET_INSTALL_INVALID:
                log.error(autServerCouldNotStart);
                clientTest.fireAUTServerStateChanged(
                    new AUTServerEvent(AUTServerEvent.DOTNET_INSTALL_INVALID));
                break;
            case AUTStartResponse.JDK_INVALID:
                log.error(autServerCouldNotStart);
                clientTest.fireAUTServerStateChanged(
                    new AUTServerEvent(AUTServerEvent.JDK_INVALID));
                break;
            default:
                log.error(Messages.UnknownState + StringConstants.SPACE  
                    + String.valueOf(state) 
                    + StringConstants.COLON + m_message.getDescription());
        }
        AutAgentRegistration.getInstance().fireAutRegistration(
            new AutRegistrationEvent(m_message.getAutId(),
                    RegistrationStatus.Deregister));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + StringConstants.DOT
                + Messages.TimeoutCalled);
    }
}

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

import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.internal.APICommand;
import org.eclipse.jubula.communication.internal.message.CAPTestResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.utils.IsAliveThread;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The command class for CAPTestResponseMessage. <br>
 * 
 * Currently the execute() methods logs the result and returns always null (no
 * message to send as response).
 * 
 * @author BREDEX GmbH
 * @created 07.09.2004
 * 
 */
public class CAPTestResponseCommand implements APICommand {
    /**
     * The logger
     */
    private static final Logger LOG =
        LoggerFactory.getLogger(CAPTestResponseCommand.class);
    /**
     * The message
     */
    private CAPTestResponseMessage m_capTestResponseMessage;
    
   
    /**
     * <code>m_messageCap</code> contains data sending to server for the
     * actual cap (test step)
     */
    private MessageCap m_messageCap;

    /**
     * constructor
     */
    public CAPTestResponseCommand() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_capTestResponseMessage;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_capTestResponseMessage = (CAPTestResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        logResult();
        Thread t = new IsAliveThread("Execute Test Step") { //$NON-NLS-1$
            public void run() {
                TestExecution.getInstance().processServerResponseWrapper(
                        (CAPTestResponseMessage)getMessage());
            }
        };
        t.start();
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        StringBuilder messageCapData = 
            new StringBuilder(StringConstants.SEMICOLON);
        messageCapData.append(Messages.MessageCap);
        messageCapData.append(StringConstants.COLON);
        messageCapData.append(StringConstants.SPACE);
        if (LOG.isErrorEnabled()) {
            if (m_capTestResponseMessage != null) {
                MessageCap msgCap = m_capTestResponseMessage.getMessageCap();
                messageCapData.append(Messages.Method)
                    .append(StringConstants.APOSTROPHE)
                    .append(msgCap.getMethod())
                    .append(StringConstants.APOSTROPHE)
                    .append(StringConstants.COMMA)
                    .append(StringConstants.SPACE)
                    .append(Messages.ComponentId)
                    .append(StringConstants.SPACE)
                    .append(StringConstants.APOSTROPHE)
                    .append(msgCap.getCi())
                    .append(StringConstants.APOSTROPHE);
            } else {
                messageCapData.append(Messages.Null);
            }
        }
        LOG.error(this.getClass().getName() 
                + StringConstants.SPACE + Messages.TimeoutCalled
                + messageCapData.toString());
        TestExecution.getInstance().timeout();
    }

    
    /**
     * private method logging the result on info level
     */
    private void logResult() {
        if (LOG.isInfoEnabled()) {
            String message = Messages.TestStepResult + StringConstants.COLON;
            int state = m_capTestResponseMessage.getState();
            switch (state) {
                case CAPTestResponseMessage.TEST_OK: 
                    message = message + Messages.Success;
                    if (!void.class.getName().equals(
                            m_capTestResponseMessage.getReturnType())) {
                        
                        message = message + StringConstants.NEWLINE 
                            + Messages.ReturnType + StringConstants.SPACE
                            + m_capTestResponseMessage.getReturnType()
                            + StringConstants.NEWLINE + Messages.ReturnValue
                            + StringConstants.COLON
                            + m_capTestResponseMessage.getReturnValue();
                    }
                    break;
                case CAPTestResponseMessage.TEST_FAILED:
                    message = message  
                        + Messages.GeneralFailure + StringConstants.NEWLINE;
                    break;
                case CAPTestResponseMessage.FAILURE_SECURITY:
                    message = message + NLS.bind(Messages.Failure, 
                                Messages.MissingPermission);
                    break;
                case CAPTestResponseMessage.FAILURE_ACCESSIBILITY:
                    message = message + NLS.bind(Messages.Failure, 
                                    Messages.MethodNotAccesible);
                    break;
                case CAPTestResponseMessage
                    .FAILURE_INVALID_IMPLEMENTATION_CLASS:
                    message = message + NLS.bind(Messages.Failure, 
                                    Messages.MissingImplementationClass);
                    break;
                case CAPTestResponseMessage.FAILURE_METHOD_NOT_FOUND:
                    break;
                case CAPTestResponseMessage.FAILURE_INVALID_PARAMETER:
                    message = message + NLS.bind(Messages.Failure, 
                            Messages.parametersAreNotValid);
                    break;
                case CAPTestResponseMessage.FAILURE_STEP_EXECUTION:
                    message = message + NLS.bind(Messages.Failure, 
                            Messages.ImplementingMethodHasThrownAnException);
                    break;
                case CAPTestResponseMessage.FAILURE_UNSUPPORTED_COMPONENT:
                    message = message + NLS.bind(Messages.Failure, 
                            Messages.ComponentIsNotSupported);
                    break;
                case CAPTestResponseMessage.FAILURE_COMPONENT_NOT_FOUND:
                    message = message + NLS.bind(Messages.Failure, 
                            Messages.ComponentNotFound);
                    break;
                default:
                    message = Messages.UnknownState + StringConstants.COLON
                        + StringConstants.SPACE + state;
            }
            LOG.debug(message);
        }
    }
    /**
     * @return Returns the messageCap.
     */
    public MessageCap getMessageCap() {
        return m_messageCap;
    }

    /**
     * @param messageCap the message cap to set
     */
    public void setMessageCap(MessageCap messageCap) {
        m_messageCap = messageCap;
    }
}

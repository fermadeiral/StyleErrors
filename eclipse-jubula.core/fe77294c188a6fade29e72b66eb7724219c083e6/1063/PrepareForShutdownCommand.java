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
package org.eclipse.jubula.rc.common.commands;

import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.connection.Connection;
import org.eclipse.jubula.communication.internal.listener.IErrorHandler;
import org.eclipse.jubula.communication.internal.message.CAPTestResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.MessageHeader;
import org.eclipse.jubula.communication.internal.message.PrepareForShutdownMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.tools.internal.constants.AUTServerExitConstants;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Prepares the AUT and AUT Server for shutdown (i.e. deregisters communication
 * error listeners).
 * 
 * @author BREDEX GmbH
 * @created Mar 23, 2010
 */
public class PrepareForShutdownCommand implements ICommand {
    /**
     * Name of the variable to override the AUTs termination delay
     */
    public static final String AUT_KEEP_ALIVE_DELAY_VAR = "TEST_AUT_KEEP_ALIVE_DELAY"; //$NON-NLS-1$

    /**
     * the shutdown hook thread
     */
    private static Thread shutdownHookThread = null;
    
    /**
     * @author BREDEX GmbH
     * Shutdown hook runnable to allow a proper AUT termination 
     */
    private static class AUTProperTerminationShutdownHook implements Runnable {
        /**
         * to represent an additional delay to use when keeping the AUT alive
         */
        private int m_addDelay;

        /**
         * Constructor
         * 
         * @param addDelay
         *            the additional delay
         */
        public AUTProperTerminationShutdownHook(int addDelay) {
            setAddDelay(addDelay);
        }

        /** {@inheritDoc} */
        public void run() {
            AUTServer autRC = AUTServer.getInstance();
            // FIXME improve AUT toolkit handling 
            if (!autRC.isRcpAccessible()) {
                // send fake message back - say last CAP execution went OK
                // this is necessary as e.g. in Swing the AUT event thread blocks 
                // and thereby our event confirming until the AUT terminates
                sendFakeCAPTestReponseMessage(autRC);
            }
            Communicator agentCommunicator = autRC.getServerCommunicator();
            if (agentCommunicator != null) {
                agentCommunicator.clearListeners();
            }
            // keep the AUT alive to perform proper AUT termination synchronization
            long timeToWait = TimeoutConstants.AUT_KEEP_ALIVE_DELAY_DEFAULT;
            try {
                String value = EnvironmentUtils
                        .getProcessOrSystemProperty(AUT_KEEP_ALIVE_DELAY_VAR);
                timeToWait = Long.valueOf(value).longValue();
            } catch (NumberFormatException e) {
                // ignore invalid formatted values and use default instead
            }
            TimeUtil.delay(timeToWait + getAddDelay());
        }

        /**
         * @param autRC
         *            the AUT-Server instance to use
         */
        private void sendFakeCAPTestReponseMessage(AUTServer autRC) {
            Communicator iteCom = autRC.getCommunicator();
            CAPTestResponseMessage fakeMessage = 
                    new CAPTestResponseMessage();
            MessageCap fakeMessageCap = new MessageCap();
            fakeMessageCap.setCi(new ComponentIdentifier());
            fakeMessage.setMessageCap(fakeMessageCap);
            try {
                iteCom.send(fakeMessage);
            } catch (CommunicationException e) {
                // This might also occur if hook has been registered but
                // AUT terminates without a connection to the ITE e.g.
                // normal AUT shutdown 
                LOG.error(e.getLocalizedMessage(), e);
            }
        }

        /**
         * @return the addDelay
         */
        public int getAddDelay() {
            return m_addDelay;
        }

        /**
         * @param addDelay the addDelay to set
         */
        public void setAddDelay(int addDelay) {
            m_addDelay = addDelay;
        }
    }

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(PrepareForShutdownCommand.class);

    /** the message */
    private PrepareForShutdownMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        PrepareForShutdownMessage message = m_message;
        boolean isForce = message.isForce();
        if (isForce) {
            Communicator agentCommunicator = 
                    AUTServer.getInstance().getServerCommunicator();
            if (agentCommunicator != null) {
                agentCommunicator.clearListeners();
            }
            Connection autAgentConnection = agentCommunicator.getConnection();
            if (shutdownHookThread != null) {
                Runtime.getRuntime().removeShutdownHook(shutdownHookThread);
            }
            if (autAgentConnection != null) {
                // Add a listener to exit the AUT normally when the connection is 
                // closed
                autAgentConnection.addErrorHandler(new IErrorHandler() {
                    public void shutDown() {
                        terminate();
                    }
                    public void sendFailed(MessageHeader header, 
                        String msg) {
                        terminate();
                    }
                    private void terminate() {
                        try {
                            AUTServer.getInstance().shutdown();
                        } finally {
                            System.exit(AUTServerExitConstants.EXIT_OK);
                        }
                    }
                });
            }
        } else {
            if (shutdownHookThread == null) {
                int addDelay = message.getAdditionalDelay();
                shutdownHookThread = new Thread(
                        new AUTProperTerminationShutdownHook(addDelay));
                Runtime.getRuntime().addShutdownHook(shutdownHookThread);
            }
            
        }
        return null;
    }

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
        m_message = (PrepareForShutdownMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }
}

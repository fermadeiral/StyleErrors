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
import org.eclipse.jubula.client.core.events.AUTEvent;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.internal.APICommand;
import org.eclipse.jubula.communication.internal.message.AUTStartStateMessage;
import org.eclipse.jubula.communication.internal.message.AUTStateMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for <code>AUTComponentsMessage</code>. <br>
 * 
 * Execute() notifies the listener which was given at construction time.<br>
 * Timeout() notifies the same listener with error(ERROR_TIMEOUT)<br>.
 *
 * @author BREDEX GmbH
 * @created 05.10.2004
 * 
 */
public class AUTStartedCommand implements APICommand {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(AUTStartedCommand.class);

    /** the message */
    private AUTStartStateMessage m_message;

    /** the state of the AUT */
    private AUTStateMessage m_stateMessage;
    
    /** flag that is set at the end of execution */
    private boolean m_wasExecuted = false;
    
    /**
     * Constructor
     */
    public AUTStartedCommand() {
        // Nothing to initialize
    }

    /**
     * @param stateMessage The stateMessage to set.
     */
    public void setStateMessage(AUTStateMessage stateMessage) {
        m_stateMessage = stateMessage;
    }
    
    /**
     * analyze the state of the message and fire appropriate events.
     */
    private void fireAutStateChanged() {
        int state = m_stateMessage.getState();
        IClientTest clientTest = ClientTest.instance();
        switch (state) {
            case AUTStateMessage.RUNNING:
                log.info(Messages.AUTIsRunning);
                clientTest.fireAUTStateChanged(
                        new AUTEvent(AUTEvent.AUT_STARTED));
                break;
            case AUTStateMessage.START_FAILED:
                log.error(Messages.AUTCouldNotStarted
                        + m_stateMessage.getDescription());
                clientTest.fireAUTStateChanged(new AUTEvent(
                        AUTEvent.AUT_START_FAILED));
                break;
            default:
            // nothing here
        }
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
        m_message = (AUTStartStateMessage)message;
    }
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {

        // do this after the OM was build
        fireAutStateChanged();
        m_wasExecuted = true;
        return null;
    }

    /**
     * 
     * @return <code>true</code> if the command has been successfully executed.
     *         Otherwise, <code>false</code>.
     */
    public boolean wasExecuted() {
        return m_wasExecuted;
    }
    
    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.warn(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}

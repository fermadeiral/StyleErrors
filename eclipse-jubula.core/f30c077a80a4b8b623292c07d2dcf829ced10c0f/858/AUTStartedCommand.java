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

import org.eclipse.jubula.communication.internal.APICommand;
import org.eclipse.jubula.communication.internal.message.AUTStartStateMessage;
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

    /**
     * Constructor
     */
    public AUTStartedCommand() {
        // Nothing to initialize
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
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.warn(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}
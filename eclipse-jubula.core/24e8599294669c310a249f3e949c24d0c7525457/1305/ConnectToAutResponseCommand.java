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
import org.eclipse.jubula.communication.internal.message.ConnectToAutResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Client-side handling for the result of an attempt to connect to an AUT.
 *
 * @author BREDEX GmbH
 * @created Mar 19, 2010
 */
public class ConnectToAutResponseCommand implements APICommand {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(ConnectToAutResponseCommand.class);
    
    /** the message */
    private ConnectToAutResponseMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        // Nothing to execute
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ConnectToAutResponseMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (ConnectToAutResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.warn(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }
}

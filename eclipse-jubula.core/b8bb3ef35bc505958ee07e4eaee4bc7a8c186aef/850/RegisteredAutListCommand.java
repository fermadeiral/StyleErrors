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

import org.eclipse.jubula.client.internal.Synchronizer;
import org.eclipse.jubula.communication.internal.APICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.RegisteredAutListMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Returns a collection of all currently running AUTs.
 *
 * @author BREDEX GmbH
 * @created Jan 26, 2010
 */
public class RegisteredAutListCommand implements APICommand {
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(RegisteredAutListCommand.class);
    
    /** the message */
    private RegisteredAutListMessage m_message;

    /**
     * Constructor
     */
    public RegisteredAutListCommand() {
    }
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        try {
            Synchronizer.instance().exchange(m_message.getAutIds());
        } catch (InterruptedException e) {
            LOG.debug(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public RegisteredAutListMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (RegisteredAutListMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + " timeout called."); //$NON-NLS-1$
    }
}
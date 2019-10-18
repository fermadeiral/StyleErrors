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

import java.util.concurrent.Exchanger;

import org.eclipse.jubula.client.internal.Synchronizer;
import org.eclipse.jubula.communication.internal.APICommand;
import org.eclipse.jubula.communication.internal.message.AutRegisteredMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Notifies the client that an AUT has been registered/deregistered from the 
 * AUT Agent.
 *
 * @author BREDEX GmbH
 * @created Jan 26, 2010
 */
public class AutRegisteredCommand implements APICommand {

    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(AutRegisteredCommand.class);

    /** the message */
    private AutRegisteredMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        if (m_message.isRegistered()) {
            Exchanger<Object> exchanger = Synchronizer.instance();
            try {
                exchanger.exchange(m_message.getAutId());
            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage(), e);
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
        m_message = (AutRegisteredMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}

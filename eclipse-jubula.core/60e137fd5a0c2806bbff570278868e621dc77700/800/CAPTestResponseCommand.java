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
import org.eclipse.jubula.communication.internal.message.CAPTestResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.MessageCap;
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
        Exchanger<Object> exchanger = Synchronizer.instance();
        try {
            exchanger.exchange(m_capTestResponseMessage);
        } catch (InterruptedException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + " timeout called."); //$NON-NLS-1$
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
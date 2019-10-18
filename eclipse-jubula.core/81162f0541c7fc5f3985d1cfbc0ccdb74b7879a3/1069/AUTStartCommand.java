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

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.AUTStartMessage;
import org.eclipse.jubula.communication.internal.message.AUTStateMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.rc.common.AUTServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command class for the message AUTStart. <br>
 * The execute() method calls AUTServer.invokeAUT() and returns a
 * <code>AUTStateMessage</code>.
 * 
 * @author BREDEX GmbH
 * @created 12.08.2004
 * 
 */
public class AUTStartCommand implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AUTStartCommand.class);
    
    /** the message */
    private AUTStartMessage m_message;
    
    
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
        m_message = (AUTStartMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        AUTServer.getInstance().startAUT();
        return new AUTStateMessage(AUTStateMessage.RUNNING);   
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }
}
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
import org.eclipse.jubula.communication.internal.message.AUTHighlightComponentMessage;
import org.eclipse.jubula.communication.internal.message.AUTHighlightComponentResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.rc.common.AUTServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for ChangeAUTModeMessage. <br>
 * The execute() method enables the <code>mode</code> and returns a
 * AUTModeChangedMessage.
 * 
 * @author BREDEX GmbH
 * @created 09.08.2005
 * 
 */
public class AUTHighlightComponentCommand implements ICommand {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(AUTHighlightComponentCommand.class);

    /** the message */
    private AUTHighlightComponentMessage m_message;
    
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
        m_message = (AUTHighlightComponentMessage) message;

    }

    /**
     * Changes the mode aof the AUTServer to the mode taken from the message.
     * Returns an AUTModeChangedMessage with the new mode.
     * 
     * {@inheritDoc}
     */
    public Message execute() {
        AUTHighlightComponentResponseMessage response = 
            new AUTHighlightComponentResponseMessage();
        response.setVerified(AUTServer.getInstance().
                highlightComponent(m_message.getComponent()));
        return response;
    }

    /** 
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}

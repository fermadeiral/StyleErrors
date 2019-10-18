/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
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
import org.eclipse.jubula.communication.internal.message.AUTErrorsResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.rc.common.AUTServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collecting errors and warnings which happened during the connection setup
 * between client and AUT
 *
 * @author BREDEX GmbH
 * @created 21.7.2015
 */
public class AUTErrorsCommand implements ICommand {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(AUTErrorsCommand.class);
    /** The message */
    private Message m_message;
    /**
     * @return the message
     */
    public Message getMessage() {
        return m_message;
    }
    
    /**
     * sets the message
     * 
     * @param message the message which should be set as the message of this command
     */
    public void setMessage(Message message) {
        m_message = message;
    }

    /**
     * Collecting errors and warnings
     * @return the response Message
     */
    public Message execute() {
        AUTErrorsResponseMessage response = 
            new AUTErrorsResponseMessage(
                    AUTServer.getInstance().getErrors(),
                    AUTServer.getInstance().getWarnings());
        return response;
    }

    /** timeout occurred will awaiting this command */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}

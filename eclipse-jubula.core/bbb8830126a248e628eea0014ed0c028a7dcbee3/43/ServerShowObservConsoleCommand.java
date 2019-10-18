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
package org.eclipse.jubula.autagent.common.commands;

import org.eclipse.jubula.autagent.common.remote.dialogs.ObservationConsoleBP;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.ServerShowObservConsoleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command class for the message ServerShowActionShell. <br>
 * 
 * @author BREDEX GmbH
 * @created 12.08.2004
 * 
 */
public class ServerShowObservConsoleCommand implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory
        .getLogger(ServerShowObservConsoleCommand.class);
    
    /** the message */
    private ServerShowObservConsoleMessage m_message;
    
    
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
        m_message = (ServerShowObservConsoleMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        if (m_message.getCheck()) {
            ObservationConsoleBP.getInstance().setCheckLabel(true);
        } else {
            ObservationConsoleBP.getInstance().setCheckLabel(false);
        }
        
        switch (m_message.getAction()) {
            case ServerShowObservConsoleMessage.ACT_SHOW_ACTION_SHELL:
                ObservationConsoleBP.getInstance().create();
                break;
            case ServerShowObservConsoleMessage.ACT_CLOSE_ACTION_SHELL:
                ObservationConsoleBP.getInstance().closeShell();
                break;
            default:
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

}

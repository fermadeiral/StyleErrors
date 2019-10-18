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
package org.eclipse.jubula.autagent.commands;

import org.eclipse.jubula.autagent.remote.dialogs.ChooseCheckModeDialogBP;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.ServerShowDialogMessage;
import org.eclipse.jubula.communication.internal.message.ServerShowDialogResponseMessage;
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
public class ServerShowDialogCommand implements ICommand {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(ServerShowDialogCommand.class);
    
    /** the message */
    private ServerShowDialogMessage m_message;
    
    
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
        m_message = (ServerShowDialogMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        boolean isDialogOpen = false;
        switch (m_message.getAction()) {
            case ServerShowDialogMessage.ACT_SHOW_CHECK_DIALOG:
                ChooseCheckModeDialogBP.getInstance().create(
                    m_message.getComponent(), m_message.getCompId(), 
                    m_message.getPoint(), m_message.getCheckValues(),
                    m_message.getLogicalName());
                isDialogOpen = true;
                break;
            case ServerShowDialogMessage.ACT_CLOSE_CHECK_DIALOG:
                ChooseCheckModeDialogBP.getInstance().closeDialog();
                isDialogOpen = false;
                break;
            default:
                isDialogOpen = false;
        }
        return new ServerShowDialogResponseMessage(isDialogOpen);
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

}

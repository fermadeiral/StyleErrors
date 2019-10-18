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
import org.eclipse.jubula.communication.internal.message.ShowObservInfoMessage;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command class for logging recorded actions in shell on server. <br>
 * xxx
 * 
 * @author BREDEX GmbH
 * @created 21.05.2008
 * 
 */
public class ShowObservInfoCommand implements ICommand {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(ShowObservInfoCommand.class);
    
    /** the message */
    private ShowObservInfoMessage m_message;
    
    
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
        m_message = (ShowObservInfoMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        //append extra Message to TextArea(ObseravtionConsole)
        if (m_message.getExtraMessage() != null
                && (!m_message.getExtraMessage().equals(
                        StringConstants.EMPTY))) {
            String extraMsg = m_message.getExtraMessage();
            ObservationConsoleBP.getInstance().setExtraMessage(extraMsg);
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

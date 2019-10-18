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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.autagent.remote.dialogs.ObservationConsoleBP;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.ShowRecordedActionMessage;
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
public class ShowRecordedActionCommand implements ICommand {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(ShowRecordedActionCommand.class);
    
    /** the message */
    private ShowRecordedActionMessage m_message;
    
    
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
        m_message = (ShowRecordedActionMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        //append recorded action to TextArea(Console)
        if (m_message.isRecorded()) {
            String recAction = m_message.getRecAction();
            ObservationConsoleBP.getInstance().setRecordedAction(recAction);
            if (StringUtils.isNotEmpty(m_message.getExtraMessage())) {
                String extraMsg = m_message.getExtraMessage();
                ObservationConsoleBP.getInstance().setExtraMessage(extraMsg);
            }
        } else {
            ObservationConsoleBP.getInstance().setRecordedActionFailed();
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

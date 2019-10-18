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
package org.eclipse.jubula.client.core.commands;

import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.events.AUTServerEvent;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.AUTModeChangedMessage;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for an AUTModeChangedMessage. <br>
 * 
 * The execute method just logs the new mode on info level.
 *
 * @author BREDEX GmbH
 * @created 23.08.2004
 * 
 */
public class AUTModeChangedCommand implements ICommand {
    /** the logger */
    private static Logger log  = 
        LoggerFactory.getLogger(AUTModeChangedCommand.class);

    /**
     * aut mode
     */
    private static int autMode = 0;
    
    /** the message */
    private AUTModeChangedMessage m_message;
    
    
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
        m_message = (AUTModeChangedMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        log.info(Messages.AUTServerModeChangedTo + StringConstants.SPACE
                + String.valueOf(m_message.getMode()));

            // HERE notify listener about changed mode 
        setAutMode(m_message.getMode());
        AUTServerEvent event;    
        switch (m_message.getMode()) {
            case ChangeAUTModeMessage.OBJECT_MAPPING :
                event = 
                    new AUTServerEvent(AUTServerEvent.MAPPING_MODE);
                ClientTest.instance().
                    fireAUTServerStateChanged(event);
                break;
            case ChangeAUTModeMessage.TESTING :
                event = 
                    new AUTServerEvent(AUTServerEvent.TESTING_MODE);
                ClientTest.instance().
                    fireAUTServerStateChanged(event);
                break;
            case ChangeAUTModeMessage.RECORD_MODE :
                event = 
                    new AUTServerEvent(AUTServerEvent.RECORD_MODE);
                ClientTest.instance().
                    fireAUTServerStateChanged(event);
                break;
            case ChangeAUTModeMessage.CHECK_MODE :
                event = 
                    new AUTServerEvent(AUTServerEvent.CHECK_MODE);
                ClientTest.instance().
                    fireAUTServerStateChanged(event);
                break;
            default : 
                break;
               
        }
        // no message to send back
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

    /**
     * @return Returns the autMode.
     */
    public static int getAutMode() {
        return autMode;
    }
    /**
     * @param a The autMode to set.
     */
    public static void setAutMode(int a) {
        autMode = a;
    }

}

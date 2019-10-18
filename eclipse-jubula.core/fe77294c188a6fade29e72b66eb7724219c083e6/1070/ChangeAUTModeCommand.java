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
import org.eclipse.jubula.communication.internal.message.AUTModeChangedMessage;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.ServerShowDialogMessage;
import org.eclipse.jubula.communication.internal.message.ServerShowObservConsoleMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for ChangeAUTModeMessage. <br>
 * The execute() method enables the <code>mode</code> and returns a
 * AUTModeChangedMessage.
 * 
 * @author BREDEX GmbH
 * @created 23.08.2004
 * 
 */
public class ChangeAUTModeCommand implements ICommand {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(ChangeAUTModeCommand.class);

    /** the message */
    private ChangeAUTModeMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * Stores also the new configuration into {@link AUTServerConfiguration}
     * contained in the given message.
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (ChangeAUTModeMessage) message;
        final AUTServerConfiguration autServerConfig =
                AUTServerConfiguration.getInstance();
        switch (m_message.getMode()) {
            case ChangeAUTModeMessage.CHECK_MODE :
            case ChangeAUTModeMessage.RECORD_MODE :
            case ChangeAUTModeMessage.AGENT_OBJECT_MAPPING :
            case ChangeAUTModeMessage.OBJECT_MAPPING :
                autServerConfig.setMappingKeyMod(m_message
                        .getMappingKeyModifier());
                autServerConfig.setMappingWithParentsKeyMod(m_message
                        .getMappingWithParentsKeyModifier());
                autServerConfig.setMappingKey(m_message.getMappingKey());
                autServerConfig.setMappingWithParentsKey(m_message
                        .getMappingWithParentsKey());
                autServerConfig.setMappingMouseButton(m_message
                        .getMappingMouseButton());
                autServerConfig.setMappingWithParentsMouseButton(m_message
                        .getMappingWithParentsMouseButton());
                autServerConfig.setKey2Mod(m_message.getKey2Modifier());
                autServerConfig.setKey2(m_message.getKey2());
                autServerConfig.setCheckModeKeyMod(m_message
                        .getCheckModeKeyModifier());
                autServerConfig.setCheckModeKey(m_message.getCheckModeKey());
                autServerConfig.setCheckCompKeyMod(m_message
                        .getCheckCompKeyModifier());
                autServerConfig.setCheckCompKey(m_message.getCheckCompKey());
                autServerConfig.setSingleLineTrigger(m_message
                        .getSingleLineTrigger());
                autServerConfig.setMultiLineTrigger(m_message
                        .getMultiLineTrigger());
            default :
        }
    }

    /**
     * Changes the mode of the AUTServer to the mode taken from the message.
     * Returns an AUTModeChangedMessage with the new mode.
     * 
     * {@inheritDoc}
     */
    public Message execute() {
        log.info("changing mode of the AUTServer to: " //$NON-NLS-1$
                + m_message.getMode());

        AUTServer autserver = AUTServer.getInstance();
        int oldMode = autserver.getMode();
        autserver.setMode(m_message.getMode());
        autserver.refreshMode();
        ServerShowObservConsoleMessage shellMsg = 
            new ServerShowObservConsoleMessage();
        ServerShowDialogMessage dialogMsg =
            new ServerShowDialogMessage();     
        switch (m_message.getMode()) {
            case ChangeAUTModeMessage.CHECK_MODE :
                if (m_message.getRecordDialogOpen()) {                    
                    shellMsg.setAction(ServerShowObservConsoleMessage
                            .ACT_SHOW_ACTION_SHELL);
                }
                shellMsg.setCheck(true);
                break;
            case ChangeAUTModeMessage.RECORD_MODE :
                if (m_message.getRecordDialogOpen()) {
                    shellMsg.setAction(ServerShowObservConsoleMessage
                            .ACT_SHOW_ACTION_SHELL);
                }
                shellMsg.setCheck(false);
                break;
            default :
                shellMsg.setAction(ServerShowObservConsoleMessage
                        .ACT_CLOSE_ACTION_SHELL);
                dialogMsg.setAction(ServerShowDialogMessage
                        .ACT_CLOSE_CHECK_DIALOG);
        }        

        try {
            autserver.getServerCommunicator().send(shellMsg);
            if (m_message.getMode() != oldMode && (m_message.getMode() 
                    == ChangeAUTModeMessage.TESTING)) {
                autserver.getServerCommunicator().send(dialogMsg);
            }
        } catch (CommunicationException e) {
            // Could not send message to AUT Agent. This is not a problem,
            // as it means that there is no more connection to the AUT Agent,
            // and if there is no connection to the AUT Agent, then this AUT
            // will be ending shortly anyway.
        }

        AUTModeChangedMessage result = new AUTModeChangedMessage();
        result.setMode(autserver.getMode());
        return result;
    }

    /** 
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}

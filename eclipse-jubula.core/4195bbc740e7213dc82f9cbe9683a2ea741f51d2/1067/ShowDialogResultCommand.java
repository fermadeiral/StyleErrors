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

import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.ServerShowDialogResponseMessage;
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
public class ShowDialogResultCommand implements ICommand {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(ShowDialogResultCommand.class);

    /** the message */
    private ServerShowDialogResponseMessage m_message;
    
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
        m_message = (ServerShowDialogResponseMessage) message;

    }

    /**
     * Changes the mode of the AUTServer to the mode taken from the message.
     * Returns an AUTModeChangedMessage with the new mode.
     * 
     * {@inheritDoc}
     */
    public Message execute() {
        AUTServer autserver = AUTServer.getInstance();
        autserver.setObservingDialogOpen(m_message.isOpen());
        if (m_message.belongsToDialog()) {
            changeCheckModeState(m_message.getMode());
        } else {
            changeCheckModeState(autserver.getMode());
        }        
        
        return null;
    }
    
    /**
     * change CheckModeState
     * @param mode int
     */
    private void changeCheckModeState(int mode) {
        ChangeAUTModeMessage msg = new ChangeAUTModeMessage();
        msg.setMode(mode);
        AUTServerConfiguration config = AUTServerConfiguration.getInstance();
        msg.setMappingKey(config.getMappingKey());
        msg.setMappingWithParentsKey(config.getMappingWithParentsKey());
        msg.setMappingKeyModifier(config.getMappingKeyMod());
        msg.setMappingWithParentsKeyModifier(
                config.getMappingWithParentsKeyMod());
        msg.setKey2(config.getKey2());
        msg.setKey2Modifier(config.getKey2Mod());
        msg.setCheckModeKey(config.getCheckModeKey());
        msg.setCheckModeKeyModifier(config.getCheckModeKeyMod());
        msg.setCheckCompKey(config.getCheckCompKey());
        msg.setCheckCompKeyModifier(config.getCheckCompKeyMod());

        msg.setSingleLineTrigger(config.getSingleLineTrigger());
        msg.setMultiLineTrigger(config.getMultiLineTrigger());

        ChangeAUTModeCommand cmd = new ChangeAUTModeCommand();
        cmd.setMessage(msg);
        try {
            Communicator clientCommunicator =
                AUTServer.getInstance().getCommunicator();
            if (clientCommunicator != null 
                    && clientCommunicator.getConnection() != null) {
                clientCommunicator.send(cmd.execute());
            }
        } catch (CommunicationException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /** 
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}

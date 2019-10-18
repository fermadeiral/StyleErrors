/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.autagent.desktop.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.autagent.agent.AutAgent;
import org.eclipse.jubula.autagent.desktop.DesktopIntegration;
import org.eclipse.jubula.autagent.desktop.connection.DirectAUTConnection;
import org.eclipse.jubula.autagent.gui.ObjectMappingFrame;
import org.eclipse.jubula.autagent.gui.utils.AgentOMKeyProperitesUtils;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.constants.InputCodeHelper.UserInput;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.tools.internal.constants.InputConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this is an {@link ActionListener} which is starting the object
 * mapping mode for the aut agent. This is done by sending a message
 * direct to the AUT. The mapping is than done via the connection between
 * AUT and Agent
 * @author BREDEX GmbH
 *
 */
public class StartOMActionListener implements ActionListener {
    
    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(StartOMActionListener.class);
    
    /** the {@link AutIdentifier} which is used for the MenuItem and
     * to get the Connection from the AUTagent to the AUT */
    private AutIdentifier m_autID;
    
    /**
     * 
     * @param autID the {@link AutIdentifier} which is used to get 
     * the connection from the AUTagent to the AUT
     */
    public StartOMActionListener(AutIdentifier autID) {
        m_autID = autID;
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        IStatus autConnection = null;
        try {
            AutAgent agent = AutStarter.getInstance().getAgent();
            AutIdentifier id = m_autID;
            Communicator comm = agent.getAutCommunicator(id);

            DirectAUTConnection connection = 
                    new DirectAUTConnection(comm, id);
            connection.setup();
            ChangeAUTModeMessage message = 
                    new ChangeAUTModeMessage();
            message.setMode(
                    ChangeAUTModeMessage.AGENT_OBJECT_MAPPING);
            int modifier = AgentOMKeyProperitesUtils.getModifier();
            UserInput userInput = AgentOMKeyProperitesUtils.getInput();
            int input = userInput.getCode();
            int inputType = userInput.getType();
            message.setMappingKeyModifier(modifier);
            switch (inputType) {
                case InputConstants.TYPE_MOUSE_CLICK:
                    message.setMappingMouseButton(input);
                    message.setMappingKey(InputConstants.NO_INPUT);
                    break;
                case InputConstants.TYPE_KEY_PRESS:
                    // fall through
                default:
                    message.setMappingKey(input);
                    message.setMappingMouseButton(InputConstants.NO_INPUT);
                    break;
            }
            message.setMappingWithParentsKeyModifier(192);
            switch (1) {
                case InputConstants.TYPE_MOUSE_CLICK:
                    message.setMappingWithParentsMouseButton(65);
                    message.setMappingWithParentsKey(InputConstants.NO_INPUT);
                    break;
                case InputConstants.TYPE_KEY_PRESS:
                    // fall through
                default:
                    message.setMappingWithParentsKey(65);
                    message.setMappingWithParentsMouseButton(
                            InputConstants.NO_INPUT);
                    break;
            }
            connection.send(message);
            ObjectMappingEventDispatcher
                .removeObserver(ObjectMappingFrame.INSTANCE);
            ObjectMappingFrame.INSTANCE.showObjectMappingPanel();
            ObjectMappingEventDispatcher
            .addObserver(ObjectMappingFrame.INSTANCE);
            DesktopIntegration.setObjectMappingAUT(id);
        } catch (CommunicationException | IllegalArgumentException ex) {
            DesktopIntegration.setObjectMappingAUT(null);
            LOG.error("There was an error during start of the OMM", ex); //$NON-NLS-1$
        }
    }

}

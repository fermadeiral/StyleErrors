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
package org.eclipse.jubula.autagent.desktop.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.autagent.common.AutStarter;
import org.eclipse.jubula.autagent.common.agent.AutAgent;
import org.eclipse.jubula.autagent.common.desktop.DesktopIntegration;
import org.eclipse.jubula.autagent.common.gui.ObjectMappingFrame;
import org.eclipse.jubula.autagent.desktop.connection.DirectAUTConnection;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.om.ObjectMappingDispatcher;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stops the Object Mapping mode for the AutAgent
 * @author BREDEX GmbH
 *
 */
public class StopOMActionListener implements ActionListener {
    
    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(StopOMActionListener.class);

    /** the {@link AutIdentifier} which is used for the MenuItem and
     * to get the Connection from the AUTagent to the AUT */
    private AutIdentifier m_autID;

    /**
     * 
     * @param autID the {@link AutIdentifier} which is used to get 
     * the connection from the AUTagent to the AUT
     */
    public StopOMActionListener(AutIdentifier autID) {
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

            DirectAUTConnection connection = new DirectAUTConnection(comm, id);
            connection.setup();
            ChangeAUTModeMessage message = new ChangeAUTModeMessage();
            message.setMode(ChangeAUTModeMessage.TESTING);

            connection.send(message);

            ObjectMappingDispatcher
            .removeObserver(ObjectMappingFrame.INSTANCE);
            
            DesktopIntegration.setObjectMappingAUT(null);
        } catch (CommunicationException | IllegalArgumentException ex) {
            DesktopIntegration.setObjectMappingAUT(null);
            LOG.error("There was an error during stop of the OMM", ex); //$NON-NLS-1$
        }
    }

}

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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import org.eclipse.jubula.client.core.commands.ServerLogResponseCommand;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IServerConnectionListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.events.IServerLogListener;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.communication.internal.message.SendServerLogMessage;
import org.eclipse.jubula.communication.internal.message.ServerLogResponseMessage;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;


/**
 * @author BREDEX GmbH
 * @created Feb 9, 2007
 */
public final class ShowServerLogBP implements IServerConnectionListener, 
                                              IServerLogListener {

    /** The timeout for request of aut starter log */
    private static final int TIMEOUT = 3000;
    
    /** the single instance */
    private static ShowServerLogBP instance = null;
    
    /** is there currently a connection to the aut starter? */
    private boolean m_isConnectedToServer;
    
    /** The response from the aut starter */
    private ServerLogResponseMessage m_response = null;
    
    /**
     * Private constructor
     */
    private ShowServerLogBP() {
        m_isConnectedToServer = false;
        DataEventDispatcher.getInstance().addAutAgentConnectionListener(this, 
            true);
    }
    
    /**
     * @return the single instance
     */
    public static ShowServerLogBP getInstance() {
        if (instance == null) {
            instance = new ShowServerLogBP();
        }
        return instance;
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleServerConnStateChanged(ServerState state) {
        m_isConnectedToServer = (state == ServerState.Connected);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return m_isConnectedToServer;
    }

    /**
     * {@inheritDoc}
     */
    public void processServerLog(ServerLogResponseMessage response) {
        m_response = response;
    }

    /**
     * Requests the aut starter log file and handles occurring errors.
     * @return The response for request of the aut starter log file. If the
     *         aut starter did not respond in time, then null is returned.
     */
    public ServerLogResponseMessage requestServerLog() {
        // Send request to aut starter and wait for response
        ServerLogResponseCommand request = new ServerLogResponseCommand(this);
        SendServerLogMessage message = new SendServerLogMessage();
        try {
            AutAgentConnection.getInstance().request(message, request, TIMEOUT);
        } catch (NotConnectedException nce) {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_NO_SERVER_CONNECTION_INIT);
        } catch (CommunicationException ce) {
            ErrorHandlingUtil.createMessageDialog(MessageIDs.E_MESSAGE_REQUEST);
        }
        
        int waited = 0;        
        while ((m_response == null) && (waited <= TIMEOUT)) {
            TimeUtil.delay(200);
            waited += 200;  
        }
        
        // reset m_response for next request of aut starter log
        ServerLogResponseMessage responseToReturn = m_response;
        m_response = null;
        
        handleServerRequestErrors(responseToReturn);
        
        return responseToReturn;
    }


    /**
     * Handles aut starter request errors by showing an error popup.
     * If the request was successful, then nothing is done.
     * @param response The response for request of the aut starter log file.
     */
    private void handleServerRequestErrors(ServerLogResponseMessage response) {
        if (response != null) {
            int status = response.getStatus();
            
            if (status == ServerLogResponseMessage.FILE_NOT_ENABLED) {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.I_FILE_LOGGING_NOT_ENABLED,
                        new String[] {"AUT Agent"}, null); //$NON-NLS-1$
            } else if (status == ServerLogResponseMessage.FILE_NOT_FOUND) {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_FILE_NOT_FOUND);
            } else if (status == ServerLogResponseMessage.IO_EXCEPTION) {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_IO_EXCEPTION);
            } else if (status == ServerLogResponseMessage.CONFIG_ERROR) {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_CONFIG_ERROR);
            }
        } else {
            ErrorHandlingUtil.createMessageDialog(MessageIDs.E_MESSAGE_REQUEST);
        }
    }
    
}

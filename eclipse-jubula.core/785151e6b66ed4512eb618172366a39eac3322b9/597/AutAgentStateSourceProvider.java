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
package org.eclipse.jubula.client.ui.rcp.sourceprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IServerConnectionListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.ui.ISources;


/**
 * Provides variables related to the status of the connection between the
 * client and AUT Agent.
 *
 * @author BREDEX GmbH
 * @created Apr 29, 2009
 */
public class AutAgentStateSourceProvider extends AbstractJBSourceProvider 
        implements IServerConnectionListener {
    
    /** 
     * ID of variable that indicates whether the client is currently connected 
     * to an AUT Agent
     */
    public static final String IS_CONNECTED_TO_AUT_AGENT = 
        "org.eclipse.jubula.client.ui.rcp.variable.isConnectedToAutAgent"; //$NON-NLS-1$

    /** 
     * ID of variable that indicates whether the client is currently connecting 
     * to an AUT Agent
     */
    public static final String IS_CONNECTING_TO_AUT_AGENT = 
        "org.eclipse.jubula.client.ui.rcp.variable.isConnectingToAutAgent"; //$NON-NLS-1$
    
    /**
     * Constructor.
     */
    public AutAgentStateSourceProvider() {
        DataEventDispatcher.getInstance().addAutAgentConnectionListener(
                this, true);
    }

    /** {@inheritDoc} */
    public void dispose() {
        DataEventDispatcher.getInstance()
            .removeAutAgentConnectionListener(this);
    }

    /** {@inheritDoc} */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentState = new HashMap<String, Object>();
        boolean isConnectedToAutAgent = false;
        try {
            isConnectedToAutAgent = 
                AutAgentConnection.getInstance().isConnected();
        } catch (ConnectionException e) {
            // Not connected. Do nothing.
        }

        currentState.put(IS_CONNECTED_TO_AUT_AGENT, 
                isConnectedToAutAgent);
        currentState.put(IS_CONNECTING_TO_AUT_AGENT, 
                false);
        
        return currentState;
    }

    /** {@inheritDoc} */
    public String[] getProvidedSourceNames() {
        return new String [] {IS_CONNECTED_TO_AUT_AGENT,
                              IS_CONNECTING_TO_AUT_AGENT};
    }

    /** {@inheritDoc} */
    public void handleServerConnStateChanged(ServerState state) {
        gdFireSourceChanged(
                ISources.WORKBENCH, 
                IS_CONNECTED_TO_AUT_AGENT, state == ServerState.Connected);
        gdFireSourceChanged(
                ISources.WORKBENCH, 
                IS_CONNECTING_TO_AUT_AGENT, state == ServerState.Connecting);
    }
}

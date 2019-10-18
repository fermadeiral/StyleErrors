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
import org.eclipse.ui.ISources;


/**
 * Provides variables related to the connected AUT / AUT AutAgent.
 *
 * @author BREDEX GmbH
 * @created Mar 3, 2010
 */
public class AutServerSourceProvider extends AbstractJBSourceProvider 
        implements IServerConnectionListener {

    /** 
     * ID of variable that indicates the state of the connection between 
     * the Client and the AUT / AUT AutAgent 
     */
    public static final String CONNECTION_STATUS = 
        "org.eclipse.jubula.client.ui.rcp.variable.autConnectionStatus"; //$NON-NLS-1$

    /** current state of the connection between the client and the AUT AutAgent */
    private ServerState m_connectionState = ServerState.Disconnected;
    
    /**
     * Constructor
     */
    public AutServerSourceProvider() {
        DataEventDispatcher.getInstance().addAutServerConnectionListener(
                this, false);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void dispose() {
        DataEventDispatcher.getInstance().removeAutServerConnectionListener(
                this);
    }

    /**
     * 
     * {@inheritDoc}
     */
    
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentState = new HashMap<String, Object>();

        currentState.put(CONNECTION_STATUS, m_connectionState.name());
        
        return currentState;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String [] {CONNECTION_STATUS};
    }

    /**
     * {@inheritDoc}
     */
    public void handleServerConnStateChanged(ServerState state) {
        m_connectionState = state;
        gdFireSourceChanged(ISources.WORKBENCH, 
                CONNECTION_STATUS, state.name());
    }

}

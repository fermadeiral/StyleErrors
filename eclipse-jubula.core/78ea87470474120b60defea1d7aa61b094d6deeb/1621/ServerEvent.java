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
package org.eclipse.jubula.client.core.events;

/**
 * Abstract class for server events. Defines constants for the state of a
 * connection to a server. <br>
 * 
 * This class is extended by the classes AUTServerEvent and AutAgentEvent.
 * 
 * @author BREDEX GmbH
 * @created 26.07.2004
 */
public abstract class ServerEvent {
    /**
     * constants representing the state of a connection to a server
     */
    /** connected to the server */
    public static final int CONNECTION_GAINED = 1;

    /** the connection to the server was closed */
    public static final int CONNECTION_CLOSED = 2;
    
    /**
     * number of defined constants. Use this in derived classes. Change this
     * when you add constants.
     */
    public static final int NUMBER_OF_CONSTANTS = 2;

    /** description of CONNECTION_GAINED for logging purpose */
    private static final String CG_DESCRIPTION = "connection established"; //$NON-NLS-1$

    /** description of CONNECTION_CLOSED for logging purpose */
    private static final String CC_DESCRIPTION = "connection closed"; //$NON-NLS-1$
    
    /** description of unknown state (this means it's an programming error)
     *  for logging purpose */
    private static final String US_DESCRIPTION = "unknown state"; //$NON-NLS-1$
    
    /** the new state */
    private int m_state;
    
    /**
     * constructor with parameter for the state, see constants
     * @param state the new state of the server
     */
    public ServerEvent(int state) {
        m_state = state;
    }
    /**
     * @return Returns the state.
     */
    public int getState() {
        return m_state;
    }
    
    /**
     * @param state The state to set.
     */
    public void setState(int state) {
        m_state = state;
    }
    
    /**
     * @return a readable description of the event
     */
    public String toString() {
        int state = getState();
        switch (state) {
            case CONNECTION_GAINED:
                return CG_DESCRIPTION;
            case CONNECTION_CLOSED:
                return CC_DESCRIPTION;
            default:
                return US_DESCRIPTION;
        }
    }
}    
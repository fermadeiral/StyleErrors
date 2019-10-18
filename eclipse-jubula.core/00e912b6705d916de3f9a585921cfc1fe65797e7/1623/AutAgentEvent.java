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
 * The event class containing state concerning the AUT Agent.
 *
 * @author BREDEX GmbH
 * @created 13.08.2004
 */
public class AutAgentEvent extends ServerEvent {
    /** could not connect to the server */
    public static final int SERVER_CANNOT_CONNECTED = NUMBER_OF_CONSTANTS + 1;
    
    /** version error between Client and AutStarter */
    public static final int VERSION_ERROR = NUMBER_OF_CONSTANTS + 2;
    
    /** description for logging purpose */
    private static final String SCC_DESCRIPTION = 
        "connection to server failed";  //$NON-NLS-1$

    /**
     * constructor with parameter for the state, see defined constants above
     * and in <code>ServerEvent</code>.
     * 
     * @param state
     *            the new state of the AUT Agent
     */
    public AutAgentEvent(int state) {
        super(state);
    }

    /**
     * @return a readable description of the event
     */
    public String toString() {
        if (getState() <= NUMBER_OF_CONSTANTS) {
            return super.toString();
        }
        
        return SCC_DESCRIPTION;
    }
}

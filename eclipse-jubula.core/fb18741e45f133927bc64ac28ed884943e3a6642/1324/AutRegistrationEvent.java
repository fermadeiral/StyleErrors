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
package org.eclipse.jubula.client.core.agent;

import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/**
 * An event representing a change in the registration status of an AUT with
 * an AUT Agent.
 *
 * @author BREDEX GmbH
 * @created Jan 27, 2010
 */
public final class AutRegistrationEvent {
    
    /** possible registration states */
    public static enum RegistrationStatus {
        /** the event was caused by registration of an AUT */
        Register, 
        
        /** the event was caused by deregistration of an AUT */
        Deregister
    }
    
    /** the ID for the AUT that caused this event */
    private AutIdentifier m_autId;
    
    /** the registration status that caused this event */
    private RegistrationStatus m_status;

    /**
     * Constructor
     * 
     * @param autId The ID for the AUT that caused this event.
     * @param status The registration status that caused this event.
     */
    public AutRegistrationEvent(AutIdentifier autId, 
            RegistrationStatus status) {

        m_autId = autId;
        m_status = status;
    }

    /**
     * @return the ID for the AUT that caused this event.
     */
    public AutIdentifier getAutId() {
        return m_autId;
    }

    /**
     * @return the registration status that caused this event.
     */
    public RegistrationStatus getStatus() {
        return m_status;
    }

}

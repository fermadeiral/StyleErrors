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
package org.eclipse.jubula.communication.internal.message;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/**
 * Message that an AUT registration event has occurred.
 * 
 * @author BREDEX GmbH
 * @created Jan 26, 2010
 */
public class AutRegisteredMessage extends Message {
    /** AUT ID */
    private AutIdentifier m_autId;

    /** boolean m_registered */
    private boolean m_registered;

    /**
     * Default constructor. Do nothing (required by Betwixt).
     */
    public AutRegisteredMessage() {
        // Nothing to initialize
    }

    /**
     * Constructor
     * 
     * @param autId
     *            The ID of the AUT that caused the event.
     * @param registered
     *            <code>true</code> if the event was caused by a registration,
     *            or <code>false</code> if it was caused by a deregistration.
     */
    public AutRegisteredMessage(AutIdentifier autId, boolean registered) {
        m_autId = autId;
        m_registered = registered;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.AUT_REGISTERED_COMMAND;
    }

    /**
     * @return the ID for the AUT that caused the event.
     */
    public AutIdentifier getAutId() {
        return m_autId;
    }

    /**
     * @param autId
     *            The ID for the AUT.
     */
    public void setAutId(AutIdentifier autId) {
        m_autId = autId;
    }

    /**
     * @return <code>true</code> if the event was caused by a registration, or
     *         <code>false</code> if it was caused by a deregistration.
     */
    public boolean isRegistered() {
        return m_registered;
    }

    /**
     * @param registered
     *            The AUT's registration status.
     */
    public void setRegistered(boolean registered) {
        m_registered = registered;
    }
}
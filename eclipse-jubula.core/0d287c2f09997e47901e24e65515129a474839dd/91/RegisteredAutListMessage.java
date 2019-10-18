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
 * @author BREDEX GmbH
 * @created Jan 26, 2010
 */
public class RegisteredAutListMessage extends Message {
    /**
     * the <code>autIds</code>
     */
    private AutIdentifier[] m_autIds;

    /**
     * Default constructor. Do nothing (required by Betwixt).
     */
    public RegisteredAutListMessage() {
        // Nothing to initialize
    }

    /**
     * Constructor
     * 
     * @param autIds
     *            All AUTs that should be reported as "running".
     */
    public RegisteredAutListMessage(AutIdentifier[] autIds) {
        m_autIds = autIds;
    }

    /** @return the autIds */
    public AutIdentifier[] getAutIds() {
        return m_autIds;
    }

    /**
     * @param autIds
     *            the autIds to set
     */
    public void setAutIds(AutIdentifier[] autIds) {
        m_autIds = autIds;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.REGISTERED_AUTS_COMMAND;
    }
}
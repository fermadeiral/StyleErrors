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
 * The message send from the client to the server to stop the AUTServer. <br>
 * The response message is StopAUTServerStateMessage.
 * 
 * @author BREDEX GmbH
 * @created 18.12.2007
 */
public class StopAUTServerMessage extends Message {
    /** autId */
    private AutIdentifier m_autId;

    /**
     * Default constructor. Not for use in standard development. Do nothing
     * (required by Betwixt).
     */
    public StopAUTServerMessage() {
        // Nothing to initialize
    }

    /**
     * Constructor
     * 
     * @param autId
     *            The ID of the AUT to stop.
     */
    public StopAUTServerMessage(AutIdentifier autId) {
        m_autId = autId;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.STOP_AUT_SERVER_COMMAND;
    }

    /** @return the ID of the AUT to stop. */
    public AutIdentifier getAutId() {
        return m_autId;
    }

    /**
     * @param autId
     *            The ID for the AUT to stop.
     */
    public void setAutId(AutIdentifier autId) {
        m_autId = autId;
    }
}
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

import org.eclipse.jubula.tools.internal.constants.AUTStartResponse;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/**
 * The message send from server to the client, transmitting the state of the
 * start process of the AUTServer. <br>
 * OK: AUTServer is starting
 * 
 * @author BREDEX GmbH
 * @created 04.08.2004
 */
public class StartAUTServerStateMessage extends Message {
    /** the reason */
    private int m_reason;

    /** a short textual description */
    private String m_description;
    
    /** */
    private AutIdentifier m_autId;

    /** empty default constructor */
    public StartAUTServerStateMessage() {
        super();
        m_reason = AUTStartResponse.UNKNOWN;
        m_description = StringConstants.EMPTY;
    }

    /**
     * public constructor
     * 
     * @param reason
     *            the reason why the AUTServer could not started, see constants
     */
    public StartAUTServerStateMessage(int reason) {
        this();
        m_reason = reason;
    }

    /**
     * public constructor
     * 
     * @param reason
     *            the reason why the AUTServer could not started, see constants
     * @param description
     *            a short textual description
     */
    public StartAUTServerStateMessage(int reason,
        String description) {
        this(reason);
        m_description = description;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.START_AUT_SERVER_STATE_COMMAND;
    }

    /** @return Returns the reason. */
    public int getReason() {
        return m_reason;
    }

    /**
     * @param reason
     *            The reason to set.
     */
    public void setReason(int reason) {
        m_reason = reason;
    }

    /** @return Returns the description. */
    public String getDescription() {
        return m_description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        m_description = description;
    }

    /**
     * @return the id of aut
     */
    public AutIdentifier getAutId() {
        return m_autId;
    }

    /**
     * @param autId the id of aut
     */
    public void setAutId(AutIdentifier autId) {
        this.m_autId = autId;
    }
}
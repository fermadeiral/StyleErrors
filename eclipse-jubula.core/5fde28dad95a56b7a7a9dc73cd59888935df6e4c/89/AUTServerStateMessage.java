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
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * The Message send from AUTServer to JubulaClient when the AUTServer is
 * started. The next message to the AUTServer should be StartAUTMessage.
 * 
 * @author BREDEX GmbH
 * @created 04.08.2004
 */
public class AUTServerStateMessage extends Message {
    /** constant for success */
    public static final int READY = 0;

    /**
     * constants signaling errors aut was not found
     */
    public static final int AUT_NOT_FOUND = READY + 1;

    /** getting main method per reflection failed */
    public static final int MAIN_METHOD_NOT_FOUND = AUT_NOT_FOUND + 1;

    /** getting main method per reflection failed */
    public static final int EXIT_AUT_WRONG_CLASS_VERSION = 
        MAIN_METHOD_NOT_FOUND + 1;

    /** the highest constant in this class, change this if you add constants. */
    public static final int MAX_CONSTANT = 4;

    /** the constant used, when no state is set */
    public static final int UNKNOWN = MAX_CONSTANT;

    /** the state */
    private int m_state;

    /** a short description */
    private String m_description;

    /** empty default constructor */
    public AUTServerStateMessage() {
        m_state = UNKNOWN;
        m_description = StringConstants.EMPTY;
    }

    /**
     * public constructor with state parameter
     * 
     * @param state
     *            the state of the aut server, see constants
     */
    public AUTServerStateMessage(int state) {
        this();
        m_state = state;
    }

    /**
     * public constructor with state and description parameter
     * 
     * @param state
     *            the state of the aut server, see constants
     * @param description
     *            a short description
     */
    public AUTServerStateMessage(int state, String description) {
        this(state);
        m_description = description;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.AUT_SERVER_STATE_COMMAND;
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

    /** @return Returns the state. */
    public int getState() {
        return m_state;
    }

    /**
     * @param state
     *            The state to set.
     */
    public void setState(int state) {
        m_state = state;
    }
}
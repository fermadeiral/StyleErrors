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

/**
 * This message is used to transmit the state of the AUT, see the constants. <br>
 * It's the response message to the AUTStartMessage.
 * 
 * @author BREDEX GmbH
 * @created 12.08.2004
 */
public class AUTStateMessage extends Message {
    /** state signaling that the AUT is running */
    public static final int RUNNING = 1;

    /** state signaling that the AUT could not started */
    public static final int START_FAILED = 2;

    /** Name of the command class */
    private static final String COMMAND_CLASS = "org.eclipse.jubula.client.core.commands.AUTStateCommand"; //$NON-NLS-1$

    /** the state of the AUT, see constants */
    private int m_state;

    /**
     * a short description in case of an error (actually only the state
     * AUT_START_FAILED)
     */
    private String m_description;

    /** public default constructor */
    public AUTStateMessage() {
        super();
    }

    /**
     * constructor with parameter for the state, use the defined constants *
     * 
     * @param state
     *            the state the AUT is in
     */
    public AUTStateMessage(int state) {
        this();
        m_state = state;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return COMMAND_CLASS;
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
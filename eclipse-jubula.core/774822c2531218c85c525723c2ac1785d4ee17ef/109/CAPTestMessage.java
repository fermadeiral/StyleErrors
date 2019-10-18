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

/**
 * This class sends a component-action-param-triple to the server.
 * 
 * @author BREDEX GmbH
 * @created 27.08.2004
 */
public class CAPTestMessage extends Message {
    /** The CAP message data. */
    private MessageCap m_messageCap;

    /**
     * Default constructor. Do nothing (required by Betwixt).
     */
    public CAPTestMessage() {
        // Nothing to be done
    }

    /**
     * Creates a new instance with the passed CAP message data. The data are
     * sent to the AUT server to execute a test step.
     * 
     * @param messageCap
     *            The message data
     */
    public CAPTestMessage(MessageCap messageCap) {
        m_messageCap = messageCap;
    }

    /**
     * Gets the CAP message data.
     * 
     * @return The message data.
     */
    public MessageCap getMessageCap() {
        return m_messageCap;
    }

    /**
     * Sets the CAP message data (required by Betwixt).
     * 
     * @param messageCap
     *            The message data
     */
    public void setMessageCap(MessageCap messageCap) {
        m_messageCap = messageCap;
    }
    
    /**
     * @return the command class
     */
    public final String getCommandClass() {
        return CommandConstants.CAP_TEST_COMMAND;
    }
}
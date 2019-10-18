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
 * The message to send recorded CAP <br>
 * 
 * @author BREDEX GmbH
 * @created 21.05.2008
 */
public class RecordActionMessage extends Message {
    /** The CAP message data. */
    private CAPRecordedMessage m_message;

    /**
     * Default constructor. Do nothing.
     */
    public RecordActionMessage() {
        // do nothing
    }

    /**
     * Creates a new instance with the passed CAP message data.
     * 
     * @param message
     *            The message data
     */
    public RecordActionMessage(CAPRecordedMessage message) {
        m_message = message;
    }

    /**
     * the message
     * 
     * @return the message
     */
    public CAPRecordedMessage getCAPRecordedMessage() {
        return m_message;
    }

    /**
     * Sets the CAP message data (required by Betwixt).
     * 
     * @param message
     *            The message data
     */
    public void setCAPRecordedMessage(CAPRecordedMessage message) {
        m_message = message;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.RECORD_ACTION_COMMAND;
    }
}
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
 * Response to an attempt to connect to an AUT.
 * 
 * @author BREDEX GmbH
 * @created Mar 18, 2010
 */
public class ConnectToAutResponseMessage extends Message {
    /** an error message, or <code>null</code> if no error occurred */
    private String m_errorMessage;

    /**
     * Constructor. Used by framework methods.
     * 
     * @deprecated
     */
    public ConnectToAutResponseMessage() {
        this(null);
    }

    /**
     * Constructor
     * 
     * @param errorMessage
     *            An error message, or <code>null</code> if no error occurred.
     */
    public ConnectToAutResponseMessage(String errorMessage) {
        m_errorMessage = errorMessage;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.CONNECT_TO_AUT_RESPONSE_COMMAND;
    }

    /** @return an error message, or <code>null</code> if no error occurred */
    public String getErrorMessage() {
        return m_errorMessage;
    }
}
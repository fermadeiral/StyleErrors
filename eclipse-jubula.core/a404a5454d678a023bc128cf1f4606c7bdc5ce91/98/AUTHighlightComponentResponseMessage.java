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
 * The response of a <code>ChangeAUTModeMessage</code>. <br>
 * Contains the new mode of the AUTServer.
 * 
 * @author BREDEX GmbH
 * @created 23.08.2004
 */
public class AUTHighlightComponentResponseMessage extends Message {
    /** set when component is highlighted by server */
    private boolean m_verified = false;

    /** default constructor */
    public AUTHighlightComponentResponseMessage() {
        super();
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.HIGHLIGHT_COMPONENT_COMMAND;
    }

    /** @return Returns the verified. */
    public boolean isVerified() {
        return m_verified;
    }

    /**
     * @param verified
     *            The verified to set.
     */
    public void setVerified(boolean verified) {
        m_verified = verified;
    }
}
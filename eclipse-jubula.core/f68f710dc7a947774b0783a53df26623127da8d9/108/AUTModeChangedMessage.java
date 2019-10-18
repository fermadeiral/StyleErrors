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
public class AUTModeChangedMessage extends Message {
    /** the new mode the AUTServer is in */
    private int m_mode;

    /** default constructor */
    public AUTModeChangedMessage() {
        super();
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.AUT_MODE_CHANGED_COMMAND;
    }

    /** @return Returns the mode. */
    public int getMode() {
        return m_mode;
    }

    /**
     * @param mode
     *            The mode to set.
     */
    public void setMode(int mode) {
        m_mode = mode;
    }
}
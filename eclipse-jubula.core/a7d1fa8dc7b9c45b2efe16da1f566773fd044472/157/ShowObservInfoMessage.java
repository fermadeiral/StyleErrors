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
 * message to send recorded Action name and information if CAP is recorded.
 * 
 * @author BREDEX GmbH
 * @created 27.08.2004
 */
public class ShowObservInfoMessage extends Message {
    /** additional Message */
    private String m_extraMsg;

    /**
     * Default constructor. Do nothing (required by Betwixt).
     */
    public ShowObservInfoMessage() {
        // Nothing to be done
    }

    /**
     * constructor
     * 
     * @param extraMsg
     *            additional Message / Info
     */
    public ShowObservInfoMessage(String extraMsg) {
        m_extraMsg = extraMsg;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.SHOW_OBSERVE_INFO_COMMAND;
    }

    /** @return additional Message / Info */
    public String getExtraMessage() {
        return m_extraMsg;
    }

    /**
     * @param extraMsg
     *            additional Message / Info
     */
    public void setExtraMessage(String extraMsg) {
        m_extraMsg = extraMsg;
    }
}
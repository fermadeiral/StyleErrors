/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.communication.internal.message.html;

import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * Message sent by the AUT containing the newly selected window's title
 * @author BREDEX GmbH
 *
 */
public class OMSelWinResponseMessage extends Message {

    /** OK */
    public static final int OK = 0;

    /** Could not activate the window - switched to default */
    public static final int NO_SUCH_WINDOW = 1;

    /** Possibly fatal error */
    public static final int UNEXPECTED_ERROR = 2;

    /** The selected window's title */
    private String m_title;

    /** The response */
    private int m_responseCode = OK;

    /**
     * @param title the title or null
     */
    public void setTitle(String title) {
        m_title = title;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.HTML_OMM_SEL_WIN_RESPONSE_COMMAND;
    }

    /**
     * @return the title of the selected window or null
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @param code the response code
     */
    public void setCode(int code) {
        m_responseCode = code;
    }

    /**
     * @return the response code
     */
    public int getCode() {
        return m_responseCode;
    }
}

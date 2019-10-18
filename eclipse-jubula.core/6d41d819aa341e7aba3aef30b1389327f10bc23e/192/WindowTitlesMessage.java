/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
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
 * This class sends the window titles of the HTML browser to the client.
 * @author BREDEX GmbH
 *
 */
public class WindowTitlesMessage extends Message {

    /**
     * a array containing the titles of open browser windows
     */
    private String[] m_windowTitles;
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.HTML_GET_WINDOW_TITLES_RESPONSE_COMMAND;
    }

    /**
     * 
     * @return the window titles of the application
     */
    public String[] getWindowTitles() {
        return m_windowTitles;
    }
    /**
     * 
     * @param windowTitles the titles of the windows
     */
    public void setWindowTitles(String[] windowTitles) {
        this.m_windowTitles = windowTitles;
    }

}

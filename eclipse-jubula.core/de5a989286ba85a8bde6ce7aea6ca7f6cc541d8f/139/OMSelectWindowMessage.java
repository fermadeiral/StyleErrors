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
 * @author BREDEX GmbH
 */
public class OMSelectWindowMessage extends Message {
    /** the title of the window which should be selected */
    private String m_windowTitle;
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.HTML_OMM_SELECT_WINDOW_COMMAND;
    }

    /**
     * @return the window which should be selected
     */
    public String getWindowTitle() {
        return m_windowTitle;
    }
    
    /**
     * @param windowTitle the window which should be selected
     */
    public void setWindowTitle(String windowTitle) {
        m_windowTitle = windowTitle;
    }
}

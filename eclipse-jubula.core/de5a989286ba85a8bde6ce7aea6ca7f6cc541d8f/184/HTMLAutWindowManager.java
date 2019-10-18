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
package org.eclipse.jubula.client.ui.rcp.utils;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IOMWindowsListener;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.communication.internal.message.html.OMSelWinResponseMessage;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
/**
 * The {@link HTMLAutWindowManager} tracks the open browser windows with their titles
 * @author BREDEX GmbH
 *
 */
public class HTMLAutWindowManager implements IOMWindowsListener {
    /** the singleton instance */
    private static HTMLAutWindowManager instance;
    /** the window titles of the browser*/
    private String[] m_windowTitles;
    /** the last window selected */
    private String m_lastSelectedWindow = null;
    
    /**
     * The constructor listens to the AUTWindowsEvents
     */
    private HTMLAutWindowManager() {
        DataEventDispatcher.getInstance().addAUTWindowsListener(this, false);
    }
    /**
     * 
     * @return single instance {@link HTMLAutWindowManager}
     */
    public static HTMLAutWindowManager getInstance() {
        if (instance == null) {
            instance = new HTMLAutWindowManager();
            return instance;
        }
        return instance;
    }
    
    /**
     * 
     * @return the titles of all browser windows
     */
    public String[] getWindowTitles() {
        return m_windowTitles;
    }

    /**
     * {@inheritDoc}
     */
    public void handleAUTChanged(String[] windowTitles) {
        m_windowTitles  = windowTitles;
    }
    
    /**
     * 
     * @return the title of the last selected window
     */
    public String getLastSelectedWindow() {
        return m_lastSelectedWindow;
    }
    
    /**
     * 
     * @param lastSelectedWindowTitle the title of the last selected window
     */
    public void setLastSelectedWindow(String lastSelectedWindowTitle) {
        this.m_lastSelectedWindow = lastSelectedWindowTitle;
    }

    /** {@inheritDoc} */
    public void handleNewWindowSelected(OMSelWinResponseMessage msg) {
        m_lastSelectedWindow = msg.getTitle();
        if (msg.getCode() == OMSelWinResponseMessage.NO_SUCH_WINDOW) {
            ErrorHandlingUtil.createMessageDialog(MessageIDs.E_OM_NO_SUCH_WIN,
                    new String[] {m_lastSelectedWindow}, null);
        } else if (msg.getCode() == OMSelWinResponseMessage.UNEXPECTED_ERROR) {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_OM_WINDOW_SWITCH_FAILED);
        }
    }
}

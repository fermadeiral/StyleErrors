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
 * The message to send information about opening the recordedActionDialog and
 * the checkmode status . <br>
 * 
 * @author BREDEX GmbH
 * @created 05.10.2004
 */
public class ServerShowObservConsoleMessage extends Message {
    /** opens the recordedAction dialog */
    public static final int ACT_SHOW_ACTION_SHELL = 1;

    /** closes the recordedAction dialog */
    public static final int ACT_CLOSE_ACTION_SHELL = 2;

    /** action/dialog that should be executed */
    private int m_action = 0;

    /** checkmode on/off */
    private boolean m_check = false;

    /** true if recorded actions dialog should be open, false otherwise */
    private boolean m_dialogOpen;

    /** empty constructor for serialisation */
    public ServerShowObservConsoleMessage() {
        // do nothing
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.SERVER_SHOW_OBSERV_CONSOLE_COMMAND;
    }

    /**
     * if dialog should be opened/closed
     * 
     * @return int
     */
    public int getAction() {
        return m_action;
    }

    /**
     * if dialog should be opened/closed
     * 
     * @param action
     *            int
     */
    public void setAction(int action) {
        m_action = action;
    }

    /**
     * checkmode on/off
     * 
     * @return boolean
     */
    public boolean getCheck() {
        return m_check;
    }

    /**
     * checkmode on/off
     * 
     * @param check
     *            boolean
     */
    public void setCheck(boolean check) {
        m_check = check;
    }

    /**
     * @return true if recorded actions dialog should be open, false otherwise
     */
    public boolean getRecordDialogOpen() {
        return m_dialogOpen;
    }

    /**
     * @param dialogOpen
     *            set state of recorded actions dialog
     */
    public void setRecordDialogOpen(boolean dialogOpen) {
        m_dialogOpen = dialogOpen;
    }
}
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
 * The message to send all supported and currently instantiated components of
 * the AUT. <br>
 * 
 * @author BREDEX GmbH
 * @created 05.10.2004
 */
public class ServerShowDialogResponseMessage extends Message {
    /** if the dialog is opened or closed */
    private boolean m_open;

    /** normal recordlistener or checkmode */
    private int m_mode = ChangeAUTModeMessage.CHECK_MODE;

    /**
     * true if closing of check dialog is caused by user-action on
     * dialog-buttons false if closing is caused by client
     */
    private boolean m_belongsToDialog = false;

    /** Constructor */
    public ServerShowDialogResponseMessage() {
        // only for serialisation
    }

    /**
     * constructor
     * 
     * @param open
     *            true if the dialog opens, false otherwise.
     */
    public ServerShowDialogResponseMessage(boolean open) {
        m_open = open;
    }

    /**
     * constructor
     * 
     * @param open
     *            true if the dialog opens, false otherwise.
     * @param mode
     *            checkmode if checkmode is on, recordmode otherwise.
     */
    public ServerShowDialogResponseMessage(boolean open, int mode) {
        m_open = open;
        m_mode = mode;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.SERVER_SHOW_DIALOG_RESULT_COMMAND;
    }

    /** @return the open */
    public boolean isOpen() {
        return m_open;
    }

    /** @return the mode */
    public int getMode() {
        return m_mode;
    }

    /**
     * @param mode
     *            int
     */
    public void setMode(int mode) {
        m_mode = mode;
    }

    /**
     * @return true if closing of check dialog is caused by user-action on
     *         dialog-buttons false if closing is caused by client
     */
    public boolean belongsToDialog() {
        return m_belongsToDialog;
    }

    /**
     * @param belongsToDialog
     *            boolean true if closing of check dialog is caused by
     *            user-action on dialog-buttons false if closing is caused by
     *            client
     */
    public void setBelongsToDialog(boolean belongsToDialog) {
        m_belongsToDialog = belongsToDialog;
    }
}
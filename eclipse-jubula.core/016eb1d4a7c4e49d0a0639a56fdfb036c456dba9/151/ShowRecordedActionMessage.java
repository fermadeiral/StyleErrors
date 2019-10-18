/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html

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
public class ShowRecordedActionMessage extends Message {
    /** if the action is recorded */
    private boolean m_recorded;

    /** name of recorded action */
    private String m_recAction;

    /** if additional Message exists */
    private boolean m_hasExtraMsg;

    /** additional Message */
    private String m_extraMsg;

    /**
     * Default constructor. Do nothing (required by Betwixt).
     */
    public ShowRecordedActionMessage() {
        // Nothing to be done
    }

    /**
     * constructor
     * 
     * @param recorded
     *            true if action recorded, false otherwise.
     */
    public ShowRecordedActionMessage(boolean recorded) {
        m_recorded = recorded;
    }

    /**
     * constructor
     * 
     * @param recorded
     *            true if action recorded, false otherwise.
     * @param recAction
     *            name of recorded Action
     */
    public ShowRecordedActionMessage(boolean recorded, String recAction) {
        m_recorded = recorded;
        m_recAction = recAction;
    }

    /**
     * constructor
     * 
     * @param recorded
     *            true if action recorded, false otherwise.
     * @param recAction
     *            name of recorded Action
     * @param extraMsg
     *            additional Message / Info
     */
    public ShowRecordedActionMessage(boolean recorded, String recAction,
            String extraMsg) {
        m_recorded = recorded;
        m_recAction = recAction;
        m_extraMsg = extraMsg;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.SHOW_RECORDED_ACTION_COMMAND;
    }

    /** @return if open or not */
    public boolean isRecorded() {
        return m_recorded;
    }

    /** @return true if additional Message exists */
    public boolean hasExtraMsg() {
        return m_hasExtraMsg;
    }

    /** @return name of recorded Action */
    public String getRecAction() {
        return m_recAction;
    }

    /**
     * @param recAction
     *            name of recorded Action
     */
    public void setRecAction(String recAction) {
        m_recAction = recAction;
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
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
 * @author BREDEX GmbH
 * @created Aug 19, 2010
 */
public class DisplayManualTestStepResponseMessage extends Message {
    /** <code>m_comment</code> */
    private String m_comment = null;

    /** <code>m_status</code> */
    private boolean m_status = false;

    /** Default */
    public DisplayManualTestStepResponseMessage() {
        super();
    }

    /**
     * Constructor
     * 
     * @param comment
     *            the comment
     * @param status
     *            true if succeeded, false otherwise
     */
    public DisplayManualTestStepResponseMessage(String comment, 
        boolean status) {
        setComment(comment);
        setStatus(status);
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.DISPLAY_MANUAL_TEST_STEP_RESPONSE_COMMAND;
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment(String comment) {
        m_comment = comment;
    }

    /** @return the comment */
    public String getComment() {
        return m_comment;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(boolean status) {
        m_status = status;
    }

    /** @return the status */
    public boolean isStatus() {
        return m_status;
    }
}
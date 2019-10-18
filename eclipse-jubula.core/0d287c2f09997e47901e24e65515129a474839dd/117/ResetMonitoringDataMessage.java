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
 * @created 13.09.2010
 */
public class ResetMonitoringDataMessage extends Message {
    /** The autId */
    private String m_autId;

    /** default constructor */
    public ResetMonitoringDataMessage() {
        // do nothing
    }

    /**
     * @param autId
     *            the autId
     */
    public ResetMonitoringDataMessage(String autId) {
        this.m_autId = autId;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.RESET_MONITORING_DATA_COMMAND;
    }

    /** @return the autId */
    public String getAutId() {
        return m_autId;
    }

    /**
     * @param autId
     *            the autId to set
     */
    public void setAutId(String autId) {
        m_autId = autId;
    }
}
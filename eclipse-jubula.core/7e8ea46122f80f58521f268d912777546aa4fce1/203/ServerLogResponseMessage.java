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
 * @created Feb 8, 2007
 */
public class ServerLogResponseMessage extends Message {
    /** status ok */
    public static final int OK = 0;

    /** file logging not enabled */
    public static final int FILE_NOT_ENABLED = 1;

    /** log file not found */
    public static final int FILE_NOT_FOUND = 2;

    /** an IOException occurred */
    public static final int IO_EXCEPTION = 3;

    /** configuration problem */
    public static final int CONFIG_ERROR = 4;

    /** the server log contents */
    private String m_serverLog;

    /** code for handling errors */
    private int m_status;

    /** Default constructor. Sets the initial status to OK. */
    public ServerLogResponseMessage() {
        m_status = OK;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.SERVER_LOG_RESPONSE_COMMAND;
    }

    /** @return the serverLog */
    public String getServerLog() {
        return m_serverLog;
    }

    /**
     * @param serverLog
     *            the serverLog to set
     */
    public void setServerLog(String serverLog) {
        m_serverLog = serverLog;
    }

    /** @return the status */
    public int getStatus() {
        return m_status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(int status) {
        m_status = status;
    }
}
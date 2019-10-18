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
 * @created 13.08.2010
 */
public class SendMonitoringReportMessage extends Message {

    /** port for the socket connection to stream the report back to the client */
    private int m_port;
    
    /** Default Constructor */
    public SendMonitoringReportMessage() {
        // currently empty
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.GET_MONITORING_REPORT_COMMAND;
    }

    /**
     * 
     * @return the port to use for data transfer
     */
    public int getPort() {
        return m_port;
    }
    /**
     * 
     * @param port the port to use for data transfer
     */
    public void setPort(int port) {
        this.m_port = port;
    }
        
}
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
package org.eclipse.jubula.client.core.commands;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.poi.util.IOUtils;
import org.eclipse.jubula.client.core.businessprocess.TestResultBP;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.SendMonitoringReportMessage;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 
 * @author BREDEX GmbH
 * @created 13.08.2010
 */
public class GetMonitoringReportCommand implements ICommand {
    /** The Logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(GetMonitoringReportCommand.class);

    /** The message from the agent, containing the report */
    private SendMonitoringReportMessage m_message;    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        
        byte[] report = null; 
        Socket socket = null;
        try {
            //Reading the monitoring report from the AutAgent
            socket = new Socket(
                    AutAgentConnection.getInstance().
                    getCommunicator().getHostName(), m_message.getPort());
            //writing stream content to byte array. This byte array will be stored in the database
            report = IOUtils.toByteArray(socket.getInputStream());
            LOG.info("The size of the received monitoring report " + report.length + " byte"); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (UnknownHostException e) {
            LOG.error("The given hostname is unknown", e); //$NON-NLS-1$ 
        } catch (IOException e) {
            LOG.error("IOException during socket read", e); //$NON-NLS-1$
        } catch (ConnectionException e) {           
            LOG.error("Connection to host failed", e); //$NON-NLS-1$
        } finally {           
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOG.error("IOException during socket close", e); //$NON-NLS-1$
                }
            }
        }        
        
        TestResult result = TestResultBP.getInstance().getResultTestModel(); 
        if (report == null) {
            result.setReportData(MonitoringConstants.EMPTY_REPORT);
            LOG.info("Monitoring report is empty"); //$NON-NLS-1$
        } else {
            result.setReportData(report);
        }
          
        return null;    
    }
    /**
     * @param message
     *            the message to set
     */
    public void setMessage(Message message) {
        m_message = (SendMonitoringReportMessage)message;
    }
    /**
     * @return the message
     */
    public Message getMessage() {
        return m_message;
    }
    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + StringConstants.DOT
                + Messages.TimeoutCalled);
    }

}

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
package org.eclipse.jubula.autagent.monitoring;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.BuildMonitoringReportMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.SendMonitoringReportMessage;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.utils.IsAliveThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This command is calling the "buildMonitoringReport" method specified by the
 * IMonitoring interface.
 * @author BREDEX GmbH
 * @created 13.09.2010
 */
public class BuildMonitoringReportCommand implements ICommand {
    
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(BuildMonitoringReportCommand.class);  
   
    /** the incoming message */
    private BuildMonitoringReportMessage m_message;      
    
    /** timeout for ServerSocket accept() method in milliseconds. 
     *  After this amount of time the Thread will be closed  */
    private int m_timeout = 30000;
    /** 
     * {@inheritDoc}
     */
    public Message execute() {
        //create new thread for the streaming ServerSocket
        new IsAliveThread(new Runnable() {
            
            public void run() {
                
                IMonitoring monitoringAgent = 
                        MonitoringDataStore.getInstance().getMonitoringAgent(
                                m_message.getAutId());

                ServerSocket serverSocket = null;
                Socket reportStreamSocket = null;
                OutputStream reportOutputStream = null;
                SendMonitoringReportMessage message = 
                        new SendMonitoringReportMessage();
                try {
                    serverSocket = new ServerSocket(0);
                    message.setPort(serverSocket.getLocalPort());
                    //sending a message to the client and waiting for client connection
                    AutStarter.getInstance().getCommunicator().send(message);
                    serverSocket.setSoTimeout(m_timeout);
                    reportStreamSocket = serverSocket.accept();
                    reportOutputStream = reportStreamSocket.getOutputStream();
                    //building monitoring report
                    monitoringAgent.writeMonitoringReport(reportOutputStream);
                } catch (SocketTimeoutException ste) {
                    LOG.error("Connection timeout while waiting for client to access monitoring report stream.", ste); //$NON-NLS-1$
                } catch (IOException ioe) {
                    LOG.error("I/O error occurred while streaming monitoring report.", ioe); //$NON-NLS-1$
                } catch (CommunicationException ce) {
                    LOG.error("Failed to send " + message.getClass().getName(), ce); //$NON-NLS-1$
                } finally {
                    if (reportOutputStream != null) {
                        try {
                            reportOutputStream.close();
                        } catch (IOException e) {
                            LOG.error("Error while closing monitoring report output stream.", e); //$NON-NLS-1$
                        }
                    }
                    if (reportStreamSocket != null) {
                        try {
                            reportStreamSocket.close();
                        } catch (IOException e) {
                            LOG.error("Error while closing monitoring report stream socket.", e); //$NON-NLS-1$
                        }
                    }
                    if (serverSocket != null) {
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            LOG.error("Error while closing monitoring report server socket.", e); //$NON-NLS-1$
                        }
                    }
                }
            }
        }, "Monitoring Report Streamer").start(); //$NON-NLS-1$

        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
       
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (BuildMonitoringReportMessage)message;

    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}

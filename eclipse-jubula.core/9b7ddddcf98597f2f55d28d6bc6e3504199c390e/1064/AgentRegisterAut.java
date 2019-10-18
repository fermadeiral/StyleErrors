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
package org.eclipse.jubula.rc.common.registration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.connection.ConnectionState;
import org.eclipse.jubula.communication.internal.connection.DefaultSocket;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.tools.internal.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Registers an AUT ID with an Aut Agent.
 *
 * @author BREDEX GmbH
 * @created Dec 11, 2009
 */
public class AgentRegisterAut implements IRegisterAut {
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(AgentRegisterAut.class);

    /** the address of the Aut Agent with which to register */
    private InetSocketAddress m_agentAddr;
    
    /** the ID of the AUT to register */
    private AutIdentifier m_autIdentifier;
    
    /** connection to the Aut Agent */
    private Socket m_agentConn;

    /**
     * Constructor
     * 
     * @param agentAddr The address of the Aut Agent with which to register.
     * @param autIdentifier The ID of the AUT to register.
     */
    public AgentRegisterAut(InetSocketAddress agentAddr, 
            AutIdentifier autIdentifier) {
        m_agentAddr = agentAddr;
        m_autIdentifier = autIdentifier;
        m_agentConn = null;
    }

    /**
     * {@inheritDoc}
     * @throws JBVersionException 
     */
    public void register() throws IOException, JBVersionException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Registering AUT '"  //$NON-NLS-1$
                    + m_autIdentifier.getExecutableName() 
                    + "' with agent at "  //$NON-NLS-1$
                    + m_agentAddr.getHostName() + ":" + m_agentAddr.getPort()); //$NON-NLS-1$
        }
        m_agentConn = new DefaultSocket(
            m_agentAddr.getAddress(), 
            m_agentAddr.getPort());

        long waitForServer = Communicator.DEFAULT_CONNECTING_TIMEOUT * 1000;
        long waitTime = 0;
        boolean success = false;
        InputStream inputStream = m_agentConn.getInputStream();
        final BufferedReader inputReader = new BufferedReader(
                new InputStreamReader(inputStream));
        PrintStream outputStream = 
            new PrintStream(m_agentConn.getOutputStream());
        while (!success && (waitTime <= waitForServer)) {
            if (inputStream.available() > 0) {
                String line = inputReader.readLine();
                if (line != null) {
                    line = line.substring(0, 
                            line.indexOf(ConnectionState.SEPARATOR));
                }
                success = true;
                if (ConnectionState.CLIENT_TYPE_REQUEST.equals(line)) {
                    final String response = 
                        ConnectionState.CLIENT_TYPE_AUT;

                    outputStream.println(response);
                    outputStream.flush();
                } else {
                    if (LOG.isWarnEnabled()) {
                        StringBuffer errBuf = new StringBuffer();
                        errBuf.append("Received invalid request from server. Expected '") //$NON-NLS-1$
                            .append(ConnectionState.CLIENT_TYPE_REQUEST)
                            .append("' but received '").append(line) //$NON-NLS-1$
                            .append("'."); //$NON-NLS-1$
                        LOG.warn(errBuf.toString());
                    }
                }
            } else {
                waitTime += TimeUtil.delay(TimingConstantsServer
                    .POLLING_DELAY_AUT_REGISTER);
            }
        }

        outputStream.println(m_autIdentifier.encode());
        outputStream.flush();

        // wait for communicator host name and port  
        String communicatorHostName = inputReader.readLine();
        String communicatorPort = inputReader.readLine();
        
        try {
            AUTServer.getInstance().initAutAgentCommunicator(
                    InetAddress.getByName(communicatorHostName), 
                    Integer.parseInt(communicatorPort));
        } catch (NumberFormatException nfe) {
            LOG.error("Error occurred while connecting to AUT Agent.", nfe); //$NON-NLS-1$
        } catch (SecurityException se) {
            LOG.error("Error occurred while connecting to AUT Agent.", se); //$NON-NLS-1$
        }

    }

}

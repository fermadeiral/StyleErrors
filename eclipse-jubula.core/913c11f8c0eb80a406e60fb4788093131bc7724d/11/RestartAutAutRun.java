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
package org.eclipse.jubula.autagent.common.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.eclipse.jubula.communication.internal.connection.RestartAutProtocol;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Restarts an AUT that was started with autrun.
 *
 * @author BREDEX GmbH
 * @created Mar 26, 2010
 */
public class RestartAutAutRun implements IRestartAutHandler {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(RestartAutAutRun.class);
    
    /** the ID of the started AUT */
    private AutIdentifier m_autId;

    /** the socket used for communicating with autrun */
    private Socket m_autrunSocket;
    
    /** reader used for communicating with autrun */
    private BufferedReader m_socketReader;

    /** the class which was used to start the AUT **/
    private String m_autStartClass;
    
    /**
     * Constructor
     * 
     * @param autId The ID of the started AUT.
     * @param socket The socket used for communicating with autrun.
     * @param reader Reader for the given socket.
     * @param startClass the class which was used to start the AUT
     */
    public RestartAutAutRun(AutIdentifier autId, Socket socket,
            BufferedReader reader, String startClass) {

        m_autId = autId;
        m_autrunSocket = socket;
        m_socketReader = reader;
        m_autStartClass = startClass;
    }

    /**
     * {@inheritDoc}
     */
    public void restartAut(AutAgent agent, int timeout) {
        try {
            PrintWriter writer = new PrintWriter(
                    m_autrunSocket.getOutputStream(), true);

            writer.println(RestartAutProtocol.REQ_PREPARE_FOR_RESTART);
            m_socketReader.readLine();

            agent.stopAut(m_autId, timeout);
            
            writer.println(RestartAutProtocol.REQ_RESTART);
            
        } catch (IOException e) {
            LOG.error("Error occurred while restarting AUT.", e); //$NON-NLS-1$
        }
        
    }

    @Override
    public String getAUTStartClass() {
        return m_autStartClass;
    }

}

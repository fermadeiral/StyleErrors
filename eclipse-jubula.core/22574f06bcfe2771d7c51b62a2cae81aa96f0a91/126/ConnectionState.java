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
package org.eclipse.jubula.communication.internal.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.jubula.tools.internal.version.IVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Constants for the state of a connection
 *
 * @author BREDEX GmbH
 * @created 20.09.2004
 */
public class ConnectionState {
    /** Separator of messages*/
    public static final String SEPARATOR = "/"; //$NON-NLS-1$
    
    /** The index of the state */
    public static final int STATE_INDEX = 0;
    
    /** The index of the version number */
    public static final int VERSION_INDEX = 1;
    
    /** unconnected, e.g. server did not send a state */
    public static final int UNKNOWN = -1;
    
    /** server is ready */
    public static final int SERVER_OK = 0;
    
    /** server is busy */
    public static final int SERVER_BUSY = 1;
    
    /** Version of client and AutStarter are different */
    public static final int VERSION_ERROR = 2;

    /** the string used by servers to determine the type of a client */
    public static final String CLIENT_TYPE_REQUEST = "ClientTypeRequest"; //$NON-NLS-1$

    /** client type that requires exclusive access to the server */
    public static final String CLIENT_TYPE_SINGLE = "ClientType.Exclusive"; //$NON-NLS-1$

    /** client type that represents an AUT wishing to register with an AUT Agent */
    public static final String CLIENT_TYPE_AUT = "ClientType.Aut"; //$NON-NLS-1$

    /** client type that represents an instance of "autrun" */
    public static final String CLIENT_TYPE_AUTRUN = "ClientType.autrun"; //$NON-NLS-1$

    /** client type that represents an external request to shut down the server */
    public static final String CLIENT_TYPE_COMMAND_SHUTDOWN = "ClientType.Command.ShutDown"; //$NON-NLS-1$
    
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(ConnectionState.class);
    
    /**
     * Utility constructor
     */
    private ConnectionState() {
        // Utility constructor
    }
    
    /**
     * Parses the state out of the given message
     * @param message the message to parse
     * @return the state or UNKNOWN-state if any error occurred
     */
    public static int parseState(String message) {
        StringTokenizer tok = new StringTokenizer(message, SEPARATOR);
        try {
            if (tok.hasMoreTokens()) {
                String stateStr = tok.nextToken();
                return Integer.parseInt(stateStr);
            }
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
        return UNKNOWN;
    }
    
    /**
     * Parses the version number out of the given message
     * @param message the message to parse
     * @return the version number or -1 if any error occurred
     */
    public static int parseVersion(String message) {
        StringTokenizer tok = new StringTokenizer(message, SEPARATOR);
        final int index = VERSION_INDEX + 1;
        if (tok.countTokens() < index) {
            return -1;
        }
        String versionStr = StringConstants.EMPTY;
        for (int i = 0; i < index; i++) {
            if (tok.hasMoreTokens()) {
                versionStr = tok.nextToken();                
            }
        }
        try {
            return Integer.parseInt(versionStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Waits for a "client type" request from the server and responds to that
     * request.
     * 
     * @param waitForServer
     *            The maximum amount of time (in milliseconds) to wait for the
     *            request from the server.
     * @param inputReader
     *            Reader for the input stream from which the request is
     *            expected.
     * @param inputStream
     *            The input stream from which the request is expected.
     * @param outputStream
     *            The printer to use for sending the response.
     * @param response
     *            The response to send, if the expected request is received.
     * @return <code>true</code> if the expected request is received in good.
     *         Otherwise <code>false</code>.
     * @throws JBVersionException
     *             in case of a version error between client and server.
     * @throws IOException
     *             in case of an I/O error.
     */
    public static boolean respondToTypeRequest(long waitForServer, 
            BufferedReader inputReader, InputStream inputStream, 
            PrintStream outputStream, String response) 
        throws JBVersionException, IOException {
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("waiting for server's 'client type request' with timeout: " //$NON-NLS-1$ 
                    + String.valueOf(waitForServer));
        }

        long waitTime = 0;
        boolean success = false;
        while (!success && (waitTime <= waitForServer)) {
            if (inputStream.available() > 0) {
                String clientVersionLine = inputReader.readLine();
                success = true;
                final int protocolVersion = Integer.parseInt(String.valueOf(
                        IVersion.JB_PROTOCOL_MAJOR_VERSION.intValue())); 
                int clientVersion = ConnectionState
                        .parseVersion(clientVersionLine);
                if (clientVersion != protocolVersion) {
                    throw new JBVersionException(
                            "Version error between Client and Server! Client version: " //$NON-NLS-1$
                                    + clientVersion + " Server Version: " //$NON-NLS-1$
                                    + protocolVersion,
                            MessageIDs.E_VERSION_ERROR);
                }

                if (clientVersionLine != null) {
                    clientVersionLine = clientVersionLine.substring(0,
                            clientVersionLine
                                    .indexOf(ConnectionState.SEPARATOR));
                }

                if (ConnectionState.CLIENT_TYPE_REQUEST
                        .equals(clientVersionLine)) {
                    LOG.debug("sending response: " + response); //$NON-NLS-1$
                    outputStream.println(response);
                    outputStream.flush();
                } else {
                    if (LOG.isWarnEnabled()) {
                        StringBuffer errBuf = new StringBuffer();
                        errBuf.append("Received invalid request from server. Expected '") //$NON-NLS-1$
                            .append(ConnectionState.CLIENT_TYPE_REQUEST)
                            .append("' but received '").append(clientVersionLine) //$NON-NLS-1$
                            .append("'."); //$NON-NLS-1$
                        LOG.warn(errBuf.toString());
                    }
                }
            } else {
                /*
                 * Do nothing in case of interrupted exception. We may end up
                 * waiting a bit longer for a response, but that shouldn't be a
                 * problem.
                 */
                waitTime += TimeUtil.delay(TimingConstantsServer
                    .POLLING_DELAY_AUT_REGISTER);
            }
        }

        if (!success) {
            LOG.error("Did not receive expected request from server."); //$NON-NLS-1$
        }
        
        return success;
    }
}

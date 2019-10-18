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
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.jubula.tools.internal.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.jubula.tools.internal.version.IVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A ServerSocket implementing the protocol for establishing a connection. <br>
 * 
 * The method send() sends a state code of the server. It's called immediately
 * after accept() by the AcceptingThread of the communicator.
 * 
 * @author BREDEX GmbH
 * @created 20.09.2004
 */
public class DefaultServerSocket extends ServerSocket {
    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(DefaultServerSocket.class);
    
    /**
     * @param port
     *            the port number to use, zero means any free port
     * @throws java.io.IOException
     *             from constructor of super class ServerSocket
     */
    public DefaultServerSocket(int port) throws IOException {
        super(port);
    }
    
    /**
     * Write the state to the socket. The state is converted to a string and terminated by a new line
     * character. 
     * @param socket
     *            the socket created by accept.
     * @param state
     *            the state to send see ConnectionState
     * @throws IOException
     *             from getting (and writing to) the outputstream of the given
     *             socket
     */
    public static void send(Socket socket, int state) throws IOException {
        PrintStream outputStream = new PrintStream(socket.getOutputStream());
        final String status = state 
            + ConnectionState.SEPARATOR 
            + IVersion.JB_PROTOCOL_MAJOR_VERSION;
        if (log.isDebugEnabled()) {
            log.debug("sending state: " + String.valueOf(status)); //$NON-NLS-1$
        }
        outputStream.println(String.valueOf(status));
        outputStream.flush();
    }

    /**
     * Sends a request to the client using the given socket and returns the 
     * response received via the given reader.
     * 
     * @param socket The socket on which the communication will take place.
     * @param reader Reader for the given socket's input stream.
     * @param inputStream the input stream of the given socket
     * @param timeout Maximum time to wait (in milliseconds) for a response.
     * @return the response received from the client, or <code>null</code> if a 
     *         timeout occurs.
     * @throws IOException
     *             from getting (and writing to) the outputstream of the given
     *             socket
     */
    public static String requestClientType(Socket socket, 
        BufferedReader reader, InputStream inputStream, long timeout) 
        throws IOException {
        PrintStream outputStream = new PrintStream(socket.getOutputStream());
        final String request = ConnectionState.CLIENT_TYPE_REQUEST 
            + ConnectionState.SEPARATOR 
            + IVersion.JB_PROTOCOL_MAJOR_VERSION;

        log.debug("sending request: " + request); //$NON-NLS-1$

        outputStream.println(request);
        outputStream.flush();

        if (log.isDebugEnabled()) {
            log.debug("waiting for client type response using timeout: " //$NON-NLS-1$ 
                    + String.valueOf(timeout));
        }

        long waitTime = 0;
        while (waitTime <= timeout) {
            if (inputStream.available() > 0) {
                return reader.readLine();

            }
            waitTime += TimeUtil.delay(TimingConstantsServer
                .POLLING_DELAY_AUT_REGISTER);
        }

        log.debug("no client type response received from client"); //$NON-NLS-1$
        return null;
    }
    
}

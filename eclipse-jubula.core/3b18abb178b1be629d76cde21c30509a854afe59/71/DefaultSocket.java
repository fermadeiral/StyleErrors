/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.communication.internal.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


/**
 * @author BREDEX GmbH
 */
public class DefaultSocket extends Socket {
    /**
     * Constructor for default DefaultSocket
     * 
     * Creates a stream socket and connects it to the specified port
     * number at the specified IP address.
     * <p>
     * If the application has specified a socket factory, that factory's
     * <code>createSocketImpl</code> method is called to create the
     * actual socket implementation. Otherwise a "plain" socket is created.
     * <p>
     * If there is a security manager, its
     * <code>checkConnect</code> method is called
     * with the host address and <code>port</code> 
     * as its arguments. This could result in a SecurityException.
     * 
     * @param      address   the IP address.
     * @param      port      the port number.
     * @exception  IOException  if an I/O error occurs when creating the socket.
     */
    public DefaultSocket(InetAddress address, int port) throws IOException {
        super(address, port);
        setSoLinger(true, 5);
    }
}

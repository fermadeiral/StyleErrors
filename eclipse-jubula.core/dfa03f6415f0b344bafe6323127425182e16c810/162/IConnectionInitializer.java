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
package org.eclipse.jubula.communication.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

/**
 * Responsible for initializing a socket connection.
 *
 * @author BREDEX GmbH
 * @created Jan 28, 2010
 */
public interface IConnectionInitializer {
   
    /**
     * Initializes the connection for the given socket. May use data read from
     * the given reader.
     * 
     * @param socket The socket for which to initialize the connection.
     * @param reader Reader for the given socket's input stream.
     * @throws IOException If an error occurs during initialization.
     */
    public void initConnection(Socket socket, BufferedReader reader) 
        throws IOException;

}
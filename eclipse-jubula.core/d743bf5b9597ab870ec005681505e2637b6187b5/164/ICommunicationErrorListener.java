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
package org.eclipse.jubula.communication.internal.listener;

import java.net.InetAddress;

import org.eclipse.jubula.communication.internal.message.Message;


/**
 * The interface a communicator uses to notify interested listener for
 * communication errors. <br>
 * These are: <br>
 * <ul>
 * <li>connectionGained()</li>
 * <li>connectingFailed()</li>
 * <li>acceptingFailed()</li>
 * <li>shutDown()</li>
 * <li>sendFailed(Message)</li>
 * </ul>
 * <p>
 * @author BREDEX GmbH
 * @created 23.07.2004
 */
public interface ICommunicationErrorListener {

    /**
     * This method will be called, when connection is gained. 
     * @param inetAddress the remote host
     * @param port the remote port
     */
    public void connectionGained(InetAddress inetAddress, int port);
    
    /**
     * This method will be called, when a communication error occurs, e.g the
     * connection is closed.
     */
    public void shutDown();

    /**
     * This method will be called, if sending the message <code>message</code>
     * failed. Do not modify the message!
     * 
     * @param message
     *            the message which should be send
     */
    public void sendFailed(Message message);
    
    /**
     * This method will be called, when an error occurs during accepting on 
     * <code>port</code>
     * @param port the port, that was used
     */
    public void acceptingFailed(int port);
    
    /**
     * This method will be called when conneting to <code>inetAddress</code> on port <code>port</code> failed.
     * @param inetAddress the remote address to which connecting failed
     * @param port the port
     */
    public void connectingFailed(InetAddress inetAddress, int port);
}

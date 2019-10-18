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

import org.eclipse.jubula.communication.internal.message.MessageHeader;

/**
 * The interface a connection uses to notify listener for connection errors.
 * The communicator uses this interface.
 * 
 * <p>
 * These are:
 * <ul>
 * <li>shutDown()</li>
 * <li>sendFailed(header, message)</li>
 * </ul>
 * 
 * @author BREDEX GmbH
 * @created 07.07.2004
 */
public interface IErrorHandler {

    /**
     * This method will be called, when an io error occurs, e.g the connection
     * is closed.
     */
    public void shutDown();

    /**
     * This method will be called, if sending the message <code>message</code>
     * with header <code>header</code> fails.
     * 
     * @param header -
     *            the header for the message
     * @param message -
     *            the message which should be send
     */
    public void sendFailed(MessageHeader header, String message);
}

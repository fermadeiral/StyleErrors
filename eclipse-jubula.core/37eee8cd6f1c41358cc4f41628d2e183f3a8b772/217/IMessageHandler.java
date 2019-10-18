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
 * The interface a connection uses to notify listener for incoming messages.
 * The communicator uses this interface.
 * 
 * <p>
 * These are:
 * <ul>
 * <li>received(header, message)</li>
 * </ul>
 * 
 * @author BREDEX GmbH
 * @created 07.07.2004
 */
public interface IMessageHandler {
    /**
     * This method will be called, when a message was received. Do not modify
     * the parameter!
     * 
     * @param header -
     *            the received message header
     * @param message -
     *            the received message as a string
     * {@inheritDoc}
     */
    public void received(MessageHeader header, String message);
}

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

import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * Exception to be thrown if the number of bytes announced for instance
 * in the precedent message header before cannot be 
 * read from the underlying socket due to a premature end of file.
 * Resembles "Cannot Receive" in SUN RPC ;-) 
 *
 * @author BREDEX GmbH
 * @created Oct 12, 2006
 */
public class UnexpectedEofException extends CommunicationException {
    
    /** 
     * public constructor
     * @param message the detailed message
     */
    public UnexpectedEofException(String message) {
        super(message, MessageIDs.E_UNEXPECTED_EXCEPTION);
    }
}
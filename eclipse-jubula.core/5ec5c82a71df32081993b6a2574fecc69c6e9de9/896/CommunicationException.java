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
package org.eclipse.jubula.tools.internal.exception;

/**
 * The exception class for exceptions concerning the client - server communication.
 *
 * @author BREDEX GmbH
 * @created 02.09.2004
 */
public class CommunicationException extends JBException {

    /** 
     * public constructor
     * @param message the detailed message
     * @param cause the exception which caused this problem
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public CommunicationException(String message, Throwable cause, Integer id) {
        super(message, cause, id);
    }

    /** 
     * public constructor
     * @param message the detailed message
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public CommunicationException(String message, Integer id) {
        super(message, id);
    }
}
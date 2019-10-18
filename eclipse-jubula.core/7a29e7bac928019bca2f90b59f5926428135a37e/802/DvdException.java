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
package org.eclipse.jubula.examples.aut.dvdtool.exception;

/**
 * The base class for all exceptions.
 *
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class DvdException extends Exception {
    
    /** the nested exception */
    private Exception m_exception = null;
    
    /**
     * public constructor
     * @param message a message describing the exception
     * @param exception a nested exception.
     */
    public DvdException(String message, Exception exception) {
        super(message);
        
        m_exception = exception;
    }
    
    /**
     * @return Returns the exception.
     */
    public Exception getException() {
        return m_exception;
    }
}

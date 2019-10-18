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
 *  The base class for all exceptions in Jubula.
 *
 * @author BREDEX GmbH
 * @created 20.07.2004
 */
public class JBException extends Exception {
    
    /** the error message id */
    private Integer m_id;

    /**
     * @param message The detailed message for this exception.
     * @param id An ErrorMessage.ID.
     */
    public JBException(String message, Integer id) {
        super(message);
        m_id = id;
    }
    /**
     * @param message The detailed message for this exception.
     * @param cause The throwable object.
     * @param id An ErrorMessage.ID.
     */
    public JBException(String message, Throwable cause, Integer id) {
        super(message, cause);
        m_id = id;
    }
    /**
     * @return Returns the error message id.
     */
    public Integer getErrorId() {
        return m_id;
    }
}
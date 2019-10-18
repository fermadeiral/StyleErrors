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
 * This class represents a Jubula runtime exception. As it exends
 * <code>RuntimeException</code>, a method is not required to declare in its
 * throws clause. The class defines its own original exception (cause) as GuiDancer
 * also runs in a JDK 1.3 environment.
 * 
 * @author BREDEX GmbH
 * @created 05.04.2005
 */
public abstract class JBRuntimeException extends RuntimeException {
    
    /** the error message id */
    private Integer m_id;
    
    /** The original exception. */
    private Throwable m_causedBy;

    /**
     * @param message The message.
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public JBRuntimeException(String message, Integer id) {
        super(message);
        m_id = id;
    }
    
    /**
     * @param message The message.
     * @param cause The original exception.
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public JBRuntimeException(String message, Throwable cause, Integer id) {
        this(message, id);
        m_causedBy = cause;
    }
    
    /**
     * @param cause The original exception.
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public JBRuntimeException(Throwable cause, Integer id) {
        this(cause.getMessage(), id);
        m_causedBy = cause;
    }
    
    /**
     * Gets the cause of this exception.
     * @return The cause or <code>null</code> if the cause is nonexistent.
     */
    public Throwable getCausedBy() {
        return m_causedBy;
    }
    
    /**
     * @return Returns the error message id.
     */
    public Integer getErrorId() {
        return m_id;
    }
}
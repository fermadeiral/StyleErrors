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
 * this exception signals, that Jubula is no longer fit for work
 * 
 * @author BREDEX GmbH
 * @created 19.08.2005
 */
public class JBFatalAbortException extends JBFatalException {

    /**
     * @param message detailed message for exception
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public JBFatalAbortException(String message, Integer id) {
        super(message, id);
    }

    /**
     * @param message detailed message for exception
     * @param cause reason
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public JBFatalAbortException(String message, Throwable cause, Integer id) {
        super(message, cause, id);
    }

    /**
     * @param cause reason
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public JBFatalAbortException(Throwable cause, Integer id) {
        super(cause, id);
    }
}
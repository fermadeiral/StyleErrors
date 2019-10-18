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
package org.eclipse.jubula.client.core.persistence;

import org.eclipse.jubula.tools.internal.exception.JBException;

/**
 * @author BREDEX GmbH
 * @created 19.04.2005
 */
public class PersistorException extends JBException {

    /**
     * @param message message
     * @param id id
     */
    public PersistorException(String message, Integer id) {
        super(message, id);
    }

    /**
     * @param message message
     * @param cause cause
     * @param id id
     */
    public PersistorException(String message, Throwable cause, Integer id) {
        super(message, cause, id);
    }
}
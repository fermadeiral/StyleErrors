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
package org.eclipse.jubula.rc.common.exception;

import org.eclipse.jubula.tools.internal.exception.InvalidDataException;

/**
 * The exception thrown if a component could not be found in the AUT for a component identifier.
 * @author BREDEX GmbH
 * @created 02.01.2007
 */
public class ComponentNotFoundException extends InvalidDataException {
    
    /**
     * public constructor
     * @param message the detailed message
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public ComponentNotFoundException(String message,
        Integer id) {
        super(message, id);
    }
}
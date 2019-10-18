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
package org.eclipse.jubula.client.core.model;

import org.eclipse.jubula.tools.internal.exception.InvalidDataException;

/**
 * The exception thrown if the object map was querried for a component
 * identifier, but the given logic component is not managed by this map
 * 
 * @author BREDEX GmbH
 * @created 14.10.2004
 *
 */
public class LogicComponentNotManagedException extends
        InvalidDataException {

    /**
     * public constructor
     * @param message the detailied message
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public LogicComponentNotManagedException(String message, 
        Integer id) {
        
        super(message, id);
    }
}
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
 * for description of exception states concerning testexecution with incomplete 
 * data like missing testdata, unresolved references etc.
 *
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class IncompleteDataException extends JBException {
    /** 
     * public constructor
     * @param message the detailed message
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public IncompleteDataException(String message, Integer id) {
        super(message, id);
    }
}
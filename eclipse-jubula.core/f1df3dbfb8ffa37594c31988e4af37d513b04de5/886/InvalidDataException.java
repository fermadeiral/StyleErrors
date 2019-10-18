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
 * The exception class for exceptions concerning invalid / unexpected data.
 *
 * @author BREDEX GmbH
 * @created 02.09.2004
 */
public class InvalidDataException extends JBException {

    /** 
     * public constructor
     * @param message the detailed message
     * @param id An ErrorMessage.ID.
     */
    public InvalidDataException(String message, Integer id) {
        super(message, id);
    }

}

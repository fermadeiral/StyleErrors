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

/**
 * 
 * exception, if an external project (not the current) is to delete, but it
 * was already deleted by another user
 * @author BREDEX GmbH
 * @created 30.01.2006
 *
 */
public class PMExtProjDeletedException extends PMException {
    
    /**
     * @param message detailed message
     * @param id An ErrorMessage.ID.
     */
    public PMExtProjDeletedException(String message, Integer id) {
        super(message, id);
    }

}

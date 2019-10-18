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
 * This exception indicates that a Component Name could not be created because 
 * a Component Name with the same name already exists.
 *
 * @author BREDEX GmbH
 * @created Aug 20, 2010
 */
public class ComponentNameExistsException extends JBException {

    /**
     * Error details
     */
    private String[] m_errorMessageParams = null;
    
    /**
     * 
     * @param message The detailed message for this exception.
     * @param id An ErrorMessage.ID.
     * @param details the details of the ErrorMessage defined in ErrorMessage.ID
     */
    public ComponentNameExistsException(String message, Integer id, 
            String[] details) {
        super(message, id);
        m_errorMessageParams = details;
    }

    /**
     * 
     * @return the error details
     */
    public String[] getErrorMessageParams() {
        return m_errorMessageParams;
    }

}

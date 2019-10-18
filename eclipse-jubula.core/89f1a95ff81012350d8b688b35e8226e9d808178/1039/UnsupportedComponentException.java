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

import org.eclipse.jubula.tools.internal.exception.JBException;

/**
 * Exception thrown when an implementation class is requested, which was not
 * configured via <code>setConfiguration</code>.
 * 
 * @author BREDEX GmbH
 * @created 11.05.2006
 */
public class UnsupportedComponentException extends JBException {

    /**
     * public constructor
     * @param message the detailed message
     * @param id An ErrorMessage.ID.
    * {@inheritDoc}
     */
    public UnsupportedComponentException(String message, 
        Integer id) {
        
        super(message, id);
    }
}
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
 * @author BREDEX GmbH
 * @created 30.01.2006
 */
public class ProjectDeletedException extends JBException {

    /**
     * @param message The detailed message for this exception.
     * @param id id for ErrorMessage
     */
    public ProjectDeletedException(String message, Integer id) {
        super(message, id);
    }    
}

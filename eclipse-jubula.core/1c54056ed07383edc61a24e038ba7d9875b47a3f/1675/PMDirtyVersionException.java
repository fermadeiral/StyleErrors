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

import org.eclipse.jubula.client.core.model.IPersistentObject;

/**
 * 
 * exception to signal a version conflict between actual java object and database object
 * @author BREDEX GmbH
 * @created 02.08.2005
 *
 */
public class PMDirtyVersionException extends PMException {
    
    
    /**
     * <code>m_dirtyObject</code> dirty object
     */
    private IPersistentObject m_dirtyObject = null;

    /**
     * @param message detailed message
     * @param id An ErrorMessage.ID.
     * @param po dirty object
     * {@inheritDoc}
     */
    public PMDirtyVersionException(IPersistentObject po, 
        String message, Integer id) {
        this(message, id);
        m_dirtyObject = po;              
    }
    
    /**
     * @param message detailed message
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public PMDirtyVersionException(String message, Integer id) {
        super(message, id);
    }

    /**
     * @return Returns the dirtyObject.
     */
    public IPersistentObject getDirtyObject() {
        return m_dirtyObject;
    }
}

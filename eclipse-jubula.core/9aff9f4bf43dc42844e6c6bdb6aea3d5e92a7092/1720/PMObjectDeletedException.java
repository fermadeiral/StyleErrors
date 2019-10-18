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
 * @author BREDEX GmbH
 * @created 27.01.2006
 *
 */
public class PMObjectDeletedException extends PMException {
    
    /**
     * <code>m_deletedObject</code> deleted object
     */
    private IPersistentObject m_deletedObject = null;

    /**
     * @param message detailed message
     * @param id An ErrorMessage.ID.
     * @param po deleted object
     * {@inheritDoc}
     */
    public PMObjectDeletedException(IPersistentObject po, 
        String message, Integer id) {
        this(message, id);
        m_deletedObject = po;              
    }
    
    /**
     * @param message detailed message
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public PMObjectDeletedException(String message, Integer id) {
        super(message, id);
    }

    /**
     * @return Returns the deletedObject.
     */
    public IPersistentObject getDeletedObject() {
        return m_deletedObject;
    }

}

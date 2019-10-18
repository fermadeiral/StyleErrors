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
 * @created 19.08.2005
 */
public class PMAlreadyLockedException extends PMException {

    /**
     * <code>m_lockedObject</code> object, which causes the lock exception
     */
    private IPersistentObject m_lockedObject;

    /**
     * @param po already locked object
     * @param message for exception
     * @param id id for error message
     * {@inheritDoc}
     */
    public PMAlreadyLockedException(IPersistentObject po, 
        String message, Integer id) {
        super(message, id);
        m_lockedObject = po;
    }

    /**
     * @return Returns the lockedObject.
     */
    public IPersistentObject getLockedObject() {
        return m_lockedObject;
    }
}
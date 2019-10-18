/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.persistence;

import javax.persistence.EntityManager;

/**
 * Allows retrieval of an EntityManager.
 */
public interface IEntityManagerProvider {

    /**
     * 
     * @return the receiver's current EntityManager. May be <code>null</code>.
     *         May be closed. Clients may not close or dispose the 
     *         EntityManager.
     */
    public EntityManager getEntityManager();
    
}

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
package org.eclipse.jubula.client.core.model;

import java.util.List;

import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;


/**
 * @author BREDEX GmbH
 * @created 20.12.2005
 */
public interface ICompIdentifierPO extends IPersistentObject,
    IComponentIdentifier {
    /**
     * @return Returns the neighbours.
     */
    public abstract List<String> getNeighbours();

    /**
     * @param neighbours The neighbours to set.
     */
    public abstract void setNeighbours(List<String> neighbours);

    /**
     * @return Returns the hierarchyNames.
     */
    public abstract List<String> getHierarchyNames();

    /**
     * @param hierarchyNames
     *            The hierarchyNames to set. if null, the list will be cleared.
     */
    public abstract void setHierarchyNames(List<String> hierarchyNames);

    /**
     * @return Clone of object
     */
    public abstract ICompIdentifierPO makePoClone();

    /** (non-Javadoc)
     * {@inheritDoc}
     */
    public abstract void setId(Long id);
    
    /**
     * get the po profile
     * @return the po profile
     */
    public IObjectMappingProfilePO getProfilePO();
}
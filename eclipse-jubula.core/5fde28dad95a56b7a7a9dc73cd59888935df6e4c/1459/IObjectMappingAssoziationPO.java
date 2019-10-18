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

import java.util.Set;

import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;

/**
 * @author BREDEX GmbH
 * @created 20.12.2005
 */
public interface IObjectMappingAssoziationPO 
        extends IPersistentObject, IComponentNameReuser {

    /**
     * @return Returns the type.
     */
    public abstract String getType();

    /**
     * @param type The type to set.
     */
    public abstract void setType(String type);

    /**
     * @param logicalNames list of GUIDs of ComponentNamePOs to set.
     */
    public abstract void setLogicalNames(Set<String> logicalNames);

    /**
     * adds a logical Name GUID
     * @param name a GUID of a ComponentNamePO of a logical name
     */
    public abstract void addLogicalName(String name);

    /**
     * removes a logical name GUID
     * @param name a GUID of a ComponentNamePO of a logical name
     */
    public abstract void removeLogicalName(String name);

    /**
     * @return Returns the technicalName.
     */
    public abstract ICompIdentifierPO getTechnicalName();

    /**
     * @return Returns the list of GUIDs of ComponentNamePOs.
     */
    public abstract Set<String> getLogicalNames();

    /**
     * @param technicalName The technicalName to set.
     */
    public abstract void setTechnicalName(ICompIdentifierPO technicalName);

    /**
     * @return Returns the category.
     */
    public abstract IObjectMappingCategoryPO getCategory();

    /**
     * Note: Due to the way this class is mapped with Persistence (JPA / EclipseLink), this method
     * should only be called by <code>ObjectMappingCategoryPO</code>. Other 
     * callers should use <code>IObjectMappingCategory.addAssociation()</code> 
     * instead.
     * 
     * @param category The category to set.
     */
    public abstract void setCategory(IObjectMappingCategoryPO category);

    /**
     * @return string representation of this object
     */
    public abstract String getName();

    /**
     * @return the top-level category to which this association belongs.
     *         This is <code>null</code> if the association does not belong to
     *         a category.
     */
    public abstract IObjectMappingCategoryPO getSection();

    /**
     * @return the compIdentifier of this technical component - only avaiable
     *         shortly (until the next editor save) after this technical component has been collected during
     *         object mapping / collecting mode.
     */
    public IComponentIdentifier getCompIdentifier();
    
    /**
     * @param compId
     *            set the compIdentifier of this technical component - only used
     *            shortly after this technical component has been collected
     *            during object mapping / collecting mode.
     */
    public void setCompIdentifier(IComponentIdentifier compId);
}
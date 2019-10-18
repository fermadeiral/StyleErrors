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
public interface IObjectMappingPO extends ITimestampPO {

    /** name of the "mappings" property */
    public static final String PROP_MAPPINGS = "mappings"; //$NON-NLS-1$

    /** name of the "profile" property */
    public static final String PROP_PROFILE = "profile"; //$NON-NLS-1$ 
    
    /**
     * invis category not shown in Editor
     */
    static final String INVISIBLECATEGORYNAME = "invisible"; //$NON-NLS-1$

    /**
     * tries to assign a logical(perhaps existing) to a technical(perhaps existing)
     * 
     * @param logic         logical name
     * @param technical     technical name
     * @return          ObjectMappingAssoziationPO
     */
    public abstract IObjectMappingAssoziationPO addObjectMappingAssoziation(
        String logic, IComponentIdentifier technical);
    /**
     * removes an association from the cache
     * @param assoc the association to remove
     */
    public void removeAssociationFromCache(IObjectMappingAssoziationPO assoc);

    /**
     * Creates a new technical Name, unassigned
     *  
     * @param tech     
     *      ComponentIdentifier
     * @param aut
     *      AUTMainPO
     * @return          ObjectMappingAssoziationPO
     */
    public abstract IObjectMappingAssoziationPO addTechnicalName(
        IComponentIdentifier tech, IAUTMainPO aut);


    /**
     * tries to assign a logical(perhaps existing) to a technical(perhaps existing)
     * 
     * @param logic         logical name
     * @param technical     technical name
     * @return          ObjectMappingAssoziationPO
     */
    public abstract IObjectMappingAssoziationPO assignLogicalToTechnicalName(
        String logic, IComponentIdentifier technical);

    /**
     * Check if a technical name exists
     * 
     * @param technical technical name
     * @return          boolean
     */
    public abstract boolean existTechnicalName(IComponentIdentifier technical);

    /**
     * gives back the number of technical names
     * @return int
     */
    public abstract int getTechnicalNamesSize();

    /**
     * returns the technicalName to a logical name
     * @param logical       String
     * @return              String
     * @throws LogicComponentNotManagedException error
     */
    public abstract IComponentIdentifier getTechnicalName(String logical)
        throws LogicComponentNotManagedException;

    /**
     * 
     * @return the profile being used for this object map.
     */
    public abstract IObjectMappingProfilePO getProfile();
    
    /**
     * 
     * @param profile the profile that this object map will use.
     */
    public abstract void setProfile(IObjectMappingProfilePO profile);

    /**
     * Returns all Associations belonging to this Object Mapping.
     * @return      Set
     */
    public abstract Set<IObjectMappingAssoziationPO> getMappings();
    
    /**
     * 
     * @param compNameGuid The GUID of the Component Name for which to find the
     *                     association.
     * @return the Association for the Component Name with the given GUID, or
     *         <code>null</code> if no such Association exists in this Object
     *         Mapping.
     */
    public abstract IObjectMappingAssoziationPO getLogicalNameAssoc(
            String compNameGuid);

    /**
     * 
     * @return the top-level category for unmapped Component Names.
     */
    public abstract IObjectMappingCategoryPO getUnmappedLogicalCategory();
    
    /**
     * 
     * @return the top-level category for unmapped Technical Names.
     */
    public abstract IObjectMappingCategoryPO getUnmappedTechnicalCategory();

    /**
     * 
     * @return the top-level category for mapped components.
     */
    public abstract IObjectMappingCategoryPO getMappedCategory();
    
    /**
     * Adds an association to the cache
     * @param assoc the assoc to add
     */
    public void addAssociationToCache(IObjectMappingAssoziationPO assoc);
    
    /**
     * removes all AutMains from the categories needed for deletion 
     */
    public void removeAllAutMains();
}
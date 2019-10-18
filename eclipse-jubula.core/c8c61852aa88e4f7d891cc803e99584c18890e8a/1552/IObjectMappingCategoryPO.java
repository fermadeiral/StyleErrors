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

/**
 * Category for organizing Object Mapping Associations. Maintains separate
 * child lists for subcategories and Associations.
 *
 * @author BREDEX GmbH
 * @created Feb 18, 2009
 */
public interface IObjectMappingCategoryPO extends IPersistentObject {

    /** category prefix */
    public static final String MAPPEDCATEGORY = "#1!mappedGDCat#1!"; //$NON-NLS-1$

    /** category prefix */
    public static final String UNMAPPEDLOGICALCATEGORY = "#1!unmappedlogicalGDCat#1!"; //$NON-NLS-1$

    /** category prefix */
    public static final String UNMAPPEDTECHNICALCATEGORY = "#1!mappedtechnicalGDCat#1!"; //$NON-NLS-1$

    /**
     * Adds the given association to this category, if it is not already in the
     * category.
     * 
     * @param assoc The Association to add to this category.
     */
    public void addAssociation(IObjectMappingAssoziationPO assoc);

    /**
     * Adds the given association to this category at the given index, if it 
     * is not already in the category.
     * 
     * @param index At which index in the Association list the Association
     *              should be inserted. If the index is out of bounds, the 
     *              Assocation is appended to the end of the list.
     * @param assoc The Association to add to this category.
     */
    public void addAssociation(int index, IObjectMappingAssoziationPO assoc);

    /**
     * 
     * @param assoc The Association to remove.
     */
    public void removeAssociation(IObjectMappingAssoziationPO assoc);

    /**
     * 
     * @param category The subcategory to add to this category.
     */
    public void addCategory(IObjectMappingCategoryPO category);

    /**
     * 
     * @param index At which index in the subcategory list the subcategory
     *              should be inserted. If the index is out of bounds, the 
     *              subcategory is appended to the end of the list.
     * @param category The subcategory to add to this category.
     */
    public void addCategory(int index, IObjectMappingCategoryPO category);

    /**
     * 
     * @param category The subcategory to remove.
     */
    public void removeCategory(IObjectMappingCategoryPO category);

    /**
     * 
     * @return an unmodifiable version of the list of child Associations.
     */
    public List<IObjectMappingAssoziationPO> getUnmodifiableAssociationList();

    /**
     * 
     * @return an unmodifiable version of the list of subcategories.
     */
    public List<IObjectMappingCategoryPO> getUnmodifiableCategoryList();

    /**
     * 
     * @return the parent category, or <code>null</code> if this category has
     *         no parent category.
     */
    public IObjectMappingCategoryPO getParent();

    /**
     * 
     * @param category The category to set as this category's parent.
     */
    public void setParent(IObjectMappingCategoryPO category);
    
    /**
     * 
     * @param name The new name for the category.
     */
    public void setName(String name);

    /**
     * 
     * @return the top-level category to which this category belongs. 
     *         Will return the category itself if the receiver is a 
     *         top-level category (has no parent category).
     */
    public IObjectMappingCategoryPO getSection();

    /**
     * @return the {@link IAUTMainPO} where the category is from
     */
    public IAUTMainPO getAutMainParent();

    /**
     * @param autMainParent the {@link IAUTMainPO} where the category is from
     */
    public void setAutMainParent(IAUTMainPO autMainParent);
}

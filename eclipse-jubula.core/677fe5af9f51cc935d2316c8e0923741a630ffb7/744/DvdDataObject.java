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
package org.eclipse.jubula.examples.aut.dvdtool.model;

import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdTableModel;

/**
 * This class holds a category and the corresponding table model containing the 
 * dvds, if there are any.
 *
 * @author BREDEX GmbH
 * @created 28.02.2008
 */
public class DvdDataObject {

    /** the category */
    private DvdCategory m_category;
    
    /** the table model containing the dvds for the category */
    private DvdTableModel m_tableModel;
    
    /**
     * public constructor
     * @param category the the category, must not be null
     * @throws IllegalArgumentException if category is null
     */
    public DvdDataObject(DvdCategory category)
        throws IllegalArgumentException {
        
        checkCategoryParameter(category);

        m_category = category;
        
        createTableModel();
    }
    /**
     * @return Returns the category.
     */
    public DvdCategory getCategory() {
        return m_category;
    }
    
    /**
     * @param category The category to set, must no be null
     * @throws IllegalArgumentException if category is null
     */
    public void setCategory(DvdCategory category)
        throws IllegalArgumentException {
        
        checkCategoryParameter(category);
        
        m_category = category;
        createTableModel();
    }
    
    /**
     * @return Returns the tableModel.
     */
    public DvdTableModel getTableModel() {
        return m_tableModel;
    }

    /**
     * @return true if the category has a parent
     */
    public boolean hasParent() {
        return m_category.getParent() != null;
    }
    
    /** 
     * returns the name of the category
     * {@inheritDoc}
     */
    public String toString() {
        return m_category.getName();
    }
    
    /**
     * @return true if the category has dvds
     */
    public boolean hasDvds() {
        return m_category.getDvds().size() > 0;
    }
    
    /**
     * @return true if the category has sub categories
     */
    public boolean hasCategories() {
        return m_category.getCategories().size() > 0;
    }

    /**
     * adds <code>dvd</code> to the category, creates also a new table model
     * 
     * @param dvd
     *            the dvd instance to add
     */
    public void addDvd(Dvd dvd) {
        m_category.insert(dvd);
        
        createTableModel();
    }
    
    /**
     * removes <code>dvd</code> from the category, creates also a new table
     * model
     * 
     * @param dvd
     *            the dvd instance to remove
     */
    public void removeDvd(Dvd dvd) {
        m_category.remove(dvd);
        
        createTableModel();
    }
    
    /**
     * (re)creats the table model
     */
    private void createTableModel() {
        m_tableModel = new DvdTableModel(
                m_category.getName(), m_category.getDvds());
    }
    
    /**
     * checks <code>category</code> for non null
     * 
     * @param category
     *            the category
     * @throws IllegalArgumentException
     *             if category is null
     */
    private void checkCategoryParameter(DvdCategory category)
        throws IllegalArgumentException {
        if (category == null) {
            throw new IllegalArgumentException("category must not be null"); //$NON-NLS-1$
        }
    }
    
}

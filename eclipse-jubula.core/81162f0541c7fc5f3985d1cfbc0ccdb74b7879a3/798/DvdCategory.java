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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the model class for a category.
 *
 * @author BREDEX GmbH
 * @created 14.04.2005
 */
public class DvdCategory implements Serializable {
    /** the name of the category */
    private String m_name;
    
    /** the parent category, may be null */
    private DvdCategory m_parent = null;
    
    /** the children categories, a Vector, see insert() and remove() */
    private List<DvdCategory> m_categories; 
    
    /** the dvds of this category, a Vector, see insert() and remove() */
    private List<Dvd> m_dvds;
    
    /** the enable state of the data object */
    private boolean m_enabled = true;
    
    /**
     * public constructor 
     * @param name the name of the category
     */
    public DvdCategory(String name) {
        m_name = name;
        
        init();
    }
    
    /**
     * private method for initialization
     */
    private void init() {
        m_categories = new ArrayList<DvdCategory>();
        m_dvds = new ArrayList<Dvd>();
    }
    
    /**
     * inserts newChild a as last child<br>
     * sets the child's parent to this node, and then inserts <code>newChild</code>to this
     * category's child <br>
     * <code>newChild</code> must not be null
     * 
     * @param newChild
     *            the category to insert under this node
     * @throws IllegalArgumentException
     *             if <code>newChild</code> is null or is an ancestor of this
     *             node
     */
    public void insert(DvdCategory newChild) throws IllegalArgumentException {
        insert(newChild, m_categories.size());
    }

    /**
     * inserts newChild at position <code>childIndex</code><br>
     * sets the child's parent to this node, and then inserts <code>newChild</code>to this
     * category's child array at index <code>childIndex</code>. if
     * <code>childIndex</code> is invalid (e.g. out of bounds)
     * <code>newChild</code> will be appended <code>newChild</code> must not
     * be null
     * 
     * @param newChild
     *            the category to insert under this node
     * @param childIndex
     *            the index in this node's child array where this node is to be
     *            inserted
     * @throws IllegalArgumentException
     *             if <code>newChild</code> is null or is an ancestor of this
     *             node
     */
    public void insert(DvdCategory newChild, int childIndex)
        throws IllegalArgumentException {
        if (newChild == null) {
            throw new IllegalArgumentException("new child must not be null"); //$NON-NLS-1$
        }

        newChild.setParent(this);
        m_categories.add(childIndex, newChild);
    }

    /**
     * removes <code>child</code> from this category, sets the parent from
     * <code>child</code> to null
     * 
     * if <code>child</code> is not child of this no changes are made
     * 
     * @param child
     *            the child to remove, must not be null
     * @throws IllegalArgumentException
     *             if child is null
     */
    public void remove(DvdCategory child) throws IllegalArgumentException {
        if (child == null) {
            throw new IllegalArgumentException("child must not be null"); //$NON-NLS-1$
        }
        
        if (m_categories.contains(child)) {
            child.setParent(null);
            m_categories.remove(child);
        }
    }

    /**
     * removes <code>dvd</code> from this category, id <code>dvd</code> does not
     * contains to this category, no changes are made.
     * 
     * @param dvd
     *            the dvd to remove, must not be null
     * @throws IllegalArgumentException
     *             if dvd is null
     */
    public void remove(Dvd dvd) throws IllegalArgumentException {
        if (dvd == null) {
            throw new IllegalArgumentException("dvd must not be null"); //$NON-NLS-1$
        }

        if (m_dvds.contains(dvd)) {
            dvd.setCategory(null);
            m_dvds.remove(dvd);
        }
    }

    /**
     * @return Returns the children.
     */
    public List getCategories() {
        return m_categories;
    }
    
    /**
     * @return Returns the dvds.
     */
    public List getDvds() {
        return m_dvds;
    }
    
    /**
     * appends <code>Dvd</code> to this the list of dvds of this category
     * <code>dvd</code> must not be null
     *
     * @param dvd the dvd to add
     * @throws IllegalArgumentException if dvd is null
     */
    public void insert(Dvd dvd) throws IllegalArgumentException {
        insert(dvd, m_dvds.size());
    }
    
    /**
     * adds <code>Dvd</code> to this category
     * if <code>index</code> is invalid (e.g. out of bounds) <code>dvd</code> will be appended
     * <code>dvd</code> must not be null

     * @param dvd the dvd to add
     * @param index the index for the dvd 
     * @throws IllegalArgumentException if dvd is null
     */
    public void insert(Dvd dvd, int index) throws IllegalArgumentException {
        if (dvd == null) {
            throw new IllegalArgumentException("dvd must not be null"); //$NON-NLS-1$
        }
        
        dvd.setCategory(this);
        m_dvds.add(index, dvd);
    }
    
    
    /**
     * @return Returns the parent.
     */
    public DvdCategory getParent() {
        return m_parent;
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * @param parent The parent to set.
     */
    private void setParent(DvdCategory parent) {
        m_parent = parent;
    }

    /**
     * @return Returns the enable state of the data object.
     */
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * @param enabled the enable state to set
     */
    public void setEnabled(boolean enabled) {
        m_enabled = enabled;
    }
}

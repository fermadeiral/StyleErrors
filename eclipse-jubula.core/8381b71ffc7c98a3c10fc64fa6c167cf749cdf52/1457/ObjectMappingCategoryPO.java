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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

/**
 * @author BREDEX GmbH
 * @created Feb 18, 2009
 */
@Entity
@Table(name = "OM_CATEGORY")
public class ObjectMappingCategoryPO implements IObjectMappingCategoryPO {

    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;

    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;

    /** name of the category */
    private String m_name;

    /** associations belonging to this category */
    private List<IObjectMappingAssoziationPO> m_childAssocList =
        new ArrayList<IObjectMappingAssoziationPO>();
    
    /** subcategories of this category */
    private List<IObjectMappingCategoryPO> m_childCategoryList =
        new ArrayList<IObjectMappingCategoryPO>();

    /** parent of this category */
    private IObjectMappingCategoryPO m_parent = null;

    /**
     * Default constructor (for Persistence (JPA / EclipseLink)).
     */
    @SuppressWarnings("unused")
    private ObjectMappingCategoryPO() {
        // For Persistence (JPA / EclipseLink). Nothing to initialize.
    }

    /**
     * Constructor
     * 
     * @param name The name of the category.
     */
    ObjectMappingCategoryPO(String name) {
        setName(name);
    }
    
    /**
     * {@inheritDoc}
     */
    public void addAssociation(IObjectMappingAssoziationPO assoc) {
        Assert.verify(assoc != null);
        if (!getHbmAssociationList().contains(assoc)) {
            assoc.setCategory(this);
            getHbmAssociationList().add(assoc);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addAssociation(int index, IObjectMappingAssoziationPO assoc) {
        Assert.verify(assoc != null);
        if (!getHbmAssociationList().contains(assoc)) {
            assoc.setCategory(this);
            if (index < 0 || index > getHbmAssociationList().size()) {
                getHbmAssociationList().add(assoc);
            } else {
                getHbmAssociationList().add(index, assoc);            
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addCategory(IObjectMappingCategoryPO category) {
        Assert.verify(category != null);
        category.setParent(this);
        getHbmCategoryList().add(category);
    }

    /**
     * {@inheritDoc}
     */
    public void addCategory(int index, IObjectMappingCategoryPO category) {
        Assert.verify(category != null);
        category.setParent(this);
        if (index < 0 || index > getHbmCategoryList().size()) {
            getHbmCategoryList().add(category);
        } else {
            getHbmCategoryList().add(index, category);            
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    @ManyToOne(targetEntity = ObjectMappingCategoryPO.class)
    @JoinColumn(name = "FK_PARENT", insertable = false, 
                updatable = false)
    public IObjectMappingCategoryPO getParent() {
        return m_parent;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public List<IObjectMappingAssoziationPO> getUnmodifiableAssociationList() {
        return Collections.unmodifiableList(getHbmAssociationList());
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public List<IObjectMappingCategoryPO> getUnmodifiableCategoryList() {
        return Collections.unmodifiableList(getHbmCategoryList());
    }

    /**
     * {@inheritDoc}
     */
    public void removeAssociation(IObjectMappingAssoziationPO assoc) {
        assoc.setCategory(null);
        getHbmAssociationList().remove(assoc);
    }

    /**
     * {@inheritDoc}
     */
    public void removeCategory(IObjectMappingCategoryPO category) {
        getHbmCategoryList().remove(category);
    }

    /**
     * {@inheritDoc}
     */
    public void setParent(IObjectMappingCategoryPO category) {
        m_parent = category;
    }

    /**
     *  
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }

    /**
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }

    /**
     *          
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "NAME", length = MAX_STRING_LENGTH)
    public String getName() {
        return m_name;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        throw new UnsupportedOperationException(
                getClass().getName() + StringConstants.SPACE
                    + Messages.DoesNotTrackItsParentProject);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Version
    public Integer getVersion() {
        return m_version;
    }

    /**
     * @param version The version to set.
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        throw new UnsupportedOperationException(
                getClass().getName() + StringConstants.SPACE
                    + Messages.DoesNotTrackItsParentProject);
    }

    /**
     * 
     * @return the associations belonging to this category.
     */
    @OneToMany(cascade = CascadeType.ALL,
               targetEntity = ObjectMappingAssoziationPO.class,
               fetch = FetchType.EAGER,
               orphanRemoval = true)
    @JoinColumn(name = "FK_CATEGORY")
    @OrderColumn(name = "IDX")
    @BatchFetch(value = BatchFetchType.JOIN)
    private List<IObjectMappingAssoziationPO> getHbmAssociationList() {
        return m_childAssocList;
    }

    /**
     * Persistence (JPA / EclipseLink) setter.
     * 
     * @param assocList The new list of associations belonging to this
     *                  category.
     */
    @SuppressWarnings("unused")
    private void setHbmAssociationList(
            List<IObjectMappingAssoziationPO> assocList) {
        
        m_childAssocList = assocList;
    }
    
    /**
     * 
     * @return The subcategories of this category.
     */
    @OneToMany(cascade = CascadeType.ALL, 
               targetEntity = ObjectMappingCategoryPO.class,
               fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PARENT")
    @OrderColumn(name = "IDX")
    @BatchFetch(value = BatchFetchType.JOIN)
    private List<IObjectMappingCategoryPO> getHbmCategoryList() {
        return m_childCategoryList;
    }

    /**
     * Persistence (JPA / EclipseLink) setter.
     * 
     * @param categoryList The new subcategories of this category.
     */
    @SuppressWarnings("unused")
    private void setHbmCategoryList(
            List<IObjectMappingCategoryPO> categoryList) {
        
        m_childCategoryList = categoryList;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public IObjectMappingCategoryPO getSection() {
        IObjectMappingCategoryPO section = this;
        while (section.getParent() != null) {
            section = section.getParent();
        }

        return section;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IObjectMappingCategoryPO)) {
            return false;
        }
        IObjectMappingCategoryPO o = (IObjectMappingCategoryPO)obj;
        if (getId() != null) {
            return getId().equals(o.getId());
        }
        return super.equals(obj);
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }
}

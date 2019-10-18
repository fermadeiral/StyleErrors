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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

/**
 * @author BREDEX GmbH
 * @created 07.04.2005
 */
@Entity
@Table(name = "OM_ASSOC")
public class ObjectMappingAssoziationPO 
    implements IObjectMappingAssoziationPO {
    
    /** column name for id */
    private static final String ID_COLUMN_NAME = "OBJECTMAPPINGASSOZIATIONPO_ID"; //$NON-NLS-1$

    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
   
    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /**
     * component type
     */
    private String m_type = null;
    
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version;
    
    /**
     * Technical Name of a component
     */
    private ICompIdentifierPO m_technicalName = null;
    
    /**
     * Category
     */
    private IObjectMappingCategoryPO m_category = null;
    
    /**
     * Logical names of compontent
     */
    private Set<String> m_logicalNames = new HashSet<String>();
    
    /**
     * The component identifier which has been used to create this po
     */
    private transient IComponentIdentifier m_compIdentifier = null;

    /**
     * Constructor
     *  
     */
    ObjectMappingAssoziationPO() {
        // only for Persistence (JPA / EclipseLink)
    }

    /**
     * Constructor
     *  
     * @param tech    ComponentIdentifier
     */
    ObjectMappingAssoziationPO(IComponentIdentifier tech) {
        setCompIdentifier(tech);
        if (tech != null) {
            ICompIdentifierPO techNamePO = createCompIdPoObject(tech);
            setTechnicalName(techNamePO);
            if (techNamePO != null) {
                techNamePO.setParentProjectId(getParentProjectId());
            }
        }
    }    
    
    /**
     * Constructor
     *  
     * @param tec      technical name
     * @param logic    logical name
     */
    ObjectMappingAssoziationPO(IComponentIdentifier tec, Set<String> logic) {
        this(tec);
        setLogicalNames(logic);
    }

    /**
     * Constructor
     *  
     * @param tec      technical name
     * @param logic    logical name
     */
    ObjectMappingAssoziationPO(IComponentIdentifier tec, String logic) {
        this(tec);
        Assert.verify(logic != null);
        getLogicalNames().add(logic);
    }

    /**
     * 
     * @return Returns the type.
     */
    @Basic
    @Column(length = MAX_STRING_LENGTH)
    public String getType() {
        return m_type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        m_type = type;
    }

    /**
     * {@inheritDoc}
     */
    public void setLogicalNames(Set<String> logicalNames) {
        m_logicalNames = logicalNames;
    }
    /**
     * {@inheritDoc}
     */
    public void addLogicalName(String name) {
        if (name != null && !getLogicalNames().contains(name)) {
            getLogicalNames().add(name);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeLogicalName(String name) {
        if (name != null) {
            getLogicalNames().remove(name);
        }
    }
    
    /**
     * 
     * @return Returns the technicalName.
     */
    @OneToOne(cascade = CascadeType.ALL, targetEntity = CompIdentifierPO.class,
            fetch = FetchType.LAZY)
    @BatchFetch(value = BatchFetchType.JOIN)
    public ICompIdentifierPO getTechnicalName() {
        return m_technicalName;
    }
    
    /**
     *         
     * {@inheritDoc}
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "LOGICAL_NAME",
        uniqueConstraints = {@UniqueConstraint(columnNames = 
                {"LOGICAL_NAME", ID_COLUMN_NAME})})
    @JoinColumn(name = "OM_ASSOC")
    @Column(name = "LOGICAL_NAME", length = MAX_STRING_LENGTH)
    @BatchFetch(value = BatchFetchType.JOIN)
    public Set<String> getLogicalNames() {
        return m_logicalNames;
    }
    
    /**
     * @param technicalName The technicalName to set.
     */
    public void setTechnicalName(ICompIdentifierPO technicalName) {
        m_technicalName = technicalName;
    }
    /**
     *         
     * @return Returns the category.
     */
    @Transient
    public IObjectMappingCategoryPO getCategory() {
        return getHbmCategory();
    }
    /**
     * @param category The category to set.
     */
    public void setCategory(IObjectMappingCategoryPO category) {
        setHbmCategory(category);
    }
    
    /**
     * 
     * @return Returns the category.
     */
    @ManyToOne(targetEntity = ObjectMappingCategoryPO.class, 
               fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CATEGORY", insertable = false, updatable = false)
    public IObjectMappingCategoryPO getHbmCategory() {
        return m_category;
    }

    /**
     * @param category The category to set.
     */
    private void setHbmCategory(IObjectMappingCategoryPO category) {
        m_category = category;
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
    @SuppressWarnings("unused")
    private void setId(Long id) {
        m_id = id;
    }
    
    /**
     *    
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
        if (getTechnicalName() != null) {
            m_technicalName.setParentProjectId(projectId);
        }
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * 
     * {@inheritDoc}
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /**
     * @param tech techName to convert in a CompIdentifierPO object
     * @return the associated compIdentifierPO object
     */
    private ICompIdentifierPO createCompIdPoObject(IComponentIdentifier tech) {
        ICompIdentifierPO techNamePO = new CompIdentifierPO();
        techNamePO.setComponentClassName(tech.getComponentClassName());
        techNamePO.setHierarchyNames(tech.getHierarchyNames());
        techNamePO.setNeighbours(tech.getNeighbours());
        techNamePO.setSupportedClassName(tech.getSupportedClassName());
        techNamePO.setAlternativeDisplayName(tech.getAlternativeDisplayName());
        techNamePO.setProfile(tech.getProfile());
        return techNamePO;
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
     * @param version version
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }
    
    
    /**
     * {@inheritDoc}
     * @return string representation of this object
     */
    @Transient
    public String getName() {
        return this.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void changeCompName(String oldCompNameGuid, String newCompNameGuid) {
        removeLogicalName(oldCompNameGuid);
        addLogicalName(newCompNameGuid);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getComponentType(
            IWritableComponentNameCache compNameCache, 
            Collection<Component> availableComponents) {
        ICompIdentifierPO techName = getTechnicalName();
        if (techName != null) {
            String supportedClassName = 
                getTechnicalName().getSupportedClassName();

            if (supportedClassName != null) {
                return CompSystem.getComponentType(
                        supportedClassName, availableComponents);
            }
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public IObjectMappingCategoryPO getSection() {
        IObjectMappingCategoryPO category = getCategory();
        IObjectMappingCategoryPO section = null;
        if (category != null) {
            section = category.getSection();
        }
        
        return section;
    }

    /**
     * @param compIdentifier the compIdentifier to set
     */
    public void setCompIdentifier(IComponentIdentifier compIdentifier) {
        m_compIdentifier = compIdentifier;
    }

    /**
     * @return the compIdentifier
     */
    @Transient
    public IComponentIdentifier getCompIdentifier() {
        return m_compIdentifier;
    }
}

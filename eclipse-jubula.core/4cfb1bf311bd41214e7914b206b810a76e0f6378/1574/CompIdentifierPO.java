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
import java.util.List;

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
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

/**
 * @author BREDEX GmbH
 * @created 20.06.2005
 */
@Entity
@Table(name = "COMP_ID")
class CompIdentifierPO extends ComponentIdentifier implements
    ICompIdentifierPO {
    
    
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;

    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;
    
    /** po profile */
    private IObjectMappingProfilePO m_profilePO = null;

    /**
     * only for Persistence (JPA / EclipseLink)
     */
    CompIdentifierPO() {
        super();
    }
    
    /**
     * @return Returns the neighbors.
     */
    @ElementCollection(targetClass = String.class, 
                       fetch = FetchType.EAGER)
    @CollectionTable(name = "NEIGHBOURS")
    @Column(name = "NEIGHBOUR", length = MAX_STRING_LENGTH)
    @OrderColumn(name = "IDX")
    @JoinColumn(name = "FK_COMP_ID")
    @BatchFetch(value = BatchFetchType.JOIN)
    public List<String> getNeighbours() {
        return super.getNeighbours();
    }

    /**
     * @param neighbours The neighbors to set.
     */
    public void setNeighbours(List<String> neighbours) {
        super.setNeighbours(neighbours);
    }

    /**
     *  
     * @return Returns the componentClassName.
     */
    @Basic
    @Column(name = "COMP_CLASS_NAME", length = MAX_STRING_LENGTH)
    public String getComponentClassName() { // NOPMD by al on 3/19/07 1:26 PM
        return super.getComponentClassName();
    }
    
    /**
     * @param componentClassName The componentClassName to set.
     */
    public void setComponentClassName(String componentClassName) { // NOPMD by al on 3/19/07 1:26 PM
        super.setComponentClassName(componentClassName);
    }
    
    /**
     * 
     * @return Returns the supportedClassName.
     */
    @Basic
    @Column(name = "SUPP_CLASS_NAME", length = MAX_STRING_LENGTH)
    public String getSupportedClassName() { // NOPMD by al on 3/19/07 1:26 PM
        return super.getSupportedClassName();
    }
    
    /**
     * @param supportedClassName The supportedClassName to set.
     */
    public void setSupportedClassName(String supportedClassName) { // NOPMD by al on 3/19/07 1:26 PM
        super.setSupportedClassName(supportedClassName);
    }
    
    /**
     * 
     * @return Returns the hierarchyNames.
     */
    @ElementCollection(targetClass = String.class, 
                       fetch = FetchType.EAGER)
    @CollectionTable(name = "HIERARCHY_NAMES")
    @Column(name = "HIERARCHY_NAME", length = MAX_STRING_LENGTH)
    @OrderColumn(name = "IDX")
    @JoinColumn(name = "FK_COMP_ID")
    @BatchFetch(value = BatchFetchType.JOIN)
    public List<String> getHierarchyNames() {
        return super.getHierarchyNames();
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public org.eclipse.jubula.tools.Profile getProfile() {
        if (super.getProfile() != null && m_profilePO != null) {
            return super.getProfile();
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public void setProfile(org.eclipse.jubula.tools.Profile profile) {
        if (profile != null) {
            super.setProfile(profile);
            if (m_profilePO == null) {
                m_profilePO = PoMaker
                        .createObjectMappingProfile((Profile) profile);
            } else {
                m_profilePO.useTemplate((Profile) profile);
            }   
        } else {
            m_profilePO = null;
            super.setProfile(null);
        }
    }
    
    /**
     * @param hierarchyNames
     *            The hierarchyNames to set. if null, the list will be cleared.
     */
    public void setHierarchyNames(List<String> hierarchyNames) {
        super.setHierarchyNames(hierarchyNames);
    }
    
    /**
     * @return Clone of object
     */
    public ICompIdentifierPO makePoClone() {
        CompIdentifierPO clone = new CompIdentifierPO();
        clone.setHierarchyNames(new ArrayList<String>(
            getHierarchyNames()));
        clone.setComponentClassName(getComponentClassName());
        if (getComponentClassName() != null) {
            clone.setSupportedClassName(getComponentClassName());
        }
        if (getNeighbours() != null) {
            clone.setNeighbours(new ArrayList<String>(
                    getNeighbours()));
        }
        if (getParentProjectId() != null) {
            clone.setParentProjectId(getParentProjectId());
        }
        if (getProfile() != null) {
            clone.setProfile(getProfile());
        }
        if (getProfilePO() != null) {
            clone.setProfilePO(getProfilePO());
        }
        clone.setAlternativeDisplayName(getAlternativeDisplayName());
        return clone;
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
     * 
     * {@inheritDoc}
     */
    public void setId(Long id) {
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
     * @return empty string
     */
    @Transient
    public String getName() {
        return StringConstants.EMPTY;
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "ALTERNATIVE_DISPLAY_NAME", length = MAX_STRING_LENGTH)
    public String getAlternativeDisplayName() {
        return super.getAlternativeDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    public void setAlternativeDisplayName(String alternativeDisplayName) {
        super.setAlternativeDisplayName(alternativeDisplayName);
    }

    /**
     * set the po profile
     * @param profile the profile
     */
    private void setProfilePO(IObjectMappingProfilePO profile) {
        if (profile != null) {
            super.setProfile(new Profile(profile.getName(),
                    profile.getNameFactor(), profile.getPathFactor(),
                    profile.getContextFactor(), profile.getThreshold()));
        }
        m_profilePO = profile;
    }
    
    @OneToOne(targetEntity = ObjectMappingProfilePO.class,
            optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "FK_PROFILE", nullable = true)
    @Override
    public IObjectMappingProfilePO getProfilePO() {
        return m_profilePO;
    }
}
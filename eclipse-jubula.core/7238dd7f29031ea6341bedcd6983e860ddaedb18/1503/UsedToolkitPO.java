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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * @author BREDEX GmbH
 * @created 05.06.2007
 * 
 */
@Entity
@Table(name = "USED_TOOLKITS", 
       uniqueConstraints = 
           @UniqueConstraint(columnNames = { "NAME", "PARENT_PROJ" }))
    class UsedToolkitPO implements IUsedToolkitPO {
    
    
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    
    /** The I18N-Key of the used toolkit */
    private String m_toolkitId = null;
    
    /** The major version number of the used toolkit */
    private Integer m_majorVersion = null; 
    
    /** The minor version number of the used toolkit */
    private Integer m_minorVersion = null;
    
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;
    
    /**
     * Constructor for Persistence (JPA / EclipseLink).
     */
    UsedToolkitPO() {
        // nothing yet
    }

    /**
     * Constructor
     * @param toolkitId The id of the used toolkit
     * @param majorVersion The major version number of the used toolkit.
     * @param minorVersion The minor version number of the used toolkit.
     * @param projectID The m_id of the depending Project.
     */
    UsedToolkitPO(String toolkitId, int majorVersion, int minorVersion, 
        Long projectID) {
        
        setHbmToolkitId(toolkitId);
        setHbmMajorVersion(Integer.valueOf(majorVersion));
        setHbmMinorVersion(Integer.valueOf(minorVersion));
        setHbmParentProjectId(projectID);
    }
    
    
    /**
     * only for Persistence (JPA / EclipseLink)
     * 
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    
    
    /**
     * only for Persistence (JPA / EclipseLink)
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }

    /**
     *      
     * @return the toolkitId
     */
    @Basic
    @Column(name = "NAME")
    String getHbmToolkitId() {
        return m_toolkitId;
    }


    /**
     * @param toolkitId the toolkitId to set
     */
    void setHbmToolkitId(String toolkitId) {
        m_toolkitId = toolkitId;
    }


    /**
     * 
     * @return the majorVersion
     */
    @Basic
    @Column(name = "MAJOR_VERSION", nullable = false)
    Integer getHbmMajorVersion() {
        return m_majorVersion;
    }


    /**
     * @param majorVersion the majorVersion to set
     */
    void setHbmMajorVersion(Integer majorVersion) {
        m_majorVersion = majorVersion;
    }


    /**
     * 
     * @return the minorVersion
     */
    @Basic
    @Column(name = "MINOR_VERSION", nullable = false)
    Integer getHbmMinorVersion() {
        return m_minorVersion;
    }


    /**
     * @param minorVersion the minorVersion to set
     */
    void setHbmMinorVersion(Integer minorVersion) {
        m_minorVersion = minorVersion;
    }

    /**
     * @return The major version number of the used toolkit.
     */
    @Transient
    public int getMajorVersion() {
        return getHbmMajorVersion().intValue();
    }
    
    /**
     * @return The minor version number of the used toolkit.
     */
    @Transient
    public int getMinorVersion() {
        return getHbmMinorVersion().intValue();
    }

    /**
     * @return The id of the used toolkit.
     */
    @Transient
    public String getToolkitId() {
        return getHbmToolkitId();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IUsedToolkitPO)) {
            return false;
        }
        IUsedToolkitPO otherUtk = (IUsedToolkitPO)obj;
        return new EqualsBuilder()
            .append(getMajorVersion(), otherUtk.getMajorVersion())
            .append(getMinorVersion(), otherUtk.getMinorVersion())
            .append(getToolkitId(), otherUtk.getToolkitId())
                .isEquals();
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder()
            .append(getMajorVersion())
            .append(getMinorVersion())
            .append(getToolkitId())
                .toHashCode();
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
     * {@inheritDoc}
     */
    void setVersion(Integer version) {
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
     *    
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }

    /**
     * {@inheritDoc}
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
    }
    
}

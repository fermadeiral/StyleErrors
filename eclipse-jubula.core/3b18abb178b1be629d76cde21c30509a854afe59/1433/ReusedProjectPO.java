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
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;


/**
 * @author BREDEX GmbH
 * @created Jun 5, 2007
 */
@Entity
@Table(name = "REUSED_PROJECTS", 
       uniqueConstraints = @UniqueConstraint(columnNames = 
           { "PARENT_PROJ", "REUSED_PROJ_GUID" }))
class ReusedProjectPO implements IReusedProjectPO {

    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    
    /** Major version number of the reused project */
    private Integer m_majorNumber = null;

    /** Minor version number of the reused project */
    private Integer m_minorNumber = null;
    
    /** The micro version number for this project */
    private Integer m_microNumber = null;
    
    /** The qualifier version for this project */
    private String m_versionQualifier = null;

    /** Persistence (JPA / EclipseLink) version id */
    private Integer m_version = null;

    /** the GUID of the reused project */
    private String m_projectGuid = null;

    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /**
     * empty constructor for Persistence (JPA / EclipseLink)
     */
    @SuppressWarnings("unused")
    private ReusedProjectPO() {
        // Only for Persistence (JPA / EclipseLink)
    }


    /**
     * Constructor when GUID, and major/minor numbers are already defined.
     * 
     * @param projectGuid The GUID of the reused project.
     * @param majorNumber The major version number.
     * @param minorNumber The minor version number.
     * @param microNumber The micro version number for this project
     * @param versionQualifier The version qualifier for this project
     */
    ReusedProjectPO(String projectGuid, 
            Integer majorNumber, Integer minorNumber,
            Integer microNumber, String versionQualifier) {
        
        setProjectGuid(projectGuid);
        setMajorNumber(majorNumber);
        setMinorNumber(minorNumber);
        setMicroNumber(microNumber);
        setVersionQualifier(versionQualifier);
    }

    /**
     * 
     * @return The name of the referenced project, if it is available. 
     *         Otherwise, <code>null</code>.
     */
    @Transient
    public String getProjectName() {
        return ProjectNameBP.getInstance().getName(getProjectGuid());
    }

    /**
     * 
     * @return the major version number
     */
    @Basic
    @Column(name = "MAJOR_VERS_NUMBER")
    public Integer getMajorNumber() {
        return m_majorNumber;
    }
    
    /**
     * 
     * @param majorNumber The major version number.
     */
    private void setMajorNumber(Integer majorNumber) {
        m_majorNumber = majorNumber;
    }
    
    /**
     * 
     * @return the minor version number
     */
    @Basic
    @Column(name = "MINOR_VERS_NUMBER")
    public Integer getMinorNumber() {
        return m_minorNumber;
    }
    
    /**
     * 
     * @param minorNumber The minor version number.
     */
    private void setMinorNumber(Integer minorNumber) {
        m_minorNumber = minorNumber;
    }
    
    /**
     * 
     * @return Returns the micro version number.
     */
    @Basic
    @Column(name = "MICRO_VERS_NUMBER")
    public Integer getMicroNumber() {
        return m_microNumber;
    }

    /**
     * @param microNumber The micro Number to set.
     */
    private void setMicroNumber(Integer microNumber) {
        m_microNumber = microNumber;
    }

    /**
     * 
     * @return Returns the qualifier version number.
     */
    @Basic
    @Column(name = "VERSION_QUALIFIER")
    public String getVersionQualifier() {
        return m_versionQualifier;
    }

    /**
     * @param versionQualifier The qualifier to set.
     */
    private void setVersionQualifier(String versionQualifier) {
        m_versionQualifier = versionQualifier;
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
     * @return a String representation of this object.
     */
    @Transient
    public String getName() {
        return getProjectName() != null ? getProjectName() : getProjectGuid();
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
     * 
     * @return the GUID of the reused project.
     */
    @Basic
    @Column(name = "REUSED_PROJ_GUID")
    public String getProjectGuid() {
        return m_projectGuid;
    }

    /**
     * @param projectGuid The GUID of the used project
     */
    private void setProjectGuid(String projectGuid) {
        m_projectGuid = projectGuid;        
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof IReusedProjectPO) {
            IReusedProjectPO reused = (IReusedProjectPO)obj;
            return new EqualsBuilder()
                .append(getMajorNumber(), reused.getMajorNumber())
                .append(getMinorNumber(), reused.getMinorNumber())
                .append(getMicroNumber(), reused.getMicroNumber())
                .append(getVersionQualifier(), reused.getVersionQualifier())
                .append(getParentProjectId(), reused.getParentProjectId())
                .append(getProjectGuid(), reused.getProjectGuid())
                .isEquals();
        }
        
        return super.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder()
            .append(getMajorNumber())
            .append(getMinorNumber())
            .append(getMicroNumber())
            .append(getVersionQualifier())
            .append(getParentProjectId())
            .append(getProjectGuid())
            .toHashCode();
    }


    /**
     * {@inheritDoc}
     */
    public int compareTo(IReusedProjectPO otherReused) {
        int retVal = getName().compareTo(otherReused.getName());
        
        if (retVal == 0) {
            retVal = getVersionString().compareTo(
                    otherReused.getVersionString());
        }

        return retVal;
    }
    
    /**
     * Gets the Version String in the following format:
     * [majNum].[minNum].[micNum]_[qualifier]
     * @return a String generated from the version
     */
    @Transient
    public String getVersionString() {
        return getProjectVersion().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public ProjectVersion getProjectVersion() {
        return new ProjectVersion(getMajorNumber(),
                getMinorNumber(), getMicroNumber(),
                getVersionQualifier());
    }
}

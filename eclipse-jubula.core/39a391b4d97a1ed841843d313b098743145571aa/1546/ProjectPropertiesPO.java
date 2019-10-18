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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.eclipse.jubula.client.core.utils.TrackingUnit;
import org.eclipse.persistence.annotations.Index;

/**
 * @author BREDEX GmbH
 * @created Jun 11, 2007
 */
@Entity
@Table(name = "PROJECT_PROPERTIES", 
        uniqueConstraints = @UniqueConstraint(columnNames = 
                { "GUID", "MAJOR_NUMBER", "MINOR_NUMBER",
                "MICRO_NUMBER", "VERSION_QUALIFIER" }))
class ProjectPropertiesPO implements IProjectPropertiesPO {
       
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;

    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;

    /** Set of reused projects */
    private Set<IReusedProjectPO> m_usedProjects = 
        new HashSet<IReusedProjectPO>();
    
    /** the container containing the configurations for the teststyle checks */
    private ICheckConfContPO m_checkConfCont;    

    /**
     * <code>m_isModified</code> flag to signal modification of language list
     * by Persistence (JPA / EclipseLink)
     */
    private transient boolean m_isModified = true;
    
    /** information about the used Toolkit (Swing(=default), Swt, Web, ...) */
    private String m_toolkit = null;

    /** The major version number of this project */
    private Integer m_majorNumber = null;

    /** The minor version number for this project */
    private Integer m_minorNumber = null;
    
    /** The micro version number for this project */
    private Integer m_microNumber = null;
    
    /** The version for this project */
    private String m_versionQualifier = null;
    
    /** Indicates whether this project can be reused in other projects */
    private boolean m_isReusable = false;

    /** Indicates whether this project can tracks changes */
    private boolean m_isTrackingActivated = false;
    
    /** Indicates what detail of a user who made a change is stored for identification */
    private String m_trackChangesSignature = null;
    
    /** The unit in which time should be measured for storing changes */
    private TrackingUnit m_trackChangesUnit = null;
    
    /** The timespan of how long changes should be stored */
    private Integer m_trackChangesSpan = null;
    
    /** */
    private String m_markupLanguage = null;
    
    /**
     * Indicates whether this project is protected against undo-able
     * modifications
     */
    private boolean m_isProtected = false;

    /** The default of auto-cleanup days for test result details */
    private Integer m_testResultCleanupInterval = IProjectPO.CLEANUP_DEFAULT;

    /** whether to report in case of a failure */
    private boolean m_reportOnFailure = false;

    /** whether to report in case of a success */
    private boolean m_reportOnSuccess = false;

    /** guid of the corresponding project*/
    private String m_guid;
    
    /**
     * For Persistence (JPA / EclipseLink)
     */
    ProjectPropertiesPO() {
        this(null, 1, 0, 0, null);
    }
    
    /**
     * Constructor for when major/minor number are known
     * 
     * @param guid for the corresponding project.
     * @param majorNumber The major number for the corresponding project.
     * @param minorNumber The minor number for the corresponding project.
     * @param microNumber The micro number for the corresponding project.
     * @param version The version string for the corresponding project.
     */
    ProjectPropertiesPO(String guid, Integer majorNumber, Integer minorNumber,
            Integer microNumber, String version) {
        setGuid(guid);
        setMajorNumber(majorNumber);
        setMinorNumber(minorNumber);
        setMicroNumber(microNumber);
        setVersionQualifier(version);
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
     * @return Long
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
    @Transient
    public String getName() {
        return "Project properties"; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
        for (IReusedProjectPO reusedProj : getHbmUsedProjects()) {
            reusedProj.setParentProjectId(projectId);
        }
        if (getCheckConfCont() != null) {
            getCheckConfCont().setParentProjectId(projectId);
        }
    }

    /**
     *    
     * @return the GUID.
     */
    @Basic
    @Column(name = "GUID")
    @Index(name = "PI_NODE_GUID")
    public String getGuid() {
        return m_guid;
    }
    /**
     * @param guid The GUID to set.
     */
    private void setGuid(String guid) {
        m_guid = guid;
    }

    /**
     * @deprecated
     * only to use by LanguageHelper
     * @return Returns the isModified.
     */
    @Transient
    public boolean isModified() {
        return m_isModified;
    }

    /**
     * @deprecated
     * only to use by LanguageHelper
     * @param isModified The isModified to set.
     */
    public void setModified(boolean isModified) {
        m_isModified = isModified;
    }

    /**
     * {@inheritDoc}
     */
    public void addUsedProject(IReusedProjectPO reusedProject) {
        reusedProject.setParentProjectId(getParentProjectId());
        getHbmUsedProjects().add(reusedProject);
    }

    /**
     * {@inheritDoc}
     */
    public void removeUsedProject(IReusedProjectPO project) {
        getHbmUsedProjects().remove(project);
    }

    /**
     * 
     * @return Returns <code>true</code> if this project can be reused by other
     *         projects. Otherwise <code>false</code>.
     */
    @Basic
    @Column(name = "IS_REUSABLE")
    public boolean getIsReusable() {
        return m_isReusable;
    }
    
    /**
     * 
     * @return Returns <code>true</code> if this project can be reused by other
     *         projects. Otherwise <code>false</code>.
     */
    @Basic
    @Column(name = "IS_TRACKING_CHANGES")
    public boolean getIsTrackingActivated() {
        return m_isTrackingActivated;
    }

    /**
     * 
     * @return Returns <code>true</code> if this project is protected. Otherwise
     *         <code>false</code>.
     */
    @Basic
    @Column(name = "IS_PROTECTED")
    public boolean getIsProtected() {
        return m_isProtected;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public String getToolkit() {
        return getHbmToolkit();
    }

    /**
     * only for Persistence (JPA / EclipseLink)
     * 
     * @return Returns the usedProjects set.
     */
    @OneToMany(cascade = CascadeType.ALL, 
               orphanRemoval = true, 
               targetEntity = ReusedProjectPO.class,
               fetch = FetchType.EAGER)
    @JoinColumn(name = "FK_PROJ_PROPERTIES")
    private Set<IReusedProjectPO> getHbmUsedProjects() {
        return m_usedProjects;
    }

    /**
     * For Persistence (JPA / EclipseLink)
     * @param reusedProjects The reusedProjects to set.
     */
    @SuppressWarnings("unused")
    private void setHbmUsedProjects(Set<IReusedProjectPO> reusedProjects) {
        m_usedProjects = reusedProjects;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public Set<IReusedProjectPO> getUsedProjects() {
        return Collections.unmodifiableSet(getHbmUsedProjects());
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setIsReusable(boolean isReusable) {
        m_isReusable = isReusable;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setIsTrackingActivated(boolean isTrackingActivated) {
        m_isTrackingActivated = isTrackingActivated;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setIsProtected(boolean isProtected) {
        m_isProtected = isProtected;
    }

    /**
     * Sets the toolkit. If th etoolkit has the I18n prefix, it will be removed.
     * @param toolkit the toolkit
     */
    public void setToolkit(String toolkit) {
        setHbmToolkit(toolkit);
    }

    /**
     * only for Persistence (JPA / EclipseLink) !!!
     *    
     * @return the toolkit
     */
    @Basic
    @Column(name = "TOOLKIT")
    private String getHbmToolkit() {
        return m_toolkit;
    }

    /**
     * @param toolkit the toolkit
     */
    private void setHbmToolkit(String toolkit) {
        m_toolkit = toolkit;
    }

    /**
     * 
     * @return Returns the major version number.
     */
    @Basic
    @Column(name = "MAJOR_NUMBER")
    public Integer getMajorNumber() {
        return m_majorNumber;
    }

    /**
     * 
     * @return Returns the minor version number.
     */
    @Basic
    @Column(name = "MINOR_NUMBER")
    public Integer getMinorNumber() {
        return m_minorNumber;
    }
    
    /**
     * 
     * @return Returns the major version number.
     */
    @Basic
    @Column(name = "MICRO_NUMBER")
    public Integer getMicroNumber() {
        return m_microNumber;
    }

    /**
     * 
     * @return Returns the minor version number.
     */
    @Basic
    @Column(name = "VERSION_QUALIFIER")
    public String getVersionQualifier() {
        return m_versionQualifier;
    }

    /**
     * @param majorNumber The majorNumber to set.
     */
    private void setMajorNumber(Integer majorNumber) {
        m_majorNumber = majorNumber;
    }

    /**
     * @param minorNumber The minorNumber to set.
     */
    private void setMinorNumber(Integer minorNumber) {
        m_minorNumber = minorNumber;
    }
    
    /**
     * @param microNumber The majorNumber to set.
     */
    private void setMicroNumber(Integer microNumber) {
        m_microNumber = microNumber;
    }

    /**
     * @param versionQualifier The minorNumber to set.
     */
    private void setVersionQualifier(String versionQualifier) {
        m_versionQualifier = versionQualifier;
    }

    /**
     * {@inheritDoc}
     */
    public void clearUsedProjects() {
        getHbmUsedProjects().clear();
    }

    /**
     * 
     * @return Returns the test result cleanup interval in days
     */
    @Basic
    @Column(name = "TRD_CLEANUP_INTERVAL")
    public Integer getTestResultCleanupInterval() {
        return m_testResultCleanupInterval;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setTestResultCleanupInterval(Integer noOfDays) {
        m_testResultCleanupInterval = noOfDays;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @OneToOne(cascade = CascadeType.ALL, 
              fetch = FetchType.EAGER,
              targetEntity = CheckConfContPO.class)
    @JoinColumn(name = "CHECK_CONF_CONT", unique = true)
    public ICheckConfContPO getCheckConfCont() {
        return m_checkConfCont;
    }
    
    /**
     * @param checkConfCont the checkConfCont to set
     */
    public void setCheckConfCont(ICheckConfContPO checkConfCont) {
        m_checkConfCont = checkConfCont;
    }
    
    /** {@inheritDoc} */
    @Basic
    @Column(name = "IS_REPORT_ON_SUCCESS")
    public boolean getIsReportOnSuccess() {
        return m_reportOnSuccess;
    }
    
    /** {@inheritDoc} */
    public void setIsReportOnSuccess(boolean isReportOnSuccess) {
        m_reportOnSuccess = isReportOnSuccess;
    }
    
    /** {@inheritDoc} */
    @Basic
    @Column(name = "IS_REPORT_ON_FAILURE")
    public boolean getIsReportOnFailure() {
        return m_reportOnFailure;
    }

    /** {@inheritDoc} */
    public void setIsReportOnFailure(boolean isReportOnFailure) {
        m_reportOnFailure = isReportOnFailure;
    }

    /** {@inheritDoc} */
    @Basic
    @Column(name = "TRACK_CHANGES_SIGNATURE")
    public String getTrackChangesSignature() {
        return m_trackChangesSignature;
    }

    /** {@inheritDoc} */
    public void setTrackChangesSignature(String signature) {
        m_trackChangesSignature = signature;
    }

    /** {@inheritDoc} */
    @Basic
    @Column(name = "TRACK_CHANGES_UNIT")
    public TrackingUnit getTrackChangesUnit() {
        return m_trackChangesUnit;
    }

    /** {@inheritDoc} */
    public void setTrackChangesUnit(TrackingUnit unit) {
        m_trackChangesUnit = unit;
    }

    /** {@inheritDoc} */
    @Basic
    @Column(name = "TRACK_CHANGES_SPAN")
    public Integer getTrackChangesSpan() {
        return m_trackChangesSpan;
    }

    /** {@inheritDoc} */
    public void setTrackChangesSpan(Integer span) {
        m_trackChangesSpan = span;
    }

    /** {@inheritDoc} */
    public void setMarkupLanguage(String markupLanguage) {
        m_markupLanguage = markupLanguage;
    }

    /** {@inheritDoc} */
    @Basic
    @Column(name = "MARKUP_LANGUAGE")
    public String getMarkupLanguage() {
        return m_markupLanguage;
    }
}

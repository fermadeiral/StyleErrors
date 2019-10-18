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
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.persistence.PersistenceUtil;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 04.02.2005
 */
@Entity
@DiscriminatorValue(value = "P")
class ProjectPO extends ParamNodePO implements IProjectPO {
    
    /** standard logging */
    private static Logger log = LoggerFactory.getLogger(ProjectPO.class);
    
    /** the properties for this project */
    private ProjectPropertiesPO m_projectProperties = null;
    
    /** object to manage all AUTs */
    private AUTContPO m_autCont = null;

    /**
     * <code>m_clientMetaDataVersion</code>metaDataVersion of Client which 
     * was used for creation of project
     */
    private Integer m_clientMetaDataVersion = null;
    
    /**
     * <code>m_testdatacubecont</code> object to manage all test data cubes
     */
    private ITestDataCategoryPO m_testdatacubecont;

    /**
     * only for Persistence (JPA / EclipseLink)
     */
    ProjectPO() {
        // only for Persistence (JPA / EclipseLink)
    }
    
    /**
     * The constructor.
     * @param name The name of the project.
     * @param metadataVersion metadata version
     * @param isGenerated indicates whether this node has been generated
     */
    ProjectPO(String name, Integer metadataVersion, boolean isGenerated) { 
        this(metadataVersion, 1, 0, null, null,
                PersistenceUtil.generateUUID(), isGenerated);
        ProjectNameBP.getInstance().setName(this.getGuid(), name, false);
    }

    /**
     * The constructor for projects that already have a GUID and version number.
     * @param metadataVersion metadataVersion
     * @param majorNumber The major version number for this project
     * @param minorNumber The minor version number for this project
     * @param microNumber The micro version number for this project
     * @param versionQualifier The version qualifier for this project
     * @param guid The GUID of the project.
     * @param isGenerated indicates whether this node has been generated
     */
    ProjectPO(Integer metadataVersion, Integer majorNumber,
            Integer minorNumber, Integer microNumber, String versionQualifier,
            String guid, boolean isGenerated) {

        super("dummy", guid, isGenerated); //$NON-NLS-1$
        if (majorNumber == null && versionQualifier == null) {
            throw new JBFatalException(
                    "Project with empty qualifier and version number are not allowed", //$NON-NLS-1$
                    MessageIDs.E_DELETE_TESTRESULT);
        }
        init(metadataVersion, majorNumber, minorNumber, microNumber,
                versionQualifier);
    }

    /**
     * @param metadataVersion metadataVersion
     * @param majorNumber The major version number of the project.
     * @param minorNumber The minor version number of the project.
     * @param microNumber The micro version number for this project
     * @param versionQualifier The version qualifier for this project
     */
    private void init(Integer metadataVersion, 
            Integer majorNumber, Integer minorNumber, 
            Integer microNumber, String versionQualifier) {
        
        setProjectProperties(
                PoMaker.createProjectPropertiesPO(getGuid(),
                    majorNumber, minorNumber, microNumber, versionQualifier));
        setAutCont(PoMaker.createAUTContPO());
        setTestDataCubeContPO(PoMaker.createTestDataCategoryPO());
        super.addNode(0, NodeMaker.createCategoryPO("TSB_ROOT")); //$NON-NLS-1$
        super.addNode(1, NodeMaker.createCategoryPO("TCB_ROOT")); //$NON-NLS-1$
        setClientMetaDataVersion(metadataVersion);
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Set<IAUTMainPO> getAutMainList() {
        AUTContPO autCont = getHbmAutCont();
        if (autCont != null)  {
            Set<IAUTMainPO> autMainList = autCont.getAutMainList();
            if (autMainList != null) {
                return Collections.unmodifiableSet(autMainList);
            }
            log.error("The AUT main list unexpectedly returned null!"); //$NON-NLS-1$
        } else {
            log.error("The AUT Cont was unexpectedly null!"); //$NON-NLS-1$
        }
        return null;
    }

    /**
     * Adds an AUT to a project.
     * @param aut The AUT to add.
     */
    public void addAUTMain(IAUTMainPO aut) {
        getHbmAutCont().addAUTMain(aut);
    }
    /**
     * Removes an AUT from a project.
     * @param aut The AUT to remove.
     */
    public void removeAUTMain(IAUTMainPO aut) {
        getHbmAutCont().removeAUTMain(aut);
        List<ITestSuitePO> tsList = TestSuiteBP.getListOfTestSuites();
        for (ITestSuitePO ts : tsList) {
            if (aut == ts.getAut()) {
                ts.setAut(null);
            }
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public String getMarkupLanguage() {
        return m_projectProperties.getMarkupLanguage();
    }
    /**
     * 
     * {@inheritDoc}
     */
    public void setMarkupLanguage(String markupLanguage) {
        m_projectProperties.setMarkupLanguage(markupLanguage);
    }  

    /**
     * @param autCont The autCont to set.
     */
    private void setAutCont(IAUTContPO autCont) {
        setHbmAutCont((AUTContPO)autCont);
    }

    /**
     *      
     * @return Returns the AutCont.
     */
    @OneToOne(cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    @JoinColumn(name = "AUT_CONT", unique = true)
    private AUTContPO getHbmAutCont() {
        return m_autCont;
    }

    /**
     * @param autCont The autCont to set.
     */
    private void setHbmAutCont(AUTContPO autCont) {
        m_autCont = autCont;
    }

    /**
     * 
     * @return the AUT container.
     */
    @Transient
    public IAUTContPO getAutCont() {
        return getHbmAutCont();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setTestDataCubeContPO(ITestDataCategoryPO testDataCategory) {
        setHbmTestDataCubeContPO(testDataCategory);
        testDataCategory.setParentProjectId(getId());
    }
    
    /**
     * 
     * @return Returns the clientMetaDataVersion.
     */
    @Basic
    @Column(name = "CONF_MAJ_VERS")
    public Integer getClientMetaDataVersion() {
        return m_clientMetaDataVersion;
    }

    /**
     * @param metaDataVersion The metaDataVersion to set.
     */
    public void setClientMetaDataVersion(Integer metaDataVersion) {
        m_clientMetaDataVersion = metaDataVersion;
    }


    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public Set<IReusedProjectPO> getUsedProjects() {
        return Collections.unmodifiableSet(
            getProjectProperties().getUsedProjects());
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public Integer getMajorProjectVersion() {
        return getProjectProperties().getMajorNumber();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public Integer getMinorProjectVersion() {
        return getProjectProperties().getMinorNumber();
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public Integer getMicroProjectVersion() {
        return getProjectProperties().getMicroNumber();
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public String getProjectVersionQualifier() {
        return getProjectProperties().getVersionQualifier();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public boolean getIsReusable() {
        return getProjectProperties().getIsReusable();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setIsReusable(boolean isReusable) {
        getProjectProperties().setIsReusable(isReusable);
    }
    

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean getIsProtected() {
        return getProjectProperties().getIsProtected();
    }

    /**
     * {@inheritDoc}
     */
    public void setIsProtected(boolean isProtected) {
        getProjectProperties().setIsProtected(isProtected);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public List<IALMReportingRulePO> getALMReportingRules() {
        return getProjectProperties().getALMReportingRules();
    }

    /**
     * {@inheritDoc}
     */
    public void setALMReportingRules(
            List<IALMReportingRulePO> reportingRules) {
        getProjectProperties().setALMReportingRules(reportingRules);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean getIsTrackingActivated() {
        return getProjectProperties().getIsTrackingActivated();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setIsTrackingActivated(boolean isTrackingActivated) {
        getProjectProperties().setIsTrackingActivated(isTrackingActivated);
    }

    /**
     * {@inheritDoc}
     */
    public void addUsedProject(IReusedProjectPO reusedProject) {
        getProjectProperties().addUsedProject(reusedProject);
    }

    /**
     * {@inheritDoc}
     */
    public void removeUsedProject(IReusedProjectPO project) {
        getProjectProperties().removeUsedProject(project);
    }
    
    /**
     * Sets the child node's parentProjectId equal to this node's id.
     * 
     * @param childNode The node that will have its parentProjectId set.
     */
    protected void setParentProjectIdForChildNode(INodePO childNode) {
        childNode.setParentProjectId(getId());
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof IProjectPO) {
            IProjectPO proj = (IProjectPO)obj;
            return new EqualsBuilder()
                .append(getGuid(), proj.getGuid())
                .append(getMajorProjectVersion(), proj.getMajorProjectVersion())
                .append(getMinorProjectVersion(), proj.getMinorProjectVersion())
                .append(getMicroProjectVersion(), proj.getMicroProjectVersion())
                .append(getProjectVersionQualifier(), 
                        proj.getProjectVersionQualifier())
                .isEquals();
        }
        
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder()
            .append(getGuid())
            .append(getMajorProjectVersion())
            .append(getMinorProjectVersion())
            .append(getMicroProjectVersion())
            .append(getProjectVersionQualifier())
            .toHashCode();
    }

    /**
     * This method is intended for use by the persistence layer and should do 
     * nothing other than return the properties.
     * 
     * @return Returns the project properties.
     */
    @OneToOne(cascade = CascadeType.ALL, 
               fetch = FetchType.EAGER)
    @JoinColumn(name = "PROJECT_PROPERTIES", unique = true)
    public ProjectPropertiesPO getProperties() {
        return m_projectProperties;
    }
    
    /**
     * This method is intended for use by the persistence layer and should do 
     * nothing other than assign the properties.
     * 
     * @param properties The new project properties.
     */
    private void setProperties(ProjectPropertiesPO properties) {
        m_projectProperties = properties;
    }
    
    /**
     * 
     * @param properties The new project properties.
     */
    private void setProjectProperties(IProjectPropertiesPO properties) {
        setProperties((ProjectPropertiesPO)properties);
        if (getProjectProperties() != null) {
            getProjectProperties().setParentProjectId(getId());
        }
    }

    /**
     * 
     * @return the project properties.
     */
    @Transient
    public IProjectPropertiesPO getProjectProperties() {
        return getProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getToolkit() {
        return getProjectProperties().getToolkit();
    }

    /**
     * {@inheritDoc}
     */
    public void setToolkit(String toolkit) {
        getProjectProperties().setToolkit(toolkit);
    }

    /**
     * {@inheritDoc}
     */
    public void clearUsedProjects() {
        getProjectProperties().clearUsedProjects();
    }

    /**
     * 
     * @return a displayable name for the project. The returned String is of the
     *         form: [name]_[majorNumber].[minorNumber]
     */
    @Transient
    public String getDisplayName() {
        StringBuffer sb = new StringBuffer(getName());
        sb.append(IProjectPO.NAME_SEPARATOR).append(getVersionString());
        return sb.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return ProjectNameBP.getInstance().getName(getGuid());
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        //ProjectNameBP.getInstance().setName(getGuid(), name);
        log.debug(Messages.SetNameNotSupportedOnProjectPO);
    }

    /**
     * Sets parentProjectId for all existing child nodes.
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        getDataManager().setParentProjectId(projectId);
        if (getProjectProperties() != null) {
            getProjectProperties().setParentProjectId(projectId);
        }
        if (getHbmAutCont() != null) {
            getHbmAutCont().setParentProjectId(projectId);
        }
        if (getHbmTestDataCubeContPO() != null) {
            getHbmTestDataCubeContPO().setParentProjectId(projectId);
        }
        super.setParentProjectId(projectId);
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getVersionString() {
        return getProjectVersion().toString();
    }
    
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.model.NodePO#isInterfaceLocked()
     */
    @Transient
    public Boolean isReused() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public ITestDataCategoryPO getTestDataCubeCont() {
        return getHbmTestDataCubeContPO();
    }

    /**
     * 
     * @param testDataCategory The receiver's new top-level Test Data Category.
     */
    private void setHbmTestDataCubeContPO(
            ITestDataCategoryPO testDataCategory) {
        m_testdatacubecont = testDataCategory;
    }
    
    /**
     *      
     * @return Returns the test data cube container.
     */
    @OneToOne(cascade = CascadeType.ALL, 
              targetEntity = TestDataCategoryPO.class,
              fetch = FetchType.LAZY)
    @JoinColumn(name = "TDC_CONT", unique = true)
    private ITestDataCategoryPO getHbmTestDataCubeContPO() {
        return m_testdatacubecont;
    }

    /**
     * {@inheritDoc}
     */
    public void setTestResultCleanupInterval(int noOfDays) {
        getProjectProperties().setTestResultCleanupInterval(noOfDays);
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Integer getTestResultCleanupInterval() {
        return getProjectProperties().getTestResultCleanupInterval();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public ProjectVersion getProjectVersion() {
        return new ProjectVersion(getMajorProjectVersion(),
                getMinorProjectVersion(), getMicroProjectVersion(),
                getProjectVersionQualifier());
    }
    
    /** {@inheridDoc} */
    @Override
    public void addNode(INodePO childNode) {
        throw new UnsupportedOperationException(NOSUPPORT);
    }
    
    /** {@inheridDoc} */
    @Override
    public void addNode(int position, INodePO childNode) {
        throw new UnsupportedOperationException(NOSUPPORT);
    }
    
    /** {@inheridDoc} */
    @Override
    public void removeNode(INodePO childNode) {
        throw new UnsupportedOperationException(NOSUPPORT);
    }
    
    /** {@inheridDoc} */
    @Override
    public void removeAllNodes() {
        throw new UnsupportedOperationException(NOSUPPORT);
    }
    
    /**
     * The following methods establish the top-level node project structure
     * ProjectPOs have two children: the first contains the TSB top-level
     *      nodes, the second the TCB top-level nodes
     * These nodes are not special in any sense, so children can be added to them
     *      the same way as for 'normal' nodes
     */

    /** {@inheritDoc} */
    public INodePO getExecObjCont() {
        return getUnmodifiableNodeList().get(0);
    }
    
    /** {@inheritDoc} */
    public INodePO getSpecObjCont() {
        return getUnmodifiableNodeList().get(1);
    }
    
    /** {@inheritDoc} */
    public List<INodePO> getUnmodExecList() {
        return getExecObjCont().getUnmodifiableNodeList();
    }

    /** {@inheritDoc} */
    public List<INodePO> getUnmodSpecList() {
        return getSpecObjCont().getUnmodifiableNodeList();
    }
    
}
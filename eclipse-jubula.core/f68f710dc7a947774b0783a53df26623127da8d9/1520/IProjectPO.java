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
import java.util.Set;

import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @author BREDEX GmbH
 * @created 19.12.2005
 */
public interface IProjectPO extends IParamNodePO {

    /** Used between project name and version number in display strings */
    public static final String NAME_SEPARATOR = StringConstants.UNDERSCORE;
    
    /** Used between major and minor version numbers in display strings */
    public static final String VERSION_SEPARATOR = StringConstants.DOT;
    
    /** indicates disabled auto-cleanup for test result details */
    public static final int NO_CLEANUP = -1;
    
    /** default for test result details auto-cleanup in days*/
    public static final int CLEANUP_DEFAULT = 5;

    /**
     * @return Returns the autMainList.
     */
    public abstract Set<IAUTMainPO> getAutMainList();

    /**
     * Adds an AUT to a project.
     * @param aut The AUT to add.
     */
    public abstract void addAUTMain(IAUTMainPO aut);

    /**
     * Removes an AUT from a project.
     * @param aut The AUT to remove.
     */
    public abstract void removeAUTMain(IAUTMainPO aut);

    /**
     * 
     * @return the set of used projects.
     */
    public Set<IReusedProjectPO> getUsedProjects();

    /**
     * 
     * @param reusedProject The project to reuse.
     */
    public void addUsedProject(IReusedProjectPO reusedProject);
    
    /**
     * 
     * @param project The project to remove.
     */
    public void removeUsedProject(IReusedProjectPO project);

    /**
     * Clears the reused project set.
     */
    public abstract void clearUsedProjects();

    /**
     * @return Returns the markupLanguage.
     */
    public abstract String getMarkupLanguage();

    /**
     * @param markupLanguage The markupLanguage to set.
     */
    public abstract void setMarkupLanguage(String markupLanguage);

    /**
     * @return Returns the test data container.
     */
    public abstract ITestDataCategoryPO getTestDataCubeCont();
    
    /**
     * All data from this PO is available from delegate methods in IProjectPO.
     * Use this PO only for locking!
     * @return the property PO
     */
    public IProjectPropertiesPO getProjectProperties();

    /**
     * @return the metadata version of data in this project
     */
    public abstract Integer getClientMetaDataVersion();

    /**
     * @param metaDataVersion The metaDataVersion to set.
     */
    public abstract void setClientMetaDataVersion(Integer metaDataVersion);
    
    /**
     * @return the major project version of this project
     */
    public abstract Integer getMajorProjectVersion();

    /**
     * @return the minor version number of this project
     */
    public abstract Integer getMinorProjectVersion();
    
    /**
     * @return the micro version number of this project
     */
    public abstract Integer getMicroProjectVersion();
    
    /**
     * @return the qualifier version of this project
     */
    public abstract String getProjectVersionQualifier();

    /**
     * @return a String representing the version number of this project
     */
    public abstract String getVersionString();
    
    /**
     * @return a {@link ProjectVersion} representing the version of the project
     */
    public abstract ProjectVersion getProjectVersion();

    /**
     * 
     * @return a displayable name for the project. The returned String is of the
     *         form: [name]_[majorNumber].[minorNumber]
     */
    public String getDisplayName();

    /**
     * @return <code>true</code> if this project is reusable. Otherwise
     *         <code>false</code>.
     */
    public abstract boolean getIsReusable();
    
    /**
     * @param isReusable Whether the project should be reusable.
     */
    public void setIsReusable(boolean isReusable);
    
    /**
     * @return <code>true</code> if this project is protected. Otherwise
     *         <code>false</code>.
     */
    public abstract boolean getIsProtected();
    
    /**
     * @param isProtected Whether the project should be protected.
     */
    public void setIsProtected(boolean isProtected);
    
    /**
     * @return the ALM reporting rules of this project
     */
    public List<IALMReportingRulePO> getALMReportingRules();

    /**
     * @param reportingRules the ALM reporting rules of this project
     */
    public void setALMReportingRules(
            List<IALMReportingRulePO> reportingRules);
        
    /**
     * @return Whether changes to the project should be stored.
     */
    public boolean getIsTrackingActivated();
    
    /**
     * @param isTrackingActivated Whether changes to the project should be stored.
     */
    public void setIsTrackingActivated(boolean isTrackingActivated);

    /**
     * @return the id of the toolkit of this project
     */
    public abstract String getToolkit();
       
    /**
     * @param toolkit the id of the toolKit type of this project
     */
    public abstract void setToolkit(String toolkit);

    /**
     * @return the the number of days to clean the results for
     */
    public abstract Integer getTestResultCleanupInterval();
    
    /**
     * @param noOfDays the number of days to clean the results for
     */
    public abstract void setTestResultCleanupInterval(int noOfDays);
    
    /**
     * 
     * @return Returns the AutCont.
     */
    public IAUTContPO getAutCont();
    
    /**
     * @return Returns the container of top-level Test Suite Tree nodes
     */
    public INodePO getExecObjCont();
    
    /**
     * @return Returns the container of top-level Test Case Tree nodes
     */
    public INodePO getSpecObjCont();

    /**
     * @return The unmodifiable list of top-level Test Suite Tree nodes
     */
    public List<INodePO> getUnmodExecList();

    /**
     * @return The unmodifiable list of top-level Test Case Tree nodes
     */
    public List<INodePO> getUnmodSpecList();
    
}
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * class only relevant for test execution
 * 
 * @author BREDEX GmbH
 * @created 12.10.2004
 */
@Entity
@DiscriminatorValue(value = "E")
class ExecTestCasePO extends TestCasePO implements 
    IEventHandlerContainer, IExecTestCasePO {
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(ExecTestCasePO.class);
    
    /**
     * reference to SpecTestCasePO from specification tree
     */
    private transient ISpecTestCasePO m_cachedSpecTestCase = null;
    
    /**
     * <code>m_hasReferencedTD</code> determines, if the execTC has own testdata 
     * or referenced testdata from corresponding specTestCase
     */
    private boolean m_hasReferencedTD = true;

    /**
     * <code>m_refEventTcMap</code> manages the flags to signal, if an event-
     * handler for a given eventType is overwritten or not
     * key: eventType, value: flag
     */
    private Map<String, Boolean> m_refEventTcMap = 
        new HashMap<String, Boolean>(
                IEventHandlerContainer.MAX_NUMBER_OF_EVENT_HANDLER);
    
    /**
     * <code>m_compNameMap</code> manages the mapping between old component name
     * and new (overwritten) component name
     */
    private Map<String, ICompNamesPairPO> m_compNamesMap;

    /** GUID of the parent project */
    private String m_projectGuid;

    /** GUID of the referenced test case */
    private String m_specTestCaseGuid;
    
    /**
     * only for Persistence (JPA / EclipseLink)
     */
    ExecTestCasePO() {
        // only for Persistence (JPA / EclipseLink)
        m_compNamesMap = new HashMap<String, ICompNamesPairPO>();
    }   
    
    /**
     * constructor
     * @param specTestCase reference to specified testcase
     * @param isGenerated indicates whether this node has been generated
     */
    ExecTestCasePO(ISpecTestCasePO specTestCase, boolean isGenerated) {
        super(null, isGenerated); // null is correct, do not set a name here!
        setSpecTestCase(specTestCase);
        m_compNamesMap = new HashMap<String, ICompNamesPairPO>();
    }

    /**
     * constructor when the GUID is already assigned
     * @param specTestCase reference to specified testcase
     * @param guid the GUID for this exec test case
     * @param isGenerated indicates whether this node has been generated
     */
    ExecTestCasePO(ISpecTestCasePO specTestCase, String guid, 
            boolean isGenerated) {
        super(null, guid, isGenerated); // null is correct, 
                                        // do not set a name here!
        setSpecTestCase(specTestCase);
        m_compNamesMap = new HashMap<String, ICompNamesPairPO>();
    }
      
    /**
     * constructor when the GUID is already assigned and the referenced testcase
     * was not imported with this exec testcase
     * @param specTestCaseGuid GUID reference to specified testcase
     * @param projectGuid GUID reference to specified testcase's parent project
     * @param guid the GUID for this exec test case
     * @param isGenerated indicates whether this node has been generated
     */
    ExecTestCasePO(String specTestCaseGuid, String projectGuid, String guid, 
            boolean isGenerated) {
        super(null, guid, isGenerated); // null is correct, 
                                        // do not set a name here!
        setSpecTestCaseGuid(specTestCaseGuid);
        setProjectGuid(projectGuid);
        m_compNamesMap = new HashMap<String, ICompNamesPairPO>();
    }

    /**
     * constructor when a new GUID must be assigned and the referenced testcase
     * was not imported with this exec testcase
     * @param specTestCaseGuid GUID reference to specified testcase
     * @param projectGuid GUID reference to specified testcase's parent project
     * @param isGenerated indicates whether this node has been generated
     */
    ExecTestCasePO(String specTestCaseGuid, String projectGuid, 
            boolean isGenerated) {
        super(null, isGenerated); // null is correct, do not set a name here!
        setSpecTestCaseGuid(specTestCaseGuid);
        setProjectGuid(projectGuid);
        m_compNamesMap = new HashMap<String, ICompNamesPairPO>();
    }

    /**
     * Gets the name of this node or, if na name is set, 
     * gets the name of the SpecTestCase.
     * {@inheritDoc}
     * @return
     */
    @Transient
    public String getName() {
        String name = super.getName();
        if (name == null || name.equals(StringConstants.EMPTY)) {
            if (getSpecTestCase() != null) {
                return getSpecTestCase().getName();
            }
            
            String reusedProjectName = 
                ProjectNameBP.getInstance().getName(m_projectGuid);
            if (reusedProjectName != null && reusedProjectName.length() != 0) {
                return NLS.bind(
                        Messages.ExecTestCasePOMissingReferenceWithProjectName, 
                        reusedProjectName);
            }

            return Messages.ExecTestCasePOMissingReference;
        }
        return name;
    }

    

    /**
     * Gets the comment of this node or, if no comment is set,
     * gets the comment of the SpecTestCase.
     * {@inheritDoc}
     * @return
     */
    @Transient
    public String getComment() {
        String comment = super.getComment();
        if ((comment == null || comment.equals(StringConstants.EMPTY))
                && getSpecTestCase() != null) {
            return getSpecTestCase().getComment();
        }
        return comment;
    }
    
    

    /**
     * {@inheritDoc}
     * @return
     */
    @Transient
    public String getDataFile() {
        String dataFile = super.getDataFile();
        if ((dataFile == null || dataFile.equals(StringConstants.EMPTY))
                && getSpecTestCase() != null) {
         
            return getSpecTestCase().getDataFile();
        }
        return dataFile;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public ISpecTestCasePO getSpecTestCase() {

        if (m_cachedSpecTestCase != null) {
            return m_cachedSpecTestCase;
        }

        ISpecTestCasePO specTc = null;
        // Search by GUID
        
        if (getParentProjectId() == null) {
            return null;
        }
        String specGuid = getSpecTestCaseGuid();
        
        try {
            if (getProjectGuid() == null
                    || getProjectGuid().equals(
                            ProjectPM.getGuidOfProjectId(
                                    getParentProjectId()))) {

                // Referenced TC is in the same project
                specTc = NodePM.getSpecTestCase(getParentProjectId(), 
                    specGuid);
                
            } else {
                // Referenced TC is in a different project
                Set<IReusedProjectPO> reusedProjects = 
                    new HashSet<IReusedProjectPO>();
                reusedProjects
                        .addAll(
                        ProjectPM.loadReusedProjectsRO(
                                getParentProjectId()));
                specTc = NodePM.getSpecTestCase(
                        reusedProjects, getProjectGuid(), 
                        specGuid);
            }
        } catch (JBException e) {
            LOG.warn("Could not retrieve referenced Test Case.", e); //$NON-NLS-1$
        }

        m_cachedSpecTestCase = specTc;
        return specTc;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setSpecTestCase(ISpecTestCasePO specTestCase) {

        if (specTestCase != null) {
            if (getId() == null) {
                // New ExecTestCase. If it's being imported, then we won't find
                // the RefSpecTestCase in the database, so we have to cache it.
                m_cachedSpecTestCase = specTestCase;
            }
            setSpecTestCaseGuid(specTestCase.getGuid());
            if (specTestCase.getParentProjectId() != getParentProjectId()) {
                try {
                    setProjectGuid(ProjectPM.loadProjectById(
                        specTestCase.getParentProjectId()).getGuid());
                } catch (JBException e) {
                    // FIXME zeb Error occurred while trying to set project guid for exec tc
                }
            } else {
                setProjectGuid(null);
            }
        }
        
    }

    /** 
     * {@inheritDoc}
     * ExecTestCasePO doesn't have an own parameter list
     * it uses generally the parameter from associated specTestCase
     */
    @Transient
    public List<IParamDescriptionPO> getParameterList() {
        if (getSpecTestCase() != null) {
            return getSpecTestCase().getParameterList();
        }
        
        return new ArrayList<IParamDescriptionPO>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public ListIterator<IParamDescriptionPO> getParameterListIter() {
        if (getSpecTestCase() != null) {
            return getSpecTestCase().getParameterListIter();
        }
        List<IParamDescriptionPO> emptyList = Collections.emptyList();
        return emptyList.listIterator();
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public int getParameterListSize() {
        if (getSpecTestCase() != null) {
            return getSpecTestCase().getParameterListSize();
        }
        
        return 0;

    }
    
    /**
     * {@inheritDoc}
     * @return the TDManagerPO from the depending SpecTestCasePO or the own if
     * TestData are overwritten.
     */
    @Transient
    public ITDManager getDataManager() {
        if (getReferencedDataCube() == null 
                && getHasReferencedTD() && getSpecTestCase() != null) {
            return getSpecTestCase().getDataManager();
        }
        
        return super.getDataManager();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasReferencedTestData() {
        return super.hasReferencedTestData() 
            || (getHasReferencedTD() && getSpecTestCase() != null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public IParameterInterfacePO getReferencedDataCube() {
        if ((getHasReferencedTD() && getSpecTestCase() != null)) {
            return getSpecTestCase().getReferencedDataCube();
        }
        return super.getReferencedDataCube();
    }
    
    /**
     * @return the DataManager of this ExecTestCase even if getHasReferencedID()
     * returns true. 
     */
    @Transient
    private ITDManager getOwnDataManager() {
        return super.getDataManager();
    }

    /**
     * Sets the data manager and changes the <code>hasReferencedTD</code> flag
     * to <code>false</code>, so that {@link #getHasReferencedTD()} will
     * return <code>false</code> after this call.
     * 
     * {@inheritDoc}
     */
    public void setDataManager(ITDManager dataManager) {
        super.setDataManager(dataManager);
        setHasReferencedTD(false);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        super.setParentProjectId(projectId);
        for (ICompNamesPairPO compNamesPair : getCompNamesPairs()) {
            compNamesPair.setParentProjectId(projectId);
        }
    }

    /**
     * Resolves the reference to the test data manager of the associated
     * specification test case node. This method creates a deep copy of the test
     * data manager an sets it as it's own manager.
     * 
     * @return The new test data manager
     */
    public ITDManager resolveTDReference() {
        if (getHasReferencedTD()) {
            final ITDManager specTDMan = getSpecTestCase().getDataManager();
            final ITDManager execTDMan = super.getHbmDataManager();
            final ITDManager modifiedExecTDMan = specTDMan.deepCopy(
                    execTDMan);
            this.setDataManager(modifiedExecTDMan);
        }
        return getDataManager();
    }
    
    /** {@inheritDoc} */
    @Transient
    public Iterator<INodePO> getNodeListIterator() {
        ISpecTestCasePO specTestCase = getSpecTestCase();
        if (specTestCase != null) {
            return specTestCase.getNodeListIterator();
        }
        
        List<INodePO> emptyList = Collections.emptyList();
        return emptyList.iterator();
    }
    
    /** {@inheritDoc} */
    @Transient
    public Iterator<INodePO> getAllNodeIter() {
        ISpecTestCasePO specTestCase = getSpecTestCase();
        if (specTestCase != null) {
            return specTestCase.getAllNodeIter();
        }
        
        List<INodePO> emptyList = Collections.emptyList();
        return emptyList.iterator();
    }
    
    /** {@inheritDoc} */
    @Transient
    public int getNodeListSize() {
        if (getSpecTestCase() != null) {
            return getSpecTestCase().getNodeListSize();
        }
        
        return 0;
    }
        
    /**
     * only for Persistence (JPA / EclipseLink)
     * 
     * @return Returns the m_hasReferencedTD.
     */
    @Basic
    @Column(name = "REF_FLAG")
    @Index(name = "PI_NODE_REF_FLAG")
    public boolean getHasReferencedTD() {
        return m_hasReferencedTD;
    }
    /**
     * only for Persistence (JPA / EclipseLink)
     * @param hasReferencedTD The m_hasReferencedTD to set.
     */
    public void setHasReferencedTD(boolean hasReferencedTD) {
        m_hasReferencedTD = hasReferencedTD;
    }
    
    /**
     * public getter for unmodifiable map of eventExecTestCases
     * @return map ov eventExecTestCases
     */
    @Transient
    public Map<String, IEventExecTestCasePO> getEventMap() {
        return Collections.unmodifiableMap(
            getSpecTestCase().getEventExecTcMap());
    }
    
    /** 
     * {@inheritDoc}
     */
    public IEventExecTestCasePO getEventExecTC(String eventType) {
        return (getFlagForRefEventTc(eventType)) 
            ? getSpecTestCase().getEventExecTC(eventType) 
            : getEventExecTC(eventType);   
    }

    /**
     * only for Persistence (JPA / EclipseLink)
     * @return Returns the refEventTcMap.
     */
    @Transient
    private Map<String, Boolean> getRefEventTcMap() {
        return m_refEventTcMap;
    }

    /**
     * only for Persistence (JPA / EclipseLink)
     * @param refEventTcMap The refEventTcMap to set.
     */
    @SuppressWarnings("unused")
    private void setRefEventTcMap(Map<String, Boolean> refEventTcMap) {
        m_refEventTcMap = refEventTcMap;
    }

    /**
     * @param eventType eventType, for which to set the flag
     * @param flag signals, if the associated eventhandler for given eventType 
     * will be reused (flag = true) or overwritten (flag = false)
     */
    public void setFlagForRefEventTc(String eventType, boolean flag) {
        getRefEventTcMap().put(eventType, Boolean.valueOf(flag));
    }
    
    /**
     * @param eventType eventType, for which to get the flag
     * @return flag, which signals, if the eventhandler for given eventType is
     * referenced from associated specTestCase or overwritten
     */
    public boolean getFlagForRefEventTc(String eventType) {
        Boolean value = m_refEventTcMap.get(eventType);
        return (value == null) ? true : value.booleanValue();
    }
    /**
     * only for Persistence (JPA / EclipseLink)
     * 
     * @return Returns the compNameMap.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, 
               fetch = FetchType.EAGER, targetEntity = CompNamesPairPO.class)
    @MapKeyColumn(name = "MK_EXECTC_COMPNAMES")
    @JoinColumn(name = "FK_EXECTC")
    @BatchFetch(value = BatchFetchType.JOIN)
    private Map<String, ICompNamesPairPO> getHbmCompNamesMap() {
        return m_compNamesMap;
    }
    
    /**
     * Removes Comp Names Pairs
     * @param sess the session
     */
    private void removeCompNamesPairs(EntityManager sess) {
        Query q = sess.createNativeQuery("delete from COMP_NAME_PAIRS where FK_EXECTC = ?1"); //$NON-NLS-1$
        q.setParameter(1, getId()).executeUpdate();
    }
    
    /**
     * only for Persistence (JPA / EclipseLink)
     * 
     * @param compNameMap The compNameMap to set.
     */
    @SuppressWarnings("unused")
    private void setHbmCompNamesMap(Map<String, ICompNamesPairPO> compNameMap) {
        m_compNamesMap = compNameMap;
    }
    /**
     * Adds the component name pair to the internal map if the pair doesn't
     * exist.
     * 
     * @param pair
     *            The component name pair
     */
    public void addCompNamesPair(ICompNamesPairPO pair) {
        String key = pair.getFirstName();
        if (!getHbmCompNamesMap().containsKey(key)) {
            getHbmCompNamesMap().put(key, pair);
            pair.setParentProjectId(getParentProjectId());
        }
    }
    /**
     * Removes the component name pair with the passed first name from the
     * internal map.
     * 
     * @param firstName
     *            The first name
     */
    public void removeCompNamesPair(String firstName) {
        getHbmCompNamesMap().remove(firstName);
    }
    /**
     * Gets the component name pair with the past first name.
     * 
     * @param firstName
     *            The first name
     * @return The component name pair or <code>null</code>, if the internal
     *         map doesn't contain a component name pair with the passed first
     *         name
     */
    public ICompNamesPairPO getCompNamesPair(String firstName) {
        return getHbmCompNamesMap().get(firstName);
    }
    /**
     * @return An unmodifyable list of all component name pairs
     */
    @Transient
    public Collection<ICompNamesPairPO> getCompNamesPairs() {
        return Collections.unmodifiableCollection(
            getHbmCompNamesMap().values());
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Transient
    public String getRealName() {
        return super.getName();
    }

    /**
     *    
     * @return the GUID of the parent Project of the referenced Test Case, or 
     *         <code>null</code> if the referenced Test Case has the same parent
     *         Project as the receiver.
     */
    @Basic
    @Column(name = "PARENT_PROJ_GUID")
    @Index(name = "PI_NODE_PARENT_PROJECT_GUID")
    public String getProjectGuid() {
        return m_projectGuid;
    }

    /**
     *    
     * @return the GUID of the referenced test case.
     */
    @Basic
    @Column(name = "SPEC_TC_GUID")
    @Index(name = "PI_NODE_SPEC_TC_GUID")
    public String getSpecTestCaseGuid() {
        return m_specTestCaseGuid;
    }

    /**
     * For Persistence (JPA / EclipseLink).
     * @param projectGuid The GUID of the parent project of the referenced test case.
     */
    public void setProjectGuid(String projectGuid) {
        m_projectGuid = projectGuid;
    }
    
    /**
     * For Persistence (JPA / EclipseLink).
     * @param testCaseGuid The GUID of the referenced test case.
     */
    private void setSpecTestCaseGuid(String testCaseGuid) {
        m_specTestCaseGuid = testCaseGuid;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getToolkitLevel() {
        return getSpecTestCase().getToolkitLevel();
    }

    /**
     * {@inheritDoc}
     */
    public void setToolkitLevel(String toolkitLevel) {
        final ISpecTestCasePO specTestCase = getSpecTestCase();
        if (specTestCase != null) {
            specTestCase.setToolkitLevel(toolkitLevel);            
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void clearCachedSpecTestCase() {
        m_cachedSpecTestCase = null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCachedSpecTestCase(ISpecTestCasePO spec) {
        if (ObjectUtils.equals(getSpecTestCaseGuid(), spec.getGuid())) {
            m_cachedSpecTestCase = spec;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTestDataComplete() {
        if (getSpecTestCase() == null) { // missing referenced Test Case
            return false;
        }

        return super.isTestDataComplete();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public IParamDescriptionPO getParameterForName(String paramName) {
        if (getSpecTestCase() != null) {
            return getSpecTestCase().getParameterForName(paramName);
        }
        
        return null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public IParamDescriptionPO getParameterForUniqueId(String uniqueId) {
        if (getSpecTestCase() != null) {
            return getSpecTestCase().getParameterForUniqueId(uniqueId);
        }
        
        return null;
    }

    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.model.ITDManager#synchronizeParameterIDs()
     */
    public void synchronizeParameterIDs() {
        if (getReferencedDataCube() == null) {
            ITDManager tdMan = getDataManager();
            List<IParamDescriptionPO> params = getParameterList();
            List<String> origParamIDs = new ArrayList<String>();
            List<String> header = new ArrayList<String>(tdMan.getUniqueIds());
            for (IParamDescriptionPO param : params) {
                origParamIDs.add(param.getUniqueId());
            }
            // add ids for new parameters
            for (String origId : origParamIDs) {
                if (!header.contains(origId)) {
                    tdMan.addUniqueId(origId);
                }
                
            }
            // remove ids of deleted parameters inclusive data
            header = new ArrayList<String>(tdMan.getUniqueIds());
            for (String tdManParamId : header) {
                if (!origParamIDs.contains(tdManParamId)) {
                    tdMan.removeUniqueId(tdManParamId);
                }
            }
        }
    }

    /**
     * Checks if there are unused Test Data in this TDManagerPO 
     * (Data column without a parameter).
     * @return true if there are unused Test Data in this TDManagerPO., 
     * false otherwise.
     */
    public boolean checkHasUnusedTestData() {
        if (getReferencedDataCube() == null) {
            for (String colId : getDataManager().getUniqueIds()) {
                boolean hasColIdParam = false;
                for (ListIterator<IParamDescriptionPO> paramListIter =
                        getParameterListIter(); paramListIter.hasNext();) {
                    IParamDescriptionPO paramDescr = paramListIter.next();
                    if (colId.equals(paramDescr.getUniqueId())) {
                        hasColIdParam = true;
                        break;
                    }
                }
                if (!hasColIdParam) {
                    return true;
                }
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public void goingToBeDeleted(EntityManager sess) {
        super.goingToBeDeleted(sess);
        removeCompNamesPairs(sess);
    }

}

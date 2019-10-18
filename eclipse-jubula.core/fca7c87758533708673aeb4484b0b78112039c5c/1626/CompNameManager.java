/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.businessprocess;

import java.util.HashSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemType;
import org.eclipse.jubula.client.core.businessprocess.treeoperations.CountCompNameUsage;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.CompNamePM.SaveCompNamesData;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PersistenceUtil;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.exception.JBException;

/**
 * 
 * The object managing Component Names
 * 
 * The ComponentNamePOs are managed by the main session
 * After any changes made to component names, the committer must notify this Manager
 * So that the Manager can refresh the stored Component Names
 * Of course only CNs belonging to the current project can change, CNs belonging to reused projects never change
 * 
 * Some of the functionality of this class comes from the ComponentNamesBP class
 * 
 * @author BREDEX GmbH
 * @created 15.Jul, 2016
 */
public class CompNameManager implements IComponentNameCache {

    /** i18n key for the "unknown" component type */
    public static final String UNKNOWN_COMPONENT_TYPE = "guidancer.abstract.Unknown"; //$NON-NLS-1$

    /** Indicates that a logical name is used in a reused project */ 
    private static final Integer IN_REUSED = -2;
    
    /** Indicates that a logical name is used in the current project, and is saved to the DB */
    private static final Integer IN_CURRENT = -1;
    
    /** The singleton instance */
    private static CompNameManager instance = null;
    
    /** the currently open project (if there is any) - to avoid repetitive calling of the GeneralStorage */
    private IProjectPO m_project;
    
    /** Managed Component Names by their GUIDs */
    private HashMap<String, IComponentNamePO> m_compNames;
    
    /** map: guid => number of in-DB usage of Component Name */
    private MapCounter m_usage;

    /** map: logical names => place of usage: current project or reused project */
    // This is used to keep track of not-yet-saved component names
    private HashMap<String, Integer> m_logicalNames;
    
    /** All CN type problems */
    private Map<String, ProblemType> m_allTypeProblems;
    
    /**
     * Singleton Constructor.
     */
    private CompNameManager() {
        clear();
    }
    
    /**
     * @return The singleton instance.
     */
    public static final CompNameManager getInstance() {
        if (instance == null) {
            instance = new CompNameManager();
        }
        return instance;
    }
    
    /**
     * Clears all Component Names
     */
    public void clear() {
        m_compNames = new HashMap<String, IComponentNamePO>(20);
        m_usage = new MapCounter();
        m_logicalNames = new HashMap<String, Integer>(20);
        m_project = GeneralStorage.getInstance().getProject();
    }
    
    /**
     * reads all paramNames of the current Project and its reused Projects 
     * from database into names map
     * @throws PMException in case of an (unexpected) DB access problem
     */
    public final void init() throws PMException {
        clear();
        initCompNamesTransitive(m_project.getId(), new HashSet<Long>(5));
        countUsage();
        recalculateTypes();
    }
    
    /**
     * reads all Component Names of the given Project and its reused Projects 
     * from the database
     * @param projectID an IProjectPO id
     * @param loadedProjectIds Accumulated IDs of reused projects that have 
     *                         been loaded.
     * @throws PMException in case of an (unexpected) DB access problem
     */
    private void initCompNamesTransitive(Long projectID, 
        Set<Long> loadedProjectIds) throws PMException { 
        
        if (projectID == null) {
            return;
        }
        readCompNamesForProjectID(projectID);
        for (IReusedProjectPO usedProj : ProjectPM
                .getReusedProjectsForProject(projectID)) {
            final String reusedGuid = usedProj.getProjectGuid();
            final Integer reuseMajVers = usedProj.getMajorNumber();
            final Integer reuseMinVers = usedProj.getMinorNumber();
            final Integer reuseMicVers = usedProj.getMicroNumber();
            final String reuseQualVers = usedProj.getVersionQualifier();
            try {
                final Long usedProjId = ProjectPM
                    .findProjectIDByGuidAndVersion(reusedGuid, reuseMajVers, 
                        reuseMinVers, reuseMicVers, reuseQualVers);
                if (usedProjId != null 
                        && loadedProjectIds.add(usedProjId)) {
                    initCompNamesTransitive(usedProjId,
                            loadedProjectIds);
                }
            } catch (JBException e) {
                // Continue! Maybe the Project is not present in DB.
            }
        }
    }
    
    /**
     * Refreshes Component Names of a given reused project
     * @param projectId the ID of the project
     * @throws PMException
     */
    public void refreshNames(Long projectId) throws PMException {
        readCompNamesForProjectID(projectId);
    }
    
    /**
     * reads all paramNames of the Project with the given ID from database 
     * into names map
     * @param projId the Project ID.
     * @throws PMException PMException in case of any db problem
     */
    private void readCompNamesForProjectID(Long projId) throws PMException {
        List<IComponentNamePO> names = CompNamePM.readAllCompNamesRO(projId);
        boolean currProj = false;
        if (m_project != null) {
            currProj = projId.equals(m_project.getId());
        }
        Integer pos = currProj ? IN_CURRENT : IN_REUSED;
        String name;
        for (IComponentNamePO compNamePO : names) {
            name = compNamePO.getName();
            m_compNames.put(compNamePO.getGuid(), compNamePO);
            if (!m_logicalNames.containsKey(name)
                    || m_logicalNames.get(name).equals(IN_CURRENT)) {
                m_logicalNames.put(name, pos);
            }
        }
    }
    
    /**
     * Determines for each Component Name how many times it is used in the current project
     */
    public void countUsage() {
        m_usage.clear();
        CountCompNameUsage op = new CountCompNameUsage(m_usage);
        TreeTraverser traverser = new TreeTraverser(m_project, op, true, false);
        traverser.setTraverseReused(false);
        traverser.traverse(true);
        for (ITestSuitePO ts : TestSuiteBP.getListOfTestSuites(m_project)) {
            traverser = new TreeTraverser(ts, op);
            traverser.traverse(true);
        }
        countCompNamesUsedInAssocs();
    }
    
    /**
     * Returns the array of used Component Names
     * @param projId The id of the project
     * @return the used Component Names
     */
    public Object[] getUsedCompNames(Long projId) {
        ArrayList<IComponentNamePO> res = new ArrayList<>(m_compNames.size());
        for (IComponentNamePO cN : m_compNames.values()) {
            if (cN.getParentProjectId().equals(projId)
                    && cN.getReferencedGuid() == null
                    && m_usage.get(cN.getGuid()) != null
                    && m_usage.get(cN.getGuid()) > 0) {
                res.add(cN);
            }
        }
        return res.toArray();
    }

    /**
     * Counts all Component Name usages in associations
     */
    private void countCompNamesUsedInAssocs() {
        Collection<IAUTMainPO> auts = m_project.getAutMainList();
        String resGuid;
        Integer count;
        for (IAUTMainPO aut : auts) {
            for (IObjectMappingAssoziationPO assoc 
                    : aut.getObjMap().getMappings()) {
                
                if (assoc.getTechnicalName() != null) {
                    for (String guid : assoc.getLogicalNames()) {
                        resGuid = resolveGuid(guid);
                        count = m_usage.get(resGuid);
                        if (count == null) {
                            count = 0;
                        }
                        m_usage.put(resGuid, count + 1);
                    }
                }
            }
        }
    }
    
    /**
     * Returns the array of unused Component Names
     * @param projId The Project Id
     * @return the unused Component Names
     */
    public Object[] getUnusedCompNames(Long projId) {
        ArrayList<IComponentNamePO> res = new ArrayList<>(m_compNames.size());
        for (IComponentNamePO cN : m_compNames.values()) {
            if (cN.getReferencedGuid() == null
                    && cN.getParentProjectId().equals(projId)
                    && (m_usage.get(cN.getGuid()) == null
                            || m_usage.get(cN.getGuid()).equals(0))) {
                res.add(cN);
            }
        }
        return res.toArray();
    }
    
    /*
     * Backward compatibility methods
     */
    
    /** {@inheritDoc} */
    public String getNameByGuid(String guid) {
        IComponentNamePO compNamePo = getResCompNamePOByGuid(guid);
        if (compNamePo != null) {
            return compNamePo.getName();
        }
        return guid;
    }
    
    /** {@inheritDoc} */
    public String getGuidForName(String name) {
        for (IComponentNamePO cN : m_compNames.values()) {
            if (cN.getName().equals(name)) {
                return resolveGuid(cN.getGuid());
            }
        }
        return null;
    }
    
    /** {@inheritDoc} */
    public String getGuidForName(String name, Long parentProjectId) {
        for (IComponentNamePO cN : m_compNames.values()) {
            if (cN.getName().equals(name)
                    && cN.getParentProjectId().equals(parentProjectId)) {
                return cN.getGuid();
            }
        }
        return null;
    }
    
    /**
     * New methods
     */
    
    /**
     * Returns the resolved ComponentNamePO identified by the guid
     * No need to provide ProjectId, because CNPOs are uniquely identified by their guids within a running Jubula
     * @param guid the guid
     * @return the ComponentNamePO
     */
    public IComponentNamePO getResCompNamePOByGuid(String guid) {
        return m_compNames.get(resolveGuid(guid));
    }
    
    /**
     * Decides whether the logical name is used anywhere
     *    (either in the project, its reused projects or in an editor)
     * @param name the name
     * @return whether the logical name is used anywhere
     */
    public boolean isLogNameUsed(String name) {
        return m_logicalNames.containsKey(name);
    }

    /**
     * Creates a Component Name with the given parameters
     * Should only be called if there is no Component Name with the given guid in the DB
     * @param guid the guid
     * @param name the name
     * @param type the type
     * @param ctx the creation context
     * @return the new Component Name
     */
    public IComponentNamePO createCompNamePO(String guid, String name,
            String type, CompNameCreationContext ctx) {
        IComponentNamePO cN = PoMaker.createComponentNamePO(guid, name, type,
                ctx, m_project.getId());

        return cN;
    }
    
    /**
     * Creates a new Component Name and persists it to the DB
     * @param name the name of the Component Name
     * @param type the type of the Component Name
     * @param ctx the creation context
     * @return the ComponentNamePO or null
     */
    public IComponentNamePO createAndPersistCompNamePO(String name, String type,
            CompNameCreationContext ctx)
                    throws PMException, ProjectDeletedException {
        // no comp name changed event is sent out, because the editors do not care about comp names created through the Browser
        IComponentNamePO cN = null;
        EntityManager sess = null;
        String guid = PersistenceUtil.generateUUID();
        try {
            cN = PoMaker.createComponentNamePO(guid, name, type,
                    ctx, m_project.getId());
            sess = Persistor.instance().openSession();
            EntityTransaction tx = Persistor.instance().getTransaction(sess);
            sess.persist(cN);
            Persistor.instance().commitTransaction(sess, tx);
        } finally {
            Persistor.instance().dropSession(sess);
        }
        cN = GeneralStorage.getInstance().getMasterSession().merge(cN);
        cN.setComponentType(type);
        m_compNames.put(guid, cN);
        m_logicalNames.put(name, IN_CURRENT);
        return cN;
    }
            
    /**
     * Renames the given Component Name
     * @param toRename the ComponentNamePO
     * @param newName the new name
     */
    public void renameCompName(IComponentNamePO toRename,
            String newName) throws PMException,
        ProjectDeletedException {
        String guid = toRename.getGuid();
        String oldName = toRename.getName();
        IComponentNamePO cN = getResCompNamePOByGuid(guid);
        if (cN != null) {
            cN = PoMaker.cloneCompName(cN);
            EntityManager sess = null;
            try {
                sess = Persistor.instance().openSession();
                EntityTransaction tx = Persistor.instance().
                        getTransaction(sess);
                cN.setName(newName);
                sess.merge(cN);
                Persistor.instance().commitTransaction(sess, tx);
            } finally {
                Persistor.instance().dropSession(sess);
            }
            GeneralStorage.getInstance().getMasterSession().
            refresh(getResCompNamePOByGuid(guid));
        }
        Integer pos = m_logicalNames.get(oldName);
        if (!pos.equals(IN_REUSED)) {
            m_logicalNames.remove(oldName);
            m_logicalNames.put(newName, pos);
        } else {
            m_logicalNames.put(newName, IN_CURRENT);
        }
        DataEventDispatcher.getInstance().fireDataChangedListener(
                toRename, DataState.Renamed, UpdateState.all);
    }
    
    /**
     * Deletes the managed Component Names
     * @param toDel the Comp Names to delete
     * @throws PMException 
     * @throws ProjectDeletedException
     */
    public void deleteCompNames(Set<IComponentNamePO> toDel) 
            throws PMException, ProjectDeletedException {
        // is only called by the Browser, which contains managed Component Names
        EntityManager s = null;
        ArrayList<IComponentNamePO> deleted = new ArrayList<>(toDel.size());
        try {
            s = Persistor.instance().openSession();
            EntityTransaction tx = Persistor.instance()
                    .getTransaction(s); 
            Persistor.instance().lockPOSet(s, toDel);
            for (IComponentNamePO compName : toDel) {
                // safer to check if another user started to use the CN
                if (CompNamePM.getNumberOfUsages(s, m_project.getId(),
                        compName.getGuid()) == 0) {
                    deleted.add(compName);
                    s.remove(s.merge(compName));
                }
            }
            Persistor.instance().commitTransaction(s, tx);
        } finally {
            Persistor.instance().dropSession(s);
        }

        EntityManager main = GeneralStorage.getInstance().getMasterSession();
        IComponentNamePO comp;
        Integer num;
        ArrayList<DataChangedEvent> events = new ArrayList<>(deleted.size());
        for (IComponentNamePO compName : deleted) {
            m_compNames.remove(compName.getGuid());
            m_usage.getCounter().remove(compName.getGuid());
            num = m_logicalNames.get(compName.getName());
            if (num != null && !num.equals(IN_REUSED)) {
                m_logicalNames.remove(compName.getName());
            }
            main.detach(compName);
            
            events.add(new DataChangedEvent(compName, DataState.Deleted,
                    UpdateState.all));
        }
        DataEventDispatcher.getInstance().fireDataChangedListener(
                events.toArray(new DataChangedEvent[0]));
    }
    
    /**
     * Should be called every time right after a successful commit changing Component Names
     * @param saveData the data of the save
     */
    public void compNamesChanged(SaveCompNamesData saveData) {
        List<IComponentNamePO> changes = saveData.getDBVersions();
        // Refreshing the managed Component Names
        IComponentNamePO cN;
        String guid;
        String name;
        Integer use;
        EntityManager sess = GeneralStorage.getInstance().getMasterSession();
        for (IComponentNamePO changed : changes) {
            guid = changed.getGuid();
            cN = m_compNames.get(guid);
            if (cN == null) {
                cN = sess.find(changed.getClass(), changed.getId());
                m_compNames.put(guid, cN);
                if (m_logicalNames.get(cN.getName()) == null) {
                    m_logicalNames.put(cN.getName(), IN_CURRENT);
                }
            } else {
                name = cN.getName();
                sess.refresh(cN);
                if (!name.equals(cN.getName())) {
                    // the CN is renamed - we have to update our logical names
                    Integer pos = m_logicalNames.get(name);
                    if (pos != null && pos.equals(IN_CURRENT)) {
                        m_logicalNames.remove(name);
                    }
                    pos = m_logicalNames.get(cN.getName());
                    if (pos == null) {
                        m_logicalNames.put(cN.getName(), IN_CURRENT);
                    }
                    // we let all the editors know that the CN was renamed
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                            cN, DataState.Renamed, UpdateState.all);
                }
            }
        }
        countUsage();
        recalculateTypes();
    }
    
    /**
     * Returns the resolved version of the guid
     * @param guid the guid to resolve
     * @return the resolved guid
     */
    public String resolveGuid(String guid) {
        if (guid == null) {
            return guid;
        }
        String curr = guid;
        IComponentNamePO currPO = m_compNames.get(guid);
        // Hopefully there are no chains with length over 1000...
        int num = 0;
        while (currPO != null && currPO.getReferencedGuid() != null
                && num < 1000) {
            curr = currPO.getReferencedGuid();
            currPO = m_compNames.get(guid);
            num++;
        }
        return curr;
    }
    
    /**
     * Returns the resolved logical name of a Component Name
     * @param guid the guid of the Component Name
     * @return the resolved logical name or the guid if Component Name does not exist
     */
    public String getResLogNameByGuid(String guid) {
        IComponentNamePO cN = getResCompNamePOByGuid(guid);
        if (cN == null) {
            return guid;
        }
        return cN.getName();
    }
    
    /** {@inheritDoc} */
    public void updateStandardMapperAndCleanup(Long projectId) {
        // Do nothing
    }
    
    /**
     * Returns the usage counter for a Component Name
     * @param guid the guid
     * @return the usage counter
     */
    public int getUsageByGuid(String guid) {
        Integer res = m_usage.get(guid);
        if (res == null) {
            res = 0;
        }
        return res;
    }
    
    /** {@inheritDoc} */
    public Collection<IComponentNamePO> getAllCompNamePOs() {
        return m_compNames.values();
    }
    
    /**
     * Returns the map of all problem types;
     * @return the problem types
     */
    public Map<String, ProblemType> getTypeProblems() {
        return m_allTypeProblems;
    }
    
    /**
     * Recalculates all CN types
     */
    public void recalculateTypes() {
        String mostAbstract = CompNameTypeManager.getMostAbstractType();
        for (IComponentNamePO cN : CompNameManager.getInstance()
                .getAllCompNamePOs()) {
            if (GeneralStorage.getInstance().getProject().getId().equals(
                    cN.getParentProjectId())) {
                cN.setComponentType(mostAbstract);
                cN.setTypeProblem(null);
            }
        }
        CalcTypes calc = new CalcTypes(CompNameManager.getInstance(), null);
        calc.setWriteTypes(true);
        calc.calculateTypes();
        CompNameManager.getInstance().setTypeProblems(calc);
    }
    
    /**
     * Stores the type problems
     * @param calc the calculator used to calculate types
     */
    public void setTypeProblems(CalcTypes calc) {
        m_allTypeProblems = calc.getAllProblems();
    }

    /** {@inheritDoc} */
    public Map<String, IComponentNamePO> getLocalChanges() {
        return new HashMap<>();
    }
}
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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.IUsedToolkitPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.SpecTreeTraverser;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.toolkit.common.utils.ToolkitUtils;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Business processes for the used toolkits information.
 *
 * @author BREDEX GmbH
 * @created 25.05.2007
 */
public class UsedToolkitBP {
    
    /** standard logging */
    private static Logger log = LoggerFactory.getLogger(UsedToolkitBP.class);
    
    /**
     * The singleton instance.
     */
    private static UsedToolkitBP instance = null;
    
    /**
     * The Set of used Toolkits.
     */
    private Set<IUsedToolkitPO> m_usedToolkits = new HashSet<IUsedToolkitPO>();


    
    /**
     * private utility constructor
     */
    private UsedToolkitBP() {
      // private
    }
    
    /**
     * Adds the used toolkits of the given {@link ISpecTestCasePO} to the list 
     * of used toolkits.
     * @param specTC The {@link ISpecTestCasePO} whose toolkits are to add.
     * @param project the project of the {@link ISpecTestCasePO}
     * @throws PMException in case of DB error.
     * @throws ProjectDeletedException if the project was deleted in another
     *                                   session.
     */
    public void addToolkit(ISpecTestCasePO specTC, IProjectPO project) 
        throws PMException, ProjectDeletedException {
        
        final Iterator<INodePO> childs = specTC.getAllNodeIter();
        final Set<IUsedToolkitPO> addedToolkits = 
            new HashSet<IUsedToolkitPO>();
        final Set<IUsedToolkitPO> currentToolkits = 
            readUsedToolkitsFromDB(project);
        while (childs.hasNext()) {
            INodePO child = childs.next();
            if (child instanceof ICapPO) {
                IUsedToolkitPO usedToolkit = addToolkit((ICapPO)child, project);
                if (usedToolkit != null 
                    && !currentToolkits.contains(usedToolkit)) {
                    
                    addedToolkits.add(usedToolkit);                  
                }
            }
        }
        insertIntoDB(addedToolkits);
    }
    
    
    /**
     * Adds the toolkit of the given {@link ICapPO} to the internal Set of 
     * used toolkits.
     * @param capPO A {@link ICapPO}.
     * @param project the project depending to the given {@link ICapPO}.
     * @return A new used IUsedToolkitPO or null if the toolkit was already used.
     */
    private IUsedToolkitPO addToolkit(ICapPO capPO, IProjectPO project) {
        
        final String compType = capPO.getComponentType();
        final CompSystem compSystem = ComponentBuilder.getInstance()
            .getCompSystem();
        final Component component = compSystem.findComponent(compType);
        final ToolkitDescriptor descr = component.getToolkitDesriptor();
        final Long projectID = project.getId();
        final IUsedToolkitPO usedToolkit = PoMaker.createUsedToolkitsPO(
            descr.getToolkitID(), 
            descr.getMajorVersion(), 
            descr.getMinorVersion(), 
            projectID);
        final boolean isAdded = m_usedToolkits.add(usedToolkit);
        if (isAdded) {
            return usedToolkit;
        }
        return null;
    }

    
    /**
     * Inserts the given List of used toolkits into the DB.
     * 
     * @param usedToolkits
     *            the used toolkits to insert into DB.
     * @throws PMException
     *             in case of DB error.
     * @throws ProjectDeletedException
     *             if the project was deleted in another session.
     */
    private void insertIntoDB(Set<IUsedToolkitPO> usedToolkits) 
        throws PMException, ProjectDeletedException {
        if (usedToolkits.isEmpty()) {
            return;
        }
        final EntityManager session = Persistor.instance().openSession();
        IUsedToolkitPO currToolkit = null;
        try {
            final EntityTransaction tx = 
                Persistor.instance().getTransaction(session);
            for (IUsedToolkitPO toolkit : usedToolkits) {
                currToolkit = toolkit;
                session.persist(toolkit);
            }
            Persistor.instance().commitTransaction(session, tx);
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForAnySession(currToolkit, 
                e, session);
        } finally {
            Persistor.instance().dropSession(session);
        }

    }
    
    /**
     * Gets all used toolkits of the given Project from the DB.
     * @param project the current Project.
     * @return set of used toolkits
     * @throws PMException in case of DB error.
     */
    @SuppressWarnings("unchecked")
    public synchronized Set<IUsedToolkitPO> readUsedToolkitsFromDB(
        IProjectPO project) throws PMException {
        
        if (project == null) {
            m_usedToolkits = new HashSet<IUsedToolkitPO>(0);
            return new HashSet<IUsedToolkitPO>(m_usedToolkits);
        }
        final EntityManager session = Persistor.instance().openSession();
        try {
            final Query q = session.createQuery(
                "select USEDTOOLKITS from UsedToolkitPO as USEDTOOLKITS where USEDTOOLKITS.hbmParentProjectId = :projectID"); //$NON-NLS-1$
            q.setParameter("projectID", project.getId()); //$NON-NLS-1$
            final List<IUsedToolkitPO> toolkits = q.getResultList();
            m_usedToolkits = new HashSet<IUsedToolkitPO>(toolkits);
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForAnySession(null, e, session);
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(session);
        }
        return new HashSet<IUsedToolkitPO>(m_usedToolkits);
    }
    
    /**
     * Refreshes the ToolkitInfo of the given Project.<br>
     * <b>Note: This is an expensive operation!</b><br> 
     * @param project the Project
     */
    public void refreshToolkitInfo(final IProjectPO project) 
        throws PMException, ProjectDeletedException {

        refreshToolkitInfo(project, null);
    }

    /**
     * Refreshes the ToolkitInfo of the given Project.<br>
     * <b>Note: This is an expensive operation!</b><br> 
     * @param project the Project
     * @param monitor The progress monitor for this operation.
     *                     <code>null</code> is allowed, and indicates that
     *                     there is no need to check for cancelation or report
     *                     progress.
     */
    public void refreshToolkitInfo(final IProjectPO project, 
        final IProgressMonitor monitor) 
        throws PMException, ProjectDeletedException {
        
        EntityManager s = Persistor.instance().openSession();
        try {
            deleteToolkitsFromDB(s, project.getId(), true);
        } finally {
            Persistor.instance().dropSession(s);           
        }
        m_usedToolkits.clear();

        final ITreeNodeOperation<INodePO> addToolkitOP = 
            new AbstractNonPostOperatingTreeNodeOperation<INodePO>() {
                @SuppressWarnings("synthetic-access")
                public boolean operate(ITreeTraverserContext<INodePO> ctx, 
                        INodePO parent, INodePO node, boolean alreadyVisited) {

                    if (node instanceof ICapPO) {
                        if (monitor != null && monitor.isCanceled()) {
                            ctx.setContinued(false);
                            return true;
                        }
                        addToolkit((ICapPO)node, project);
                    }
                    return true;
                }
            };

        SpecTreeTraverser traverser = new SpecTreeTraverser(project, 
            addToolkitOP);
        traverser.traverse();
        if (monitor != null && monitor.isCanceled()) {
            return;
        }
        insertIntoDB(m_usedToolkits);
    }

    /**
     * 
     * @param s session to use for delete operation
     * @param parentProjectId id of root project
     * @param commit flag for commitment of delete statement
     * @throws PMException in case of failed delete statement
     */
    public void deleteToolkitsFromDB(EntityManager s, Long parentProjectId, 
        boolean commit) throws PMException, ProjectDeletedException {
        try {
            if (commit) {
                EntityTransaction tx = Persistor
                    .instance().getTransaction(s);
                executeDeleteStatement(s, parentProjectId);
                Persistor.instance().commitTransaction(s, tx);
            } else {
                executeDeleteStatement(s, parentProjectId);
            }
        } catch (PersistenceException e) {
            String msg = Messages.DeletionOfToolkitsFailed 
                + StringConstants.DOT;
            log.error(msg, e); 
            throw new PMException(msg, MessageIDs.E_DB_SAVE);
        }
    }

    /**
     * @param s session to use
     * @param parentProjectId id of parent project
     */
    private synchronized void executeDeleteStatement(
        EntityManager s, Long parentProjectId) {
        
        Query q = s.createQuery(
            "delete from UsedToolkitPO u where u.hbmParentProjectId = :parentProjId"); //$NON-NLS-1$
        q.setParameter("parentProjId", parentProjectId); //$NON-NLS-1$
        q.executeUpdate();
    }
    
    /**
     * @return the singleton instance
     */
    public static UsedToolkitBP getInstance() {
        if (instance == null) {
            instance = new UsedToolkitBP();
        }
        return instance;
    }
    
    /**
     * @return A Set of currently used toolkits.
     */
    public Set<IUsedToolkitPO> getUsedToolkits() {
        return new HashSet<IUsedToolkitPO>(m_usedToolkits);
    }
    
    /**
     * Gets the toolkit level of the given node.
     * @param node an InodePO
     * @return the toolkit level.
     */
    public String getToolkitLevel(INodePO node) {
         // FIXME (AT) : Aufteilen fuer TC- und TS-Browser!!!
        String level = ToolkitConstants.LEVEL_ABSTRACT;
        node.setToolkitLevel(ToolkitConstants.LEVEL_ABSTRACT);
        if (node instanceof ICapPO) {
            ICapPO cap = (ICapPO)node;
            final String compType = cap.getComponentType();
            final Component comp = ComponentBuilder.getInstance()
                .getCompSystem().findComponent(compType);
            level = comp.getToolkitDesriptor().getLevel();
            node.setToolkitLevel(level);
            return level;
        }
        Iterator< ? extends INodePO> iter = node.getNodeListIterator();
        if (node instanceof IProjectPO) {
            iter = TestSuiteBP.getListOfTestSuites().iterator();
        }
        String mainLevel = ToolkitConstants.LEVEL_ABSTRACT;
        while (iter.hasNext()) {
            level = getToolkitLevel(iter.next());
            if (ToolkitUtils.isToolkitMoreConcrete(level, mainLevel)) {
                mainLevel = level;
            }
        }
        node.setToolkitLevel(mainLevel);
        return mainLevel;
    }
    
    
     /**
     * Updates the toolkit level in the hierarchy.
     * @param node the changed node.
     * @param oldLevel the old toolkit level of the given node.
     */
    public void updateToolkitLevel(INodePO node, String oldLevel) {
        String oldTkLevel = oldLevel;
        final String newLevel = getToolkitLevel(node);
        if (oldTkLevel.length() == 0) {
            oldTkLevel = newLevel;
        }
        if (newLevel.equals(oldTkLevel)) {
            return;
        }
        final boolean moreConcrete = ToolkitUtils.isToolkitMoreConcrete(
            newLevel, oldTkLevel);
        if (node instanceof ISpecTestCasePO) {
            ISpecTestCasePO specTC = (ISpecTestCasePO)node;
            final List<IExecTestCasePO> execs = 
                NodePM.getInternalExecTestCases(specTC.getGuid(), 
                    specTC.getParentProjectId());
            for (IExecTestCasePO exec : execs) {
                final INodePO execParent = exec.getSpecAncestor();
                final String parentLevel = execParent.getToolkitLevel();
                if (ToolkitUtils.isToolkitMoreConcrete(parentLevel, oldTkLevel)
                    || (!moreConcrete && existsCapWithLevel(oldTkLevel, 
                        execParent))) {
                    
                    continue;
                }
                final String oldLev = execParent.getToolkitLevel();
                execParent.setToolkitLevel(newLevel);
                updateToolkitLevel(execParent, oldLev);
            }
        }
        
        INodePO parent = node.getParentNode();
        if (parent instanceof ICategoryPO) {
            parent = parent.getParentNode();
            while ((parent instanceof ICategoryPO) && parent != null) {
                parent = parent.getParentNode();
            }
        }
        if (parent == null) {
            parent = GeneralStorage.getInstance().getProject();
        }
        if (parent == null) {
            return;
        }
        final String parentLevel = parent.getToolkitLevel();
        if (moreConcrete) {
            if (parentLevel.equals(newLevel) || ToolkitUtils
                .isToolkitMoreConcrete(parentLevel, newLevel)) {
                return;
            }
        } else if (existsCapWithLevel(oldTkLevel, parent)) {
            return;
        }
        parent.setToolkitLevel(newLevel);
        if (parent.getParentNode() == null) {
            return;
        }
        updateToolkitLevel(parent.getParentNode(), parentLevel);
    }
    

    /**
     * Checks if there are version conflicts of the toolkit plugins.
     * @param usedToolkits toolkits used in given project
     * @return A List of {@link ToolkitPluginError.ERROR}, or an empty List
     *  if there are no errors.
     */
    public List<ToolkitPluginError> checkUsedToolkitPluginVersions(
        Set<IUsedToolkitPO> usedToolkits) {
        
        final List<ToolkitPluginError> errors = 
            new ArrayList<ToolkitPluginError>(0);
        final List<ToolkitDescriptor> toolkitPluginDescriptors = 
            ComponentBuilder.getInstance().getCompSystem()
                .getAllToolkitDescriptors();
        for (IUsedToolkitPO usedTk : usedToolkits) {
            final int usedTkMajVers = usedTk.getMajorVersion();
            final int usedTkMinVers = usedTk.getMinorVersion();
            for (ToolkitDescriptor plDescr : toolkitPluginDescriptors) {

                if (usedTk.getToolkitId().equals(plDescr.getToolkitID())) {
                    final int pluginMajVers = plDescr.getMajorVersion();
                    final int pluginMinVers = plDescr.getMinorVersion();
                    if (usedTkMajVers != pluginMajVers) {
                        errors.add(new ToolkitPluginError(
                            ToolkitPluginError.ERROR.MAJOR_VERSION_ERROR, 
                            usedTk.getToolkitId(), pluginMajVers, 
                            usedTkMajVers));
                    }
                    if (usedTkMinVers < pluginMinVers) {
                        errors.add(new ToolkitPluginError(
                            ToolkitPluginError.ERROR.MINOR_VERSION_LOWER, 
                            usedTk.getToolkitId(), pluginMinVers, 
                            usedTkMinVers));
                    }
                    if (usedTkMinVers > pluginMinVers) {
                        errors.add(new ToolkitPluginError(
                            ToolkitPluginError.ERROR.MINOR_VERSION_HIGHER, 
                            usedTk.getToolkitId(), pluginMinVers, 
                            usedTkMinVers));
                    }
                }
            }
        }
        return errors;
    }
    
    /**
     * @author BREDEX GmbH
     * @created 31.07.2007
     */
    public static class ToolkitPluginError {
        
        /**
         * The errors.
         */
        public static enum ERROR {
            /***/
            MAJOR_VERSION_ERROR, 
            /***/
            MINOR_VERSION_LOWER, 
            /***/
            MINOR_VERSION_HIGHER
        }
        
        /**
         * The error.
         */
        private ToolkitPluginError.ERROR m_error = null;
        
        /**
         * The id of the toolkit.
         */
        private String m_toolkitId = null;
        
        /**
         * The version of the toolkit.
         */
        private int m_pluginToolkitVersion = 0;
        
        /**
         * The version of the used plugin.
         */
        private int m_usedToolkitVerison = 0;
        
        
        /**
         * Constructor.
         * @param error the error.
         * @param toolkitId the id of the toolkit.
         * @param pluginToolkitVersion the version of the plugin.
         * @param usedToolkitVerison the version of the used toolkit.
         */
        public ToolkitPluginError(ToolkitPluginError.ERROR error, 
            String toolkitId, int pluginToolkitVersion, 
            int usedToolkitVerison) {
            
            m_error = error;
            m_toolkitId = toolkitId;
            m_pluginToolkitVersion = pluginToolkitVersion;
            m_usedToolkitVerison = usedToolkitVerison;
        }

        /**
         * @return the error.
         */
        public ToolkitPluginError.ERROR getError() {
            return m_error;
        }

        /**
         * @return the version of the plugin.
         */
        public int getPluginToolkitVersion() {
            return m_pluginToolkitVersion;
        }

        /**
         * @return the version of the used toolkit.
         */
        public int getUsedToolkitVerison() {
            return m_usedToolkitVerison;
        }

        /**
         * @return the id of the toolkit.
         */
        public String getToolkitId() {
            return m_toolkitId;
        }
        
        
    }   
    
    
    /**
     * Checks, if a Cap with the given toolkit level exists under the given 
     * root.
     * @param level the level to check.
     * @param root the root
     * @return true if a cap with the given toolkit level exists, 
     * false otherwise.
     */
    private boolean existsCapWithLevel(final String level, INodePO root) {
        
        /**
         * Helper class to get the result out of the ITreeNodeOperation.
         */
        final class BooleanHolder {

            private boolean m_bool = false;

            /**
             * @return the boolean
             */
            public boolean getBool() {
                return m_bool;
            }

            /** 
             * @param bool a boolean
             */
            public void setBool(boolean bool) {
                m_bool = bool;
            }
            
        }
        
        final BooleanHolder treeOpResult = new BooleanHolder();
        
        final ITreeNodeOperation<INodePO> levelSearchOp = 
            new AbstractNonPostOperatingTreeNodeOperation<INodePO>() {
                public boolean operate(
                        ITreeTraverserContext<INodePO> ctx, INodePO parent, 
                        INodePO node, boolean alreadyVisited) {
                
                    if (node instanceof ICapPO) {
                        final ICapPO cap = (ICapPO)node;
                        if (level.equals(cap.getToolkitLevel())) {
                            treeOpResult.setBool(true);
                            ctx.setContinued(false);
                        }
                    }
                    return true;
                }
            };
        
        final TreeTraverser traverser = new SpecTreeTraverser(root, 
            levelSearchOp);
        traverser.traverse();
        return treeOpResult.getBool();
    }
    
    /**
     * Calculates and returns the path from the root node of the toolkit
     * hierarchy to the node representing the given toolkit. For example: 
     *     
     *     Swing inherits from Concrete; Concrete inherits from Abstract.
     *     Abstract is the root of the toolkit hierarchy.
     *      
     *     The path for Swing would be [Abstract, Concrete, Swing].
     * 
     * @param toolkit The toolkit for which to find the path to the root 
     *                toolkit.
     * @return the tree path for the given toolkit.
     */
    private List<ToolkitDescriptor> getPathToRoot(
            ToolkitDescriptor toolkit) {
        
        List<ToolkitDescriptor> path = 
            new LinkedList<ToolkitDescriptor>();
        ToolkitDescriptor currentToolkit = toolkit;
        while (currentToolkit != null) {
            path.add(0, currentToolkit);
            ToolkitDescriptor included =
                ComponentBuilder.getInstance().getCompSystem()
                    .getToolkitDescriptor(currentToolkit.getIncludes());
            currentToolkit = included != null ? included 
                    : ComponentBuilder.getInstance().getCompSystem()
                        .getToolkitDescriptor(
                                currentToolkit.getDepends());
            
        }
        
        return path;
    }
    
    /**
     * @param project the Project
     * @return A List of the allowed toolkits for the given project.
     */
    public List<ToolkitDescriptor> getAllowedProjectToolkits(
        IProjectPO project) {
        
        List<ToolkitDescriptor> allowedToolkits = 
            new ArrayList<ToolkitDescriptor>();
        try {
            refreshToolkitInfo(project);
        } catch (PMException e) {
            // nothing
        } catch (ProjectDeletedException e) {
            // nothing
        }

        Set<ToolkitDescriptor> allowedByAuts = 
            getAllowedProjectToolkitsAut(project.getAutMainList());
        Set<ToolkitDescriptor> allowedByReusedProjects =
            getAllowedProjectToolkitsReused(project.getUsedProjects());
        Set<ToolkitDescriptor> allowedBySteps =
            getAllowedProjectToolkitsStep(getUsedToolkits());
        
        // Take intersection of allowed toolkits
        allowedToolkits.addAll(ComponentBuilder.getInstance().getCompSystem()
                .getAllToolkitDescriptors());
        allowedToolkits.retainAll(allowedByAuts);
        allowedToolkits.retainAll(allowedByReusedProjects);
        allowedToolkits.retainAll(allowedBySteps);
        
        return allowedToolkits;
    }

    /**
     * Computes and returns the set of possible toolkits for a project
     * that uses <code>usedToolkits</code>.
     * 
     * @param usedToolkits Set of toolkits within a project. This is deterimined
     *                     by the test steps / components used within the 
     *                     project.
     * @return allowed toolkits.
     */
    private Set<ToolkitDescriptor> getAllowedProjectToolkitsStep(
            Set<IUsedToolkitPO> usedToolkits) {

        Set<ToolkitDescriptor> stepToolkits = 
            new HashSet<ToolkitDescriptor>();
        for (IUsedToolkitPO usedTk : usedToolkits) {
            stepToolkits.add(ComponentBuilder.getInstance().getCompSystem()
                    .getToolkitDescriptor(usedTk.getToolkitId()));
        }

        return getMostConcreteAllowed(stepToolkits);
    }

    /**
     * Computes and returns the path to root for the toolkit in 
     * <code>toolkits</code> that is furthest from the root node of the 
     * toolkit hierarchy. The toolkit described by this path can be considered
     * to be the most concrete toolkit from <code>toolkits</code>. 
     * 
     * @param toolkits The toolkits to check.
     * @return The path to root for the most concrete element in 
     *         <code>toolkits</code>.
     */
    private List<ToolkitDescriptor> getLongestPathToRoot(
            Set<ToolkitDescriptor> toolkits) {

        List<ToolkitDescriptor> longestPath = 
            new ArrayList<ToolkitDescriptor>();
        for (ToolkitDescriptor toolkit : toolkits) {
            List<ToolkitDescriptor> pathToRoot = getPathToRoot(toolkit);
            if (pathToRoot.size() > longestPath.size()) {
                longestPath = pathToRoot;
            }
        }

        return longestPath;
    }

    /**
     * Computes and returns the set of possible toolkits for a project
     * that reuses <code>reusedProjects</code>.
     * 
     * @param reusedProjects Set of projects reused by a project.
     * @return allowed toolkits.
     */
    private Set<ToolkitDescriptor> getAllowedProjectToolkitsReused(
            Set<IReusedProjectPO> reusedProjects) {

        Set<ToolkitDescriptor> reusedToolkits = 
            new HashSet<ToolkitDescriptor>();
        for (IReusedProjectPO reused : reusedProjects) {
            IProjectPO reusedProject;
            try {
                reusedProject = ProjectPM.loadProjectFromMaster(reused);
                // If the project cannot be found in the db, then we cannot consider
                // it in our computations.
                if (reusedProject != null) {
                    reusedToolkits.add(
                            ComponentBuilder.getInstance().getCompSystem()
                            .getToolkitDescriptor(
                                    reusedProject.getToolkit()));
                }
            } catch (JBException e) {
                // Do nothing.
                // If the project cannot be found in the db, then we cannot consider
                // it in our computations.
            }
        }

        return getMostConcreteAllowed(reusedToolkits);

    }

    /**
     * Computes and returns the set of toolkits allowed based on the following
     * criteria:
     *   The most concrete (least abstract) toolkit in <code>toolkits</code>
     *   is selected. This toolkit and all descendants of this toolkit are 
     *   returned.
     * 
     * @param toolkits Set of toolkits from which to choose.
     * @return A set of allowed toolkits containing the most concrete instance 
     *         in <code>toolkits</code> and all of its descendants.
     */
    private Set<ToolkitDescriptor> getMostConcreteAllowed(
            Set<ToolkitDescriptor> toolkits) {

        Set<ToolkitDescriptor> allowedToolkits = 
            new HashSet<ToolkitDescriptor>();
        allowedToolkits.addAll(ComponentBuilder.getInstance()
                .getCompSystem().getAllToolkitDescriptors());
        List<ToolkitDescriptor> longestPathToRoot = 
            getLongestPathToRoot(toolkits);
        if (longestPathToRoot.size() > 0) {
            ToolkitDescriptor mostConcreteToolkit = 
                longestPathToRoot.get(longestPathToRoot.size() - 1);
            mostConcreteToolkit = 
                getMostConcreteIndependentToolkit(mostConcreteToolkit);
            allowedToolkits.retainAll(getDescendants(mostConcreteToolkit));
        }
        
        return allowedToolkits;
    }

    /**
     * Searches up the Toolkit hierarchy for an independent Toolkit 
     * (that is, a Toolkit that includes another toolkit and is
     * thus available as a specification Toolkit).
     * 
     * @param baseToolkit The Toolkit from which the search is started.
     *                    May be <code>null</code>.
     * @return The first independent Toolkit encountered during the search.
     *         Will be <code>baseToolkit</code> if <code>baseToolkit</code> is
     *         independent. Will be <code>null</code> if no independent Toolkit
     *         can be found among <code>baseToolkit</code>'s ancestor Toolkits. 
     */
    private ToolkitDescriptor getMostConcreteIndependentToolkit(
            ToolkitDescriptor baseToolkit) {

        ToolkitDescriptor currentToolkit = baseToolkit;
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        while (currentToolkit != null 
                && compSystem.getToolkitDescriptor(
                        currentToolkit.getIncludes()) == null
                && compSystem.getToolkitDescriptor(
                        currentToolkit.getDepends()) != null) {

            currentToolkit = compSystem.getToolkitDescriptor(
                    currentToolkit.getDepends());
            
        }

        return currentToolkit;
    }

    /**
     * Computes and returns the set of possible toolkits for a project
     * that contains <code>projectAuts</code>.
     * 
     * @param projectAuts Set of AUTs contained in a project.
     * @return allowed toolkits.
     */
    private Set<ToolkitDescriptor> getAllowedProjectToolkitsAut(
            Set<IAUTMainPO> projectAuts) {

        final CompSystem compSys = 
            ComponentBuilder.getInstance().getCompSystem();
        Set<ToolkitDescriptor> allowedToolkits =
            new HashSet<ToolkitDescriptor>();
        final Set<ToolkitDescriptor> autToolkits = 
            new HashSet<ToolkitDescriptor>();
        if (projectAuts.isEmpty()) {
            // If no AUT is defined, then the project toolkit is not
            // restricted based on defined AUTs.
            allowedToolkits.addAll(compSys.getAllToolkitDescriptors());
        }
        
        for (IAUTMainPO autMain : projectAuts) {
            final String autToolkit = autMain.getToolkit();
            autToolkits.add(compSys.getToolkitDescriptor(autToolkit));
        }

        // Determine paths to root for all aut toolkits
        List<List<ToolkitDescriptor>> pathsToRoot = 
            new LinkedList<List<ToolkitDescriptor>>();
        for (ToolkitDescriptor toolkit : autToolkits) {
            List<ToolkitDescriptor> path = getPathToRoot(toolkit);
            pathsToRoot.add(path);
        }

        // Add all common ancestors
        if (!pathsToRoot.isEmpty()) {
            // The path to which all other paths can be compared
            List<ToolkitDescriptor> refPath = pathsToRoot.get(0);
            
            for (int i = 0; i < refPath.size(); i++) {
                ToolkitDescriptor toolkit = refPath.get(i);
                boolean isCommon = true;
                for (int j = 1; j < pathsToRoot.size() && isCommon; j++) {
                    List<ToolkitDescriptor> otherPath = 
                        pathsToRoot.get(j);
                    isCommon = otherPath.size() > i 
                        && otherPath.get(i).equals(toolkit);
                }
                if (isCommon) {
                    allowedToolkits.add(toolkit);
                }
            }
        }

        return allowedToolkits;
    }

    /**
     * 
     * @param toolkit The toolkit for which to find all descendants.
     * @return all descendants of <code>toolkit</code> within the toolkit
     *         hierarchy. This <em>includes</em> the given toolkit.
     */
    private Set<ToolkitDescriptor> getDescendants(
            ToolkitDescriptor toolkit) {

        Set<ToolkitDescriptor> descendants = 
            new HashSet<ToolkitDescriptor>();
        descendants.add(toolkit);
        List<ToolkitDescriptor> allToolkits = 
            ComponentBuilder.getInstance().getCompSystem()
                .getAllToolkitDescriptors();
        for (ToolkitDescriptor toolkitDesc : allToolkits) {
            if (ToolkitUtils.doesToolkitInclude(toolkitDesc.getToolkitID(), 
                    toolkit.getToolkitID())) {
                
                descendants.add(toolkitDesc);
            }
        }

        return descendants;
    }

}

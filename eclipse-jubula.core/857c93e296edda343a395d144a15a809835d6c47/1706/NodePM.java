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
package org.eclipse.jubula.client.core.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.utils.ValueListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class to persist and read nodes
 * 
 * @author BREDEX GmbH
 * @created 07.09.2004
 */
public class NodePM extends PersistenceManager {
    
    /**
     * Command for parent/child adding and removing
     */
    public abstract static class AbstractCmdHandleChild {
        
        /** A bit cumbersome, but this object will be used in some children */
        private IPersistentObject m_parent = null;
        
        /**
         * Template for this command. If executed add the child to the parent.
         * 
         * @param parent
         *            Node where the child should be added.
         * @param child
         *            Child to be added to parent.
         * @param pos
         *            where to insert the child, value null means insert after
         *            end
         */
        public abstract void add(INodePO parent, INodePO child, Integer pos);

        /**
         * Template for this command. If executed remove the child from the
         * parent. This method assumes that the child is to be deleted.
         * Calls dispose() on child!
         * @param parent
         *            Node where the child should be removed.
         * @param child
         *            Child to be removed from parent.
         */
        public void delete(INodePO parent, INodePO child) {
            remove(parent, child);
        }

        /**
         * Like delete, but the child is not to be deleted.
         * Does <b>not</b> call dispose() on child!
         * {@inheritDoc}
         */
        public abstract void remove(INodePO parent, INodePO child);
        
        /**
         * Sets the parentProjectId for a node inserted into another node.
         * @param child the child node
         * @param parent the parent node
         */
        public void setParentProjectId(INodePO child, INodePO parent) {
            Long parentProjectId = parent.getParentProjectId();
            if (parentProjectId == null) {
                parentProjectId = GeneralStorage.getInstance().getProject()
                        .getId();
            }
            child.setParentProjectId(parentProjectId);
        }
        
        /**
         * Sets the alternative parent - used only by ChildIntoSpec and Exec
         * @param par the parent
         */
        public void setParent(IPersistentObject par) {
            m_parent = par;
        }
        
        /**
         * @return guess what...
         */
        public IPersistentObject getParent() {
            return m_parent;
        }
    }

    /**
     * {@inheritDoc}
     */
    public static class CmdHandleChildIntoNodeList extends
        AbstractCmdHandleChild {

        /**
         * {@inheritDoc}
         *      org.eclipse.jubula.client.core.model.INodePO)
         */
        public void add(INodePO parent, INodePO child, Integer pos) {
            parent.addNode(pos == null ? -1 : pos, child);
            setParentProjectId(child, parent);
        }

        /**
         * {@inheritDoc}
         *      org.eclipse.jubula.client.core.model.INodePO)
         * @param parent
         * @param child
         */
        public void remove(INodePO parent, INodePO child) {
            parent.removeNode(child);
        }
    }

    /**
     * {@inheritDoc}
     */
    public static class CmdHandleEventHandlerIntoMap 
        extends AbstractCmdHandleChild {
        /**
         * {@inheritDoc}
         * @param assocNode specTc which will use the evHandler
         * @param evHandler evHandler to add to assocNode
         * @param pos not required (use null)
         */
        public void add(INodePO assocNode, INodePO evHandler, Integer pos) {
            if (assocNode instanceof ISpecTestCasePO
                && evHandler instanceof IEventExecTestCasePO) {
                ISpecTestCasePO usingSpecTc = (ISpecTestCasePO)assocNode;
                try {
                    usingSpecTc.addEventTestCase(
                        (IEventExecTestCasePO)evHandler);
                    setParentProjectId(usingSpecTc, evHandler);
                } catch (InvalidDataException e) {
                    log.error(Messages.AttemptToAddAnEventhandlerTwice, e);
                }
            } else {
                throw new JBFatalException(
                    Messages.WrongTypeForAdditionOfEventhandler,
                    MessageIDs.E_UNEXPECTED_EXCEPTION);
            }
            
        }
        
        /**
         * {@inheritDoc}
         * @param assocNode specTc which contains the evHandler
         * @param evHandler evHandler to remove from assocNode
         */
        public void remove(INodePO assocNode, INodePO evHandler) {
            if (assocNode instanceof ISpecTestCasePO
                && evHandler instanceof IEventExecTestCasePO) {
                ISpecTestCasePO usingSpecTc = (ISpecTestCasePO)assocNode;
                usingSpecTc.removeNode(evHandler);
            } else {
                throw new JBFatalException(
                    Messages.WrongTypeForRemovalOfEventhandler,
                    MessageIDs.E_UNEXPECTED_EXCEPTION);
            }
            
        }
    }

    /**
     * class variable for Singleton
     */
    private static NodePM nodePersManager = null;

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(NodePM.class);

    /** cache for project IDs */
    private Map<String, Long> m_projectIDCache = null;
    
    /** cache for SpecTCs */
    private Map<String, Object> m_specTCCache = null;

    /** Session used in last request */
    private EntityManager m_lastSession = null;
    
    /** is cache usage enabled */
    private boolean m_useCache = false;
    
    /**
     * getter for Singleton
     * 
     * @return single instance of CapPM problem of database
     */
    public static NodePM getInstance() {
        if (nodePersManager == null) {
            nodePersManager = new NodePM();
        }
        return nodePersManager;
    }

    /**
     * Factory for Commands
     * 
     * @param parent
     *            p
     * @param child
     *            c
     * @return C
     */
    public static AbstractCmdHandleChild getCmdHandleChild(INodePO parent,
            INodePO child) {
        if (parent instanceof ICategoryPO
                || parent instanceof IAbstractContainerPO) {
            // category/specTc in category
            return new CmdHandleChildIntoNodeList();
        } else if (parent instanceof ITestSuitePO) {
            // execTc in testsuite
            return new CmdHandleChildIntoNodeList();
        } else if (parent instanceof ISpecTestCasePO) {
            if (child instanceof IEventExecTestCasePO) {
                // eventhandler in using specTc
                return new CmdHandleEventHandlerIntoMap();
            }
            // execTc or Cap in SpecTestCase
            return new CmdHandleChildIntoNodeList();
        }
        final String msg = Messages.UnsupportedINodePOSubclass;
        log.error(msg);
        throw new JBFatalException(msg, MessageIDs.E_UNSUPPORTED_NODE);
    }

    /**
     * Insert a child and persist to DB
     * 
     * @param parent
     *            parent of child to insert
     * @param child
     *            child to insert
     * @param pos
     *            where to insert the child. if null insert after end
     * @throws PMSaveException
     *             in case of DB problem or refresh errors
     * @throws PMAlreadyLockedException in case of locked parent
     * @throws PMException in case of rollback failed
     * @throws ProjectDeletedException if the project was deleted in another
     * instance
     */
    public static void addAndPersistChildNode(INodePO parent, INodePO child,
        Integer pos) throws PMSaveException, PMAlreadyLockedException,
        PMException, ProjectDeletedException {
        final Persistor persistor = Persistor.instance();
        final EntityManager sess = persistor.openSession();
        try {
            AbstractCmdHandleChild handler = getCmdHandleChild(parent, child);
            IProjectPO currProj = GeneralStorage.getInstance().getProject();
            EntityTransaction tx = persistor.getTransaction(sess);
            sess.persist(child);
            IPersistentObject newParent;
            newParent = sess.find(parent.getClass(), parent.getId());
            persistor.lockPO(sess, newParent);
            handler.add((INodePO) newParent, child, pos);
            
            persistor.commitTransaction(sess, tx);
            refreshMasterSession(newParent);
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForMasterSession(null, e);
        } finally {
            persistor.dropSession(sess);
        }
    }
    
    /**
     * Refreshes an object in the master session
     * @param refr the objects
     */
    public static void refreshMasterSession(IPersistentObject refr) {
        EntityManager sess = GeneralStorage.getInstance().getMasterSession();
        IPersistentObject obj = sess.find(refr.getClass(), refr.getId());
        if (obj != null) {
            try {
                sess.refresh(obj);
            } catch (EntityNotFoundException e) {
                sess.detach(obj);
            }
        }
    }

    /**
     * @param node
     *            the node to be renamed
     * @param newName
     *            the new name
     * @throws PMDirtyVersionException
     *             in case of dirty version
     * @throws PMAlreadyLockedException
     *             in case of locked node
     * @throws PMSaveException
     *             in case of DB save error
     * @throws PMException in case of general db error
     * @throws ProjectDeletedException if the project was deleted in another
     * instance            
     */
    public static void renameNode(INodePO node, String newName)
        throws PMDirtyVersionException, PMAlreadyLockedException,
        PMSaveException, PMException, ProjectDeletedException {

        Persistor per = Persistor.instance();
        EntityManager sess = per.openSession();
        EntityTransaction tx = null;
        try {
            INodePO newNode = sess.find(node.getClass(), node.getId());
            tx = per.getTransaction(sess);
            per.lockPO(sess, node);
            newNode.setName(newName);
            per.commitTransaction(sess, tx);
            refreshMasterSession(node);
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForMasterSession(node, e);
        } finally {
            per.dropSession(sess);
        }
    }

    /**
     * @param node
     *            the node to be renamed
     * @param newComment
     *            the new comment
     * @throws PMDirtyVersionException
     *             in case of dirty version
     * @throws PMAlreadyLockedException
     *             in case of locked node
     * @throws PMSaveException
     *             in case of DB save error
     * @throws PMException in case of general db error
     * @throws ProjectDeletedException if the project was deleted in another
     * instance            
     */
    public static void setComment(INodePO node, String newComment)
        throws PMDirtyVersionException, PMAlreadyLockedException,
        PMSaveException, PMException, ProjectDeletedException {

        final Persistor persistor = Persistor.instance();
        EntityManager sess = persistor.openSession();
        EntityTransaction tx = null;
        try {
            tx = persistor.getTransaction(sess);
            persistor.lockPO(sess, node);
            node.setComment(newComment);
            INodePO persNode = sess.find(node.getClass(), node.getId());
            persNode.setComment(newComment);
            persistor.commitTransaction(sess, tx);
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForMasterSession(node, e);
        } finally {
            persistor.dropSession(sess);
        }
    }
    
    /**
     * @param type
     *            the type of elements to find
     * @param parentProjectId
     *            ID of the parent project to search in
     * @param s
     *            The session into which the INodePOs will be loaded.
     * @return list of param all INodePOs
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<? extends INodePO> computeListOfNodes(Class type,
            Long parentProjectId, EntityManager s) {
        Assert.isNotNull(type);
        Assert.isNotNull(s);
        CriteriaQuery query = s.getCriteriaBuilder().createQuery();
        Root from = query.from(type);
        query.select(from).where(
            s.getCriteriaBuilder().equal(
                    from.get("hbmParentProjectId"), parentProjectId)); //$NON-NLS-1$
        
        List<INodePO> queryResult = s.createQuery(query).getResultList();
        return queryResult;
    }

    /**
     * Returns test cases that reference the test case given information. 
     * Only returns test cases that are in the same project as the given test 
     * case. These test cases are loaded in the Master Session.
     * Warning: the fetched ExecTestCases have no parent, because the database
     * doesn't know the parent.
     * 
     * @param specTcGuid GUID of the test case being reused.
     * @param parentProjectId ID of the parent project of the test case being
     *                        reused.
     * @return all test cases that reference the test case with the given
     *         information, provided that the cases are also in the same 
     *         project.
     * @see getAllExecTestCases
     * @see getExternalExecTestCases
     */
    public static List<IExecTestCasePO> getInternalExecTestCases(
        String specTcGuid, long parentProjectId) {

        // a SpecTC with guid == null can't be reused
        if (specTcGuid == null) {
            return new ArrayList<IExecTestCasePO>(0);
        }
        
        List<Long> parentProjectIds = new ArrayList<Long>();
        parentProjectIds.add(parentProjectId);

        return getExecTestCasesFor(specTcGuid, parentProjectIds, 
            GeneralStorage.getInstance().getMasterSession());
    }
    
    /**
     * Returns ref test suites that reference the test suites given information.
     * Only returns ref test cases that are in the same project as the given
     * test suite. These ref test suites are loaded in the Master Session.
     * Warning: the fetched ref test suites have no parent, because the database
     * doesn't know the parent.
     * 
     * @param tsGuid
     *            GUID of the test suite being reused.
     * @param parentProjectId
     *            ID of the parent project of the test case being reused.
     * @return all ref test suites that reference the test suite with the given
     *         information, provided that the cases are also in the same
     *         project.
     */
    public static List<IRefTestSuitePO> getInternalRefTestSuites(
            String tsGuid, long parentProjectId) {
        // a test suite with guid == null can't be reused
        if (tsGuid == null) {
            return new ArrayList<IRefTestSuitePO>(0);
        }

        List<Long> parentProjectIds = new ArrayList<Long>();
        parentProjectIds.add(parentProjectId);

        return getRefTestSuitesFor(tsGuid, parentProjectIds, GeneralStorage
                .getInstance().getMasterSession());
    }
    
    /**
     * 
     * @param tsGuid The GUID of the reused test suite.
     * @param parentProjectIds All returned test suites will have one of these as
     *                         their project parent ID.
     * @param s The session into which the test cases will be loaded.
     * @return list of test suites.
     */
    @SuppressWarnings("unchecked")
    private static synchronized List<IRefTestSuitePO> getRefTestSuitesFor(
        String tsGuid, List<Long> parentProjectIds, EntityManager s) {

        StringBuffer queryBuffer = new StringBuffer(
            "select ref from RefTestSuitePO as ref where ref.testSuiteGuid = :tsGuid and ref.hbmParentProjectId in :ids"); //$NON-NLS-1$
        Query q = s.createQuery(queryBuffer.toString());
        q.setParameter("tsGuid", tsGuid); //$NON-NLS-1$
        q.setParameter("ids", parentProjectIds); //$NON-NLS-1$
        
        List<IRefTestSuitePO> refTestSuiteList = q.getResultList();
        return refTestSuiteList;
    }

    /**
     * Returns test cases that reference the test case given information. Only
     * returns test cases that are in the same project as the given test case
     * including test cases from reused projects. These test cases are loaded in
     * the Master Session. Warning: the fetched ExecTestCases have no parent,
     * because the database doesn't know the parent.
     * 
     * @param specTcGuid
     *            GUID of the test case being reused.
     * @param parentProjectIds
     *            IDs of the parent projects of the test case being reused.
     * @return all test cases that reference the test case with the given
     *         information, provided that the cases are also in the same
     *         project or reused projects.
     * @see getAllExecTestCases
     * @see getExternalExecTestCases
     * @see getInternalExecTestCases
     */
    public static List<IExecTestCasePO> getExecTestCases(
        String specTcGuid, List<Long> parentProjectIds) {

        // a SpecTC with guid == null can't be reused
        if (specTcGuid == null) {
            return new ArrayList<IExecTestCasePO>(0);
        }

        return getExecTestCasesFor(specTcGuid, parentProjectIds, 
            GeneralStorage.getInstance().getMasterSession());
    }
    
    /**
     * Returns test cases that reference the test case given information. Only
     * returns test cases that are in the same project as the given test case
     * including test cases from reused projects. These test cases are loaded in
     * the Master Session. Warning: the fetched ExecTestCases have no parent,
     * because the database doesn't know the parent.
     * 
     * @param specTcGuid
     *            GUID of the test case being reused.
     * @param parentProjectIds
     *            IDs of the parent projects of the test case being reused.
     * @param session
     *            The session into which the test cases will be loaded.
     * @return all test cases that reference the test case with the given
     *         information, provided that the cases are also in the same
     *         project or reused projects.
     * @see getAllExecTestCases
     * @see getExternalExecTestCases
     * @see getInternalExecTestCases
     */
    @Nullable
    public static List<IExecTestCasePO> getExecTestCases(String specTcGuid,
            List<Long> parentProjectIds, EntityManager session) {
        // a SpecTC with guid == null can't be reused
        if (specTcGuid == null) {
            return null;
        }
        return getExecTestCasesFor(specTcGuid, parentProjectIds, session);
    }
    
    /**
     * 
     * @param specTcGuid The GUID of the reused test case.
     * @param parentProjectIds All returned test cases will have one of these as
     *                         their project parent ID.
     * @param s The session into which the test cases will be loaded.
     * @return list of test cases.
     */
    @SuppressWarnings("unchecked")
    private static synchronized List<IExecTestCasePO> getExecTestCasesFor(
        String specTcGuid, List<Long> parentProjectIds, EntityManager s) {

        StringBuffer queryBuffer = new StringBuffer(
            "select ref from ExecTestCasePO as ref where ref.specTestCaseGuid = :specTcGuid and ref.hbmParentProjectId in :ids"); //$NON-NLS-1$
    
        Query q = s.createQuery(queryBuffer.toString());
        q.setParameter("specTcGuid", specTcGuid); //$NON-NLS-1$
        q.setParameter("ids", parentProjectIds); //$NON-NLS-1$
        
        List<IExecTestCasePO> execTcList = q.getResultList();

        return execTcList;

    }

    /**
     * 
     * @param project the project
     * @param reused the reused project
     * @return list of exec test cases in the given project that use 
     *         specTestCases from the given reused project
     */
    @SuppressWarnings("unchecked")
    public static synchronized List<IExecTestCasePO> getUsedTestCaseNames(
            IProjectPO project, IReusedProjectPO reused) {
 
        if (project == null) {
            return new ArrayList<IExecTestCasePO>();
        }

        EntityManager s = GeneralStorage.getInstance().getMasterSession();
        Query q = s.createQuery("select ref from ExecTestCasePO as ref where ref.hbmParentProjectId = :parentProjectId"  //$NON-NLS-1$
                + " and ref.projectGuid = :projectGuid"); //$NON-NLS-1$
        q.setParameter("parentProjectId", project.getId()); //$NON-NLS-1$
        q.setParameter("projectGuid", reused.getProjectGuid()); //$NON-NLS-1$
        
        List<IExecTestCasePO> result = q.getResultList();
        return result;
        
    }

    /**
     * Finds a test case within reused projects.
     * @param reusedProjects Set of reused projects that are available.
     * @param projectGuid The GUID of the parent project of the spec testcase
     * @param specTcGuid The GUID of the spec testcase
     * @return the spec testcase with the given guid, or <code>null</code> if 
     *         the testcase cannot be found
     */
    public static synchronized ISpecTestCasePO getSpecTestCase(
        Set<IReusedProjectPO> reusedProjects, String projectGuid, 
        String specTcGuid) {
       
        ProjectVersion version = null;
        for (IReusedProjectPO reusedProj : reusedProjects) {
            if (reusedProj.getProjectGuid().equals(projectGuid)) {
                version = reusedProj.getProjectVersion();
                break;
            }
        }

        if (version == null || (version.getMajorNumber() == null 
                && version.getVersionQualifier() == null)) {
            return null;
        }

        EntityManager s = GeneralStorage.getInstance().getMasterSession();
        Long projectId = NodePM.getInstance().findProjectID(s, projectGuid,
                version);
        if (projectId == null) {
            return null;
        }
        Object result = NodePM.getInstance().findSpecTC(s, specTcGuid, 
                projectId);
        if (result instanceof ISpecTestCasePO) {
            return (ISpecTestCasePO)result;
        }
        
        return null;
    }

    /**
     * find and cache a referenced spec TC
     * @param s Session
     * @param specTcGuid GUID of TC
     * @param projectId ID of project containing TC
     * @return the resulting TC or null if none was found
     */
    private Object findSpecTC(EntityManager s, String specTcGuid,
            Long projectId) {
        validateSession(s);
        
        StringBuilder idBuilder = new StringBuilder(50);
        idBuilder.append(specTcGuid);
        idBuilder.append(':');
        idBuilder.append(projectId);
        String key = idBuilder.toString();
        
        if (m_useCache) {
            Object cached = m_specTCCache.get(key);
            if (cached != null) {
                if (cached instanceof INodePO) { // check for not found
                    return cached;
                } 
                return null;                
            }
        }
        Query specTcQuery =
            s.createQuery("select node from SpecTestCasePO as node where node.guid = :guid" //$NON-NLS-1$
                + " and node.hbmParentProjectId = :projectId"); //$NON-NLS-1$
        specTcQuery.setParameter("guid", specTcGuid); //$NON-NLS-1$
        specTcQuery.setParameter("projectId", projectId); //$NON-NLS-1$

        Object result = null;

        try {
            result = specTcQuery.getSingleResult();
        } catch (NoResultException nre) {
            // No result found. The result remains null.
        }

        if (m_useCache) {
            if (result != null) {
                m_specTCCache.put(key, result);
            } else {
                m_specTCCache.put(key, new Object()); // set a not found marker
            }
        }
        return result;
    }
    
    /**
     * find and cache a reused projects OID
     * @param s Session
     * @param projectGuid GUID of project
     * @param projVersion the version of project
     * @return the OID of the project or null if the project cannot be found
     */
    private Long findProjectID(EntityManager s, String projectGuid,
            ProjectVersion projVersion) {
        validateSession(s);

        String key = buildProjectKey(projectGuid, projVersion.getMajorNumber(),
                projVersion.getMinorNumber(), projVersion.getMicroNumber(),
                projVersion.getVersionQualifier());

        if (m_useCache) {
            Long id = m_projectIDCache.get(key);
            if (id != null) {
                if (id.longValue() != -1) { // means already lookuped but not
                                            // found
                    return id;
                }
                return null;
            }
        }
        Long projectId = null;
        try {
            projectId = ProjectPM.findProjectId(projectGuid,
                    projVersion.getMajorNumber(), projVersion.getMinorNumber(),
                    projVersion.getMicroNumber(),
                    projVersion.getVersionQualifier());
        } catch (JBException e) {
            // ignored - id is therefore null
        }
        if (projectId != null) {
            if (m_useCache) {
                m_projectIDCache.put(key, projectId);
            }
            return projectId;
        }
        if (m_useCache) {
            m_projectIDCache.put(key, new Long(-1));
        }
        return null;

    }

    /**
     * checks if the Session was used before and discards caches if not
     * @param s Session
     */
    private void validateSession(EntityManager s) {
        if (m_useCache && m_lastSession != s) {
            resetCaching();
            m_lastSession = s;
        }        
    }

    /**
     * clears all caches
     */
    private void resetCaching() {
        if (m_projectIDCache != null) {
            m_projectIDCache.clear();
            m_projectIDCache = null;
        }
        if (m_specTCCache != null) {
            m_specTCCache.clear();
            m_specTCCache = null;
        }
        m_lastSession = null;
        if (m_useCache) {
            m_projectIDCache = new HashMap<String, Long>(20);
            m_specTCCache = new HashMap<String, Object>(500);
        }
    }

    /**
     * build a key for the cache
     * 
     * @param projectGuid
     *            part
     * @param majorNumber
     *            part
     * @param minorNumber
     *            part
     * @param microNumber
     *            part
     * @param versionQualifier
     *            part
     * @return a key combined from the parts
     */
    private static String buildProjectKey(String projectGuid,
            Integer majorNumber, Integer minorNumber,
            Integer microNumber, String versionQualifier) {
        StringBuilder idBuilder = new StringBuilder(200);
        idBuilder.append(projectGuid);
        idBuilder.append(StringConstants.COLON);
        idBuilder.append(majorNumber);
        idBuilder.append(StringConstants.COLON);
        idBuilder.append(minorNumber);
        idBuilder.append(StringConstants.COLON);
        idBuilder.append(microNumber);
        idBuilder.append(StringConstants.COLON);
        idBuilder.append(versionQualifier);
        return idBuilder.toString();
    }

    /**
     * Finds a test case within the project with the given ID.
     * @param projectId The ID of the parent project of the spec testcase
     * @param specTcGuid The GUID of the spec testcase
     * @return the spec testcase with the given guid, or <code>null</code> if 
     *         the testcase cannot be found
     */
    public static synchronized ISpecTestCasePO getSpecTestCase(Long projectId, 
        String specTcGuid) {
       
        EntityManager s = GeneralStorage.getInstance().getMasterSession();
        return (ISpecTestCasePO)NodePM.getInstance().findSpecTC(s, specTcGuid,
                projectId);
    }
    
    /**
     * @param child witch parent of node searched
     * @return the first SpecTestCase parent
     */
    public static ISpecTestCasePO getSpecTestCaseParent(INodePO child) {
        if (child == null) {
            return null;
        }
        INodePO node = child;
        if (node instanceof ISpecTestCasePO) {
            return (ISpecTestCasePO)node;
        }
        while (node.getParentNode() != null) {
            node = node.getParentNode();
            if (node instanceof ISpecTestCasePO) {
                return (ISpecTestCasePO)node;
            }
        }
        return null;
    }
    
    /**
     * Finds a Test Suite within the currently opened project.
     * 
     * @param testSuiteGuid The GUID of the Test Suite.
     * @return the Test Suite with the given GUID, or <code>null</code> if 
     *         no such Test Suite can be found.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static synchronized ITestSuitePO getTestSuite(
            String testSuiteGuid) {
       
        GeneralStorage gs = GeneralStorage.getInstance();
        IProjectPO currentProject = gs.getProject();
        if (currentProject != null) {
            EntityManager s = gs.getMasterSession();
            
            CriteriaBuilder builder = s.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery();
            Root from = query.from(NodeMaker.getTestSuitePOClass());
            query.select(from).where(
                builder.like(from.get("guid"), testSuiteGuid),  //$NON-NLS-1$
                builder.equal(
                    from.get("hbmParentProjectId"), currentProject.getId())); //$NON-NLS-1$

            try {
                Object result = s.createQuery(query).getSingleResult();
                if (result instanceof ITestSuitePO) {
                    return (ITestSuitePO)result;
                }
            } catch (NoResultException nre) {
                // No result found. Fall through to return null as per javadoc.
            }
        }
        
        return null;
    }
    
    /**
     * Finds a node within the project with the given ID.
     * @param projectId The ID of the parent project of the spec testcase
     * @param nodeGuid The GUID of the node
     * @return the spec testcase with the given guid, or <code>null</code> if 
     *         the testcase cannot be found
     */
    public static synchronized INodePO getNode(Long projectId, 
            String nodeGuid) {
        return getNode(projectId, nodeGuid, 
                GeneralStorage.getInstance().getMasterSession());
    }
    
    /**
     * Finds a node within the project with the given ID.
     * @param projectId The ID of the parent project of the spec testcase
     * @param nodeGuid The GUID of the node
     * @param session may not be null
     * @return the spec testcase with the given guid, or <code>null</code> if 
     *         the testcase cannot be found
     */
    public static synchronized INodePO getNode(Long projectId, String nodeGuid,
            EntityManager session) {
        
        Validate.notNull(session);
        
        Query specTcQuery = session.createQuery("select node from NodePO node where node.guid = :guid" //$NON-NLS-1$
                + " and node.hbmParentProjectId = :projectId"); //$NON-NLS-1$
        specTcQuery.setParameter("guid", nodeGuid); //$NON-NLS-1$
        specTcQuery.setParameter("projectId", projectId); //$NON-NLS-1$
 
        try {
            Object result = specTcQuery.getSingleResult();
            if (result instanceof INodePO) {
                return (INodePO)result;
            }
        } catch (NoResultException nre) {
            // No result found. Fall through to return null.
        }
        return null;
    }
    
    /**
     * Loads a bag of Nodes into the given session and returns the loaded
     * Nodes.
     * 
     * @param projectId The Project in which to search for the Nodes.
     * @param guids The GUIDs for which to load Nodes.
     * @param session The session into which to load the Nodes.
     * @return the loaded Nodes, mapped by GUID. GUIDs for which no node could
     *         be found are mapped to <code>null</code>.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static synchronized Map<String, INodePO> getNodes(Long projectId, 
            Collection<String> guids, EntityManager session) {
        
        CriteriaQuery query = session.getCriteriaBuilder().createQuery();
        Root from = query.from(NodeMaker.getNodePOClass());
        Predicate parentProjectPred = session.getCriteriaBuilder().equal(
                from.get("hbmParentProjectId"), projectId); //$NON-NLS-1$
        Predicate guidDisjunction = PersistenceUtil.getExpressionDisjunction(
                guids, from.get("guid"), session.getCriteriaBuilder()); //$NON-NLS-1$
        query.select(from).where(parentProjectPred, guidDisjunction);
        
        List<INodePO> nodeList = session.createQuery(query).getResultList();

        Map<String, INodePO> guidToNodeMap = new HashMap<String, INodePO>();
        for (INodePO node : nodeList) {
            String guid = node.getGuid();
            if (!guidToNodeMap.containsKey(guid)) {
                guidToNodeMap.put(guid, node);
            }
        }

        return guidToNodeMap;
    }

    /**
     * @param parentProjectId The ID of the project for which to find the number
     *                        of nodes.
     * @param sess The session in which to perform the query.
     * @return The number of nodes that have the project for the given ID as 
     *         an absolute parent.
     */
    public static long getNumNodes(long parentProjectId, EntityManager sess) {
        
        Query specTcQuery =
            sess.createQuery("select count(node) from NodePO as " //$NON-NLS-1$
                    + "node where node.hbmParentProjectId = :parentProjectId"); //$NON-NLS-1$
        specTcQuery.setParameter("parentProjectId", parentProjectId); //$NON-NLS-1$
        
        try {
            return (Long)specTcQuery.getSingleResult();
        } catch (NoResultException nre) {
            return 0;
        }
        
    }

    /**
     * @param parentProjectId The ID of the project for which to find the number
     *                        of TD managers.
     * @param sess The session in which to perform the query.
     * @return The number of TD managers that have the project for the 
     *         given ID as an absolute parent.
     */
    public static long getNumTestDataManagers(
            long parentProjectId, EntityManager sess) {
        
        Query tdManQuery = sess.createQuery("select count(tdMan) from TDManagerPO " //$NON-NLS-1$
                    + "tdMan where tdMan.hbmParentProjectId = :parentProjectId"); //$NON-NLS-1$
        tdManQuery.setParameter("parentProjectId", parentProjectId); //$NON-NLS-1$

        try {
            return (Long)tdManQuery.getSingleResult();
        } catch (NoResultException nre) {
            return 0;
        }
    }

    /**
     * @param parentProjectId The ID of the project for which to find the number
     *                        of execTCs.
     * @param sess The session in which to perform the query.
     * @return The number of execTCs that have the project for the 
     *         given ID as an absolute parent.
     */
    public static long getNumExecTestCases(
            long parentProjectId, EntityManager sess) {
        
        Query execTcQuery =
            sess.createQuery("select count(execTc) from ExecTestCasePO as " //$NON-NLS-1$
                    + "execTc where execTc.hbmParentProjectId = :parentProjectId"); //$NON-NLS-1$
        execTcQuery.setParameter("parentProjectId", parentProjectId); //$NON-NLS-1$

        try {
            return (Long)execTcQuery.getSingleResult();
        } catch (NoResultException nre) {
            return 0;
        }
    }

    /**
     * @param parentProjectId The ID of the project for which to find the number
     *                        of execTCs.
     * @param sess The session in which to perform the query.
     * @return The number of execTCs that have the project for the 
     *         given ID as an absolute parent.
     */
    public static long getNumExecTestCasesWithRefTd(
            long parentProjectId, EntityManager sess) {
        
        Query execTcQuery =
            sess.createQuery("select count(execTc) from ExecTestCasePO as " //$NON-NLS-1$
                    + "execTc where execTc.hbmParentProjectId = :parentProjectId and execTc.hasReferencedTD = :hasReferencedTD"); //$NON-NLS-1$
        execTcQuery.setParameter("parentProjectId", parentProjectId); //$NON-NLS-1$
        execTcQuery.setParameter("hasReferencedTD", true); //$NON-NLS-1$

        try {
            return (Long)execTcQuery.getSingleResult();
        } catch (NoResultException nre) {
            return 0;
        }
    }

    /**
     * @param useCache should the cache be used
     */
    public void setUseCache(boolean useCache) {
        m_useCache = useCache;
        resetCaching();
    }

    /**
     * @return true if the internal cache is in use
     */
    public boolean isUseCache() {
        return m_useCache;
    }
    
    /**
     * Class to collect nodes with tracked changes
     * @author BREDEX GmbH
     * @created 05.11.2013
     */
    private static class CollectNodesWithTrackedChangesOperation 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
        
        /**
         * list of nodes with tracked changes
         */
        private List<INodePO> m_listOfNodesWithTrackedChanges = 
                new ArrayList<INodePO>();
        
        /**
         * project which contains the nodes
         */
        private IProjectPO m_project;
        
        /**
         * the constructor
         * @param project the project which contains the nodes
         */
        public CollectNodesWithTrackedChangesOperation(IProjectPO project) {
            m_project = project;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            Long parentProjectId = node.getParentProjectId();
            if (parentProjectId != null
                    && !m_project.getId().equals(parentProjectId)) {
                return false;
            }
            if (!node.getTrackedChanges().isEmpty()) {
                m_listOfNodesWithTrackedChanges.add(node);
            }
            return true;
        }
        
        /**
         * returns the list of nodes with tracked changes
         * @return the list of nodes with tracked changes
         */
        public List<INodePO> getListOfNodesWithTrackedChanges() {
            return m_listOfNodesWithTrackedChanges;
        }
    }
    
    /**
     * Deletes all tracked changes of a project
     * @param monitor the monitor
     * @param project the project
     * @return the map from changed nodes to whether they were locked (for which tracked changes could not be deleted)
     * @throws ProjectDeletedException 
     * @throws PMException
     */
    public static Map<INodePO, Boolean> cleanupTrackedChanges(
            IProgressMonitor monitor, final IProjectPO project) 
        throws PMException, ProjectDeletedException {
        
        CollectNodesWithTrackedChangesOperation treeNodeOp = 
                new CollectNodesWithTrackedChangesOperation(project);
        
        TreeTraverser treeTraverser = new TreeTraverser(
                project, treeNodeOp, true, true);
        treeTraverser.traverse();
        
        List<INodePO> listOfNodesWithTrackedChanges = 
                treeNodeOp.getListOfNodesWithTrackedChanges();
        
        Map<INodePO, Boolean> nodeToWasLockedMap = new HashMap<>(
                listOfNodesWithTrackedChanges.size());
        
        monitor.beginTask(Messages.DeleteTrackedChangesActionDialog, 
                listOfNodesWithTrackedChanges.size());

        final Persistor persistor = Persistor.instance();
        final EntityManager session = persistor.openSession();
        EntityTransaction tx = null;
        tx = persistor.getTransaction(session);
        
        for (INodePO node: listOfNodesWithTrackedChanges) {
            try {
                persistor.lockPO(session, node);
                node.deleteTrackedChanges();
                session.merge(node);
                nodeToWasLockedMap.put(node, new Boolean(false));
            } catch (PMException | PersistenceException e) {
                // can not delete tracked changes of this node
                nodeToWasLockedMap.put(node, new Boolean(true));
            }
            monitor.worked(1);
        }
        
        persistor.commitTransaction(session, tx);
        persistor.dropSession(session);
        EntityManager master = GeneralStorage.getInstance().getMasterSession();
        for (INodePO key : nodeToWasLockedMap.keySet()) {
            if (!nodeToWasLockedMap.get(key)) {
                INodePO refr = master.find(key.getClass(), key.getId());
                if (refr != null) {
                    master.refresh(refr);
                }
            }
        }
        
        monitor.done();
        return nodeToWasLockedMap;
    }

    /**
     * Returns a node by its id. The node is managed by the master (RO) session
     * @param id the id
     * @return the node or null if not found
     */
    public static INodePO findNodeById(Long id) {
        EntityManager em = GeneralStorage.getInstance().getEntityManager();
        Query q = em.createQuery("select node from NodePO node where node.id =:id"); //$NON-NLS-1$
        q.setParameter("id", id); //$NON-NLS-1$
        Object res = null;
        try {
            res = q.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        if (res != null) {
            return (INodePO) res;
        }
        return null;
    }

    /**
     * @param projIds the project ids to search in
     * @param specTCGuids spec TC guids
     * @param sess the EntityManager to use
     * @return all ExecTCs from the given projects which reference
     *      one of the given SpecTCs
     */
    @SuppressWarnings("nls")
    public static List<IExecTestCasePO> findExecTCsByRefSpecTCAndProject(
            Set<Long> projIds, List<String> specTCGuids,
            EntityManager sess) {
        TypedQuery<IExecTestCasePO> q = sess.createQuery(
                "select exec from ExecTestCasePO exec "
                + "where exec.hbmParentProjectId in :projIds "
                + "and exec.specTestCaseGuid in :specTCGuids "
                + "and exec.projectGuid = :currProjGuid",
                IExecTestCasePO.class);
        q.setParameter("projIds", projIds);
        q.setParameter("currProjGuid",
                GeneralStorage.getInstance().getProject().getGuid());
        ValueListIterator listIter = new ValueListIterator(specTCGuids);
        List<IExecTestCasePO> result = new ArrayList<>();
        while (listIter.hasNext()) {
            q.setParameter("specTCGuids", listIter.nextList());
            result.addAll(q.getResultList());
        }
        return result;
    }

    /**
     * @param spec the {@link ISpecTestCasePO}
     * @param selectedItems the new {@link IObjectMappingCategoryPO} to set
     */
    public static void setOMAssoc(ISpecTestCasePO spec,
            Collection<IObjectMappingCategoryPO> selectedItems) 
                    throws PMException, ProjectDeletedException {
        EntityTransaction tx = null;
        Persistor per = Persistor.instance();
        EntityManager session = per.openSession(); 
        ISpecTestCasePO sessionSpec =
                session.find(spec.getClass(), spec.getId());
        tx = per.getTransaction(session);
        per.lockPO(session, spec);
        sessionSpec.setOmCategoryAssoc(new ArrayList<>(selectedItems));
        per.commitTransaction(session, tx);
        refreshMasterSession(spec);
    }

}

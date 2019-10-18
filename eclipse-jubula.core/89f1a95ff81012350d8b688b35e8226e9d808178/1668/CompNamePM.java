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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.progress.OperationCanceledUtil;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.core.persistence.locking.LockedObjectPO;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.JBFatalAbortException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Logic to persist Component Names.
 *
 * @author BREDEX GmbH
 * @created Apr 8, 2008
 */
public class CompNamePM extends AbstractNamePM {

    /**
     * <code>COMP_NAME_TABLE_ID</code>
     */
    private static final String COMP_NAME_TABLE_ID = "org.eclipse.jubula.client.core.model.ComponentNamePO"; //$NON-NLS-1$

    /**
     * Query Parameter for component name guid.
     */
    private static final String P_COMP_NAME_GUID = "compNameGuid"; //$NON-NLS-1$

    /**
     * Query Parameter for parent project id.
     */
    private static final String P_PARENT_PROJECT_ID = "parentProjectId"; //$NON-NLS-1$

    /** Query Parameter for single Logical Names */
    private static final String P_NAME = "logName"; //$NON-NLS-1$

    /** Query to find a preexisting Component Name */
    private static final String Q_PREEX_NAMES_SINGLE =
        "select compName from ComponentNamePO compName where compName.hbmParentProjectId = :"  //$NON-NLS-1$
        + P_PARENT_PROJECT_ID + " and compName.hbmName = :" + P_NAME;   //$NON-NLS-1$

    /**
     * Query to find the number of Comp Name Pairs having a CN as their first or second CN
     */
    private static final String Q_NUM_REUSE_TYPE_PAIRS = 
        "select count(compNamePair) from CompNamesPairPO as compNamePair" //$NON-NLS-1$
        + " where (compNamePair.secondName = :" + P_COMP_NAME_GUID //$NON-NLS-1$
        + " or compNamePair.firstName = :" + P_COMP_NAME_GUID //$NON-NLS-1$
        + ") and compNamePair.hbmParentProjectId = :" + P_PARENT_PROJECT_ID; //$NON-NLS-1$

    /**
     * Query to find the types of reuse of a component name by test steps.
     */
    private static final String Q_REUSE_TYPE_CAPS_COUNT = 
        "select count (cap.componentType) from CapPO as cap" //$NON-NLS-1$
        + " where cap.componentName = :" + P_COMP_NAME_GUID //$NON-NLS-1$
        + " and cap.hbmParentProjectId = :" + P_PARENT_PROJECT_ID; //$NON-NLS-1$
    
    /**
     * Query to find the GUIDs of all Component Names within a given Project 
     * that reference other Component Names.
     */
    private static final String Q_REF_COMP_NAME_GUIDS = 
        "select compName.hbmGuid from ComponentNamePO as compName" //$NON-NLS-1$
        + " where compName.hbmReferencedGuid is not null" //$NON-NLS-1$
        + " and compName.hbmParentProjectId = :" + P_PARENT_PROJECT_ID; //$NON-NLS-1$

    /**
     * Query to find the GUIDs of all Component Names that are referenced by
     * one or more Test Steps in a given Project.
     */
    private static final String Q_CAP_COMP_NAME_GUIDS = 
        "select cap.componentName from CapPO as cap" //$NON-NLS-1$
        + " where cap.hbmParentProjectId = :" + P_PARENT_PROJECT_ID; //$NON-NLS-1$
    
    /** Query to find the number of non-trivial associations of a CN */
    private static final String Q_ASSOC_COUNT = 
        " select count(assoc) from ObjectMappingAssoziationPO as assoc" //$NON-NLS-1$
        + " join assoc.logicalNames as logical" //$NON-NLS-1$
        + " where logical = :" + P_COMP_NAME_GUID //$NON-NLS-1$
        + " and assoc.technicalName is not null" //$NON-NLS-1$
        + " and assoc.hbmParentProjectId = :" + P_PARENT_PROJECT_ID; //$NON-NLS-1$

    /**
     * Query to find the GUIDs of all Component Names that are referenced by
     * the First Name of one or more Component Name Pairs in a given Project.
     */
    private static final String Q_PAIR_FIRST_COMP_NAME_GUIDS = 
        "select pair.firstName from CompNamesPairPO as pair" //$NON-NLS-1$
        + " where pair.hbmParentProjectId = :" + P_PARENT_PROJECT_ID; //$NON-NLS-1$

    /**
     * Query to find the GUIDs of all Component Names that are referenced by
     * the Second Name of one or more Component Name Pairs in a given Project.
     */
    private static final String Q_PAIR_SECOND_COMP_NAME_GUIDS = 
        "select pair.secondName from CompNamesPairPO as pair" //$NON-NLS-1$
        + " where pair.hbmParentProjectId = :" + P_PARENT_PROJECT_ID; //$NON-NLS-1$

    /**
     * Query to find the GUIDs of all Component Names that are referenced by
     * one or more Object Mapping Associations in a given Project.
     */
    private static final String Q_ASSOC_COMP_NAME_GUIDS = 
        "select logicalName from ObjectMappingAssoziationPO as assoc" //$NON-NLS-1$
        + " join assoc.logicalNames as logicalName" //$NON-NLS-1$
        + " where assoc.hbmParentProjectId = :" + P_PARENT_PROJECT_ID; //$NON-NLS-1$

    /**
     * Query to find the GUIDs of all Component Names that are referenced by
     * one or more other Component Names in a given Project.
     */
    private static final String Q_COMP_NAME_REF_GUIDS = 
        "select compName.hbmReferencedGuid from ComponentNamePO as compName" //$NON-NLS-1$
        + " where compName.hbmReferencedGuid is not null" //$NON-NLS-1$
        + " and compName.hbmParentProjectId = :" + P_PARENT_PROJECT_ID; //$NON-NLS-1$

    /** GUIDs of Component Names to delete from the DB */
    private static final String P_COMP_NAME_REMOVAL_LIST = "compNameRemovalList"; //$NON-NLS-1$

    /**
     * "Query" to delete all Component Names based on GUID and parent Project.
     */
    private static final String Q_DELETE_COMP_NAMES = 
        "delete from ComponentNamePO compName" //$NON-NLS-1$
        + " where compName.hbmParentProjectId = :" + P_PARENT_PROJECT_ID //$NON-NLS-1$
        + " and compName.hbmGuid in :" + P_COMP_NAME_REMOVAL_LIST; //$NON-NLS-1$

    /**
     * Class used to collect data required after executing a save
     * @author BREDEX GmbH
     */
    public static class SaveCompNamesData {
        /** The Component Names managed by the save session */
        private List<IComponentNamePO> m_dbVersions;
        
        /** The Component Name guids that need to be changed due to duplicate logical names */
        private Map<String, String> m_guidsToSwap;
        
        /**
         * Constructor
         * @param dbVersions the managed Component Names
         * @param guidsToSwap the required guid swaps
         */
        public SaveCompNamesData(List<IComponentNamePO> dbVersions,
                Map<String, String> guidsToSwap) {
            m_dbVersions = dbVersions;
            m_guidsToSwap = guidsToSwap;
        }
        
        /**
         * returns the DB Versions
         * @return the DB Versions
         */
        public List<IComponentNamePO> getDBVersions() {
            return m_dbVersions;
        }
        
        /**
         * returns the guids to swap
         * @return the guids to swap map
         */
        public Map<String, String> getGuidsToSwap() {
            return m_guidsToSwap;
        }
    }
    
    /**
     * <code>log</code>logger
     */
    private static Logger log = LoggerFactory.getLogger(ParamNamePM.class);
    
    /** The ILockedObjectPO for locks on ComponentNames tabe in database */
    private static LockedObjectPO lockObj = null;
    
    /**
     * Read component names from the master session
     * @param parentProjectId id from root project
     * @return list of all param name objects for given project
     * @throws PMException in case of any db problem
     */
    public static final List<IComponentNamePO> readAllCompNamesRO(
            Long parentProjectId) throws PMException {

        EntityManager s = GeneralStorage.getInstance().getMasterSession();

        return readAllCompNames(parentProjectId, s);
    }

    /**
     * @param parentProjectId id from root project
     * @param s The session to use for the query.
     * @return list of all param name objects for given project
     * @throws PMException in case of any db problem
     */
    @SuppressWarnings("unchecked")
    private static final List<IComponentNamePO> readAllCompNames(
        Long parentProjectId, EntityManager s) throws PMException {

        final List <IComponentNamePO> compNames = 
            new ArrayList<IComponentNamePO>();
        try {
            final Query q = s.createQuery(
                    "select compName from ComponentNamePO compName where compName.hbmParentProjectId = :parentProjId"); //$NON-NLS-1$
            q.setParameter("parentProjId", parentProjectId); //$NON-NLS-1$
            compNames.addAll(q.getResultList());
        } catch (PersistenceException e) {
            OperationCanceledUtil.checkForOperationCanceled(e);
            log.error(Messages.CouldNotReadComponentNamesFromDBOfProjectWithID
                + StringConstants.SPACE + StringConstants.APOSTROPHE
                + String.valueOf(parentProjectId) + StringConstants.APOSTROPHE, 
                     e);
            PersistenceManager.handleDBExceptionForAnySession(null, e, s);
        }
        return compNames;
    }
    
    /**
     * deletes all ComponentNames of the Project with the 
     * given rootProjId from DataBase without a commit!
     * @param s the Session which is to use.
     * @param rootProjId the parent project ID.
     * @throws PMException in case of any db problem
     */
    public static final void deleteCompNames(EntityManager s, Long rootProjId) 
        throws PMException {
        
        try {
            lockComponentNames(s);
            final Query q = s.createQuery(
                    "delete from ComponentNamePO c where c.hbmParentProjectId = :rootProjId"); //$NON-NLS-1$
            q.setParameter("rootProjId", rootProjId); //$NON-NLS-1$
            q.executeUpdate();
        } catch (PersistenceException e) {
            OperationCanceledUtil.checkForOperationCanceled(e);
            log.error(Messages.CouldNotReadComponentNamesFromDBOfProjectWithID
                + StringConstants.SPACE + StringConstants.APOSTROPHE
                + String.valueOf(rootProjId) + StringConstants.APOSTROPHE, e);
            PersistenceManager.handleDBExceptionForAnySession(null, e, s);
        }
    }
    
    /**
     * Gets a lock of the ComponentNames Table in database.
     * @param s A Session.
     * @throws PMAlreadyLockedException if no lock is available.
     */
    private static final void lockComponentNames(EntityManager s) 
        throws PMObjectDeletedException, PMAlreadyLockedException {
        
        final long timeOut = 5000;
        try {
            if (lockObj == null) {
                initLockedObj();
            }
            final long start = System.currentTimeMillis();
            while (!LockManager.instance().lockPO(s, lockObj, false)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // nothing is OK here!
                }
                final long stop = System.currentTimeMillis();
                if ((stop - start) > timeOut) {
                    throw new PMAlreadyLockedException(lockObj, 
                            Messages.CouldNotGetALockOnTableCOMPONENT_NAMES,
                            MessageIDs.E_DATABASE_GENERAL);
                }
            }
        } catch (PMDirtyVersionException e) {
            // cannot happen because checkVersion == false!
            Assert.notReached(Messages.ExceptionShouldNotHappen 
                + StringConstants.COLON + StringConstants.SPACE + e);
        }
    }

    /**
     * Releases the lock of the ComponentNames Table in database.
     */
    private static final void unlockComponentNames() {
        if (lockObj != null) {
            LockManager.instance().unlockPO(lockObj);
        }
    }

    /**
     * Initializes the locking of the CompNames table
     */
    private static void loadLockedObj() {
        EntityManager sess = null;
        try {
            sess = Persistor.instance().openSession();
            EntityTransaction tx = sess.getTransaction();
            tx.begin();
            
            final Query q = sess.createQuery(
                    "select p from LockedObjectPO p where p.hbmObjectName = :hbmObjectName"); //$NON-NLS-1$
            q.setParameter("hbmObjectName", COMP_NAME_TABLE_ID); //$NON-NLS-1$
            
            try {
                lockObj = (LockedObjectPO)q.getSingleResult();
            } catch (NoResultException nre) {
                lockObj = null;
            }
            
            tx.commit();
        } catch (PersistenceException e) {
            throw new JBFatalAbortException(
                    Messages.ErrorInitializingComponentNamesLocking
                            + StringConstants.EXCLAMATION_MARK,
                    e, MessageIDs.E_DATABASE_GENERAL);
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(sess);
        }
    }
    
    /**
     * Initializes the LockedObj.
     */
    private static void initLockedObj() {
        createOrUpdateCompNamesLock();
        loadLockedObj();
    }
   
    /**
     * 
     * @throws PersistenceException if we blow up
     */
    private static void createOrUpdateCompNamesLock() 
        throws PersistenceException {
        
        EntityManager s = null;
        EntityTransaction tx = null;
        try {
            s = Persistor.instance().openSession();
            tx = s.getTransaction();
            tx.begin();
            final Query q = s.createQuery(
                    "select p from LockedObjectPO p where p.hbmObjectName = :hbmObjectName"); //$NON-NLS-1$
            q.setParameter("hbmObjectName", COMP_NAME_TABLE_ID); //$NON-NLS-1$
            try {
                q.getSingleResult();
            } catch (NoResultException nre) {
                s.persist(new LockedObjectPO(COMP_NAME_TABLE_ID));
            }

            tx.commit();
        } catch (PersistenceException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            Persistor.instance().dropSession(s);
        }
    }

    /**
     * Get the number of all current types of reuse for the
     * Component Name with the given GUID.
     * 
     * @param session
     *            The session in which to execute the various queries required
     *            to find the types of reuse.
     * @param parentProjectId
     *            The id of the active project. Only reuses and component names
     *            belonging to the project with this id will be considered
     *            during the check.
     * @param compNameGuid
     *            The guid of the component name to check.
     * @return number of the reusage.
     */
    public static synchronized long getNumberOfUsages(
            EntityManager session, Long parentProjectId, 
            String compNameGuid) {
 
        // Number of references of the given component name
        long referenceCount = 0;
        
        FlushModeType flushMode = session.getFlushMode();
        // Disable automatic flushing during this read-only operation because
        // a flush may cause database-level locks to be acquired.
        session.setFlushMode(FlushModeType.COMMIT);
        
        try {
            referenceCount += countOfReusedCompnameTypesInCaps(session,
                    parentProjectId, compNameGuid);
            
            IProjectPO inSessionProject = session
                    .find(NodeMaker.getProjectPOClass(), parentProjectId);
            
            referenceCount += getAutAssociations(session, parentProjectId,
                    compNameGuid);
            
            referenceCount += getNumPairs(compNameGuid, parentProjectId,
                    session);
            
        } finally {
            session.setFlushMode(flushMode);
        }
        return referenceCount;
    }

    /**
     * Get associated component types
     * 
     * @param session
     *            The session in which to execute the various queries required
     *            to find the types of reuse.
     * @param parentProjectId
     *            The id of the active project. Only reuses and component names
     *            belonging to the project with this id will be considered
     *            during the check.
     * @param compNameGuid
     *            compNameGuid The guid of the component name to check.
     * @param assocCompTypes
     * @param compSystem
     * @param allAutsForProject
     * @return associated component types
     */
    private static long getAutAssociations(EntityManager session,
            Long parentProjectId, String compNameGuid) {

        StringBuilder capQuerySb = new StringBuilder(Q_ASSOC_COUNT);

        final Query capQuery = session.createQuery(capQuerySb.toString());
        capQuery.setParameter(P_PARENT_PROJECT_ID, parentProjectId);
        capQuery.setParameter(P_COMP_NAME_GUID, compNameGuid);
        long res = (long) capQuery.getSingleResult();
        return res;
    }

    /**
     * @param session
     *            the entity manager session
     * @param parentProjectId
     *            The id of the active project. Only reuses and component names
     *            belonging to the project with this id will be considered
     *            during the check.
     * @param compNameGuid
     *            The guid of the component name to check.
     * @return number of the types of reuse of a component name by test steps
     */
    private static long countOfReusedCompnameTypesInCaps(EntityManager session,
            Long parentProjectId, String compNameGuid) {

        StringBuilder capQuerySb = new StringBuilder(Q_REUSE_TYPE_CAPS_COUNT);

        final Query capQuery = session.createQuery(capQuerySb.toString());
        capQuery.setParameter(P_PARENT_PROJECT_ID, parentProjectId);
        capQuery.setParameter(P_COMP_NAME_GUID, compNameGuid);
        return (long) capQuery.getSingleResult();
    }
    
    /**
     * Get reused component names.
     * @param compNameGuid The guid of the component name to check.
     * @param parentProjectId The id of the active project. Only reuses and 
     *                        component names belonging to the project with this
     *                        id will be considered during the check.
     * @param session The session in which to execute the various queries required to
     *          find the types of reuse.
     * @return reused component names
     */
    private static long getNumPairs(
            String compNameGuid, Long parentProjectId, EntityManager session) {
        StringBuilder reuseQuerySb = 
            new StringBuilder(Q_NUM_REUSE_TYPE_PAIRS);
        final Query reuseQuery = session.createQuery(reuseQuerySb.toString());
        reuseQuery.setParameter(P_PARENT_PROJECT_ID, parentProjectId);
        reuseQuery.setParameter(P_COMP_NAME_GUID, compNameGuid);
        Collection<ICompNamesPairPO> compNamePairs = 
            new HashSet<ICompNamesPairPO>();
        return (long) reuseQuery.getResultList().get(0);
    }
    
    /**
     * Merges the Component Names from the cache to the session
     * @param sess the session
     * @param projId the ID of the project
     * @param cache the cache
     * @return the data
     */
    public static SaveCompNamesData flushCompNames(EntityManager sess,
            Long projId, IWritableComponentNameCache cache) {
        ArrayList<IComponentNamePO> dbVersions = new ArrayList<>();
        HashMap<String, String> guidsToSwap = new HashMap<>();
        Map<String, IComponentNamePO> localChanges = cache.getLocalChanges();
        if (localChanges == null) {
            // avoiding NullComponentNameMappers
            return null;
        }
        IComponentNamePO dbPO = null;
        IComponentNamePO newCN = null;
        Query q = sess.createQuery(Q_PREEX_NAMES_SINGLE);
        q.setParameter(P_PARENT_PROJECT_ID, projId).setMaxResults(1);
        List<IComponentNamePO> resList;
        for (String guid : localChanges.keySet()) {
            
            // First we check whether a Component Name with the same logical name
            //     and current projectId already exists in the DB
            // If it does and has a different guid, we are going to use the DB version everywhere
            newCN = localChanges.get(guid);
            if (newCN.getParentProjectId() == null
                    || projId.equals(newCN.getParentProjectId())) {
                q.setParameter(P_NAME, newCN.getName());
                resList = q.getResultList();
                if (!resList.isEmpty()) {
                    if (!resList.get(0).getGuid().equals(guid)) {
                        newCN = resList.get(0);
                        guidsToSwap.put(guid, resList.get(0).getGuid());
                    } else {
                        // Component Name with the same guid exists in DB
                        // If it was created parallelly in separate editors
                        // Then the current one may have a null id
                        newCN.setId(resList.get(0).getId());
                    }
                    sess.detach(resList.get(0));
                }
            }
            
            // If Component Name has been removed from the DB by another user
            // we repersist it...
            dbPO = null;
            if (newCN.getId() != null) {
                dbPO = sess.find(newCN.getClass(), newCN.getId());
            }
            if (dbPO == null) {
                sess.persist(newCN);
                dbPO = newCN;
            }
            if (newCN.getName() != null) {
                dbPO.setName(newCN.getName());
            }
            if (dbPO.getParentProjectId() == null) {
                dbPO.setParentProjectId(projId);
            }
            
            dbVersions.add(dbPO);
        }
        if (!guidsToSwap.isEmpty()) {
            cache.handleExistingNames(guidsToSwap);
        }
        return new SaveCompNamesData(dbVersions, guidsToSwap);
    }
    
    /**
     * Merges the Component Names from the cache to the session
     * @param sess the session
     * @param projId the ID of the project
     * @param cache the cache
     */
    public static void flushCompNamesImport(EntityManager sess,
            Long projId, IWritableComponentNameCache cache) {
        Map<String, IComponentNamePO> localChanges = cache.getLocalChanges();
        for (String guid : localChanges.keySet()) {
            IComponentNamePO cN = localChanges.get(guid);
            if (cN.getParentProjectId() == null) {
                cN.setParentProjectId(projId);
            }
            sess.persist(localChanges.get(guid));
        }
    }

    /**
     * Deletes all unused Component Names that reference other Component Names.
     * Will only delete Component Names belonging to the Project with the given
     * ID. The search for reuse instances is also limited to the scope of the 
     * Project with the given ID.
     * 
     * @param projectId The ID of the Project to use as the scope for this
     *                  operation.
     * @param session The session in which the operation will take place.
     */
    @SuppressWarnings("unchecked")
    public static void removeUnusedCompNames(
            Long projectId, EntityManager session) {

        Query refCompNameGuidQuery = session.createQuery(Q_REF_COMP_NAME_GUIDS);
        refCompNameGuidQuery.setParameter(P_PARENT_PROJECT_ID, projectId);
        List refCompNameGuids = refCompNameGuidQuery.getResultList();

        if (refCompNameGuids.isEmpty()) {
            return;
        }
        
        Query capQuery = session.createQuery(Q_CAP_COMP_NAME_GUIDS);
        capQuery.setParameter(P_PARENT_PROJECT_ID, projectId);
        List capCompNames = capQuery.getResultList();
        
        refCompNameGuids.removeAll(capCompNames);

        if (refCompNameGuids.isEmpty()) {
            return;
        }
        
        Query pairQuery = session.createQuery(Q_PAIR_FIRST_COMP_NAME_GUIDS);
        pairQuery.setParameter(P_PARENT_PROJECT_ID, projectId);
        List pairCompNameGuids = pairQuery.getResultList();

        refCompNameGuids.removeAll(pairCompNameGuids);

        if (refCompNameGuids.isEmpty()) {
            return;
        }

        pairQuery = session.createQuery(Q_PAIR_SECOND_COMP_NAME_GUIDS);
        pairQuery.setParameter(P_PARENT_PROJECT_ID, projectId);
        pairCompNameGuids = pairQuery.getResultList();

        refCompNameGuids.removeAll(pairCompNameGuids);

        if (refCompNameGuids.isEmpty()) {
            return;
        }

        Query assocQuery = session.createQuery(Q_ASSOC_COMP_NAME_GUIDS);
        assocQuery.setParameter(P_PARENT_PROJECT_ID, projectId);
        List assocCompNameGuids = assocQuery.getResultList();
        
        refCompNameGuids.removeAll(assocCompNameGuids);

        if (refCompNameGuids.isEmpty()) {
            return;
        }

        Query compNameRefQuery = session.createQuery(Q_COMP_NAME_REF_GUIDS);
        compNameRefQuery.setParameter(P_PARENT_PROJECT_ID, projectId);
        List compNameRefGuidList = compNameRefQuery.getResultList();
        
        refCompNameGuids.removeAll(compNameRefGuidList);
        
        if (refCompNameGuids.isEmpty()) {
            return;
        }
        
        Query deleteQuery = session.createQuery(Q_DELETE_COMP_NAMES);
        deleteQuery.setParameter(P_PARENT_PROJECT_ID, projectId);
        deleteQuery.setParameter(P_COMP_NAME_REMOVAL_LIST, refCompNameGuids);
        deleteQuery.executeUpdate();
        
    }
    
    /**
     * sets lockObj to null, when database is changed
     */
    public static void dispose() {
        lockObj = null;
    }

}

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IProjectNamePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.utils.NameValidationUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Business processes for the project name <=> GUID mapping information.
 *
 * @author BREDEX GmbH
 * @created Jun 20, 2007
 */
public class ProjectNameBP {

    /** standard logging */
    private static Logger log = LoggerFactory.getLogger(ProjectNameBP.class);

    /**
     * The singleton instance.
     */
    private static ProjectNameBP instance = null;
    
    /** cache for project names */
    private Map<String, String> m_names;

    /** cache for work versions of names */
    private Map<String, String> m_transientNames;
    
    /**
     * private utility constructor
     */
    private ProjectNameBP() {
        m_names = new HashMap<String, String>();
        m_transientNames = new HashMap<String, String>();
    }
        
    /**
     * Method makes use of the project name cache
     * @param guid id for the project name
     * @return the name associated with guid or null if no such name exists
     */
    public String getName(String guid) {
        return getName(guid, true);
    }
    
    /**
     * @param guid id for the project name
     * @param useCache whether to use the project name cache or not
     * @return the name associated with guid or null if no such name exists
     */
    public String getName(String guid, boolean useCache) {
        if (guid == null) {
            return null;
        }
        String res =  null;
        if (useCache) {
            res = m_transientNames.get(guid);
            if (res == null && m_names.containsKey(guid)) {
                return m_names.get(guid);
            }
        }
        if (res == null) {
            try {
                res = readProjectNameFromDB(guid);
            } catch (PMException e) {
                log.warn(e.getLocalizedMessage(), e);
            }
        }
        
        m_names.put(guid, res);
        return res;
    }
    
    /**    
     * @param projectGuid the Project guid
     * @throws PMException in cas of DB problem
     * @return the string for the GUID freshly read from the DB
     */
    private synchronized String readProjectNameFromDB(final String projectGuid) 
        throws PMException {

        if (projectGuid == null) {
            return null;
        }
        final EntityManager session = Persistor.instance().openSession();
        try {
            final Query q = session.createQuery(
                "select projectName from ProjectNamePO as projectName where projectName.hbmGuid = :projectGuid"); //$NON-NLS-1$
            q.setParameter("projectGuid", projectGuid); //$NON-NLS-1$

            try {
                return ((IProjectNamePO)q.getSingleResult()).getName();
            } catch (NoResultException nre) {
                // No such project name in the database. Fall through to return
                // null as per the javadoc.
            }
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForAnySession(null, e, session);
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(session);
        }
        return null;
    }

    /**    
     * @throws PMException in cas of DB problem
     * @return mapping between all guids in the DB and their corresponding names
     */
    @SuppressWarnings("unchecked")
    public synchronized Map<String, String> readAllProjectNamesFromDB() 
        throws PMException {

        final EntityManager session = Persistor.instance().openSession();
        try {
            final Query q = session.createQuery("select name from ProjectNamePO name"); //$NON-NLS-1$
            List<IProjectNamePO> projectNameList = q.getResultList();
            Map<String, String> guidToNameMap = new HashMap<String, String>();
            for (IProjectNamePO projectName : projectNameList) {
                guidToNameMap.put(projectName.getGuid(), projectName.getName());
            }
            return guidToNameMap;
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForAnySession(null, e, session);
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(session);
        }
        return null;
    }

    /**
     * Deletes the current project name from the DB if no other existing project
     * is using it.
     * @param guid id of the project name
     */
    public void checkAndDeleteName(String guid) 
        throws PMException, ProjectDeletedException {
        
        EntityManager session = null;
        try {
            session = Persistor.instance().openSession();
            final EntityTransaction tx = 
                Persistor.instance().getTransaction(session);

            if (!isGuidBeingUsed(session, guid)) {
                deleteName(session, guid);
            }
            Persistor.instance().commitTransaction(session, tx);
            m_names.remove(guid);
            m_transientNames.remove(guid);
        } catch (PersistenceException he) {
            StringBuilder msgbuid = new StringBuilder();
            msgbuid.append(Messages.CouldNotDeleteProjectName);
            msgbuid.append(StringConstants.LEFT_PARENTHESIS);
            msgbuid.append(Messages.ForGuid);
            msgbuid.append(guid);
            msgbuid.append(StringConstants.RIGHT_PARENTHESIS);
            msgbuid.append(Messages.FromTheDatabase);
            msgbuid.append(StringConstants.DOT);
            log.error(msgbuid.toString(), he);
        } finally {
            Persistor.instance().dropSession(session);
        }
    }

    /**
     * Checks whether any projects exist that use the current name.
     * @param session Persistence (JPA / EclipseLink) session context
     * @param guid id of the project name
     * @return <code>true</code> if at least one project exists with the
     *         current name. Otherwise <code>false</code>.
     */
    private synchronized boolean isGuidBeingUsed(
            EntityManager session, String guid) {
        Long hits = null;
        Query q = session.createQuery(
                "select count(project) from ProjectPO as project " //$NON-NLS-1$
                + "where project.guid = :guid"); //$NON-NLS-1$
        q.setParameter("guid", guid); //$NON-NLS-1$
        hits = (Long)q.getSingleResult();
        return (hits != null && hits.intValue() > 0); 
    }
    
    /**
     * Deletes the project name from the DB.
     * @param session Persistence (JPA / EclipseLink) session context
     * @param guid id of the project name
     */
    private synchronized void deleteName(EntityManager session, String guid) {
        Query q = session.createQuery("delete from ProjectNamePO name where name.hbmGuid = :guid"); //$NON-NLS-1$
        q.setParameter("guid", guid); //$NON-NLS-1$
        q.executeUpdate();
    }

    /**
     * Commits a changed Name <=> GUID mapping.
     * 
     * @param guid the guid for which to change the mapped name 
     * @param newProjectName the new name for all projects with this <code>guid</code>
     * @param doPersist shall this name be persisted or just added to the list
     * of name waiting to be persisted?
     */
    public void setName(String guid, String newProjectName, boolean doPersist) {
        if (guid == null || newProjectName == null) {
            return;
        }
        if (doPersist) {
            final EntityManager session = Persistor.instance().openSession();
            try {
                final EntityTransaction tx = 
                    Persistor.instance().getTransaction(session);
                setName(session, guid, newProjectName);
                Persistor.instance().commitTransaction(session, tx);
            } catch (PMException e) {
                throw new JBFatalException(Messages.SavingProjectFailed 
                        + StringConstants.DOT, e,
                        MessageIDs.E_DATABASE_GENERAL);
            } catch (ProjectDeletedException e) {
                throw new JBFatalException(Messages.SavingProjectFailed 
                        + StringConstants.DOT, e,
                        MessageIDs.E_PROJECT_NOT_FOUND);
            } finally {
                Persistor.instance().dropSession(session);
            }
        } else {
            setTransientName(guid, newProjectName);
        }
    }

    /**
     * @param guid id for name
     * @param newProjectName value for name
     */
    private void setTransientName(String guid, String newProjectName) {
        if (m_names.containsKey(guid)) { 
            // there is a persistent key for the guid, so just uopate the name
            log.debug("setTransientName() " + Messages.CalledForPersistantObject //$NON-NLS-1$
                    + StringConstants.DOT);
            m_names.put(guid, newProjectName);
        } else {
            m_transientNames.put(guid, newProjectName);
        }
    }
    
    /**
     * persist all entries in the transient name map
     *
     * @param s Persistence (JPA / EclipseLink) Session to join
     */
    public synchronized void storeTransientNames(EntityManager s) {
        Map<String, String> workMap = new HashMap<String, String>(
                m_transientNames);
        for (Entry<String, String> name : workMap.entrySet()) {
            setName(s, name.getKey(), name.getValue());
        }
        m_transientNames.clear();
    }

    /**
     * Commits a changed Name <=> GUID mapping.
     * 
     * @param session use this specific Persistence (JPA / EclipseLink) session
     * @param guid the guid for which to change the mapped name 
     * @param newProjectName the new name for all projects with this <code>guid</code>
     */
    public synchronized void setName(
        EntityManager session, String guid, String newProjectName) {
        
        if (guid == null || newProjectName == null) {
            return;
        }

        final Query q = session.createQuery(
                "select projectName from ProjectNamePO projectName where projectName.hbmGuid = :projectGuid"); //$NON-NLS-1$
        q.setParameter("projectGuid", guid); //$NON-NLS-1$

        IProjectNamePO projectName = null;
        try {
            projectName = (IProjectNamePO)q.getSingleResult();
        } catch (NoResultException nre) {
            projectName = PoMaker.createProjectNamePO(guid, newProjectName);
            session.persist(projectName);
        }

        projectName.setName(newProjectName);
        m_names.put(projectName.getGuid(), projectName.getName());
        m_transientNames.remove(guid); // make sure the key is removed 
                                       //from the transient map

    }
    
    /**
     * call if the DB values have changed and it's not clear if the cached names
     * are still valid.
     * 
     */
    public void clearCache() {
        m_names.clear();
        m_transientNames.clear();
    }

    /**
     * @return the singleton instance
     */
    public static ProjectNameBP getInstance() {
        if (instance == null) {
            instance = new ProjectNameBP();
        }
        return instance;
    }

    /**
     * Checks if a given String is a valid project name. This includes the
     * ability to be used as a file name on supported operating systems.
     * @param name Name candidate
     * @param checkSpaces test for spaces at the beginning or end of name
     * 
     * @return true if the name is considered a valid project name.
     */
    public static boolean isValidProjectName(String name, boolean checkSpaces) {
        if (checkSpaces) {
            if (name.startsWith(StringConstants.SPACE)) {
                return false; // no leading spaces
            }
            if (name.endsWith(StringConstants.SPACE)) {
                return false; // no trailing spaces
            }
        }

        return NameValidationUtil.containsNoIllegalChars(name);
    }

}

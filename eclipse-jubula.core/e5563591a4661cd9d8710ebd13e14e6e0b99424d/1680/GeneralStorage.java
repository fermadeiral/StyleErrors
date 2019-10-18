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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.JBFatalAbortException;
import org.eclipse.jubula.tools.internal.exception.JBRuntimeException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author BREDEX GmbH
 * @created 13.10.2004
 */
public class GeneralStorage implements IEntityManagerProvider {
    
    /** standard logging */
    private static final Logger LOG = 
        LoggerFactory.getLogger(GeneralStorage.class);
    
    /**
     * Comment for <code>instance</code>
     */
    private static GeneralStorage instance = null;

    /**
     * associated project with childrenList of testsuites and additional list 
     * of object for specification tree (specTestCases and categories)
     **/
    private IProjectPO m_project = null;
    
    /** The reused projects managed by the main session */
    private Map<Long, IProjectPO> m_reusedProjects = null;
    
    /**
     * <code>m_masterSession</code>session only for objects to display
     * 
     **/
    private EntityManager m_masterSession = null;

    /** List of  listeners for reloaded session. */
    private Set<IReloadedSessionListener> m_reloadSessionListeners = 
        new HashSet<IReloadedSessionListener>();

    /** list of listeners for refreshed object */
    private Set<IDataModifiedListener> m_dataModifiedListeners =
        new HashSet<IDataModifiedListener>(); 
    
    /**
     * only for persistence
     */
    private GeneralStorage() {
        // nothing
    }

    /**
     * @return Returns the instance.
     */
    public static synchronized GeneralStorage getInstance() {
        if (instance == null) {
            instance = new GeneralStorage();
        }
        return instance;
    }
    /**
     * @return Returns the project.
     */
    public IProjectPO getProject() {
        return m_project;
    }

    /** Nulls the project */
    public void nullProject() {
        m_project = null;
        m_reusedProjects = null;
    }
    
    /**
     * Sets the project and loads the reused projects into the master session
     * @param project the project
     * @throws JBException the exception
     */
    public void setProjectLoadReused(IProjectPO project) throws JBException {
        m_project = project;
        if (project == null) {
            m_reusedProjects = null;
            return;
        }
        m_reusedProjects = new HashMap<>();
        for (IReusedProjectPO reused : project.getUsedProjects()) {
            IProjectPO reusedP = ProjectPM.loadReusedProjectInMasterSession(
                    reused);
            if (reusedP != null) {
                m_reusedProjects.put(reusedP.getId(), reusedP);
            }
        }
    }
    
    /**
     * Returns the reused projects
     * @return the ID => ProjectPO map
     */
    public Map<Long, IProjectPO> getReusedProjects() {
        return m_reusedProjects;
    }

    /**
     * @return Returns the masterSession.
     */
    public EntityManager getMasterSession() {
        if (m_masterSession == null) {
            m_masterSession = Persistor.instance().openSession();           
        }
        return m_masterSession;
    }
    
    /**
     * dispose (to be able to open a new db connection) 
     */
    public void dispose() {
        try {
            clearMasterSession();
        } catch (PMException e) {
            LOG.error(Messages.ClearingOfMasterSessionFailed, e);
        }
        Persistor.instance().dropSession(m_masterSession);
        m_masterSession = null;
        m_project = null;
    }

    /**
     * 
     */
    public void reset() {
        final EntityManager masterSession = GeneralStorage.getInstance()
            .getMasterSession();
        try { 
            clearMasterSession(); 
            Persistor.instance().dropSession(masterSession); 
            m_masterSession = Persistor.instance().openSession(); 
        } catch (PMException e) { 
            LOG.warn(Messages.ResetFailed, e); 
        }
        m_project = null;
    }
    
    /**
     * @throws PMException in case of problem with evicting of objects
     */
    private void clearMasterSession() throws PMException {
        final EntityManager masterSession = GeneralStorage.getInstance()
            .getMasterSession();
        ParamNameBP.getInstance().clearParamNames();
        try {
            masterSession.clear();
        } catch (PersistenceException e) {
            throw new PMException(Messages.ClearingOfMasterSessionFailed,
                MessageIDs.E_DATABASE_GENERAL);
        }
    }
    
    /**
     * recovers a faulty session, no data is reloaded
     */
    public void recoverSession() {
        try {
            if (getMasterSession() != null && getMasterSession().isOpen()) {
                Persistor.instance().dropSession(getMasterSession());
            }
            m_masterSession = Persistor.instance().openSession();
            if (m_project != null) {
                getMasterSession().lock(m_project, LockModeType.NONE);
            }
        } catch (PersistenceException e) {
            handleFatalError(e);
        } catch (JBRuntimeException e) {
            handleFatalError(e);
        }
    }
    
    /**
     * @param t cause of error
     */
    public static void handleFatalError(Throwable t) {
        final String msg = Messages.NonRecoverableError + StringConstants.DOT;
        LOG.error(msg, t);
        throw new JBFatalAbortException(msg, t, MessageIDs.E_NON_RECOVERABLE);
    }

    /**
     * reopen the session and reload any object in master session
     * 
     * @param monitor The progress monitor for this operation.
     * @throws ProjectDeletedException if the project was deleted in another
     * instance
     */
    public void reloadMasterSession(IProgressMonitor monitor) 
        throws ProjectDeletedException {
        
        ProjectNameBP.getInstance().clearCache();
        try {
            NodePM.getInstance().setUseCache(true);

            if (m_project != null) {
                // loadProjectInROSession will do a complete reset
                ProjectPM.loadProjectInROSession(m_project); 
                
                if (m_project == null) {
                    reset();
                    throw new ProjectDeletedException(
                            Messages.ProjectWasDeleted,
                            MessageIDs.E_CURRENT_PROJ_DEL);
                }
                fireSessionReloaded(monitor);
            }
        } catch (PersistenceException e) {
            handleFatalError(e);
        } catch (PMReadException e) {
            handleFatalError(e);
        } finally {
            NodePM.getInstance().setUseCache(false);
        }
    }
    
    /**
     * Adds the IReloadedSessionListener.
     * @param listener the IReloadedSessionListener to be added.
     */
    public void addReloadedSessListener(IReloadedSessionListener listener) {
        m_reloadSessionListeners.add(listener);
    }
    
    /**
     * Removes the IReloadedSessionListener.
     * @param listener the IReloadedSessionListener.
     */
    public void removeReloadedSessListener(IReloadedSessionListener listener) {
        m_reloadSessionListeners.remove(listener);
    }
    
    /**
     * notifies the IReloadedSessionListener
     * 
     * @param monitor The progress monitor for this operation.
     */
    public void fireSessionReloaded(IProgressMonitor monitor) {
        Set<IReloadedSessionListener> listeners = 
            new HashSet<IReloadedSessionListener>(m_reloadSessionListeners);
        for (IReloadedSessionListener listener : listeners) {
            try {
                listener.reloadData(monitor);
            } catch (Throwable e) {
                LOG.error(Messages.InvocationOfListenerForReloadingSessionFailed
                    + StringConstants.DOT);
            }
        }
    }
    
    
    /**
     * listener for reloaded session
     * warning: this listener is only provided for notification about events
     * inside of GeneralStorage
     * Don't use this listener for other purpose!
     *
     */
    public interface IReloadedSessionListener {
        
        /**
         * callback method
         * 
         * @param monitor The progress monitor for this operation.
         */
        public void reloadData(IProgressMonitor monitor);
    }
    /**
     * listener for refreshed persistent object
     * warning: this listener is only provided for notification about events
     * inside of GeneralStorage
     * Don't use this listener for other purpose!
     *
     */    
    public interface IDataModifiedListener {
        
        /**
         * callback method
         * @param po refreshed object
         */
        public void dataModified(IPersistentObject po);
    }


    /**
     * @param l listener to add
     */
    public void addDataModifiedListener(IDataModifiedListener l) {
        m_dataModifiedListeners.add(l);
    }
    
    /**
     * @param l listener to remove
     */
    public void removeDataModifiedListener(IDataModifiedListener l) {
        m_dataModifiedListeners.remove(l);
    }
    
    /**
     * notifies the IDataModifiedListener
     * @param po modified persistent object
     */
    public void fireDataModified(IPersistentObject po) {
        Set<IDataModifiedListener> listeners = 
            new HashSet<IDataModifiedListener>(m_dataModifiedListeners);
        for (IDataModifiedListener listener : listeners) {
            try {
                listener.dataModified(po);
            } catch (Throwable e) {
                LOG.error(Messages.InvocationOfListenerForReloadingSessionFailed
                        + StringConstants.DOT);
            }
        }
    }
    

    /**
     * Ensures that the given project is still in the DB. Please be aware that
     * there is still a slightly chance that the project is delete at this
     * very moment.
     * @param project Project to verify
     * @throws ProjectDeletedException if the project is not in the DB
     */
    public void validateProjectExists(IProjectPO project) 
        throws ProjectDeletedException {
        if (!ProjectPM.doesProjectExist(project.getId())) {
            throw new ProjectDeletedException(Messages.ProjectNotInDB,
                    MessageIDs.E_CURRENT_PROJ_DEL);
        }

    }

    /**
     * 
     * {@inheritDoc}
     */
    public EntityManager getEntityManager() {
        return getMasterSession();
    }
}

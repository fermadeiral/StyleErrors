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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jubula.client.core.businessprocess.CompNameCacheFactory;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITcParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.CompNamePM.SaveCompNamesData;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.JBFatalAbortException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 15.07.2005
 */
public class EditSupport {
    /** standard logging */
    private static Logger log = LoggerFactory.getLogger(EditSupport.class);

    /** Persistence (JPA / EclipseLink) session for editing */
    private EntityManager m_session;

    /** working version of persistent object */
    private IPersistentObject m_workVersion;

    /**
     * <code>m_lockedObjects</code>objects are locked by current edit support
     */
    private List<IPersistentObject> m_lockedObjects = 
        new ArrayList<IPersistentObject>();
    
    /**
     * <code>m_transaction</code> actual transaction
     */
    private EntityTransaction m_transaction = null;

    /**
     * <code>m_isLocked</code> lock status for m_workVersion
     */
    private boolean m_isLocked = false;

    /**
     * <code>m_isValid</code> signals the validity of this instance
     */
    private boolean m_isValid = true;
    
    /**
     * <code>m_mapper</code>mapper for resolving and persistence of parameter names
     */
    private ParamNameBPDecorator m_paramMapper = null;
    
    /**
     * <code>m_compMapper</code>mapper for resolving and persistence of 
     * component names
     */
    private IWritableComponentNameCache m_cache;
    
    /**
     * Instantiate edit support for the supplied persistent object
     * 
     * @param po Master instance for the new editable persistent object
     * @param paramMapper mapper for resolving and persistence of parameter names
     * the mapper is null in case of po objects not derived of NodePO
     * @throws PMException in case of unexpected db error
     * 
     */
    public EditSupport(IPersistentObject po, ParamNameBPDecorator paramMapper) 
        throws PMException {
        
        init();
        m_workVersion = createWorkVersion(po);
        m_paramMapper = paramMapper;
        m_cache = CompNameCacheFactory.createCompNameCache(
                m_workVersion);
    }

    /**
     * (re)set internal data
     */
    private void init() {
        m_workVersion = null;
        m_isValid = true;
        m_session = Persistor.instance().openSession();
        m_transaction = Persistor.instance().getTransaction(m_session);
    }
    
    

    /**
     * @param po
     *            the persistent object for which the working version shall be
     *            created.
     * @return a working version of the PO supplied to the constructor. This
     *         version is db identical to its original, but not Java identical.
     * @throws PMException
     *             in case of unspecified db error
     * 
     */
    public IPersistentObject createWorkVersion(IPersistentObject po)
        throws PMException {
        Assert.verify(m_isValid, 
            Messages.InvalidInstanceForInvokingOfThisMethod);
        Validate.notNull(po,
            Messages.OriginalObjectForCreatingOfWorkversionIsNull
            + StringConstants.DOT);
        try {
            IPersistentObject result = m_session
                    .find(po.getClass(), po.getId());
            if (result == null) {
                throw new EntityNotFoundException(
                        Messages.UnableToFind + StringConstants.SPACE
                        + po.getClass().getName() 
                        + StringConstants.SPACE + Messages.WithID 
                        + StringConstants.SPACE + po.getId());
            }
            /* if po in the mastersession is newer than the corresponding
               object in the editor session, the instance in the editor session
               must be from the session cache; therefore the instance from the
               editor session must be evicted and reloaded */
            if ((result.getVersion() == null)
                || (po.getVersion().intValue() 
                    > result.getVersion().intValue())) {
                m_session.detach(result);
                result = m_session.find(po.getClass(), po.getId());
                if (result == null) {
                    throw new EntityNotFoundException(
                            Messages.UnableToFind + StringConstants.SPACE
                            + po.getClass().getName() 
                            + StringConstants.SPACE + Messages.WithID
                            + StringConstants.SPACE + po.getId());
                }
            }
            return result;
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForEditor(po, e, this);
        }
        return null;
    }

    /**
     * locks the actual work version for modification. This method may be called
     * several times during one editing session if the node is locked and the
     * user retries the edit operation.
     * 
     * @throws PMDirtyVersionException
     *             in case of version conflict
     * @throws PMAlreadyLockedException
     *             if another user has locked this object
     * @throws PMReadException
     *             in case of reading error of DB
     * @throws PMException
     *             in case of general db error
     */
    public void lockWorkVersion() throws PMReadException,
        PMAlreadyLockedException, PMDirtyVersionException, PMException {
        Assert.verify(m_isValid, 
            Messages.InvalidInstanceForInvokingOfThisMethod);
        try {
            if (m_workVersion instanceof ISpecTestCasePO) {
                List<IParamDescriptionPO> params = 
                    ((ISpecTestCasePO)m_workVersion)
                        .getParameterList();
                for (IParamDescriptionPO desc : params) {
                    ((ITcParamDescriptionPO)desc)
                            .setParamNameMapper(m_paramMapper);
                }
            } else if (m_workVersion instanceof ITestDataCategoryPO) {
                for (IParameterInterfacePO pio 
                        : ((ITestDataCategoryPO)m_workVersion)
                            .getTestDataChildren()) {
                    List<IParamDescriptionPO> params = pio.getParameterList();
                    for (IParamDescriptionPO desc : params) {
                        ((ITcParamDescriptionPO)desc)
                                .setParamNameMapper(m_paramMapper);
                    }
                }
            }
            Persistor.instance().lockPO(m_session, m_workVersion);
            m_lockedObjects.add(m_workVersion);
            m_isLocked = true;
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForEditor(m_workVersion, e,
                this);
        }
    }

    
    /**
     * closes the actual session
     */
    private void closeSession() {
        if (Persistor.instance() != null) {
            Persistor.instance().dropSession(m_session);
        }
        
        invalidate();
    }
    
    /**
     * persists the workversion in database
     * 
     * @throws PMReadException
     *             in case of stale state exception for object to refresh
     * @throws PMSaveException
     *             if commit failed
     * @throws PMException
     *             in case of failed rollback
     * @throws ProjectDeletedException
     *             if the project was deleted in another instance
     */
    public void saveWorkVersion()
        throws PMReadException, PMSaveException, PMException,
        ProjectDeletedException {
        SaveCompNamesData saveData = null;
        if (!m_isValid) {
            throw new JBFatalAbortException(
                Messages.NotAllowedToSaveAnUnlockedWorkversion
                + StringConstants.DOT, MessageIDs.E_CANNOT_SAVE_INVALID); 
        }
        if (!m_isLocked) {
            throw new JBFatalAbortException(
                Messages.NotAllowedToSaveAnUnlockedWorkversion
                + StringConstants.DOT, MessageIDs.E_CANNOT_SAVE_UNLOCKED); 
        }
        trackChanges();
        boolean stayLocked = false;
        try {
            boolean mayModifyParamNames = 
                m_workVersion instanceof ISpecTestCasePO
                    || m_workVersion instanceof ITestDataCategoryPO;
            if (mayModifyParamNames) {
                saveParamNames();
            }
            /* CARE: isDirty() only checks for the synchronisation state
             * of the session and the database; --> e.g. isDirty() is
             * false if the session has been flushed
             */
            // The saving of param names that occurs above may flush the
            // session, which would make the session not "dirty".
            // We cover this case by assuming that any situation in 
            // which param names may be modified is a situation where we definitely want to save+commit.
            if (mayModifyParamNames 
                    || m_session.unwrap(JpaEntityManager.class)
                        .getUnitOfWork().hasChanges()) {

                Long projId = GeneralStorage.getInstance().getProject()
                        .getId();
                saveData = CompNamePM.flushCompNames(
                        m_session, projId, m_cache);
                Persistor.instance().commitTransaction(m_session,
                        m_transaction);
                if (m_paramMapper != null) {
                    m_paramMapper.updateStandardMapperAndCleanup(projId);
                }
                refreshOriginalVersions();
                if (saveData != null) {
                    CompNameManager.getInstance().compNamesChanged(saveData);
                }
            } else {
                Persistor.instance().rollbackTransaction(m_session,
                        m_transaction);
            }
            m_lockedObjects.clear();
            if (m_session != null) {
                m_transaction = m_session.getTransaction();
                m_transaction.begin();
            } else {
                init();
            }
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForEditor(
                    m_workVersion, e, this);
        } finally {
            m_isLocked = stayLocked;
            detachObjects(saveData);
        }
    }
    
    /**
     * Detaching all managed Component Names
     * @param data t save data
     */
    private void detachObjects(SaveCompNamesData data) {
        if (m_session == null || data == null) {
            return;
        }
        for (IComponentNamePO cN : data.getDBVersions()) {
            try {
                m_session.detach(cN);
            } catch (IllegalArgumentException e) {
                // Should not happen, but even if it does, it means the
                // Component Name is already detached, so we rejoice 
            }
        }
    }
    
    /**
     * Tracks, that a test case, a test suite, or a test job has been modified.
     */
    private void trackChanges() {
        if (m_workVersion instanceof ISpecTestCasePO
                || m_workVersion instanceof ITestSuitePO
                || m_workVersion instanceof ITestJobPO) {
            INodePO node = (INodePO) m_workVersion;
            node.addTrackedChange("modified", true); //$NON-NLS-1$
        }
    }

    /**
     * Persists the Parameter Names.
     * @throws PMException
     */
    private void saveParamNames() throws PMException {
        m_paramMapper.persist(m_session, 
            GeneralStorage.getInstance().getProject().getId());
    }
    
    /**
     * Refreshes the original versions, which were possibly modified in editor
     * 
     * @throws ProjectDeletedException
     *             if the project was deleted in another instance
     */
    private void refreshOriginalVersions() throws ProjectDeletedException {
        try {
            final EntityManager masterSession = GeneralStorage.getInstance()
                    .getMasterSession();
            IPersistentObject original = getOriginal();
            if (original != null) {
                masterSession.refresh(masterSession.merge(getWorkVersion()));
                GeneralStorage.getInstance().fireDataModified(original);
            }
        } catch (PersistenceException e) {
            log.error(Messages.RefreshOfOriginalVersionFailed
                    + StringConstants.DOT, e);
            GeneralStorage.getInstance().reloadMasterSession(
                    new NullProgressMonitor());
        }
    }

    /**
     * discards the work version
     * 
     */
    public void close() {
        Assert.verify(m_isValid, 
            Messages.InvalidInstanceForInvokingOfThisMethod);
        closeSession();
    }

    /**
     * resets all instance variables
     */
    private void invalidate() {
        m_isValid = false;
        m_isLocked = false;
        m_workVersion = null;
        m_transaction = null;
        m_session = null;
        m_lockedObjects.clear();
    }

    /**
     * @return Returns the original.
     */
    public IPersistentObject getOriginal() {
        return GeneralStorage.getInstance().getMasterSession()
                .find(m_workVersion.getClass(), m_workVersion.getId());
    }

    /**
     * @return locked objects of current edit support
     */
    public List<IPersistentObject> getLockedObjects() {
        return m_lockedObjects;
    }
    

    /**
     * @return Returns the workVersion.
     */
    public IPersistentObject getWorkVersion() {
        return m_workVersion;
    }

    /**
     * @return Returns the session.
     */
    public EntityManager getSession() {
        return m_session;
    }
   
    /**
     * attachs the detached workVersion to a new session
     * to use for postprocessing of Persistence (JPA / EclipseLink) exceptions without refresh of objects 
     * @throws PMException in case of any db error
     */
    public void reinitializeEditSupport() throws PMException {
        try {
            IPersistentObject workVersion = m_workVersion;
            close();
            init();
            m_workVersion = workVersion;
            m_workVersion = m_session.merge(m_workVersion);
            m_cache = CompNameCacheFactory.createCompNameCache(
                    m_workVersion);
        } catch (PersistenceException e) {
            final String msg = Messages.ReinitOfSessionFailed;
            log.error(msg);
            throw new PMException(msg,
                MessageIDs.E_DATABASE_GENERAL);
        }
    }
    
    /**
     * refreshs the editSession
     * @throws PMException in case of any db error
     */
    public void reloadEditSession() throws PMException {
        try {
            IPersistentObject workVersion = m_workVersion;
            close();
            init();
            m_workVersion = createWorkVersion(workVersion);
            m_cache = CompNameCacheFactory.createCompNameCache(
                    m_workVersion);
            if (m_paramMapper != null) {
                Long projId = 
                    GeneralStorage.getInstance().getProject().getId();
                m_paramMapper.updateStandardMapperAndCleanup(projId);
            }
        } catch (PersistenceException e) {
            final String msg = Messages.ReinitOfSessionFailed;
            log.error(msg);
            throw new PMException(msg,
                MessageIDs.E_DATABASE_GENERAL);
        }
        
    }
    
    /**
     * @return project associated with current session
     * @throws PMException
     *           in case of unspecified db error
     * 
     */
    public IProjectPO getWorkProject() throws PMException {
        IProjectPO masterProj = GeneralStorage.getInstance().getProject();
        IProjectPO workProj = null;

        try {
            workProj = m_session.find(
                    masterProj.getClass(), masterProj.getId());
            if (workProj == null) {
                throw new EntityNotFoundException(
                        Messages.UnableToFind + StringConstants.SPACE
                        + masterProj.getClass().getName() 
                        + StringConstants.SPACE + Messages.WithID
                        + StringConstants.SPACE + masterProj.getId());
            }
            return workProj;
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForEditor(masterProj, e, this);
        }
        return null;
    }

    /**
     * @return the ParamMapper, businessLogic for Parameter Names.
     */
    public ParamNameBPDecorator getParamMapper() {
        return m_paramMapper;
    }
    
    /**
     * Returns the cache
     * @return the cache
     */
    public IWritableComponentNameCache getCache() {
        return m_cache;
    }
}
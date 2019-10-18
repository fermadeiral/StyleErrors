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
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.eclipse.jubula.client.core.businessprocess.progress.OperationCanceledUtil;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IParamNamePO;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * class to manage persistence of param names
 *
 * @author BREDEX GmbH
 * @created 28.06.2007
 */
public class ParamNamePM extends AbstractNamePM {
   
    /**
     * <code>log</code>logger
     */
    private static Logger log = LoggerFactory.getLogger(ParamNamePM.class);
    
    
    
    /**
     * private constructor for singleton
     */
    private ParamNamePM() {
        super();
        // nothing
    }
    
    
    
    /**
     * @param guid guid of parameter
     * @param rootProjId id of current project
     * @return ParamNamePO object or null, if no name is available for given GUID
     * @throws PMException in case of any db problem
     */
    public static final synchronized IParamNamePO readParamNamePO (String guid, 
        Long rootProjId) 
        throws PMException {
        
        IParamNamePO paramName = null;
        EntityManager s = null;
        try {
            s = Persistor.instance().openSession();
            Query q = s.createQuery(
                "select p from ParamNamePO as p where p.hbmGuid = :guid AND p.parentProjectId = :projId"); //$NON-NLS-1$
            q.setParameter("guid", guid); //$NON-NLS-1$
            q.setParameter("projId", rootProjId); //$NON-NLS-1$
            paramName = (IParamNamePO)q.getSingleResult();
        } catch (NoResultException nre) {
            // No result for query. Fall through to return null.
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForAnySession(
                paramName, e, s);
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(s);
        }
        return paramName;
    }
    
   
    
    /**
     * @param parentProjectId id from root project
     * @return list of all param name objects for given project
     * @throws PMException in case of any db problem
     */
    @SuppressWarnings("unchecked")
    public static final synchronized List<IParamNamePO> readAllParamNames(
        Long parentProjectId) throws PMException {

        EntityManager s = null;
        List <IParamNamePO> paramNames = null;
        try {
            s = Persistor.instance().openSession();
            Query q = s.createQuery("select paramName from ParamNamePO as paramName where paramName.parentProjectId = :parentProjId"); //$NON-NLS-1$
            q.setParameter("parentProjId", parentProjectId); //$NON-NLS-1$
            paramNames = q.getResultList();            
        } catch (PersistenceException e) {
            OperationCanceledUtil.checkForOperationCanceled(e);
            log.error(Messages.CouldNotReadParameterNamesFromDB, e);
            PersistenceManager.handleDBExceptionForAnySession(null, e, s);
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(s);
        }
        return paramNames != null ? paramNames : new ArrayList<IParamNamePO>(0);
    }
    
    /**
     * @param s session to use for delete operation
     * @param parentProjectId id of root project
     * @param commit flag for commitment of delete statement
     * @throws PMException in case of failed delete statement
     * @throws ProjectDeletedException in case of already deleted project
     */
    public static final void deleteParamNames(EntityManager s, 
        Long parentProjectId, 
        boolean commit) throws PMException, ProjectDeletedException {
        try {
            if (commit) {
                EntityTransaction tx = 
                    Persistor.instance().getTransaction(s);
                executeDeleteStatement(s, parentProjectId);
                Persistor.instance().commitTransaction(s, tx);
            } else {
                executeDeleteStatement(s, parentProjectId);
            }
        } catch (PersistenceException e) {
            String msg = Messages.DeletionOfParamNamePOsFailed;
            log.error(msg, e); 
            throw new PMException(msg, MessageIDs.E_DB_SAVE);
        }
    }

    /**
     * @param s session to use for statement execution
     * @param parentProjectId id of root project of paramName
     */
    private static synchronized void executeDeleteStatement(
        EntityManager s, Long parentProjectId) {
        
        Query q = s.createQuery(
            "delete from ParamNamePO p where p.parentProjectId = :parentProjId"); //$NON-NLS-1$
        q.setParameter("parentProjId", parentProjectId); //$NON-NLS-1$
        q.executeUpdate();
    }
    
}

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNamePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITcParamDescriptionPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 12.07.2007
 */
public class ParamNameBPDecorator extends GuidNameCache<IParamNamePO> 
    implements IParamNameMapper, INameMapper {
    
    /**
     * <code>log</code> logger for class
     */
    private static Logger log = 
        LoggerFactory.getLogger(ParamNameBPDecorator.class);
    
    /**
     * <code>m_paramNameBP</code>wrapped paramNameBP instance
     */
    private IParamNameMapper m_paramNameBP = null;
    

    /**
     * <code>m_paramDescriptions</code> registered param descriptions
     */
    private Set<ITcParamDescriptionPO> m_paramDescriptions = 
        new HashSet<ITcParamDescriptionPO>();

    /**
     * constructor
     * @param paramNameBP wrapped ParamNameBP instance
     */
    public ParamNameBPDecorator(IParamNameMapper paramNameBP) {
        m_paramNameBP = paramNameBP;
    }
    
    /**
     * use this constructor only in SpecTestCaseEditor context
     * @param paramNameBP wrapped ParamNameBP instance
     * @param obj root node of editor
     */
    public ParamNameBPDecorator(IParamNameMapper paramNameBP, 
        IPersistentObject obj) {
        
        this(paramNameBP);
        if (obj instanceof ISpecTestCasePO) {
            ISpecTestCasePO specTc = (ISpecTestCasePO)obj;
            cacheParamNames(specTc);
        }
    }

    /**
     * caches the parameter names of root node in SpecTestCaseEditor
     * it's important for parameters of root node, that we use the param names
     * were set at the moment of creation of editor; otherwise we could get later
     * param names of Mastersession context
     * @param specTc root node of editor
     */
    private void cacheParamNames(ISpecTestCasePO specTc) {
        List<IParamDescriptionPO> descs = specTc.getParameterList();
        for (IParamDescriptionPO desc : descs) {
            String name = m_paramNameBP.getName(desc.getUniqueId(), 
                specTc.getParentProjectId());
            addNameToCache(desc.getUniqueId(), name);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName(String guid, Long rootProjId) {
        String name = StringConstants.EMPTY;
        if (super.getName(guid) != null) {
            name = super.getName(guid);
        } else {
            name = m_paramNameBP.getName(guid, rootProjId);
        }
        return name;
    }
    
    
    /**
     * @param namePO namePO to insert in db
     */
    public void addParamNamePO(IParamNamePO namePO) {
        addNameToInsert(namePO);
    }

    /**
     * writes new paramNames in database
     * @param s session to use
     * @param rootProjId id of rootProject       
     * @throws PMException in case of any db problem
     * 
     */
    public void persist(EntityManager s, Long rootProjId) 
        throws PMException {
        
        saveParamNames(s, rootProjId);
    }

    /**
     * update map with all param names to use in Mastersession
     * Hint: call this method not before the transaction to persist the param names
     * is completely and successfull finished
     * @param rootProjId id of rootProject   
     */
    @SuppressWarnings("unchecked")
    private void updateStandardMapper(Long rootProjId) {
        for (IParamNamePO paramName : getNamesToInsert()) {
            ParamNameBP.getInstance().addParamNamePO(paramName);
        }
        List<String> guids = getNameGuidsToUpdate();
        EntityManager s = GeneralStorage.getInstance().getMasterSession();
        if (!guids.isEmpty()) {
            Query q = s.createQuery(
                "select p from ParamNamePO as p where p.parentProjectId = :projId AND p.hbmGuid in :guidList"); //$NON-NLS-1$
            q.setParameter("projId", rootProjId); //$NON-NLS-1$
            q.setParameter("guidList", guids); //$NON-NLS-1$
            List<IParamNamePO> paramNames = q.getResultList();
            for (IParamNamePO paramName : paramNames) {
                s.refresh(paramName);
                ParamNameBP.getInstance().addParamNamePO(paramName);
            }
        }
        for (String guid : getNameGuidsToDelete()) {
            ParamNameBP.getInstance().removeParamNamePO(guid);
        }
    }

    /**
     * @param s session to use
     * @param rootProjId id of rootProject
     * @throws PMSaveException in case of any problem to save, update or delete param names in db
     */
    @SuppressWarnings("unchecked")
    private synchronized void saveParamNames(EntityManager s, Long rootProjId) 
        throws PMSaveException {      
        for (IParamNamePO paramName : getNamesToInsert()) {
            paramName.setParentProjectId(rootProjId);
            try {
                s.persist(paramName);
            } catch (PersistenceException e) {
                StringBuilder msgbuid = new StringBuilder();
                msgbuid.append(Messages.CouldNotSaveParameter);
                msgbuid.append(StringConstants.SPACE);
                msgbuid.append(paramName.getName());
                msgbuid.append(StringConstants.SPACE);
                msgbuid.append(Messages.AndGUID);
                msgbuid.append(StringConstants.SPACE);
                msgbuid.append(paramName.getGuid());
                msgbuid.append(StringConstants.DOT);
                String msg = msgbuid.toString();
                log.error(msg, e); 
                throw new PMSaveException(msg, MessageIDs.E_DB_SAVE);
            }
        }
        // parameter names will be automatically committed in this session because of
        // invocation of createQuery
        List<String> guids = getNameGuidsToUpdate();
        if (!guids.isEmpty()) {
            Query q = s.createQuery(
                "select p from ParamNamePO as p where p.hbmGuid in :guidList AND p.parentProjectId = :projId"); //$NON-NLS-1$
            q.setParameter("guidList", guids); //$NON-NLS-1$
            q.setParameter("projId", rootProjId); //$NON-NLS-1$
            List<IParamNamePO> paramNames = q.getResultList();
            for (IParamNamePO paramName : paramNames) {
                String name = getNameToUpdate(paramName.getGuid());
                if (name != null) {
                    paramName.setName(name);
                }
            }
        }
        try {
            if (!getNameGuidsToDelete().isEmpty()) {
                Query q = s.createQuery(
                    "delete from ParamNamePO p where p.hbmGuid in :guidList AND p.parentProjectId = :projId"); //$NON-NLS-1$
                q.setParameter("guidList", getNameGuidsToDelete()); //$NON-NLS-1$
                q.setParameter("projId", rootProjId); //$NON-NLS-1$
                q.executeUpdate();
            }

        } catch (PersistenceException e) {
            String msg = Messages.CouldNotDeleteAllParameters 
                + StringConstants.DOT;                   
            log.error(msg, e);
            throw new PMSaveException(msg, MessageIDs.E_DB_SAVE);
        }
        updateLocalCache();
    }
    

    /**
     * {@inheritDoc}
     */
    public void registerParamDescriptions(ITcParamDescriptionPO desc) {
        m_paramDescriptions.add(desc);
        
    }

    /**
     * sets the standard mapper for all registered param descriptions and clears
     * the param descriptions
     */
    private void deregisterAllParamDescriptions() {
        for (ITcParamDescriptionPO desc : m_paramDescriptions) {
            desc.setParamNameMapper(ParamNameBP.getInstance());
        }
        m_paramDescriptions.clear();
        
    }

    /**
     * {@inheritDoc}
     */
    public void removeParamNamePO(String guid) {
        removeNamePO(guid);
    }
    
    /**
     * updates map for param names in Mastersession, update param descriptions
     * and cleanup saved param names in current decorator
     * @param rootProjId id of rootProject   
     */
    public void updateStandardMapperAndCleanup(Long rootProjId) {
        updateStandardMapper(rootProjId);
        deregisterAllParamDescriptions();
        clearAllNames();
    }
   

}

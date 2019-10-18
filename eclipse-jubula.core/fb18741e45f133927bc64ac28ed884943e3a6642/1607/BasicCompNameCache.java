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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemType;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IComponentNameReuser;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PersistenceUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * @author BREDEX GmbH
 * @created Apr 21, 2008
 */
public class BasicCompNameCache implements IWritableComponentNameCache {

    /** Local changes to Component Names */
    private Map<String, IComponentNamePO> m_localChanges = new HashMap<>();
    
    /** Problems introduced by local changes */
    private Map<String, ProblemType> m_localProblems = null;
    
    /** The context object - for editors, this is the edited node */
    private IPersistentObject m_context;

    /**
     * Constructor
     * @param context the context
     */
    public BasicCompNameCache(IPersistentObject context) {
        m_context = context;
    }
    
    /** {@inheritDoc} */
    public void addCompNamePO(IComponentNamePO compNamePo) {
        if (compNamePo != null) {
            m_localChanges.put(compNamePo.getGuid(), compNamePo);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void updateStandardMapperAndCleanup(Long projectId) {
        try {
            CompNameManager.getInstance().refreshNames(projectId);
            clear();
        } catch (PMException e) {
            throw new JBFatalException(Messages.ReadingComponentNamesFailed
                    + StringConstants.EXCLAMATION_MARK, e, 
                    MessageIDs.E_DATABASE_GENERAL);
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getGuidForName(String name) {
        for (IComponentNamePO cN : m_localChanges.values()) {
            if (cN.getName().equals(name)) {
                return cN.getGuid();
            }
        }
        
        return CompNameManager.getInstance().getGuidForName(name);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public String getGuidForName(String name, Long parentProjectId) {
        Long parentProjectIdToUse = parentProjectId;
        if (parentProjectIdToUse == null) {
            IProjectPO currentProject = 
                GeneralStorage.getInstance().getProject();
            if (currentProject != null) {
                parentProjectIdToUse = currentProject.getId();
            }
        }
        
        if (parentProjectIdToUse == null) {
            return null;
        }

        for (IComponentNamePO cN : m_localChanges.values()) {
            if (cN.getName().equals(name) && parentProjectIdToUse
                    .equals(cN.getParentProjectId())) {
                return cN.getGuid();
            }
        }
        
        return CompNameManager.getInstance().getGuidForName(
                name, parentProjectIdToUse);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public IComponentNamePO createComponentNamePO(String name, String type, 
            CompNameCreationContext creationContext) {
        
        return createComponentNamePO(null, name, type, creationContext);
    }
    
    /** {@inheritDoc} */
    public IComponentNamePO createComponentNamePO(String guid, String name, 
            String type, CompNameCreationContext creationContext) {
        
        String nameGuid = guid;
        if (guid == null) {
            nameGuid = PersistenceUtil.generateUUID();
        }

        final IComponentNamePO newComponentNamePO =
                CompNameManager.getInstance().createCompNamePO(nameGuid, name, 
                    type, creationContext);
        newComponentNamePO.setParentProjectId(
                    GeneralStorage.getInstance().getProject().getId());

        addCompNamePO(newComponentNamePO);

        return newComponentNamePO;
    }

    /** {@inheritDoc} */
    public void renameComponentName(String guid, String newName) {
        IComponentNamePO cN = m_localChanges.get(guid);
        if (cN != null) {
            cN.setName(newName);
            return;
        }
        cN = PoMaker.cloneCompName(getResCompNamePOByGuid(guid));
        m_localChanges.put(guid, cN);
        cN.setName(newName);
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        m_localChanges.clear();
    }
    
    /** {@inheritDoc} */
    public IComponentNamePO getResCompNamePOByGuid(String guid) {
        IComponentNamePO res = m_localChanges.get(guid);
        if (res != null && res.getReferencedGuid() == null) {
            return res;
        }
        return CompNameManager.getInstance().getResCompNamePOByGuid(guid);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getNameByGuid(String guid) {
        String retVal;
        IComponentNamePO compNamePo = getResCompNamePOByGuid(guid);
        if (compNamePo != null) {
            retVal = compNamePo.getName();
        } else {
            retVal = CompNameManager.getInstance().getNameByGuid(guid);
        }
        return retVal;
    }
    
    /** {@inheritDoc} */
    public Collection<IComponentNamePO> getAllCompNamePOs() {
        Set<IComponentNamePO> compNames = new HashSet<IComponentNamePO>();
        compNames.addAll(m_localChanges.values());
        compNames.addAll(
                CompNameManager.getInstance().getAllCompNamePOs());
        return compNames;
    }

    /** {@inheritDoc} */
    public Map<String, IComponentNamePO> getLocalChanges() {
        return m_localChanges;
    }
    
    /** {@inheritDoc} */
    public void removeCompName(String guid) {
        m_localChanges.remove(guid);
    }
    
    /** {@inheritDoc} */
    public void renamedCompName(String guid, String newName) {
        IComponentNamePO cN = m_localChanges.get(guid);
        if (cN != null) {
            cN.setName(newName);
        }
    }
    
    /** {@inheritDoc} */
    public void addIfNotYetPresent(String guid) {
        if (!m_localChanges.containsKey(guid)) {
            IComponentNamePO cN = getResCompNamePOByGuid(guid);
            if (cN != null) {
                addCompNamePO(PoMaker.cloneCompName(cN));
            }            
        }
    }
    
    /** {@inheritDoc} */
    public void clearUnusedCompNames(INodePO node) {
        Set<String> usedGuids = new HashSet<>();
        for (Iterator<INodePO> it = node.getAllNodeIter(); it.hasNext(); ) {
            INodePO next = it.next();
            if (next instanceof ICapPO) {
                usedGuids.add(((ICapPO) next).getComponentName());
            } else if (next instanceof IExecTestCasePO) {
                for (ICompNamesPairPO pair : ((IExecTestCasePO) next).
                        getCompNamesPairs()) {
                    usedGuids.add(pair.getSecondName());
                }
            }
        }
        for (Iterator<String> it = m_localChanges.keySet()
                .iterator(); it.hasNext();) {
            String guid = it.next();
            if (!usedGuids.contains(guid)) {
                it.remove();
            }
        }
    }
    
    /** {@inheritDoc} */
    public void storeLocalProblems(CalcTypes calc) {
        m_localProblems = calc.getAllProblems();
        calc.writeLocalTypes();
    }

    @Override
    public Map<String, ProblemType> getNewProblems(CalcTypes calc) {
        Map<String, ProblemType> problems = calc.getAllProblems();
        // we set the local problems as late as possible
        // that is, only if a real change is introduced, until then they remain null
        Map<String, ProblemType> locProbs;
        if (m_localProblems == null) {
            m_localProblems = CompNameManager.getInstance().getTypeProblems();
        }
        locProbs = m_localProblems;
        Map<String, ProblemType> newProblems = new HashMap<>();
        for (String guid : problems.keySet()) {
            if (!(problems.get(guid).equals(locProbs.get(guid)))) {
                newProblems.put(guid, problems.get(guid));
            }
        }
        return newProblems;
    }
    
    /** {@inheritDoc} */
    public void changeReuse(
            IComponentNameReuser user, String oldGuid, String newGuid) {
        if (user == null) {
            // null-safe
            return;
        }
        user.changeCompName(oldGuid, newGuid);
        if (newGuid != null) {
            addIfNotYetPresent(newGuid);
        }
    }
    
    /**
     * Returns the cache context
     * @return the context
     */
    public IPersistentObject getContext() {
        return  m_context;
    }

    /** {@inheritDoc} */
    public void handleExistingNames(Map<String, String> guidToCompNameMap) {
        // null implementation
    }
    
    /** {@inheritDoc} */
    public void setContext(IPersistentObject context) {
        m_context = context;
    }
}
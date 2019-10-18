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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.ProjectCompNameCache;
import org.eclipse.jubula.client.core.datastructure.CompNameUsageMap;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNamePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITcParamDescriptionPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.core.utils.NativeSQLUtils;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.messagehandling.MessageInfo;


/**
 * This class offers support for executing multiple commands in a single
 * transaction. Therefore :
 * -all commands are queried for a list of objects
 * to lock
 * -all commands are executed
 * -locks are removed
 *
 * @author BREDEX GmbH
 * @created 26.03.2007
 */
public class MultipleNodePM  extends PersistenceManager {

    /**
     * Abstract command, offering support for a Set of objects to lock. All
     * commands are executed in the same transaction
     * 
     * REMEMBER : some nodes do not return their Persistence (JPA / EclipseLink) parent, 
     *  but the project when in top level, for example ICategoryPO 
     *  or ISpecTestCasePO
     */
    public abstract static class AbstractCmdHandle {

        /** set of objects to lock; empty by default */
        private final Set<IPersistentObject> m_objsToLock = 
            new HashSet<IPersistentObject>();
        
        /**
         * @return a Set of objects to be locked in database
         */
        public final Set <IPersistentObject> getObjsToLock() {
            return m_objsToLock;
        }
        
        /**
         * executes the command
         * 
         * @param sess The session in which this command is executing.
         * @return a message containing information about the error that 
         *         occurred during execution, or <code>null</code> if no error
         *         occurred.
         */
        public abstract MessageInfo execute(EntityManager sess);
        
    }

    /**
     * "Transfers" Component Names used in Test Cases / Test Steps from one 
     * project to another. The transfer is not a move, nor is it a copy.
     * It is based on the names of the Component Names and has the following 
     * logic:
     * 
     * Rules for Component Names (CNs) used as "old name" in an 
     * ICompNamesPairPO:
     *  1. If the CN is from a reused Project (P), do nothing to change it.
     *     Note that this can essentially "remove" the CN if the new 
     *     project does not reuse P. However, this seems reasonable
     *     given that TCs from P are also not available in the new project.
     *  2. If the CN is from the current Project, create a new CN in the new 
     *     Project. The new CN will have the same name as the original, but 
     *     will have a different GUID. 
     *     If a CN with same name already exists in the new Project, 
     *     cancel the operation with an error. 
     * 
     * Rules for Component Names (CNs) used as "new name" in an 
     * ICompNamesPairPO or used in a Test Step:
     * 1. If a CN with the same name already exists in the new Project, use it.
     *    In case of type incompatibility, cancel the operation with an error.
     * 2. If no CN with the same name exists in the new Project, create a new 
     *    CN in the new Project. The new CN will have the same name as the 
     *    original, but will have a different GUID. 
     * 
     * @author BREDEX GmbH
     * @created May 30, 2008
     */
    public static final class TransferCompNameHandle extends AbstractCmdHandle {

        /** mapping from "old name"s to their users */
        private Map<IComponentNamePO, Set<INodePO>> m_firstCompNameToUsers;

        /** mapping from "new name"s to their users */
        private Map<IComponentNamePO, Set<INodePO>> m_secondCompNameToUsers;
        
        /** 
         * the ID of the Project from which the Component Names are being
         * "transferred"
         */
        private Long m_currentProjectId = null;

        /** 
         * the Project into which the Component names are being "transferred" 
         */
        private IProjectPO m_newParentProj = null;

        /**
         * Constructor
         * 
         * @param usageMap Mapping from used Component Names to their users.
         * @param currentProjectId The ID of the Project from which the 
         *                         Component Names are being "transferred".
         * @param newParentProj The Project into which the Component names 
         *                      are being "transferred".
         */
        public TransferCompNameHandle(
                CompNameUsageMap usageMap,
                Long currentProjectId,
                IProjectPO newParentProj) {
            
            m_firstCompNameToUsers = 
                new HashMap<IComponentNamePO, Set<INodePO>>();
            for (IComponentNamePO compName : usageMap.getFirstCompNames()) {
                Set<INodePO> users = new HashSet<INodePO>();
                users.addAll(usageMap.getFirstNameUsers(compName));
                m_firstCompNameToUsers.put(compName, users);
            }

            m_secondCompNameToUsers = 
                new HashMap<IComponentNamePO, Set<INodePO>>();
            for (IComponentNamePO compName : usageMap.getSecondCompNames()) {
                Set<INodePO> users = new HashSet<INodePO>();
                users.addAll(usageMap.getSecondNameUsers(compName));
                m_secondCompNameToUsers.put(compName, users);
            }
            
            m_currentProjectId = currentProjectId;
            m_newParentProj = newParentProj;
        }

        /**
         * {@inheritDoc}
         */
        public MessageInfo execute(EntityManager sess) {
            IWritableComponentNameCache extProjectCompCache = 
                new ProjectCompNameCache(m_newParentProj);

            try {
                Map<String, IComponentNamePO> createdComponentNames =
                    new HashMap<String, IComponentNamePO>();
                createdComponentNames.putAll(
                        handleFirstNames(sess, extProjectCompCache));
                createdComponentNames.putAll(
                        handleSecondNames(sess, extProjectCompCache));
                for (String guid : createdComponentNames.keySet()) {
                    handleMapping(sess, extProjectCompCache, 
                            guid, createdComponentNames.get(guid));
                }
                
                CompNamePM.flushCompNames(
                        sess, m_newParentProj.getId(), extProjectCompCache);
            } catch (PMException e) {
                return new MessageInfo(e.getErrorId(), null);
            } catch (ComponentNameExistsException e) {
                return new MessageInfo(
                        e.getErrorId(), e.getErrorMessageParams());
            }
            return null;
        }

        /**
         * Maps the given Component Name according to the current mapping for 
         * the Component Name with the given GUID. If the AUT for an Object Map
         * is locked, the Component Name will not be mapped for that Object Map.
         * 
         * @param sess The session in which to perform the work. This session
         *             will not be managed at all by this method (i.e. no 
         *             commit or rollback for transactions, no closing).
         * @param extProjectCompCache The Comp Name Cache
         * @param originalGuid The GUID of the Component Name for which to 
         *                     check for mappings.
         * @param compName The Component Name that will be mapped.
         * @throws PMException If a persistence error occurs while performing
         *                     the mapping.
         */
        private void handleMapping(EntityManager sess,
                IWritableComponentNameCache extProjectCompCache, 
                String originalGuid, IComponentNamePO compName) 
            throws PMException {
            
            IProjectPO currentProject = sess.find(
                    NodeMaker.getProjectPOClass(), m_currentProjectId);
            for (IAUTMainPO aut : currentProject.getAutMainList()) {
                if (LockManager.instance().lockPO(sess, aut, true)) {
                    IObjectMappingAssoziationPO assoc = 
                        aut.getObjMap().getLogicalNameAssoc(originalGuid);
                    extProjectCompCache.changeReuse(
                            assoc, null, compName.getGuid());
                }
            }
        }

        /**
         * @param sess The session in which to perform the operation.
         * @param extProjectCompCache The Component Name cache.
         * @return mapping from GUID of "moved/cloned" Component Name to newly 
         *                 created Component Name in reused Project.
         */
        private Map<String, IComponentNamePO> handleSecondNames(
                EntityManager sess,
                IWritableComponentNameCache extProjectCompCache) {

            Map<String, IComponentNamePO> createdNames = 
                new HashMap<String, IComponentNamePO>();
            Map<IComponentNamePO, Set<INodePO>> secondCompNameToUsers =
                loadInSession(sess, m_secondCompNameToUsers);
            for (IComponentNamePO compName : secondCompNameToUsers.keySet()) {
                IComponentNamePO existingExtCompName =
                    getCompNamePOForName(
                            compName.getName(), 
                            extProjectCompCache,
                            m_newParentProj.getId());
                if (existingExtCompName != null 
                        && existingExtCompName.getParentProjectId().equals(
                                m_newParentProj.getId())) {
                    // Update relevant users
                    updateSecondNameUsers(extProjectCompCache,
                            secondCompNameToUsers, compName,
                            existingExtCompName);
                } else {
                    // Create a new Component Name in the new project.
                    String compType = ComponentBuilder.getInstance()
                        .getCompSystem().getMostAbstractComponent().getType();
                    IComponentNamePO newCompName = 
                        extProjectCompCache.createComponentNamePO(
                                    compName.getName(), compType, 
                                    CompNameCreationContext.OVERRIDDEN_NAME);
                    extProjectCompCache.addCompNamePO(newCompName);
                    newCompName.setParentProjectId(m_newParentProj.getId());
                    createdNames.put(compName.getGuid(), newCompName);
                    // Update relevant users
                    updateSecondNameUsers(extProjectCompCache,
                            secondCompNameToUsers, compName, newCompName);
                }
            }
            
            return createdNames;
        }

        /**
         * Updates the objects using the given Component Name as a second name.
         * 
         * @param extProjectCompCache The Component Name cache to use to
         *                             perform the update.
         * @param secondCompNameToUsers The users to update.
         * @param compName The Component Name currently used.
         * @param existingExtCompName The new Component Name to use.
         */
        private void updateSecondNameUsers(
                IWritableComponentNameCache extProjectCompCache,
                Map<IComponentNamePO, Set<INodePO>> secondCompNameToUsers,
                IComponentNamePO compName,
                IComponentNamePO existingExtCompName) {
            
            for (INodePO node : secondCompNameToUsers
                    .get(compName)) {
                if (node instanceof ICapPO) {
                    ICapPO capPo = (ICapPO)node;
                    extProjectCompCache.changeReuse(
                            capPo, capPo.getComponentName(), 
                            existingExtCompName.getGuid());
                } else if (node instanceof IExecTestCasePO) {
                    for (ICompNamesPairPO pair 
                            : ((IExecTestCasePO)node)
                            .getCompNamesPairs()) {
                        if (pair.getSecondName().equals(
                                compName.getGuid())) {
                            extProjectCompCache.changeReuse(
                                    pair, pair.getSecondName(), 
                                    existingExtCompName.getGuid());
                        }
                    }
                }
            }
        }

        /**
         * 
         * @param sess The session in which to perform the operation.
         * @param compCache The Component Name cache to use for the
         *                             operation.
         * @return mapping from GUID of "moved/cloned" Component Name to newly 
         *                 created Component Name in reused Project.
         * @throws ComponentNameExistsException 
         *                          If the name to handle already exists in the 
         *                          reused Project.
         */
        private Map<String, IComponentNamePO> handleFirstNames(
                EntityManager sess, IWritableComponentNameCache compCache) 
            throws ComponentNameExistsException {
            
            Map<String, IComponentNamePO> createdComponentNames =
                new HashMap<String, IComponentNamePO>();
            
            // Load users into given session
            Map<IComponentNamePO, Set<INodePO>> firstCompNameToUsers =
                loadInSession(sess, m_firstCompNameToUsers);
            
            for (IComponentNamePO compName : firstCompNameToUsers.keySet()) {
                if (compName.getParentProjectId().equals(m_currentProjectId)) {
                    // Component Name is from the current project. 
                    // Check whether a Component Name with the same name 
                    // already exists in the new project.
                    IComponentNamePO existingExtCompName = 
                        getCompNamePOForName(
                                compName.getName(), 
                                compCache,
                                m_newParentProj.getId());
                    if (existingExtCompName != null 
                            && existingExtCompName.getParentProjectId()
                                .equals(m_newParentProj.getId())) {
                        // Component Name with same name already exists in
                        // new project.
                        throw new ComponentNameExistsException(
                                "Cannot perform move operation.",  //$NON-NLS-1$
                                MessageIDs.E_MOVE_TC_COMP_NAME_EXISTS, 
                                new String [] {existingExtCompName.getName()});
                    }
                    // Create a new Component Name in the new project.
                    String compType = ComponentBuilder.getInstance()
                        .getCompSystem().getMostAbstractComponent().getType();
                    
                    IComponentNamePO newCompName = 
                        compCache.createComponentNamePO(
                                    compName.getName(), compType, 
                                    CompNameCreationContext.STEP);
                    compCache.addCompNamePO(newCompName);
                    newCompName.setParentProjectId(m_newParentProj.getId());
                    createdComponentNames.put(compName.getGuid(), newCompName);
                    // Update relevant users
                    for (INodePO node : firstCompNameToUsers.get(compName)) {
                        if (node instanceof IExecTestCasePO) {
                            for (ICompNamesPairPO pair 
                                    : ((IExecTestCasePO)node)
                                        .getCompNamesPairs()) {
                                if (pair.getFirstName().equals(
                                        compName.getGuid())) {
                                    pair.setFirstName(newCompName.getGuid());
                                }
                            }
                        }
                    }
                }
            }
            
            return createdComponentNames;
        }

        /**
         * 
         * @param sess The session in which to load the objects in 
         *             <code>toLoad</code>.
         * @param toLoad Contains all of the objects to be loaded into the 
         *               session.
         * @return the objects that have been loaded in the given session as a 
         *         result of this method call.
         */
        private Map<IComponentNamePO, Set<INodePO>> loadInSession(
                EntityManager sess, 
                Map<IComponentNamePO, Set<INodePO>> toLoad) {
            
            Map<IComponentNamePO, Set<INodePO>> sessionMap = 
                new HashMap<IComponentNamePO, Set<INodePO>>();
            for (IComponentNamePO compName : toLoad.keySet()) {
                Set<INodePO> userSet = new HashSet<INodePO>();
                for (INodePO node : toLoad.get(compName)) {
                    userSet.add(sess.find(node.getClass(), node.getId()));
                }
                sessionMap.put(sess.find(compName.getClass(), 
                                compName.getId()), userSet);
            }
            
            return sessionMap;
        }

        /**
         * 
         * @param name The name of the Component Name to find.
         * @param compNameCache The cache to use to find the Component Name.
         * @param parentProjectId The ID of the Project in which to find the
         *                        Component Name.
         * @return The Component Name with name equal to <code>name</code> 
         *         within the Project with the given ID, or <code>null</code> 
         *         if no such Component Name can be found. Reused Projects are
         *         ignored for the purposes of this search.
         */
        private IComponentNamePO getCompNamePOForName(
                String name, IComponentNameCache compNameCache, 
                Long parentProjectId) {
            String guid = compNameCache.getGuidForName(name, parentProjectId);
            if (guid != null) {
                return compNameCache.getResCompNamePOByGuid(guid);
            }
            return null;
        }
    }
    
    /**
     * Command to add a single Exec to a parent
     */
    public static class AddExecTCHandle extends AbstractCmdHandle {
        /** parent in which the Exec should be included */
        private INodePO m_parent;
        /** the index at which position it should be added */
        private Integer m_index;
        /** the new Exec */
        private IExecTestCasePO m_newExec;
        /**
         * 
         * @param parent the parent in which the node should be added
         * @param newExec the new ExecTestCase
         * @param index the index or null if it should be added to the end
         */
        public AddExecTCHandle(INodePO parent, IExecTestCasePO newExec,
                Integer index) {
            getObjsToLock().add(parent);
            m_parent = parent;
            m_index = index;
            m_newExec = newExec;
        }
        /** 
         * {@inheritDoc}
         */
        public MessageInfo execute(EntityManager sess) {
            sess.persist(m_newExec);
            INodePO parent = sess.find(m_parent.getClass(), m_parent.getId());
            if (m_newExec instanceof IEventExecTestCasePO) {
                if (parent instanceof ISpecTestCasePO) {

                    ISpecTestCasePO spec = (ISpecTestCasePO) parent;
                    try {
                        spec.addEventTestCase((IEventExecTestCasePO) m_newExec);
                        return null;
                    } catch (InvalidDataException e) {
                        return new MessageInfo(e.getErrorId(), null);
                    }
                }
                // This secures that no IEventExecTC is added in a wrong PO
                return new MessageInfo(MessageIDs.E_EVENT_SUPPORT, null);
            }
            parent.addNode(m_index, m_newExec);
            
            return null;
        }
        
    }
    /**
     * Command to move a single INodePO from one parent to another. Should work
     * for moving any type of node 
     */
    public static class MoveNodeHandle extends AbstractCmdHandle {

        /** node to move */
        private INodePO m_node;
        /** oldParent (could be INodePO, ISpecObjCont or ITestSuiteCont)*/
        private IPersistentObject m_oldParent;
        /** newParent (could be INodePO, ISpecObjCont or ITestSuiteCont)*/
        private IPersistentObject m_newParent;
        
        /**
         * constructor
         * @param node
         *      INodePO
         * @param oldParent
         *      INodePO
         * @param newParent
         *      INodePO
         */
        public MoveNodeHandle(INodePO node, IPersistentObject oldParent,
                IPersistentObject newParent) {
            IProjectPO proj = GeneralStorage.getInstance().getProject();
            m_oldParent = oldParent;
            m_newParent = newParent;
            m_node = node;
            getObjsToLock().add(m_node);
            getObjsToLock().add(m_oldParent);
            getObjsToLock().add(m_newParent);
        }
        
        /** {@inheritDoc} */
        public MessageInfo execute(EntityManager sess) {
            EntityManager masterSession = 
                GeneralStorage.getInstance().getMasterSession();
            IPersistentObject oldParent = m_oldParent;
            IPersistentObject newParent = m_newParent;
            INodePO node = m_node;
            
            if (masterSession != sess) {
                masterSession.detach(node);
                oldParent = sess.find(oldParent.getClass(), oldParent.getId());
                newParent = sess.find(newParent.getClass(), newParent.getId());
                node = sess.find(node.getClass(), node.getId());
            }

            // remove CentralTestData if the project changes.
            if (node instanceof ISpecTestCasePO 
                    && !(oldParent.getParentProjectId().equals(
                            newParent.getParentProjectId()))) {
                Iterator iter = node.getAllNodeIter();
                while (iter.hasNext()) {
                    INodePO child = (INodePO)iter.next();
                    if (child instanceof IExecTestCasePO) {
                        IExecTestCasePO execTC = (IExecTestCasePO)child;
                        execTC.setReferencedDataCube(null);
                    }
                }
                ((ISpecTestCasePO) node).setReferencedDataCube(null);
            }
            
            
            // remove from old parent
            ((INodePO)oldParent).removeNode(node);
            
            // add to new parent
            ((INodePO)newParent).addNode(node);
            
            return null;
        }
    }
    
    /**
     * command to update ParamNames, which was moved from current project
     * to a reused project 
     *
     */
    public static class UpdateParamNamesHandle extends AbstractCmdHandle {
        
        /** list of moved SpecTestCases */
        private List<ISpecTestCasePO> m_specTestCases;
        
        /** project, where the SpecTestCases moved to */
        private IProjectPO m_reusedProject;
        
        /** list of param names to update */
        private List <IParamNamePO> m_paramNames = 
            new ArrayList<IParamNamePO>();       

        /**
         * @param specTCs list of moved SpecTestCases
         * @param reusedProject project, where the SpecTestCases moved to
         */
        public UpdateParamNamesHandle(List<ISpecTestCasePO> specTCs, 
            IProjectPO reusedProject) {
            m_specTestCases = specTCs;
            m_reusedProject = reusedProject;
        }

        /** {@inheritDoc}
         * @see org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle#execute(org.Persistence (JPA / EclipseLink).Session)
         */
        public MessageInfo execute(EntityManager sess) {
            
            for (ISpecTestCasePO specTC : m_specTestCases) {
                for (IParamDescriptionPO desc : specTC.getParameterList()) {
                    IParamNamePO paramNamePO = ParamNameBP.getInstance()
                        .getParamNamePO(desc.getUniqueId());
                    if (paramNamePO != null) {
                        paramNamePO.setParentProjectId(m_reusedProject.getId());
                        m_paramNames.add(paramNamePO);
                        sess.merge(paramNamePO);
                        getObjsToLock().add(paramNamePO);
                    }                    
                }
            }
            
            return null;
        }

        /**
         * @return Returns the paramNames.
         */
        List<IParamNamePO> getParamNames() {
            return m_paramNames;
        }
    }

    /**
     * Command to update the reference to a TestCase. 
     */
    public static class UpdateTestCaseRefHandle extends AbstractCmdHandle {

        /** node to update */
        private IExecTestCasePO m_execTc;
        /** new referenced SpecTestCase */
        private ISpecTestCasePO m_specTc;
        
        /**
         * Constructor
         * 
         * @param execTc
         *      ExecTestCase whose reference is to be updated.
         * @param specTc
         *      New referenced SpecTestCase.
         */
        public UpdateTestCaseRefHandle(
            IExecTestCasePO execTc, ISpecTestCasePO specTc) {

            m_execTc =  execTc;
            m_specTc = specTc;

            getObjsToLock().add(m_execTc);
        }
        
        /**
         * {@inheritDoc}
         */
        public MessageInfo execute(EntityManager sess) {
            ISpecTestCasePO specTc = sess.find(m_specTc.getClass(),
                    m_specTc.getId());
            IExecTestCasePO execTc = sess.find(m_execTc.getClass(),
                    m_execTc.getId());
            execTc.setSpecTestCase(specTc);
            return null;
        }
    }

    /**
     * Command to update ExecTCs in other projects referencing the moved SpecTCs
     * @author BREDEX GmbH
     *
     */
    public static class UpdateReferencesHandler extends AbstractCmdHandle {

        /** The GUIDs of the moved SpecTCs */
        private List<String> m_specGUIDs;

        /** The GUID of the new project */
        private String m_newProjGuid;

        /**
         * @param specGUIDs the list of GUIDs of the moved SpecTCs
         * @param newProjGuid the new project's guid
         */
        public UpdateReferencesHandler(List<String> specGUIDs,
                String newProjGuid) {
            m_specGUIDs = specGUIDs;
            m_newProjGuid = newProjGuid;
        }

        @Override
        public MessageInfo execute(EntityManager sess) {
            IProjectPO currProj = GeneralStorage.getInstance().getProject();
            Set<Long> userProjects = ProjectPM.findUsedOrUserProjects(
                    currProj.getId(), ProjectPM.DEPPROJECT_USERS);
            if (userProjects.isEmpty()) {
                return null;
            }
            List<IExecTestCasePO> toModify =
                NodePM.findExecTCsByRefSpecTCAndProject(
                        userProjects, m_specGUIDs, sess);
            Set<Long> specTCsToLock = NativeSQLUtils.getSpecTCParentIds(
                    toModify, sess);
            try {
                for (Long id : specTCsToLock) {
                    if (!LockManager.instance().lockPOById(sess, id,
                            ISpecTestCasePO.SPEC_TC_CLASSNAME)) {
                        return new MessageInfo(
                            MessageIDs.E_CANNOT_UPDATE_MOVETOEXTERNAL, null);
                    }
                }
                for (IExecTestCasePO exec : toModify) {
                    exec.setProjectGuid(m_newProjGuid);
                }
            } catch (PMException e) {
                return new MessageInfo(
                        MessageIDs.E_CANNOT_UPDATE_MOVETOEXTERNAL, null);
            }
            return null;
        }
        
    }

    /**
     * Command to delete a single test case
     */
    public static class DeleteTCHandle extends AbstractCmdHandle {

        /** test case to delete */
        private ISpecTestCasePO m_testCase;
        /**
         * <code>m_mapper</code> to manage param names for deletion in db
         */
        private ParamNameBPDecorator m_dec;
        
        /**
         * constructor 
         * @param tc to delete
         * @param mapper mapper to manage param names for deletion in db
         *      ISpecTestCasePO
         */
        public DeleteTCHandle(ISpecTestCasePO tc, ParamNameBPDecorator mapper) {
            m_testCase = tc;
            m_dec = mapper;
            getObjsToLock().add(tc);
            
            if (isNestedNode(tc)) {
                getObjsToLock().add(tc.getParentNode());
            } else {
                IProjectPO proj = GeneralStorage.getInstance().getProject();
                getObjsToLock().add(proj.getSpecObjCont());  
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public MessageInfo execute(EntityManager sess) {
            ISpecTestCasePO tc = sess.find(m_testCase.getClass(),
                    m_testCase.getId());
            if (isNestedNode(m_testCase)) {
                INodePO par = m_testCase.getParentNode();
                par = sess.find(par.getClass(), par.getId());
                par.removeNode(tc);
            } else {
                INodePO cont = GeneralStorage.getInstance().
                        getProject().getSpecObjCont();
                cont = sess.find(cont.getClass(), cont.getId());
                cont.removeNode(tc);
            }
            registerParamNamesForDeletion(tc);
            
            sess.remove(tc);
            
            return null;
        }

        /**
         * @param specTcPO specTc to delete
         */
        private void registerParamNamesForDeletion(ISpecTestCasePO specTcPO) {
            for (IParamDescriptionPO desc : specTcPO.getParameterList()) {
                getDec().registerParamDescriptions((ITcParamDescriptionPO)desc);
                getDec().removeParamNamePO(desc.getUniqueId());
            }
            
        }

        /**
         * @return the m_dec
         */
        public ParamNameBPDecorator getDec() {
            return m_dec;
        }
    }

    /**
     * Command to delete an EventExecTestCasePO
     */
    public static class DeleteEvHandle extends AbstractCmdHandle {

        /** test suite to delete */
        private IEventExecTestCasePO m_eventTestCase;
        
        /**
         * constructor 
         * @param tc
         *      IEventExecTestCasePO
         */
        public DeleteEvHandle(IEventExecTestCasePO tc) {
            m_eventTestCase = tc;
            getObjsToLock().add(tc);
            
            getObjsToLock().add(tc.getParentNode());
        }
        
        /**
         * {@inheritDoc}
         */
        public MessageInfo execute(EntityManager sess) {
            ISpecTestCasePO specTc = (ISpecTestCasePO)m_eventTestCase.
                getParentNode();
            specTc = sess.find(specTc.getClass(), specTc.getId());
            IEventExecTestCasePO evTC = sess.find(m_eventTestCase.getClass(),
                    m_eventTestCase.getId());
            specTc.removeNode(evTC);
            sess.remove(evTC);
            
            return null;
        }
    }

    /**
     * Command to delete a single Exec
     */
    public static class DeleteExecTCHandle extends AbstractCmdHandle {

        /** the exec node to delete */
        private IExecTestCasePO m_execNode;
        
        /**
         * constructor 
         * @param exec
         *      the exec node to delete
         */
        public DeleteExecTCHandle(IExecTestCasePO exec) {
            m_execNode = exec;
            getObjsToLock().add(exec);
            
            if (isNestedNode(exec)) {
                getObjsToLock().add(exec.getParentNode());
            } else {
                IProjectPO proj = GeneralStorage.getInstance().getProject();
                getObjsToLock().add(proj.getExecObjCont());  
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public MessageInfo execute(EntityManager sess) {
            if (isNestedNode(m_execNode)) {
                m_execNode.getParentNode().removeNode(m_execNode);
            }
            IExecTestCasePO exec = sess.find(m_execNode.getClass(),
                    m_execNode.getId());
            if (exec != null) {
                sess.remove(exec);
            }
            return null;
        }
    }
    
    /**
     * class variable for Singleton
     */
    private static MultipleNodePM persManager = null;
    
    /**
     * getter for Singleton
     * 
     * @return single instance
     */
    public static MultipleNodePM getInstance() {
        if (persManager == null) {
            persManager = new MultipleNodePM();
        }
        return persManager;
    }
    
    /**
     * executes a list of commands in a single transaction in the Master
     * Session.
     * 
     * @param cmds
     *      List<AbstractCmdHandle>
     * @throws PMException
     *      error occurred
     * @throws ProjectDeletedException
     *      error occurred
     * @return a message containing information about the error that 
     *         occurred during execution, or <code>null</code> if no error
     *         occurred.
     */
    public MessageInfo executeCommands(List<AbstractCmdHandle> cmds) 
        throws PMException, ProjectDeletedException {

        return executeCommands(cmds, GeneralStorage.getInstance()
                .getMasterSession());
    }
    
    /**
     * executes a list of commands in a single transaction. The given session
     * is managed by the caller. This method will commit or rollback the 
     * transaction as appropriate, but will not close the session.
     * 
     * @param cmds
     *      List<AbstractCmdHandle>
     * @param sess The session in which to execute the commands.
     * @throws PMException
     *      error occurred
     * @throws ProjectDeletedException
     *      error occurred
     * @return a message containing information about the error that 
     *         occurred during execution, or <code>null</code> if no error
     *         occurred.
     */
    public MessageInfo executeCommands(List<AbstractCmdHandle> cmds,
        EntityManager sess) 
        throws PMException, ProjectDeletedException {
        
        final Persistor persistor = Persistor.instance();
        EntityTransaction tx = null;
        
        // get List of objects to lock
        Set <IPersistentObject> objectsToLock = 
            new HashSet<IPersistentObject>();
        IPersistentObject actObj = null;
        for (AbstractCmdHandle cmd : cmds) {
            objectsToLock.addAll(cmd.getObjsToLock());
        }
        
        try {
            
            // get new Transaction
            tx = persistor.getTransaction(sess);
            
            // lock objects
            persistor.lockPOSet(sess, objectsToLock);
            
            // the param name bp decorator which should be used for param changes
            ParamNameBPDecorator dec = null;
            
            // execute command code in transaction
            for (AbstractCmdHandle cmd : cmds) {
                MessageInfo errorMessage = cmd.execute(sess);
                if (errorMessage != null) {
                    persistor.rollbackTransaction(sess, tx);
                    return errorMessage;
                }
                if (cmd instanceof DeleteTCHandle) {
                    dec = ((DeleteTCHandle) cmd).getDec();
                }
            }
            if (dec != null) {
                deleteParamNames(dec, sess);
            }
            
            // commit transaction and remove all locks
            persistor.commitTransaction(sess, tx);
            if (dec != null) {
                // sync with master sessions mapper
                Long projId = GeneralStorage.getInstance().getProject().getId();
                dec.updateStandardMapperAndCleanup(projId);
            }
            
            EntityManager master = 
                GeneralStorage.getInstance().getMasterSession();
            for (AbstractCmdHandle cmd : cmds) {
                if (cmd instanceof UpdateParamNamesHandle) {
                    UpdateParamNamesHandle paramCmd = 
                        (UpdateParamNamesHandle)cmd;
                    for (IParamNamePO paramName : paramCmd.getParamNames()) {
                        master.detach(paramName);
                    }
                }
            }
            for (IPersistentObject next : objectsToLock) {
                IPersistentObject obj = master.find(next.getClass(),
                        next.getId());
                if (obj != null) {
                    try {
                        master.refresh(obj);
                    } catch (EntityNotFoundException e) {
                        // ok, the object was deleted...
                        master.detach(obj);
                    }
                }
            }
        } catch (PersistenceException e1) {
            PersistenceManager.handleDBExceptionForMasterSession(actObj, e1);
        }
        return null;
    }
    
    /**
     * @param dec decorator to handle deletion of param names
     * @param s session to use for deletion
     */
    private void deleteParamNames(ParamNameBPDecorator dec, EntityManager s) {
        try {
            dec.persist(s, GeneralStorage.getInstance()
                .getProject().getId());
        } catch (PMException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * @param node
     *            the node to check wheter its nested = non-top level
     *            node
     * @return true if non top-level node
     */
    private static boolean isNestedNode(INodePO node) {
        boolean isNested = false;
        INodePO parent = node.getParentNode();
        if (parent != null && !parent.isSpecObjCont()
                && !parent.isExecObjCont()) {
            isNested = true;
        }
        return isNested;
    }
    
    /**
     * collects all nodes of a specified node, that are required for the current
     * operation
     * 
     * @param affectedNodes
     *            Set<INodePO>
     * @param node
     *            INodePO
     */
    public static void collectAffectedNodes(List<INodePO> affectedNodes,
            INodePO node) {
        affectedNodes.add(node);
        if (node instanceof ICategoryPO) {
            Iterator iter = node.getNodeListIterator();
            while (iter.hasNext()) {
                collectAffectedNodes(affectedNodes, (INodePO) iter.next());
            }
        } else if (node instanceof ISpecTestCasePO) {
            ISpecTestCasePO specTcPO = (ISpecTestCasePO) node;
            if (!specTcPO.getAllEventEventExecTC().isEmpty()) {
                affectedNodes.addAll(specTcPO.getAllEventEventExecTC());
            }
        }
    }
    
    /**
     * Checks if all ExecTestCases are used in SpecTestCases, that are going
     * to be affected
     * 
     * @param affectedNodes
     *      List<INodePO>
     * @param execTestCases
     *      List<IExecTestCasePO>
     * @return
     *      true, if no conflict exists. That means, there are no IExecTestCasePO 
     *      or all located in other nodes, which are going to be affected
     */
    public static boolean allExecsFromList(Collection<INodePO> affectedNodes, 
        List<IExecTestCasePO> execTestCases) {
        if (execTestCases.isEmpty()) {
            return true;
        }
        for (IExecTestCasePO execTc : execTestCases) {
            INodePO parent;
            parent = execTc.getSpecAncestor();
            if (!affectedNodes.contains(parent)) {
                return false;
            }
        }
        return true;
    }
}

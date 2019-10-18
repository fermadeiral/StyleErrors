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
package org.eclipse.jubula.client.core.businessprocess.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Mar 18, 2010
 */
public class NodeBP {
    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger(NodeBP.class);

    /**
     * Utility class
     */
    protected NodeBP() {
    // do nothing
    }
    
    /**
     * @param editSupport holding the DB session for locking purposes
     * @param node node to lock
     * @throws PMObjectDeletedException
     * @throws PMDirtyVersionException
     * @throws PMAlreadyLockedException
     */
    protected static void lockPO(EditSupport editSupport, INodePO node)
        throws PMObjectDeletedException, PMDirtyVersionException,
            PMAlreadyLockedException {
        final EntityManager lockSession = editSupport.getSession();
        try {
            try {
                // make sure there is no old version
                // in the session cache
                lockSession.detach(node);
                lockSession.find(node.getClass(), node.getId());
            } catch (PersistenceException e) {
                PersistenceManager.handleDBExceptionForEditor(node, e,
                        editSupport);
            }
        } catch (PMDirtyVersionException e) { // NOPMD by al on 3/19/07 1:25 PM
            // ignore, we are not interested in version checking
        } catch (PMObjectDeletedException e) {
            // OK, this may happen, just forward to caller
            throw e;
        } catch (PMException e) {
            // Continue since we are just refreshing the cache
            LOG.error(Messages.StrayPersistenceException + StringConstants.DOT
                    + StringConstants.DOT, e);
        }
        if (!LockManager.instance().lockPO(lockSession, node, false)) {
            throw new PMAlreadyLockedException(node,
                    Messages.OrginalTestcaseLocked + StringConstants.DOT,
                    MessageIDs.E_OBJECT_IN_USE);
        }
    }
    
    /**
     * @param type
     *            the type to search for
     * @return a list of nodes with the given type
     */
    public static List<? extends INodePO> 
        getAllNodesForGivenTypeInCurrentProject(Class type) {
        if (INodePO.class.isAssignableFrom(type)) {
            GeneralStorage gs = GeneralStorage.getInstance();
            return NodePM.computeListOfNodes(type, 
                    gs.getProject().getId(), gs.getMasterSession());
        }
        return ListUtils.EMPTY_LIST;
    }
    
    /**
     * 
     * @param po The persistent object to check.
     * @return <code>true</code> if the given node can be modified within
     *         the active project. Otherwise <code>false</code>.
     */
    public static boolean isEditable(IPersistentObject po) {
        Validate.notNull(po);
        IProjectPO activeProject = GeneralStorage.getInstance().getProject();
        if (activeProject == po) {
            return true;
        }
        return activeProject != null 
            && activeProject.getId().equals(po.getParentProjectId());
    }
    
    /**
     * @param node
     *            the node to check
     * @return whether the given node or one of it's parent is active or not
     */
    public static boolean isNodeActive(INodePO node) {
        if (node == null || !node.isActive()) {
            return false;
        }
        INodePO parentNode = node.getParentNode();
        while (parentNode != null) {
            if (node != null) {
                if (!node.isActive()) {
                    return false;
                }
            }
            parentNode = parentNode.getParentNode();
        }
        return true;
    }
    
    /**
     * @param nodePO
     *            the node to test
     * @return true if editable --> belongs to current project; false otherwise
     *         or if nodePO == null
     */
    public static boolean belongsToCurrentProject(INodePO nodePO) {
        IProjectPO currentProject = GeneralStorage.getInstance()
                .getProject();
        if (nodePO != null && currentProject != null) {
            EqualsBuilder eb = new EqualsBuilder();
            eb.append(nodePO.getParentProjectId(), currentProject.getId());
            return eb.isEquals();
        }
        return false;
    }
    
    /**
     * Finds offsprings of a node - should only be called for Categories, SpecTCs, TestSuites and TestJobs
     * @param nodes the node
     * @return all offspring
     */
    public static Collection<INodePO> getOffspringCollection(
            Collection<INodePO> nodes) {
        for (INodePO node : nodes) {
            if (!(node instanceof ICategoryPO || node instanceof ITestSuitePO
                    || node instanceof ISpecTestCasePO
                    || node instanceof ITestJobPO)) {
                throw new UnsupportedOperationException("This method only supports Categories, Test Suites, Spec Test Cases and Test Jobs."); //$NON-NLS-1$
            }
        }
        List<INodePO> offspring = new ArrayList<INodePO>();
        for (INodePO node : nodes) {
            addOffspring(node, offspring);
        }
        return offspring;
    }
    
    /**
     * Adds the offspring of a node to a collection
     * @param node the node
     * @param offspring the offspring
     */
    private static void addOffspring(INodePO node,
            Collection<INodePO> offspring) {
        if (node instanceof ICategoryPO) {
            for (INodePO child : node.getUnmodifiableNodeList()) {
                addOffspring(child, offspring);
            }
        }
        offspring.add(node);
    }
    
}

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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;

/**
 * @author BREDEX GmbH
 * @created Mar 18, 2010
 */
public class TestSuiteBP extends NodeBP {
    /**
     * Utility class
     */
    private TestSuiteBP() {
    // do nothing
    }

    /**
     * Add an existing Test Suite to another Test Job by reference. Lock the TS
     * if this is the first usage to prevent accidental deletion before the
     * editor is saved.
     * 
     * @param editSupport
     *            holds the DB session for locking purposes
     * @param tj
     *            TestJob where the reference shall be added.
     * @param referencedTS
     *            TS to be used as a reference.
     * @param position
     *            Index position for the RefTestSuite, null means append.
     * @throws PMAlreadyLockedException
     *             if the referenced TS was never referenced before and is
     *             edited by another user.
     * @throws PMDirtyVersionException
     *             if the referenced TS was modified
     * @throws PMObjectDeletedException
     *             if the po as deleted by another concurrently working user
     * @return The RefTestSuite used to reference the Test Suite
     */
    public static IRefTestSuitePO addReferencedTestSuite(
            EditSupport editSupport, INodePO tj, ITestSuitePO referencedTS,
            Integer position) throws PMAlreadyLockedException,
            PMDirtyVersionException, PMObjectDeletedException {
        
        Set<IAUTConfigPO> autConfigSet = null;
        
        if (referencedTS != null) {
            IAUTMainPO aut = referencedTS.getAut();
            if (aut != null) {
                autConfigSet = aut.getAutConfigSet();
            }
        }
        
        handleFirstReference(editSupport, referencedTS, false);
        IRefTestSuitePO newRefTs;
        if (autConfigSet != null && autConfigSet.size() == 1) {
            Map<String, String> configMap = autConfigSet.iterator()
                    .next().getConfigMap();
            String autName = configMap.get(AutConfigConstants.AUT_ID);
            newRefTs = NodeMaker.createRefTestSuitePO(referencedTS,
                    autName);
        } else {
            newRefTs = NodeMaker.createRefTestSuitePO(referencedTS);
        }
        if (position != null) {
            tj.addNode(position.intValue(), newRefTs);
        } else {
            tj.addNode(newRefTs);
        }
        return newRefTs;
    }

    /**
     * Lock the TS if it is reused for the first time. This prevents deletion of
     * the TS while the editor is not saved.
     * 
     * @param editSupport
     *            holding the DB session for locking purposes
     * @param referencedTS
     *            TS to be used as a reference.
     * @param isReferencedByThisAction
     *            tells if there was a reference created by the current action,
     *            i.e. there is one references even if no other TC in the db
     *            references this one.
     * @throws PMAlreadyLockedException
     *             if the TC is locked by someone else
     * @throws PMDirtyVersionException
     *             if the TC was modified outside this instance of the
     *             application.
     * @throws PMObjectDeletedException
     *             if the po as deleted by another concurrently working user
     */
    private static void handleFirstReference(EditSupport editSupport,
            ITestSuitePO referencedTS, boolean isReferencedByThisAction)
        throws PMAlreadyLockedException, PMDirtyVersionException,
        PMObjectDeletedException {
        int minSize = 0;
        if (isReferencedByThisAction) {
            minSize = 1;
        }
        
        if (NodePM.getInternalRefTestSuites(referencedTS.getGuid(),
                referencedTS.getParentProjectId()).size() <= minSize) {
            lockPO(editSupport, referencedTS);
        }
    }
    
    /**
     * @return an unmodifiable list of test suite for the current project 
     */
    public static List<ITestSuitePO> getListOfTestSuites() {
        return getListOfTestSuites(GeneralStorage.getInstance().getProject());
    }
    
    /**
     * @param project the project to use for test suite retrieval
     * @return an unmodifiable list of test suite for the current project 
     */
    public static List<ITestSuitePO> getListOfTestSuites(IProjectPO project) {
        if (project != null) {
            final ExecNodeFinder<ITestSuitePO> op = 
                    new ExecNodeFinder<ITestSuitePO>(ITestSuitePO.class);
            new TreeTraverser(project, op, false, true).traverse(false);
            return op.getListOfExecNodes();
        }
        
        return ListUtils.EMPTY_LIST;
    }
    
    /**
     * @author BREDEX GmbH
     * 
     * @param <T> the type
     */
    public static class ExecNodeFinder <T>
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
        /**
         * the list of test suites
         */
        private List<T> m_listOfExecNodes = new ArrayList<T>();
        /**
         * the node type to search for
         */
        private final Class<T> m_nodeType;

        /**
         * @param nodeType the node type to search for
         */
        public ExecNodeFinder(Class<T> nodeType) {
            this.m_nodeType = nodeType;
        }

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (m_nodeType.isAssignableFrom(node.getClass())) {
                m_listOfExecNodes.add((T) node);
                // short circuit optimization. we've found the exec node.
                // we do not need to examine its children
                return false;
            }
            return true;
        }

        /**
         * @return the listOfTestSuites
         */
        public List<T> getListOfExecNodes() {
            return m_listOfExecNodes;
        }
    }
}

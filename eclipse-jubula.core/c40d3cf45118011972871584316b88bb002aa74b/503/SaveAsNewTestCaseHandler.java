/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IControllerPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.model.TDCell;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransaction;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.NativeSQLUtils;
import org.eclipse.jubula.client.core.utils.RefToken;
import org.eclipse.jubula.client.ui.rcp.actions.TransactionWrapper;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Markus Tiede
 * @since 1.2
 */
public class SaveAsNewTestCaseHandler extends AbstractRefactorHandler {
    /**
     * @author Markus Tiede
     * @since 1.2
     */
    private static class CloneTransaction implements ITransaction {
        /**
         * the name of the new test case
         */
        private final String m_newTestCaseName;
        /**
         * the param nodes to clone
         */
        private final List<INodePO> m_nodesToClone;

        /**
         * the new spec test case
         */
        private ISpecTestCasePO m_newSpecTC = null;
        
        /** The editor root node */
        private IPersistentObject m_oldRoot = null;
        /** the category where the new Spec TC should be added */
        private INodePO m_category;
        /**
         * Constructor
         * 
         * @param newTestCaseName
         *            the name of the new test case
         * @param nodesToClone
         *            the param nodes to clone
         * @param cat the category where the new Spec TC should be added
         */
        public CloneTransaction(String newTestCaseName,
                List<INodePO> nodesToClone, INodePO cat) {
            m_newTestCaseName = newTestCaseName;
            m_nodesToClone = nodesToClone;
            m_oldRoot = m_nodesToClone.get(0).getSpecAncestor();
            m_category = cat;
        }

        /** {@inheritDoc} */
        public void run(EntityManager s) throws PMException {
            ISpecTestCasePO newTc = NodeMaker
                    .createSpecTestCasePO(m_newTestCaseName);
            final ParamNameBPDecorator pMapper = new ParamNameBPDecorator(
                    ParamNameBP.getInstance());
            s.persist(newTc);
            Map<String, String> oldToNewGuids = new HashMap<String, String>();
            for (INodePO node : m_nodesToClone) {
                addCloneToNode(newTc, node, pMapper, oldToNewGuids, newTc);
            }
            registerParamNamesToSave(newTc, pMapper);
            s.merge(newTc);
            pMapper.persist(s, GeneralStorage.getInstance().getProject()
                    .getId());
            NativeSQLUtils.addNodeAFFECTS(s, newTc, m_category);
            setNewSpecTC(newTc);
        }
        
        /**
         * @param addTo
         *            the node to add to
         * @param nodeToCopy
         *            the param node to clone and add
         * @param pMapper the param name mapper
         * @param oldToNewGuids the old to new guid map
         * @param specTC the top-level specTC for the params
         * @throws PMException in case of an persitence exception
         */
        private void addCloneToNode(INodePO addTo,
            INodePO nodeToCopy, ParamNameBPDecorator pMapper,
            Map<String, String> oldToNewGuids, ISpecTestCasePO specTC)
            throws PMException {
            INodePO newNode = null;
            if (nodeToCopy instanceof IExecTestCasePO) {
                IExecTestCasePO origExec = (IExecTestCasePO) nodeToCopy;
                IExecTestCasePO newExec = NodeMaker.createExecTestCasePO(
                        origExec.getSpecTestCase());
                fillExec(origExec, newExec);
                newNode = newExec;
            } else if (nodeToCopy instanceof ICapPO) {
                ICapPO origCap = (ICapPO) nodeToCopy;
                ICapPO newCap = NodeMaker.createCapPO(
                        origCap.getName(), 
                        origCap.getComponentName(), 
                        origCap.getComponentType(), 
                        origCap.getActionName());
                fillCap(origCap, newCap);
                newNode = newCap;
            } else if (nodeToCopy instanceof ICommentPO) {
                ICommentPO origComment = (ICommentPO) nodeToCopy;
                ICommentPO newComment = NodeMaker.createCommentPO(
                        origComment.getName());
                newNode = newComment;
            } else if (nodeToCopy instanceof IControllerPO) {
                newNode = NodeMaker.createControllerPO(
                        (IControllerPO) nodeToCopy);
                int i = 0;
                for (INodePO node : nodeToCopy.getUnmodifiableNodeList()) {
                    for (INodePO node2 : node.getUnmodifiableNodeList()) {
                        addCloneToNode(newNode.getUnmodifiableNodeList().get(i),
                                node2, pMapper, oldToNewGuids, specTC);
                    }
                    i++;
                }
            }
            if (newNode != null) {
                if (newNode instanceof IParamNodePO) {                    
                    addParamsToSpec(specTC, (IParamNodePO) newNode, pMapper,
                            oldToNewGuids);
                }
                addTo.addNode(newNode);
            }
        }

        /**
         * @param newSpecTC
         *            the new parent to modify
         * @param newParamChildNode
         *            the new param child node
         * @param pMapper
         *            the param name mapper
         * @param oldToNewUuids
         *            the old to new guids map
         */
        private void addParamsToSpec(ISpecTestCasePO newSpecTC,
            IParamNodePO newParamChildNode, ParamNameBPDecorator pMapper,
            Map<String, String> oldToNewUuids) {
            TDCell cell = null;
            for (Iterator<TDCell> it = newParamChildNode
                    .getParamReferencesIterator(); it.hasNext();) {
                cell = it.next();
                String guid = newParamChildNode.getDataManager()
                        .getUniqueIds().get(cell.getCol());
                IParamDescriptionPO childDesc = newParamChildNode
                        .getParameterForUniqueId(guid);
                // The childDesc can be null if the parameter has been
                // removed in another session and not yet updated in the 
                // current editor session.
                if (childDesc != null) {
                    ModelParamValueConverter conv = 
                            new ModelParamValueConverter(
                            cell.getTestData(), newParamChildNode,
                            childDesc);
                    List<RefToken> refTokens = conv.getRefTokens();
                    for (RefToken refToken : refTokens) {
                        String oldUuid = RefToken.extractCore(refToken
                                .getModelString());
                        String paramName = ParamNameBP.getInstance()
                                .getName(oldUuid,
                                        childDesc.getParentProjectId());
                        IParamDescriptionPO parentParamDescr = newSpecTC
                                .addParameter(childDesc.getType(),
                                        paramName, false, pMapper);
                        if (parentParamDescr != null) {
                            String newUuid = parentParamDescr.getUniqueId();
                            oldToNewUuids.put(oldUuid, newUuid);
                        }
                        // update test data of child with UUID for reference
                        conv.replaceUuidsInReferences(oldToNewUuids);
                        cell.setTestData(conv.getModelString());
                    }
                }
            }
        }

        /**
         * @param origCap
         *            the source
         * @param newCap
         *            the target
         */
        private void fillCap(ICapPO origCap, ICapPO newCap) {
            newCap.setActive(origCap.isActive());
            newCap.setComment(origCap.getComment());
            newCap.setGenerated(origCap.isGenerated());
            newCap.setToolkitLevel(origCap.getToolkitLevel());
            origCap.getDataManager().deepCopy(
                    newCap.getDataManager());
        }

        /**
         * @param origExec
         *            the source
         * @param newExec
         *            the target
         */
        private void fillExec(IExecTestCasePO origExec, 
            IExecTestCasePO newExec) {
            newExec.setActive(origExec.isActive());
            newExec.setComment(origExec.getComment());
            newExec.setDataFile(origExec.getDataFile());
            newExec.setGenerated(origExec.isGenerated());
            ISpecTestCasePO origSpecTC = origExec.getSpecTestCase();
            if (!origExec.getName().equals(
                    origSpecTC.getName())) {
                newExec.setName(origExec.getName());
            }
            newExec.setToolkitLevel(origExec.getToolkitLevel());
            if (!origExec.getDataManager().equals(
                    origSpecTC.getDataManager())) {
                newExec.setHasReferencedTD(false);
                origExec.getDataManager().deepCopy(
                        newExec.getDataManager());
            } else {
                newExec.setHasReferencedTD(true);
            }
            newExec.setReferencedDataCube(origExec.getReferencedDataCube());
            
            for (ICompNamesPairPO origPair : origExec.getCompNamesPairs()) {
                ICompNamesPairPO newPair = PoMaker.createCompNamesPairPO(
                        origPair.getFirstName(), origPair.getSecondName(),
                        origPair.getType());
                newPair.setPropagated(origPair.isPropagated());
                newExec.addCompNamesPair(newPair);
            }
        }

        /**
         * @return the m_newSpecTC
         */
        public ISpecTestCasePO getNewSpecTC() {
            return m_newSpecTC;
        }

        /**
         * @param newSpecTC the m_newSpecTC to set
         */
        public void setNewSpecTC(ISpecTestCasePO newSpecTC) {
            m_newSpecTC = newSpecTC;
        }

        /** {@inheritDoc} */
        public Collection<? extends IPersistentObject> getToLock() {
            List<IPersistentObject> list = new ArrayList<>();
            list.add(m_category);
            list.add(m_oldRoot);
            return list;
        }

        /** {@inheritDoc} */
        public Collection<? extends IPersistentObject> getToRefresh() {
            List<IPersistentObject> list = new ArrayList<>();
            list.add(m_category);
            return list;
        }

        /** {@inheritDoc} */
        public Collection<? extends IPersistentObject> getToMerge() {
            List<IPersistentObject> toMerge = new ArrayList<>();
            toMerge.add(m_newSpecTC);
            return toMerge;
        }
    }

    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        if (!prepareForRefactoring(event)) {
            return null;
        }
        final AbstractTestCaseEditor editor = (AbstractTestCaseEditor) 
                HandlerUtil.getActivePart(event);
        if (!askNewNameAndCategory(editor)) {
            return null;
        }

        ISpecTestCasePO newSpecTC = null;
        IStructuredSelection ss = getSelection();
        final List<INodePO> nodesToClone =
                new ArrayList<INodePO>(ss.size());
        Iterator it = ss.iterator();
        while (it.hasNext()) {
            nodesToClone.add((INodePO) it.next());
        }
        newSpecTC = createAndPerformNodeDuplication(getNewTCName(),
                nodesToClone, getCategory());
        if (newSpecTC == null) {
            return null;
        }
        newSpecTC = GeneralStorage.getInstance().getMasterSession().find(
                newSpecTC.getClass(), newSpecTC.getId());

        DataEventDispatcher.getInstance().fireDataChangedListener(
                newSpecTC, DataState.Added, UpdateState.all);
        TestCaseBrowser tcb = MultipleTCBTracker.getInstance().getMainTCB();
        if (tcb != null) {
            tcb.getTreeViewer().setSelection(
                    new StructuredSelection(newSpecTC), true);
        }
        return null;
    }

    /**
     * @param newTestCaseName
     *            the new test case name
     * @param nodesToClone
     *            the nodes to clone
     * @param category the category where the new Spec TC should be added
     * @return the new spec test case with cloned param nodes
     *         or null if something went wrong
     */
    private ISpecTestCasePO createAndPerformNodeDuplication(
            String newTestCaseName, List<INodePO> nodesToClone,
            INodePO category) {
        final CloneTransaction op = 
                new CloneTransaction(newTestCaseName, nodesToClone, category);
        
        if (TransactionWrapper.executeOperation(op)) {
            return op.getNewSpecTC();
        }
        return null;
    }
}

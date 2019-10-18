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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.TreeOpsBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransaction;
import org.eclipse.jubula.client.core.utils.NativeSQLUtils;
import org.eclipse.jubula.client.ui.rcp.actions.TransactionWrapper;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for extract Test Case Command
 * 
 * @author BREDEX GmbH
 * @created 27.04.2009
 */
public class ExtractTestCaseHandler extends AbstractRefactorHandler {
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
        // we have a non-null name and category
        
        editor.getEditorHelper().getClipboard().clearContents();
        final INodePO node = (INodePO) editor.getEditorHelper()
                .getEditSupport().getOriginal();
        if (node != null) {
            validateNode(node);
            IExecTestCasePO exec = performExtraction(getNewTCName(), node,
                    getSelection(), getCategory());
            try {
                editor.reOpenEditor(node);
                editor.getTreeViewer().setSelection(
                        new StructuredSelection(editor.getEntityManager().
                                find(exec.getClass(), exec.getId())));
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForEditor(e, editor);
            }
        } else {
            ErrorHandlingUtil.createMessageDialog((new JBException(
                    Messages.EditorWillBeClosed, MessageIDs.E_DELETED_TC)),
                    null, null);
            try {
                GeneralStorage.getInstance().reloadMasterSession(
                        new NullProgressMonitor());
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleProjectDeletedException();
            }
        }

        return null;
    }

    /**
     * @param node
     *            the {@link INodePO} on which the extraction is to be
     *            performed.
     */
    private void validateNode(INodePO node) {
        Assert.verify(node instanceof ISpecTestCasePO
                || node instanceof ITestSuitePO,
                Messages.ExtractTestCaseOperateISpecTestCasePO);
    }
    
    /**
     * The class execution the extraction
     * @author BREDEX GmbH
     */
    private static class ExtractOperation implements ITransaction {
        
        /** The objects to lock */
        private List<IPersistentObject> m_lock = null;
        
        /** The extracted nodes */
        private List<INodePO> m_modNodes;

        /** The Spec TC being edited */
        private INodePO m_owner;
        
        /** The new Exec TC */
        private IExecTestCasePO m_exec;
        
        /** The name of the new Spec TC */
        private String m_specName;
        
        /** Maper used to handle param names */
        private ParamNameBPDecorator m_mapper = new ParamNameBPDecorator(
                ParamNameBP.getInstance());

        /** The category where the new SpecTC should be added */
        private INodePO m_category;
        
        /**
         * Constructor
         * @param owner the Spec TC containing the extracted nodes 
         * @param modNodes the extracted nodes (they must have the same parent)
         * @param name the name of the new Spec TC
         * @param category the category where the new Spec TC should be added
         */
        public ExtractOperation(INodePO owner, List<INodePO> modNodes,
                String name, INodePO category) {
            m_modNodes = modNodes;
            m_owner = owner;
            m_specName = name;
            m_category = category;
        }
        
        /** {@inheritDoc} */
        public Collection<? extends IPersistentObject> getToLock() {
            m_lock = new ArrayList<>(2);
            m_lock.add(m_category);
            m_lock.add(m_owner);
            return m_lock;
        }

        /** {@inheritDoc} */
        public Collection<? extends IPersistentObject> getToRefresh() {
            return m_lock;
        }
        
        /** {@inheritDoc} */
        public Collection<? extends IPersistentObject> getToMerge() {
            List<IPersistentObject> toMerge = new ArrayList<>();
            toMerge.add(m_exec.getSpecTestCase());
            return toMerge;
        }

        /** {@inheritDoc} */
        public void run(EntityManager sess)
                throws PMException {
            List<INodePO> nodesToRef = new ArrayList<INodePO>();
            getModNodesFromCurrentSession(sess, nodesToRef);
            m_exec = TreeOpsBP.extractTestCase(m_specName, m_owner, nodesToRef,
                            sess, m_mapper);
            final ISpecTestCasePO newSpecTc = m_exec.getSpecTestCase();
            registerParamNamesToSave(newSpecTc, m_mapper);
            m_mapper.persist(sess, GeneralStorage.getInstance().getProject()
                    .getId());
            NativeSQLUtils.addNodeAFFECTS(sess, m_exec.getSpecTestCase(),
                    m_category);
        }
        
        /**
         * Loads the moved nodes to the session
         * @param s session used for refactoring
         * @param nodesToRef nodes to refactor from current session
         */
        private void getModNodesFromCurrentSession(EntityManager s,
                List<INodePO> nodesToRef) {
            if (m_modNodes.isEmpty()) {
                return;
            }
            // we have to load all parent-child relationships
            // of the SpecTC / TestSuite into the new session
            INodePO spec = m_modNodes.get(0).getSpecAncestor();
            spec = s.find(spec.getClass(), spec.getId());
            for (Iterator<INodePO> it = spec.getAllNodeIter(); it.hasNext(); ) {
                it.next();
            }
            for (INodePO node : m_modNodes) {
                INodePO object = s.find(node.getClass(), node.getId());
                if (object != null) {
                    nodesToRef.add(object);
                }
            }
        }
        
        /**
         * @return the used param name mapper
         */
        public ParamNameBPDecorator getMapper() {
            return m_mapper;
        }
        
        /**
         * @return the new exec node
         */
        public IExecTestCasePO getExec() {
            return m_exec;
        }

    }

    /**
     * performs the extraction
     * 
     * @param newTcName
     *            the name of the new SpecTestCase
     * @param node
     *            the edited {@link INodePO} from which to extract
     * @param selection
     *            the nodes to be extracted
     * @param category the category to put the new node into
     * @return an error message or null.
     */
    private IExecTestCasePO performExtraction(final String newTcName,
            final INodePO node, final IStructuredSelection selection,
            final INodePO category) {

        final List<INodePO> modNodes = new ArrayList<INodePO>(
                selection.size());
        Iterator<?> it = selection.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof INodePO) {
                modNodes.add((INodePO) next);
            }
        }
        if (modNodes.isEmpty()) {
            return null;
        }
        
        ExtractOperation op = new ExtractOperation(node, modNodes,
                newTcName, category);

        boolean succ = TransactionWrapper.executeOperation(op);
        if (!succ) {
            return null;
        }
        op.getMapper().updateStandardMapperAndCleanup(
                node.getParentProjectId());

        IExecTestCasePO newExec = op.getExec();
        
        DataEventDispatcher.getInstance().fireDataChangedListener(node,
                DataState.StructureModified, UpdateState.all);
        DataEventDispatcher.getInstance().fireDataChangedListener(
                newExec, DataState.Added, UpdateState.all);
        ISpecTestCasePO newSpecTC = newExec.getSpecTestCase();
        newSpecTC = GeneralStorage.getInstance().getMasterSession().find(
                newSpecTC.getClass(), newSpecTC.getId());
        DataEventDispatcher.getInstance().fireDataChangedListener(
                newSpecTC, DataState.Added, UpdateState.all);
        TestCaseBrowser tcb = MultipleTCBTracker.getInstance().getMainTCB();
        if (tcb != null) {
            tcb.getTreeViewer().setSelection(
                    new StructuredSelection(newSpecTC), true);
        }
        return newExec;
    }
}

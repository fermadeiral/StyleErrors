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
package org.eclipse.jubula.client.ui.rcp.handlers.existing.testcase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.events.InteractionEventDispatcher;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.TestCaseTreeDialog;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.editors.NodeEditorInput;
import org.eclipse.jubula.client.ui.rcp.handlers.NewTestCaseHandlerTCEditor;
import org.eclipse.jubula.client.ui.rcp.utils.NodeTargetCalculator.NodeTarget;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.swt.SWT;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 18.02.2009
 */
public class ReferenceExistingTestCase 
    extends AbstractSelectionBasedHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        final AbstractTestCaseEditor tce = 
            (AbstractTestCaseEditor)HandlerUtil.getActiveEditor(event);

        tce.getEditorHelper().doEditorOperation(new IEditorOperation() {
            public void run(IPersistentObject workingPo) {
                final INodePO editorNode = (INodePO)workingPo;
                final INodePO node = (INodePO) getSelection().getFirstElement();
                if (node == null) { // check for existing selection
                    return;
                }
                ISpecTestCasePO specTC = null;
                if (editorNode instanceof ISpecTestCasePO) {
                    specTC = (ISpecTestCasePO)editorNode;
                }
                TestCaseTreeDialog dialog = new TestCaseTreeDialog(
                        getActiveShell(), specTC, SWT.MULTI);
                dialog.setHelpAvailable(true);
                dialog.create();
                DialogUtils.setWidgetNameForModalDialog(dialog);
                Plugin.getHelpSystem().setHelp(dialog.getShell(),
                        ContextHelpIds.TESTCASE_ADD_EXISTING);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    addNewNodes(tce, node, dialog.getSelection());
                }
            }
        });

        return null;
    }

    /**
     * 
     * @param tce the {@link AbstractTestCaseEditor}
     * @param selected the node after which to add
     * @param listOfNodes the list of nodes to add
     */
    public void addNewNodes(final AbstractTestCaseEditor tce,
            final INodePO selected, List<INodePO> listOfNodes) {
        List<INodePO> selectedElements = listOfNodes;
        Collections.reverse(selectedElements);
        Iterator iter = selectedElements.iterator();
        List<IExecTestCasePO> addedElements = new ArrayList<IExecTestCasePO>();
        NodeTarget place = NewTestCaseHandlerTCEditor.getPositionToInsert(
                selected, tce.getTreeViewer().getExpandedState(selected));
        if (place == null) {
            return;
        }
        try {
            while (iter.hasNext()) {
                ISpecTestCasePO specTcToInsert = (ISpecTestCasePO) iter.next();
                try {
                    addedElements.add(TestCaseBP.addReferencedTestCase(
                            tce.getEditorHelper().getEditSupport(),
                            place.getNode(), specTcToInsert, place.getPos()));
                } catch (PMException e) {
                    NodeEditorInput inp = (NodeEditorInput) tce
                            .getAdapter(NodeEditorInput.class);
                    INodePO inpNode = inp.getNode();
                    PMExceptionHandler.handlePMExceptionForMasterSession(e);
                    tce.reOpenEditor(inpNode);
                }
                InteractionEventDispatcher.getDefault()
                        .fireProgammableSelectionEvent(
                                new StructuredSelection(specTcToInsert));
            }
            tce.getEditorHelper().getEditSupport().lockWorkVersion();
            tce.getEditorHelper().setDirty(true);
            tce.refresh();
            tce.getTreeViewer().setExpandedState(place.getNode(), true);
            tce.setSelection(new StructuredSelection(addedElements));
        } catch (PMException e1) {
            PMExceptionHandler.handlePMExceptionForEditor(e1, tce);
        }
    }

}

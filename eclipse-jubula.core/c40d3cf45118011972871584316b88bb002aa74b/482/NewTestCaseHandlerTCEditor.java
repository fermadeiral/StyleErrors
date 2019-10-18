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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.editors.TestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.NodeTargetCalculator;
import org.eclipse.jubula.client.ui.rcp.utils.NodeTargetCalculator.NodeTarget;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;

/**
 * @author BREDEX GmbH
 * @created 27.06.2006
 */
public class NewTestCaseHandlerTCEditor extends AbstractNewHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        final TestCaseEditor tce = (TestCaseEditor)Plugin.getActiveEditor();
        if (!(tce.getTreeViewer().getSelection() 
                instanceof IStructuredSelection)) {
            return null;
        }

        tce.getEditorHelper().doEditorOperation(new IEditorOperation() {
            public void run(IPersistentObject workingPo) {
                INodePO selectedNode = (INodePO)((IStructuredSelection)tce
                        .getTreeViewer().getSelection()).getFirstElement();
                final ISpecTestCasePO editorNode = (ISpecTestCasePO)workingPo;
                InputDialog dialog = new InputDialog(getActiveShell(),
                        Messages.NewTestCaseActionTCTitle,
                        InitialValueConstants.DEFAULT_TEST_CASE_NAME,
                        Messages.NewTestCaseActionTCMessage,
                        Messages.RenameActionTCLabel,
                        Messages.RenameActionTCError,
                        Messages.NewTestCaseActionDoubleTCName,
                        IconConstants.NEW_TC_DIALOG_STRING,
                        Messages.NewTestCaseActionTCShell, false);
                
                dialog.setHelpAvailable(true);
                dialog.create();
                Plugin.getHelpSystem().setHelp(dialog.getShell(),
                        ContextHelpIds.DIALOG_TC_ADD_NEW);
                DialogUtils.setWidgetNameForModalDialog(dialog);
                dialog.open();
                ISpecTestCasePO newSpecTC = null;
                if (Window.OK == dialog.getReturnCode()) {
                    String tcName = dialog.getName();
                    try {
                        newSpecTC = TestCaseBP.createNewSpecTestCase(tcName,
                                GeneralStorage.getInstance().
                                getProject().getSpecObjCont());
                        DataEventDispatcher.getInstance()
                            .fireDataChangedListener(
                                newSpecTC, DataState.Added, UpdateState.all);
                    } catch (PMException e) {
                        PMExceptionHandler.handlePMExceptionForMasterSession(e);
                    } catch (ProjectDeletedException e) {
                        PMExceptionHandler.handleProjectDeletedException();
                    }
                }
                if (newSpecTC != null) {
                    Integer index = null;
                    NodeTarget place = getPositionToInsert(selectedNode,
                            tce.getTreeViewer().getExpandedState(
                                    selectedNode));
                    if (place == null) {
                        return;
                    }

                    try {
                        ISpecTestCasePO workNewSpecTC = (ISpecTestCasePO) tce
                                .getEditorHelper().getEditSupport()
                                .createWorkVersion(newSpecTC);
                        IExecTestCasePO newExecTC = TestCaseBP
                                .addReferencedTestCase(tce.getEditorHelper()
                                        .getEditSupport(), place.getNode(),
                                        workNewSpecTC, place.getPos());

                        tce.getEditorHelper().setDirty(true);
                        DataEventDispatcher.getInstance()
                            .fireDataChangedListener(
                                newExecTC, DataState.Added, UpdateState.all);
                    } catch (PMException e) {
                        PMExceptionHandler.handlePMExceptionForEditor(e, tce);
                    }
                }
            }
        });
        return null;
    }
    
    /**
     * @param node the currently selected node: we insert below this or at the end of the branch
     * @param exp whether node is expanded
     * @return the position to add
     */
    public static NodeTarget getPositionToInsert(INodePO node, boolean exp) {
        /* Insert the node after the selected node? */
        if (Plugin.getDefault().getPreferenceStore()
                .getBoolean(Constants.NODE_INSERT_KEY)) {
            return NodeTargetCalculator.calcNodeTarget(null, node,
                    ViewerDropAdapter.LOCATION_AFTER, exp);
        }
        /* Is the top node of a branch selected? */
        if (node instanceof IAbstractContainerPO
                || node instanceof ISpecTestCasePO
                || node instanceof ITestSuitePO) {
            return new NodeTarget(node.getNodeListSize(), node);
        }
        /* Is a node within a branch selected? */
        if (node.getParentNode() != null) {
            return new NodeTarget(node.getParentNode().getNodeListSize(),
                    node.getParentNode());
        }
        /* Something went wrong here. */
        return null;
    }

}
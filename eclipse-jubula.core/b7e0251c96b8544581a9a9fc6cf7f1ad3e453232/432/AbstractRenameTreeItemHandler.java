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
package org.eclipse.jubula.client.ui.rcp.handlers.rename;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.editors.NodeEditorInput;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;


/**
 * Superclass of all RenameTreeItemHandlers
 *
 * @author BREDEX GmbH
 * @created 09.03.2006
 */
public abstract class AbstractRenameTreeItemHandler 
    extends AbstractSelectionBasedHandler {
    /**
     * @param node the corresponding NodePO.
     * @return a new InputDialog.
     */
    protected InputDialog createDialog(final INodePO node) {
        if (node instanceof ITestSuitePO) {
            return createRenameTestSuiteDialog(node);
        } else if (node instanceof ITestJobPO) {
            return createRenameTestJobDialog(node);
        } else if (node instanceof ITestCasePO) {
            return createRenameTestCaseDialog(node);
        } else if (node instanceof ICategoryPO) {
            return createRenameCategoryDialog(node);
        }
        return null;
    }

    /**
     * @param node the node to rename
     * @return the dialog for renaming
     */
    private InputDialog createRenameTestJobDialog(final INodePO node) {
        final IProjectPO project = GeneralStorage.getInstance().getProject();
        return new InputDialog(getActiveShell(), 
                Messages.RenameActionTJTitle,
                node.getName(), Messages.RenameActionTJMessage,
                Messages.RenameActionTJLabel,
                Messages.RenameActionTJError,
                Messages.RenameActionDoubleTJName,
                IconConstants.TJ_DIALOG_STRING, Messages.RenameActionTJShell,
                false) {
            protected boolean isInputAllowed() {
                String newName = getInputFieldText();
                return !(!node.getName().equals(newName)
                        && ProjectPM
                        .doesTestJobExists(project.getId(), newName));
            }
        };
    }

    /**
     * @param node the node to rename
     * @return the dialog for renaming
     */
    private InputDialog createRenameCategoryDialog(final INodePO node) {
        return new InputDialog(getActiveShell(),
            Messages.RenameActionCatTitle, 
            node.getName(), Messages.RenameActionCatMessage, 
            Messages.RenameActionCatLabel, 
            Messages.RenameActionCatError,
            Messages.RenameActionDoubleCatName,
            IconConstants.RENAME_CAT_DIALOG_STRING,
            Messages.RenameActionCatShell,
            false);
    }

    /**
     * @param node the node to rename
     * @return the dialog for renaming
     */
    private InputDialog createRenameTestCaseDialog(final INodePO node) {
        String name = node.getName();
        if (node instanceof IExecTestCasePO) {
            name = ((IExecTestCasePO)node).getRealName();
        }
        return new InputDialog(getActiveShell(),
            Messages.RenameActionTCTitle,
            name, Messages.RenameActionTCMessage,
            Messages.RenameActionTCLabel,
            Messages.RenameActionTCError,
            Messages.RenameActionDoubleTCName,
            IconConstants.RENAME_TC_DIALOG_STRING,
            Messages.RenameActionTCShell,
            false);
    }

    /**
     * @param node the node to rename
     * @return the dialog for renaming
     */
    private InputDialog createRenameTestSuiteDialog(final INodePO node) {
        final IProjectPO project = GeneralStorage.getInstance().
            getProject();
        return new InputDialog(getActiveShell(),
            Messages.RenameActionTSTitle, 
            node.getName(), Messages.RenameActionTSMessage,
            Messages.RenameActionTSLabel,
            Messages.RenameActionTSError,
            Messages.RenameActionDoubleTSName,
            IconConstants.NEW_TS_DIALOG_STRING, 
            Messages.RenameActionTSShell,
            false) {
            protected boolean isInputAllowed() {
                String newName = getInputFieldText();
                return !(!node.getName().equals(newName)
                        && ProjectPM.doesTestSuiteExists(project.getId(),
                        newName));
            }
        };
    }
    
    /**
     * @param node the node to rename
     * @return true, if renaming of the node is allowed.
     */
    private boolean isRenamingAllowed(INodePO node) {
        if (node instanceof ISpecTestCasePO) {
            ISpecTestCasePO spec = (ISpecTestCasePO)node;
            for (IExecTestCasePO reuse 
                : NodePM.getInternalExecTestCases(
                    spec.getGuid(), spec.getParentProjectId())) {
                INodePO reusedInNode = reuse.getParentNode();
                for (IEditorReference edit : Plugin.getAllEditors()) {
                    try {
                        if (edit.isDirty() && edit.getEditorInput() 
                                instanceof NodeEditorInput) {
                            
                            INodePO editNode = ((NodeEditorInput)edit
                                    .getEditorInput()).getNode();
                            if (reusedInNode instanceof ITestSuitePO 
                                    && editNode == reusedInNode) {
                                
                                ErrorHandlingUtil.createMessageDialog(
                                        MessageIDs.I_LOCK_OBJ_3, 
                                        new Object[]{node.getName(), 
                                            reusedInNode.getName(),
                                                edit.getPartName()}, null);
                                return false;
                            }
                            if (editNode == reusedInNode) {
                                ErrorHandlingUtil.createMessageDialog(
                                        MessageIDs.I_LOCK_OBJ_2, 
                                        new Object[]{node.getName(), 
                                            reusedInNode.getName(),
                                                edit.getPartName()}, null);
                                return false;
                            }
                        }
                    } catch (PartInitException e) {
                        // do nothing
                    }
                }
            }
        } else if (node instanceof IExecTestCasePO) {
            for (IEditorReference edit : Plugin.getAllEditors()) {
                try {
                    if (edit.isDirty() && edit.getEditorInput() 
                            instanceof NodeEditorInput) {
                        
                        INodePO editNode = ((NodeEditorInput)edit
                                .getEditorInput()).getNode();
                        if (editNode == node.getParentNode()) {
                            ErrorHandlingUtil.createMessageDialog(
                                    MessageIDs.I_LOCK_OBJ_3, 
                                    new Object[]{node.getName(), 
                                        editNode.getName(),
                                            edit.getPartName()}, null);
                            return false;
                        }
                    }
                } catch (PartInitException e) {
                    // do nothing
                }
            }
        }
        return true;
    }
    
    /**
     * Opens the dialog for renaming a node.
     * @param sel The actual selection.
     */
    protected void dialogPopUp(IStructuredSelection sel) {
        INodePO node = (INodePO)sel.getFirstElement();
        InputDialog dialog = createDialog(node);
        if (dialog != null) {
            dialog.setHelpAvailable(true);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            Plugin.getHelpSystem().setHelp(dialog.getShell(), 
                ContextHelpIds.DIALOG_RENAME);
            dialog.open();
            if (dialog.getReturnCode() == Window.OK) {
                try {
                    if (!isRenamingAllowed(node)) {
                        return;
                    }
                    NodePM.renameNode(node, dialog.getName());
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                            node, DataState.Renamed, UpdateState.all);
                } catch (PMException e) {
                    PMExceptionHandler.handlePMExceptionForMasterSession(e);
                } catch (ProjectDeletedException e) {
                    PMExceptionHandler.handleProjectDeletedException();
                }
            }
        }
    }
}
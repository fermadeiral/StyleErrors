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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.OMEditorBP;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 29.05.2006
 */
public class RenameCategoryHandlerOMEditor 
        extends AbstractRenameTreeItemHandler {
    
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        if (editor instanceof ObjectMappingMultiPageEditor) {
            dialogPopUp((ObjectMappingMultiPageEditor)editor, 
                    getSelection());
        }

        return null;
    }

    /**
     * Opens the dialog for renaming a node.
     * 
     * @param editor The current editor.
     * @param sel The current selection.
     * 
     */
    protected void dialogPopUp(final ObjectMappingMultiPageEditor editor, 
            final IStructuredSelection sel) {
        editor.getEditorHelper().doEditorOperation(new IEditorOperation() {
            
            public void run(IPersistentObject workingPo) {
                IObjectMappingCategoryPO category = 
                        (IObjectMappingCategoryPO)sel.getFirstElement();
                InputDialog dialog = 
                        createDialog(category, editor.getOmEditorBP());
                dialog.setHelpAvailable(true);
                dialog.create();
                DialogUtils.setWidgetNameForModalDialog(dialog);
                Plugin.getHelpSystem().setHelp(dialog.getShell(), 
                    ContextHelpIds.DIALOG_RENAME);
                if (dialog.open() == Window.OK) {
                    if (!category.getName().equals(dialog.getName())) {
                        category.setName(dialog.getName());
                        editor.getEditorHelper().setDirty(true);
                        DataEventDispatcher.getInstance()
                            .fireDataChangedListener(category, 
                                    DataState.Renamed, 
                                    UpdateState.onlyInEditor);
                    }
                }
            }
        });
    }
    
    /**
     * @param category the corresponding object mapping category.
     * @param editorBp The business process to use for checking the existence
     *                 of a category.
     * @return a new InputDialog. Never <code>null</code>.
     */
    private InputDialog createDialog(final IObjectMappingCategoryPO category, 
            final OMEditorBP editorBp) {
        
        return new InputDialog(getActiveShell(), 
                Messages.RenameCategoryActionOMEditorTitle,
                category.getName(), 
                Messages.RenameCategoryActionOMEditorMessage,
                Messages.RenameCategoryActionOMEditorLabel,
                Messages.RenameCategoryActionOMEditorError1,
                Messages.RenameCategoryActionOMEditorDoubleCatName,
                IconConstants.RENAME_CAT_DIALOG_STRING, 
                Messages.RenameCategoryActionOMEditorShell,
                false) {
                /**
                 * @return False, if the input name already exists.
                 */
                protected boolean isInputAllowed() {
                    return (!editorBp.existCategory(
                            category.getParent(), getInputFieldText())
                        || category.getName().equals(getInputFieldText()));
                }
            };
    }
}
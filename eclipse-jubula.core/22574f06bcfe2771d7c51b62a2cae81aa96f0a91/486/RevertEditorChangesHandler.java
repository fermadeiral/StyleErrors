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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author BREDEX GmbH
 * @created 04.03.2008
 */
public class RevertEditorChangesHandler extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        final IWorkbenchPart activePart = Plugin.getActivePart();
        if (activePart == null) {
            return null;
        }
        
        final IJBEditor editor = activePart.getAdapter(IJBEditor.class);
        if (editor != null) {
            MessageDialog dialog = showConfirmDialog();
            if (dialog.getReturnCode() == Window.OK) {
                revertEditorChanges(editor);
            }
        }
        return null;
    }
    
    /**
     * Shows confirm dialog for this action and returns the dialog object
     * @return confirm dialog for this action
     */
    private MessageDialog showConfirmDialog() {
        MessageDialog dialog = new MessageDialog(getActiveShell(), 
            Messages.RevertEditorChangesActionShellTitle,
                null,
                Messages.RevertEditorChangesActionQuestionText,
                MessageDialog.QUESTION, new String[] {
                    Messages.DialogMessageButton_YES,
                    Messages.DialogMessageButton_NO }, 0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog;
    }

    /**
     * @param editor The editor in that the changes have to be reverted
     */
    private void revertEditorChanges(IJBEditor editor) {
        try {
            editor.reOpenEditor(
                    editor.getEditorHelper().getEditSupport().getOriginal());
            if (editor instanceof AbstractJBEditor) {
                ((AbstractJBEditor) editor).getTreeViewer().expandAll();
            }
        } catch (PMException e) {
            ErrorHandlingUtil.createMessageDialog(
                MessageIDs.E_REVERT_EDITOR_CHANGES_FAILED);
        }
    }
}

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

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog.Parameter;
import org.eclipse.jubula.client.ui.rcp.dialogs.EditParametersDialog;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.utils.DialogUtils;


/**
 * @author BREDEX GmbH
 * @created Oct 29, 2007
 */
public class EditParametersHandler extends AbstractEditParametersHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        final IJBEditor editor = getEditor(event);
        if (editor != null) {
            editor.getEditorHelper().doEditorOperation(new IEditorOperation() {
                public void run(IPersistentObject workingPo) {
                    final ISpecTestCasePO workTC = 
                            (ISpecTestCasePO)workingPo;
                    final EditParametersDialog dialog = 
                            new EditParametersDialog(getActiveShell(), workTC);
                    dialog.create();
                    DialogUtils.setWidgetNameForModalDialog(dialog);
                    if (dialog.open() ==  Window.OK) {
                        performChanges(editor, workTC, dialog);
                    }
                }
            });
        }
        return null;
    }

    /**
     * Performs the changes done in the EditParametersDialog
     * @param editor the TestCaseEditor
     * @param workTC the working ISpecTestCasePO
     * @param dialog the EditParametersDialog
     */
    private static void performChanges(IJBEditor editor, 
        ISpecTestCasePO workTC, EditParametersDialog dialog) {
        
        final List<Parameter> parameters = dialog.getParameters();
        final boolean isInterfaceLocked = dialog.isInterfaceLocked();
        boolean isModified = editParameters(workTC, parameters, 
            isInterfaceLocked, 
            editor.getEditorHelper().getEditSupport().getParamMapper(),
            new TestCaseParamBP());
        if (isModified) {
            editor.getEditorHelper().setDirty(true);
            DataEventDispatcher.getInstance()
                .fireParamChangedListener();
            DataEventDispatcher.getInstance().firePropertyChanged(false);
        }
    }
}

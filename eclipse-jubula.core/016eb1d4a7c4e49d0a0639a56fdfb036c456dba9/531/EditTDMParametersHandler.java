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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.ParameterInterfaceBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog.Parameter;
import org.eclipse.jubula.client.ui.rcp.dialogs.EditParametersTDMDialog;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Jul 14, 2010
 */
public class EditTDMParametersHandler extends AbstractEditParametersHandler {
    /**
     * @param event
     *            An event containing all the information about the current
     *            state of the application; must not be <code>null</code>.
     * @return the currently selected TestDataCube, or <code>null</code> if 
     *         no TestDataCube is currently selected.
     */
    protected final ITestDataCubePO getSelectedTestDataManager(
            ExecutionEvent event) {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)selection;
            Object selectedObject = structuredSelection.getFirstElement();
            if (selectedObject instanceof ITestDataCubePO) {
                return (ITestDataCubePO)selectedObject;
            }
        }
        
        return null;
    }

    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        final ITestDataCubePO tdc = getSelectedTestDataManager(event);
        if (tdc != null) {
            final IJBEditor editor = getEditor(event);
            editor.getEditorHelper().doEditorOperation(new IEditorOperation() {
                public void run(IPersistentObject workingPo) {
                    final AbstractEditParametersDialog dialog = 
                            new EditParametersTDMDialog(getActiveShell(), tdc);
                    dialog.create();
                    DialogUtils.setWidgetNameForModalDialog(dialog);
                    if (dialog.open() == Window.OK) {
                        performChanges(editor, tdc, dialog);
                    }
                }
            });
        }
        return null;
    }
    /**
     * @param editor
     *            the current editor
     * @param tdc
     *            the test data cube to perform the changes on
     * @param dialog
     *            the edit parameters dialog
     */
    private void performChanges(IJBEditor editor, ITestDataCubePO tdc,
            AbstractEditParametersDialog dialog) {
        final List<Parameter> parameters = dialog.getParameters();
        boolean isModified = editParameters(tdc, parameters, editor
                .getEditorHelper().getEditSupport().getParamMapper(),
                new ParameterInterfaceBP());
        if (isModified) {
            editor.getEditorHelper().setDirty(true);
            DataEventDispatcher ded = DataEventDispatcher.getInstance();
            ded.fireParamChangedListener();
            ded.firePropertyChanged(false);
            ded.fireDataChangedListener(tdc, 
                    DataState.StructureModified, UpdateState.onlyInEditor);
        }
    }
}

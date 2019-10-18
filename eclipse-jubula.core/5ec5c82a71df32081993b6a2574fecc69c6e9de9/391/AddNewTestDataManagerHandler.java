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

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.dialogs.EnterTestDataManagerDialog;
import org.eclipse.jubula.client.ui.rcp.dialogs.NewTestDataManagerDialog;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Jun 29, 2010
 */
public class AddNewTestDataManagerHandler extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(final ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof CentralTestDataEditor) {
            final CentralTestDataEditor ctdEditor = 
                    (CentralTestDataEditor)activePart;

            ctdEditor.getEditorHelper().doEditorOperation(
                    new IEditorOperation() {
                        public void run(IPersistentObject workingPo) {
                            ISelection selection = 
                                    HandlerUtil.getCurrentSelection(event);
        
                            // Show dialog
                            String newName = openDialog(
                                    HandlerUtil.getActiveShell(event),
                                    getSetOfUsedNames(ctdEditor));
        
                            if (newName != null) {
                                performOperation(ctdEditor, newName, selection);
                            }
                        }
                    });
        }

        return null;
    }

    /**
     * @param ctdEditor the editor
     * @return a set of used test data cube names
     */
    public static Set<String> getSetOfUsedNames(
            CentralTestDataEditor ctdEditor) {
        ITestDataCategoryPO po = (ITestDataCategoryPO)ctdEditor
                .getEditorHelper().getEditSupport().getWorkVersion();
        return TestDataCubeBP.getSetOfUsedNames(po);
    }

    /**
     * Opens the "New Test Data Set..." dialog.
     * 
     * @param parentShell The parent Shell for the dialog.
     * @param usedNames a set of already used names
     * @return the name typed into the dialog, or <code>null</code> if the 
     *         dialog was cancelled.
     */
    protected String openDialog(Shell parentShell, Set<String> usedNames) {
        EnterTestDataManagerDialog newNameDialog = 
            new NewTestDataManagerDialog(parentShell, usedNames);
        newNameDialog.setHelpAvailable(true);
        newNameDialog.create();
        DialogUtils.setWidgetNameForModalDialog(newNameDialog);
        Plugin.getHelpSystem().setHelp(newNameDialog.getShell(), 
                ContextHelpIds.NEW_TESTDATACUBE_NAME);
        if (newNameDialog.open() == Window.OK) {
            return newNameDialog.getName();
        }
        return null;
    }

    /**
     * @param ctdEditor
     *            the editor
     * @param newName
     *            the new name
     * @param selection
     *            the current selection
     */
    private void performOperation(CentralTestDataEditor ctdEditor,
            String newName, ISelection selection) {
        EditSupport es = ctdEditor.getEditorHelper().getEditSupport();
        ITestDataCategoryPO parent = AddNewTestDataCategoryHandler.getParent(
                selection, (ITestDataCategoryPO)es.getWorkVersion());
        ITestDataCubePO testdata = PoMaker.createTestDataCubePO(newName);
        parent.addTestData(testdata);
        ctdEditor.getEditorHelper().setDirty(true);
        DataEventDispatcher.getInstance().fireDataChangedListener(testdata,
                DataState.Added, UpdateState.onlyInEditor);
    }
}

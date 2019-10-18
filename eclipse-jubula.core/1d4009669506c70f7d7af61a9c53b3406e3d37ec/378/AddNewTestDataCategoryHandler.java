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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Zeb Ford-Reitz
 * @created Oct 02, 2011
 */
public class AddNewTestDataCategoryHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(final ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof CentralTestDataEditor) {
            CentralTestDataEditor ctdEditor = (CentralTestDataEditor)activePart;
            ctdEditor.getEditorHelper().doEditorOperation(
                    new IEditorOperation() {
                
                        public void run(IPersistentObject workingPo) {
                            ITestDataCategoryPO rootCategory = 
                                    (ITestDataCategoryPO)workingPo;
                            
                            ITestDataCategoryPO categoryParent = getParent(
                                    HandlerUtil.getCurrentSelection(event), 
                                    rootCategory);
                            
                            InputDialog dialog = new InputDialog(
                                getActiveShell(), 
                                Messages.CreateNewCategoryActionCatTitle,
                                InitialValueConstants.DEFAULT_CATEGORY_NAME,
                                Messages.CreateNewCategoryActionCatMessage,
                                Messages.CreateNewCategoryActionCatLabel,
                                Messages.CreateNewCategoryActionCatError,
                                Messages.CreateNewCategoryActionDoubleCatName,
                                IconConstants.NEW_CAT_DIALOG_STRING,
                                Messages.CreateNewCategoryActionNewCategory, 
                                false);
        
                            dialog.setHelpAvailable(true);
                            dialog.create();
                            DialogUtils.setWidgetNameForModalDialog(dialog);
                            Plugin.getHelpSystem().setHelp(dialog.getShell(),
                                ContextHelpIds.NEW_TEST_DATA_CATEGORY);
                            dialog.open();
                            if (Window.OK == dialog.getReturnCode()) {
                                ITestDataCategoryPO category = 
                                        PoMaker.createTestDataCategoryPO(
                                                dialog.getName());
                                categoryParent.addCategory(category);
                                DataEventDispatcher.getInstance()
                                    .fireDataChangedListener(
                                        category, DataState.Added, 
                                        UpdateState.onlyInEditor);
                            }
                        }
                    });
            
        }

        return null;
    }

    /**
     * 
     * 
     * @param selection The current selection.
     * @param fallback The parent to return if <code>selection</code> does
     *                 not contain a valid parent.
     * @return the selected parent, or <code>fallback</code> if 
     *         <code>selection</code> does not contain a valid parent.
     */
    public static ITestDataCategoryPO getParent(
            ISelection selection, ITestDataCategoryPO fallback) {

        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = 
                    (IStructuredSelection)selection;
            Object selectedObject = structuredSelection.getFirstElement();
            if (selectedObject instanceof ITestDataCategoryPO) {
                return (ITestDataCategoryPO)selectedObject;
            }
        }
        
        return fallback;
    }

}

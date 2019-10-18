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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.dialogs.TestCaseTreeDialog;
import org.eclipse.jubula.client.ui.rcp.editors.TestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 05.04.2005
 */
public class AddExistingEventHandlerHandler extends AbstractHandler {
    /**
     * @author BREDEX GmbH
     */
    private static class SelectionTransfer {
        /**
         * the selection to transfer
         */
        private ISelection m_selection = null;

        /**
         * @param selection the selection
         */
        public void setSelection(ISelection selection) {
            this.m_selection = selection;
        }

        /**
         * @return the selection
         */
        public ISelection getSelection() {
            return m_selection;
        }
    }
    
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        Assert.verify(editor instanceof TestCaseEditor, 
            Messages.WrongEditorType + StringConstants.EXCLAMATION_MARK);
        final TestCaseEditor testCaseEditor = (TestCaseEditor)editor;
        testCaseEditor.getEditorHelper().doEditorOperation(
                new IEditorOperation() {
                    public void run(IPersistentObject workingPo) {
                        openTestCasePopUp(testCaseEditor);
                    }
                });

        return null;
    }
        
    /**
     * Opens the PopUp with the TestCaseTree.
     * @param editor The test case editor.
     */
    private void openTestCasePopUp(final TestCaseEditor editor) {  
        final ISpecTestCasePO parentNode = (ISpecTestCasePO)editor
            .getTreeViewer().getTree().getItem(0).getData(); 
        if (hasTestCaseAllEventHandler(parentNode)) {
            
            return;
        }
        String title = Messages.AddEventHandlerActionAddEventHandler;
        TestCaseTreeDialog dialog = new TestCaseTreeDialog(
                getActiveShell(), title, StringConstants.EMPTY, 
            parentNode, title, SWT.SINGLE, 
            IconConstants.ADD_EH_IMAGE); 
        final SelectionTransfer selTransferObj = new SelectionTransfer();
        ISelectionListener selListener = new ISelectionListener() {
            public void selectionChanged(IWorkbenchPart part,
                    ISelection selection) {
                selTransferObj.setSelection(selection);
            }
        };
        dialog.addSelectionListener(selListener);
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        Plugin.getHelpSystem().setHelp(dialog.getShell(), 
            ContextHelpIds.EVENT_HANDLER_ADD);
        int returnCode = dialog.open();
        if (returnCode == IDialogConstants.OK_ID) {
            addEventHandler(selTransferObj.getSelection(), parentNode, editor);
        }
        dialog.removeSelectionListener(selListener);
    }
    
    /**
     * Checks, if the actual test case has eventhandler with all available event types.
     * @param parentNode The actual test case.
     * @return True, if the actual test case has eventhandler with all available event types, false otherwise.
     */
    public static boolean hasTestCaseAllEventHandler(
            ISpecTestCasePO parentNode) {
        Collection eventTcList = parentNode.getAllEventEventExecTC();
        // get a List of used event types in this TestCase.
        List < String > existentEventTypes = new ArrayList < String > ();
        for (Object object : eventTcList) {
            IEventExecTestCasePO eventTc = (IEventExecTestCasePO)object;
            existentEventTypes.add(eventTc.getEventType());
        }
        
        Set mapKeySet = ComponentBuilder.getInstance().getCompSystem()
            .getEventTypes().keySet(); 
        String[] eventTypes = new String[mapKeySet.size()];
        int i = 0;
        for (Object object : mapKeySet) {
            eventTypes[i] = object.toString();
            i++;
        }
        List < String > availableEventTypes = Arrays.asList(eventTypes);
        if (availableEventTypes.size() == existentEventTypes.size()) {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.I_ENOUGH_EVENT_HANDLER, null,
                    new String[]{NLS.bind(
                            Messages.AddEventHandlerDialogEnoughEventHandler,
                            parentNode.getName())});
            return true;
        }
        return false;
    }
     
    /**
     * Adds the given Selection as Eventhandler.
     * @param selection the ISelection to add as EventHandler
     * @param nodeGUI The selected nodeGUI.
     * @param editor the editor
     */
    void addEventHandler(ISelection selection, INodePO nodeGUI, 
            TestCaseEditor editor) {
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        ISpecTestCasePO eventHandler = (ISpecTestCasePO)
            ((IStructuredSelection)selection).getFirstElement();
        editor.addEventHandler(eventHandler, (ISpecTestCasePO)nodeGUI);
    }
}
/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers.edit;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.TCEditorDndSupport;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.TJEditorDndSupport;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.TSEditorDndSupport;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestJobEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestSuiteEditor;


/**
 * Implementation of the Paste action within most of the Editors.
 *
 * @author BREDEX GmbH
 */
public class PasteJBEditorHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        AbstractJBEditor jbe = 
            (AbstractJBEditor)Plugin.getActiveEditor();
        LocalSelectionClipboardTransfer transfer = 
            LocalSelectionClipboardTransfer.getInstance();
        
        if (!(jbe.getEditorHelper().getClipboard().getContents(transfer)
                instanceof IStructuredSelection)) {
            return null;
        }
        IStructuredSelection pasteSelection = 
            (IStructuredSelection)jbe.getEditorHelper()
                .getClipboard().getContents(transfer);
        if (pasteSelection != null
                && jbe.getSelection() instanceof IStructuredSelection) {

            // Paste will always occur at the most recently selected node.
            IStructuredSelection selection = 
                (IStructuredSelection)jbe.getSelection();
            Object [] selArray = selection.toArray();
            
            INodePO target;
            if (jbe.getSelection().isEmpty()) {
                target = (INodePO)jbe.getTreeViewer().getTree().getTopItem()
                        .getData();
            } else {
                target = (INodePO)selArray[selArray.length - 1];
            }
            
            if (!transfer.getIsItCut()) {
                if (jbe instanceof TestCaseEditor) {
                    TCEditorDndSupport.copyPaste((TestCaseEditor)jbe,
                            pasteSelection, target);
                } else if (jbe instanceof TestSuiteEditor) {
                    new TSEditorDndSupport().copyPaste((TestSuiteEditor)jbe,
                            pasteSelection, target);
                } else if (jbe instanceof TestJobEditor) {
                    TJEditorDndSupport.copyPaste(jbe, pasteSelection, target);
                }
            } else if (jbe instanceof AbstractTestCaseEditor) {
                if (TCEditorDndSupport.performDrop((AbstractTestCaseEditor)jbe,
                        pasteSelection, target,
                        ViewerDropAdapter.LOCATION_ON)) {
                    jbe.getEditorHelper().getClipboard().clearContents();
                    transfer.setSelection(null, null, false);
                }
            }
        }
        
        return null;
    }
}

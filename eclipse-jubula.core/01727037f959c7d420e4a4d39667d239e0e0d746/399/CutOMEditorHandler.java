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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Implementation of the Cut action within the Object Mapping Editor.
 *
 * @author BREDEX GmbH
 * @created 20.03.2008
 */
public class CutOMEditorHandler extends AbstractHandler {

    /**
     * 
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        ObjectMappingMultiPageEditor ome = 
            (ObjectMappingMultiPageEditor)HandlerUtil.getActiveEditor(event);
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (!(sel instanceof IStructuredSelection)) {
            return null;
        }

        IStructuredSelection selection = (IStructuredSelection)sel;
        LocalSelectionClipboardTransfer transfer = 
            LocalSelectionClipboardTransfer.getInstance(); 
        ome.getEditorHelper().getClipboard().setContents(
                new Object [] {selection}, new Transfer [] {transfer});
        transfer.setSelection(selection, ome.getTreeViewer(),
                ome.getTreeViewers(), true);
        
        return null;
    }

}

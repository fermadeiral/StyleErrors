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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.swt.dnd.Transfer;


/**
 * Implementation of the Copy action within most of the Editors.
 *
 * @author BREDEX GmbH
 */
public class CopyJBEditorHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        AbstractJBEditor jbe = (AbstractJBEditor) Plugin.getActiveEditor();
        ISelection selection = jbe.getSelection();
        if (!(selection instanceof IStructuredSelection)) {
            return null;
        }
        IStructuredSelection strucSelection = (IStructuredSelection) selection;
        LocalSelectionClipboardTransfer transfer = 
                LocalSelectionClipboardTransfer.getInstance();
        jbe.getEditorHelper().getClipboard()
                .setContents(new Object[] { strucSelection },
                        new Transfer[] { transfer });
        transfer.setSelection(strucSelection, jbe.getTreeViewer(), false);
        return null;
    }
}

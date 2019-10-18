/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers.open;

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 */
public abstract class AbstractOpenEditorHandler extends AbstractOpenHandler {

    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        if (!(currentSelection instanceof IStructuredSelection)) {
            return null;
        }
        Iterator iter = ((IStructuredSelection) currentSelection).iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof INodePO) {
                INodePO selectedNode = (INodePO) obj;
                INodePO editableNode = findEditableNode(selectedNode);
                if (editableNode != null) {
                    IEditorPart openEditor = openEditor(editableNode);
                    if (openEditor instanceof AbstractJBEditor) {
                        AbstractJBEditor jbEditor = 
                                (AbstractJBEditor) openEditor;
                        jbEditor.setSelection(
                                new StructuredSelection(selectedNode));
                    }
                }
            }
        }
        return null;
    }
}

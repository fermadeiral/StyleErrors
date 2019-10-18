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

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created Feb 10, 2011
 */
public class CopyIdToClipboardHandler extends AbstractClipboardHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (sel instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection)sel;
            Iterator<INodePO> iter = selection.iterator();
            while (iter.hasNext()) {
                INodePO selectedNode = iter.next();
                INodePO editableNode = findEditableNode(selectedNode);
                if (editableNode != null) {
                    copyIDToClipboard(editableNode);
                }
            }
        }
        return null;
    }
}

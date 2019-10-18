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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 22.10.2015
 */
public class DeleteCommentHandler extends AbstractSelectionBasedHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof AbstractJBEditor) {
            final AbstractJBEditor tce = 
                    (AbstractJBEditor)activePart;
            tce.getEditorHelper().doEditorOperation(new IEditorOperation() {
                
                public void run(IPersistentObject workingPo) {
                    INodePO workTC = (INodePO)workingPo;
                    IStructuredSelection selection = getSelection();
                    for (Object selectedNode : selection.toArray()) {
                        deleteComment(workTC, (ICommentPO)selectedNode, tce);
                    }
                }
            });
        }
        return null;
    }
    
    /**
     * Deletes a comment in the given node and sets the editor dirty
     * 
     * @param workTC
     *            the workversion of the Node
     * @param comment
     *            the comment to delete
     * @param editor
     *            the editor.
     */
    private void deleteComment(INodePO workTC, ICommentPO comment,
            AbstractJBEditor editor) {
        workTC.removeNode(comment);
        editor.getEditorHelper().setDirty(true);
        DataEventDispatcher.getInstance().fireDataChangedListener(comment, 
                DataState.Deleted, UpdateState.onlyInEditor);
    }
}

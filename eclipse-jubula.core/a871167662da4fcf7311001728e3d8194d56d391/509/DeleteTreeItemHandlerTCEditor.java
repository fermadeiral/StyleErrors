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
package org.eclipse.jubula.client.ui.rcp.handlers.delete;

import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.CalcTypes;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public class DeleteTreeItemHandlerTCEditor 
        extends AbstractDeleteTreeItemHandler {
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked") 
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        
        if (activePart instanceof AbstractJBEditor) {
            final AbstractJBEditor tce = (AbstractJBEditor)activePart;
            tce.getEditorHelper().doEditorOperation(new IEditorOperation() {
                public void run(IPersistentObject workingPo) {
                    IStructuredSelection structuredSelection = getSelection();
                    if (confirmDelete(structuredSelection)) {
                        deleteNodesFromEditor(
                                structuredSelection.toList(), tce);
                    }
                }
            });

        }
        return null;
    }
    
    /**
     * @param nodes
     *            the nodes to delete
     * @param editor
     *            the editor to perform the deletion for
     */
    public static void deleteNodesFromEditor(List<? extends INodePO> nodes,
            AbstractJBEditor editor) {
        editor.getEditorHelper().getClipboard().clearContents();
        EditSupport supp = editor.getEditorHelper().getEditSupport();
        IWritableComponentNameCache localCache = supp.getCache();
        for (INodePO node : nodes) {
            deleteNode(node, editor.getEntityManager());
        }

        editor.getEditorHelper().setDirty(true);
        CalcTypes calc = new CalcTypes(localCache,
                (INodePO) supp.getWorkVersion());
        calc.calculateTypes();
        localCache.storeLocalProblems(calc);
    }
    
    /**
     * Deletes children of a node then the node itself
     * @param node the node
     * @param sess the session
     */
    private static void deleteNode(INodePO node, EntityManager sess) {
        node.getParentNode().removeNode(node);
        if (node.getId() != null) {
            sess.remove(node);
        }
        DataEventDispatcher.getInstance().fireDataChangedListener(node,
                DataState.Deleted, UpdateState.onlyInEditor);
    }
}

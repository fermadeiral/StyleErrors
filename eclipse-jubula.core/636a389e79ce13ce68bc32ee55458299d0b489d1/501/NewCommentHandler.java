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
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.dialogs.NewCommentDialog;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 22.10.2015
 */
public class NewCommentHandler extends AbstractSelectionBasedHandler {
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
                    IStructuredSelection selection = getSelection();
                    INodePO selected = (INodePO)selection.getFirstElement();
                    INodePO target = selected;
                    if (selected != null) {
                        while (!(target instanceof ISpecTestCasePO
                                || target instanceof IAbstractContainerPO
                                || target instanceof ITestSuitePO
                                || target instanceof ITestJobPO
                                || target == null)) {
                            target = selected.getParentNode();
                        }
                    } else {
                        target = (INodePO)workingPo;
                    }
                    if (target == null) {
                        return;
                    }
                    int posistionToAdd = target == selected
                            ? 0 : target.indexOf(selected);
                    addComment(target, posistionToAdd, tce);
                }
            });
        }
        return null;
    }
    
    /**
     * Adds a new comment to the given SpecTestCase at the given
     * position and, if successful, sets the given editor dirty.
     * 
     * @param workTC
     *            the workversion of the SpecTestCase
     * @param position
     *            the position to add
     * @param editor
     *            the editor.
     */
    private void addComment(INodePO workTC, 
        Integer position, AbstractJBEditor editor) {

        final NewCommentDialog dialog = new NewCommentDialog(getActiveShell());
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        if (dialog.getReturnCode() != Window.OK) {
            return;
        }
        
        ICommentPO comment = NodeMaker.createCommentPO(dialog.getCommentText());
        workTC.addNode(position, comment);
        editor.getEditorHelper().setDirty(true);
        DataEventDispatcher.getInstance().fireDataChangedListener(comment, 
                DataState.Added, UpdateState.onlyInEditor);
    }
}

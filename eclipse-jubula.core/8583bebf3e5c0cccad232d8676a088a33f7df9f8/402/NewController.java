/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
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
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.utils.NodeTargetCalculator.NodeTarget;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 
 * @author BREDEX GmbH
 *
 */
public class NewController extends AbstractSelectionBasedHandler {

    @Override
    protected Object executeImpl(final ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof AbstractTestCaseEditor) {
            final AbstractTestCaseEditor tce = 
                    (AbstractTestCaseEditor)activePart;
            tce.getEditorHelper().doEditorOperation(new IEditorOperation() {
                
                public void run(IPersistentObject workingPo) {
                    INodePO selected = (INodePO) getSelection().
                            getFirstElement();
                    
                    NodeTarget place = NewTestCaseHandlerTCEditor.
                            getPositionToInsert(selected, tce.getTreeViewer().
                                    getExpandedState(selected));
                    if (place == null) {
                        return;
                    }
                    addStatement(event.getCommand().getId(), place.getNode(),
                            place.getPos(), tce);
                }
            });
        }
        return null;
    }
    
    /**
     * @param comId the command Id
     * @param target node where the condition statement will be
     * @param position selected position
     * @param tce the used editor
     */
    private void addStatement(String comId, INodePO target,
            int position, AbstractTestCaseEditor tce) {

        INodePO toAdd = null;
        
        switch (comId) {
            case RCPCommandIDs.NEW_CONDITIONAL_STATEMENT:
                toAdd = NodeMaker.createConditionalStatementPO();
                break;
            case RCPCommandIDs.NEW_DO_WHILE:
                toAdd = NodeMaker.createDoWhilePO();
                break;
            case RCPCommandIDs.NEW_WHILE_DO:
                toAdd = NodeMaker.createWhileDoPO();
                break;
            case RCPCommandIDs.NEW_ITERATE_LOOP:
                toAdd = NodeMaker.createIteratePO();
                break;
            default:
                throw new UnsupportedOperationException("Unknown command ID"); //$NON-NLS-1$
        }
        
        target.addNode(position, toAdd);
        tce.getEditorHelper().setDirty(true);
        DataEventDispatcher.getInstance().fireDataChangedListener(toAdd,
                DataState.Added, UpdateState.onlyInEditor);
        tce.getTreeViewer().expandToLevel(target, 1);
    }

}

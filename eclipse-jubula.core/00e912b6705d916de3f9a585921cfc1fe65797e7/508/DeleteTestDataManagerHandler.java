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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.ITestDataNodePO;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Jun 29, 2010
 */
public class DeleteTestDataManagerHandler 
    extends AbstractDeleteTreeItemHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof CentralTestDataEditor) {
            IStructuredSelection structuredSelection = getSelection();
            CentralTestDataEditor editor = (CentralTestDataEditor)activePart;
            if (editor.getEditorHelper().requestEditableState() 
                    != EditableState.OK) {
                return null;
            }

            if (confirmDelete(structuredSelection)) {
                deleteSelection(editor, structuredSelection);
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    protected String getName(Object obj) {
        if (obj instanceof ITestDataNodePO) {
            return ((ITestDataNodePO)obj).getName();
        }
        return super.getName(obj);
    }

    /**
     * @param editor
     *            the CentralTestDataEditor
     * @param structuredSelection
     *            the selected elements to delete
     */
    private void deleteSelection(CentralTestDataEditor editor,
            IStructuredSelection structuredSelection) {
        List<String> reusedCubeList = new ArrayList<String>(0);
        Set<ITestDataCubePO> toCheck = new HashSet<ITestDataCubePO>();
        computeCubesToCheck(structuredSelection.toArray(), toCheck);
        
        for (ITestDataCubePO td : toCheck) {
            if (TestDataCubeBP.isCubeReused(td)) {
                reusedCubeList.add(td.getName());
            }
        }

        if (reusedCubeList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(StringConstants.NEWLINE);
            for (String s : reusedCubeList) {
                sb.append(StringConstants.SPACE);
                sb.append(StringConstants.MINUS);
                sb.append(StringConstants.SPACE);
                sb.append(s);
                sb.append(StringConstants.NEWLINE);
            }
            ErrorHandlingUtil.createMessageDialog(MessageIDs.I_REUSED_TDC,
                    new Object[] { sb.toString() }, null);
        } else {
            for (Object toDelete : structuredSelection.toArray()) {
                if (toDelete instanceof ITestDataNodePO) {
                    ITestDataNodePO node = (ITestDataNodePO)toDelete;
                    editor.getEditorHelper().getEditSupport().getSession()
                        .remove(node);
                    editor.getEditorHelper().setDirty(true);
                    
                    // Necessary for proper UI updating. Otherwise, the deleted 
                    // child is still considered to belong to the parent until
                    // the change is committed to the DB and new working objects
                    // are created.
                    ITestDataCategoryPO parent = node.getParent();
                    if (parent != null) {
                        parent.removeNode(node);
                    }
                    
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                            node, DataState.Deleted, UpdateState.onlyInEditor);
                }
            }
        }
    }
    /**
     * Adds all Central Test Data instances (recursively) contained in the
     * given array of objects to the given collection. 
     * 
     * @param selectedObjects The initial objects to analyze.
     * @param toCheck The collection of Central Test Data to be filled or added
     *                to by this computation. 
     */
    private void computeCubesToCheck(
            Object[] selectedObjects, Set<ITestDataCubePO> toCheck) {

        for (Object selected : selectedObjects) {
            if (selected instanceof ITestDataCubePO) {
                toCheck.add((ITestDataCubePO)selected);
            } else if (selected instanceof ITestDataCategoryPO) {
                ITestDataCategoryPO category = (ITestDataCategoryPO)selected;
                computeCubesToCheck(
                        category.getCategoryChildren().toArray(), 
                        toCheck);
                toCheck.addAll(category.getTestDataChildren());
            }
        }
    }

}

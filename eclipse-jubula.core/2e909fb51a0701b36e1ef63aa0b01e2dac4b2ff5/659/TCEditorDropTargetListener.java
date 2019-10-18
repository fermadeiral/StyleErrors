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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.swt.dnd.TransferData;


/**
 * {@inheritDoc}
 *
 * @author BREDEX GmbH
 * @created 12.05.2005
 */
public class TCEditorDropTargetListener extends AbstractNodeViewerDropAdapter {
    /** <code>m_editor</code> */
    private AbstractTestCaseEditor m_editor;
    
    
    /**
     * @param editor the TestCaseEditor.
     */
    public TCEditorDropTargetListener(AbstractTestCaseEditor editor) {
        super(editor.getTreeViewer());
        m_editor = editor;
        boolean scrollExpand = Plugin.getDefault().getPreferenceStore().
            getBoolean(Constants.TREEAUTOSCROLL_KEY);
        setScrollExpandEnabled(scrollExpand);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean performDrop(Object data) {
        LocalSelectionTransfer transfer = LocalSelectionTransfer.getInstance();
        IStructuredSelection selection = transfer.getSelection();
        Object target = getCurrentTarget();
        if (selection instanceof StructuredSelection) {
            int location = getCurrentLocation();
            if (target == null) {
                target = getFallbackTarget(getViewer());
                location = ViewerDropAdapter.LOCATION_AFTER;
            }
            if (target instanceof INodePO) {
                boolean succ = TCEditorDndSupport.performDrop(m_editor,
                        selection, (INodePO)target, location);
                logDrop(selection, (INodePO) target, succ);
                return succ;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean validateDrop(Object target, int operation,
        TransferData transferType) {
        
        LocalSelectionTransfer transfer = LocalSelectionTransfer.getInstance();
        IStructuredSelection selection = transfer.getSelection();
        if (selection instanceof StructuredSelection) {
            Object targetObject = target;
            if (targetObject == null) {
                targetObject = m_editor.getEditorHelper()
                    .getEditSupport().getWorkVersion();
            }
            if (targetObject instanceof INodePO) {
                return TCEditorDndSupport.validateDrop(transfer.getSource(),
                        getViewer(), selection, (INodePO)targetObject, true);
            }
        }
        return false;
    }
}
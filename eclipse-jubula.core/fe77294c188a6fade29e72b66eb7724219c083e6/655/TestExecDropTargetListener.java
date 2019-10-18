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

import java.util.List;

import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.views.TestSuiteBrowser;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author BREDEX GmbH
 * @created 19.10.2011
 */
public class TestExecDropTargetListener extends ViewerDropAdapter {

    /**
     * @param view the depending view.
     */
    public TestExecDropTargetListener(TestSuiteBrowser view) {
        super(view.getTreeViewer());
        boolean scrollExpand = Plugin.getDefault().getPreferenceStore().
            getBoolean(Constants.TREEAUTOSCROLL_KEY);
        setScrollExpandEnabled(scrollExpand);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean performDrop(Object data) {
        DropTargetEvent ev = getCurrentEvent();
        if (FileTransfer.getInstance().isSupportedType(ev.currentDataType)
                && ev.data instanceof String[]) {
            DropFileOperation.dropFiles((String[]) ev.data);
            return true;
        }
        LocalSelectionTransfer transfer = LocalSelectionTransfer.getInstance();
        IPersistentObject target = (IPersistentObject)getCurrentTarget();
        List <INodePO> toMove = transfer.getSelection().toList();
        boolean succ = TSBrowserDndSupport.moveNodes(toMove, target);
        TestSpecDropTargetListener.logDrop("Test Suite Browser", //$NON-NLS-1$
                toMove, target, succ);
        if (!succ) {
            return false;
        }
        transfer.setSelection(null);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean validateDrop(Object target, int operation,
        TransferData transferType) {
        if (FileTransfer.getInstance().isSupportedType(
                getCurrentEvent().currentDataType)) {
            return true;
        }
        if (LocalSelectionTransfer.getInstance().getSelection() == null) {
            return false;
        }
        if (getCurrentLocation() == LOCATION_BEFORE
            || getCurrentLocation() == LOCATION_AFTER) {
            return false;
        }
        LocalSelectionTransfer transfer = LocalSelectionTransfer.getInstance();
        if (transfer.getSource() != null 
            && !transfer.getSource().equals(getViewer())) {
            return false;
        }

        return TSBrowserDndSupport.canMove(transfer.getSelection(), target);
    }

    /**
     * no expand of ISpecTestCasePO nodes
     * {@inheritDoc}
     */
    public void dragOver(DropTargetEvent event) {
        super.dragOver(event);
        if (event.item != null
            && event.item.getData() instanceof ITestSuitePO
            && event.item.getData() instanceof ITestJobPO) {
            event.feedback &= ~DND.FEEDBACK_EXPAND;
        }
        if (getCurrentLocation() == LOCATION_BEFORE
            || getCurrentLocation() == LOCATION_AFTER) {
            event.feedback &= ~DND.FEEDBACK_EXPAND;
        }
    }
}
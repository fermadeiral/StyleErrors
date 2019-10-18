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
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 06.07.2005
 */
public class TestSpecDropTargetListener extends ViewerDropAdapter {

    /** The log */
    private static final Logger LOG = LoggerFactory.getLogger(
            TestSpecDropTargetListener.class);
    
    /**
     * @param view the depending view.
     */
    public TestSpecDropTargetListener(TestCaseBrowser view) {
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
        LocalSelectionTransfer transfer = LocalSelectionTransfer.getInstance();
        IPersistentObject target = (IPersistentObject)getCurrentTarget();
        List <INodePO> nodesToBeMoved = transfer.getSelection().toList();
        boolean succ = TCBrowserDndSupport.moveNodes(nodesToBeMoved, target);
        logDrop("Test Case Browser", nodesToBeMoved, target, succ); //$NON-NLS-1$
        if (!succ) {
            return false;
        }
        LocalSelectionTransfer.getInstance().setSelection(null);
        return true;
    }
    
    /**
     * @param browser the browser
     * @param toMove to move
     * @param target target
     * @param succ whether drop was successful
     */
    @SuppressWarnings("nls")
    public static void logDrop(String browser, List<INodePO> toMove,
            IPersistentObject target, boolean succ) {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        StringBuilder str = new StringBuilder();
        str.append("\nDropping nodes in the ");
        str.append(browser);
        str.append(".\n The nodes:\n");
        str.append(toMove.toString());
        str.append("\nThe target: ");
        str.append(target.getName());
        str.append("\nSuccess: ");
        str.append(succ);
        str.append("\n");
        LOG.debug(str.toString());
    }

    /**
     * {@inheritDoc}
     */
    public boolean validateDrop(Object target, int operation,
        TransferData transferType) {
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

        return TCBrowserDndSupport.canMove(transfer.getSelection(), target);
    }

    /**
     * no expand of ISpecTestCasePO nodes
     * {@inheritDoc}
     */
    public void dragOver(DropTargetEvent event) {
        super.dragOver(event);
        if (event.item != null
            && event.item.getData() instanceof ISpecTestCasePO) {
            event.feedback &= ~DND.FEEDBACK_EXPAND;
        }
        if (getCurrentLocation() == LOCATION_BEFORE
            || getCurrentLocation() == LOCATION_AFTER) {
            event.feedback &= ~DND.FEEDBACK_EXPAND;
        }
    }
}
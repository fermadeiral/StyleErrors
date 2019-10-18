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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.model.INodePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created Sep 16, 2010
 */
public abstract class AbstractNodeViewerDropAdapter extends ViewerDropAdapter {
    
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger(
            AbstractNodeViewerDropAdapter.class);
    
    /**
     * @param viewer
     *            the viewer
     */
    protected AbstractNodeViewerDropAdapter(Viewer viewer) {
        super(viewer);
    }

    /**
     * @param viewer
     *            the target viewer
     * @return the fallback target for dropping
     */
    protected Object getFallbackTarget(Viewer viewer) {
        if (viewer != null) {
            Object fallbackTarget = null;
            Object viewerInput = ((Object[])viewer.getInput())[0];
            if (viewerInput instanceof INodePO) {
                List<INodePO> viewerRootChildren = ((INodePO) viewerInput)
                        .getUnmodifiableNodeList();
                int childrenCount = 1;
                if (viewerRootChildren != null) {
                    childrenCount = viewerRootChildren.size();
                }
                if (childrenCount > 0) {
                    fallbackTarget = viewerRootChildren.get(childrenCount - 1);
                } else {
                    fallbackTarget = viewerInput;
                }
            }
            return fallbackTarget;
        }
        return null;
    }
    
    /**
     * Logs the drop
     * @param sel the selection
     * @param target the target
     * @param succ whether successful
     */
    @SuppressWarnings("nls")
    void logDrop(IStructuredSelection sel, INodePO target, boolean succ) {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        INodePO par = target.getSpecAncestor();
        StringBuilder str = new StringBuilder();
        str.append("\nDropping in an Editor.\nThe root node is: ");
        str.append(par.toString());
        str.append("\nThe drop target is: ");
        str.append(target.toString());
        str.append("\nThe dropped nodes are:\n");
        str.append(sel.toList().toString());
        str.append("\nSuccess: ");
        str.append(succ);
        str.append("\n");
        LOG.debug(str.toString());
    }
}

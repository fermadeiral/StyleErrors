/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.ui.controllers.TreeViewContainerGUIController;
import org.eclipse.jubula.client.ui.views.IMultiTreeViewerContainer;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created 05.04.2017
 */
public class CollapseBranchHandler extends AbstractHandler {

    /** {@inheritDoc} */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        TreeViewer activeTreeViewer = null;
        if (part instanceof IMultiTreeViewerContainer) {
            activeTreeViewer = ((IMultiTreeViewerContainer) part)
                    .getActiveTreeViewer();
        } else if (part instanceof ITreeViewerContainer) {
            activeTreeViewer = ((ITreeViewerContainer) part).getTreeViewer();
        }

        TreeViewContainerGUIController.collapseExpandTree(activeTreeViewer);

        return null;
    }

}

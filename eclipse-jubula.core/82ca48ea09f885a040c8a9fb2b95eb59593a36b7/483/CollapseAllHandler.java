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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.rcp.controllers.TreeViewContainerGUIController;
import org.eclipse.jubula.client.ui.rcp.views.ComponentNameBrowser;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Default Handler for all CollapseAll Commands in Trees
 *
 * @author BREDEX GmbH
 * @created 12.02.2009
 */
public class CollapseAllHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        
        if (activePart instanceof ITreeViewerContainer) {
            TreeViewContainerGUIController.collapseExpandTree(
                (ITreeViewerContainer)activePart);
        }
        
        if (activePart instanceof ComponentNameBrowser) {
            ComponentNameBrowser cnb = ((ComponentNameBrowser)activePart);
            try {
                cnb.getTreeViewer().getTree().setRedraw(false);
                cnb.getTreeViewer().collapseAll();
            } finally {
                cnb.getTreeViewer().getTree().setRedraw(true);
            }
        }
        
        return null;
    }

}

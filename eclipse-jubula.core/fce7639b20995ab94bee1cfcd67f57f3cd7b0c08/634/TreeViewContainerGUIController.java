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
package org.eclipse.jubula.client.ui.rcp.controllers;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;


/**
 * This class contains some general actions, that can be
 * executed on a TreeViewContainer like expanding etc.
 *
 * @author BREDEX GmbH
 * @created Feb 28, 2006
 *
 */
public class TreeViewContainerGUIController {

    /**
     * utility class does not have constructor
     */
    private TreeViewContainerGUIController() {
        // private
    }

    /**
     * collapse or expand the give TreeViewer
     * 
     * @param treeCont
     *            ITreeViewerContainer
     */
    public static void collapseExpandTree(ITreeViewerContainer treeCont) {
        TreeViewer tv = treeCont.getTreeViewer();
        try {
            tv.getTree().setRedraw(false);
            tv.collapseAll();
            int autoExpandLevel = tv.getAutoExpandLevel();
            tv.expandToLevel(autoExpandLevel);
        } finally {
            tv.getTree().setRedraw(true);
        }
    }
    
    /**
     * expand a subtree
     * 
     * @param tv
     *            the tree viewer to use; may be <code>null</code>
     */
    public static void expandSubTree(TreeViewer tv) {
        if (tv == null) {
            return;
        }
        if (tv.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) tv
                    .getSelection();
            for (Object obj : selection.toArray()) {
                tv.expandToLevel(obj, 2);
            }
        }
    }

}
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
package org.eclipse.jubula.client.ui.controllers;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;


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
     * @param tv tree viewer
     */
    public static void collapseExpandTree(TreeViewer tv) {
        if (tv != null && tv.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) tv
                    .getSelection();
            for (Object obj : selection.toArray()) {
                tv.collapseToLevel(obj, AbstractTreeViewer.ALL_LEVELS);
            }
        }
    }
    
    /**
     * expand a subtree
     * 
     * @param tv
     *            the tree viewer to use; may be <code>null</code>
     */
    public static void expandSubTree(TreeViewer tv) {
        if (tv != null && tv.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) tv
                    .getSelection();
            for (Object obj : selection.toArray()) {
                tv.expandToLevel(obj, 1);
            }
        }
    }
    
    /**
     * Expands the selected branch
     * @param tv the TreeViewer to use
     */
    public static void expandBranch(TreeViewer tv) {
        if (tv != null && tv.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = 
                    (IStructuredSelection) tv.getSelection();
            for (Object obj: selection.toArray()) {
                tv.expandToLevel(obj, 10);
            }
        }
    }

}
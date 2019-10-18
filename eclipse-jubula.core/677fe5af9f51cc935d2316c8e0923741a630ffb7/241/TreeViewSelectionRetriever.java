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
package org.eclipse.jubula.client.ui.rcp.search.data;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.ui.rcp.views.AbstractJBTreeView;
import org.eclipse.swt.widgets.Display;

/**
 * This class is used to retrieve the {@link IStructuredSelection}
 * of an {@link AbstractJBTreeView} from outside of the GUI thread.
 *
 * @author BREDEX GmbH
 * @created April 23, 2013
 */
public class TreeViewSelectionRetriever implements Runnable {

    /** The abstract tree view. */
    private AbstractJBTreeView m_treeView;

    /** The selection of the tree view. */
    private ISelection m_selection;

    /**
     * @param treeView The tree view retrieving selection from.
     */
    private TreeViewSelectionRetriever(AbstractJBTreeView treeView) {
        m_treeView = treeView;
    }

    /**
     * @return The structured selection of the tree view, or null,
     *         if not available.
     */
    private IStructuredSelection getStructuredSelection() {
        // implicitly call run() and wait until finished
        Display.getDefault().syncExec(this);
        if (m_selection instanceof IStructuredSelection) {
            return (IStructuredSelection) m_selection;
        }
        return null;
    }

    /**
     * Executed by the GUI thread to retrieve the selection.
     */
    public void run() {
        if (m_treeView != null) {
            m_selection = m_treeView.getSelection();
        }
    }

    /**
     * @param treeView The tree view.
     * @return The structured selection from the Test Suite Browser or null,
     *         if not available.
     */
    public static IStructuredSelection getStructuredSelection(
            AbstractJBTreeView treeView) {
        TreeViewSelectionRetriever selectionRetriever =
                new TreeViewSelectionRetriever(treeView);
        return selectionRetriever.getStructuredSelection();
    }

}

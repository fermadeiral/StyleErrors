/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.bridge.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.mylyn.internal.context.ui.BrowseFilteredListener;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action to provide the 'show filtered children' function to the Test Case
 * Browser and the Test Suite Browser menu
 * 
 * @author BREDEX GmbH
 * @created Nov 16, 2010
 */
public class ShowFilteredChildrenAction implements IObjectActionDelegate,
        IViewActionDelegate {
    /**
     * the listener
     */
    private BrowseFilteredListener m_browseFilteredListener;

    /**
     * the tree viewer
     */
    private TreeViewer m_treeViewer;

    /**
     * the selection
     */
    private IStructuredSelection m_selection;

    /**
     * Constructor
     */
    public ShowFilteredChildrenAction() { 
        // default constuctor 
    }

    /** {@inheritDoc} */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        if (targetPart instanceof ITreeViewerContainer) {
            m_treeViewer = ((ITreeViewerContainer)targetPart).getTreeViewer();
            m_browseFilteredListener = new BrowseFilteredListener(m_treeViewer);
        }
    }

    /** {@inheritDoc} */
    public void run(IAction action) {
        if (m_selection != null) {
            m_browseFilteredListener.unfilterSelection(m_treeViewer,
                    m_selection);
        }
    }

    /** {@inheritDoc} */
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.m_selection = (IStructuredSelection)selection;
        }
    }

    /** {@inheritDoc} */
    public void init(IViewPart view) {
        if (view instanceof ITreeViewerContainer) {
            m_treeViewer = ((ITreeViewerContainer)view).getTreeViewer();
            m_browseFilteredListener = new BrowseFilteredListener(m_treeViewer);
        }
    }

}

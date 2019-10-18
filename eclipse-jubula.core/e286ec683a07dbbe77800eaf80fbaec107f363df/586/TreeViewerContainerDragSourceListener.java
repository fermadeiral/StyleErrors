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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

/**
 * {@inheritDoc}
 *
 * @author BREDEX GmbH
 * @created 12.05.2005
 */
public class TreeViewerContainerDragSourceListener extends DragSourceAdapter {

    /**
     * the viewer
     */
    private TreeViewer m_viewer;
    
    /**
     * @param viewer The viewer.
     */
    public TreeViewerContainerDragSourceListener(TreeViewer viewer) {
        m_viewer = viewer;
    }
    
    /**
     * {@inheritDoc}
     */
    public void dragStart(DragSourceEvent event) {
        IStructuredSelection selection = 
            (IStructuredSelection)m_viewer.getSelection();
        LocalSelectionTransfer.getInstance().setSelection(selection);
        LocalSelectionTransfer.getInstance().setSource(m_viewer);
        event.data = new String("local-selection-transfer-format".getBytes()); //$NON-NLS-1$
    }
    
    /**
     * {@inheritDoc}
     */
    public void dragFinished(DragSourceEvent event) {
        if (LocalSelectionTransfer.getInstance().getSelection() == null) {
            Object [] expandedElements = 
                m_viewer.getExpandedElements();
            m_viewer.refresh();
            m_viewer.setExpandedElements(expandedElements);
        }
    }
}

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
package org.eclipse.jubula.examples.aut.dvdtool.control;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.eclipse.jubula.examples.aut.dvdtool.model.DvdCategory;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdDataObject;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdTransferableCategory;


/**
 * A drag source wrapper for a JTree. This class can be used to control 
 * a rearrangeable tree.
 * The drag gestures MOVE and COPY are both handled with the 'drag and drop' 
 * action MOVE.
 *
 * @author BREDEX GmbH
 * @created 05.02.2008
 */
public class DvdTreeDragSource implements DragSourceListener, 
                                          DragGestureListener {

    /** the supported action type, both are treated as MOVE */
    private static final int ACTION_TYPE = DnDConstants.ACTION_COPY_OR_MOVE;
    
    /** the tree to be controlled */
    private final JTree m_tree;
    /** the corresponding DragSource */
    private final DragSource m_source;

    /** the object to be transferred */
    private DvdTransferableCategory m_transferable;
    /** the node to be dragged */
    private DefaultMutableTreeNode m_draggedNode;

    /**
     * public constructor
     * @param tree The tree to be controlled
     */
    public DvdTreeDragSource(JTree tree) {
        m_tree = tree;
        m_source = new DragSource();
        m_source.createDefaultDragGestureRecognizer(m_tree, ACTION_TYPE, this);
    }

    /**
     * {@inheritDoc}
     */
    public void dragGestureRecognized(DragGestureEvent dge) {
        TreePath path = m_tree.getLeadSelectionPath();
        if ((path == null) || (path.getPathCount() <= 1)) {
            // We cannot move the root node or an empty selection
            return;
        }
        m_draggedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        m_transferable = new DvdTransferableCategory(path);
        m_source.startDrag(dge, DragSource.DefaultMoveDrop, 
                m_transferable, this);
    }

    /**
     * {@inheritDoc}
     */
    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (dsde.getDropSuccess()) {
            ((DefaultTreeModel) m_tree.getModel())
                .removeNodeFromParent(m_draggedNode);            
            DvdDataObject dataObject = 
                (DvdDataObject) m_draggedNode.getUserObject();
            DvdCategory toRemove = dataObject.getCategory();

            if (toRemove.getParent() != null) {
                toRemove.getParent().remove(toRemove);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void dragEnter(DragSourceDragEvent dsde) {
        // no action
    }

    /**
     * {@inheritDoc}
     */
    public void dragExit(DragSourceEvent dse) {
        // no action
    }

    /**
     * {@inheritDoc}
     */
    public void dragOver(DragSourceDragEvent dsde) {
        // no action
    }

    /**
     * {@inheritDoc}
     */
    public void dropActionChanged(DragSourceDragEvent dsde) {
        // no action
    }

}

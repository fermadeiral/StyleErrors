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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.eclipse.jubula.examples.aut.dvdtool.model.DvdCategory;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdDataObject;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdTransferableCategory;


/**
 * A drop target wrapper for a JTree. This class can be used to control 
 * a rearrangeable tree.
 * The operation MOVE is the only supported 'drag and drop' action type.
 *
 * @author BREDEX GmbH
 * @created 05.02.2008
 */
public class DvdTreeDropTarget implements DropTargetListener {

    /** the supported action type */
    private static final int ACTION_TYPE = DnDConstants.ACTION_MOVE;
    
    /**
     * public constructor
     * @param tree The tree to be controlled
     */
    public DvdTreeDropTarget(JTree tree) {
        new DropTarget(tree, this);
    }

    /**
     * {@inheritDoc}
     */
    public void drop(DropTargetDropEvent dtde) {
        Point pt = dtde.getLocation();
        DropTargetContext dtc = dtde.getDropTargetContext();
        JTree tree = (JTree) dtc.getComponent();
        TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
        DefaultMutableTreeNode newParent = (DefaultMutableTreeNode) parentpath
            .getLastPathComponent();
        try {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (tr.isDataFlavorSupported(flavors[i])) {
                    DvdTransferableCategory transferableTreePath = 
                        (DvdTransferableCategory)tr.getTransferData(flavors[i]);
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    DvdCategory draggedNodeCategory = 
                            transferableTreePath.getCategory();
                    int draggedNodeHashCode = 
                        transferableTreePath.getNodeHashCode();
                    
                    // create dragged node and all subnodes of it using 
                    // the transferred data in 'draggedNodeCategory'
                    DefaultMutableTreeNode draggedNode = 
                        createNodeWithSubTreeForCategory(model, 
                                draggedNodeCategory);
                    
                    // check, whether the drop operation is allowed
                    // (dropping a node to itself or any subnode is forbidden)
                    DefaultMutableTreeNode nodeToCheck = newParent;
                    do {
                        if (isNodeEqualToDraggedNode(nodeToCheck, draggedNode,
                                draggedNodeHashCode)) {

                            dtde.rejectDrop();
                            return;
                        }
                        // check parent node next
                        nodeToCheck = 
                            (DefaultMutableTreeNode) nodeToCheck.getParent();
                        
                    } while (nodeToCheck != null);                       
                        
                    dtde.acceptDrop(ACTION_TYPE);
                    
                    // add to data model of new parent 
                    DvdDataObject newParentData = 
                        (DvdDataObject) newParent.getUserObject();
                    DvdCategory newParentCategory = 
                        newParentData.getCategory();
                    newParentCategory.insert(draggedNodeCategory);

                     // add to new parent node as last child
                    model.insertNodeInto(draggedNode, newParent, 
                            newParent.getChildCount());
                    
                    // select dragged node
                    tree.setSelectionPath(new TreePath(draggedNode.getPath()));

                    dtde.dropComplete(true);
                    return;
                }
            }
            dtde.rejectDrop();
        } catch (Exception e) {
            dtde.rejectDrop();
        }
    }

    /**
     * Creates a node and all needed subnodes for the given DvdCategory.
     * The given tree model is updated.
     * 
     * @param category The data to be used for node creation
     * @param model The tree model to be updated
     * @return the created node containing the given data
     */
    private DefaultMutableTreeNode createNodeWithSubTreeForCategory(
            DefaultTreeModel model,
            DvdCategory category) {
        
        DefaultMutableTreeNode node = 
            new DefaultMutableTreeNode(new DvdDataObject(category));
        
        // create all sub nodes
        for (int i = 0; i < category.getCategories().size(); i++) {
            DvdCategory childCategory = 
                (DvdCategory) category.getCategories().get(i);
            
            // do recursive call
            DefaultMutableTreeNode childNode = 
                createNodeWithSubTreeForCategory(model, childCategory);

            // add to new child node to new node
            model.insertNodeInto(childNode, node, node.getChildCount());
        }
        return node;
    }
   
    /**
     * Compares two nodes using the string representation and the hash codes.
     * Nodes are equal only if both kinds of values are equal.
     * (The draggedNode might be newly created after deserialization, so its
     * original hash code must be given and cannot be retrieved from the node.)
     * 
     * @param node The node to be compared to 'draggedNode'
     * @param draggedNode The node to be compared to 'node'
     * @param draggedNodeHashCode The original hash code of 'draggedNode'
     *      before it has been serialized
     * @return boolean indicating whether both DefaultMutableTreeNodes are equal
     */
    private boolean isNodeEqualToDraggedNode(DefaultMutableTreeNode node, 
                                             DefaultMutableTreeNode draggedNode,
                                             int draggedNodeHashCode) {
        // compare string representation of the nodes
        if (node.toString().equals(draggedNode.toString())) {            
            // compare hash code of the nodes
            return (node.hashCode() == draggedNodeHashCode);
        } 

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(ACTION_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    public void dragOver(DropTargetDragEvent dtde) {
        dtde.acceptDrag(ACTION_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    public void dragExit(DropTargetEvent dte) {
        // no action
    }

    /**
     * {@inheritDoc}
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // no action
    }

}

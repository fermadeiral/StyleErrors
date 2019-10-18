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

import java.util.Iterator;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.jubula.examples.aut.dvdtool.DevelopmentState;
import org.eclipse.jubula.examples.aut.dvdtool.gui.Constants;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdMainFrame;
import org.eclipse.jubula.examples.aut.dvdtool.model.Dvd;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdCategory;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdDataObject;


/**
 * This is the controller class for the tree.
 * 
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DvdTreeController {
    /** the main frame contoller, holding the actions, etc. */
    private DvdMainFrameController m_controller;

    /** the tree selection listener */
    private TreeSelectionListener m_treeSelectionListener = 
        new MyTreeSelectionListener();

    /**
     * public constructor, initialises this controller
     * 
     * @param mainFrameController
     *            the controller of the main frame
     */
    public DvdTreeController(DvdMainFrameController mainFrameController) {
        m_controller = mainFrameController;

        init();
    }

    /**
     * build up the tree from a DvdCategory
     * 
     * @param root
     *            a dvd cytegory representing the root of the tree
     */
    public void createTree(DvdCategory root) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
                new DvdDataObject(root)); 

        recCreateTree(rootNode, root);
        
        // set the model
        removeTreeSelectionListener();
        JTree tree = getTree();
        tree.setModel(new DefaultTreeModel(rootNode));

        addTreeSelectionListener();
        tree.setSelectionRow(0);
    }

    /**
     * adds a <code>dvd</code> to the currently selected category, updates the model
     * 
     * @param dvd
     *            the dvd to add
     */
    public void addDvd(Dvd dvd) {
        TreePath selectionPath = getTree().getSelectionModel()
            .getLeadSelectionPath();

        if (selectionPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath
                .getLastPathComponent();

            // update DvdCategory
            DvdDataObject data = (DvdDataObject) node.getUserObject();
            data.addDvd(dvd);
            
            m_controller.setChanged(true);
            
            // update the table model
            m_controller.getDvdMainFrame().getTable().setModel(
                    data.getTableModel());
            m_controller.getDvdMainFrame().showCard(DvdMainFrame.CARD_DATA);
        }
        
        JTable table = m_controller.getDvdMainFrame().getTable();
        table.requestFocus();
        table.setEditingColumn(0);
        table.setEditingRow(table.getRowCount() - 1);
        int row = table.getEditingRow();
        table.setColumnSelectionInterval(0, 0);
        table.setRowSelectionInterval(row, row);
    }

    
    /**
     * removes <code>dvd</code> from the current selected category
     * @param dvd the dvd to remove
     */
    public void removeDvd(Dvd dvd) {
        TreePath selectionPath = getTree().getSelectionModel()
            .getLeadSelectionPath();

        if (selectionPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath
                .getLastPathComponent();

            // update DvdCategory
            DvdDataObject data = (DvdDataObject) node.getUserObject();
            data.removeDvd(dvd);
            
            m_controller.setChanged(true);
            
            // update the table model
            updateTable(data);
            
            showCard(data);
            m_controller.getDvdMainFrame().getTreePanel().getTree().repaint();
        }
    }
    
    /**
     * sets the model from <code>data</code> to the table
     * 
     * @param data
     *            the DvdDataObject containing the tablemodel
     */
    private void updateTable(DvdDataObject data) {
        m_controller.getDvdMainFrame().getTable().setModel(
                data.getTableModel());        
        boolean categoryEnabled = isCurrentCategoryEnabled();
        m_controller.getTableController().setAllDvdsEnableState(
                categoryEnabled);
    }
    
    
    /**
     * determine the and shows the card depending on <code>data</code>
     * 
     * @param data
     *            the DvdDataObject
     */
    private void showCard (DvdDataObject data) {
        if (data.hasDvds()) {
            m_controller.getDvdMainFrame().showCard(
                    DvdMainFrame.CARD_DATA);
        } else {
            m_controller.getDvdMainFrame().showCard(
                    DvdMainFrame.CARD_LOGO);
        }
    }

    /**
     * creates and adds a new category with the <code>name</code> to each 
     * selected category, updates the model
     * 
     * @param name 
     *            the name for the category to create and add
     */
    public void addNewCategoryToSelectedCategories(String name) {
        TreePath[] selectionPathArray = getTree().getSelectionModel()
            .getSelectionPaths();
        
        for (int i = 0; i < selectionPathArray.length; i++) {
            TreePath path = selectionPathArray[i];
            
            if (path != null) {
                DefaultMutableTreeNode node = 
                    (DefaultMutableTreeNode) path.getLastPathComponent();
                DvdCategory category = new DvdCategory(name);
                addCategory(node, category);
            }
        }
    }

    /**
     * adds a <code>category</code> to the given category <code>node</code>,
     * updates the model
     * 
     * @param node
     *            the category node to add to
     * @param category
     *            the category to add
     */
    private void addCategory(DefaultMutableTreeNode node, 
            DvdCategory category) {
        
        // update DvdCategory
        DvdDataObject data = (DvdDataObject) node.getUserObject();
        DvdCategory cat = data.getCategory();
        cat.insert(category);

        m_controller.setChanged(true);
        
        // update tree model
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
                new DvdDataObject(category));

        JTree tree = getTree();
        ((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, node,
                node.getChildCount());


        // expand the node and make the new node visible
        tree.expandPath(new TreePath(node.getPath()));
        tree.scrollPathToVisible(new TreePath(newNode.getPath()));
    }

    /**
     * removes the current selected category, updates the model
     */
    public void removeCurrentCategory() {
        TreePath selectionPath = getTree().getSelectionModel()
            .getLeadSelectionPath();

        if (selectionPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath
                    .getLastPathComponent();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node
                    .getParent();
            DvdDataObject data = (DvdDataObject) node.getUserObject();

            // update DvdCategory
            DvdCategory toRemove = data.getCategory();
            if (toRemove.getParent() != null) {
                toRemove.getParent().remove(toRemove);
                m_controller.setChanged(true);
            }

            // update tree model
            if (parent != null) {
                JTree tree = getTree();
                ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(node);
                // select the parent of the removed node
                TreePath parentPath = new TreePath(parent.getPath());
                tree.setSelectionPath(parentPath);
                tree.scrollPathToVisible(parentPath);
            }
        }
    }

    /**
     * create the recursivly
     * 
     * @param parent
     *            the node to insert childs
     * @param category
     *            the category reprented by <code>parent</code>
     */
    private void recCreateTree(DefaultMutableTreeNode parent,
            DvdCategory category) {

        List children = category.getCategories();
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            DvdCategory element = (DvdCategory) iter.next();

            DvdDataObject data = new DvdDataObject(element);
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(
                    data);
            parent.add(newChild);

            recCreateTree(newChild, element);
        }
    }

    /**
     * Creates the popupmenu
     * @return the popupmenu
     */
    private JPopupMenu createPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_controller.getAddCategoryAction());
        pm.add(m_controller.getEnableCategoryAction());
        pm.add(m_controller.getDisableCategoryAction());
        pm.add(m_controller.getRemoveCategoryAction());
        pm.add(m_controller.getAddDvdAction());
        return pm;
    }

    /**
     * private method for initialisation
     */
    private void init() {
        JTree tree = getTree();

        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        DvdCategory category = new DvdCategory(Constants.TREE_ROOT_NAME);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                new DvdDataObject(category));
        
        tree.setModel(new DefaultTreeModel(node));
        addTreeSelectionListener();
        
        tree.addMouseListener(new PopupListener(createPopupMenu()));

        tree.setSelectionRow(0);

        // create and install the 'drag and drop' listeners
        new DvdTreeDragSource(tree);
        new DvdTreeDropTarget(tree);
        
        DvdManager.singleton().setRootCategory(category);
    }

    /**
     * adds the tree selection listener
     */
    private void addTreeSelectionListener() {
        getTree().addTreeSelectionListener(m_treeSelectionListener);
    }

    /**
     * removes the tree selection listener
     */
    private void removeTreeSelectionListener() {
        getTree().removeTreeSelectionListener(m_treeSelectionListener);
    }

    /**
     * returns the controlled tree
     * @return the controlled tree
     */
    private JTree getTree() {
        return m_controller.getDvdMainFrame().getTreePanel().getTree();
    }

    /**
     * inner class used as TreeSelectionListener
     * 
     * @author BREDEX GmbH
     * @created 14.04.2005
     */
    private class MyTreeSelectionListener implements TreeSelectionListener {
        /**
         * listens to selection changes in the tree, updates the table, displays
         * the table / logo, enables/disables the actions AddCategory,
         * RemoveCategory, AddDvd and RemoveDvd
         * 
         * {@inheritDoc}
         */
        public void valueChanged(TreeSelectionEvent e) {
            boolean multipleTreeSelections = 
                (getTree().getSelectionCount() > 1);
            
            TreePath newLeadSelectionPath = e.getNewLeadSelectionPath();

            if (newLeadSelectionPath != null) {
                // selection changed
                // first store the data into the model
                m_controller.updateModel();
                
                // and then update the table
                Object o = newLeadSelectionPath.getLastPathComponent();

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
                DvdDataObject data = (DvdDataObject) node
                        .getUserObject();
                
                updateTable(data);
                showCard(data);
                
                // enable / disable the actions
                m_controller.getAddCategoryAction().setEnabled(true);
                if (data.hasParent()) {
                    if (!DevelopmentState.instance().isV2()) {
                        m_controller.getRemoveCategoryAction().setEnabled(
                                !multipleTreeSelections);
                    } else {
                        m_controller.getRemoveCategoryAction()
                                .setEnabled(false);
                    }
                } else {
                    m_controller.getRemoveCategoryAction().setEnabled(false);
                }

                m_controller.updateDisableOrEnableActions();
                
                // other actions depend on table selection, see table controller

            } else {
                // nothing selected
                m_controller.getAddCategoryAction().setEnabled(false);
                m_controller.getEnableCategoryAction().setEnabled(false);
                m_controller.getDisableCategoryAction().setEnabled(false);
                m_controller.getRemoveCategoryAction().setEnabled(false);
                m_controller.getAddDvdAction().setEnabled(false);
                
                // other actions depend on table selection, see table controller
            }
        }
    }
    
    /**
     * sets the enable state of the current selected category
     * @param enable the enable state to set
     */
    public void setCurrentCategoryEnableState(boolean enable) {
        TreePath selectionPath = getTree().getSelectionModel()
            .getLeadSelectionPath();

        if (selectionPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath
                    .getLastPathComponent();
            DvdDataObject data = (DvdDataObject) node.getUserObject();
            data.getCategory().setEnabled(enable);
        }
    }
    
    /**
     * returns the enable state of the current selected category
     * @return the enable state of the current selected category
     */
    public boolean isCurrentCategoryEnabled() {
        TreePath selectionPath = getTree().getSelectionModel()
            .getLeadSelectionPath();
        boolean enableState = true;

        if (selectionPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath
                    .getLastPathComponent();
            DvdDataObject data = (DvdDataObject) node.getUserObject();
            enableState = data.getCategory().isEnabled();
        }
        
        return enableState;
    }
    
}

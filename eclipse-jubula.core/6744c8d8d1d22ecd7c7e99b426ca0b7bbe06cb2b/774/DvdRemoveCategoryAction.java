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

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdDialogs;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdDataObject;


/**
 * This is the action class for deleteing a category.
 * @author BREDEX GmbH
 * @created 18.04.2005
 */
public class DvdRemoveCategoryAction extends AbstractAction  {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller; // see findBugs
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdRemoveCategoryAction(String name,  
            DvdMainFrameController controller) {
        
        super(name);
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        JTree tree = m_controller.getDvdMainFrame().getTreePanel().getTree();
        // ask for confirmation if the selected category has childrens
        TreePath selectionPath = tree.getSelectionModel()
            .getLeadSelectionPath();     
        if (selectionPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath
                    .getLastPathComponent();
            DvdDataObject data = (DvdDataObject) node.getUserObject();
            List<String> keys = new Vector<String>();
            if (data.hasCategories()) {
                keys.add("remove.category.confirmation.has.categories"); //$NON-NLS-1$                    
            }
            if (data.hasDvds()) {
                keys.add("remove.category.confirmation.has.dvd"); //$NON-NLS-1$
            }
            boolean remove = true;
            if (!keys.isEmpty()) {
                keys.add("remove.category.confirmation.consequence"); //$NON-NLS-1$
                keys.add("remove.category.confirmation.question"); //$NON-NLS-1$               
                remove = DvdDialogs.confirm2(m_controller.getDvdMainFrame(),
                        "dialog.confirm.remove.title", keys); //$NON-NLS-1$                        
            }
            if (remove) {
                m_controller.removeCurrentCategory();
            }
        }
    }
}
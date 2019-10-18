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
package org.eclipse.jubula.examples.aut.dvdtool.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * This class displays the tree with the category structure.
 *
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DvdTreePanel extends JPanel {
    /** the tree */
    private JTree m_tree = new JTree();
    
    /**
     * public constructor, initialises the panel
     */
    public DvdTreePanel() {
        super();
        init();
    }

    /**
     * private method for initialisation, constructs the whole tree.
     */    
    private void init() {
        // set the layout 
        setLayout(new BorderLayout());

        // set the renderer
        m_tree.setCellRenderer(new DvdTreeCellRenderer());

        // add the tree
        m_tree.setName("categoryTree"); //$NON-NLS-1$
        add(new JScrollPane(m_tree), BorderLayout.CENTER);
    }

    /**
     * @return the tree displaying the category structure
     */
    public JTree getTree() {
        return m_tree;
    }
}

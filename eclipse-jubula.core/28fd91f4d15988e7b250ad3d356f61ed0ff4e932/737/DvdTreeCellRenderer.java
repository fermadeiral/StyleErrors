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

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.eclipse.jubula.examples.aut.dvdtool.model.DvdDataObject;
import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This class is the renderer for the tree displaying the category structure.
 *
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DvdTreeCellRenderer extends DefaultTreeCellRenderer {
    /** the image icon for a category containing dvd(s) */
    private ImageIcon m_iconDvd = Resources.getImageIcon(Resources.CAT_ICON);
    /** the image icon for an empty category */
    private ImageIcon m_iconEmpty = Resources
            .getImageIcon(Resources.EMPTY_CAT_ICON);

    /**
     * public constructor
     */
    public DvdTreeCellRenderer() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel, boolean expanded, 
            boolean leaf, int row, boolean focus) {

        super.getTreeCellRendererComponent(
                tree, value, sel, expanded, leaf, row, focus);
        
        Object obj = ((DefaultMutableTreeNode)value).getUserObject();
        
        if (obj instanceof DvdDataObject) {
            DvdDataObject data = (DvdDataObject) obj;
            setIcon(data.hasDvds() ? m_iconDvd : m_iconEmpty);
            
            if (!data.getCategory().isEnabled()) {                
                setForeground(Color.gray);
            }
        }
        
        return this;
    }
}

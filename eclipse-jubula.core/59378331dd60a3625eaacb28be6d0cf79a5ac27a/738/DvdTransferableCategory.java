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
package org.eclipse.jubula.examples.aut.dvdtool.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


/**
 * The instances of this class provide a DvdCategory object and a node hash code 
 * for transfer operations.
 * The node hash code can be used for identification of the corresponding node.
 * (This class is needed for supporting 'drag and drop' feature in the tree.)
 *
 * @author BREDEX GmbH
 * @created 05.02.2008
 */
public class DvdTransferableCategory implements Transferable, Serializable {
    
    /** serialVersionUID */
    public static final long serialVersionUID = 2L;
    
    /** the data flavor class */
    private static final Class DATA_FLAVOR_CLASS = DvdCategory.class;    
    /** the transfer data flavor */
    private static final DataFlavor DATA_FLAVOR = new DataFlavor(
            DATA_FLAVOR_CLASS, "DVD Category"); //$NON-NLS-1$
    /** the array of supported transfer data flavors */
    private static final DataFlavor[] SUPPORTED_FLAVORS = {DATA_FLAVOR};
    
    /** the DvdCategory object to be transferred */
    private final DvdCategory m_category;   
    /** the node hash code to be transferred */
    private final int m_nodeHashCode;
    
    /**
     * public constructor
     * @param treePath The tree path to the node containing the DvdCategory 
     *      object to be transferred together with the node hash code.
     */
    public DvdTransferableCategory(TreePath treePath) {
        DefaultMutableTreeNode node = 
            (DefaultMutableTreeNode) treePath.getLastPathComponent();
        DvdDataObject dataObject = (DvdDataObject) node.getUserObject();
        m_category = dataObject.getCategory();  
        m_nodeHashCode = node.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public DataFlavor[] getTransferDataFlavors() {
        return SUPPORTED_FLAVORS;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.getRepresentationClass() == DATA_FLAVOR_CLASS);
    }

    /**
     * {@inheritDoc}
     */
    public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException {
        
        if (isDataFlavorSupported(flavor)) {
            return this;
        } 
        
        throw new UnsupportedFlavorException(flavor);
    }
    
    /**
     * @return the contained DvdCategory object
     */
    public DvdCategory getCategory() {
        return m_category;
    }
    
    /**
     * @return the contained node hash code
     */
    public int getNodeHashCode() {
        return m_nodeHashCode;
    }
    
}

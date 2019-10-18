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

/**
 * A <code>Transferable</code> which implements the capability required
 * to transfer a <code>String</code> array.
 * 
 * @author BREDEX GmbH
 * @created 06.02.2008
 */
public class StringArraySelection implements Transferable {

    /** the data flavor class */
    private static final Class DATA_FLAVOR_CLASS = String.class;    
    /** the transfer data flavor */
    private static final DataFlavor DATA_FLAVOR = new DataFlavor(
            DATA_FLAVOR_CLASS, "String array"); //$NON-NLS-1$
    /** the array of supported transfer data flavors */
    private static final DataFlavor[] SUPPORTED_FLAVORS = {DATA_FLAVOR};
    
    /** the data to be transferred */
    private String[] m_stringArray;
                           
    /**
     * Creates a <code>Transferable</code> capable of transferring
     * the specified <code>String</code> array.
     * 
     * @param strArray the data to be transferred
     */
    public StringArraySelection(String[] strArray) {
        m_stringArray = strArray;
    }

    /**
     * Returns an array of flavors in which this <code>Transferable</code>
     * can provide the data.
     * 
     * @return an array of the supported flavors
     */
    public DataFlavor[] getTransferDataFlavors() {
        // Note: Returning flavors itself would allow client code to modify
        // our internal behavior.
        return SUPPORTED_FLAVORS.clone();
    }

    /**
     * Returns whether the requested flavor is supported by this
     * <code>Transferable</code>.
     *
     * @param flavor the requested flavor for the data
     * @return true if <code>flavor</code> is supported
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (int i = 0; i < SUPPORTED_FLAVORS.length; i++) {
            if (flavor.equals(SUPPORTED_FLAVORS[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the <code>Transferable</code>'s data in the requested
     * <code>DataFlavor</code> if possible.
     *
     * @param flavor the requested flavor for the data
     * @return the data in the requested flavor, as outlined above
     * @throws UnsupportedFlavorException if the requested data flavor is not
     *         supported
     */
    public Object getTransferData(DataFlavor flavor) 
        throws UnsupportedFlavorException {
        
        if (isDataFlavorSupported(flavor)) {
            return m_stringArray;
        }

        throw new UnsupportedFlavorException(flavor);

    }

}

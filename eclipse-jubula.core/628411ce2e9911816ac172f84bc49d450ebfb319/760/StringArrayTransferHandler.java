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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.eclipse.jubula.examples.aut.dvdtool.model.StringArraySelection;


/**
 * This abstract class is for handling transfer operations of String arrays.
 * 
 * @author BREDEX GmbH
 * @created 06.02.2008
 */
abstract class StringArrayTransferHandler extends TransferHandler {
    
    /**
     * {@inheritDoc}
     */
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                String[] strArray = 
                    (String[])t.getTransferData(DataFlavor.stringFlavor);
                importStringArray(c, strArray);
                return true;
            } catch (UnsupportedFlavorException ufe) {
                throw new RuntimeException(ufe);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (DataFlavor.stringFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    protected Transferable createTransferable(JComponent c) {
        return new StringArraySelection(exportStringArray(c));
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSourceActions(JComponent c) {
        return COPY;
    }
    
    /**
     * @param c The component to retrieve the String array from
     * @return the String array to be retrieved
     */
    protected abstract String[] exportStringArray(JComponent c);
    
    /**
     * @param c The component to write the given String array into
     * @param strArray The String array to be written
     */
    protected abstract void importStringArray(JComponent c, String[] strArray);
    
}

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
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * This abstract class is for handling transfer operations of Strings.
 * 
 * @author BREDEX GmbH
 * @created 06.02.2008
 */
abstract class StringTransferHandler extends TransferHandler {
    
    /**
     * {@inheritDoc}
     */
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                String str = 
                    (String)t.getTransferData(DataFlavor.stringFlavor);
                importString(c, str);
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
        return new StringSelection(exportString(c));
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSourceActions(JComponent c) {
        return COPY;
    }
    
    /**
     * @param c The component to retrieve the String from
     * @return the String to be retrieved
     */
    protected abstract String exportString(JComponent c);
    
    /**
     * @param c The component to write the given String into
     * @param str The String to be written
     */
    protected abstract void importString(JComponent c, String str);
    
}

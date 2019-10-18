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

import javax.swing.JComponent;
import javax.swing.JTable;

/**
 * The instances of this class handle transfer operations of String arrays
 * in JTable objects.
 * The source table row of the transfer operation is copied over the target 
 * table row.
 * 
 * (Needed for supporting 'drag and drop' feature in the dvd table.)
 * 
 * @author BREDEX GmbH
 * @created 06.02.2008
 */
public class DvdTableTransferHandler extends StringArrayTransferHandler {
    
    /**
     * {@inheritDoc}
     */
    protected String[] exportStringArray(JComponent c) {
        JTable table = (JTable)c;
        int row = table.getSelectedRow();
        
        String[] strArray = new String[table.getColumnCount()];
        for (int i = 0; i < strArray.length; i++) {
            strArray[i] = table.getValueAt(row, i).toString();
        }
 
        return strArray;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void importStringArray(JComponent c, String[] strArray) {
        JTable table = (JTable)c;             
        int row = table.getSelectedRow();
            
        for (int i = 0; i < strArray.length; i++) {
            table.getModel().setValueAt(strArray[i], row, i);
        }
        table.repaint();
    }
    
}

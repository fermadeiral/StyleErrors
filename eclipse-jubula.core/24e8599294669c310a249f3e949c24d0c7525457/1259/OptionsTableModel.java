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
package org.eclipse.jubula.examples.aut.adder.swing.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * The model of the options table.
 *
 * @author BREDEX GmbH
 * @created 29.03.2005
 */
public class OptionsTableModel extends AbstractTableModel {
    /**
     * The table rows.
     */
    private List<OptionsTableEntry> m_rows = new ArrayList<OptionsTableEntry>();
    /**
     * {@inheritDoc}
     */
    public int getColumnCount() {
        return 2;
    }
    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return m_rows.size();
    }
    /**
     * {@inheritDoc}
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return getRowEntry(rowIndex).getValue();
    }
    /**
     * {@inheritDoc}
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        getRowEntry(rowIndex).setValue(aValue);
        fireTableDataChanged();
    }
    /**
     * Adds a new row to the table.
     * 
     * @param entry The table row.
     */
    public void addOptionsEntry(OptionsTableEntry entry) {
        m_rows.add(entry);
    }
    /**
     * {@inheritDoc}
     */
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Description"; //$NON-NLS-1$
            case 1:
                return "Value"; //$NON-NLS-1$
            default:
                return null;
        }
    }
    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                return false;
        }
    }
    /**
     * @param row The table row.
     * @return The row entry.
     */
    public OptionsTableEntry getRowEntry(int row) {
        return m_rows.get(row);
    }
}

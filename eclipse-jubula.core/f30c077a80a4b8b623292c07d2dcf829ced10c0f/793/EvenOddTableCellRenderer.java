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

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * This is a table cell renderer which displays the rows in two alternate
 * background colors.
 * 
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class EvenOddTableCellRenderer implements TableCellRenderer {
    /** the component (check box) used to render boolean values */
    private JCheckBox m_boolRenderer = new JCheckBox();
    
    /** the renderer which *really* does the work */ 
    private DefaultTableCellRenderer m_defaultRenderer = 
        new DefaultTableCellRenderer();

    /**
     * public constructor, initialises this renderer
     */
    public EvenOddTableCellRenderer() {
        super();

        init();
    }

    /**
     * private method for initialisation
     */
    private void init() {
        m_boolRenderer.setOpaque(true);
        m_boolRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    /**
     * private method determining the background color for the row
     * <code>row</code>
     * 
     * @param row
     *            the row number
     * @return the <code>Color</code> to use for the background of row
     *         <code>row</code>
     */
    private Color getBackgroundColor(int row) {
        return row % 2 == 0 ? Color.yellow : Color.white;
    }

    /**
     * Private method configuring and returning a check box to be used as
     * renderer for boolean values. 
     * 
     * @param table
     *            the <code>JTable</code> that is asking the renderer to draw;
     *            can be <code>null</code>
     * @param value
     *            the value of the cell to be rendered. Must be a Boolean.
     * @param isSelected
     *            true if the cell is to be rendered with the selection
     *            highlighted; otherwise false
     * @param row
     *            the row index of the cell being drawn. <br>
     *            determines the background color 
     * @return a configured CheckBox
     */
    private JCheckBox getBooleanRenderer(JTable table, Object value,
            boolean isSelected, int row) {

        if (isSelected) {
            m_boolRenderer.setForeground(table.getSelectionForeground());
            m_boolRenderer.setBackground(table.getSelectionBackground());
        } else {
            m_boolRenderer.setForeground(table.getForeground());
            m_boolRenderer.setBackground(getBackgroundColor(row));
        }
        
        m_boolRenderer.setSelected(((Boolean) value).booleanValue());

        return m_boolRenderer;
    }

    /**
     * {@inheritDoc}
     *      java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof Boolean) {
            return getBooleanRenderer(
                    table, value, isSelected, row);
        } 
        
        m_defaultRenderer.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        m_defaultRenderer.setHorizontalAlignment(value instanceof Number 
                ? SwingConstants.RIGHT : SwingConstants.LEFT);
        if (!isSelected) {
            m_defaultRenderer.setBackground(getBackgroundColor(row));
        }
        m_defaultRenderer.setOpaque(true);
        
        if (table.isEnabled()) {
            m_defaultRenderer.setForeground(table.getForeground());
        } else {
            m_defaultRenderer.setForeground(Color.gray);
        }
        
        return m_defaultRenderer;
    }
}

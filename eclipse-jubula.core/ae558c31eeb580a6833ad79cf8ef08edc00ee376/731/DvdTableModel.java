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

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.eclipse.jubula.examples.aut.dvdtool.model.Dvd;
import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This is the table model for a category. Each row is represented by an instance of <code>Dvd</code>
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DvdTableModel extends AbstractTableModel {
    
    /** serialVersionUID */
    public static final long serialVersionUID = 1L; // see findBugs
    
    /** the names of the columns */
    private static final String[] COLUM_NAMES = {
            Resources.getString("title"), Resources.getString("actor"), //$NON-NLS-1$ //$NON-NLS-2$
            Resources.getString("direction"), Resources.getString("year"), Resources.getString("limited") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** the category */
    private String m_category;

    /** the data for the category */
    private List m_data;

    /**
     * public constructor without parameter the model is empty
     */
    public DvdTableModel() {
        this("", new Vector()); //$NON-NLS-1$
    }

    /**
     * public constructor
     * @param category the category for this model
     * @param data the data to the category, should contain instances of model
     *            class <code>Dvd</code>, must not be null
     * @throws IllegalArgumentException if data is null
     */
    public DvdTableModel(String category, List data)
        throws IllegalArgumentException {
        
        super();
        if (data == null) {
            throw new IllegalArgumentException("null is not allowed as table data!"); //$NON-NLS-1$
        }
        this.m_category = category;
        this.m_data = data;
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return m_data.size();
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnCount() {
        return COLUM_NAMES.length;
    }

    /**
     * {@inheritDoc}
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        Dvd dvd = getDvd(rowIndex);
        switch (columnIndex) {
            case 0:
                value = dvd.getTitle();
                break;
            case 1:
                value = dvd.getActor();
                break;
            case 2:
                value = dvd.getDirection();
                break;
            case 3:
                value = dvd.getYear();
                break;
            case 4:
                value = dvd.isLimited();
                break;
            default:
                // do nothing
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Dvd dvd = getDvd(rowIndex);
        switch (columnIndex) {
            case 0:
                dvd.setTitle(aValue.toString());
                break;
            case 1:
                dvd.setActor(aValue.toString());
                break;
            case 2:
                dvd.setDirection(aValue.toString());
                break;
            case 3:
                dvd.setYear(Integer.parseInt(aValue.toString()));
                break;
            case 4:
                dvd.setLimited(Boolean.valueOf(aValue.toString())
                        .booleanValue());
                break;
            default:
                // do nothing
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getColumnName(int column) {
        return COLUM_NAMES[column];
    }

    /**
     * returns the <code>Dvd</code> for row <code>row</code>
     * @param row the row number
     * @return the dvd displayed in <code>row</code>
     */
    public Dvd getDvd(int row) {
        return (Dvd) m_data.get(row);
    }

    /**
     * returns the (international) category as string representation.
     * {@inheritDoc}
     */
    public String toString() {
        return m_category;
    }
}
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
package org.eclipse.jubula.client.core.businessprocess.importfilter;

/**
 * @author BREDEX GmbH
 * @created Nov 8, 2005
 *
 */
public class DataTable {

    /**
     * data table
     */
    private String[][] m_data;
    
    /** height of datatable */
    private int m_height;
    
    /** width of datatable */
    private int m_width;
    
    /**
     * constructor
     * 
     * @param height
     *      int
     * @param width
     *      int
     */
    public DataTable(int height, int width) {
        m_height = height;
        m_width = width;
        m_data = new String[m_height][m_width];
    }
    /**
     * @return height of data table
     */
    public int getRowCount() {
        return m_height;
    }

    /**
     * @return width of data table
     */
    public int getColumnCount() {
        return m_width;
    }
    
    /**
     * @return amount of cells in dataTable
     */
    public int getCellAmount() {
        return m_height * m_width;
    }
    
    /**
     * @return dataTable
     * @param row
     *      selected row
     * @param column
     *      selected column
     */
    public String getData(int row, int column) {
        return m_data[row][column];
    }
    
    /**
     * Updates a data Entry
     * @param row
     *      Row (starts with 0) 
     * @param column
     *      Column (starts with 0)
     * @param data
     *      DataEntry as String
     */
    public void updateDataEntry(int row, int column,
        String data) {
        m_data[row][column] = data;
    }
    
}

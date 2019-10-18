/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester.adapter.interfaces;

import java.awt.Rectangle;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;

/**
 * Interface for all necessary methods for testing tables.
 * 
 * @author BREDEX GmbH
 * @param <T> the type of a table item
 */
public interface ITableComponent<T> extends ITextComponent {
    
    
    /**
     * Gets the number of columns
     * 
     * @return the number of columns
     */
    public int getColumnCount();
    
    /**
     * Gets the number of rows
     * 
     * @return the number of rows
     */
    public int getRowCount();
    
    /**
     * @param row
     *            the zero based index of the row
     * @param column
     *            the zero based index of the column
     * @return the text of the cell of the given coordinates
     */
    public String getCellText(int row, int column);
     
    /**
     * Returns the text of the column appearing in the view at column position
     * <code>column</code>.
     * 
     * @param column
     *            the zero based index of the column in the view being queried
     * @return the text of the column at position <code>column</code> in the
     *         view where the first column is column 0
     */
    public String getColumnHeaderText(int column);

    /**
     * This is only for a specific case where tables could act like lists. And
     * the getText is not working. If this is not the case for the component
     * return <code>null</code>
     * 
     * @param row
     *            the zero based index of the row
     * @return the text of the row
     */
    public String getRowText(int row);
    
    /**
     * @return The currently selected cell of the Table.
     * @throws StepExecutionException
     *             If no cell is selected.
     */
    public Cell getSelectedCell() throws StepExecutionException;
    
    /**
     * 
     * @return <code>true</code> if the header is visible, <code>false</code> otherwise
     */
    public boolean isHeaderVisible();

    /**
     * 
     * @param row
     *            zero based index of the row
     * @param col
     *            zero based index of the column
     * @return <code>true</code> if the Cell is editable, <code>false</code>
     *         otherwise
     */
    public boolean isCellEditable(int row, int col);

    /**
     * @return <code>true</code>, if there is any cell selection in the table,
     *         <code>false</code> otherwise.
     */
    public boolean hasCellSelection();
    
    /**
     * Scrolls the passed cell (as row and column) to visible.<br>
     * This method must return null if there is no scrolling.
     * 
     * @param row
     *            zero based index of the row.
     * @param col
     *            zero based index of the column.
     * @return The rectangle of the cell.
     * @throws StepExecutionException
     *             If getting the cell rectangle or the scrolling fails.
     */
    public Rectangle scrollCellToVisible (int row, int col)
        throws StepExecutionException;

    /**
     * @return The TableHeader if there is one,otherwise
     *                  the table is returned.
     */
    public Object getTableHeader();

    /**
     * gets header bounds for column within the object returned
     *    by getTableHeader()
     * 
     * @param col
     *            the zero based index of the column.
     * @return The rectangle of the header
     */
    public Rectangle getHeaderBounds(int col);

    /**
     * Gets the property value of a table cell
     * @param name the name of the property
     * @param cell the cell
     * @return the value
     */
    public String getPropertyValueOfCell(String name, T cell);

    /**
     * Required for SWT - we don't want to unnecessarily
     *      scan the whole tree
     * @param rowInd the row index
     * @return whether the row exists
     */
    public boolean doesRowExist(int rowInd);

    /**
     * Returns the top element's index
     * @return the index
     */
    public int getTopIndex();

    /**
     * @param row the row index
     * @param col the column index
     * @param restr whether to return the approximate bounds
     *        for the text or the whole cell
     * @return the cell bounds within the Tree / Table
     */
    public Rectangle getCellBounds(int row, int col, boolean restr);

}

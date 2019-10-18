/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester.interfaces;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.AbstractTableTester;

/**
 * Actions implemented by Tables and TreeTables
 * @author BREDEX GmbH
 *
 */
public interface ITableActions {

    /**
     * Stores the string representation of the value of the property of the
     * given Node
     * 
     * @param variableName
     *            the name of the variable
     * @param propertyName
     *            the name of the property
     * @return string representation of the property value
     */
    public String rcStorePropertyValueAtMousePosition(String variableName,
            final String propertyName);

    /**
     * Selects the cell of the Table.<br>
     * With the xPos, yPos, xunits and yUnits the click position inside the cell can be defined.
     * @param row The row of the cell.
     * @param rowOperator The row header operator
     * @param col The column of the cell.
     * @param colOperator The column header operator
     * @param clickCount The number of clicks with the right mouse button
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param extendSelection Should this selection be part of a multiple selection
     * @param button what mouse button should be used
     * @throws StepExecutionException If the row or the column is invalid
     */
    public void rcSelectCell(final String row, final String rowOperator,
        final String col, final String colOperator,
        final int clickCount, final int xPos, final String xUnits,
        final int yPos, final String yUnits, final String extendSelection,
        int button) 
        throws StepExecutionException;

    /**
     * Verifies the editable property of the given indices.
     *
     * @param editable
     *            The editable property to verify.
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param col the column to select
     * @param colOperator the column header operator
     * @param timeout the maximum amount of time to wait to verify whether the
     *          cell is editable or not
     */
    public void rcVerifyEditable(final boolean editable, String row,
            String rowOperator, String col, String colOperator, int timeout);

    /**
     * Verifies the editable property of the cell under current mouse position.
     *
     * @param editable the editable property to verify.
     * @param timeout the maximum amount of time to wait to verify whether
     *          the property of the cell under current mosue position is
     *          editable
     */
    public void rcVerifyEditableMousePosition(final boolean editable,
            int timeout);

    /**
     * Verifies the rendered text inside the passed cell.
     * @param row The row of the cell.
     * @param rowOperator The row header operator
     * @param col The column of the cell.
     * @param colOperator The column header operator
     * @param text The cell text to verify.
     * @param operator The operation used to verify
     * @param timeout the amount of time to wait for the text in the passed
     *          cell to be verified
     * @throws StepExecutionException If the row or the column is invalid, or if the rendered text cannot be extracted.
     */
    public void rcVerifyText(final String text, final String operator,
            final String row, final String rowOperator, final String col,
            final String colOperator, int timeout)
            throws StepExecutionException;

    /**
     * Verifies the rendered text inside cell at the mouse position on screen.
     *
     * @param text The cell text to verify.
     * @param operator The operation used to verify
     * @param timeout the maximum amount of time to wait for the text at mouse
     *          position to be verified
     * @throws StepExecutionException If there is no selected cell, or if the
     *                              rendered text cannot be extracted.
     */
    public void rcVerifyTextAtMousePosition(final String text,
            final String operator, int timeout) throws StepExecutionException;

    /**
     * Verifies, if value exists in column.
     *
     * @param col The column of the cell.
     * @param colOperator the column header operator
     * @param value The cell text to verify.
     * @param operator The operation used to verify
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param exists true if value exists, false otherwise
     * @param timeout the maximum amount of time to wait to verify if the
     *          value exists in the specified column
     * @throws StepExecutionException
     *             If the row or the column is invalid, or if the rendered text
     *             cannot be extracted.
     */
    public void rcVerifyValueInColumn(final String col,
            final String colOperator, final String value,
            final String operator, final String searchType,
            final boolean exists, int timeout)
        throws StepExecutionException;

    /**
     * Verifies, if value exists in row..
     *
     * @param row The row of the cell.
     * @param rowOperator the row header operator
     * @param value The cell text to verify.
     * @param operator The operation used to verify
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param exists true if value exists, false otherwise
     * @param timeout the maximum amount of time to wait to verify whether the
     *          value exists in the row
     * @throws StepExecutionException
     *             If the row or the column is invalid, or if the rendered text
     *             cannot be extracted.
     */
    public void rcVerifyValueInRow(final String row, final String rowOperator,
            final String value, final String operator, final String searchType,
            final boolean exists, int timeout)
                    throws StepExecutionException;

    /**
     * Finds the first row which contains the value <code>value</code>
     * in column <code>col</code> and selects this row.
     * @param col the column
     * @param colOperator the column header operator
     * @param value the value
     * @param clickCount the number of clicks.
     * @param regexOp the regex operator
     * @param extendSelection Should this selection be part of a multiple selection
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param button what mouse button should be used
     */
    public void rcSelectRowByValue(String col, String colOperator,
            final String value, final String regexOp, int clickCount,
            final String extendSelection, final String searchType, int button);

    /**
     * Finds the first column which contains the value <code>value</code>
     * in the given row and selects the cell.
     *
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param value the value
     * @param clickCount the number of clicks
     * @param regex search using regex
     * @param extendSelection Should this selection be part of a multiple selection
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param button what mouse button should be used
     */
    public void rcSelectCellByColValue(String row, String rowOperator,
        final String value, final String regex, int clickCount,
        final String extendSelection, final String searchType, int button);

    /**
     * Action to read the value of the passed cell of the JTable
     * to store it in a variable in the Client
     * @param variable the name of the variable
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param col the column to select
     * @param colOperator the column header operator
     * @return the text value.
     */
    public String rcReadValue(String variable, String row, String rowOperator,
            String col, String colOperator);

    /**
     * @see {@link AbstractTableTester#rcReadValue(String, String, String, String)}
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param col the column to select
     * @param colOperator the column header operator
     * @return the text value at the defined position.
     */
    public String rcReadValue(String row, String rowOperator,
            String col, String colOperator);

    /**
     * Verifies the value of the property with the name <code>name</code>
     * of the tree item at the current mouse position.
     * The name of the property has be specified according to the JavaBean
     * specification. The value returned by the property is converted into a
     * string by calling <code>toString()</code> and is compared to the passed
     * <code>value</code>.
     * 
     * @param name The name of the property
     * @param value The value of the property as a string
     * @param operator The operator used to verify
     * @param timeout the maximum amount of time to wait for the property
     *          at mouse position to be checked
     */
    public void rcCheckPropertyAtMousePosition(final String name,
            final String value, final String operator, int timeout);

    /**
     * Checks if a given column exists, respectively does not exist
     * @param column the column
     * @param columnOperator the operator to find the column
     * @param exists true when the column should be found
     * @param timeout the maximum amount of time to wait for the check whether
     *          the given column exists to be performed
     */
    public void rcCheckExistenceOfColumn(final String column,
            final String columnOperator, final boolean exists, int timeout);

    /**
     * Reads the cell value from below the mouse position
     * @param variable the variable name
     * @return the value
     */
    public String rcReadValueAtMousePosition(String variable);

    /**
     * Reads the cell value from below the mouse position
     * @return the text value at mouse position
     */
    public String rcReadValueAtMousePosition();

}
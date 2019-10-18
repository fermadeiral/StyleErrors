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
package org.eclipse.jubula.rc.common.tester;

import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;

import java.awt.Rectangle;

import org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITableComponent;
import org.eclipse.jubula.rc.common.tester.interfaces.ITableActions;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.SearchType;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * General implementation for tables.
 * 
 * @author BREDEX GmbH
 */
public abstract class AbstractTableTester 
    extends AbstractTextInputSupportTester implements ITableActions {
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            AbstractTableTester.class);

    /**
     * @return the log
     */
    public static AutServerLogger getLog() {
        return log;
    }
    
    /**
     * This method is mostly needed for clicks
     * @return the real table as object
     */
    private Object getRealTable() {
        return getComponent().getRealComponent();
    }
    /**
     * 
     * @return the ITableAdapter of this table
     */
    private ITableComponent getTableAdapter() {
        return (ITableComponent) getComponent();
    }
    
    /**
     * Verifies the rendered text inside the currently selected cell.
     *
     * @param text The cell text to verify.
     * @param timeout the maximum amount of time to wait for the rendered text
     *          inside the currently selected cell to be verified
     * @throws StepExecutionException
     *      If there is no selected cell, or if the rendered text cannot
     *      be extracted.
     */
    public void rcVerifyText(String text, int timeout)
        throws StepExecutionException {

        rcVerifyText(text, MatchUtil.DEFAULT_OPERATOR, timeout);
    }
    
    /**
     * Verifies the rendered text inside the currently selected cell.
     * @param text The cell text to verify.
     * @param operator The operation used to verify
     * @param timeout the maximum amount of time to wait for the rendered text
     *          inside the currently selected cell to be verified
     * @throws StepExecutionException If there is no selected cell, or if the
     *          rendered text cannot be extracted.
     */
    public void rcVerifyText(final String text, final String operator,
            int timeout) throws StepExecutionException {
        invokeAndWait("rcVerifyText", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                ITableComponent adapter = getTableAdapter();
                Cell cell = adapter.getSelectedCell();
                final int implRow = cell.getRow();
                final int implCol = cell.getCol();
                checkRowColBounds(implRow, implCol);

                adapter.scrollCellToVisible(implRow, implCol);
                final String current = getCellText(implRow, implCol);
                Verifier.match(current, text, operator);
            }
        });
    }
    
    /** {@inheritDoc} */
    public void rcVerifyText(final String text, final String operator,
            final String row, final String rowOperator, final String col,
            final String colOperator, int timeout)
            throws StepExecutionException {
        invokeAndWait("rcVerifyText", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                ITableComponent adapter = getTableAdapter();
                String current;
                int startRowIndex = 0;
                Integer oldImplRow = null;
                do {
                    final int implRow = getRowFromStringAbstract(row,
                            rowOperator, startRowIndex);

                    if (oldImplRow != null
                            && implRow == oldImplRow.intValue()) {
                        Verifier.match(null, text, operator);
                    }

                    final int implCol =
                            getColumnFromStringAbstract(col, colOperator);
                    // if row is header
                    if (implRow == -1) {
                        current = adapter.getColumnHeaderText(implCol);
                    } else {
                        checkRowColBounds(implRow, implCol);
                        adapter.scrollCellToVisible(implRow, implCol);
                        current = getCellText(implRow, implCol);
                    }
                    oldImplRow = new Integer(implRow);
                    startRowIndex = implRow + 1;
                } while (!MatchUtil.getInstance().match(current, text,
                        operator));
            }
        });
        
    }
    
    /** {@inheritDoc} */
    public void rcSelectCell(final String row, final String rowOperator,
        final String col, final String colOperator,
        final int clickCount, final int xPos, final String xUnits,
        final int yPos, final String yUnits, final String extendSelection,
        int button) 
        throws StepExecutionException {
        ITableComponent adapter = getTableAdapter();
        final int implRow = getRowFromStringAbstract(row, rowOperator, 0);
        final int implCol = getColumnFromStringAbstract(col, colOperator);
        final boolean isExtendSelection = extendSelection.equals(
                ValueSets.BinaryChoice.yes.rcValue()); 
        if (log.isDebugEnabled()) {
            log.debug("Selecting row, col: " + row + ", " + col); //$NON-NLS-1$//$NON-NLS-2$
        }
        
        Rectangle cellBounds;
        Object source = getRealTable();
        ClickOptions clickOptions = ClickOptions.create();
        clickOptions.setClickCount(clickCount).setScrollToVisible(false);
        clickOptions.setMouseButton(button);
        //if row is header
        if (implRow == -1) {
            cellBounds = adapter.getHeaderBounds(implCol);
            source = adapter.getTableHeader();
            // clicking on headers sometimes don't make the Table generate the click event
            clickOptions.setConfirmClick(false);
        } else {
            cellBounds = adapter.scrollCellToVisible(implRow, implCol);
        }        
        Object o = getSpecificRectangle(cellBounds);
        try {
            if (isExtendSelection) {
                getRobot().keyPress(getRealTable(),
                        getExtendSelectionModifier());
            }
            getRobot().click(source, o, clickOptions, 
                    xPos, xUnits.equalsIgnoreCase(
                            ValueSets.Unit.pixel.rcValue()), 
                    yPos, yUnits.equalsIgnoreCase(
                            ValueSets.Unit.pixel.rcValue()));
        } finally {
            if (isExtendSelection) {
                getRobot().keyRelease(getRealTable(),
                        getExtendSelectionModifier());
            }
        }
    }
    /**
     * This is a workaround because the toolkit specific
     * Robot implementation are using different rectangle types.
     * @param rectangle the java.awt.rectangle which needs to 
     *          casted
     * @return the rectangle in the type for the specific robot
     */
    protected Object getSpecificRectangle(Rectangle rectangle) {
        return rectangle;
    }
    /** {@inheritDoc} */
    public void rcVerifyValueInColumn(final String col,
            final String colOperator, final String value,
            final String operator, final String searchType,
            final boolean exists, int timeout)
        throws StepExecutionException {
        invokeAndWait("rcVerifyValueInColumn", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                ITableComponent adapter = getTableAdapter();
                final int implCol =
                        getColumnFromStringAbstract(col, colOperator);

                boolean valueExists = isValueExisting(adapter, implCol,
                        value, operator, searchType);

                Verifier.equals(exists, valueExists);
            }
        });
        
    }
    /**
     * Looks if value exists in the Column.
     * 
     * @param adapter the teble adapter working on.
     * @param implCol the implementation column of the cell.
     * @param value the cellt text to verify.
     * @param operator The operation used to verify.
     * @param searchType searchType Determines where the search begins ("relative" or "absolute")
     * @return <code>true</code> it the value exists in the column
     */
    private boolean isValueExisting(ITableComponent adapter, int implCol, 
            String value, String operator, final String searchType) {
        final int rowCount = adapter.getRowCount();
        for (int i = getStartingRowIndex(searchType);
                i < rowCount; ++i) {
            if (MatchUtil.getInstance().match(getCellText(i,
                            implCol), value, operator)) {
                return true;
            }
        }
        if (adapter.isHeaderVisible()) {
            String header = adapter.getColumnHeaderText(implCol);
            if (MatchUtil.getInstance().match(header, value,
                                operator)) {
                return true;
            }
        }
        return false;
    }
    
    
    
    /** {@inheritDoc} */
    public void rcVerifyValueInRow(final String row, final String rowOperator,
            final String value, final String operator, final String searchType,
            final boolean exists, int timeout)
                    throws StepExecutionException {
        invokeAndWait("rcVerifyValueInRow", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                final ITableComponent adapter = getTableAdapter();
                final int implRow =
                        getRowFromStringAbstract(row, rowOperator, 0);
                boolean valueIsExisting = false;
                //if row is header
                if (implRow == -1) {
                    
                    for (int k = getStartingColIndex(searchType); 
                                            k < adapter.getColumnCount(); ++k) {
                        if (MatchUtil.getInstance().match(
                                adapter.getColumnHeaderText(k),
                                value, operator)) {
                            valueIsExisting = true;
                            break;
                        }
                    }
                                    
                                
                } else {
                    
                    final int columnCount = adapter.getColumnCount();
                    if (columnCount > 0) {
                        for (int i = getStartingColIndex(searchType); 
                                i < columnCount; ++i) {
                            if (MatchUtil.getInstance().match(
                                    getCellText(implRow, i), value, operator)) {
                                valueIsExisting = true;
                                break;
                            }
                        }
                    } else {
                        // No columns found. This table is used to present a
                        // list-like component.
                        if (MatchUtil.getInstance().match(
                                adapter.getRowText(implRow),
                                    value, operator)) {
                            valueIsExisting = true;
                            
                        }
                    }             
          
                }
                Verifier.equals(exists, valueIsExisting);
            }
        });
    }
    
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
            String rowOperator, String col, String colOperator, int timeout) {
        final int rowInd = getRowFromStringAbstract(row, rowOperator, 0);
        final int colInd = getColumnFromStringAbstract(col, colOperator);
        //if row is header row
        if (rowInd == -1) {
            throw new StepExecutionException("Unsupported Header Action", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.UNSUPPORTED_HEADER_ACTION));
        }
        selectCell(row, rowOperator, col, colOperator, ClickOptions.create(),
                ValueSets.BinaryChoice.no.rcValue());
        CheckWithTimeoutQueuer.invokeAndWait("rcVerifyEditable", timeout, //$NON-NLS-1$
            new Runnable() {
                public void run() {
                    Verifier.equals(editable, getTableAdapter()
                            .isCellEditable(rowInd, colInd));
                }
            });
    }
    
    
    /**
     * Selects a table cell in the given row and column via click in the middle of the cell.
     * @param row The row of the cell.
     * @param rowOperator The row header operator
     * @param col The column of the cell.
     * @param colOperator The column header operator
     * @param co the click options to use
     * @param extendSelection Should this selection be part of a multiple selection
     */
    private void selectCell(final String row, final String rowOperator,
            final String col, final String colOperator,
            final ClickOptions co, final String extendSelection) {
            
        rcSelectCell(row, rowOperator, col, colOperator, co.getClickCount(),
                50, ValueSets.Unit.percent.rcValue(), 50,
                ValueSets.Unit.percent.rcValue(), extendSelection,
                co.getMouseButton());
    }
    
    
    /** {@inheritDoc} */
    public void rcVerifyTextAtMousePosition(final String text,
            final String operator, int timeout) throws StepExecutionException {
        if (isMouseOnHeader()) {
            throw new StepExecutionException("Unsupported Header Action", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.UNSUPPORTED_HEADER_ACTION));
        }
        Cell cell = getCellAtMousePosition();
        String cellText = null;
        if (cell.getRow() == -1) {
            cellText = getTableAdapter().getColumnHeaderText(cell.getCol());
        } else {
            cellText = getCellText(cell.getRow(), cell.getCol());
        }
        MatchUtil.getInstance().match(cellText, text, operator);
    }
    
    /**
     * Verifies the editable property of the current selected cell.
     *
     * @param editable The editable property to verify.
     * @param timeout the maximum amount of time to wait for the component
     *                  to have the editable status to be the same as the parameter

     */
    public void rcVerifyEditable(final boolean editable, int timeout) {
        final Cell cell = getTableAdapter().getSelectedCell();
        CheckWithTimeoutQueuer.invokeAndWait("rcVerifyEditable", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        Verifier.equals(editable, getTableAdapter()
                                .isCellEditable(cell.getRow(), cell.getCol()));
                    }
                });
    }
    
    /** {@inheritDoc} */
    public void rcVerifyEditableMousePosition(final boolean editable,
            int timeout) {
        //if row is header row
        invokeAndWait("rcVerifyEditableMousePosition", timeout, //$NON-NLS-1$
            new Runnable () {
                    public void run() {
                    if (isMouseOnHeader()) {
                        throw new StepExecutionException(
                                "Unsupported Header Action", //$NON-NLS-1$
                                EventFactory.createActionError(
                                    TestErrorEvent.UNSUPPORTED_HEADER_ACTION));
                    }
                    Cell cell = getCellAtMousePosition();
                    boolean isEditable = getTableAdapter().isCellEditable(
                            cell.getRow(), cell.getCol());
                    Verifier.equals(editable, isEditable);
                }
            });
    }

    /** {@inheritDoc} */
    public void rcSelectRowByValue(String col, String colOperator,
            final String value, final String regexOp, int clickCount,
            final String extendSelection, final String searchType, int button) {
        selectRowByValue(col, colOperator, value, regexOp, extendSelection,
                searchType, ClickOptions.create()
                        .setClickCount(clickCount)
                        .setMouseButton(button));
    }
    
    /**
     * Finds the first row which contains the value <code>value</code>
     * in column <code>col</code> and selects this row.
     *
     * @param col the column
     * @param colOperator the column header operator
     * @param value the value
     * @param regexOp the regex operator
     * @param extendSelection Should this selection be part of a multiple selection
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param co the clickOptions to use
     */
    protected void selectRowByValue(String col, String colOperator,
        final String value, final String regexOp, final String extendSelection,
        final String searchType, ClickOptions co) {
        ITableComponent adapter = getTableAdapter();
        final int implCol = getColumnFromStringAbstract(col, colOperator);
        Integer implRow = findRow(value, regexOp, searchType, adapter, implCol);
        
        String  userIdxRow = new Integer(IndexConverter.toUserIndex(
                implRow.intValue())).toString();
        String  userIdxCol = new Integer(IndexConverter.toUserIndex(
                implCol)).toString();            
        
        selectCell(userIdxRow, MatchUtil.EQUALS, userIdxCol, colOperator, co,
                extendSelection);
    }

    /**
     * Find the row containing the given value in the given column
     * @param value the value
     * @param regexOp the regular expression
     * @param searchType relative or absolute
     * @param adapter the adapter
     * @param implCol the implementation column index
     * @return the implementation row index
     */
    protected Integer findRow(final String value, final String regexOp,
            final String searchType, ITableComponent adapter,
            final int implCol) {
        Integer implRow = null;
        final int rowCount = adapter.getRowCount();

        for (int i = getStartingRowIndex(searchType); i < rowCount; ++i) {
            if (MatchUtil.getInstance().match(getCellText(i, implCol), value,
                    regexOp)) {

                implRow = new Integer(i);
                break;
            }
        }
        if (implRow == null) {
            String header = adapter.getColumnHeaderText(implCol);
            if (MatchUtil.getInstance().match(header, value, regexOp)) {
                implRow = new Integer(-1);
            }
        }

        if (implRow == null) {
            throw new StepExecutionException("no such row found", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        return implRow;
    }
    
    /** {@inheritDoc} */
    public void rcSelectCellByColValue(String row, String rowOperator,
        final String value, final String regex, int clickCount,
        final String extendSelection, final String searchType, int button) {
        selectCellByColValue(row, rowOperator, value, regex, extendSelection,
                searchType, ClickOptions.create()
                    .setClickCount(clickCount)
                    .setMouseButton(button));
    }
    
    /**
     * Finds the first column which contains the value <code>value</code>
     * in the given row and selects the cell.
     *
     * @param row the row
     * @param rowOperator the row header operator
     * @param value the value
     * @param regex search using regex
     * @param extendSelection Should this selection be part of a multiple selection
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param co the click options to use
     */
    protected void selectCellByColValue(String row, String rowOperator,
            final String value, final String regex,
            final String extendSelection, final String searchType,
            ClickOptions co) {
        ITableComponent adapter = getTableAdapter();
        final int implRow = getRowFromStringAbstract(row, rowOperator, 0);
        Integer implCol = findColumn(value, regex, searchType, adapter,
                implRow);

        String usrIdxRowStr = new Integer(IndexConverter.toUserIndex(implRow))
                .toString();
        String usrIdxColStr = new Integer(
                IndexConverter.toUserIndex(implCol.intValue())).toString();

        selectCell(usrIdxRowStr, rowOperator, usrIdxColStr, MatchUtil.EQUALS,
                co, extendSelection);

    }

    /**
     * Determines the column, within the given row, containing the cell or header text equal to value. 
     * @param value the value
     * @param regex the regular expression
     * @param searchType absolute or relative search
     * @param adapter the table adapter
     * @param implRow the row using the implementation index, starts at 0
     * @return the implementation index of the column
     */
    protected Integer findColumn(final String value, final String regex,
            final String searchType, ITableComponent adapter,
            final int implRow) {
        int colCount = adapter.getColumnCount();
        Integer implCol = null;
        if (implRow == -1) {

            for (int i = getStartingColIndex(searchType); i < colCount; ++i) {
                if (MatchUtil.getInstance()
                        .match(adapter.getColumnHeaderText(i), value, regex)) {
                    implCol = new Integer(i);
                    break;
                }
            }
        } else {
            for (int i = getStartingColIndex(searchType); i < colCount; ++i) {
                if (MatchUtil.getInstance().match(getCellText(implRow, i),
                        value, regex)) {

                    implCol = new Integer(i);
                    break;
                }
            }
        }
        if (implCol == null) {
            throw new StepExecutionException("no such cell found", EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.NOT_FOUND));
        }
        return implCol;
    }
    
    /** {@inheritDoc} */
    public String rcReadValue(String variable, String row, String rowOperator,
            String col, String colOperator) {
        ITableComponent adapter = getTableAdapter();
        final int implRow = getRowFromStringAbstract(row, rowOperator, 0);
        final int implCol = getColumnFromStringAbstract(col, colOperator);
        
        //if row is header
        if (implRow == -1) {
            return adapter.getColumnHeaderText(implCol); 
        }

        checkRowColBounds(implRow, implCol);

        adapter.scrollCellToVisible(implRow, implCol);
        return getCellText(implRow, implCol);
    }
    
    /** {@inheritDoc} */
    public String rcReadValue(String row, String rowOperator,
            String col, String colOperator) {
        return rcReadValue(null, row, rowOperator, col, colOperator);
    }
    
    /**
     * {@inheritDoc}
     */
    public String rcReadValueAtMousePosition(String variable) {
        Cell cellAtMousePosition = getCellAtMousePosition();
        return getCellText(cellAtMousePosition.getRow(), 
                cellAtMousePosition.getCol());
    }
    
    /**
     * @see {@link AbstractTableTester#rcReadValueAtMousePosition(String)}
     * @return the text value at mouse position
     */
    public String rcReadValueAtMousePosition() {
        return rcReadValueAtMousePosition(null);
    }
    
    /**
     * Tries to click in the cell under the mouse position. If the mouse is not
     * over a cell, the current selected cell will be clicked on. If there is no
     * selected cell, the middle of the table is used to click on.
     * @param count Number of clicks
     * @param button The mouse button
     */
    public void rcClick(int count, int button) {
        ITableComponent adapter = getTableAdapter();
        Cell cell = null;
        if (isMouseOverCell()) {
            cell = getCellAtMousePosition();
        } else if (adapter.hasCellSelection()) {
            cell = adapter.getSelectedCell();
        } 
        if (cell != null) {
            Rectangle cellRect = 
                    adapter.scrollCellToVisible(cell.getRow(), cell.getCol());
            Object robotSpecifcRectangle = getSpecificRectangle(cellRect);
            getRobot().click(getRealTable(), robotSpecifcRectangle,
                    ClickOptions.create().setClickCount(count)
                    .setMouseButton(button));
        } else {
            super.rcClick(count, button);
        }
    }
   
    /**
     * Selects a cell relative to the cell at the current mouse position.
     * If the mouse is not at any cell, the current selected cell is used.
     * @param direction the direction to move.
     * @param cellCount the amount of cells to move
     * @param clickCount the click count to select the new cell.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param extendSelection Should this selection be part of a multiple selection
     * @throws StepExecutionException if any error occurs
     */
    public void rcMove(String direction, int cellCount, int clickCount,
        final int xPos, final String xUnits,
        final int yPos, final String yUnits, final String extendSelection)
        throws StepExecutionException {
        if (isMouseOnHeader()) {
            throw new StepExecutionException("Unsupported Header Action", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.UNSUPPORTED_HEADER_ACTION));
        }
        Cell currCell = null;
        try {
            currCell = getCellAtMousePosition();
        } catch (StepExecutionException e) {
            currCell = getTableAdapter().getSelectedCell();
        }
        int newCol = currCell.getCol();
        int newRow = currCell.getRow();
        if (ValueSets.Direction.up.rcValue().equalsIgnoreCase(direction)) {
            newRow -= cellCount;
        } else if (ValueSets.Direction.down.rcValue()
                .equalsIgnoreCase(direction)) {
            newRow += cellCount;
        } else if (ValueSets.Direction.left.rcValue()
                .equalsIgnoreCase(direction)) {
            newCol -= cellCount;
        } else if (ValueSets.Direction.right.rcValue()
                .equalsIgnoreCase(direction)) {
            newCol += cellCount;
        }
        newRow = IndexConverter.toUserIndex(newRow);
        newCol = IndexConverter.toUserIndex(newCol);
        String row = Integer.toString(newRow);
        String col = Integer.toString(newCol);
        rcSelectCell(row, MatchUtil.DEFAULT_OPERATOR, col,
                MatchUtil.DEFAULT_OPERATOR, clickCount, xPos, xUnits, yPos,
                yUnits, extendSelection, InteractionMode.primary.rcIntValue());
    }
    
    /**
     * Writes the passed text into the currently selected cell.
     * 
     * @param text
     *            The text.
     * @throws StepExecutionException
     *             If there is no selected cell, or if the cell is not editable,
     *             or if the table cell editor permits the text to be written.
     */
    public void rcInputText(final String text) throws StepExecutionException {
        inputText(text, false, getTableAdapter().getSelectedCell());
    }
    
    /**
     * Types the text in the specified cell.
     * @param text The text
     * @param row The row of the cell.
     * @param rowOperator The row operator
     * @param col The column of the cell.
     * @param colOperator The column operator
     * @throws StepExecutionException If the text input fails
     */
    public void rcInputText(String text, String row, String rowOperator,
            String col, String colOperator)
        throws StepExecutionException {
        //if row is header row
        if (getRowFromStringAbstract(row, rowOperator, 0) == -1) {
            throw new StepExecutionException("Unsupported Header Action", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.UNSUPPORTED_HEADER_ACTION));
        }
        selectCell(row, rowOperator, col, colOperator, 
                ClickOptions.create().setClickCount(1), 
                ValueSets.BinaryChoice.no.rcValue());
        rcInputText(text);
    }
    
    /**
     * Types <code>text</code> into the component. This replaces the shown
     * content in the current selected cell.
     * 
     * @param text the text to type in
     * @throws StepExecutionException
     *  If there is no selected cell, or if the cell is not editable,
     *  or if the table cell editor permits the text to be written.
     */
    public void rcReplaceText(String text) throws StepExecutionException {
        inputText(text, true, getTableAdapter().getSelectedCell());
    }
    
    /**
     * Replaces the given text in the given cell coordinates
     * @param text the text to replace
     * @param row The row of the cell.
     * @param rowOperator The row operator
     * @param col The column of the cell.
     * @param colOperator The column operator
     */
    public void rcReplaceText(String text, String row, String rowOperator,
            String col, String colOperator) {
        // if row is header row
        if (getRowFromStringAbstract(row, rowOperator, 0) == -1) {
            throw new StepExecutionException("Unsupported Header Action", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.UNSUPPORTED_HEADER_ACTION));
        }
        selectCell(row, rowOperator, col, colOperator, 
                ClickOptions.create().setClickCount(1), 
                ValueSets.BinaryChoice.no.rcValue());
        inputText(text, true, getCellAtMousePosition());
    }
    
    /**
     * Drags the cell of the Table.<br>
     * With the xPos, yPos, xunits and yUnits the click position inside the
     * cell can be defined.
     *
     * @param mouseButton the mouseButton.
     * @param modifier the modifier.
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param col the column to select
     * @param colOperator the column header operator
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @throws StepExecutionException
     *             If the row or the column is invalid
     */
    public void rcDragCell(final int mouseButton, final String modifier,
            final String row, final String rowOperator,
            final String col, final String colOperator, final int xPos,
            final String xUnits, final int yPos, final String yUnits)
        throws StepExecutionException {

        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setDragComponent(null);
        rcSelectCell(row, rowOperator, col, colOperator, 0, xPos, xUnits, yPos,
                yUnits, ValueSets.BinaryChoice.no.rcValue(), 1);
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);
    }
    
    /**
     * Drops on the cell of the JTable.<br>
     * With the xPos, yPos, xunits and yUnits the click position inside the
     * cell can be defined.
     *
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param col the column to select
     * @param colOperator the column header operator
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     * @throws StepExecutionException
     *             If the row or the column is invalid
     */
    public void rcDropCell(final String row, final String rowOperator,
            final String col, final String colOperator, final int xPos,
            final String xUnits, final int yPos, final String yUnits,
            int delayBeforeDrop) throws StepExecutionException {

        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        try {
            rcSelectCell(row, rowOperator, col, colOperator, 0, xPos, xUnits,
                    yPos, yUnits, ValueSets.BinaryChoice.no.rcValue(), 1);
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    
    /**
     * Finds the first row which contains the value <code>value</code>
     * in column <code>col</code> and drags this row.
     *
     * @param mouseButton the mouse button
     * @param modifier the modifier
     * @param col the column
     * @param colOperator the column header operator
     * @param value the value
     * @param regexOp the regex operator
     * @param searchType Determines where the search begins ("relative" or "absolute")
     */
    public void rcDragRowByValue(int mouseButton, String modifier, String col,
            String colOperator, final String value, final String regexOp,
            final String searchType) {

        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        rcSelectRowByValue(col, colOperator, value, regexOp, 1,
                ValueSets.BinaryChoice.no.rcValue(), searchType, 1);
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);
    }
    
    /**
     * Finds the first row which contains the value <code>value</code>
     * in column <code>col</code> and drops on this row.
     *
     * @param col the column to select
     * @param colOperator the column header operator
     * @param value the value
     * @param regexOp the regex operator
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     */
    public void rcDropRowByValue(String col, String colOperator,
            final String value, final String regexOp, final String searchType,
            int delayBeforeDrop) {
        
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        try {
            selectRowByValue(col, colOperator, value, regexOp,
                    ValueSets.BinaryChoice.no.rcValue(), 
                    searchType, ClickOptions
                    .create().setClickCount(0));
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    
    /**
     * Finds the first column which contains the value <code>value</code>
     * in the given row and drags the cell.
     *
     * @param mouseButton the mouse button
     * @param modifier the modifiers
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param value the value
     * @param regex search using regex
     * @param searchType Determines where the search begins ("relative" or "absolute")
     */
    public void rcDragCellByColValue(int mouseButton, String modifier,
            String row, String rowOperator, final String value,
            final String regex, final String searchType) {

        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);

        selectCellByColValue(row, rowOperator, value, regex,
                ValueSets.BinaryChoice.no.rcValue(), searchType, 
                ClickOptions.create().setClickCount(0));
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);
    }
    
    /**
     * Finds the first column which contains the value <code>value</code>
     * in the given row and drops on the cell.
     *
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param value the value
     * @param regex search using regex
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     */
    public void rcDropCellByColValue(String row, String rowOperator,
            final String value, final String regex, final String searchType,
            int delayBeforeDrop) {

        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        try {
            selectCellByColValue(row, rowOperator, value, regex, 
                    ValueSets.BinaryChoice.no.rcValue(), searchType, 
                    ClickOptions.create().setClickCount(0));
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    

    /**
     * Gets the text from the specific cell which is given
     * by the row and the column.
     * @param row the zero based index of the row
     * @param column the zero based index of the column
     * @return the text of the cell of the given coordinates
     */
    private String getCellText(final int row, final int column) {
        return getTableAdapter().getCellText(row, column);

    }
        
    /**
     * Checks whether <code>0 <= value < count</code>. 
     * @param value The value to check.
     * @param count The upper bound.
     */
    private void checkBounds(int value, int count) {
        if (value < 0 || value >= count) {
            throw new StepExecutionException("Invalid row/column: " + value, //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_INDEX_OR_HEADER));
        }
    }
    
    /**
     * Checks if the passed row and column are inside the bounds of the Table. 
     * @param row The row
     * @param column The column
     * @throws StepExecutionException If the row or the column is outside of the Table's bounds.
     */
    protected void checkRowColBounds(int row, int column)
        throws StepExecutionException {
        ITableComponent adapter = getTableAdapter();
        checkBounds(row, adapter.getRowCount());
        
        // Corner case: Only check the bounds if the table is not being
        //              used as a list or anything other than the first column
        //              is being checked.
        int colCount = adapter.getColumnCount();
        if (colCount > 0 || column > 0) {
            checkBounds(column, colCount);
        }
    }
    
    /**
     * @param searchType Determines column where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected cell.
     */
    private int getStartingColIndex(String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(SearchType.relative.rcValue())) {
            startingIndex = getTableAdapter().getSelectedCell().getCol() + 1;
        }
        return startingIndex;
    }

    /**
     * @param searchType Determines the row where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected cell.
     */
    private int getStartingRowIndex(String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(SearchType.relative.rcValue())) {
            startingIndex = getTableAdapter().getSelectedCell().getRow() + 1;
        }
        return startingIndex;
    }
    
    /**
     * inputs/replaces the given text
     * @param text the text to input
     * @param replace whether to replace or not
     * @param cell {@link Cell} the location of a Cell
     * @throws StepExecutionException If there is no selected cell,
     * or if the cell is not editable, or if the table cell editor permits
     * the text to be written.
     */
    private void inputText(final String text, boolean replace, Cell cell) 
        throws StepExecutionException {
        ITableComponent adapter = getTableAdapter();

        // Ensure that the cell is visible.
        Rectangle rectangle = 
                adapter.scrollCellToVisible(cell.getRow(), cell.getCol());
        
        Object editor = activateEditor(cell, rectangle);
        editor = setEditorToReplaceMode(editor, replace);
        
        getRobot().type(editor, text);
    }
    
    /**
     * @return true if the mouse pointer is over any cell, false otherwise.
     */
    private boolean isMouseOverCell() {
        try {
            getCellAtMousePosition();
        } catch (StepExecutionException se) {
            return false;
        }
        return true;
    }
    
    /**
     * Sets the specific editor to an replace mode. Means that the next key
     * input will override the complete text of the editor.
     * @param editor 
     * @param replace if <code>true</code> than the editor has to override
     *              the complete text with the next key input. Else the next
     *              key input will append to the end.
     * @return the editor if it changed
     */
    protected abstract Object setEditorToReplaceMode(Object editor,
            boolean replace);
   
    /**
     * Activates the editor of the specific cell.
     * @param cell 
     * @param rectangle 
     * @return the editor of the cell
     */
    protected abstract Object activateEditor(Cell cell, Rectangle rectangle);
    

    /**
     * Gets The modifier for an extended selection (more than one item)
     * @return the modifier
     */
    protected abstract int getExtendSelectionModifier();
    
    /**
     * @return the cell under the current mouse position.
     * @throws StepExecutionException If no cell is found.
     */
    protected abstract Cell getCellAtMousePosition() 
        throws StepExecutionException;

    /**
     * @return the object under the current mouse position.
     * @throws StepExecutionException If no cell is found.
     */
    protected abstract Object getNodeAtMousePosition() 
        throws StepExecutionException;
    
    /**
     * Verifies if mouse is on header.
     * @return true if mouse is on header
     */
    protected abstract boolean isMouseOnHeader();
    
    /** {@inheritDoc} */
    public void rcCheckPropertyAtMousePosition(final String name,
            final String value, final String operator, int timeout) {
        invokeAndWait("rcCheckPropertyAtMousePosition", timeout, //$NON-NLS-1$
            new Runnable() {
                public void run() {
                    final Object cell = getNodeAtMousePosition();
                    final ITableComponent bean = getTableAdapter();
                    final String propToStr =
                            bean.getPropertyValueOfCell(name, cell);
                    Verifier.match(propToStr, value, operator);
                }
            });
    }
    
    /** {@inheritDoc} */
    public String rcStorePropertyValueAtMousePosition(String variableName,
            final String propertyName) {
        return getTableAdapter().getPropertyValueOfCell(propertyName,
                getNodeAtMousePosition());
    }
    
    /** {@inheritDoc} */
    public void rcCheckExistenceOfColumn(final String column,
            final String columnOperator, final boolean exists, int timeout) {
        invokeAndWait("rcCheckExistenceOfColumn", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                boolean existsValue = true;
                try {
                    int index = getColumnFromStringAbstract(
                            column, columnOperator);
                    Rectangle bounds = getTableAdapter().getHeaderBounds(index);
                    if (bounds == null || bounds.getWidth() <= 0) {
                        existsValue = false;
                    }
                } catch (StepExecutionException e) {
                    existsValue = false;
                }
                Verifier.equals(exists, existsValue);
            }
        });
    }
    
    /**
     * Verifies the editable property of the selected cell.
     *
     * @param editable
     *            the editable property to verify.
     * @param timeout the maximum amount of time to wait for the check whether
     *          the given selected column is editable
     */
    public void rcVerifyEditableSelected(boolean editable, int timeout) {
        rcVerifyEditable(editable, timeout);
    }

    /**
     * Gets a column index in a TableComponent
     * @param col the column string
     * @param operator the operator
     * @return the column index
     * @throws StepExecutionException if column cannot be found
     */
    public int getColumnFromStringAbstract(final String col,
            final String operator) {
        try {
            int usrIdxCol = Integer.parseInt(col);
            if (usrIdxCol == 0) {
                usrIdxCol = usrIdxCol + 1;
            }
            return IndexConverter.toImplementationIndex(usrIdxCol);
        } catch (NumberFormatException nfe) {
            // empty
        }
        Boolean isVisible = getEventThreadQueuer().invokeAndWait("getColumnFromString", //$NON-NLS-1$
            new IRunnable<Boolean>() {
                public Boolean run() {
                    return getTableAdapter().isHeaderVisible();
                }
            });
        if (!isVisible) {
            throw new StepExecutionException("No Header", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.NO_HEADER));
        }

        Integer implCol = getEventThreadQueuer().invokeAndWait("getColumnFromString", new IRunnable<Integer>() { //$NON-NLS-1$
            public Integer run() throws StepExecutionException {
                for (int i = 0; i < getTableAdapter().getColumnCount(); i++) {
                    String colHeader = getTableAdapter().getColumnHeaderText(i);
                    if (MatchUtil.getInstance().match(
                            colHeader, col, operator)) {
                        return i;
                    }
                }
                throw new StepExecutionException("Column does not exist", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
            }
        });                
        return implCol;
    }

    /**
     * Converts a row string + operator to index
     * 
     * @param row the row string or number
     * @param operator the operator
     * @param startRowIndex the starting row index. This is used to search
     *            further for a wanted row.
     * @return the row index
     */
    public int getRowFromStringAbstract(final String row, final String operator,
            final int startRowIndex) {
        return getEventThreadQueuer().invokeAndWait("getRowFromString", //$NON-NLS-1$
                new IRunnable<Integer>() {
                    public Integer run() throws StepExecutionException {
                        try {
                            int rowInt = IndexConverter.toImplementationIndex(
                                    Integer.parseInt(row));
                            checkValidIndex(rowInt);
                            // after first loop, error
                            return rowInt;
                        } catch (NumberFormatException nfe) {
                            // nothing
                        }
                        return getRowFromName(row, operator, startRowIndex);
                    }
                });
    }
    
    /**
     * 
     * @param rowInt the integer which should be checked
     * @throws StepExecutionException if the header does not exist or the row index does not exist
     */
    private void checkValidIndex(final int rowInt)
            throws StepExecutionException {
        if (rowInt == -1) {
            if (!getTableAdapter().isHeaderVisible()) {
                throw new StepExecutionException(
                        "Header not visible", //$NON-NLS-1$
                        EventFactory.createActionError(
                                TestErrorEvent.NO_HEADER));
            }
        } else if (!getTableAdapter()
                .doesRowExist(rowInt)) {
            throw new StepExecutionException(
                    "Row with index " + rowInt //$NON-NLS-1$
                            + " does not exist.", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.NOT_FOUND));
        }
    }

    /**
     * 
     * @param row the row name
     * @param operator the row operator
     * @param startRowIndex the starting row index to search further
     * @return the row index
     */
    private int getRowFromName(final String row,
            final String operator, final int startRowIndex) {
        int i = startRowIndex;
        while (getTableAdapter().doesRowExist(i)) {
            String cellTxt = getCellText(i, 0);
            if (MatchUtil.getInstance().match(cellTxt, row,
                    operator)) {
                return i;
            }
            i++;
        }
        if (startRowIndex > 0) {
            throw new StepExecutionException("Row not found", //$NON-NLS-1$
                    EventFactory.createVerifyFailed(
                            "Cell matching all values not found", row, //$NON-NLS-1$
                            operator));
        }
        throw new StepExecutionException("Row does not exist.", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.NOT_FOUND));
    }

}
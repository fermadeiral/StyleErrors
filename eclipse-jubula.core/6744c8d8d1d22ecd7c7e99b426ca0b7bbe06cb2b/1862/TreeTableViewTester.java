/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.AbstractTreeTableTester;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.tester.util.NodeTraverseHelper;
import org.eclipse.jubula.rc.javafx.tester.util.TreeTableOperationContext;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.SearchType;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableView;

/**
 * Tester Class for the <code>TreeTableView</code>. If you are looking for more
 * implemented actions on TreeTables look at <code>TreeTableOperationContext</code>.
 *
 * @author BREDEX GmbH
 * @created 23.06.2014
 */
public class TreeTableViewTester extends AbstractTreeTableTester {

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            TreeTableViewTester.class);

    /**
     * EventHandler to consume scroll events during DnD
     */
    private EventHandler<ScrollToEvent> m_scrollConsumer = 
            new EventHandler<ScrollToEvent>() {
        @Override
        public void handle(ScrollToEvent event) {
            event.consume();
        }
    };

    @Override
    public void rcDragByTextPath(int mouseButton, String modifier,
        String pathType, int preAscend, String treeTextPath, String operator) {
        //Add event filter to prevent scrolling
        Node tree = ((Node) getRealComponent());
        tree.addEventFilter(ScrollToEvent.ANY, m_scrollConsumer);
        super.rcDragByTextPath(mouseButton, modifier, pathType, preAscend,
                treeTextPath, operator);
    }

    @Override
    public void rcDropByTextPath(String pathType, int preAscend,
            String treeTextPath, String operator, int delayBeforeDrop) {
        super.rcDropByTextPath(pathType, preAscend, treeTextPath, operator,
                delayBeforeDrop);
        //Remove event filter after scrolling
        Node tree = ((Node) getRealComponent());
        tree.removeEventFilter(ScrollToEvent.ANY, m_scrollConsumer);
    }

    @Override
    public void rcDragByIndexPath(int mouseButton, String modifier,
            String pathType, int preAscend, String treeIndexPath) {
        // Add event filter to prevent scrolling
        Node tree = ((Node) getRealComponent());
        tree.addEventFilter(ScrollToEvent.ANY, m_scrollConsumer);
        super.rcDragByIndexPath(mouseButton, modifier, pathType, preAscend,
                treeIndexPath);
    }

    @Override
    public void rcDropByIndexPath(String pathType, int preAscend,
            String treeIndexPath, int delayBeforeDrop) {
        super.rcDropByIndexPath(pathType, preAscend, treeIndexPath,
                delayBeforeDrop);
        //Remove event filter after scrolling
        Node tree = ((Node) getRealComponent());
        tree.removeEventFilter(ScrollToEvent.ANY, m_scrollConsumer);
    }

    @Override
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        Point awtPoint = getRobot().getCurrentMousePosition();
        final Point2D point = new Point2D(awtPoint.x, awtPoint.y);
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getNodeAtMousePosition", new Callable<Object>() { //$NON-NLS-1$
                    @Override
                    public Object call() throws Exception {
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        ((TreeTableView<?>) getRealComponent()).layout();

                        List<TreeTableCell> tCells = NodeTraverseHelper
                                .getInstancesOf((Parent) getRealComponent(),
                                        TreeTableCell.class);
                        TreeTableCell resultCell = null;
                        for (TreeTableCell cell : tCells) {
                            //this check is necessary because the parent cell also contains the point
                            if (childCheck(point, resultCell, cell)) {
                                resultCell = cell;
                            }
                        }
                        if (resultCell != null) {
                            return resultCell;
                        }
                        throw new StepExecutionException(
                                "No tree node found at mouse position: " //$NON-NLS-1$
                                        + "X: " + point.getX() //$NON-NLS-1$
                                        + "Y: " + point.getY(), //$NON-NLS-1$
                                EventFactory
                                        .createActionError(
                                                TestErrorEvent.NOT_FOUND));
                    }
                });
        return result;
    }
    
    /**
     * @param point mouse point
     * @param parent supposed parent cell
     * @param child supposed child of parent cell
     * @return <code>true</code> if the child cell contains the point
     *              and the supposed parent is null or the supposed child
     *              is child of supposed parent. Otherwise <code>false</code>.
     */
    private boolean childCheck (Point2D point, TreeTableCell parent,
            TreeTableCell child) {
        
        return NodeBounds.checkIfContains(point, child) && (parent == null
                || child.getTreeTableRow().getTreeItem().getParent()
                    .equals(parent.getTreeTableRow().getTreeItem()));
    }

    /**
     * @return the tree table operation context
     */
    private TreeTableOperationContext getContext() {
        return new TreeTableOperationContext(
                getEventThreadQueuer(), getRobot(),
                (TreeTableView<?>) getRealComponent());
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
     * @param timeout the maximum amount of time to wait for the check to be
     *          performed
     */
    public void rcVerifyEditable(boolean editable, String row,
            String rowOperator, String col, String colOperator, int timeout) {
        TreeTableOperationContext context = getContext();
        if (context.getRowFromString(row, rowOperator) == -1) {
            throw new StepExecutionException("Unsupported Header Action", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.UNSUPPORTED_HEADER_ACTION));
        }        
        selectCell(row, rowOperator, col, colOperator, ClickOptions.create(),
                ValueSets.BinaryChoice.no.rcValue());
        rcVerifyEditableSelected(editable, timeout);
    }
    
    /**
     * Verifies the editable property of the current selected cell.
     *
     * @param editable The editable property to verify.
     * @param timeout the maximum amount of time to wait for the check to be
     *          performed
     */
    public void rcVerifyEditableSelected(final boolean editable,
            int timeout) {
        invokeAndWait("rcVerifyEditableSelected", timeout, new Runnable() { //$NON-NLS-1$
            @Override
            public void run() {
                Cell cell = getContext().getSelectedCell();
                
                Verifier.equals(editable, getContext().isCellEditable(
                        cell.getRow(), cell.getCol()));
            }
        });
    }
    
    /**
     * Verifies the editable property of the cell under the mouse.
     *
     * @param editable The editable property to verify.
     * @param timeout the maximum amount of time to wait for the check to be
     *          performed
     */
    public void rcVerifyEditableMousePosition(boolean editable, int timeout) {
        invokeAndWait("rcVerisfEditableAtMousePosition", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                TreeTableCell<?, ?> cell = 
                        (TreeTableCell<?, ?>)getNodeAtMousePosition();
                Verifier.equals(editable, getContext().isCellEditable(cell));
            }
        });
    }
    
    /**
     * Verifies, whether value exists in row..
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
            final boolean exists, int timeout) throws StepExecutionException {
        invokeAndWait("rcVerifyValueInRow", timeout, new Runnable() { //$NON-NLS-1$
            @Override
            public void run() {
                final TreeTableOperationContext adapter = getContext();
                final int implRow = adapter.getRowFromString(row, rowOperator);
                boolean valueIsExisting = false;
                //if row is header
                if (implRow == -1) {
                    for (int i = getStartingColIndex(searchType); 
                            i < adapter.getColumnCount(); ++i) {
                        if (MatchUtil.getInstance().match(
                                adapter.getColumnHeaderText(i),
                                value, operator)) {
                            valueIsExisting = true;
                            break;
                        }
                    }             
                } else {
                    for (int i = getStartingColIndex(searchType); 
                            i < adapter.getColumnCount(); ++i) {
                        if (MatchUtil.getInstance().match(
                                getCellText(implRow, i), value, operator)) {
                            valueIsExisting = true;
                            break;
                        }
                    }
                }
                Verifier.equals(exists, valueIsExisting);
            }
        });
    }

    /**
     * @param searchType Determines column where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected cell.
     */
    private int getStartingColIndex(String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(SearchType.relative.rcValue())) {
            startingIndex = getContext().getSelectedCell().getCol() + 1;
        }
        return startingIndex;
    }
    
    /**
     * Verifies, whether value exists in column.
     *
     * @param col The column of the cell.
     * @param colOperator the column header operator
     * @param value The cell text to verify.
     * @param operator The operation used to verify
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param exists true if value exists, false otherwise
     * @param timeout the maximum amount of time to wait to verify whether the
     *          value exists in the column
     * @throws StepExecutionException
     *             If the row or the column is invalid, or if the rendered text
     *             cannot be extracted.
     */
    public void rcVerifyValueInColumn(final String col,
            final String colOperator, final String value,
            final String operator, final String searchType,
            final boolean exists, int timeout)
                    throws StepExecutionException {
        invokeAndWait("rcVerifyValueInColumn", timeout, new Runnable() { //$NON-NLS-1$
            @Override
            public void run() {
                TreeTableOperationContext adapter = getContext();
                final int implCol = adapter.getColumnFromString(col,
                        colOperator, true);
                
                boolean valueExists = isValueExisting(adapter, implCol,
                        value, operator, searchType);

                Verifier.equals(exists, valueExists);
            }
        });
    }

    /**
     * Looks if value exists in the Column.
     * 
     * @param adapter the table adapter working on.
     * @param implCol the implementation column of the cell.
     * @param value the cell text to verify.
     * @param operator The operation used to verify.
     * @param searchType searchType Determines where the search begins ("relative" or "absolute")
     * @return <code>true</code> it the value exists in the column
     */
    private boolean isValueExisting(TreeTableOperationContext adapter,
            int implCol, String value, String operator,
            final String searchType) {
        final int rowCount = adapter.getRowCount();
        for (int i = getStartingRowIndex(searchType); i < rowCount; ++i) {
            if (MatchUtil.getInstance().match(getCellText(i, implCol),
                    value, operator)) {
                return true;
            }
        }
        if (adapter.isHeaderVisible()) {
            String header = adapter.getColumnHeaderText(implCol);
            if (MatchUtil.getInstance().match(header, value, operator)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param searchType Determines the row where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected cell.
     */
    private int getStartingRowIndex(String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(SearchType.relative.rcValue())) {
            startingIndex = getContext().getSelectedCell().getRow() + 1;
        }
        return startingIndex;
    }

    /**
     * Verifies the rendered text inside the passed cell.
     * @param row The row of the cell.
     * @param rowOperator The row header operator
     * @param col The column of the cell.
     * @param colOperator The column header operator
     * @param text The cell text to verify.
     * @param operator The operation used to verify
     * @param timeout the maximum amount of time to wait for the text to be
     *          verified
     * @throws StepExecutionException If the row or the column is invalid, or if the rendered text cannot be extracted.
     */
    public void rcVerifyText(String text, String operator, final String row,
            final String rowOperator, final String col,
            final String colOperator, int timeout) 
                    throws StepExecutionException {
        invokeAndWait("rcVerifyText", timeout, new Runnable() { //$NON-NLS-1$
            @Override
            public void run() {
                TreeTableOperationContext adapter = getContext();
                final int implRow = adapter.getRowFromString(row, rowOperator);
                final int implCol = adapter.getColumnFromString(
                        col, colOperator, implRow != -1);
                String current;
                //if row is header and column is existing
                if (implRow == -1 && implCol > -1) {
                    current = adapter.getColumnHeaderText(implCol);        
                } else {
                    checkRowColBounds(implRow, implCol);
                    adapter.scrollCellToVisible(implRow, implCol);
                    current = getCellText(implRow, implCol);
                }
                Verifier.match(current, text, operator);
            }
        });
    }
    
    /**
     * Verifies the text of the cell under the mouse
     * @param txt the text
     * @param operator the operator
     */
    public void rcVerifyCellTextAtMousePosition(String txt, String operator) {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "rcVerifyCellTextAtMousePosition", new Callable<String>() { //$NON-NLS-1$
                    @Override
                    public String call() throws Exception {
                        TreeTableCell<?, ?> cell = 
                                (TreeTableCell<?, ?>)getNodeAtMousePosition();
                        return getContext().getRenderedText(cell);
                    }
                });
        Verifier.match(result, txt, operator);
    }
    
    /**
     * Verifies the text of the cell under the mouse
     * @param txt the text
     * @param operator the operator
     * @param timeout the timeout for the check
     */
    public void rcVerifyCellTextAtMousePosition(String txt, String operator,
            int timeout) {
        invokeAndWait("rcVerifyCellTextAtMousePosition", //$NON-NLS-1$
                timeout, new Runnable() {
                    @Override
                    public void run() {
                        TreeTableCell<?, ?> cell = 
                                (TreeTableCell<?, ?>) getNodeAtMousePosition();
                        String result = getContext().getRenderedText(cell);
                        Verifier.match(result, txt, operator);
                    }
                });
    }
    
    /**
     * Checks if the passed row and column are inside the bounds of the Table. 
     * @param row The row
     * @param column The column
     * @throws StepExecutionException If the row or the column is outside of the Table's bounds.
     */
    protected void checkRowColBounds(int row, int column)
        throws StepExecutionException {
        TreeTableOperationContext adapter = getContext();
        checkBounds(row, adapter.getRowCount());
        checkBounds(column, adapter.getColumnCount());
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
     * Gets the text from the specific cell which is given
     * by the row and the column.
     * @param row the zero based index of the row
     * @param column the zero based index of the column
     * @return the text of the cell of the given coordinates
     */
    private String getCellText(final int row, final int column) {
        return getContext().getCellText(row, column);

    }
    
    /**
     * Action to read the value of the passed cell of the table
     * to store it in a variable in the Client
     * @param variable the name of the variable
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param col the column to select
     * @param colOperator the column header operator
     * @return the text value.
     */
    public String rcReadValue(String variable, String row, String rowOperator,
            String col, String colOperator) {
        TreeTableOperationContext adapter = getContext();
        final int implRow = adapter.getRowFromString(row, rowOperator);
        final int implCol = adapter.getColumnFromString(
                col, colOperator, implRow != -1);
        
        //if row is header and column is existing
        if (implRow == -1 && implCol > -1) {
            return adapter.getColumnHeaderText(implCol); 
        }
        checkRowColBounds(implRow, implCol);
        adapter.scrollCellToVisible(implRow, implCol);
        return getCellText(implRow, implCol);
    }
    
    /**
     * @see {@link TreeTableViewTester#rcReadValue(String, String, String, String, String)}
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param col the column to select
     * @param colOperator the column header operator
     * @return the text value.
     */
    public String rcReadValue(String row, String rowOperator,
            String col, String colOperator) {
        return rcReadValue(null, row, rowOperator, col, colOperator);
    }

    /**
    * Read the value of the cell under the mouse and
    * store it in a variable in the Client
    * @param variable the name of the variable
    * @return the text value.
    */
    public String rcReadValueAtMousePosition(String variable) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "rcVerifyCellTextAtMousePosition", new Callable<String>() { //$NON-NLS-1$
                    @Override
                    public String call() throws Exception {
                        TreeTableCell<?, ?> cell = 
                                (TreeTableCell<?, ?>) getNodeAtMousePosition();
                        return getContext().getRenderedText(cell);
                    }
                });
    }

    /**
    * @see {@link TreeTableViewTester#rcReadValueAtMousePosition(String)}
    * @return the text value.
    */
    public String rcReadValueAtMousePosition() {
        return rcReadValueAtMousePosition(null);
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
    
    /**
     * Selects the cell of the Table.<br>
     * With the xPos, yPos, xUnits and yUnits the click position inside the cell can be defined.
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
        throws StepExecutionException {
        TreeTableOperationContext adapter = getContext();
        final int implRow = adapter.getRowFromString(row, rowOperator);
        final int implCol = adapter.getColumnFromString(col, colOperator,
                implRow != -1);
        final boolean isExtendSelection = extendSelection.equals(
                ValueSets.BinaryChoice.yes.rcValue()); 
        if (log.isDebugEnabled()) {
            log.debug("Selecting row, col: " + row + ", " + col); //$NON-NLS-1$//$NON-NLS-2$
        }
        
        Rectangle cellBounds;
        Object source = getRealComponent();
        //if row is header and col is existing
        if (implRow == -1 && implCol > -1) {
            cellBounds = adapter.getHeaderBounds(implCol);
            source = adapter.getTableHeader();
        } else {
            cellBounds = adapter.scrollCellToVisible(implRow, implCol);
        }        
        ClickOptions clickOptions = ClickOptions.create();
        clickOptions.setClickCount(clickCount).setScrollToVisible(false);
        clickOptions.setMouseButton(button);
        try {
            if (isExtendSelection) {
                getRobot().keyPress(getRealComponent(),
                        getExtendSelectionModifier());
            }
            getRobot().click(source, cellBounds, clickOptions, 
                    xPos, xUnits.equalsIgnoreCase(
                            ValueSets.Unit.pixel.rcValue()), 
                    yPos, yUnits.equalsIgnoreCase(
                            ValueSets.Unit.pixel.rcValue()));
        } finally {
            if (isExtendSelection) {
                getRobot().keyRelease(getRealComponent(),
                        getExtendSelectionModifier());
            }
        }
    }
    
    /**
     * Gets The modifier for an extended selection (more than one item)
     * @return the modifier
     */
    protected int getExtendSelectionModifier() {
        return KeyEvent.VK_CONTROL;
    }
    
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
        final String value, final String regex, final String extendSelection,
        final String searchType, ClickOptions co) { 
        TreeTableOperationContext adapter = getContext();
        final int implRow = adapter.getRowFromString(row, rowOperator);
        int colCount = adapter.getColumnCount();
        Integer implCol = null;
        if (implRow == -1) {
            
            for (int i = getStartingColIndex(searchType); i < colCount; ++i) {
                if (MatchUtil.getInstance().match(
                        adapter.getColumnHeaderText(i), value, regex)) {
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
        
        String usrIdxRowStr = new Integer(IndexConverter.toUserIndex(
                implRow)).toString();
        String usrIdxColStr = new Integer(IndexConverter.toUserIndex(
                implCol.intValue())).toString();
        
        selectCell(usrIdxRowStr, rowOperator, usrIdxColStr, MatchUtil.EQUALS,
                co, extendSelection);
        
    }

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
        TreeTableOperationContext adapter = getContext();
        final int implCol = adapter.getColumnFromString(col, colOperator, true);
        Integer implRow = null;
        final int rowCount = adapter.getRowCount();
        
        for (int i = getStartingRowIndex(searchType); i < rowCount; ++i) {
            if (MatchUtil.getInstance().match(getCellText(i, implCol), 
                    value, regexOp)) {

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
        
        String  userIdxRow = new Integer(IndexConverter.toUserIndex(
                implRow.intValue())).toString();
        String  userIdxCol = new Integer(IndexConverter.toUserIndex(
                implCol)).toString();            
        
        selectCell(userIdxRow, MatchUtil.EQUALS, userIdxCol, colOperator, co,
                extendSelection);
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
            final int xPos, final String xUnits, final int yPos,
            final String yUnits, final String extendSelection)
            throws StepExecutionException {
        if (mouseOnHeader()) {
            throw new StepExecutionException("Unsupported Header Action", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.UNSUPPORTED_HEADER_ACTION));
        }
        Cell currCell = null;
        try {
            TreeTableCell<?, ?> cell = (TreeTableCell<?, ?>) 
                    getNodeAtMousePosition();

            currCell = new Cell(getRowFromCell(cell), getColumnFromCell(cell));
        } catch (StepExecutionException e) {
            currCell = getContext().getSelectedCell();
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
     * Checks if a given column exists, respectively does not exist
     * @param column the column
     * @param columnOperator the operator to find the column
     * @param exists true when the column should be found
     * @param timeout the maximum amount of time to wait for the check whether
     *          the given column exists to be performed
     */
    public void rcCheckExistenceOfColumn(String column, String columnOperator,
            boolean exists, int timeout) {
        invokeAndWait("rcCheckExistenceOfColumn", timeout, new Runnable() {
            public void run() {
                int index = -2;
                try {
                    index = getContext().getColumnFromString(
                            column, columnOperator, true);
                } catch (StepExecutionException see) {
                    // If a column can not be found, an exception is thrown. Because
                    // this is a valid outcome for this method in this context, we
                    // catch the exception.
                    
                    if (exists) {
                        throw see;
                    }
                }
                if (index >= 0) {
                    Rectangle bounds = getContext().getHeaderBounds(index);
                    if (bounds.getWidth() <= 0) {
                        index = -2;
                    }
                }
                Verifier.equals(exists, index >= 0);
            }
        });
    }
    /**
     * get the row index from a given cell
     * @param cell the cell
     * @return the row index or -1 if the cell is not in a tree table
     */
    private int getRowFromCell(TreeTableCell<?, ?> cell) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("get row", //$NON-NLS-1$
                new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        return cell.getTreeTableRow().getIndex();
                    }
                });
    }
    
    /**
     * get the column index from a given cell
     * @param cell the cell
     * @return the column index or -1 if the cell is not in a tree table
     */
    private int getColumnFromCell(TreeTableCell<?, ?> cell) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("get row", //$NON-NLS-1$
                new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        return ((TreeTableView<?>) getRealComponent())
                        .getVisibleLeafColumns()
                        .indexOf(cell.getTableColumn());
                    }
                });
    }

    /**
     * check if the mouse is on the tree table header
     * @return true if the mouse is on the header, false otherwise
     */
    private boolean mouseOnHeader() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("mouse on header", //$NON-NLS-1$
                new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        Point awtPoint = getRobot().getCurrentMousePosition();
                        final Point2D point = new Point2D(awtPoint.x,
                                awtPoint.y);
                        return NodeBounds.checkIfContains(point,
                                (Node) getContext().getTableHeader());
                    }
                });
    }
}

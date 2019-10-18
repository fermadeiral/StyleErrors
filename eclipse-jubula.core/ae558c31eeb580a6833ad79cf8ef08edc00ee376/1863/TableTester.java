/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester;

import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.AbstractTableTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITableComponent;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.adapter.TableAdapter;
import org.eclipse.jubula.rc.javafx.tester.util.AbstractTraverser;
import org.eclipse.jubula.rc.javafx.tester.util.GenericTraverseHelper;
import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.tester.util.NodeTraverseHelper;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.toolkit.enums.ValueSets.SearchType;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

/**
 * Toolkit specific commands for the <code>TableView</code>
 *
 * @author BREDEX GmbH
 * @created 27.11.2013
 */
public class TableTester extends AbstractTableTester {
    /** The AUT Server logger. */
    private static AutServerLogger log = new AutServerLogger(TableTester.class);
    
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
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jubula.rc.common.tester.AbstractTableTester#rcDragCell(int,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, int, java.lang.String, int, java.lang.String)
     */
    @Override
    public void rcDragCell(int mouseButton, String modifier, String row,
            String rowOperator, String col, String colOperator, int xPos,
            String xUnits, int yPos, String yUnits)
            throws StepExecutionException {

        super.rcDragCell(mouseButton, modifier, row, rowOperator, col,
                colOperator, xPos, xUnits, yPos, yUnits);
        //Add event filter to prevent scrolling
        Node table = ((Node) getRealComponent());
        table.addEventFilter(ScrollToEvent.ANY, m_scrollConsumer);
        DragAndDropHelper.getInstance().setDragMode(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jubula.rc.common.tester.AbstractTableTester#rcDropCell(java.
     * lang.String, java.lang.String, java.lang.String, java.lang.String, int,
     * java.lang.String, int, java.lang.String, int)
     */
    @Override
    public void rcDropCell(String row, String rowOperator, String col,
            String colOperator, int xPos, String xUnits, int yPos,
            String yUnits, int delayBeforeDrop) throws StepExecutionException {
        try {
            TableAdapter adapter = (TableAdapter) getComponent();
            int implRow = adapter.getRowFromString(row, rowOperator);
            int implCol = adapter.getColumnFromString(col, colOperator);
            TableCell targetCell = getCellAt(implRow + 1, implCol + 1);
            if (targetCell == null) {
                throw new StepExecutionException("Drop target not visible", //$NON-NLS-1$
                        EventFactory
                                .createActionError(TestErrorEvent.NOT_VISIBLE));
            }
            super.rcDropCell(row, rowOperator, col, colOperator, xPos, xUnits,
                    yPos, yUnits, delayBeforeDrop);
        } finally {
            Node table = ((Node) getRealComponent());
            table.removeEventFilter(ScrollToEvent.ANY, m_scrollConsumer);
            DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            try {
                pressOrReleaseModifiers(dndHelper.getModifier(), false);
            } catch (RobotException e) {
                if (!EnvironmentUtils.isLinuxOS()) {
                    throw e;
                }
            }
            dndHelper.setDragMode(false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jubula.rc.common.tester.AbstractTableTester#rcDragRowByValue(
     * int, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void rcDragRowByValue(int mouseButton, String modifier, String col,
            String colOperator, String value, String regexOp,
            String searchType) {
        super.rcDragRowByValue(mouseButton, modifier, col, colOperator, value,
                regexOp, searchType);
        //Add event filter to prevent scrolling
        Node table = ((Node) getRealComponent());
        table.addEventFilter(ScrollToEvent.ANY, m_scrollConsumer);
        DragAndDropHelper.getInstance().setDragMode(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jubula.rc.common.tester.AbstractTableTester#rcDropRowByValue(
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, int)
     */
    @Override
    public void rcDropRowByValue(String col, String colOperator, String value,
            String regexOp, String searchType, int delayBeforeDrop) {
        try {
            TableAdapter adapter = (TableAdapter) getComponent();
            int implCol = adapter.getColumnFromString(col, colOperator);
            int implRow = super.findRow(value, regexOp, searchType, adapter,
                    implCol);
            TableCell targetCell = getCellAt(implRow + 1, implCol + 1);
            if (targetCell == null) {
                throw new StepExecutionException("Drop target not visible", //$NON-NLS-1$
                        EventFactory
                                .createActionError(TestErrorEvent.NOT_VISIBLE));
            }
            super.rcDropRowByValue(col, colOperator, value, regexOp, searchType,
                    delayBeforeDrop);
        } finally {
            Node table = ((Node) getRealComponent());
            table.removeEventFilter(ScrollToEvent.ANY, m_scrollConsumer);
            DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            try {
                pressOrReleaseModifiers(dndHelper.getModifier(), false);
            } catch (RobotException e) {
                if (!EnvironmentUtils.isLinuxOS()) {
                    throw e;
                }
            }
            dndHelper.setDragMode(false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jubula.rc.common.tester.AbstractTableTester#
     * rcDragCellByColValue(int, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void rcDragCellByColValue(int mouseButton, String modifier,
            String row, String rowOperator, String value, String regex,
            String searchType) {
        super.rcDragCellByColValue(mouseButton, modifier, row, rowOperator,
                value, regex, searchType);
        // Add event filter to prevent scrolling
        Node table = ((Node) getRealComponent());
        table.addEventFilter(ScrollToEvent.ANY, m_scrollConsumer);
        DragAndDropHelper.getInstance().setDragMode(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jubula.rc.common.tester.AbstractTableTester#
     * rcDropCellByColValue(java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public void rcDropCellByColValue(String row, String rowOperator,
            String value, String regex, String searchType,
            int delayBeforeDrop) {
        try {
            TableAdapter adapter = (TableAdapter) getComponent();
            int implRow = adapter.getRowFromString(row, rowOperator);
            int implCol = super.findColumn(value, regex, searchType, adapter,
                    implRow);
            TableCell targetCell = getCellAt(implRow + 1, implCol + 1);
            if (targetCell == null) {
                throw new StepExecutionException("Drop target not visible", //$NON-NLS-1$
                        EventFactory
                                .createActionError(TestErrorEvent.NOT_VISIBLE));
            }
            super.rcDropCellByColValue(row, rowOperator, value, regex,
                    searchType, delayBeforeDrop);
        } finally {
            Node table = ((Node) getRealComponent());
            table.removeEventFilter(ScrollToEvent.ANY, m_scrollConsumer);
            DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            try {
                pressOrReleaseModifiers(dndHelper.getModifier(), false);
            } catch (RobotException e) {
                if (!EnvironmentUtils.isLinuxOS()) {
                    throw e;
                }
            }
            dndHelper.setDragMode(false);
        }
    }

    @Override
    protected Object setEditorToReplaceMode(Object editor, boolean replace) {
        Object returnvalue = editor;
        if (replace) {
            getRobot().clickAtCurrentPosition(editor, 3, 1);
        } else {
            returnvalue = getComponent().getRealComponent();
        }
        return returnvalue;
    }

    @Override
    protected Object activateEditor(Cell cell, Rectangle rectangle) {
        Object table = getComponent().getRealComponent();
        getRobot().click(table, rectangle);
        TableCell<?, ?> realCell = getCellAt(cell.getRow(), cell.getCol());
        // Check if setting the cell in its edit state was successful
        if (realCell.isEditing()) {
            ClickOptions co = ClickOptions.create().setClickCount(2);
            getRobot().click(table, rectangle, co);
        }
        return realCell;
    }

    /**
     * Returns the TableCell at the given position.
     *
     * @param row
     *            the row
     * @param column
     *            the column
     * @return the TableCell at the specified position.
     */
    private TableCell<?, ?> getCellAt(final int row, final int column) {
        TableCell<?, ?> result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getCellText", new Callable<TableCell<?, ?>>() { //$NON-NLS-1$

                    @Override
                    public TableCell<?, ?> call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        table.scrollTo(row);
                        table.scrollToColumnIndex(column);
                        table.layout();
                        TableColumn<?, ?> col = table
                                .getVisibleLeafColumn(column);
                        
                        List<? extends TableCell> tCells = NodeTraverseHelper
                                .getInstancesOf(table, TableCell.class);
                        for (TableCell<?, ?> cell : tCells) {
                            if (cell.getIndex() == row
                                    && cell.getTableColumn() == col
                                    && NodeTraverseHelper.isVisible(cell)) {
                                return cell;
                            }
                        }
                        return null;
                    }

                });
        return result;
    }

    @Override
    protected int getExtendSelectionModifier() {
        return KeyEvent.VK_CONTROL;
    }

    @Override
    protected Cell getCellAtMousePosition() throws StepExecutionException {
        final Point p = getRobot().getCurrentMousePosition();
        Cell result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getCellAtMousePosition", new Callable<Cell>() { //$NON-NLS-1$

                    @Override
                    public Cell call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        table.requestLayout();
                        table.layout();
                        
                        List<? extends TableCell> tCells = NodeTraverseHelper
                                .getInstancesOf(table, TableCell.class);
                        for (TableCell<?, ?> cell : tCells) {
                            if (NodeBounds.checkIfContains(
                                    new Point2D(p.x, p.y), cell)
                                    && cell.getTableView().equals(table)
                                    && NodeTraverseHelper.isVisible(cell)) {
                                TableColumn cellColumn = cell
                                        .getTableColumn();
                                int col = table.getVisibleLeafIndex(cellColumn);
                                return new Cell(cell.getIndex(), col);
                            }
                        }
                        return null;
                    }
                });
        return result;
    }
    
    /**
     * Gets the index path for a given column 
     * @param column the column
     * @param table the table of the column
     * @return the index path
     */
    private String getColumnPath(TableColumnBase column, TableView table) {
        String colPath = ""; //$NON-NLS-1$
        TableColumnBase nxtColumn = column;
        while (nxtColumn.getParentColumn() != null) {
            colPath = String.valueOf(TestDataConstants.
                    PATH_CHAR_DEFAULT).concat(String.
                            valueOf((nxtColumn.getParentColumn()
                            .getColumns()
                            .indexOf(nxtColumn) + 1) + colPath));
            nxtColumn = nxtColumn.getParentColumn();
        }
        colPath = (table.getColumns()
                .indexOf(nxtColumn) + 1) + colPath;
        return colPath;
    }

    @Override
    protected boolean isMouseOnHeader() {
        Point p = getRobot().getCurrentMousePosition();
        final Point2D pos = new Point2D(p.x, p.y);
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getTableHeader", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        table.layout();
                        Parent header = (Parent) table.lookup(
                                TableHeaderRow.class.getSimpleName());
                        return NodeBounds.checkIfContains(pos, header);
                    }
                });
        return result;
    }

    /**
     * Toggles the checkbox in the selected row
     */
    public void rcToggleCheckboxInSelectedRow() {
        int row = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "rcToggleCheckboxInSelectedRow", new Callable<Integer>() { //$NON-NLS-1$

                    @Override
                    public Integer call() throws StepExecutionException {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        return new Integer(table.getSelectionModel()
                                .getSelectedIndex());
                    }
                });
        int column = getIndexOfColumnWithCheckbox(row);
        clickCheckBox(row, column);
    }

    /**
     * @param row the row
     * @return the index of the column containing a checkbox
     */
    private int getIndexOfColumnWithCheckbox(int row) {
        int columnCount = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "rcToggleCheckboxInSelectedRow", new Callable<Integer>() { //$NON-NLS-1$

                    @Override
                    public Integer call() throws StepExecutionException {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        return new Integer(table.getColumns().size());
                    }
                });
        Map<Node, Integer> possibleCheckBoxes = new HashMap<Node, Integer>();
        for (int column = 0; column < columnCount; column++) {
            Node checkBox = getCheckBox(row, column);
            if (checkBox != null) {
                possibleCheckBoxes.put(checkBox, column);            
            }
        }
        switch (possibleCheckBoxes.size()) {
            case 0:
                throw new StepExecutionException(
                        "No checkbox found in selected row", //$NON-NLS-1$
                        EventFactory.createActionError(
                                TestErrorEvent.CHECKBOX_NOT_FOUND));
            case 1: 
                // only one checkbox in row, return its column
                return possibleCheckBoxes.values().iterator().next();
            default:
                throw new StepExecutionException(
                        "Multiple checkboxes found in selected row", //$NON-NLS-1$
                        EventFactory.createActionError(
                                TestErrorEvent.CHECKBOX_NOT_UNIQUE));
        }
    }

    /**
     * Toggles the checkbox under the Mouse Pointer
     * (The "InRow"-part of the name is not precise anymore,
     * as in JavaFX checkboxes can appear not just in the first column.)
     */
    public void rcToggleCheckboxInRowAtMousePosition() {
        Cell cell = getCellAtMousePosition();
        clickCheckBox(cell.getRow(), cell.getCol());
    }

    /**
     * Verifies whether the checkbox in the row of the selected cell is checked.
     * Fails if there are no or more than one checkboxes in the selected row.
     *
     * @param checked
     *            true if checkbox in cell should be selected, false otherwise
     * @param timeout the maximum amount of time to wait for the check whether
     *          the given column the checkbox is selected or not
     * @throws StepExecutionException
     *             If no cell is selected or the verification fails.
     */
    public void rcVerifyCheckboxInSelectedRow(final boolean checked,
            int timeout) throws StepExecutionException {
        invokeAndWait("rcVerifyCheckboxInSelectedRow", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                Cell selectedCell = ((ITableComponent) getComponent())
                        .getSelectedCell();
                int row = selectedCell.getRow();
                int column = getIndexOfColumnWithCheckbox(row);
                verifyCheckboxInRow(checked, row, column);
            }
        });
    }

    /**
     * Verifies whether the checkbox in the row under the mouse pointer is
     * checked
     *
     * @param checked
     *            true if checkbox in cell is selected, false otherwise
     * @param timeout the maximum amount of time to wait to verify whether the
     *          checkbox in the row under the mouse pointer is checked
     */
    public void rcVerifyCheckboxInRowAtMousePosition(boolean checked,
            int timeout) {
        invokeAndWait("rcVerifyCheckboxInRowAtMousePosition", timeout, //$NON-NLS-1$
                new Runnable() {
                    @Override
                    public void run() {
                        Cell cell = getCellAtMousePosition();
                        if (cell != null) {
                            int row = cell.getRow();
                            int column = cell.getCol();
                            verifyCheckboxInRow(checked, row, column);
                        } else {
                            throw new StepExecutionException(
                                    "No checkbox found", //$NON-NLS-1$
                                    EventFactory.createActionError(
                                            TestErrorEvent.CHECKBOX_NOT_FOUND));
                        }
                    }
                });
    }
    
    /**
     * Clicks on the CheckBox in given row and column
     *
     * @param row
     *            the Row
     * @param column
     *            the Column
     */
    private void clickCheckBox(final int row, final int column) {
        Node box = getCheckBox(row, column);
        if (box != null) {
            getRobot().click(box, null,
                    ClickOptions.create().setClickCount(1).setMouseButton(1));
        } else {
            throw new StepExecutionException(
                    "No checkbox found", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.CHECKBOX_NOT_FOUND));
        }
    }

    /**
     * Verifies whether the checkbox in the row with the given
     * <code>index</code> is checked
     *
     * @param checked
     *            true if checkbox in cell is selected, false otherwise
     * @param row
     *            the row-index of the cell in which the checkbox-state should
     *            be verified
     * @param column
     *            the column-index of the cell in which the checkbox-state should
     *            be verified
     */
    private void verifyCheckboxInRow(boolean checked, final int row,
            final int column) {
        final CheckBox box = (CheckBox) getCheckBox(row, column);
        Boolean checkIndex = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "verifyCheckboxInRow", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws StepExecutionException {
                        if (box == null) {
                            throw new StepExecutionException(
                                    "No checkbox found", //$NON-NLS-1$
                                    EventFactory.createActionError(
                                            TestErrorEvent.CHECKBOX_NOT_FOUND));
                        }
                        return box.isSelected();
                    }
                });
        Verifier.equals(checked, checkIndex.booleanValue());
    }

    /**
     * get the CheckBox in the first TableColumn of the given row
     *
     * @param row
     *            the Row
     * @param column
     *            the Column
     * @return the CheckBox or null
     */
    private Node getCheckBox(final int row, final int column) {

        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "clickCheckBoxFirstColumn", new Callable<Node>() { //$NON-NLS-1$

                    @Override
                    public Node call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        table.layout();
                        TableColumn<?, ?> col = table.getVisibleLeafColumn(
                                column);
                        // Check if the CheckBox is realized via a CheckBoxCell
                        List<? extends CheckBoxTableCell> checkboxCells = 
                                NodeTraverseHelper
                                .getInstancesOf(table, CheckBoxTableCell.class);
                        for (CheckBoxTableCell<?, ?> cell : checkboxCells) {
                            if (cell.getTableColumn().equals(col)
                                    && cell.getIndex() == row
                                    && NodeTraverseHelper.isVisible(cell)) {
                                return cell.lookup(CheckBox.class
                                        .getSimpleName());
                            }
                        }
                        // No CheckBoxCell found. Now we have to check all
                        // Cells!
                        List<? extends TableCell> cells = NodeTraverseHelper
                                .getInstancesOf(table, TableCell.class);
                        for (TableCell<?, ?> cell : cells) {
                            if (cell.getTableColumn().equals(col)
                                    && cell.getIndex() == row
                                    && NodeTraverseHelper.isVisible(cell)) {
                                return cell.lookup(CheckBox.class
                                        .getSimpleName());
                            }
                        }
                        return null;
                    }
                });
    }

    @Override
    public void rcVerifyValueInRow(final String row, final String rowOperator,
            final String value, final String operator, final String searchType,
            final boolean exists, int timeout) throws StepExecutionException {
        invokeAndWait("rcVerifyValueInRow", timeout, new Runnable() { //$NON-NLS-1$
            @Override
            public void run() {
                final TableAdapter adapter = (TableAdapter) getComponent();
                final int implRow = adapter.getRowFromString(row, rowOperator);
                // if row is header
                boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                        "rcVerifyValueInRow", new Callable<Boolean>() { //$NON-NLS-1$
                            @Override
                            public Boolean call() throws Exception {
                                boolean valueIsExisting = false;
                                if (implRow == -1) {
                                    valueIsExisting = (getColumnByName(value,
                                            operator, searchType, adapter,
                                            implRow) != null) ? true : false;
                                } else {
                                    valueIsExisting = (getColumnByValue(value,
                                            operator, searchType, adapter,
                                            implRow) != null) ? true : false;
                                }
                                return valueIsExisting;
                            }
                        });
                Verifier.equals(exists, result);
            }
        });
    }
    
    /**
     * Returns the internal index of the first column which has the given name
     * CALL IN JAVAFX-THREAD!
     * @param name the name of the column
     * @param operator the operator
     * @param searchType the searchType
     * @param adapter the adapter class
     * @param implRow the row
     * @return String with the column path or null
     */
    private String getColumnByName(final String name, final String operator,
            final String searchType, final ITableComponent adapter,
            final int implRow) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getColumnByName", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        final int columnCount = adapter.getColumnCount();
                        if (columnCount > 0) {
                            List<TableColumn> columns = getColumnsFromTable(
                                    searchType, adapter);
                            for (TableColumn column : columns) {
                                if (MatchUtil.getInstance().match(
                                        column.getText(), name, operator)) {
                                    return getColumnPath(column,
                                                            (TableView) 
                                                            getRealComponent());
                                }
                            }
                        }
                        return null;
                    }
                });
    }
    
    /**
     * Returns a list of columns. if the search type is "relative" all Columns
     * relative to the current selection.
     * 
     * CALL IN JAVAFX-THREAD!
     * 
     * @param searchType the searchType
     * @param adapter the adapter
     * @return list of columns
     */
    private List<TableColumn> getColumnsFromTable(final String searchType,
            final ITableComponent adapter) {
        ArrayList<TableColumn> columns = new ArrayList<>();
        if (searchType
                .equalsIgnoreCase(SearchType.relative.rcValue())) {
            TableView table = (TableView) adapter.getRealComponent();
            TableColumn selColumn = ((TablePosition) table.getSelectionModel()
                    .getSelectedCells().get(0)).getTableColumn();
            TableColumnBase parCol = selColumn.getParentColumn();
            while (parCol != null) {
                selColumn = (TableColumn) parCol;
                parCol = parCol.getParentColumn();
            }

            columns.addAll(new GenericTraverseHelper<TableColumn, TableColumn>()
                    .getInstancesOf(
                            new AbstractTraverser<TableColumn, TableColumn>(
                                    selColumn) {

                                @Override
                                public Iterable<TableColumn> 
                                    getTraversableData() {
                                    return this.getObject().getColumns();
                                }
                            }, TableColumn.class));
        } else {
            for (TableColumn column : ((TableView<?>) getRealComponent())
                    .getColumns()) {
                columns.addAll(new GenericTraverseHelper
                        <TableColumn, TableColumn>()
                        .getInstancesOf(
                                new AbstractTraverser<TableColumn, TableColumn>(
                                        column) {

                                    @Override
                                    public Iterable<TableColumn> 
                                        getTraversableData() {
                                        return this.getObject().getColumns();
                                    }
                                }, TableColumn.class));
            }
            columns.addAll(((TableView)adapter.getRealComponent()).
                    getColumns());
        }
        return columns;
    }

    /**
     * Returns the internal index of the first column which contains the given value
     * CALL IN JAVAFX-THREAD!
     * @param value the value
     * @param operator the operator
     * @param searchType the searchType
     * @param adapter the adapter class
     * @param implRow the row
     * @return String with the column path or null
     */
    private String getColumnByValue(final String value, final String operator,
            final String searchType, final TableAdapter adapter,
            final int implRow) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getColumnByValue", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() {
                        final int columnCount = adapter.getColumnCount();
                        if (columnCount > 0) {
                            int startIndex = getStartingColIndex(searchType) 
                                    - 1;
                            List<TableColumn> columns = 
                                    ((TableView) getRealComponent())
                                    .getVisibleLeafColumns();
                            for (int i = startIndex; i < columns.size(); i++) {
                                TableColumn column = columns.get(i);
                                int index = adapter.getColumnFromString(
                                        getColumnPath(column,
                                                (TableView) getRealComponent()),
                                                "equals"); //$NON-NLS-1$
                                String cellValue = adapter.
                                        getCellText(implRow, index);
                                if (MatchUtil.getInstance().match(
                                        cellValue,
                                        value,
                                        operator)) {
                                    return getColumnPath(column,
                                            (TableView) getRealComponent());
                                }
                            }
                        }
                        return null;
                    }
                    
                });
    }

    @Override
    public void rcSelectCellByColValue(final String row,
            final String rowOperator, final String value,
            final String operator, int clickCount,
            final String extendSelection, final String searchType,
            final int button) {
        final TableAdapter adapter =
                (TableAdapter) getComponent();
        final int implRow = adapter.getRowFromString(row, rowOperator);
        // if row is header
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "rcSelectCellByColValue", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        if (implRow == -1) {
                            return getColumnByName(value, operator, searchType,
                                    adapter, implRow);
                        }
                        return getColumnByValue(value, operator,
                                searchType, adapter, implRow);
                    }
                });
        if (result == null) {
            throw new StepExecutionException("no such cell found", EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.NOT_FOUND));
        }
        rcSelectCell(row, rowOperator, result, operator, clickCount, 50,
                ValueSets.Unit.percent.rcValue(), 50,
                ValueSets.Unit.percent.rcValue(), extendSelection, button);
    }
    
    /**
     * @param searchType Determines column where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected cell.
     */
    private int getStartingColIndex(String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(SearchType.relative.rcValue())) {
            Cell c = ((ITableComponent)getComponent())
                    .getSelectedCell();
            if (c == null) {
                throw new StepExecutionException("No selection found", //$NON-NLS-1$
                        EventFactory
                                .createActionError(TestErrorEvent.
                                        NO_SELECTION));
            }
            startingIndex = c.getCol();
        }
        return startingIndex + 1;
    }

    /** {@inheritDoc} */
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        Point awtPoint = getRobot().getCurrentMousePosition();
        final Point2D point = new Point2D(awtPoint.x, awtPoint.y);
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getNodeAtMousePosition", new Callable<Object>() { //$NON-NLS-1$
                    @Override
                    public Object call() throws Exception {
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        ((TableView<?>) getRealComponent()).layout();

                        List<TableCell> tCells = NodeTraverseHelper
                                .getInstancesOf((Parent) getRealComponent(),
                                        TableCell.class);
                        for (TableCell cell : tCells) {
                            if (NodeBounds.checkIfContains(point, cell)) {
                                return cell;
                            }
                        }
                        throw new StepExecutionException(
                                "No table node found at mouse position: " //$NON-NLS-1$
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
     * JavaFX-specific method, to deal with nested columns.
     * @param col the column(path) string
     * @param operator the operator to use
     * @return the column index
     */
    public int getColumnFromStringAbstract(final String col,
            final String operator) {
        return ((TableAdapter) getComponent()).
                getColumnFromString(col, operator);
    }

}

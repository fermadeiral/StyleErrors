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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITableComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextComponent;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.util.AbstractTraverser;
import org.eclipse.jubula.rc.javafx.tester.util.GenericTraverseHelper;
import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.tester.util.NodeTraverseHelper;
import org.eclipse.jubula.rc.javafx.tester.util.Rounding;
import org.eclipse.jubula.rc.javafx.tester.util.compatibility.TableUtils;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.StringParsing;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

/**
 * Adapter for a TableView(Table)
 *
 * @author BREDEX GmbH
 * @created 7.11.2013
 */
public class TableAdapter extends JavaFXComponentAdapter<TableView<?>> 
                          implements ITableComponent<TableCell<?, ?>> {

    /**
     * Workaround to support nested Columns without modifying classes which would
     * affect other toolkits
     **/
    private List<TableColumn> m_columns = new ArrayList<TableColumn>();
    /**
     * Creates an adapter for a TableView.
     *
     * @param objectToAdapt
     *            the object which needed to be adapted
     */
    public TableAdapter(TableView<?> objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public String getText() {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait("getText", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        ObservableList<?> sCells = getRealComponent()
                                .getSelectionModel().getSelectedCells();
                        if (!sCells.isEmpty()) {
                            TablePosition<?, ?> pos =
                                    (TablePosition<?, ?>) sCells.get(0);
                            return getCellText(pos.getRow(), pos.getColumn());
                        }
                        throw new StepExecutionException("No selection found", //$NON-NLS-1$
                                EventFactory
                                        .createActionError(TestErrorEvent.
                                                NO_SELECTION));
                    }
                });
        return result;
    }
    
    @Override
    public int getColumnCount() {
        int result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getColumnCount", new Callable<Integer>() { //$NON-NLS-1$

                    @Override
                    public Integer call() throws Exception {
                        int counter = 0;
                        for (TableColumn<?, ?> column : getRealComponent()
                                .getColumns()) {
                            counter += new GenericTraverseHelper
                                    <TableColumn, TableColumn>()
                                    .getInstancesOf(
                                            new AbstractTraverser
                                            <TableColumn, TableColumn>(
                                                    column) {

                                                @Override
                                                public Iterable<TableColumn> 
                                                    getTraversableData() {
                                                    return this.getObject()
                                                            .getColumns();
                                                }
                                            }, TableColumn.class).size();
                        }
                        return counter + getRealComponent().getColumns().size();
                    }
                });
        return result;
    }

    @Override
    public int getRowCount() {
        int result = EventThreadQueuerJavaFXImpl.invokeAndWait("getRowCount", //$NON-NLS-1$
                new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        return getRealComponent().getItems().size();
                    }
                });
        return result;
    }

    @Override
    public String getCellText(final int row, final int column) {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait("getCellText", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        TableView table = getRealComponent();
                        TableColumn<?, ?> col = null;
                        if (m_columns.size() == 0) {
                            col = table.getVisibleLeafColumn(column);
                        } else {
                            col = m_columns.get(column);
                        }
                        table.scrollTo(row);
                        table.scrollToColumn(col);
                        table.layout();
                        List<? extends TableCell> tCells = NodeTraverseHelper
                                .getInstancesOf(table, TableCell.class);
                        for (TableCell<?, ?> cell : tCells) {
                            if (cell.getIndex() == row
                                    && cell.getTableColumn() == col
                                    && cell.getTableView() == table
                                    && NodeTraverseHelper.isVisible(cell)) {
                                IComponent adapter = (IComponent) 
                                        AdapterFactoryRegistry.getInstance()
                                        .getAdapter(IComponent.class, cell);
                                if (adapter != null
                                        && adapter instanceof ITextComponent) {
                                    return ((ITextComponent) adapter).getText();
                                }
                            }
                        }
                        return null;
                    }
                });
        return result;
    }

    @Override
    public String getColumnHeaderText(final int column) {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getColumnHeaderText", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        if (m_columns.size() > 0) {
                            TableColumn<?, ?> tCol = m_columns.get(column);
                            return tCol.getText();
                        }
                        TableColumn<?, ?> tCol = getRealComponent().
                                getVisibleLeafColumn(column);
                        return tCol.getText();
                    }
                });
        return result;
    }

    /**
     * Gets column index from string with header name or index
     * 
     * @param colPath Column index or path
     * @param op the operation used to verify
     * @return column index
     */
    public int getColumnFromString(final String colPath, final String op) {
        Integer result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getColumnFromString", new Callable<Integer>() { //$NON-NLS-1$

                    @Override
                    public Integer call() throws Exception {
                        TableView table = getRealComponent();
                        List<String> path = StringParsing.splitToList(colPath,
                                TestDataConstants.PATH_CHAR_DEFAULT,
                                TestDataConstants.ESCAPE_CHAR_DEFAULT, false);
                        TableColumn<?, ?> column = determineColumn(colPath, op,
                                table, path); 
                        if (column == null) {
                            return -2;
                        }
                        if (table.getVisibleLeafColumns().contains(column)) {
                            return table.getVisibleLeafColumns().
                                    indexOf(column);
                        }
                        m_columns.add(column);
                        return m_columns.indexOf(column); 
                    }
                });
        return result.intValue();
    }
    
    /**
     * get column for the given path
     * @param colPath the path
     * @param op the operation
     * @param table the table
     * @param path the path as list
     * @return the column or null if no column was found
     */
    private TableColumn<?, ?> determineColumn(final String colPath,
            final String op, TableView table, List<String> path) {
        ObservableList<TableColumn> columns;
        if (colPath.contains("" + TestDataConstants.PATH_CHAR_DEFAULT)) { //$NON-NLS-1$
            columns = table.getColumns();
        } else {
            columns = table.getVisibleLeafColumns();
        }
        Iterator<String> pathIterator = path.iterator();
        String currCol = null;
        TableColumn<?, ?> column = null;
        pathIteration: while (pathIterator.hasNext()) {
            try {
                currCol = pathIterator.next();
                int usrIdxCol = Integer.parseInt(currCol);
                if (usrIdxCol == 0) {
                    usrIdxCol = usrIdxCol + 1;
                }
                int i = IndexConverter.toImplementationIndex(usrIdxCol);
                if (MatchUtil.NOT_EQUALS == op) {
                    for (int j = 0; j < columns.size(); j++) {
                        if (j != i) {
                            if (pathIterator.hasNext()) {
                                columns = columns.get(j).getColumns();
                            } else {
                                column = columns.get(j);
                            }
                        }
                    }
                } else {
                    try {
                        if (pathIterator.hasNext()) {
                            columns = columns.get(i).getColumns();
                        } else {
                            column = columns.get(i);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        return null;
                    }
                }
            } catch (NumberFormatException nfe) {
                try {
                    if (path.size() <= 1) {
                        columns = table.getColumns();
                    }
                    if (columns.size() <= 0) {
                        throw new StepExecutionException(
                                "No Columns", EventFactory.createActionError(//$NON-NLS-1$
                                        TestErrorEvent.NO_HEADER));
                    }
                    for (TableColumn c : columns) {
                        String h = c.getText();
                        if (MatchUtil.getInstance().match(h, currCol, op)) {
                            column = c;
                            if (pathIterator.hasNext()) {
                                columns = c.getColumns();
                            }
                            continue pathIteration;
                        }
                    }
                    return null;
                } catch (IllegalArgumentException iae) {
                    // do nothing here
                }
            }
        }
        return column;
    }

    @Override
    public String getRowText(int row) {
        // TableView does not act like lists
        return null;
    }

    /**
     * Gets row index from string with index or text of first row
     * 
     * @param row
     *            index or value in first col
     * @param operator
     *            the operation used to verify
     * @return integer of String of row
     */
    public int getRowFromString(final String row, final String operator) {
        Integer result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getRowFromString", new Callable<Integer>() { //$NON-NLS-1$
                    @Override
                    public Integer call() throws Exception {
                        Integer rowInt = null;
                        TableView<?> table = getRealComponent();
                        try {
                            rowInt = IndexConverter
                                    .toImplementationIndex(Integer
                                            .parseInt(row));
                            if (rowInt == -1) {
                                if (table.getColumns().size() <= 0) {
                                    throw new StepExecutionException(
                                            "No Header", //$NON-NLS-1$
                                            EventFactory
                                                    .createActionError(
                                                            TestErrorEvent.
                                                            NO_HEADER));
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            for (int i = 0; i < table.getItems().size(); i++) {
                                String cellTxt = getCellText(i, 0);
                                if (MatchUtil.getInstance().match(cellTxt, row,
                                        operator)) {
                                    return new Integer(i);
                                }
                            }
                        }
                        if (rowInt == null) {
                            throw new StepExecutionException(
                                "Row not found", //$NON-NLS-1$
                                EventFactory
                                        .createActionError(TestErrorEvent.
                                                NOT_FOUND));
                        }
                        return rowInt;
                    }
                });
        return result.intValue();
    }

    @Override
    public Rectangle getHeaderBounds(final int column) {
        Rectangle result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getHeaderBounds", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TableView table = getRealComponent();
                        TableColumn<?, ?> col;
                        if (m_columns.size() > 0) {
                            col = m_columns.get(column);
                        } else {
                            col = getRealComponent().
                                getVisibleLeafColumn(column);
                        }
                        table.scrollToColumn(col);
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        return TableUtils.getNodeBoundsofHeader(table, column,
                                true);
                    }
                });
        return result;
    }

    @Override
    public Cell getSelectedCell() throws StepExecutionException {
        Cell result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getSelectedCell", new Callable<Cell>() { //$NON-NLS-1$

                    @Override
                    public Cell call() throws StepExecutionException {
                        TableView<?> table = getRealComponent();
                        ObservableList<?> list = table
                                .getSelectionModel().getSelectedCells();

                        if (list.size() > 0) {
                            TablePosition<?, ?> pos = null;
                            for (Object object : list) {
                                TablePosition<?, ?> curr =
                                        (TablePosition<?, ?>) object;
                                if (curr.getRow() == table.getSelectionModel()
                                        .getSelectedIndex()) {
                                    pos = curr;
                                    break;
                                }
                            }
                            if (pos != null) {
                                return new Cell(pos.getRow(), pos.getColumn());
                            }
                        }
                        throw new StepExecutionException("No selection found", //$NON-NLS-1$
                                EventFactory
                                .createActionError(TestErrorEvent.
                                        NO_SELECTION));
                    }
                });
        return result;
    }

    @Override
    public boolean isHeaderVisible() {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isHeaderVisible", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        Pane header = (Pane) getRealComponent().lookup(
                                "TableHeaderRow"); //$NON-NLS-1$
                        if (header != null) {
                            return header.isVisible();
                        }
                        return false;
                    }
                });
        return result;
    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isCellEditable", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        TableView table = getRealComponent();
                        if (table.isEditable()) {
                            TableColumn<?, ?> col = null;
                            if (m_columns.size() == 0) {
                                col = table.getVisibleLeafColumn(column);
                            } else {
                                col = m_columns.get(column);
                            }
                            if (col.isEditable()) {
                                table.scrollTo(row);
                                table.scrollToColumn(col);
                                table.layout();
                                List<? extends TableCell> tCells = 
                                        NodeTraverseHelper
                                        .getInstancesOf(table, TableCell.class);
                                for (TableCell<?, ?> cell : tCells) {
                                    if (cell.getIndex() == row
                                            && cell.getTableColumn() == col
                                            && cell.getTableView() == table
                                            && NodeTraverseHelper
                                            .isVisible(cell)) {
                                        return cell.isEditable();
                                    }
                                }
                            }
                        }
                        return false;
                    }
                });
        return result;
    }

    @Override
    public boolean hasCellSelection() {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "hasCellSelection", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        TableView<?> table = getRealComponent();

                        return table.getSelectionModel().getSelectedCells()
                                .size() > 0;
                    }
                });
        return result;
    }

    @Override
    public Rectangle scrollCellToVisible(final int row, final int column)
        throws StepExecutionException {
        Rectangle result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "scrollCellToVisible", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TableView table = getRealComponent();
                        TableColumn<?, ?> col = null;
                        if (m_columns.size() == 0) {
                            col = table.getVisibleLeafColumn(column);
                        } else {
                            col = m_columns.get(column);
                        }

                        table.scrollTo(row);
                        table.scrollToColumn(col);
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        table.layout();
                        List<? extends TableCell> tCells = NodeTraverseHelper.
                                getInstancesOf(table, TableCell.class);
                        for (TableCell<?, ?> cell : tCells) {
                            if (cell.getIndex() == row
                                    && cell.getTableColumn() == col
                                    && cell.getTableView() == table) {

                                Rectangle b = NodeBounds
                                        .getAbsoluteBounds(cell);
                                Rectangle tableB = NodeBounds
                                        .getAbsoluteBounds(table);
                                return new Rectangle(
                                        Math.abs(tableB.x - b.x),
                                        Math.abs(tableB.y - b.y),
                                        Rounding.round(b.getWidth()),
                                        Rounding.round(b.getHeight()));
                            }
                        }
                        return null;
                    }
                });
        return result;
    }

    @Override
    public Object getTableHeader() {
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getTableHeader", new Callable<Object>() { //$NON-NLS-1$

                    @Override
                    public Object call() throws Exception {
                        return getRealComponent().lookup("TableHeaderRow"); //$NON-NLS-1$
                    }
                });
        return result;
    }


    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(String name, TableCell<?, ?> cell) {
        Object prop = EventThreadQueuerJavaFXImpl.invokeAndWait("getProperty", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        try {
                            IComponent adapter = (IComponent) 
                                    AdapterFactoryRegistry.getInstance()
                                    .getAdapter(IComponent.class, cell);
                            if (adapter != null
                                    && adapter instanceof ITextComponent) {
                                return ((ITextComponent) adapter)
                                        .getPropteryValue(name);
                            }
                            return null;
                        } catch (RobotException e) {
                            throw new StepExecutionException(e.getMessage(),
                                    EventFactory.createActionError(
                                            TestErrorEvent
                                            .PROPERTY_NOT_ACCESSABLE));
                        }
                    }
                });
        return String.valueOf(prop);
    }

    /** {@inheritDoc} */
    public boolean doesRowExist(int row) {
        return row >= 0 && row < getRowCount();
    }

    /** {@inheritDoc} */
    public int getTopIndex() {
        throw new UnsupportedOperationException("JavaFX table adapter does not implement getTopIndex."); //$NON-NLS-1$
        // and we don't need it currently...
    }

    /** {@inheritDoc} */
    public Rectangle getCellBounds(int row, int col, boolean restr) {
        throw new UnsupportedOperationException("JavaFX table adapter does not implement getCellBounds."); //$NON-NLS-1$
        // and we don't need it currently...
    }
}

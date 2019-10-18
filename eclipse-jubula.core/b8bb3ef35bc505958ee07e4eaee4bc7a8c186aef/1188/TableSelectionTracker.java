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
package org.eclipse.jubula.rc.swt.listener;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


/**
 * Tracks the currently selected cell (or row, if no column can be determined) 
 * for each Table.
 *
 * @author BREDEX GmbH
 * @created Oct 15, 2008
 */
public class TableSelectionTracker implements BaseAUTListener, Listener {

    /** the singular instance */
    private static TableSelectionTracker instance;
    
    /** 
     * Table => Cell
     * Based on events received from Tables.
     * This mapping is checked only if no mapping exists based on a TableCursor
     * because this mapping only contains a row (no column). 
     */
    private Map m_tableToSelection = new HashMap();
    
    /** 
     * Table => Cell 
     * Based on event received from TableCursors.
     * This mapping is always checked first because it contains
     * more specific information (column).
     */
    private Map m_tableToCursorSelection = new HashMap();

    /**
     * TableCursor => Table
     * Allows us to determine when to remove a mapping from 
     * <code>m_tableToCursorSelection</code>.
     */
    private Map m_cursorToTable = new HashMap();
    
    /**
     * Private constructor for singleton
     */
    private TableSelectionTracker() {
        // Nothing to initialize
    }

    /**
     * 
     * @return the single instance.
     */
    public static TableSelectionTracker getInstance() {
        if (instance == null) {
            instance = new TableSelectionTracker();
        }
        
        return instance;
    }

    /**
     * <strong>Must be called from the UI thread.</strong>
     * 
     * @param table The table for which to find the selected cell.
     * @return the currently selected cell in <code>table</code>. If the column
     *         of the selection cannot be determined (i.e. because only 
     *         selection of an entire row is supported), then the column index
     *         for the returned cell will be <code>0</code>.
     *          
     * @throws StepExecutionException if <code>table</code> has no selection.
     */
    public Cell getSelectedCell(Table table) throws StepExecutionException {
        // First try to get the selection from the TableCursor, as this
        // also contains a column index.
        Cell selectedCell = (Cell)m_tableToCursorSelection.get(table);
        if (selectedCell == null) {
            // Try finding the selected cell based on the currently focused
            // component. If the currently focused component lies
            // within the bounds of the table, then we assume that the component
            // is a cell editor. We then take the center point of the component
            // and try to determine which cell is being edited. We consider this
            // edited cell as selected.
            Display d = table.getDisplay();
            Control focusControl = d.getFocusControl();
            if (focusControl != null) {
                Rectangle bounds = d.map(focusControl.getParent(), null, 
                        focusControl.getBounds());
                Point center = new Point(
                        bounds.x + (bounds.width / 2), 
                        bounds.y + (bounds.height / 2));
                if (d.map(table.getParent(), null, table.getBounds())
                        .contains(center)) {
                    
                    for (int row = 0; row < table.getItemCount() 
                            && selectedCell == null; row++) {
                        for (int col = 0; col < table.getColumnCount()
                                && selectedCell == null; col++) {
                            
                            if (d.map(table, null, table.getItem(row)
                                    .getBounds(col)).contains(center)) {
                                selectedCell = new Cell(row, col);
                            }
                        }
                    }
                }
            }
        }
        if (selectedCell == null) {
            // As a fallback, use the selection of the Table itself, which
            // only provides a row index.
            // This fallback will be used whenever the given table does not
            // use a TableCursor.
            selectedCell = (Cell)m_tableToSelection.get(table);
        }
        
        if (selectedCell == null) {
            throw new StepExecutionException("No table cell selected.", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.NO_SELECTION));
        }

        return selectedCell;
    }
    
    /**
     * {@inheritDoc}
     */
    public long[] getEventMask() {
        return new long [] {SWT.Selection, SWT.Dispose, SWT.Move};
    }

    /**
     * {@inheritDoc}
     */
    public void handleEvent(Event event) {
        if ((event.type == SWT.Selection || event.type == SWT.DefaultSelection) 
                && event.widget instanceof Table) {
            handleTableSelection((Table)event.widget);
        } else if (event.type == SWT.Dispose) {
            handleDisposeEvent(event);
        } else if (event.type == SWT.Move 
                && event.widget instanceof TableCursor) {
            handleTableCursorMoved((TableCursor)event.widget);
        }
    }

    /**
     * @param tableCursor The TableCursor that has moved.
     */
    private void handleTableCursorMoved(TableCursor tableCursor) {
        TableItem selectedRow = tableCursor.getRow();
        
        if (selectedRow != null) {
            // Something is selected
            Table table = selectedRow.getParent();
            int selectedRowIdx = table.indexOf(selectedRow);
            int selectedColIdx = tableCursor.getColumn();

            m_tableToCursorSelection.put(table, 
                    new Cell(selectedRowIdx, selectedColIdx));
            m_cursorToTable.put(tableCursor, table);
        } else {
            // No selection
            m_tableToCursorSelection.remove(m_cursorToTable.get(tableCursor));
            m_cursorToTable.remove(tableCursor);
        }
    }

    /**
     * @param event The dispose event to handle.
     */
    private void handleDisposeEvent(Event event) {
        if (event.widget instanceof Table) {
            m_tableToSelection.remove(event.widget);
            m_tableToCursorSelection.remove(event.widget);
        } else if (event.widget instanceof TableCursor) {
            TableCursor eventCursor = (TableCursor)event.widget;
            m_tableToCursorSelection.remove(m_cursorToTable.get(eventCursor));
            m_cursorToTable.remove(eventCursor);
        }
    }

    /**
     * @param table The table for which the selection has changed.
     */
    private void handleTableSelection(Table table) {
        int selectionIndex = table.getSelectionIndex();
        if (selectionIndex != -1) {
            // Something is selected
            // Since Tables themselves only support selection of entire rows
            // (rather than individual cells), we consider the first cell as
            // selected.
            m_tableToSelection.put(table, new Cell(selectionIndex, 0));
        } else {
            // No selection
            m_tableToSelection.remove(table);
        }
        
    }

}

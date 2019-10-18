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
package org.eclipse.jubula.rc.swt.tester.adapter;

import java.awt.Rectangle;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITableComponent;
import org.eclipse.jubula.rc.swt.components.ISWTTableComponent;
import org.eclipse.jubula.rc.swt.listener.TableSelectionTracker;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.utils.SwtPointUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
/**
 * Implements the Table interface for adapting a <code>SWT.Table</code>
 * 
 * @author BREDEX GmbH
 */
public class TableAdapter extends ControlAdapter
    implements ITableComponent, ISWTTableComponent {
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        TableAdapter.class);
    
    /** the table */
    private Table m_table;
    
    /**
     * 
     * @param objectToAdapt graphics component which will be adapted
     */
    public TableAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_table = (Table) objectToAdapt;
    }

    /** {@inheritDoc} */
    public int getColumnCount() {
        return getEventThreadQueuer().invokeAndWait(
                "getColumnCount", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() {
                        return m_table.getColumnCount();
                    }
                });
    }

    /** {@inheritDoc} */
    public int getRowCount() {
        return getEventThreadQueuer().invokeAndWait(
                "getRowCount", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() {
                        return m_table.getItemCount();
                    }
                });
    }

    /** {@inheritDoc} */
    public String getCellText(final int row, final int column) {
        return getEventThreadQueuer().invokeAndWait("getCellText", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() {
                        final TableItem item = m_table.getItem(row);
                        String value = CAPUtil.getWidgetText(item,
                                SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX
                                        + column, item.getText(column));
                        if (log.isDebugEnabled()) {
                            log.debug("Getting cell text:"); //$NON-NLS-1$
                            log.debug("Row, col: " + row + ", " + column); //$NON-NLS-1$ //$NON-NLS-2$
                            log.debug("Value: " + value); //$NON-NLS-1$
                        }
                        return value;
                    }
                });
    }

    /** {@inheritDoc} */
    public String getColumnHeaderText(final int colIdx) {
        return getEventThreadQueuer().invokeAndWait("getColumnName", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() {
                        final TableColumn column = m_table.getColumn(colIdx);
                        return CAPUtil.getWidgetText(column, column.getText());
                    }
                });
    }

    /** {@inheritDoc} */
    public String getRowText(final int rowIdx) {
        return getEventThreadQueuer().invokeAndWait("getRowText", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() {
                        final TableItem row = m_table.getItem(rowIdx);
                        return CAPUtil.getWidgetText(row, row.getText());
                    }
                });
    }

    /** {@inheritDoc} */
    public Rectangle getBounds() {
        return getEventThreadQueuer().invokeAndWait("getBounds", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() throws StepExecutionException {
                        org.eclipse.swt.graphics.Rectangle rect = m_table
                                .getBounds();
                        return new Rectangle(rect.x, rect.y, rect.width,
                                rect.height);
                    }
                });
    }

    /** {@inheritDoc} */
    public Rectangle getHeaderBounds(final int col) {
        return getEventThreadQueuer().invokeAndWait("getHeaderBounds", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() throws StepExecutionException {
                        org.eclipse.swt.graphics.Rectangle rect = m_table
                                .getItem(0).getBounds(col);
                        rect.y = m_table.getClientArea().y;
                        return new Rectangle(rect.x, rect.y, rect.width,
                                rect.height);
                    }
                });
    }

    /** {@inheritDoc} */
    public Cell getSelectedCell() throws StepExecutionException {
        return getEventThreadQueuer().invokeAndWait(
                "getSelectedSell", new IRunnable<Cell>() { //$NON-NLS-1$         
                    public Cell run() throws StepExecutionException {
                        return TableSelectionTracker.getInstance()
                                .getSelectedCell(m_table);
                    }
                });
    } 

    /** {@inheritDoc} */
    public boolean isHeaderVisible() {
        return getEventThreadQueuer().invokeAndWait("isHeaderVisible", //$NON-NLS-1$
                new IRunnable<Boolean>() {
                    public Boolean run() {
                        return m_table.getHeaderVisible();
                    }
                });
    }

    /** {@inheritDoc} */
    public boolean isCellEditable(final int row, final int col) {
        final Control cellEditor = (Control) 
                activateEditor(new Cell(row, col));
        return invokeIsEditable(cellEditor);
    }

    /** {@inheritDoc} */
    public boolean hasCellSelection() {
        TableItem[] selItems = getEventThreadQueuer()
                .invokeAndWait("hasCellSelection", //$NON-NLS-1$
                    new IRunnable<TableItem[]>() {
                        public TableItem[] run() {
                            return m_table.getSelection();
                        }
                    });
        return selItems.length > 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public Rectangle scrollCellToVisible(final int row, final int col)
        throws StepExecutionException {
        final Table table = m_table;
        getEventThreadQueuer().invokeAndWait("scrollCellToVisible", //$NON-NLS-1$
                new IRunnable<Void>() {
                    public Void run() {
                        if (table.getColumnCount() > 0 || col > 0) {
                            table.showColumn(table.getColumn(col));
                        }
                        table.showItem(table.getItem(row));
                        return null;
                    }
                });

        checkRowColBounds(row, col);
        final org.eclipse.swt.graphics.Rectangle cBoundsRelToParent = 
                SwtPointUtil.toSwtRectangle(getCellBounds(row, col, true));

        getEventThreadQueuer().invokeAndWait("getCellBoundsRelativeToParent", //$NON-NLS-1$
                new IRunnable<Void>() {
                    public Void run() {
                        org.eclipse.swt.graphics.Point cOriginRelToParent = 
                                table.getDisplay().map(table,
                                        table.getParent(),
                                        new org.eclipse.swt.graphics.Point(
                                                cBoundsRelToParent.x,
                                                cBoundsRelToParent.y));
                        cBoundsRelToParent.x = cOriginRelToParent.x;
                        cBoundsRelToParent.y = cOriginRelToParent.y;
                        return null;
                    }
                });

        Control parent = getEventThreadQueuer().invokeAndWait("getParent", //$NON-NLS-1$
                new IRunnable<Control>() {
                    public Control run() {
                        return table.getParent();
                    }
                });
            
        getRobot().scrollToVisible(parent, cBoundsRelToParent);
        return getVisibleBounds(getCellBounds(row, col, true));
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
        checkBounds(row, getRowCount());
        
        // Corner case: Only check the bounds if the table is not being
        //              used as a list or anything other than the first column
        //              is being checked.
        int colCount = getColumnCount();
        if (colCount > 0 || column > 0) {
            checkBounds(column, colCount);
        }
    }
    
    /**
     * Computes the visible cellBounds inside the visible bounds of the table.<br>
     * The result is the intersection of the visible bounds of the table and the 
     * bounds of the cell.
     * @param cellBounds the bounds of the cell to click in. These bounds must
     *                  be relative to the table's location.
     * @return the visible cell bounds, relative to the table's location.
     */
    private Rectangle getVisibleBounds(Rectangle cellBounds) {
        org.eclipse.swt.graphics.Rectangle r = getEventThreadQueuer()
                .invokeAndWait("getVisibleCellBounds: " + cellBounds, //$NON-NLS-1$
                        new IRunnable<org.eclipse.swt.graphics.Rectangle>() {
                            public org.eclipse.swt.graphics.Rectangle run() {
                                return m_table.getClientArea();
                            }
                        });
        
        Rectangle visibleTableBounds = new Rectangle(
            r.x, r.y, r.width, r.height);
        Rectangle visibleCellBounds = 
            visibleTableBounds.intersection(cellBounds);
        return visibleCellBounds;
    }
    
    /**
     * @param cellEditor The cell editor to check.
     * @return <code>true</code> if the given editor is editable. Otherwise
     *         <code>false</code>.
     */
    private boolean invokeIsEditable(final Control cellEditor) {
        return getEventThreadQueuer().invokeAndWait("getSelectedCell", new IRunnable<Boolean>() { //$NON-NLS-1$
            public Boolean run() {
                if (cellEditor == null || cellEditor instanceof TableCursor
                    || cellEditor == m_table) {
                    // No actual editor found.
                    return false;
                }
                return (cellEditor.getStyle() & SWT.READ_ONLY) == 0;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public Object activateEditor(final Cell cell) {

        Rectangle rect = scrollCellToVisible(cell.getRow(), cell.getCol());
        Control editor = SwtUtils.getEditor(m_table,
                SwtPointUtil.toSwtRectangle(rect), getRobot());
        // sometimes the editor only appears after doubleclick!

        if (!invokeIsEditable(editor)) {
            org.eclipse.swt.graphics.Rectangle cellBounds = 
                    new org.eclipse.swt.graphics.Rectangle(
                    rect.x, rect.y, rect.width, rect.height);
            ClickOptions co = ClickOptions.create().setClickCount(2);
            Control clickTarget = editor == null
                    || editor instanceof TableCursor ? m_table : editor;
            getRobot().click(clickTarget, cellBounds, co);
            editor = SwtUtils.getEditor(m_table,
                    SwtPointUtil.toSwtRectangle(rect), getRobot());
        }

        return editor;

    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        final Cell selectedCell = getSelectedCell();
        return getCellText(selectedCell.getRow(), selectedCell.getCol());
    }

    /**
     * {@inheritDoc}
     */
    public Object getTableHeader() {
        return m_table;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(final String name, final Object cell) {
        return getEventThreadQueuer().invokeAndWait(
            "getPropertyValueOfCell", new IRunnable<String>() { //$NON-NLS-1$
                public String run() {
                    return getRobot().getPropertyValue(cell, name);
                }
            });
    }

    /** {@inheritDoc} */
    public boolean doesRowExist(final int rowInd) {
        return getEventThreadQueuer().invokeAndWait("doesRowExist", new IRunnable<Boolean>() { //$NON-NLS-1$
            public Boolean run() throws StepExecutionException {
                return rowInd >= 0 && rowInd < m_table.getItemCount();
            }
        });
    }

    /** {@inheritDoc} */
    public int getTopIndex() {
        return getEventThreadQueuer().invokeAndWait("getTopIndex", new IRunnable<Integer>() { //$NON-NLS-1$
            public Integer run() throws StepExecutionException {
                return m_table.getTopIndex();
            }
        });
    }

    /** {@inheritDoc} */
    public boolean isChecked(final int row) {
        return getEventThreadQueuer().invokeAndWait("isChecked", new IRunnable<Boolean>() { //$NON-NLS-1$
            public Boolean run() throws StepExecutionException {
                return m_table.getItem(row).getChecked();
            }
        });
    }

    /** {@inheritDoc} */
    public int getSelectionIndex() {
        return getEventThreadQueuer().invokeAndWait("getSelectionIndex", new IRunnable<Integer>() { //$NON-NLS-1$
            public Integer run() throws StepExecutionException {
                return m_table.getSelectionIndex();
            }
        });
    }

    /** {@inheritDoc} */
    public Rectangle getCellBounds(final int row, final int col,
            final boolean restr) {
        return getEventThreadQueuer().invokeAndWait("getCellBounds", new IRunnable<Rectangle>() { //$NON-NLS-1$
            public Rectangle run() throws StepExecutionException {
                TableItem ti = m_table.getItem(row); 
                int column = (m_table.getColumnCount() > 0 || col > 0) 
                    ? col : 0;
                org.eclipse.swt.graphics.Rectangle cellbound =
                        ti.getBounds(column);
                if (!restr) {
                    return SwtPointUtil.toAwtRectangle(cellbound);
                }
                org.eclipse.swt.graphics.Rectangle calculatedBounds =
                                new org.eclipse.swt.graphics.Rectangle(
                                        cellbound.x, cellbound.y,
                                        cellbound.width, cellbound.height);
                String text = CAPUtil.getWidgetText(ti,
                        SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX
                                + column, ti.getText(column));
                Image image = ti.getImage(column);
                if (text != null && text.length() != 0) {
                    GC gc = new GC(m_table);
                    int charWidth = 0; 
                    try {
                        FontMetrics fm = gc.getFontMetrics();
                        charWidth = fm.getAverageCharWidth();
                    } finally {
                        gc.dispose();
                    }
                    calculatedBounds.width = text.length() * charWidth;
                    if (image != null) {
                        calculatedBounds.width += image.getBounds().width;
                    }
                } else if (image != null) {
                    calculatedBounds.width = image.getBounds().width;
                }
                if (column > 0) {
                    TableColumn tc = m_table.getColumn(column);
                    int alignment = tc.getAlignment();
                    if (alignment == SWT.CENTER) {
                        calculatedBounds.x += ((double)tc.getWidth() / 2)
                                - ((double)calculatedBounds.width / 2);
                    }
                    if (alignment == SWT.RIGHT) {
                        calculatedBounds.x += tc.getWidth()
                                - calculatedBounds.width;
                    }
                }
                return SwtPointUtil.toAwtRectangle(
                        cellbound.intersection(calculatedBounds));
            }
        });
    }

    /** {@inheritDoc} */
    public Item[] getColumnItems() {
        return m_table.getColumns();
    }
}

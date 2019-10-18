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
package org.eclipse.jubula.rc.swing.tester.adapter;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITableComponent;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.swing.tester.util.TesterUtil;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
/**
 * Implements the table interface as an adapter for the <code>JTable</code>.
 *
 * @author BREDEX GmbH
 */
public class JTableAdapter extends JComponentAdapter 
    implements ITableComponent {
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            JTableAdapter.class);

    /** The JTable from the AUT   */
    private JTable m_table;
    
    /**
     * Creates an object with the adapted JMenu.
     * @param objectToAdapt the object which needed to be adapted
     */
    public JTableAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_table = (JTable) objectToAdapt;
    }
   
    /**
     * {@inheritDoc}
     */
    public void setComponent(Object element) {
        m_table = (JTable) element;

    }

    /**
     * {@inheritDoc}
     */
    public int getColumnCount() {
        return getEventThreadQueuer().invokeAndWait(
                "getColumnCount", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() {
                        return m_table.getColumnCount();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return getEventThreadQueuer().invokeAndWait(
                "getRowCount", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() {
                        return m_table.getRowCount();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public String getCellText(final int row, final int column) {
        String o = getEventThreadQueuer().invokeAndWait("getCellText", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() {
                        Object value = m_table.getValueAt(row, column);
                        boolean selected = m_table.isCellSelected(row,
                                column);
                        if (log.isDebugEnabled()) {
                            log.debug("Getting cell text:"); //$NON-NLS-1$
                            log.debug("Row, col: " + row + ", " + column); //$NON-NLS-1$ //$NON-NLS-2$
                            log.debug("Value: " + value); //$NON-NLS-1$
                        }
                        TableCellRenderer renderer = m_table.getCellRenderer(
                                row, column);
                        Component c = renderer.getTableCellRendererComponent(
                                m_table, value, selected, true, row,
                                column);

                        return TesterUtil.getRenderedText(c);
                    }
                });
        
        return String.valueOf(o);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getColumnHeaderText(final int column) {
        return getEventThreadQueuer().invokeAndWait("getColumnName", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() {
                        return m_table.getColumnName(column);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnFromString(final String col, final String operator) {
        return getEventThreadQueuer().invokeAndWait(
                "getColumnFromString", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() {
                        int column = -2;
                        try {
                            int usrIdxCol = Integer.parseInt(col);
                            if (usrIdxCol == 0) {
                                usrIdxCol = usrIdxCol + 1;
                            }
                            column = IndexConverter.toImplementationIndex(
                                    usrIdxCol);
                        } catch (NumberFormatException nfe) {
                            try {
                                if (m_table.getTableHeader() == null
                                        || !(m_table.getTableHeader()
                                                .isShowing())) {
                                    throw new StepExecutionException("No Header", //$NON-NLS-1$
                                            EventFactory.createActionError(
                                                TestErrorEvent.NO_HEADER));
                                }
                                for (int i = 0; i < m_table.getColumnCount();
                                        i++) {
                                    String header = m_table.getColumnName(i);
                                    if (MatchUtil.getInstance().match(
                                            header, col, operator)) {
                                        column = i;
                                    }
                                }
                            } catch (IllegalArgumentException iae) {
                                //do nothing here                
                            }
                        }
                        
                        return column;
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public String getRowText(final int row) {
        // JTable does not act like lists
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getBounds() {
        return getEventThreadQueuer().invokeAndWait("getBounds", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() throws StepExecutionException {
                        return m_table.getBounds();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getHeaderBounds(final int col) {
        return getEventThreadQueuer().invokeAndWait("getHeaderBounds", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() throws StepExecutionException {
                        return m_table.getTableHeader().getHeaderRect(col);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public Cell getSelectedCell() throws StepExecutionException {
        
        return getEventThreadQueuer().invokeAndWait(
                "getSelectedCell", new IRunnable<Cell>() { //$NON-NLS-1$
                    public Cell run() {
                
                        int row = m_table.getSelectedRow();
                        int col = m_table.getSelectedColumn();
                        if (log.isDebugEnabled()) {
                            log.debug("Selected row, col: " + row + ", " + col); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        try {
                            checkRowColBounds(row, col);
                        } catch (StepExecutionException e) {
                            if ((e.getEvent() != null)
                                    && (TestErrorEvent.INVALID_INDEX.equals(
                                            e.getEvent()
                                            .getProps().get(TestErrorEvent
                                                    .Property
                                                    .DESCRIPTION_KEY)))) {
                                // set "invalid index" to "no selection" -> better description!
                                throw new StepExecutionException("No selection found", //$NON-NLS-1$
                                        EventFactory.createActionError(
                                                TestErrorEvent.NO_SELECTION));
                            }
                            throw e;
                        }
                        return new Cell(row, col);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isHeaderVisible() {
        return getEventThreadQueuer().invokeAndWait(
                "isHeaderVisible", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        if (m_table.getTableHeader() != null) {
                            return m_table.getTableHeader().isVisible();
                        }
                        return Boolean.FALSE;
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(final int row, final int col) {
        return getEventThreadQueuer().invokeAndWait("isCellEditable", //$NON-NLS-1$
                new IRunnable<Boolean>() {
                    public Boolean run() {
                        return m_table.isCellEditable(row, col);
                    }
                });
    }

    /**
     * Checks whether <code>0 <= value < count</code>.
     *
     * @param value
     *            The value to check.
     * @param count
     *            The upper bound.
     */
    private void checkBounds(int value, int count) {
        if ((value < 0) || (value >= count)) {
            throw new StepExecutionException("Invalid row/column: " + value, //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_INDEX_OR_HEADER));
        }
    }

    /**
     * Checks if the passed row and column are inside the bounds of the JTable.
     *
     * @param row
     *            The row
     * @param column
     *            The column
     * @throws StepExecutionException
     *             If the row or the column is outside of the JTable's bounds.
     */
    private void checkRowColBounds(int row, int column)
        throws StepExecutionException {
        checkBounds(row, m_table.getRowCount());
        checkBounds(column, m_table.getColumnCount());
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasCellSelection() {
        try {
            getSelectedCell();
        } catch (StepExecutionException e) {
            return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public Rectangle scrollCellToVisible(final int row, final int col)
            throws StepExecutionException {
        Rectangle bounds = getEventThreadQueuer().invokeAndWait("getCellRect", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() {
                        return m_table.getCellRect(row, col, true);
                    }
                });

        getRobot().scrollToVisible(m_table, bounds);
        return bounds;
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
        return getEventThreadQueuer().invokeAndWait("getHeaderBounds", //$NON-NLS-1$
                new IRunnable<JTableHeader>() {
                    public JTableHeader run() throws StepExecutionException {
                        return m_table.getTableHeader();
                    }
                });

    }


    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(String name, Object cell) {
        StepExecutionException.throwUnsupportedAction();
        return null;
    }

    /** {@inheritDoc} */
    public boolean doesRowExist(int rowInd) {
        return rowInd >= 0 && rowInd < m_table.getRowCount(); 
    }

    /** {@inheritDoc} */
    public int getTopIndex() {
        throw new UnsupportedOperationException("Swing table adapter does not implement getTopIndex."); //$NON-NLS-1$
        // and we don't need it currently...
    }

    /** {@inheritDoc} */
    public Rectangle getCellBounds(int row, int col, boolean restr) {
        throw new UnsupportedOperationException("Swing table adapter does not implement getCellBounds."); //$NON-NLS-1$
        // and we don't need it currently...
    }
}

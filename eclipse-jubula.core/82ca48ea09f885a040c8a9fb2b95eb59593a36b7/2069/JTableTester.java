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
package org.eclipse.jubula.rc.swing.tester;

import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.AbstractTableTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IWidgetComponent;
import org.eclipse.jubula.rc.swing.tester.adapter.JComponentAdapter;
import org.eclipse.jubula.rc.swing.utils.SwingUtils;
/**
 * Toolkit specific commands for the <code>JTable</code>
 * 
 * @author BREDEX GmbH
 */
public class JTableTester extends AbstractTableTester {
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            JTableTester.class);

    /**
     * @return the log
     */
    public static AutServerLogger getLog() {
        return log;
    }    
    
    /**
     * @return the real AUT JTable
     */
    private JTable getTable() {
        return (JTable) getComponent().getRealComponent();
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        final String[] componentTextArray;
        TableColumnModel columnModel = getTable().getColumnModel();
        if (columnModel == null) {
            componentTextArray = null;
        } else {
            componentTextArray = new String[columnModel.getColumnCount()];
            for (int i = 0; i < componentTextArray.length; i++) {
                TableColumn tableColumn = columnModel.getColumn(i);
                if (tableColumn == null) {
                    componentTextArray[i] = null;
                } else {
                    Object headerValue = tableColumn.getHeaderValue();
                    if (headerValue == null) {
                        componentTextArray[i] = null;
                    } else {
                        componentTextArray[i] = headerValue.toString();
                    }
                }
            }
        }
        return componentTextArray;
    }
    
    /**
     * {@inheritDoc}
     */
    protected int getExtendSelectionModifier() {
        return SwingUtils.getSystemDefaultModifier();
    }

    /**
     * {@inheritDoc}
     */
    protected Cell getCellAtMousePosition() throws StepExecutionException {
        JTable table = getTable();
        Point mousePos = getRobot().getCurrentMousePosition();
        Point tablePos = table.getLocationOnScreen();
        Point relativePos = new Point(mousePos.x - tablePos.x,
            mousePos.y - tablePos.y);
        final int column = table.columnAtPoint(relativePos);
        final int row = table.rowAtPoint(relativePos);
        if (log.isDebugEnabled()) {
            log.debug("Selected row, col: " + row + ", " + column); //$NON-NLS-1$ //$NON-NLS-2$
        }
        checkRowColBounds(row, column);
        return new Cell(row, column);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isMouseOnHeader() {
        JTable table = getTable();
        if (table.getTableHeader() == null
                || !(table.getTableHeader().isShowing())) {
            return false;
        }
        
        JTableHeader header = table.getTableHeader();
        Point mousePos = getRobot().getCurrentMousePosition();        
        try {
            Point headerPos = header.getLocationOnScreen();
            Point relativePos = new Point(mousePos.x - headerPos.x,
                mousePos.y - headerPos.y);            
            return header.getBounds().contains(relativePos);
        } catch (IllegalComponentStateException icse) {
            return false;
        } 
    }

    /**
     * {@inheritDoc}
     */
    protected Object setEditorToReplaceMode(Object editor, boolean replace) {
        Object returnvalue = editor;
        if (replace) {
            getRobot().clickAtCurrentPosition(editor, 3, 1);
        } else {
            returnvalue = getComponent().getRealComponent();
        }
        return returnvalue;
    }

    /**
     * {@inheritDoc}
     */
    protected Object activateEditor(Cell cell, Rectangle rectangle) {
        Object table = getComponent().getRealComponent();
        getRobot().click(table, rectangle);
        Component editor = getTableCellEditor(cell);
        // sometimes the editor only appears after doubleclick!
        if (editor == null) {
            ClickOptions co = ClickOptions.create().setClickCount(2);
            getRobot().click(table, rectangle, co);
            editor = getTableCellEditor(cell);
        }
        return editor;
    }
    
    /**
     * Gets the TableCellEditor of the given cell
     * @param cell the cell.
     * @return the TableCellEditor
     */
    private Component getTableCellEditor(final Cell cell) {
        final JTable table = (JTable) getComponent().getRealComponent();
        return getEventThreadQueuer()
            .invokeAndWait("getCellEditor", //$NON-NLS-1$
                new IRunnable<Component>() {
                    public Component run() {
                        Object value = table.getValueAt(
                            cell.getRow(), cell.getCol());
                        boolean selected = table.isCellSelected(
                            cell.getRow(), cell.getCol());
                        return table.getCellEditor(cell.getRow(),
                            cell.getCol()).getTableCellEditorComponent(table,
                                value, selected, cell.getRow(), cell.getCol());
                    }
                });
    }

    /** {@inheritDoc} */
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
        return null;
    }

    @Override
    protected IWidgetComponent getWidgetAdapter() {
        if (getTable().getTableHeader() != null
                && getRobot().isMouseInComponent(getTable().getTableHeader())) {
            return new JComponentAdapter(getTable().getTableHeader());
        }
        return (IWidgetComponent) getComponent();
    }

}
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
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeTableOperationContext;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITreeTableComponent;
import org.eclipse.jubula.rc.swt.components.ISWTTableComponent;
import org.eclipse.jubula.rc.swt.components.SWTCell;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.tester.util.RowManager;
import org.eclipse.jubula.rc.swt.tester.util.TreeOperationContext;
import org.eclipse.jubula.rc.swt.tester.util.TreeTableOperationContext;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Implements the Tree interface for adapting a <code>SWT.Tree</code>
 * 
 * @author BREDEX GmbH
 */
public class TreeAdapter extends ControlAdapter
        implements ITreeTableComponent, ISWTTableComponent {

    /** The row manager to fetch the rows of the Tree by their index */
    private RowManager m_rowManager;

    /**
     * @param objectToAdapt
     *            graphics component which will be adapted
     */
    public TreeAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_rowManager = new RowManager((Tree) objectToAdapt);
    }

    /**
     * {@inheritDoc}
     */
    public AbstractTreeOperationContext getContext() {
        return new TreeOperationContext(getEventThreadQueuer(), getRobot(),
                (Tree) getRealComponent());
    }
    
    /**
     * {@inheritDoc}
     */
    public AbstractTreeTableOperationContext getContext(int column) {
        return new TreeTableOperationContext(getEventThreadQueuer(), getRobot(),
                (Tree) getRealComponent(), column);
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(final String name, final Object cell) {
        return getEventThreadQueuer().invokeAndWait("getPropertyValueOfCell", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() {
                        return getRobot().getPropertyValue(cell, name);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public Object getRootNode() {
        return getEventThreadQueuer().invokeAndWait("getRootNode", //$NON-NLS-1$
                new IRunnable<TreeItem[]>() {
                    public TreeItem[] run() {
                        return ((Tree) getRealComponent()).getItems();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRootVisible() {
        return true;
    }

    /**
     * @return the tree, properly casted
     */
    private Tree getTree() {
        return (Tree) getRealComponent();
    }

    /** {@inheritDoc} */
    public int getColumnCount() {
        return getEventThreadQueuer().invokeAndWait("getRootNode", //$NON-NLS-1$
                new IRunnable<Integer>() {
                    public Integer run() {
                        int col = getTree().getColumnCount();
                        return col == 0 ? 1 : col;
                    }
                });
    }

    /** {@inheritDoc} */
    public int getRowCount() {
        return getEventThreadQueuer().invokeAndWait("getRowCount", new IRunnable<Integer>() { //$NON-NLS-1$
            public Integer run() throws StepExecutionException {
                return m_rowManager.getRowCount();
            }
        });
    }

    /** {@inheritDoc} */
    public String getCellText(final int row, final int column) {
        return getEventThreadQueuer().invokeAndWait("getCellText", new IRunnable<String>() { //$NON-NLS-1$
            public String run() throws StepExecutionException {
                TreeItem treeItem = m_rowManager.getRow(row);
                return CAPUtil.getWidgetText(treeItem,
                        SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX + column,
                        treeItem.getText(column));
            }
        });
    }

    /** {@inheritDoc} */
    public String getColumnHeaderText(final int column) {
        return getEventThreadQueuer().invokeAndWait("getColumnName", //$NON-NLS-1$
            new IRunnable<String>() {
                public String run() {
                    final TreeColumn colItem = getTree().getColumn(column);
                    return CAPUtil.getWidgetText(colItem, colItem.getText());
                }
            });
    }

    /** {@inheritDoc} */
    public String getRowText(final int rowIdx) {
        return getEventThreadQueuer().invokeAndWait("getRowText", new IRunnable<String>() { //$NON-NLS-1$
            public String run() {
                final TreeItem rowItem = m_rowManager.getRow(rowIdx);
                if (rowItem == null) {
                    throw new StepExecutionException("Row with index " + rowIdx + " does not exist.", //$NON-NLS-1$ //$NON-NLS-2$
                        EventFactory.createActionError(
                                TestErrorEvent.NOT_FOUND));
                }
                return CAPUtil.getWidgetText(rowItem, rowItem.getText());
            }
        });
    }

    /** {@inheritDoc} */
    public Cell getSelectedCell() throws StepExecutionException {
        return getEventThreadQueuer().invokeAndWait("getSelectedCell", new IRunnable<Cell>() { //$NON-NLS-1$
            public Cell run() throws StepExecutionException {
                if (getTree().getSelection().length == 0) {
                    throw new StepExecutionException("No table cell selected.", //$NON-NLS-1$
                            EventFactory.createActionError(
                                    TestErrorEvent.NO_SELECTION));
                }
                TreeItem ti = getTree().getSelection()[0];
                // Tree tables don't have individual cell selection
                // If more sophisticated approach is required, check TableSelectionTracker
                return new SWTCell(m_rowManager.getRowIndex(ti), 0, ti);
            }
        });
    }

    /** {@inheritDoc} */
    public boolean isHeaderVisible() {
        return getEventThreadQueuer().invokeAndWait("isHeaderVisible", new IRunnable<Boolean>() { //$NON-NLS-1$
            public Boolean run() throws StepExecutionException {
                return getTree().getHeaderVisible();
            }
        });
    }

    /** {@inheritDoc} */
    public boolean isCellEditable(final int row, final int col) {
        final Control cellEditor = (Control) 
                activateEditor(new Cell(row, col));
        return invokeIsEditable(cellEditor);
    }

    /**
     * @param cellEditor The cell editor to check.
     * @return <code>true</code> if the given editor is editable. Otherwise
     *         <code>false</code>.
     */
    private boolean invokeIsEditable(final Control cellEditor) {
        return getEventThreadQueuer().invokeAndWait("getSelectedCell", new IRunnable<Boolean>() { //$NON-NLS-1$
            public Boolean run() {
                // probably Trees can't have TableCursors, but it doesn't hurt to have this check here
                if (cellEditor == null || cellEditor instanceof TableCursor
                    || cellEditor == getTree()) {
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

        scrollCellToVisible(cell.getRow(), cell.getCol());
        Rectangle rect = getCellBounds(cell.getRow(), cell.getCol(), true);
        Control editor = SwtUtils.getEditor(getTree(),
                SwtPointUtil.toSwtRectangle(rect), getRobot());
        // sometimes the editor only appears after doubleclick!

        if (!invokeIsEditable(editor)) {
            org.eclipse.swt.graphics.Rectangle cellBounds = 
                    new org.eclipse.swt.graphics.Rectangle(
                    rect.x, rect.y, rect.width, rect.height);
            ClickOptions co = ClickOptions.create().setClickCount(2).
                    setScrollToVisible(false);
            getRobot().click(getTree(), cellBounds, co);
            editor = SwtUtils.getEditor(getTree(),
                    SwtPointUtil.toSwtRectangle(rect), getRobot());
        }

        return editor;

    }

    /** {@inheritDoc} */
    public boolean hasCellSelection() {
        return getEventThreadQueuer().invokeAndWait("hasCellSelection", new IRunnable<Boolean>() { //$NON-NLS-1$
            public Boolean run() {
                return getTree().getSelection().length > 0;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle scrollCellToVisible(final int row, final int col)
        throws StepExecutionException {
        getEventThreadQueuer().invokeAndWait("scrollCellToVisible", //$NON-NLS-1$
                new IRunnable<Void>() {
                    public Void run() {
                        if (getTree().getColumnCount() > 0 || col > 0) {
                            getTree().showColumn(getTree().getColumn(col));
                        }
                        getTree().showItem(m_rowManager.getRow(row));
                        return null;
                    }
                });
        
        final org.eclipse.swt.graphics.Rectangle cBoundsRelToParent = 
                SwtPointUtil.toSwtRectangle(getCellBounds(row, col, true));

        getEventThreadQueuer().invokeAndWait("getCellBoundsRelativeToParent", //$NON-NLS-1$
                new IRunnable<Void>() {
                    public Void run() {
                        org.eclipse.swt.graphics.Point cOriginRelToParent = 
                                getTree().getDisplay().map(getTree(),
                                        getTree().getParent(),
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
                        return getTree().getParent();
                    }
                });
            
        getRobot().scrollToVisible(parent, cBoundsRelToParent);
        
        return getCellBounds(row, col, true);
    }

    /** {@inheritDoc} */
    public Rectangle scrollCellToVisibleMy(final int row, final int col)
        throws StepExecutionException {
        return getEventThreadQueuer().invokeAndWait("scrollCellToVisible", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() {
                        TreeItem res = m_rowManager.getRow(row);
                        getTree().showColumn(getTree().getColumn(col));
                        getTree().showItem(res);
                        org.eclipse.swt.graphics.Rectangle bounds = 
                                res.getBounds(col);
                        getRobot().scrollToVisible(getTree(), bounds);
                        bounds = res.getBounds(col);
                        bounds.intersect(getTree().getClientArea());
                        return new Rectangle(bounds.x, bounds.y,
                                bounds.width, bounds.height);
                    }
                });
    }

    /** {@inheritDoc} */
    public Object getTableHeader() {
        // no header, so the table itself is returned
        return getRealComponent();
    }

    /** {@inheritDoc} */
    public Rectangle getHeaderBounds(final int col) {
        return getEventThreadQueuer().invokeAndWait("getHeaderBounds", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() throws StepExecutionException {
                        org.eclipse.swt.graphics.Rectangle rect = getTree()
                                .getItem(0).getBounds(col);
                        rect.y = getTree().getClientArea().y;
                        return new Rectangle(rect.x, rect.y, rect.width,
                                getTree().getHeaderHeight());
                    }
                });
    }

    /** {@inheritDoc} */
    public String getText() {
        final Cell selectedCell = getSelectedCell();
        return getCellText(selectedCell.getRow(), selectedCell.getCol());
    }

    /** {@inheritDoc} */
    public boolean doesRowExist(int row) {
        return m_rowManager.getRow(row) != null;
    }

    /** {@inheritDoc} */
    public int getTopIndex() {
        return m_rowManager.getRowIndex(getTree().getTopItem());
    }

    /**
     * @param ti the row item
     * @return the index of the row item
     */
    public int getRowIndex(TreeItem ti) {
        return m_rowManager.getRowIndex(ti);
    }

    /** {@inheritDoc} */
    public boolean isChecked(int row) {
        return m_rowManager.getRow(row).getChecked();
    }

    /** {@inheritDoc} */
    public int getSelectionIndex() {
        return getEventThreadQueuer().invokeAndWait("getSelectionIndex", new IRunnable<Integer>() { //$NON-NLS-1$
            public Integer run() throws StepExecutionException {
                TreeItem[] items = getTree().getSelection();
                if (items.length == 0) {
                    return -1;
                }
                return m_rowManager.getRowIndex(items[0]);
            }
        });
    }

    /** {@inheritDoc} */
    public Rectangle getCellBounds(final int row, final int col,
            final boolean restr) {
        return getEventThreadQueuer().invokeAndWait("getCellBounds", new IRunnable<Rectangle>() { //$NON-NLS-1$
            public Rectangle run() throws StepExecutionException {
                TreeItem ti = m_rowManager.getRow(row); 
                int column = (getTree().getColumnCount() > 0 || col > 0) 
                    ? col : 0;
                org.eclipse.swt.graphics.Rectangle r = 
                        ti.getBounds(column);
                if (!restr) {
                    return SwtPointUtil.toAwtRectangle(r);
                }
                String text = CAPUtil.getWidgetText(ti,
                        SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX
                                + column, ti.getText(column));
                Image image = ti.getImage(column);
                if (text != null && text.length() != 0) {
                    GC gc = new GC(getTree());
                    int charWidth = 0; 
                    try {
                        FontMetrics fm = gc.getFontMetrics();
                        charWidth = fm.getAverageCharWidth();
                    } finally {
                        gc.dispose();
                    }
                    r.width = text.length() * charWidth;
                    if (image != null) {
                        r.width += image.getBounds().width;
                    }
                } else if (image != null) {
                    r.width = image.getBounds().width;
                }
                if (column > 0) {
                    TreeColumn tc = getTree().getColumn(column);
                    int alignment = tc.getAlignment();
                    if (alignment == SWT.CENTER) {
                        r.x += ((double)tc.getWidth() / 2) 
                                - ((double)r.width / 2);
                    }
                    if (alignment == SWT.RIGHT) {
                        r.x += tc.getWidth() - r.width;
                    }
                }
                
                return new Rectangle(r.x, r.y, r.width, r.height);
            }
        });
    }

    /** {@inheritDoc} */
    public Item[] getColumnItems() {
        return getTree().getColumns();
    }

    /**
     * @param rowInd the index of the row
     * @return the row
     */
    public TreeItem getRow(int rowInd) {
        return m_rowManager.getRow(rowInd);
    }

}
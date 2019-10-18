/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester;

import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;

import java.awt.MouseInfo;
import java.util.StringTokenizer;

import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.implclasses.tree.ExpandCollapseTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.INodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.StandardDepthFirstTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperation;
import org.eclipse.jubula.rc.common.tester.AbstractTreeTableTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITreeComponent;
import org.eclipse.jubula.rc.common.tester.interfaces.ITableActions;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.swt.components.SWTCell;
import org.eclipse.jubula.rc.swt.driver.KeyCodeConverter;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.tester.util.ToggleCheckboxOperation;
import org.eclipse.jubula.rc.swt.tester.util.TreeOperationContext;
import org.eclipse.jubula.rc.swt.tester.util.VerifyCheckboxOperation;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Toolkit specific commands for the <code>Tree</code>
 *
 * @author BREDEX GmbH
 */
public class TreeTester extends AbstractTreeTableTester
    implements ITableActions {

    /**
     * Finds the item at a given position in the tree.
     *
     * @author BREDEX GmbH
     * @created Jul 28, 2010
     */
    private static final class ItemAtPointTreeNodeOperation 
            extends AbstractTreeNodeOperation {

        /** the item that was found at the given position */
        private TreeItem m_itemAtPoint;
        
        /** the position (in absolute coordinates) at which to find the item */
        private Point m_absPoint;
        
        /** 
         * the bounds (in absolute coordinates) of the tree in which the 
         * search should take place 
         */
        private Rectangle m_absTreeBounds;
        
    
    /**
     * Constructor
     * 
     * @param absPoint The position (in absolute coordinates) at which to 
     *                 find the item.
     * @param absTreeBounds The bounds (in absolute coordinates) of the 
     *                      tree in which the search should take place. 
     */
        public ItemAtPointTreeNodeOperation(Point absPoint, 
            Rectangle absTreeBounds) {
            m_absPoint = absPoint;
            m_absTreeBounds = absTreeBounds;
        }
    
    /**
     * {@inheritDoc}
     */
        public boolean operate(Object node) throws StepExecutionException {
            if (getContext().isVisible(node) && node instanceof TreeItem) {
                TreeItem currentItem = (TreeItem)node;
                final Rectangle absItemBounds = 
                        SwtUtils.getBounds(currentItem);
                absItemBounds.x = m_absTreeBounds.x;
                absItemBounds.width = m_absTreeBounds.width;
                if (SwtUtils.containsInclusive(
                        absItemBounds, m_absPoint)) {
                    m_itemAtPoint = currentItem;
                    return false;
                }
            }
        
            return true;
        }
    
    /**
     * 
     * @return the item found at the given position, or <code>null</code> if
     *         no item was found. Note that this method will always return 
     *         <code>null</code> if called before or during execution of
     *         {@link #operate(Object)}.
     */
        public TreeItem getItemAtPoint() {
            return m_itemAtPoint;
        }
    }

    /**
     * 
     * @return the Tree
     */
    private Tree getTreeTable() {
        return (Tree) getComponent().getRealComponent();
    }
    
    /** {@inheritDoc} */
    @Override
    public void rcCheckPropertyAtMousePosition(final String name,
            final String value, final String operator, int timeout) {
        invokeAndWait("rcCheckPropertyAtMousePosition", timeout, //$NON-NLS-1$
            new Runnable() {
                public void run() {
                    Object cell = null;
                    int numColumns = getEventThreadQueuer().invokeAndWait(
                            "checkColumnIndex", //$NON-NLS-1$
                            new IRunnable<Integer>() {
            
                                public Integer run() {
                                    return getTreeTable().getColumnCount();
                                }
                            });
                    if (numColumns > 0) {            
                        cell = getCellAtMousePosition();
                    } else {
                        cell = getNodeAtMousePosition();
                    }
                    final ITreeComponent bean = getTreeAdapter();
                    final String propToStr =
                            bean.getPropertyValueOfCell(name, cell);
                    Verifier.match(propToStr, value, operator);
                }
            });
    }

    /**
     * {@inheritDoc}
     */
    public void rcVerifyTextAtMousePosition(final String pattern,
            final String operator, int timeout) {
        // AbstractTreeTester.rcVerifyTextAtMousePosition uses TreeTableOperationContext.getNodeTextList
        // which is wrongly implemented since a long time ago. Unfortunately, that method is used during
        // expanding by Textpaths, so it shouldn't be altered due to backward compatibility.
        // That's why TableTester is used here...
        TableTester tableTester = new TableTester(getTreeAdapter());
        tableTester.rcVerifyTextAtMousePosition(pattern, operator, timeout);
    }

    /**
     * Same as Check Text At Mouse Position...
     * @param pattern the pattern
     * @param operator the operator
     * @param timeout the timeout
     */
    public void rcVerifyCellTextAtMousePosition(final String pattern,
            final String operator, int timeout) {
        rcVerifyTextAtMousePosition(pattern, operator, timeout);
    }

    /**
     * {@inheritDoc}
     */
    public void rcDragByTextPath(int mouseButton, String modifier,
            String pathType, int preAscend, String treePath, String operator) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        SwtUtils.waitForDisplayIdle(getTreeTable().getDisplay());
        rcSelect(pathType, preAscend, treePath, operator, 0, 1,
                ValueSets.BinaryChoice.no.rcValue());
        SwtUtils.waitForDisplayIdle(getTreeTable().getDisplay());
    }

    /**
     * {@inheritDoc}
     */
    public void rcDropByTextPath(final String pathType, final int preAscend,
            final String treePath, final String operator, int delayBeforeDrop) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        final IRobot robot = getRobot();
        try {
            pressOrReleaseModifiers(dndHelper.getModifier(), true);
            getEventThreadQueuer().invokeAndWait(
                    "rcDropByTextPath - perform drag", new IRunnable<Void>() { //$NON-NLS-1$
                        public Void run() throws StepExecutionException {
                            // drag
                            robot.mousePress(dndHelper.getDragComponent(), null,
                                    dndHelper.getMouseButton());
                            CAPUtil.shakeMouse();
                            return null;
                        }
                    });
            postMouseMovementEvent();
            // drop
            rcSelect(pathType, preAscend, treePath, operator, 0, 1,
                    ValueSets.BinaryChoice.no.rcValue());
            SwtUtils.waitForDisplayIdle(getTreeTable().getDisplay());
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            robot.mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
            SwtUtils.waitForDisplayIdle(getTreeTable().getDisplay());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rcDragByIndexPath(int mouseButton, String modifier,
            String pathType, int preAscend, String indexPath) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        SwtUtils.waitForDisplayIdle(getTreeTable().getDisplay());
        rcSelectByIndices(pathType, preAscend, indexPath, 0, 1,
                ValueSets.BinaryChoice.no.rcValue());
        SwtUtils.waitForDisplayIdle(getTreeTable().getDisplay());
    }

    /**
     * {@inheritDoc}
     */
    public void rcDropByIndexPath(final String pathType, final int preAscend,
            final String indexPath, int delayBeforeDrop) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        final IRobot robot = getRobot();
        try {
            pressOrReleaseModifiers(dndHelper.getModifier(), true);
            getEventThreadQueuer().invokeAndWait(
                    "rcDropByIndexPath - perform drag", new IRunnable<Void>() { //$NON-NLS-1$
                        public Void run() throws StepExecutionException {
                            // drag
                            robot.mousePress(dndHelper.getDragComponent(), null,
                                    dndHelper.getMouseButton());
                            CAPUtil.shakeMouse();
                            return null;
                        }
                    });
            postMouseMovementEvent();
            // drop
            rcSelectByIndices(pathType, preAscend, indexPath, 0, 1,
                    ValueSets.BinaryChoice.no.rcValue());
            SwtUtils.waitForDisplayIdle(getTreeTable().getDisplay());
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            robot.mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
            SwtUtils.waitForDisplayIdle(getTreeTable().getDisplay());
        }
    }

    /**
     * Post a MouseMove event in order to break the Display out of its post-drag
     * "freeze". It appears as though the mouse position change needs to be
     * extreme in order to nudge the Display back into action (i.e.
     * (<mouse-location> + 1) was insufficient), so the default Event values (x,
     * y = 0) are used.
     * 
     * The problem re-occurred if something was drag and dropped in the upper left
     * corner. So the observation that the change must be extreme is still correct
     */
    private void postMouseMovementEvent() {
        Event wakeEvent = new Event();
        java.awt.Point location = MouseInfo.getPointerInfo().getLocation();
        final int minimaloffset = 300;
        if (location.x - minimaloffset < 0) {
            wakeEvent.x = location.x + minimaloffset;
        }
        if (location.y - minimaloffset < 0) {
            wakeEvent.y = location.y + minimaloffset;
        }
        wakeEvent.type = SWT.MouseMove;
        getTreeTable().getDisplay().post(wakeEvent);
        waitForDisplayUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        return getEventThreadQueuer().invokeAndWait("getItemAtMousePosition", new IRunnable<TreeItem>() { //$NON-NLS-1$
            
            public TreeItem run() throws StepExecutionException {
                Point mousePos = SwtUtils.convertToSwtPoint(
                        getRobot().getCurrentMousePosition());
                ItemAtPointTreeNodeOperation op = 
                    new ItemAtPointTreeNodeOperation(
                            mousePos, SwtUtils.getWidgetBounds(getTreeTable()));

                TreeItem topItem = getTreeTable().getTopItem();
                if (topItem != null) {
                    
                    // FIXME zeb This may be slow for very large trees, as the 
                    //           search may continue long past the
                    //           visible client area of the tree.
                    //           It may also cause problems with regard to 
                    //           lazy/virtual nodes. 
                    StandardDepthFirstTraverser traverser = 
                        new StandardDepthFirstTraverser(
                            new TreeOperationContext(
                                getEventThreadQueuer(),
                                getRobot(),
                                getTreeTable()));
                    traverser.traversePath(op, topItem);
                    if (op.getItemAtPoint() != null) {
                        return op.getItemAtPoint();
                    }
                    
                }

                throw new StepExecutionException("No tree node found at mouse position.", //$NON-NLS-1$
                        EventFactory.createActionError(
                                TestErrorEvent.NOT_FOUND));
            }
        });
    }
    
    /**
     *
     * @return the table cell at the current mouse position.
     * @throws StepExecutionException If no table cell can be found at the
     *                                current mouse position.
     */
    private Cell getCellAtMousePosition() throws StepExecutionException {
        
        final Tree tree = getTreeTable();
        final java.awt.Point awtMousePos = getRobot().getCurrentMousePosition();
        Cell returnvalue = getEventThreadQueuer().invokeAndWait(
                "getCellAtMousePosition",  //$NON-NLS-1$
                new IRunnable<Cell>() {
                    private int m_rowCount = 0;

                    public Cell run() throws StepExecutionException {
                        Cell cell = null;
                        for (TreeItem item : tree.getItems()) {
                            cell = findCell(item);
                            if (cell != null) {
                                break;
                            }
                        }
                        if (cell == null) {
                            throw new StepExecutionException(
                                "No cell under mouse position found!", //$NON-NLS-1$
                                EventFactory.createActionError(
                                        TestErrorEvent.NOT_FOUND));
                        }
                        return cell;
                    }

                    /**
                     * This method tries to find the cell which is under the current mouse position
                     * belonging to a given tree item or its sub items
                     * @param item the tree item
                     * @return the cell if found, <code>null</code> if not
                     */
                    private Cell findCell(TreeItem item) {
                        Cell cell = null;
                        int lim = tree.getColumnCount() == 0
                                ? 1 : tree.getColumnCount();
                        for (int col = 0; col < tree.getColumnCount(); col++) {
                            Rectangle itemBounds = item.getBounds(col);
                            final org.eclipse.swt.graphics.Point 
                                absItemBounds = tree.toDisplay(itemBounds.x,
                                            itemBounds.y);
                            final java.awt.Rectangle absRect =
                                new java.awt.Rectangle(absItemBounds.x,
                                    absItemBounds.y, itemBounds.width,
                                    itemBounds.height);
                            if (absRect.contains(awtMousePos)) {
                                cell = new SWTCell(m_rowCount, col, item);
                            }
                        }
                        m_rowCount++;
                        if (cell == null && item.getExpanded()) {
                            for (TreeItem subItem : item.getItems()) {
                                cell = findCell(subItem);
                                if (cell != null) {
                                    break;
                                }
                            }
                        }
                        return cell;
                    }
                });
        return returnvalue;
    }
    
    
    /**
     * @param etq
     *            the EventThreadQueuer to use
     * @param table
     *            the table to use
     * @param row
     *            The row of the cell
     * @param col
     *            The column of the cell
     * @param ti
     *            The tree item
     * @return The bounding rectangle for the cell, relative to the table's
     *         location.
     */
    private static Rectangle getCellBounds(IEventThreadQueuer etq,
        final Tree table, final int row, final int col, final TreeItem ti) {
        Rectangle cellBounds = etq.invokeAndWait(
                "getCellBounds", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() {
                        int column = (table.getColumnCount() > 0 || col > 0) 
                            ? col : 0;
                        org.eclipse.swt.graphics.Rectangle r = 
                                ti.getBounds(column);
                        String text = CAPUtil.getWidgetText(ti,
                                SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX
                                        + column, ti.getText(column));
                        Image image = ti.getImage(column);
                        if (text != null && text.length() != 0) {
                            GC gc = new GC(table);
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
                            TreeColumn tc = table.getColumn(column);
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
        return cellBounds;
    }
    /**
     * Selects Checkbox of last node of the path given by <code>treepath</code>.
     * 
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param treePath The tree path.
     * @param operator
     *  If regular expressions are used to match the tree path
     * @throws StepExecutionException If the tree path is invalid, if the
     * double-click to expand the node fails, or if the selection is invalid.
     */
    public void rcToggleCheckbox(String pathType, int preAscend, String
            treePath, String operator)
        throws StepExecutionException {
        toggleCheckBoxByPath(pathType, preAscend, 
                createStringNodePath(splitTextTreePath(treePath), operator));
    }
    
    /**
     * Selects Checkbox of last node of the path given by <code>indexPath</code>
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param indexPath the index path
     * @throws StepExecutionException if <code>indexPath</code> is not a valid
     * path
     */
    public void rcToggleCheckboxByIndices(String pathType, int preAscend, 
                    String indexPath)
        throws StepExecutionException {
    
        toggleCheckBoxByPath(pathType, preAscend,
                createIndexNodePath(splitIndexTreePath(indexPath)));
    }
    
    /**
     * Verify Selection of checkbox of the node at the end of the <code>treepath</code>.
     * 
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param treePath The tree path.
     * @param operator
     *  If regular expressions are used to match the tree path
     * @param checked true if checkbox of tree node is selected, false otherwise
     * @param timeout the maximum amount of time to wait for the state to occur
     * @throws StepExecutionException If the tree path is invalid, if the
     * double-click to expand the node fails, or if the selection is invalid.
     */
    public void rcVerifyCheckbox(final String pathType, final int preAscend,
            final String treePath, final String operator, final boolean checked,
            int timeout)
        throws StepExecutionException {
        invokeAndWait("rcVerifyCheckBox", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                verifyCheckBoxByPath(pathType, preAscend, 
                        createStringNodePath(
                                splitTextTreePath(treePath), operator), 
                        checked);    
            }
        });
    }
    
    /**
     * Verify Selection of checkbox of last node of the path given by <code>indexPath</code>
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param indexPath the index path
     * @param checked true if checkbox of tree node is selected, false otherwise
     * @param timeout the maximum amount of time to wait for the state to occur
     * @throws StepExecutionException if <code>indexPath</code> is not a valid
     * path
     */
    public void rcVerifyCheckboxByIndices(final String pathType,
            final int preAscend, final String indexPath, final boolean checked,
            int timeout)
        throws StepExecutionException {
        invokeAndWait("rcVerifyChecktboxByIndices", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                verifyCheckBoxByPath(pathType, preAscend,
                        createIndexNodePath(splitIndexTreePath(indexPath)), 
                        checked);    
            }
        });
    }
    
    /**
     * Verifies whether the checkbox of the first selection in the tree is checked
     * 
     * @param checked true if checkbox of node is selected, false otherwise
     * @param timeout the maximum amount of time to wait for the state to occur
     * @throws StepExecutionException If no node is selected or the verification fails.
     */
    public void rcVerifySelectedCheckbox(final boolean checked, int timeout)
        throws StepExecutionException {
        invokeAndWait("rcVerifySelectedCheckbox", timeout, new Runnable() { //$NON-NLS-1$
            
            public void run() {
                Boolean checkSelected = getEventThreadQueuer().invokeAndWait(
                        "rcVerifyTreeCheckbox", new IRunnable<Boolean>() { //$NON-NLS-1$
                            public Boolean run() {
                                AbstractTreeOperationContext context = 
                                        ((ITreeComponent)getComponent())
                                        .getContext();
                                TreeItem node = 
                                        (TreeItem) getSelectedNode(context);
                                return node.getChecked();
                            }            
                        });       
                Verifier.equals(checked, checkSelected.booleanValue());
                
            }
        });
    }
    
    /**
     * @param pathType pathType
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param objectPath objectPath
     * @param checked true if Checkbox should be enabled, false otherwise
     */
    private void verifyCheckBoxByPath(String pathType, int preAscend, 
            INodePath objectPath, final boolean checked) {

        TreeNodeOperation expOp = 
            new ExpandCollapseTreeNodeOperation(false);
        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), getTreeTable());
        TreeNodeOperation checkboxOp = new VerifyCheckboxOperation(
                checked, context);
        INodePath subPath = objectPath.subPath(0, objectPath.getLength() - 1);
        
        traverseTreeByPath(subPath, pathType, preAscend, expOp);
        traverseLastElementByPath(objectPath, pathType, preAscend, checkboxOp);
    }
    
    /**
     * @param pathType pathType
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param objectPath objectPath
     */
    private void toggleCheckBoxByPath(String pathType, int preAscend, 
            INodePath objectPath) {

        TreeNodeOperation expOp = 
            new ExpandCollapseTreeNodeOperation(false);
        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), getTreeTable());
        TreeNodeOperation selCheckboxOp = new ToggleCheckboxOperation(context);
        INodePath subPath = objectPath.subPath(0, objectPath.getLength() - 1);
        
        traverseTreeByPath(subPath, pathType, preAscend, expOp);
        traverseLastElementByPath(objectPath, pathType, preAscend,
                selCheckboxOp);      
    }
    
    /**
     * Forces all outstanding paint requests for the receiver's component's 
     * display to be processed before this method returns.
     * 
     * @see Display#update()
     */
    private void waitForDisplayUpdate() {
        ((Control)getComponent().getRealComponent())
            .getDisplay().syncExec(new Runnable() {
                    public void run() {
                        ((Control) getComponent().getRealComponent())
                                .getDisplay().update();
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void pressOrReleaseModifiers(String modifier, boolean press) {
        final IRobot robot = getRobot();
        final StringTokenizer modTok = new StringTokenizer(
                KeyStrokeUtil.getModifierString(modifier), " "); //$NON-NLS-1$
        while (modTok.hasMoreTokens()) {
            final String mod = modTok.nextToken();
            final int keyCode = KeyCodeConverter.getKeyCode(mod);
            if (press) {
                robot.keyPress(null, keyCode);
            } else {
                robot.keyRelease(null, keyCode);
            }
        }
    }

    /**
     * @return the TableTester used to execute Table actions
     */
    private TableTester getTableTester() {
        return new TableTester(getTreeAdapter());
    }

    /** {@inheritDoc} */
    public String rcStorePropertyValueAtMousePosition(String variableName,
            final String propertyName) {
        return getTableTester().rcStorePropertyValueAtMousePosition(
                variableName, propertyName);
    }

    /** {@inheritDoc} */
    public void rcSelectCell(final String row, final String rowOperator,
        final String col, final String colOperator,
        final int clickCount, final int xPos, final String xUnits,
        final int yPos, final String yUnits, final String extendSelection,
        int button) 
        throws StepExecutionException {
        getTableTester().rcSelectCell(row, rowOperator, col, colOperator,
            clickCount, xPos, xUnits, yPos, yUnits, extendSelection, button);
    }

    /** {@inheritDoc} */
    public void rcVerifyEditable(final boolean editable, String row,
            String rowOperator, String col, String colOperator, int timeout) {
        getTableTester().rcVerifyEditable(editable, row, rowOperator,
                col, colOperator, timeout);
    }

    /** {@inheritDoc} */
    public void rcVerifyEditableMousePosition(final boolean editable,
            int timeout) {
        getTableTester().rcVerifyEditableMousePosition(editable, timeout);
    }

    /** {@inheritDoc} */
    public void rcVerifyText(final String text, final String operator,
            final String row, final String rowOperator, final String col,
            final String colOperator, int timeout)
            throws StepExecutionException {
        getTableTester().rcVerifyText(text, operator, row, rowOperator,
                col, colOperator, timeout);
    }

    /** {@inheritDoc} */
    public void rcVerifyValueInColumn(final String col,
            final String colOperator, final String value,
            final String operator, final String searchType,
            final boolean exists, int timeout)
        throws StepExecutionException {
        getTableTester().rcVerifyValueInColumn(col, colOperator,
                value, operator, searchType, exists, timeout);
    }

    /** {@inheritDoc} */
    public void rcVerifyValueInRow(final String row, final String rowOperator,
            final String value, final String operator, final String searchType,
            final boolean exists, int timeout)
                    throws StepExecutionException {
        getTableTester().rcVerifyValueInRow(row, rowOperator, value, operator,
                searchType, exists, timeout);
    }

    /** {@inheritDoc} */
    public void rcSelectRowByValue(String col, String colOperator,
            final String value, final String regexOp, int clickCount,
            final String extendSelection, final String searchType, int button) {
        getTableTester().rcSelectRowByValue(col, colOperator, value, regexOp,
                clickCount, extendSelection, searchType, button);
    }

    /** {@inheritDoc} */
    public void rcSelectCellByColValue(String row, String rowOperator,
        final String value, final String regex, int clickCount,
        final String extendSelection, final String searchType, int button) {
        getTableTester().rcSelectCellByColValue(row, rowOperator, value, regex,
                clickCount, extendSelection, searchType, button);
    }

    /** {@inheritDoc} */
    public String rcReadValue(String variable, String row, String rowOperator,
            String col, String colOperator) {
        return getTableTester().rcReadValue(variable, row, rowOperator,
                col, colOperator);
    }

    /** {@inheritDoc} */
    public String rcReadValue(String row, String rowOperator,
            String col, String colOperator) {
        return getTableTester().rcReadValue(row, rowOperator, col, colOperator);
    }

    /** {@inheritDoc} */
    public void rcCheckExistenceOfColumn(final String column,
            final String columnOperator, final boolean exists, int timeout) {
        getTableTester().rcCheckExistenceOfColumn(column, columnOperator,
                exists, timeout);
    }

    /** {@inheritDoc} */
    public String rcReadValueAtMousePosition(String variable) {
        return getTableTester().rcReadValueAtMousePosition(variable);
    }

    /** {@inheritDoc} */
    public String rcReadValueAtMousePosition() {
        return getTableTester().rcReadValueAtMousePosition();
    }

}

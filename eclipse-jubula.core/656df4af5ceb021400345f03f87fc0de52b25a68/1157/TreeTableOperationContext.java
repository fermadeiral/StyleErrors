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
package org.eclipse.jubula.rc.swt.tester.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeTableOperationContext;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.SelectionUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.swt.utils.SwtPointUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author BREDEX GmbH
 */
public class TreeTableOperationContext
        extends AbstractTreeTableOperationContext<Tree, TreeItem> {

    /** The AUT Server logger. */
    private static AutServerLogger log = 
        new AutServerLogger(TreeTableOperationContext.class);
    
    /**
     * Creates a new instance.
     * 
     * @param queuer
     *            the queuer
     * @param robot
     *            the robot
     * @param tree
     *            the tree
     * @param column
     *            the column
     */
    public TreeTableOperationContext(
        IEventThreadQueuer queuer, IRobot robot, Tree tree, int column) {
        super(queuer, robot, tree, column);
    }

    /**
     * Creates a new instance.
     * 
     * @param queuer
     *            the queuer
     * @param robot
     *            the robot
     * @param tree
     *            the tree
     */
    public TreeTableOperationContext(
        IEventThreadQueuer queuer, IRobot robot, Tree tree) {
        super(queuer, robot, tree, 0);
    }
    
    @Override
    public int getNumberOfColumns() {
        return getTreeTable().getColumnCount();
    }

    /**
     * {@inheritDoc}
     */
    public void clickNode(final TreeItem node, ClickOptions clickOps) {
        scrollNodeToVisible(node);
        org.eclipse.swt.graphics.Rectangle visibleItemBounds = 
            getQueuer().invokeAndWait(
                "getVisibleNodeBounds " + node, new IRunnable<org.eclipse.swt.graphics.Rectangle>() { //$NON-NLS-1$
                    public org.eclipse.swt.graphics.Rectangle run() {
                        final Rectangle nodeBounds = 
                            SwtPointUtil.toAwtRectangle(
                                SwtUtils.getRelativeBounds(node, getColumn()));
                        return SwtPointUtil.toSwtRectangle(
                                getVisibleRowBounds(nodeBounds));
                    }
                });
        
        getRobot().click(getTree(), visibleItemBounds,  
            clickOps.setScrollToVisible(false));
    }

    @Override
    public String getRenderedTextOfColumn(final Object node) {
        if (node instanceof TreeItem) {
            final TreeItem treeItem = (TreeItem) node;

            final int userIndex = IndexConverter.toUserIndex(getColumn());
            return getQueuer().invokeAndWait(
                    "getNodeText: " + treeItem + " at column: " + userIndex, //$NON-NLS-1$ //$NON-NLS-2$
                    new IRunnable<String>() {
                        public String run() {
                            return CAPUtil.getWidgetText(treeItem,
                                    SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX
                                            + getColumn(),
                                    treeItem.getText(getColumn()));
                        }
                    });
        }
        return StringConstants.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    public String getRenderedText(TreeItem node) throws StepExecutionException {
        return getRenderedTextOfColumn(node);
    }

    /**
     * {@inheritDoc}
     */
    public void scrollNodeToVisible(final TreeItem node) {
        getQueuer().invokeAndWait("showColumn: " + node, //$NON-NLS-1$
                new IRunnable<Void>() {
                    public Void run() {
                        getTree().showItem(node);
                        getTree().showColumn(getTree().getColumn(getColumn()));
                        return null;
                    }
                });

        final Rectangle nodeBoundsRelativeToParent = getNodeBounds(node);
        final Tree tree = getTree();
        
        getQueuer().invokeAndWait("getNodeBoundsRelativeToParent", //$NON-NLS-1$
            new IRunnable<Void>() {
                public Void run() {
                    org.eclipse.swt.graphics.Point cellOriginRelativeToParent = 
                        tree.getDisplay().map(
                                tree, tree.getParent(), 
                                new org.eclipse.swt.graphics.Point(
                                        nodeBoundsRelativeToParent.x, 
                                        nodeBoundsRelativeToParent.y));
                    nodeBoundsRelativeToParent.x = 
                        cellOriginRelativeToParent.x;
                    nodeBoundsRelativeToParent.y = 
                        cellOriginRelativeToParent.y;
                    return null;
                }
            });

        Control parent = getQueuer().invokeAndWait("getParent", //$NON-NLS-1$
                new IRunnable<Control>() {
                    public Control run() {
                        return tree.getParent();
                    }
                });
        
        getRobot().scrollToVisible(parent, 
                SwtPointUtil.toSwtRectangle(nodeBoundsRelativeToParent));
    }

    @Override
    public int getColumnFromString(final String col, final String operator,
            boolean leaf) {
        // FIXME bjoern check integrity
        int column = -2;
        try {
            int usrIdxCol = Integer.parseInt(col);
            if (usrIdxCol == 0) {
                usrIdxCol = usrIdxCol + 1;
            }
            column = IndexConverter.toImplementationIndex(usrIdxCol);
        } catch (NumberFormatException nfe) {
            try {
                Boolean isVisible = getQueuer().invokeAndWait(
                        "getColumnFromString", //$NON-NLS-1$
                        new IRunnable<Boolean>() {
                            public Boolean run() {
                                return getTree().getHeaderVisible();
                            }
                        });
                if (!(isVisible.booleanValue())) {
                    throw new StepExecutionException("No Header", //$NON-NLS-1$
                            EventFactory.createActionError(
                                    TestErrorEvent.NO_HEADER));
                }

                Integer implCol = getQueuer().invokeAndWait(
                        "getColumnFromString", new IRunnable<Integer>() { //$NON-NLS-1$
                            public Integer run() throws StepExecutionException {
                                for (int i = 0; i < getTree()
                                        .getColumnCount(); i++) {
                                    String colHeader = getColumnHeaderText(i);
                                    if (MatchUtil.getInstance().match(colHeader,
                                            col, operator)) {
                                        return i;
                                    }
                                }
                                return -2;
                            }
                        });
                column = implCol.intValue();
            } catch (IllegalArgumentException iae) {
                // do nothing here
            }
        }
        return column;
    }
    
    @Override
    public String getColumnHeaderText(final int colIdx) {
        return getQueuer().invokeAndWait("getColumnName", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() {
                        final TreeColumn column = getTree().getColumn(colIdx);
                        return CAPUtil.getWidgetText(column, column.getText());
                    }
                });
    }
    
    @Override
    public Rectangle getHeaderBounds(final int col) {
        return getQueuer().invokeAndWait("getHeaderBounds", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() throws StepExecutionException {
                        Tree tree = getTree();
                        org.eclipse.swt.graphics.Rectangle rect = tree
                                .getItem(0).getBounds(col);
                        rect.y = tree.getClientArea().y;
                        return new Rectangle(rect.x, rect.y, rect.width,
                                rect.height);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getNodeBounds(final TreeItem node) {
        org.eclipse.swt.graphics.Rectangle r = 
            getQueuer().invokeAndWait("getNodeBounds: " + node,  //$NON-NLS-1$
                    new IRunnable<org.eclipse.swt.graphics.Rectangle>() {
                    public org.eclipse.swt.graphics.Rectangle run() {
                        Tree tree = getTree();
                        org.eclipse.swt.graphics.Rectangle bounds = 
                            SwtUtils.getBounds(node, getColumn());
                        Point relativeLocation = 
                            tree.toControl(bounds.x, bounds.y);
                        bounds.x = relativeLocation.x;
                        bounds.y = relativeLocation.y;
                        
                        return bounds;
                    }
                });
        Rectangle nodeBounds = new Rectangle(
            r.x, r.y, r.width, r.height);

        return nodeBounds;
    }
    
    /*
     * Actions from TreeOperationContext
     */

    /**
     * @param node
     *            The node.
     * @param row
     *            The node row.
     * @return The converted text
     * @throws StepExecutionException
     *             If the method call fails.
     */
    protected String convertValueToText(final TreeItem node, final int row)
        throws StepExecutionException {
        return getRenderedText(node);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible(final TreeItem node) {
        return getQueuer().invokeAndWait("isVisible", //$NON-NLS-1$
                new IRunnable<Boolean>() {
                    public Boolean run() {
                        TreeItem item = node;
                        boolean vis = true;
                        while (item != null && item.getParentItem() != null) {
                            vis = item.getParentItem().getExpanded();
                            item = item.getParentItem();
                        }
                        return vis;
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public Rectangle getVisibleRowBounds(Rectangle rowBounds) {
        org.eclipse.swt.graphics.Rectangle r = 
            getQueuer().invokeAndWait("getVisibleRowBounds: " + rowBounds,  //$NON-NLS-1$
                    new IRunnable<org.eclipse.swt.graphics.Rectangle>() {
                    public org.eclipse.swt.graphics.Rectangle run() {
                        Tree tree = getTree();
                        org.eclipse.swt.graphics.Rectangle visibleBounds = 
                            tree.getClientArea();
                        return visibleBounds;
                    }
                });
        
        Rectangle visibleTreeBounds = new Rectangle(
            r.x, r.y, r.width, r.height);
        Rectangle visibleRowBounds = visibleTreeBounds.intersection(rowBounds);
        return visibleRowBounds;
    }

    /**
     * {@inheritDoc}
     */
    public void collapseNode(final TreeItem node) {
        final Tree tree = getTree();
        boolean doAction = isExpanded(node);
        if (doAction) {
            if (log.isDebugEnabled()) {
                log.debug("Collapsing node: " //$NON-NLS-1$
                    + node);
            }
            getQueuer().invokeAndWait("collapse", new IRunnable<Void>() { //$NON-NLS-1$
                public Void run() throws StepExecutionException {
                    Event collapseEvent = new Event();
                    collapseEvent.time = (int)System.currentTimeMillis();
                    collapseEvent.type = SWT.Collapse;
                    collapseEvent.widget = tree;
                    collapseEvent.item = node;
                    tree.notifyListeners(SWT.Collapse, collapseEvent);
                    node.setExpanded(false);
                    tree.update();
                    
                    // Return value not used
                    return null;
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    public void expandNode(final TreeItem node) {
        
        final ClassLoader oldCl = Thread.currentThread()
            .getContextClassLoader();

        try {
            final Tree tree = getTree();

            boolean doAction = !isExpanded(node);
            Thread.currentThread().setContextClassLoader(tree.getClass()
                .getClassLoader());

            getQueuer().invokeAndWait("Scroll Tree item: " + node  //$NON-NLS-1$
                + " to visible", new IRunnable<Void>() { //$NON-NLS-1$
                    public Void run() {
                        tree.showItem(node);
                        return null;
                    }
                });

            final Rectangle nodeBounds = getNodeBounds(node);
            Rectangle visibleNodeBounds = getVisibleRowBounds(nodeBounds);
            
            org.eclipse.swt.graphics.Rectangle swtVisibleNodeBounds =
                new org.eclipse.swt.graphics.Rectangle(
                    visibleNodeBounds.x,
                    visibleNodeBounds.y,
                    visibleNodeBounds.width,
                    visibleNodeBounds.height);
            
            getRobot().move(tree, swtVisibleNodeBounds);
            if (doAction) {
                if (log.isDebugEnabled()) {
                    log.debug("Expanding node: " + node); //$NON-NLS-1$ 
                    log.debug("Node bounds   : " + visibleNodeBounds); //$NON-NLS-1$
                }

                getQueuer().invokeAndWait("expand", new IRunnable<Void>() { //$NON-NLS-1$
                    public Void run() throws StepExecutionException {
                        
                        Event expandEvent = new Event();
                        expandEvent.time = (int)System.currentTimeMillis();
                        expandEvent.type = SWT.Expand;
                        expandEvent.widget = tree;
                        expandEvent.item = node;
                        tree.notifyListeners(SWT.Expand, expandEvent);
                        node.setExpanded(true);
                        tree.update();
                        
                        return null;
                    }
                });
                
                
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }

    }

    /**
     * {@inheritDoc}
     */
    public TreeItem[] getRootNodes() {
        return getQueuer().invokeAndWait("getRootNode", //$NON-NLS-1$
                new IRunnable<TreeItem[]>() {
                    public TreeItem[] run() {
                        return getTree().getItems();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isExpanded(final TreeItem node) {
        // FIXME zeb: Verify that getExpanded() works like I think it does:
        //            It should return false if any of the parent nodes are 
        //            collapsed.
        return getQueuer().invokeAndWait("isExpanded: " + node, //$NON-NLS-1$
                new IRunnable<Boolean>() {
                    public Boolean run() {
                        return node.getExpanded();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public void toggleNodeCheckbox(final TreeItem node) {
        scrollNodeToVisible(node);

        getQueuer().invokeAndWait(
                "selectNodeCheckbox", new IRunnable<Void>() { //$NON-NLS-1$
                    public Void run() {
                        Tree tree = getTree();
                        boolean toggledValue = !node.getChecked();
                        node.setChecked(toggledValue);
                        Event toggleEvent = new Event();
                        toggleEvent.type = SWT.Selection;
                        toggleEvent.detail = SWT.CHECK;
                        toggleEvent.widget = tree;
                        toggleEvent.item = node;
                        toggleEvent.button = SWT.BUTTON1;
                        toggleEvent.count = 1;
                        toggleEvent.display = node.getDisplay();
                        tree.notifyListeners(SWT.Selection, toggleEvent);
                        return null;
                    }            
                }); 
    }
    
    /**
     * {@inheritDoc}
     */
    public void verifyCheckboxSelection(final TreeItem node,
            final boolean checked) {
        scrollNodeToVisible(node);
        Boolean checkSelected = getQueuer().invokeAndWait(
                "verifyCheckboxSelection", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        return node.getChecked();
                    }
                });  
        
        Verifier.equals(checked, checkSelected.booleanValue());
    }

    /**
     * {@inheritDoc}
     */
    public TreeItem getChild(final TreeItem parent, final int index) {
        if (parent == null) {
            TreeItem [] rootNodes = getRootNodes();
            if (index < 0 || index >= rootNodes.length) {
                // FIXME zeb: Handle child not found
                return null;
            }
            return rootNodes[index];
        }

        return getQueuer().invokeAndWait(
                "getChild: " + parent + "; With index: " + index, //$NON-NLS-1$ //$NON-NLS-2$
                new IRunnable<TreeItem>() {

                    public TreeItem run() {
                        try {
                            return parent.getItem(index);
                        } catch (IllegalArgumentException iae) {
                            // FIXME zeb: Handle child not found
                            return null;
                        }
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public TreeItem getParent(final TreeItem child) {
        return getQueuer().invokeAndWait("getParent: " + child, //$NON-NLS-1$
                new IRunnable<TreeItem>() {
                    public TreeItem run() {
                        return child.getParentItem();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public TreeItem getSelectedNode() {
        return getSelectedNodes()[0];
    }
    
    /**
     * {@inheritDoc}
     */
    public TreeItem[] getSelectedNodes() {
        return getQueuer().invokeAndWait("getSelectedNodes", //$NON-NLS-1$
                new IRunnable<TreeItem[]>() {
                    public TreeItem[] run() {
                        TreeItem[] selectedItems = getTree().getSelection();
                        SelectionUtil.validateSelection(selectedItems);
                        return selectedItems;
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public TreeItem[] getChildren(final TreeItem parent) {
        if (parent == null) {
            return getRootNodes();
        }
        return getQueuer().invokeAndWait("getChildren: " + parent, //$NON-NLS-1$
                new IRunnable<TreeItem[]>() {
                    public TreeItem[] run() {
                        return parent.getItems();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfChildren(final TreeItem parent) {
        if (parent == null) {
            return getRootNodes().length;
        }
        return getQueuer().invokeAndWait("getChildren: " + parent, //$NON-NLS-1$
                new IRunnable<Integer>() {
                    public Integer run() {
                        return parent.getItemCount();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getNodeTextList(final TreeItem node) {
        final Collection<String> res = new ArrayList<String>();
        
        getQueuer().invokeAndWait("getNodeText: " + node,  //$NON-NLS-1$
            new IRunnable<Void>() {

                public Void run() {
                    int colCount = getTree().getColumnCount();
                    
                    for (int i = 0; i < colCount; i++) {
                        String textAtColumn = CAPUtil.getWidgetText(node,
                                SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX
                                + i, node.getText(i));
                        if (textAtColumn != null) {
                            res.add(textAtColumn);
                        }
                    }
                    
                    String text = CAPUtil.getWidgetText(node, node.getText());
                    if (text != null) {
                        res.add(text);
                    }
                    
                    return null;
                }

            });

        return res;
    }

    /**
     * {@inheritDoc}
     */
    public int getIndexOfChild(final TreeItem parent, final TreeItem child) {
        if (parent != null) {
            return getQueuer().invokeAndWait("getIndexOfChild", //$NON-NLS-1$
                    new IRunnable<Integer>() {
                        public Integer run() throws StepExecutionException {
                            return parent.indexOf(child);
                        }
                    });
        }
     
        TreeItem [] rootNodes = getRootNodes();
        for (int i = 0; i < rootNodes.length; i++) {
            if (rootNodes[i] == child) {
                return i;
            }
        }
        
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeaf(TreeItem node) {
        if (getNumberOfChildren(node) == 0) {
            return true;
        }
        return false;
    }

}
/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextComponent;
import org.eclipse.jubula.rc.common.util.SelectionUtil;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * This context holds the tree and supports access to the Robot. It also
 * implements some general operations on trees.
 *
 * @author BREDEX GmbH
 */
public class TreeOperationContext 
    extends AbstractTreeOperationContext<TreeView<?>, TreeItem<?>> {

    /** The AUT Server logger. */
    private static AutServerLogger log = new AutServerLogger(
            TreeOperationContext.class);

    /**
     * Creates a new instance. The JTree must have a tree model.
     *
     * @param queuer
     *            the queuer
     * @param robot
     *            the Robot
     * @param tree
     *            the tree
     */
    public TreeOperationContext(IEventThreadQueuer queuer, IRobot robot,
            TreeView<?> tree) {
        super(queuer, robot, tree);
        if (tree.getRoot() == null) {
            throw new StepExecutionException(
                    "Tree is empty.", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param row
     *            Not used!
     */
    @Override
    protected String convertValueToText(final TreeItem<?> node, final int row)
        throws StepExecutionException {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "convertValueToText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        if (node != null) {
                            Object val = node.getValue();
                            if (val != null) {
                                return val.toString();
                            }
                        }
                        return null;
                    }
                });
        return result;
    }

    @Override
    public Collection<String> getNodeTextList(TreeItem<?> node) {
        List<String> res = new ArrayList<String>();
        int rowNotUsed = 0;
        String valText = convertValueToText(node, rowNotUsed);
        if (valText != null) {
            res.add(valText);
        }
        String rendText = getRenderedText(node);
        if (rendText != null) {
            res.add(rendText);
        }
        return res;
    }

    @Override
    public String getRenderedText(final TreeItem<?> node)
        throws StepExecutionException {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getRenderedText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        TreeCell cell = getCellForNode(node);
                        if (cell != null) {
                            IComponent adapter = (IComponent) 
                                    AdapterFactoryRegistry.getInstance()
                                    .getAdapter(IComponent.class, cell);
                            if (adapter != null
                                    && adapter instanceof ITextComponent) {
                                return ((ITextComponent) adapter).getText();
                            }
                        }
                        return null;
                    }
                });

        return result;
    }
    
    /**
     * Get the actual TreeCell for a given TreeItem
     * @param node the item
     * @return the cell or null if no cell was found
     */
    public TreeCell getCellForNode(final TreeItem<?> node) {
        scrollNodeToVisible(node);
        TreeView<?> tree = getTree();
        // Update the layout coordinates otherwise
        // we would get old position values
        tree.layout();
        List<? extends TreeCell> tCells = NodeTraverseHelper
                .getInstancesOf(tree, TreeCell.class);
        for (TreeCell<?> cell : tCells) {
            TreeItem<?> item = cell.getTreeItem();
            if (NodeTraverseHelper.isVisible(cell) && item != null
                    && item.equals(node)) {
                return cell;
            }
        }
        return null;
    }

    @Override
    public boolean isVisible(final TreeItem<?> node) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("isVisible", //$NON-NLS-1$
                new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        TreeItem<?> parent = node.getParent();
                        if (parent != null) {
                            return parent.isExpanded() && getTree().isVisible();
                        }
                        return getTree().isVisible();
                    }
                });
    }

    @Override
    public Rectangle getVisibleRowBounds(final Rectangle rowBounds) {
        Rectangle result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getVisibleRowBounds", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TreeView<?> tree = getTree();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        tree.layout();
                        Rectangle visibleTreeBounds = new Rectangle(0, 0,
                                Rounding.round(tree.getWidth()), Rounding
                                        .round(tree.getHeight()));
                        return rowBounds.intersection(visibleTreeBounds);
                    }
                });
        return result;

    }

    @Override
    public void scrollNodeToVisible(final TreeItem<?> node) {
        EventThreadQueuerJavaFXImpl.invokeAndWait("scrollNodeToVisible", //$NON-NLS-1$
                new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        TreeView<?> tree = getTree();
                        int index = ((TreeView) tree).getRow(node);
                        tree.scrollTo(index);
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        tree.layout();
                        return null;
                    }
                });
    }

    @Override
    public void clickNode(TreeItem<?> node, ClickOptions clickOps) {
        scrollNodeToVisible(node);
        Rectangle rowBounds = getNodeBounds(node);
        Rectangle visibleRowBounds = getVisibleRowBounds(rowBounds);
        getRobot().click(getTree(), visibleRowBounds, clickOps);
    }

    @Override
    public void expandNode(final TreeItem<?> node) {
        scrollNodeToVisible(node);
        boolean expanded = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "expandNodeCheckIfExpanded", //$NON-NLS-1$
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return node.isExpanded();
                    }
                });
        if (expanded) {
            return;
        }
        // If this is called during drag mode the target is not visible
        if (DragAndDropHelper.getInstance().isDragMode()) {
            throw new StepExecutionException("Drop target not visible", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_VISIBLE));
        }
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait("expandNode", //$NON-NLS-1$
                new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        TreeView<?> tree = getTree();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        tree.layout();
                        List<? extends TreeCell> tCells = NodeTraverseHelper
                                .getInstancesOf(tree, TreeCell.class);
                        for (TreeCell<?> cell : tCells) {
                            TreeItem<?> item = cell.getTreeItem();
                            if (NodeTraverseHelper.isVisible(cell)
                                    && item != null && item.equals(node)
                                    && !item.isExpanded()) {

                                return cell.getDisclosureNode();
                            }
                        }
                        return null;
                    }
                });
        if (result != null) {
            getRobot().click(result, null,
                    ClickOptions.create().setClickCount(1).setMouseButton(1));
        }
        EventThreadQueuerJavaFXImpl.invokeAndWait("expandNodeCheckIfExpanded", //$NON-NLS-1$
                new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        if (!getTree().isDisabled() && !node.isExpanded()) {
                            log.warn("Expand node fallback used for: " //$NON-NLS-1$
                                    + node.getValue());

                            node.setExpanded(true);
                        }
                        return null;
                    }
                });
    }

    @Override
    public void collapseNode(final TreeItem<?> node) {
        scrollNodeToVisible(node);
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "collapseNode", new Callable<Object>() { //$NON-NLS-1$

                    @Override
                    public Object call() throws Exception {
                        TreeView<?> tree = getTree();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        tree.layout();
                        List<? extends TreeCell> tCells = NodeTraverseHelper
                                .getInstancesOf(tree, TreeCell.class);
                        for (TreeCell<?> cell : tCells) {
                            TreeItem<?> item = cell.getTreeItem();
                            if (NodeTraverseHelper.isVisible(cell) 
                                    && item != null && item.equals(node)
                                    && item.isExpanded()) {

                                return cell.getDisclosureNode();
                            }
                        }
                        return null;
                    }
                });
        if (result != null) {
            getRobot().click(result, null,
                    ClickOptions.create().setClickCount(1).setMouseButton(1));
        }
        EventThreadQueuerJavaFXImpl.invokeAndWait(
                "collapseNodeCheckIfCollapsed", new Callable<Void>() { //$NON-NLS-1$

                    @Override
                    public Void call() throws Exception {
                        if (!getTree().isDisabled()
                                && node.isExpanded()) {
                            log.warn("Collapse node fallback used for: " //$NON-NLS-1$
                                    + node.getValue());

                            node.setExpanded(false);
                        }
                        return null;
                    }
                });
    }

    @Override
    public TreeItem<?> getSelectedNode() {
        TreeItem<?> result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getSelectedNode", new Callable<TreeItem<?>>() { //$NON-NLS-1$

                    @Override
                    public TreeItem<?> call() throws Exception {

                        return getTree().getSelectionModel().getSelectedItem();
                    }
                });
        if (result != null) {
            SelectionUtil.validateSelection(new Object[] { result });
        } else {
            SelectionUtil.validateSelection(new Object[] {});
        }
        return result;
    }

    @Override
    public TreeItem<?>[] getSelectedNodes() {
        TreeItem<?>[] result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getSelectedNode", new Callable<TreeItem<?>[]>() { //$NON-NLS-1$

                    @Override
                    public TreeItem<?>[] call() throws Exception {
                        ObservableList<?> selectedItems = getTree()
                                .getSelectionModel().getSelectedItems();
                        return selectedItems.toArray(new TreeItem[
                                selectedItems.size()]);
                    }
                });
        SelectionUtil.validateSelection(result);
        return result;
    }

    @Override
    public TreeItem<?>[] getRootNodes() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getRootNodes", new Callable<TreeItem<?>[]>() { //$NON-NLS-1$

                    @Override
                    public TreeItem<?>[] call() throws Exception {
                        TreeView<?> tree = getTree();

                        // If the root is visible, just return that.
                        if (tree.showRootProperty().getValue()) {
                            return new TreeItem[] { tree.getRoot() };
                        }

                        // If the root is not visible, return all direct
                        // children of the
                        // non-visible root.
                        return getChildren(tree.getRoot());
                    }
                });
    }

    @Override
    public TreeItem<?> getParent(final TreeItem<?> child) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getParent", //$NON-NLS-1$
                new Callable<TreeItem<?>>() {

                    @Override
                    public TreeItem<?> call() throws Exception {
                        return child.getParent();
                    }
                });
    }

    @Override
    public TreeItem<?> getChild(final TreeItem<?> parent, final int index) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getChild", //$NON-NLS-1$
                new Callable<TreeItem<?>>() {

                    @Override
                    public TreeItem<?> call() throws Exception {

                        return parent.getChildren().get(index);
                    }
                });
    }

    @Override
    public int getNumberOfChildren(final TreeItem<?> parent) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getNumberOfChildren", new Callable<Integer>() { //$NON-NLS-1$

                    @Override
                    public Integer call() throws Exception {
                        return parent.getChildren().size();
                    }
                });
    }

    @Override
    public boolean isLeaf(final TreeItem<?> node) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("isLeaf", //$NON-NLS-1$
                new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        return node.isLeaf();
                    }
                });
    }

    @Override
    public TreeItem<?>[] getChildren(final TreeItem<?> parent) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getChildren", new Callable<TreeItem<?>[]>() { //$NON-NLS-1$

                    @Override
                    public TreeItem<?>[] call() throws Exception {
                        ObservableList<?> children = parent.getChildren();
                        return children.toArray(new TreeItem[children.size()]);
                    }
                });
    }

    @Override
    public Rectangle getNodeBounds(final TreeItem<?> node) {
        Rectangle result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getNodeBounds", new Callable<Rectangle>() { //$NON-NLS-1$
                    @Override
                    public Rectangle call() throws Exception {
                        TreeView<?> tree = getTree();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        tree.layout();
                        List<? extends TreeCell> tCells = NodeTraverseHelper
                                .getInstancesOf(tree, TreeCell.class);
                        for (TreeCell<?> cell : tCells) {
                            TreeItem<?> item = cell.getTreeItem();
                            if (NodeTraverseHelper.isVisible(cell)
                                    && (item != null && item.equals(node))) {

                                Rectangle b = NodeBounds
                                        .getAbsoluteBounds(cell);
                                Rectangle treeB = NodeBounds
                                        .getAbsoluteBounds(tree);
                                return new Rectangle(
                                        Math.abs(treeB.x - b.x),
                                        Math.abs(treeB.y - b.y),
                                        Rounding.round(b.getWidth()),
                                        Rounding.round(b.getHeight()));
                            }
                        }
                        return null;
                    }
                });
        if (result == null) {
            throw new StepExecutionException(
                    "Could not retrieve visible node bounds.", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_VISIBLE));
        }
        return result;
    }

    @Override
    public int getIndexOfChild(final TreeItem<?> parent, 
        final TreeItem<?> child) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getIndexOfChild", new Callable<Integer>() { //$NON-NLS-1$

                    @Override
                    public Integer call() throws Exception {
                        if (parent == null) {
                            Object[] rootNodes = getRootNodes();
                            for (int i = 0; i < rootNodes.length; i++) {
                                if (ObjectUtils.equals(rootNodes[i], child)) {
                                    return i;
                                }
                            }

                            return -1;
                        }
                        List<?> children = parent.getChildren();
                        if (children.contains(child)) {
                            return children.indexOf(child);
                        }
                        return -1;
                    }
                });
    }
}
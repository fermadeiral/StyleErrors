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
package org.eclipse.jubula.rc.swing.tester.util;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.SelectionUtil;
import org.eclipse.jubula.rc.swing.driver.EventThreadQueuerAwtImpl;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;


/**
 * This context holds the tree, the tree model and supports access to the Robot.
 * It also implements some general operations on trees.
 * 
 * @author BREDEX GmbH
 * @created 09.08.2005
 */
public class TreeOperationContext 
    extends AbstractTreeOperationContext<JTree, Object> {
    /** The AUT Server logger. */
    private static AutServerLogger log = 
        new AutServerLogger(TreeOperationContext.class);
    
    /** The tree model. */
    private TreeModel m_model;
    
    /**
     * Creates a new instance. The JTree must have a tree model.
     *      
     * @param queuer The queuer
     * @param robot The Robot
     * @param tree The tree
     */
    public TreeOperationContext(IEventThreadQueuer queuer, IRobot robot,
        JTree tree) {
        
        super(queuer, robot, tree);
        TreeModel model = tree.getModel();
        Validate.notNull(model);
        m_model = model;
    }
    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parent) {
        if (parent == null) {
            return getRootNodes();
        }
        int childCount = m_model.getChildCount(parent);
        List<Object> childList = new ArrayList<Object>();
        for (int i = 0; i < childCount; i++) {
            childList.add(m_model.getChild(parent, i));
        }
        
        return childList.toArray();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getNodeTextList(Object node) {
        Collection<String> res = new ArrayList<String>();
        int row = getRowForTreeNode(node);
        String valText = convertValueToText(node, row);
        if (valText != null) {
            res.add(valText);
        }
        String rendText = getRenderedText(node);
        if (rendText != null) {
            res.add(rendText);
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfChildren(Object parent) {
        if (parent == null) {
            return getRootNodes().length;
        }

        return m_model.getChildCount(parent);
    }

    
    /**
     * Calls
     * {@link JTree#convertValueToText(java.lang.Object, boolean, boolean, boolean, int, boolean)}
     * on the passed JTree.
     * @param node
     *            The node.
     * @param row
     *            The node row.
     * @return The converted text
     * @throws StepExecutionException
     *             If the method call fails.
     */
    protected String convertValueToText(final Object node, final int row)
            throws StepExecutionException {

        return getQueuer().invokeAndWait(
                "convertValueToText", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        return getTree().convertValueToText(node, false,
                                getTree().isExpanded(row),
                                m_model.isLeaf(node), row, false);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public String getRenderedText(final Object node) {
        return getQueuer().invokeAndWait(
                "getRenderedText", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        int row = getRowForTreeNode(node);
                        JTree tree = getTree();
                        Component cellRendererComponent = tree
                                .getCellRenderer()
                                .getTreeCellRendererComponent(tree, node,
                                        false, tree.isExpanded(row),
                                        m_model.isLeaf(node), row, false);
                        try {
                            return TesterUtil
                                    .getRenderedText(cellRendererComponent);
                        } catch (StepExecutionException e) {
                            // This is a valid case in JTrees since if there is no text
                            // there is also no renderer 
                            log.warn("Renderer not supported: " +       //$NON-NLS-1$
                                    cellRendererComponent.getClass(), e); 
                            return null;
                        }
                    }
                });
    }
    
    /**
     * Calls
     * {@link JTree#convertValueToText(java.lang.Object, boolean, boolean, boolean, int, boolean)}
     * on any tree node of the <code>treePath</code> and returns the texts as an array.
     * @param treePath The tree path
     * @return The array of converted texts
     */
    protected String[] convertTreePathToText(Object treePath) {
        final TreePath tp = (TreePath)treePath;
        Object[] path = tp.getPath();
        String[] values = new String[path.length];
        for (int i = 0; i < path.length; i++) {
            values[i] = convertValueToText(path[i], getRowForTreeNode(path[i]));
        }
        return values;
    }
    
    /**
     * Returns the row for the given node. The row is calculated based on how 
     * many nodes are visible above this node.
     * @param node  The node for which to find the row.
     * @return
     *      A zero-based index representing the row that the given node 
     *      occupies in the tree.
     * @throws StepExecutionException
     *      if the node cannot be found.
     */
    protected int getRowForTreeNode(final Object node)
        throws StepExecutionException {
        
        Integer row = getQueuer().invokeAndWait(
            "getRowForTreeNode", new IRunnable<Integer>() { //$NON-NLS-1$
                public Integer run() {
                    TreePath pathToRoot = new TreePath(getPathToRoot(node));
                    return new Integer(getTree().getRowForPath(
                        pathToRoot));
                }
            });
        return row.intValue();
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     * 
     * Contract adapted from javax.swing.tree.DefaultTreeModel.getPathToRoot().
     * 
     * @param node the TreeNode to get the path for
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node.
     */
    private Object[] getPathToRoot(Object node) {
        Object rootNode = m_model.getRoot();
        List<Object> path = getPathToRootImpl(node, rootNode);
        return path.toArray();
    }
    
    /** {@inheritDoc} */
    public Rectangle getNodeBounds(final Object node)
            throws StepExecutionException {

        final int row = getRowForTreeNode(node);
        Rectangle nodeBounds = getQueuer().invokeAndWait(
                "getRowBounds", new IRunnable<Rectangle>() { //$NON-NLS-1$
                    public Rectangle run() {
                        return getTree().getRowBounds(row);
                    }
                });
        if (nodeBounds == null) {
            throw new StepExecutionException(
                    "Could not retrieve visible node bounds.", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_VISIBLE));
        }
        
        return nodeBounds;
    }

    /**
     * Returns the path of all selected values.
     * @return
     *      an array of Objects indicating the selected nodes, or null 
     *      if nothing is currently selected.
     */
    protected Object[] getSelectionPaths() {
        return getQueuer().invokeAndWait("getSelectionPath", //$NON-NLS-1$
                new IRunnable<TreePath[]>() {
                    public TreePath[] run() {
                        return getTree().getSelectionPaths();
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isVisible(final Object node) {
        Boolean visible = getQueuer().invokeAndWait("isVisible", //$NON-NLS-1$
                new IRunnable<Boolean>() {
                    public Boolean run() {
                        Object[] path = getPathToRoot(node);
                        return getTree().isVisible(new TreePath(path));
                    }
                });
        return visible.booleanValue();
    }

    /**
     * Getter for the model
     * @return Returns the model.
     */
    protected TreeModel getModel() {
        return m_model;
    }
    
    /**
     * {@inheritDoc}
     */
    public Rectangle getVisibleRowBounds(Rectangle rowBounds) {
        Rectangle visibleTreeBounds = getTree().getVisibleRect();
        Rectangle visibleRowBounds = visibleTreeBounds.intersection(rowBounds);
        return visibleRowBounds;
    }

    /** {@inheritDoc} */
    public void collapseNode(Object node) {
        alterExpansionState(node, false);
    }

    /** {@inheritDoc} */
    public void expandNode(Object node) {
        alterExpansionState(node, true);
    }
    
    /**
     * @param node
     *            the node
     * @param shouldBeExpanded
     *            whether the nodes state is supposed to be expanded or
     *            collapsed
     */
    private void alterExpansionState(Object node, 
        final boolean shouldBeExpanded) {
        final JTree tree = getTree();
        final ClassLoader oldCl = Thread.currentThread()
            .getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(tree.getClass()
                .getClassLoader());
            final int row = getRowForTreeNode(node);
            final Rectangle nodeBounds = getNodeBounds(node);
            final boolean expanded = tree.isExpanded(row);
            boolean doAction = expanded != shouldBeExpanded;
            final IEventThreadQueuer queuer = new EventThreadQueuerAwtImpl();

            queuer.invokeAndWait("scrollRowToVisible", new IRunnable<Void>() { //$NON-NLS-1$
                public Void run() {
                    tree.scrollRowToVisible(row);
                    return null;
                }
            });

            Rectangle visibleNodeBounds = getVisibleRowBounds(nodeBounds);
            getRobot().move(tree, visibleNodeBounds);
            if (doAction) {
                if (log.isDebugEnabled()) {
                    log.debug(doAction ? "Expanding" : "Collapsing" //$NON-NLS-1$ //$NON-NLS-2$  
                            + " node: " + node); //$NON-NLS-1$
                    log.debug("Row           : " + row); //$NON-NLS-1$
                    log.debug("Node bounds   : " + visibleNodeBounds); //$NON-NLS-1$
                }
                queuer.invokeAndWait("alteringExpansionState", new IRunnable<Void>() { //$NON-NLS-1$
                    public Void run() {
                        if (shouldBeExpanded) {
                            tree.expandRow(row);
                        } else {
                            tree.collapseRow(row);
                        }
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
    public Object[] getRootNodes() {
        JTree tree = getTree();
        
        // If the root is visible, just return that.
        if (tree.isRootVisible()) {
            return new Object [] {tree.getModel().getRoot()};
        }
        
        // If the root is not visible, return all direct children of the 
        // non-visible root.
        return getChildren(tree.getModel().getRoot());
    }

    /**
     * {@inheritDoc}
     */
    public void scrollNodeToVisible(Object node) {
        getTree().scrollRowToVisible(getRowForTreeNode(node));
    }

    /**
     * {@inheritDoc}
     */
    public Object getChild(Object parent, int index) {

        try {
            if (parent == null) {
                Object [] rootNodes = getRootNodes();
                return rootNodes[index];
            }
            return m_model.getChild(parent, index);
        } catch (ArrayIndexOutOfBoundsException e) {
            // FIXME zeb: Deal with child not found
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getParent(Object child) {
        Object parent = null;
        if (child instanceof TreeNode) {
            // This is the easy way.
            parent = ((TreeNode)child).getParent();
        } else {
            // Great. The node doesn't implement TreeNode. Looks like we'll
            // have to_do things the hard way.
            Object[] pathToRoot = getPathToRoot(child);
            if (pathToRoot.length > 1) {
                // parent is the next-to-last element in the path
                parent = pathToRoot[pathToRoot.length - 2];
            }
        } 
        
        // If the parent is the actual root node, and the root is not visible,
        // then treat the child as one of the "root" nodes.
        if (parent != null 
            && parent.equals(m_model.getRoot()) 
            && !getTree().isRootVisible()) {
            parent = null;
        }
        
        return parent;
    }

    /**
     * Recursively builds and returns the path to root for <code>node</code>.
     * The contract is similar to that of 
     * javax.swing.tree.DefaultTreeModel.getPathToRoot().
     * 
     * @param node The node for which to find the path to root.
     * @param currentNode The node currently being checked.
     * @return a List containing the elements of the path in the proper order.
     */
    private List<Object> getPathToRootImpl(Object node, Object currentNode) {
        if (ObjectUtils.equals(currentNode, node)) {
            List<Object> retList = new ArrayList<Object>();
            retList.add(currentNode);
            return retList;
        }
        
        int childCount = m_model.getChildCount(currentNode);
        for (int i = 0; i < childCount; i++) {
            List<Object> path = getPathToRootImpl(
                    node, m_model.getChild(currentNode, i)); 
            if (path != null) {
                // prepend the current node to the path and return
                path.add(0, currentNode);
                return path;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void clickNode(Object node, ClickOptions clickOps) {
        scrollNodeToVisible(node);
        Rectangle rowBounds = getNodeBounds(node);
        Rectangle visibleRowBounds = getVisibleRowBounds(rowBounds);
        getRobot().click(getTree(), visibleRowBounds, clickOps);
    }

    /**
     * {@inheritDoc}
     */
    public Object getSelectedNode() {
        TreePath[] paths = getCheckedSelectedPaths();
        SelectionUtil.validateSelection(paths);
        return paths[0].getLastPathComponent();
    }

    /**
     * Returns the selected tree paths.
     * 
     * @return The path
     */
    private TreePath[] getCheckedSelectedPaths() {
        TreePath[] paths = (TreePath[])getSelectionPaths();
        SelectionUtil.validateSelection(paths);
        return paths;
    }
    /**
     * {@inheritDoc}
     */
    public int getIndexOfChild(Object parent, Object child) {

        if (parent != null) {
            return getTree().getModel().getIndexOfChild(parent, child);
        }
        
        Object [] rootNodes = getRootNodes();
        for (int i = 0; i < rootNodes.length; i++) {
            if (ObjectUtils.equals(rootNodes[i], child)) {
                return i;
            }
        }

        return -1;

    }
    /**
     * {@inheritDoc}
     */
    public boolean isLeaf(Object node) {
        return m_model.isLeaf(node);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object[] getSelectedNodes() {
        TreePath[] paths = getCheckedSelectedPaths();
        Object[] nodes = new Object[paths.length];
        for (int i = 0; i < paths.length; i++) {
            nodes[i] = paths[i].getLastPathComponent();
        }
        SelectionUtil.validateSelection(paths);
        return nodes;
    }

}
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
package org.eclipse.jubula.rc.common.implclasses.tree;

import java.awt.Rectangle;
import java.util.Collection;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;


/**
 * This context holds the tree, the tree model and supports access to the Robot.
 * It also implements some general operations on trees.
 * 
 * @author BREDEX GmbH
 * @created 09.08.2005
 * @param <TREE_TYPE>
 *            the tree type
 *  @param <NODE_TYPE>
 *            the node type          
 */
public abstract class AbstractTreeOperationContext<TREE_TYPE, NODE_TYPE> {
    /** The tree. */
    private TREE_TYPE m_tree;
    /** The Robot. */
    private IRobot m_robot;
    /** The event thread queuer. */
    private IEventThreadQueuer m_queuer;
    
    /**
     * Creates a new instance.
     * 
     * @param queuer The queuer
     * @param robot The Robot
     * @param tree The tree
     */
    public AbstractTreeOperationContext(IEventThreadQueuer queuer, IRobot robot,
        TREE_TYPE tree) {
        
        m_queuer = queuer;
        m_robot = robot;
        m_tree = tree;
    }
    
    /**
     * @return The Robot.
     */
    public IRobot getRobot() {
        return m_robot;
    }
    /**
     * @return The event thread queuer.
     */
    public IEventThreadQueuer getQueuer() {
        return m_queuer;
    }

    /**
     * Calls {@link JTree#convertValueToText(java.lang.Object, boolean, boolean, boolean, int, boolean)} on the passed JTree.
     * @param node The node.
     * @param row The node row.
     * @return The converted text
     * @throws StepExecutionException If the method call fails.
     */
    protected abstract String convertValueToText(final NODE_TYPE node, 
        final int row) throws StepExecutionException;
    
    /**
     * Returns the result of <code>toString</code> on the node value and, if 
     * it can be obtained, the rendered value. Consider using 
     * {@link #getRenderedText(Object)} instead if only the rendered text is 
     * needed.
     * 
     * @param node the node
     * @return a collection with the text strings of the node
     */
    // FIXME zeb:   Returning more than one value could be a problem because
    //              the user only sees one value (the rendered text). This can
    //              lead to unpredictable tests. The problem is that customers 
    //              may already rely on this "bug". As a result, we cannot 
    //              change this method (or calls to this method) without 
    //              breaking backwards compatibility for tests.
    public abstract Collection<String> getNodeTextList(NODE_TYPE node);
    
    /**
     * Returns the rendered text for the given node.
     * 
     * @param node The tree node for which to get the rendered text.
     * @return the rendered text for the given node, or <code>null</code> if 
     *         the given node does not have rendered text or if the rendered
     *         text cannot be determined.
     * @throws StepExecutionException If the method call fails (for example, 
     *                                if the given node could not be found).
     */
    public abstract String getRenderedText(final NODE_TYPE node)
        throws StepExecutionException;
    
    /**
     * Returns <code>true</code> if the node is visible
     * @param node a node
     * @return if the node is visible
     */
    public abstract boolean isVisible(final NODE_TYPE node);

    /**
     * Getter for the tree
     * @return Returns the tree.
     */
    protected TREE_TYPE getTree() {
        return m_tree;
    }
    
    /**
     * Computes the visible rowBounds inside the visible bounds of the tree.<br>
     * The result is the intersection of the visible bounds of the tree and the 
     * rowBounds of the node.
     * @param rowBounds the rowBounds of the node to click in. These bounds must
     *                  be relative to the tree's location.
     * @return the visible rowBounds, relative to the tree's location.
     */
    public abstract Rectangle getVisibleRowBounds(Rectangle rowBounds);  
    
    /**
     * Scrolls the Tree's container, if necessary, in order to ensure that the
     * given node is viewable.
     * @param node  The node 
     */
    public abstract void scrollNodeToVisible(NODE_TYPE node);

    /**
     * Move the mouse pointer directly over the given node's onscreen location
     * and perform a mouse click.
     * @param node      The node
     * @param clickOps  The click options
     */
    public abstract void clickNode(NODE_TYPE node, ClickOptions clickOps);

    /**
     * Expands the given node.
     * @param node  The node
     */
    public abstract void expandNode(NODE_TYPE node);

    /**
     * Collapses the given node.
     * @param node  The node
     */
    public abstract void collapseNode(NODE_TYPE node);

    /**
     * @return  The currently selected node.
     */
    // FIXME zeb: The behavior of this method when multiple nodes are selected
    //            is undefined.
    public abstract NODE_TYPE getSelectedNode();
    
    /**
     * @return  The currently selected nodes.
     */
    public abstract NODE_TYPE[] getSelectedNodes();
    
    /**
     * @return  All top-level nodes for the tree. These are the highest-level
     *          nodes that the user can see.
     */
    public abstract NODE_TYPE[] getRootNodes();
    
    /**
     * @param child The child node.
     * @return  The parent node of the given node, or <code>null</code> if the
     *          node is a graphical root node.
     */
    public abstract NODE_TYPE getParent(NODE_TYPE child);

    /**
     * @param parent    The parent node. Can be <code>null</code>.
     * @param index     The index for the child node.
     * @return  the child node of <code>parent</code> with index <code>index
     *          </code> in the <code>parent</code>'s child node list.
     *          If <code>parent</code> is <code>null</code>, then the <i>n</i>th
     *          graphical root node will be returned, where <i>n</i> is equal
     *          to <code>index</code> (i.e. when <code>index == 0</code>, the 
     *          topmost root node will be returned).
     */
    public abstract NODE_TYPE getChild(NODE_TYPE parent, int index);
    
    /**
     * @param parent    The parent node. Can be <code>null</code>.
     * @return  the number of child nodes for <code>parent</code>, or the number
     *          of graphical root nodes if <code>parent == null</code>.
     */
    public abstract int getNumberOfChildren(NODE_TYPE parent);
 
    /**
     * @param node the node to check
     * @return true if the node is a leaf
     */
    public abstract boolean isLeaf(NODE_TYPE node);
    
    /**
     * 
     * @param parent    The parent node. Can be <code>null</code>.
     * @return  an <code>Array</code> containing all child nodes of <code>
     *          parent</code>, or all root nodes if <code>parent</code> is
     *          <code>null</code>.
     */
    public abstract NODE_TYPE[] getChildren(NODE_TYPE parent);
    
    /**
     * @param node  The node for which to find the bounds.
     * @return  the graphical bounds of the node, as a 
     *          <code>java.awt.Rectangle</code>.
     */
    public abstract Rectangle getNodeBounds(NODE_TYPE node);
    
    /**
     * @param parent    The parent node, can be <code>null</code>
     * @param child     The child node
     * @return  the index of the given <code>child</code> in the given 
     * <code>parent</code>'s child node list, or -1 if <code>child</code>
     * is not a child of <code>parent</code>. If <code>parent</code> is 
     * <code>null</code>, then <code>child</code> is assumed to be a root node,
     * and the returned index will be determined by the order in which the root
     * nodes are displayed (i.e. the topmost root node will have an index of 0).
     */
    public abstract int getIndexOfChild(NODE_TYPE parent, NODE_TYPE child);
}
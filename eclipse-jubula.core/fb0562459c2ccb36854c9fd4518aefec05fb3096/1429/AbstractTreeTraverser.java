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
package org.eclipse.jubula.client.core.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Traverses a tree of <code>T</code> instances top-down. The traversal starts 
 * at the root node passed to the constructor. The recursion is established by 
 * calling {@link #getChildIterator(Object)} on any node. On the way top-down, 
 * the <code>operate()</code> method of the passed operation is called for 
 * any node.
 * <br/><br/>
 * Based on {@link TreeTraverser}, which should eventually be replaced by a 
 * concrete class extending this class.
 * 
 * @param <T> The class of object contained in the tree to be traversed.
 * 
 * @author BREDEX GmbH
 * @created 02.06.2010
 */
public abstract class AbstractTreeTraverser<T> {
    
    /** constant for no maximum traversal depth */
    public static final int NO_DEPTH_LIMIT = -1;

    /**
     * The tree operation.
     */
    private List<ITreeNodeOperation<T>> m_operations = 
        new ArrayList<ITreeNodeOperation<T>>();
    /**
     * The root node.
     */
    private T m_rootNode;
    
    /** 
     * The maximum traversal depth. <code>NO_DEPTH_LIMIT</code> by default. 
     */
    private int m_maxDepth = NO_DEPTH_LIMIT;

    /**
     * The constructor.
     * 
     * @param rootNode
     *            The node where the traversion starts
     */
    public AbstractTreeTraverser(T rootNode) {
        m_rootNode = rootNode;
    }

    /**
     * The constructor.
     * 
     * @param rootNode
     *            The node where the traversion starts
     * @param operation
     *            The operation to call on any node
     */
    public AbstractTreeTraverser(T rootNode, 
            ITreeNodeOperation<T> operation) {
        this(rootNode);
        m_operations.add(operation);
    }

    /**
     * The constructor.
     * 
     * @param rootNode
     *            The node where the traversion starts
     * @param operation
     *            The operation to call on any node
     * @param maxTraversalDepth The maximum depth of traversal. 
     */
    public AbstractTreeTraverser(T rootNode, 
            ITreeNodeOperation<T> operation, int maxTraversalDepth) {
        this(rootNode, operation);
        m_maxDepth = maxTraversalDepth;
    }

    /**
     * Implements the recursive traversion.
     * 
     * @param context
     *            The context
     * @param parent
     *            The parent node
     * @param node
     *            The current node
     */
    protected void traverseImpl(ITreeTraverserContext<T> context, 
            T parent, T node) {
        if (m_maxDepth == NO_DEPTH_LIMIT 
                || m_maxDepth > context.getCurrentTreePath().size()) {
            context.append(node);
            Set<ITreeNodeOperation<T>> suspendedOps = null;
                
            for (ITreeNodeOperation<T> operation : m_operations) {
                boolean continueWork =
                        operation.operate(context, parent, node, false);
                if (!continueWork) {
                    if (suspendedOps ==  null) {
                        suspendedOps = new HashSet<ITreeNodeOperation<T>>(
                                m_operations.size());
                    }
                    suspendedOps.add(operation);
                }
            }
            if (suspendedOps != null) {
                m_operations.removeAll(suspendedOps);
            }
            
            if (context.isContinue() && !m_operations.isEmpty()) {
                for (Iterator<T> iter = getChildIterator(node); 
                        iter.hasNext(); ) {
                    traverseImpl(context, node, iter.next());
                }
            
            }
            if (suspendedOps != null) {
                m_operations.addAll(suspendedOps);
            }
            if (context.isContinue()) {
                for (ITreeNodeOperation<T> operation : m_operations) {
                    operation.postOperate(context, parent, node, 
                            false);
                }                
            }
            context.removeLast();
        }
    }

    /**
     * @param node A node in the traversed tree.
     * @return an iterator that returns the children of the given node.
     */
    protected abstract Iterator<T> getChildIterator(T node);

    /**
     * Starts the traversion of the tree under the root node passed to the
     * constructor. Event handlers are not included during the traversion.
     */
    public void traverse() {
        traverseImpl(new TreeTraverserContext<T>(m_rootNode), null, m_rootNode);
    }
    
    /**
     * 
     * @return the tree node operation
     */
    protected List<ITreeNodeOperation<T>> getOperations() {
        return m_operations;
    }
    
    /**
     * adds a <code>ITreeNodeOperation</code> to the list of operations
     * that are executed on every step
     * 
     * @param op
     *      <code>ITreeNodeOperation</code>
     */
    public void addOperation(ITreeNodeOperation<T> op) {
        m_operations.add(op);
    }

}

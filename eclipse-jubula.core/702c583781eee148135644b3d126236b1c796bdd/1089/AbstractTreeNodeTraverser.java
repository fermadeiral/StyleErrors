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

import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * Traverses a given tree node path.
 *
 * @author BREDEX GmbH
 * @created Nov 30, 2006
 */
public abstract class AbstractTreeNodeTraverser {
    
    /** The tree operation context. */
    private AbstractTreeOperationContext m_context;
    
    /** The constraint on operations applied using this Traverser. */
    private TreeNodeOperationConstraint m_constraint;
    
    /**
     * Creates a new instance with the given Context. 
     * @param context The context for this Traverser
     */
    AbstractTreeNodeTraverser(AbstractTreeOperationContext context) {
        this(context, null);
    }

    /**
     * Creates a new instance to traverse the passed tree node path.
     * 
     * @param context The context for this Traverser
     * @param constraint The constraint for this Traverser
     */
    AbstractTreeNodeTraverser(AbstractTreeOperationContext context, 
            TreeNodeOperationConstraint constraint) {
        
        m_context = context;
        m_constraint = constraint;
    }

    /**
     * @return the context for this Traverser
     */
    protected AbstractTreeOperationContext getContext() {
        return m_context;
    }

    /**
     * Calls the passed operation.
     * @param node The node to operate on
     * @param operation The tree operation to execute
     */
    protected void callOperation(Object node, TreeNodeOperation operation) {
        operation.operate(node);
    }

    /**
     * Checks the constraints to see if the operation should be executed
     * @param currentTraversalIndex The current index of traversal
     * @param traversalSize The size of the path for this Traverser
     * @return true if the operation should be executed
     */
    protected boolean isOperable(int currentTraversalIndex, 
            int traversalSize) {
        
        if (m_constraint != null) {
            return m_constraint.isOperable(currentTraversalIndex, 
                    traversalSize);
        } 
        
        return true;
    }

    /**
     * Traverses the tree node path and calls the passed tree node operation. 
     * Implementors should honor the <code>isOperable</code> hook method before
     * executing an operation on a node. 
     * @param operation The tree node operation
     * @param startNode The traversal will begin with this node
     * @throws StepExecutionException  If the tree path is invalid or if the <code>operate()</code> method of the operation fails.
     */
    public abstract void traversePath(TreeNodeOperation operation,
            Object startNode)
        throws StepExecutionException;
}

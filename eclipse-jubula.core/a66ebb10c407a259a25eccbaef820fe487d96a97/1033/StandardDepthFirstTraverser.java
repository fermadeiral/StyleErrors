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
 * Performs depth-first traversal of a tree, with an optional starting node. If
 * the starting node is defined, then the traversal will not cover any nodes 
 * that are "above" the starting node in the tree. For example, using the 
 * following tree:
 * 
 * <pre>
 * Root1
 *   |- ChildA
 *        |- SubchildA
 *        |- SubchildB
 *   |- ChildB
 *        |- SubchildA
 *        |- SubchildB
 * Root2
 *   |- ChildA
 *        |- SubchildA
 *        |- SubchildB
 *   |- ChildB
 *        |- SubchildA
 *        |- SubchildB
 * Root3
 *   |- ChildA
 *        |- SubchildA
 *        |- SubchildB
 *   |- ChildB
 *        |- SubchildA
 *        |- SubchildB
 * </pre>
 * 
 * if the path to the starting node is /Root2/ChildA/SubchildB, then no nodes
 * "above" /Root2/ChildA/SubchildB will be covered by the traversal. As such, 
 * the following nodes would not be traversed: Root1 (and all descendants) and  
 * /Root2/ChildA/SubchildA.
 *
 * @author BREDEX GmbH
 * @created Jul 27, 2010
 */
public class StandardDepthFirstTraverser extends AbstractTreeNodeTraverser {

    /**
     * Constructor
     * 
     * @param context The traversal context.
     */
    public StandardDepthFirstTraverser(AbstractTreeOperationContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    public void traversePath(TreeNodeOperation operation, Object startNode)
        throws StepExecutionException {

        operation.setContext(getContext());

        traversePath(startNode, operation);
        
        Object currentNode = startNode;
        while (currentNode != null) {
            Object parent = getContext().getParent(currentNode);
            int childIndexOfCurrentNode = 
                getContext().getIndexOfChild(parent, currentNode);
            Object [] currentNodeSiblings = getContext().getChildren(parent);
            for (int i = childIndexOfCurrentNode + 1; 
                    i < currentNodeSiblings.length; i++) {
                
                traversePath(currentNodeSiblings[i], operation);
            }
            
            // Very important to increment the while loop condition
            currentNode = parent;
        }

    }

    /**
     * Recursively traverses the tree node path.
     * 
     * @param node
     *            The currently traversed tree node.
     * @param operation
     *            The tree node operation.
     * @throws StepExecutionException
     *             If the {@link TreeNodeOperation#operate(Object)} method of 
     *             the operation fails.
     */
    private void traversePath(Object node, TreeNodeOperation operation) 
        throws StepExecutionException {

        if (node != null) {
            callOperation(node, operation);
        }
        int childCount = getContext().getNumberOfChildren(node);
        for (int i = 0; i < childCount; i++) {
            Object child = getContext().getChild(node, i);
            traversePath(child, operation);
        }

    }

}

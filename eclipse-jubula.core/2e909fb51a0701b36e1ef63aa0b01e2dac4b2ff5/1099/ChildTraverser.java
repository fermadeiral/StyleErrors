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
 * Traverses tree nodes by always visiting the first child of the current node.
 *
 * @author BREDEX GmbH
 * @created Nov 30, 2006
 */
public class ChildTraverser extends AbstractChildTraverser {

    /**
     * @param context context
     * @param distance distance
     */
    public ChildTraverser(AbstractTreeOperationContext context, int distance) {
        super(context, distance);
    }

    /**
     * @param context context
     * @param distance distance
     * @param constraint constraint
     */
    public ChildTraverser(AbstractTreeOperationContext context, int distance, 
            TreeNodeOperationConstraint constraint) {
        super(context, distance, constraint);
    }

    /**
     * {@inheritDoc}
     */
    public void traversePath(TreeNodeOperation operation, Object startNode)
        throws StepExecutionException {

        operation.setContext(getContext());
        if (isOperable(-1, getDistance())) {
            callOperation(startNode, operation);
        }
        Object node = startNode;
        for (int i = 0; i < getDistance(); i++) {
            
            // Equivalent to node.isLeaf()
            if (getContext().getNumberOfChildren(node) == 0) { 
                throwTreeNodeNotFound(node);
            } else {
                node = getContext().getChild(node, 0);
                if (isOperable(i, getDistance())) {
                    callOperation(node, operation);
                }
            }
        }
    }

}
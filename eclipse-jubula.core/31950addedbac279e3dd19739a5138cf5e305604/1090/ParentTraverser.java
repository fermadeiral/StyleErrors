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
 * Traverses tree nodes by always visiting the parent of the current node.
 *
 * @author BREDEX GmbH
 * @created Nov 30, 2006
 */
public class ParentTraverser extends AbstractParentTraverser {

    
    /**
     * Create a new ParentTraverser.
     * @param context   The context
     * @param distance  The distance
     */
    public ParentTraverser(AbstractTreeOperationContext context, int distance) {
        super(context, distance);
    }

    /**
     * Create a new ParentTraverser.
     * @param context       The context
     * @param distance      The distance
     * @param constraint    The constraint
     */
    public ParentTraverser(AbstractTreeOperationContext context, int distance, 
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
            if (getContext().getParent(node) == null) {
                throwTreeNodeNotFound(node);
            } else {
                node = getContext().getParent(node);
                if (isOperable(i, getDistance())) {
                    callOperation(node, operation);
                }
            }
        }
    }

}
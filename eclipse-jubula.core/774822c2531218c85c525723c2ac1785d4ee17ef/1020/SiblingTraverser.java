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
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;


/**
 * Traverses tree nodes by always visiting the siblings of the current node.
 * This traversal is performed either forward (always visiting the next 
 * sibling) or backward (always visiting the previous sibling).
 *
 * @author BREDEX GmbH
 * @created Nov 30, 2006
 */
public class SiblingTraverser extends DistanceBasedTraverser {

    /** <code>true</code> if the traversal should move forward */
    private boolean m_isSearchForward;

    /**
     * @param context The context
     * @param distance The distance
     * @param searchForward 
     *          <code>true</code> if the Traverser should move forward.
     */
    public SiblingTraverser(AbstractTreeOperationContext context, 
            int distance, boolean searchForward) {
        
        super(context, distance);
        m_isSearchForward = searchForward;
    }

    /**
     * @param context The context
     * @param distance The distance
     * @param searchForward <code>true</code> if the Traverser should move forward.
     * @param constraint The constraint
     */
    public SiblingTraverser(AbstractTreeOperationContext context, 
            int distance, boolean searchForward, 
            TreeNodeOperationConstraint constraint) {
        
        super(context, distance, constraint);
        m_isSearchForward = searchForward;
    }

    /**
     * {@inheritDoc}
     */
    public void traversePath(TreeNodeOperation operation, 
        Object startNode) throws StepExecutionException {

        operation.setContext(getContext());
        int modifier = m_isSearchForward ? 1 : -1;
        Object node = startNode;
        Object parent = getContext().getParent(node);

        int curIndex = getContext().getIndexOfChild(parent, node);
        if (isOperable(-1, getDistance())) {
            callOperation(startNode, operation);
        }
        for (int i = 0; i < getDistance(); i++) {
            curIndex += modifier;
            if (curIndex < 0 
                || curIndex >= getContext().getNumberOfChildren(parent)) {
                throwTreeNodeNotFound(node);
            }
            node = getContext().getChild(parent, curIndex);
            if (isOperable(i, getDistance())) {
                callOperation(node, operation);
            }
        }
    }

    /**
     * Handles a "tree node not found" event
     * 
     * @param startNode
     *            The node that doesn't have the target sibling
     * @throws StepExecutionException
     *             To indicate that the tree node could not be found
     */
    private void throwTreeNodeNotFound(Object startNode)
        throws StepExecutionException {
        TestErrorEvent event = EventFactory
                .createActionError(TestErrorEvent.TREE_NODE_NOT_FOUND);
        throw new StepExecutionException(
                "Tree node not found: Sibling of " //$NON-NLS-1$
                        + startNode.toString(), event);
    }

}
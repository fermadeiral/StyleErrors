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
 * Traverses tree nodes by always visiting the parent of the current node.
 *
 * @author BREDEX GmbH
 * @created Nov 30, 2006
 */
public abstract class AbstractParentTraverser extends DistanceBasedTraverser {
    /**
     * {@inheritDoc}
     * @param context the context
     */
    public AbstractParentTraverser(AbstractTreeOperationContext context, 
        int distance) {
        
        super(context, distance);
    }

    /**
     * {@inheritDoc}
     * @param context the context
     */
    public AbstractParentTraverser(AbstractTreeOperationContext context, 
        int distance, TreeNodeOperationConstraint constraint) {
        
        super(context, distance, constraint);
    }

    /**
     * {@inheritDoc}
     */
    public abstract void traversePath(TreeNodeOperation operation, 
        Object startNode) throws StepExecutionException;

    /**
     * Handles a "tree node not found" event
     * @param orphan The node that has no parent
     * @throws StepExecutionException To indicate that the tree node could not be found
     */
    protected void throwTreeNodeNotFound(Object orphan)
        throws StepExecutionException {
        TestErrorEvent event = EventFactory
                .createActionError(TestErrorEvent.TREE_NODE_NOT_FOUND);
        throw new StepExecutionException(
                "Tree node not found: Parent of " //$NON-NLS-1$
                        + orphan.toString(), event);
    }
}
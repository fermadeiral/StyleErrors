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
 * Traverses tree nodes by always visiting the first child of the current node.
 *
 * @author BREDEX GmbH
 * @created Nov 30, 2006
 */
public abstract class AbstractChildTraverser extends DistanceBasedTraverser {

    /**
     * @param context context
     * @param distance distance
     */
    public AbstractChildTraverser(AbstractTreeOperationContext context, 
        int distance) {
        
        super(context, distance);
    }

    /**
     * @param context context
     * @param distance distance
     * @param constraint constraint
     */
    public AbstractChildTraverser(AbstractTreeOperationContext context, 
        int distance, TreeNodeOperationConstraint constraint) {
        
        super(context, distance, constraint);
    }

    /**
     * {@inheritDoc}
     */
    public abstract void traversePath(TreeNodeOperation operation, 
        Object startNode)throws StepExecutionException;

    /**
     * Handles a "tree node not found" event 
     * @param childless The node that has no children
     * @throws StepExecutionException To indicate that the tree node could not be found
     */
    protected void throwTreeNodeNotFound(Object childless)
        throws StepExecutionException {
        TestErrorEvent event = EventFactory
                .createActionError(TestErrorEvent.TREE_NODE_NOT_FOUND);
        throw new StepExecutionException(
                "Tree node not found: Children of " //$NON-NLS-1$
                        + childless.toString(), event);
    }

}

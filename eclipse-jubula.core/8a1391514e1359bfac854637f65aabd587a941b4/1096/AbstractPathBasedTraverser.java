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
 * Traverses based on a given path.
 *
 * @author BREDEX GmbH
 * @created Dec 1, 2006
 */
public abstract class AbstractPathBasedTraverser 
    extends AbstractTreeNodeTraverser {

    /** The tree node path. */
    private INodePath m_treePath;

    /**
     * @param context The context
     * @param treePath  A series of <code>Object</code>s representing tree nodes. The
     *      Traverser attempts to follow this path.
     */
    public AbstractPathBasedTraverser(AbstractTreeOperationContext context, 
            INodePath treePath) {
        super(context);
        
        m_treePath = treePath;
    }

    /**
     * @param context The context
     * @param treePath A series of <code>Object</code>s representing tree nodes. The Traverser attempts to follow this path.
     * @param constraint The constraint
     */
    public AbstractPathBasedTraverser(AbstractTreeOperationContext context, 
        INodePath treePath, TreeNodeOperationConstraint constraint) {
        
        super(context, constraint);
        m_treePath = treePath;
    }

    /**
     * Handles a "tree node not found" event 
     * @param level The level
     * @throws StepExecutionException To indicate that the tree node could not be found
     */
    protected void throwTreeNodeNotFound(int level) 
        throws StepExecutionException {
        
        TestErrorEvent event = EventFactory
                .createActionError(TestErrorEvent.TREE_NODE_NOT_FOUND);
        throw new StepExecutionException(
                "Tree node not found: " + m_treePath.getObject(level), event); //$NON-NLS-1$      
    }

    /**
     * {@inheritDoc}
     * Traverses the tree node path passed to the constructor and calls the
     * passed tree node operation.
     * @param operation The tree node operation
     * @throws StepExecutionException If the tree path is invalid or if the <code>operate()</code>
     *             method of the operation fails.
     */
    public abstract void traversePath(TreeNodeOperation operation, 
        Object startNode) throws StepExecutionException;

}
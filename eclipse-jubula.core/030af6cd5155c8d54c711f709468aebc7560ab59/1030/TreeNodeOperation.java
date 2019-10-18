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
 * This tree node operation is called by the
 * {@link org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeTraverser}
 * on any node the traverser passes on its top-down traversal of the Tree.
 * Classes that implement this interface may, for example, expand or collapse
 * the passed tree node.
 * 
 * @author BREDEX GmbH
 * @created 16.03.2005
 * @param <NODE_TYPE>
 *            the node type
 */
public interface TreeNodeOperation<NODE_TYPE> {

    /**
     * Sets the tree operation context.
     * 
     * @param context
     *            The context
     */

    public void setContext(AbstractTreeOperationContext context);

    /**
     * Operates on the passed tree node. This method is called by the
     * <code>TreeNodeTraverser</code> on any node that is specified in the tree
     * node path.
     * 
     * @param node
     *            The current tree node.
     * @return <code>true</code> if the <code>TreeNodeTraverser</code> should
     *         proceed.
     * @throws StepExecutionException
     *             If the operation, e.g. the node selection or expansion,
     *             fails.
     */
    public boolean operate(NODE_TYPE node) throws StepExecutionException;
}

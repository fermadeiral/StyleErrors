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


/**
 * This interface represents an operation on a tree node. The tree traverser
 * calls the <code>operate()</code> method on any node when it traverses
 * the tree top-down. The <code>postOperate()</code> method is called
 * when the traverser traverses its way back (bottom-up).
 *
 * @param <T> The class of nodes handled by the operation.
 * 
 * @author BREDEX GmbH
 * @created 13.09.2005
 *
 */
public interface ITreeNodeOperation<T> {
    /**
     * Implements the operation on a node of the tree.
     * 
     * @param ctx
     *            The traverser context
     * @param parent
     *            The parent node of the passed <code>node</code> in the mean
     *            of the tree top-down layout. This is usually not the node
     *            provided by a call of <code>node.getParentNode()</code>
     * @param node
     *            The node
     * @param alreadyVisited
     *              true if this node has been visited by the traverser before
     * @return true if the children of this node shall be visited, false if this
     *              branch shall be cut
     */
    public boolean operate(ITreeTraverserContext<T> ctx, T parent, 
        T node, boolean alreadyVisited);
    /**
     * Implements the post operation on a tree node.
     * 
     * @param ctx
     *            The traverser context
     * @param parent
     *            The parent node of the passed <code>node</code> in the mean
     *            of the tree top-down layout. This is usually not the node
     *            provided by a call of <code>node.getParentNode()</code>
     * @param node
     *            The node
     * @param alreadyVisited
     *              true if this node has been visited by the traverser before
     */
    public void postOperate(ITreeTraverserContext<T> ctx, T parent,
        T node, boolean alreadyVisited);
}

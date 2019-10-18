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

import java.util.List;


/**
 * Defines the context of the tree traverser.
 * 
 * @param <T> The class of objects traversed within this context.
 * 
 * @author BREDEX GmbH
 * @created 13.09.2005
 *
 */
public interface ITreeTraverserContext<T> {
    /**
     * @return The actual tree path as a list. The first element of the list is
     *         the root node passed to the constructor of the tree traverser. The
     *         last node is the node passed as <code>node</code> to
     *         {@link ITreeNodeOperation#operate(ITreeTraverserContext, INodePO, INodePO)}
     *         or
     *         {@link ITreeNodeOperation#postOperate(ITreeTraverserContext, INodePO, INodePO)}
     */
    public List<T> getCurrentTreePath();
    /**
     * @return The root node passed to the constructor of the tree traverser
     */
    public T getRootNode();
    /**
     * @param continued
     *            If set to <code>false</code>, the tree traverser stops,
     *            <code>true</code> has no effect
     */
    public void setContinued(boolean continued);
    
    /**
     * 
     * @return <code>true</code> if the receiver indicates that traversal should
     *         continue. Otherwise, <code>false</code>.
     */
    public boolean isContinue();
    
    /**
     * @param node node to append to tree path
     */
    public void append(T node);
    
    /**
     * removes the last item from tree path
     */
    public void removeLast();
}

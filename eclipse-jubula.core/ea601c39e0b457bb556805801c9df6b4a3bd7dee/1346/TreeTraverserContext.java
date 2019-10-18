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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * Concrete implementation of a tree traverser context.
 *
 * @param <T> The class of objects contained within the tree being traversed.
 *
 * @author BREDEX GmbH
 * @created May 28, 2010
 */
public class TreeTraverserContext<T> implements ITreeTraverserContext<T> {
    /**
     * The current tree path (top-down).
     */
    private LinkedList<T> m_treePath = new LinkedList<T>();
    /**
     * Flag to indicate if the traversion should be continued.
     */
    private boolean m_continue = true;

    /** the root node of the tree being traversed */
    private T m_rootNode;

    /**
     * Constructor
     * 
     * @param rootNode The root node of the tree being traversed.
     */
    public TreeTraverserContext(T rootNode) {
        m_rootNode = rootNode;
    }
    
    /**
     * @return The actual tree path as a list. The first element of the list is
     *         the root node passed to the constructor of the tree traverser. The
     *         last node is the node passed as <code>node</code> to
     *         <br>{@link ITreeNodeOperation#operate(ITreeTraverserContext, INodePO, INodePO)}
     *         <br>or
     *         <br>{@link ITreeNodeOperation#postOperate(ITreeTraverserContext, INodePO, INodePO)}
     *         <br>The returned list is unmodifiable.
     */
    public List<T> getCurrentTreePath() {
        return Collections.unmodifiableList(m_treePath);
    }
    /**
     * {@inheritDoc}
     */
    public T getRootNode() {
        return m_rootNode;
    }
    /**
     * {@inheritDoc}
     */
    public void setContinued(boolean continued) {
        m_continue = continued;
    }
    
    
    /**
     * @return continue or not
     */
    public boolean isContinue() {
        return m_continue;
    }
    /**
     * {@inheritDoc}
     */
    public void append(T node) {
        m_treePath.add(node);
        
    }
    /**
     * {@inheritDoc}
     */
    public void removeLast() {
        m_treePath.removeLast();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        try {
            INodePO [] treePath = 
                m_treePath.toArray(new INodePO [m_treePath.size()]);
            StringBuilder sb = new StringBuilder();
            for (INodePO node : treePath) {
                sb.append(StringConstants.SLASH).append(node.getName());
            }
            if (sb.length() == 0) {
                sb.append(StringConstants.SLASH);
            }
            return sb.toString();
        } catch (Throwable t) {
            return super.toString();
        }
    }
}

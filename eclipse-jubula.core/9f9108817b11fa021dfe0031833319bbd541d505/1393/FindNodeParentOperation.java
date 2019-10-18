/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.utils;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.model.INodePO;

/**
 * Operation for finding the parent of a given node.
 */
public class FindNodeParentOperation 
    extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {

    /** the found parent */
    private INodePO m_parent;
    
    /** the node for which to find the parent */
    private INodePO m_child;

    /**
     * Constructor
     * 
     * @param child The node for which to find the parent. Must not be 
     *              <code>null</code>.
     */
    public FindNodeParentOperation(INodePO child) {
        Validate.notNull(child);
        m_child = child;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public boolean operate(ITreeTraverserContext<INodePO> ctx, INodePO parent,
            INodePO node, boolean alreadyVisited) {

        if (m_child.equals(node)) {
            m_parent = parent;
        }
        
        return m_parent == null;
    }

    /**
     * 
     * @return the parent found for the given child node, or <code>null</code>
     *         if no parent was found. Always returns <code>null</code> if 
     *         called before starting traversal. 
     */
    public INodePO getParent() {
        return m_parent;
    }
}

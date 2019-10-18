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



/**
 * This is a base class for all tree node operations. It acts as a
 * decorator if it is created with another tree node operation as its
 * parameter. The <code>operate()</code> method delegates to the tree node
 * operation if it is set.
 *
 * @author BREDEX GmbH
 * @created 22.03.2005
 * @param <NODE_TYPE>
 *            the node type
 */
public abstract class AbstractTreeNodeOperation<NODE_TYPE> implements
    TreeNodeOperation<NODE_TYPE> {
    /**
     * The tree operation context.
     */
    private AbstractTreeOperationContext m_context;
    
    /**
     * Getter for the tree operation context.
     * 
     * @return Returns the context.
     */
    public AbstractTreeOperationContext getContext() {
        return m_context;
    }
    
    /**
     * Setter for the tree operation context
     * 
     * @param context The context to set.
     */
    public void setContext(AbstractTreeOperationContext context) {
        m_context = context;
    }
}
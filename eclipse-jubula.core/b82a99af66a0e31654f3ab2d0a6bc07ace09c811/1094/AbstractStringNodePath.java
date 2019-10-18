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
 * @author BREDEX GmbH
 * @created Dec 8, 2006
 */
public abstract class AbstractStringNodePath implements INodePath {

    /** The tree path */
    private String[] m_treePath;

    /** The matching operator */
    private String m_operator;
    
    /**
     * @param path The tree node path. Must be an array of Strings representing a series of tree nodes.
     * @param operator If regular expressions are used to match the tree path
     */
    public AbstractStringNodePath(String[] path, String operator) {
        m_treePath = path;
        m_operator = operator;
        
    }
    
    /**
     * {@inheritDoc}
     */
    public int getLength() {
        return m_treePath.length;
    }

    /**
     * {@inheritDoc}
     */
    public Object getObject(int level) {
        return m_treePath[level];
    }

    /**
     * @param node The node
     * @param level The level
     * @param context The context
     * @return <code>true</code> if the given node's string representation or 
     * rendered text match the String at the given level in the path.
     */
    public abstract boolean isInPath(Object node, int level, 
            AbstractTreeOperationContext context);

    /**
     * {@inheritDoc}
     */
    public abstract INodePath subPath(int startIndex, int endIndex);

    /**
     * @return the operator
     */
    public String getOperator() {
        return m_operator;
    }

    /**
     * @return the treePath
     */
    public String[] getTreePath() {
        return m_treePath;
    }
}

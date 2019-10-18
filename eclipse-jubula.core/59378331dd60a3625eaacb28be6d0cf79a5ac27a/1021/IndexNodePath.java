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
 * @created Dec 6, 2006
 */
public class IndexNodePath implements INodePath {

    /** The tree path */
    private Integer[] m_treePath;
    
    /**
     * 
     * @param path the path
     */
    public IndexNodePath(Integer[] path) {
        m_treePath = path;
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
     * {@inheritDoc}
     */
    public boolean isInPath(Object node, int level, 
            AbstractTreeOperationContext context) {
        
        Object parent = context.getParent(node);

        int nodeIndex = context.getIndexOfChild(parent, node);
        return m_treePath[level].intValue() == nodeIndex;
    }

    /**
     * {@inheritDoc}
     */
    public INodePath subPath(int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex > getLength() || startIndex > endIndex) {
            throw new IndexOutOfBoundsException("Invalid index"); //$NON-NLS-1$
        }
        Integer[] newTreePath = new Integer[endIndex - startIndex];
        for (int i = startIndex; i < endIndex; i++) {
            newTreePath[i] = m_treePath[startIndex + i];
        }
        return new IndexNodePath(newTreePath);
    }

}

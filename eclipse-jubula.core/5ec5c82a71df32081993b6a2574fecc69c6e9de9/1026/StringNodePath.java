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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jubula.rc.common.util.MatchUtil;


/**
 * @author BREDEX GmbH
 * @created Dec 8, 2006
 */
public class StringNodePath extends AbstractStringNodePath {
    
    /**
     * @param path The tree node path. Must be an array of Strings representing a series of tree nodes.
     * @param operator If regular expressions are used to match the tree path
     */
    public StringNodePath(String[] path, String operator) {
        super(path, operator);
        
    }
    
    /**
     * {@inheritDoc}
     */
    public int getLength() {
        return getTreePath().length;
    }

    /**
     * {@inheritDoc}
     */
    public Object getObject(int level) {
        return getTreePath()[level];
    }

    /**
     * @param node The node
     * @param level The level
     * @param context The context
     * @return <code>true</code> if the given node's string representation or 
     * rendered text match the String at the given level in the path.
     */
    public boolean isInPath(Object node, int level, 
        AbstractTreeOperationContext context) {
        
        Collection nodeTextList = context.getNodeTextList(node);
        String pattern = getTreePath()[level];

        for (Iterator it = nodeTextList.iterator(); it.hasNext(); ) {
            String text = (String)it.next();
            if (MatchUtil.getInstance().match(text, pattern, getOperator())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public INodePath subPath(int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex > getLength() || startIndex > endIndex) {
            throw new IndexOutOfBoundsException("Invalid index"); //$NON-NLS-1$
        }
        String[] newTreePath = new String[endIndex - startIndex];
        for (int i = startIndex; i < endIndex; i++) {
            newTreePath[i] = getTreePath()[startIndex + i];
        }
        return new StringNodePath(newTreePath, getOperator());
    }

}

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
 * Represents a user-provided tree path.
 *
 * @author BREDEX GmbH
 * @created Dec 6, 2006
 */
public interface INodePath {
    
    /**
     * Hook method to check that the given node is in the tree path at the
     * given level.
     * @param node 
     *          The node currently being traversed
     * @param level 
     *          The current level of the path that is being traversed.
     * @param context The context to check against    
     * @return <code>true</code> if the given node matches the tree
     *  path in <code>level</code>), otherwise <code>false</code>
     */
    public boolean isInPath(Object node, int level, 
            AbstractTreeOperationContext context);
    
    /**
     * 
     * @param level
     *          The level.
     * @return  The Object at the given level of the path.
     */
    public Object getObject(int level);
    
    /**
     * 
     * @return  The length of the path.
     */
    public int getLength();

    /**
     * Returns a subpath of this INodePath, containing all elements
     * from startIndex (inclusive) to endIndex (exclusive).
     * 
     * @param startIndex
     *          The starting index for the subpath (inclusive).
     * @param endIndex
     *          The ending index for the subpath (exclusive).
     * @return
     *          An <code>INodePath</code> that is a subpath of this
     *          <code>INodePath</code>.
     * @throws IndexOutOfBoundsException for an illegal endpoint index value
     *     (startIndex &lt; 0 || endIndex &gt; getLength() || startIndex &gt; endIndex).
     */
    public INodePath subPath(int startIndex, int endIndex) 
        throws IndexOutOfBoundsException;

}
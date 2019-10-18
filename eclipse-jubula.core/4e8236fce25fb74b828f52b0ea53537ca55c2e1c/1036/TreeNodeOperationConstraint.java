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
 * This class constrains <code>TreeNodeOperation</code>s. That is, it allows
 * Traversers to determine whether an operation should be applied at a given 
 * point in the traversal.
 * @author BREDEX GmbH
 * @created Nov 30, 2006
 */
public class TreeNodeOperationConstraint {

    /**
     * Determines whether or not a TreeNodeOperation should be executed.
     * The operation should be executed if this is the last node in the path.
     * @param currentTraversalIndex The current index of traversal
     * @param traversalSize The total path length, or distance, that the traverser will travel
     * @return <code>true</code> if the operation should be executed
     */
    public boolean isOperable(int currentTraversalIndex,
            int traversalSize) {
        return (currentTraversalIndex == (traversalSize - 1));
    }
}
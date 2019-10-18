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
 * Traverser that follows a pre-specified path, to a given distance. A Traverser
 * of this type could, for example, traverse by always visiting the parent of 
 * the current node.
 *
 * @author BREDEX GmbH
 * @created Nov 30, 2006
 */
public abstract class DistanceBasedTraverser extends AbstractTreeNodeTraverser {

    /** The distance this Traverser should travel */
    private int m_distance;

    /**
     * @param context The context
     * @param distance 
     *      The distance this Traverser should travel
     */
    public DistanceBasedTraverser(AbstractTreeOperationContext context, 
            int distance) {
        super(context);
        m_distance = distance;
    }

    /**
     * @param context The context
     * @param distance 
     *      The distance this Traverser should travel
     * @param constraint The constraint
     */
    public DistanceBasedTraverser(AbstractTreeOperationContext context, 
            int distance, TreeNodeOperationConstraint constraint) {
        super(context, constraint);
        m_distance = distance;
    }

    /**
     * 
     * @return the total distance that this Traversal should travel
     */
    protected int getDistance() {
        return m_distance;
    }
}
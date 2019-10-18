/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.utils;

import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;

/**
 * Class used to determine whether an insertion of a new node is allowed
 *  and to calculate exactly where to insert a node in an Editor
 * Used both for validating and executing drop and paste actions
 * The validation takes into account only the types of the nodes,
 *    not other requirements like parameters, etc.
 * @author BREDEX GmbH
 *
 */
public class NodeTargetCalculator {
    
    /** hidden constructor */
    private NodeTargetCalculator() {
        // empty
    }

    /**
     * Calculates exactly where to insert a new node
     *      we assume that the insertion has been validated before
     *      by calling canInsert
     * @param source the source
     * @param target the target
     * @param pos the position (BEFORE, AFTER, ON, etc)
     * @param expanded whether the target is expanded
     * @return the drop target data or null if no drop
     */
    public static NodeTarget calcNodeTarget(INodePO source, INodePO target,
            int pos, boolean expanded) {
        
        INodePO top = target.getSpecAncestor();
        
        if (top instanceof ISpecTestCasePO || top instanceof ITestSuitePO) {
            return calcNodeTargetTCEditor(source, target, pos, expanded);
        }
        
        if (top instanceof ITestJobPO) {
            int posi = top.indexOf(target);
            return new NodeTarget(posi + 1, top);
        }
        
        return null;
    }

    /**
     * Calculates the drop target in a Test Case Editor
     * @param source the source (null if new)
     * @param target the target
     * @param pos the position
     * @param expanded whether target is expanded
     * @return the position or null
     */
    private static NodeTarget calcNodeTargetTCEditor(INodePO source,
            INodePO target, int pos, boolean expanded) {
        NodeTarget res = null;
        if (target instanceof ISpecTestCasePO
                || target instanceof ITestSuitePO
                || (target instanceof IAbstractContainerPO
                        && pos != ViewerDropAdapter.LOCATION_BEFORE)) {
            if (expanded) {
                res = new NodeTarget(0, target);
            } else {
                res = new NodeTarget(target.getNodeListSize(), target);
            }
        }  else if (target instanceof IAbstractContainerPO) {
            // the location must be BEFORE, so we insert to the end of the previous container
            int posi = target.getParentNode().indexOf(target);
            if (posi != 0) {
                INodePO newTarg = target.getParentNode().
                        getUnmodifiableNodeList().get(posi - 1);
                res = new NodeTarget(newTarg.getNodeListSize(), newTarg);
            }
        } else {
            int newPos = target.getParentNode().indexOf(target);
            if (pos != ViewerDropAdapter.LOCATION_BEFORE) {
                newPos++;
            }
            res = new NodeTarget(newPos, target.getParentNode());
        }
        if (res != null && source != null && source.hasCircularDependencies(
                res.getNode().getSpecAncestor())) {
            return null;
        }
        return res;
    }
    
    /** Class representing a drop target */
    public static class NodeTarget {
        /** the node */
        private INodePO m_node;
        /** the position */
        private int m_position;
        /**
         * Constructor
         * @param pos position
         * @param node node
         */
        public NodeTarget(int pos, INodePO node) {
            m_node = node;
            m_position = pos;
        }
        /**
         * getter
         * @return node
         */
        public INodePO getNode() {
            return m_node;
        }
        /**
         * getter
         * @return position
         */
        public int getPos() {
            return m_position;
        }
    }
    
}

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

import java.util.List;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * checks for nodes if they have dependencies with a certain parent
 * @author BREDEX GmbH
 * @created 26.09.2005
 */
public class DependencyCheckerOp 
    extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {

    /**
     * dependency finder implementation
     */
    private DependencyFinderOp m_dependencyFinder;
    
    /**
     * the path to the node that would cause circular dependencies
     */
    private String m_dependencyPath;

    /**
     * contructor
     * @param node
     *      INodePO
     */
    public DependencyCheckerOp(INodePO node) {
        m_dependencyFinder = new DependencyFinderOp(node);
    }

    /**
     * {@inheritDoc}
     *      org.eclipse.jubula.client.core.model.NodePO,
     *      org.eclipse.jubula.client.core.model.NodePO)
     * @param ctx
     *            ITreeTraverserContext
     * @param parent
     *            INodePO
     * @param node
     *            INodePO
     */
    public boolean operate(ITreeTraverserContext<INodePO> ctx, INodePO parent, 
        INodePO node, boolean alreadyVisited) {

        if (hasDependency()) {
            ctx.setContinued(false);
            return true;
        }

        m_dependencyFinder.operate(ctx, parent, node, alreadyVisited);
        if (hasDependency()) {
            buildPathString(ctx.getCurrentTreePath());
        }
        
        return true;
    }

    /**
     * @param currentTreePath List of all nodes visited
     */
    private void buildPathString(List<INodePO> currentTreePath) {
        StringBuilder sb =  new StringBuilder();
        for (INodePO node : currentTreePath) {
            sb.append(StringConstants.SLASH);
            sb.append(node.getName());
        }
        m_dependencyPath = sb.toString();
    }

    /**
     * returns true if any dependency found
     * @return
     *      boolean
     */
    public boolean hasDependency() {
        return !m_dependencyFinder.getDependentNodes().isEmpty();
    }

    /**
     * @return the path where the dependency is located
     */
    public String getDependencyPath() {
        return m_dependencyPath;
    }
}

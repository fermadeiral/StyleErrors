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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;


/**
 * checks for nodes if they have dependencies with a certain parent
 * @author BREDEX GmbH
 * @created 26.09.2005
 */
public class DependencyFinderOp 
    extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
    /**
     * what node could become parent
     */
    private INodePO m_parentCandidate;

    /**
     * list of dependent nodes
     */
    private Set <INodePO> m_dependentNodes = new HashSet<INodePO>();

    /**
     * Constructor
     * 
     * @param node
     *      INodePO
     */
    public DependencyFinderOp(INodePO node) {
        m_parentCandidate = node;
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

        
        if (node instanceof IExecTestCasePO) {
            IExecTestCasePO execTC = (IExecTestCasePO) node;
            if (m_parentCandidate.equals(execTC.getSpecTestCase())) {
                m_dependentNodes.add(node);
                for (INodePO pathNode : ctx.getCurrentTreePath()) {
                    if (pathNode instanceof ISpecTestCasePO) {
                        m_dependentNodes.add(pathNode);
                    }
                }
            }
        } else if (node.equals(m_parentCandidate)) {
            m_dependentNodes.add(node);
        }
        return true;
    }

    /**
     * returns true if any dependency found
     * @return
     *      boolean
     */
    public Set <INodePO> getDependentNodes() {
        return m_dependentNodes;
    }
}

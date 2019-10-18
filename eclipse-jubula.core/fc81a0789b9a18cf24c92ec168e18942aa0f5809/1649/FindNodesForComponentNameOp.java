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
package org.eclipse.jubula.client.core.businessprocess.treeoperations;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;


/**
 * Operation for finding all nodes that use a specific Component Name.
 *
 * @author BREDEX GmbH
 * @created Mar 2, 2009
 */
public class FindNodesForComponentNameOp 
    extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {

    /** responsible NodePO */
    private Set<INodePO> m_nodes = new HashSet<INodePO>();

    /** GUID of Component Name to use for this operation */
    private String m_compNameGuid;

    /**
     * Constructor
     * 
     * @param compNameGuid The GUID of the Component Name to use for this 
     *                     operation.
     */
    public FindNodesForComponentNameOp(String compNameGuid) {
        setCompNameGuid(compNameGuid);
    }

    /**
     * {@inheritDoc}
     */
    public boolean operate(ITreeTraverserContext<INodePO> ctx, INodePO parent, 
            INodePO node, boolean alreadyVisited) {
        if (alreadyVisited) {
            return false;
        }
        if (node instanceof ICapPO) {
            ICapPO cap = (ICapPO)node;
            if (getCompNameGuid().equals(cap.getComponentName())) {
                getNodes().add(cap);
            }
        } else if (node instanceof IExecTestCasePO) {
            IExecTestCasePO execTc = (IExecTestCasePO)node;
            for (ICompNamesPairPO pair : execTc.getCompNamesPairs()) {
                if (pair.getFirstName().equals(getCompNameGuid())
                        || pair.getSecondName().equals(getCompNameGuid())) {
                    getNodes().add(execTc);
                }
            }
        }
        return true;
    }

    /**
     * All nodes using this name
     * 
     * @return Set
     */
    public Set<INodePO> getNodes() {
        return m_nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    protected void setNodes(Set<INodePO> nodes) {
        m_nodes = nodes;
    }

    /**
     * @param compNameGuid the compNameGuid to set
     */
    protected void setCompNameGuid(String compNameGuid) {
        m_compNameGuid = compNameGuid;
    }

    /**
     * @return the compNameGuid
     */
    protected String getCompNameGuid() {
        return m_compNameGuid;
    }
}

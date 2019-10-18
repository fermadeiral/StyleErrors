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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;

/**
 * Operation for checking whether the visited cap is deprecated.
 *
 * @author BREDEX GmbH
 * @created Mar 6, 2009
 */
public class CheckIfCAPisDeprecated 
    extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
    
    /**
     * result list of deprecated nodes
     */
    private Set<INodePO> m_nodes = new HashSet<INodePO>();
    

    /**
     * Constructor
     */
    public CheckIfCAPisDeprecated() {
    }
    
    /** {@inheritDoc} */
    public boolean operate(ITreeTraverserContext<INodePO> ctx, INodePO parent,
            INodePO node, boolean alreadyVisited) {
        if (node instanceof IExecTestCasePO) {
            final IExecTestCasePO exec = (IExecTestCasePO) node;
            IProjectPO project = GeneralStorage
                    .getInstance().getProject();
            ISpecTestCasePO specTestCase = exec.getSpecTestCase();
            if (project == null) {
                return false;
            } else if (specTestCase == null) {
                return true;
            }
            Long projectId = project.getId();
            if (specTestCase.getParentProjectId() != projectId
                    && (projectId.equals(exec.getParentProjectId())
                    || projectId.equals(exec.getParentNode()
                            .getParentProjectId()))) {
                String name = specTestCase.getName();
                if (StringUtils.containsIgnoreCase(name, "deprecated")) { //$NON-NLS-1$
                    // We are just checking for the string deprecated
                    m_nodes.add(exec);
                }
            }
        }
        return true;
    }

    /**
     * @return Get deprecated nodes found during traversing
     */
    public Set<INodePO> getDeprecatedNodes() {
        return m_nodes;
    }

    
    
}

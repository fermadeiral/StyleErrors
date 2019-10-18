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
package org.eclipse.jubula.client.ui.rcp.handlers.filter.testcases;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.NodePM;


/**
 * @author BREDEX GmbH
 * @created 03.07.2009
 */
public class FilterUsedTestCases extends ViewerFilter {
    /** local cache */
    private Map<INodePO, Boolean> m_alreadyVisited = 
        new HashMap<INodePO, Boolean>();
    
    /**
     * {@inheritDoc}
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof ISpecTestCasePO) {
            ISpecTestCasePO tc = (ISpecTestCasePO)element;
            List<IExecTestCasePO> execTestCases;
            if (!m_alreadyVisited.containsKey(tc)) {
                execTestCases = NodePM.getInternalExecTestCases(
                        tc.getGuid(), tc.getParentProjectId());
                if (execTestCases.isEmpty()) {
                    m_alreadyVisited.put(tc, Boolean.TRUE);
                    return true;
                }
            } else {
                return m_alreadyVisited.get(tc).booleanValue();
            }
            return false;
        } else if (element instanceof ICategoryPO) {
            ICategoryPO cat = (ICategoryPO)element;
            for (INodePO child : cat.getUnmodifiableNodeList()) {
                if (!m_alreadyVisited.containsKey(child)) {
                    if (select(viewer, parentElement, child)) {
                        m_alreadyVisited.put(child, Boolean.TRUE);
                        return true;
                    }
                } else {
                    return m_alreadyVisited.get(child).booleanValue();
                }
            }
            return false;
        }
        return true;
    }
    
    /**
     * clear the cache
     */
    public void resetCache() {
        m_alreadyVisited.clear();
    }
}

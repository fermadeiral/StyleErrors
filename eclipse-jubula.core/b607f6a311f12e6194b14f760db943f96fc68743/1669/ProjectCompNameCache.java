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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;

/**
 * Component Name Cache for a whole project
 * @author BREDEX GmbH
 *
 */
public class ProjectCompNameCache extends BasicCompNameCache {

    /**
     * Constructor
     * @param context the context
     */
    public ProjectCompNameCache(IPersistentObject context) {
        super(context);
    }

    /**
     * Resets the GUID for reuse locations of certain Component Names to match
     * the GUID of the corresponding existing Component Names.
     *
     * @author BREDEX GmbH
     * @created Feb 10, 2009
     */
    private class ExistingCompTypeHandler 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
        /** 
         * mapping from GUID of Component Names to save to GUID of existing 
         * Component Names with the same name 
         */
        private Map<String, String> m_guidToCompNameMap;
        
        /**
         * Constructor
         * 
         * @param guidToCompNameMap Mapping from GUID of Component Names to 
         *                          save to GUID of existing Component Names 
         *                          with the same name. 
         */
        public ExistingCompTypeHandler(Map<String, String> guidToCompNameMap) {
            m_guidToCompNameMap = guidToCompNameMap;
        }

        /**
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx, 
                INodePO parent, INodePO node, boolean alreadyVisited) {

            if (node instanceof IExecTestCasePO) {
                IExecTestCasePO execTc = (IExecTestCasePO)node;
                for (ICompNamesPairPO pair : execTc.getCompNamesPairs()) {
                    if (m_guidToCompNameMap.containsKey(pair.getFirstName())) {
                        pair.setFirstName(
                            m_guidToCompNameMap.get(pair.getFirstName()));
                    }
                    if (m_guidToCompNameMap.containsKey(pair.getSecondName())) {
                        pair.setSecondName(
                            m_guidToCompNameMap.get(pair.getSecondName()));
                    }
                }
            } else if (node instanceof ICapPO) {
                ICapPO capPo = (ICapPO)node;
                if (m_guidToCompNameMap.containsKey(capPo.getComponentName())) {
                    capPo.setComponentName(
                        m_guidToCompNameMap.get(capPo.getComponentName()));
                }
            }
            return true;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleExistingNames(Map<String, String> guidToCompNameMap) {
        ExistingCompTypeHandler operation = 
            new ExistingCompTypeHandler(guidToCompNameMap);
        new TreeTraverser((IProjectPO) getContext(), operation, true).
            traverse(true);
        for (IAUTMainPO aut : ((IProjectPO) getContext()).getAutMainList()) {
            for (IObjectMappingAssoziationPO assoc 
                    : aut.getObjMap().getMappings()) {
                
                Set<String> guidIntersection = new HashSet<String>();
                guidIntersection.retainAll(assoc.getLogicalNames());
                for (String guid : guidIntersection) {
                    assoc.removeLogicalName(guid);
                    assoc.addLogicalName(guidToCompNameMap.get(guid));
                }
            }
        }
        
    }
}

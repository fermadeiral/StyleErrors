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
package org.eclipse.jubula.client.core.businessprocess.treeoperations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.MapCounter;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;

/**
 * @author BREDEX GmbH
 * @created 15.Jul.2016
 */
public class CountCompNameUsage 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
    
    /** GUIDs of the used component names */
    private MapCounter m_usageByGuid;
    
    /** Id of the current project */
    private Long m_id;
    
    /** The Component Name Manager */
    private CompNameManager m_man;
    
    /** the operation used to find instances of use */

    /**
     * Constructor
     * @param usageMap the usage Map
     */
    public CountCompNameUsage(MapCounter usageMap) {
        m_id = GeneralStorage.getInstance().getProject().getId();
        m_man = CompNameManager.getInstance();
        m_usageByGuid = usageMap;
    }
    
    /** {@inheritDoc} */
    public boolean operate(
            ITreeTraverserContext<INodePO> ctx, INodePO parent, INodePO node, 
            boolean alreadyVisited) {
        if (alreadyVisited) {
            return false;
        }
        Integer res;
        if (node instanceof ICapPO) {
            ICapPO cap = (ICapPO)node;
            addOne(cap.getComponentName());
        } else if (node instanceof IExecTestCasePO
                && m_id.equals(node.getParentProjectId())) {
            IExecTestCasePO execTc = (IExecTestCasePO)node;
            Set<String> collectedFirst = new HashSet<>();
            for (ICompNamesPairPO pair : execTc.getCompNamesPairs()) {
                addOne(pair.getSecondName());
                addOne(pair.getFirstName());
                collectedFirst.add(pair.getFirstName());
            }
            ISpecTestCasePO spec = execTc.getSpecTestCase();
            if (spec == null) {
                return false;
            }
            collectSpecialFirstRefs(spec, collectedFirst);
            return false;
        }
        return true;
    }
    
    /**
     * Collecting first references from pairs which are automatically created
     *      these pairs are not included in the data structure, but can be seen in Editors...
     * @param spec the SpecTC 
     * @param alreadyCollected the already collected first references - to avoid double collection...
     */
    private void collectSpecialFirstRefs(ISpecTestCasePO spec,
            Set<String> alreadyCollected) {
        for (Iterator<INodePO> it = spec.getAllNodeIter(); it.hasNext(); ) {
            INodePO next = it.next();
            String guid = null;
            if (next instanceof ICapPO) {
                guid = ((ICapPO) next).getComponentName();
                if (!alreadyCollected.contains(guid)) {
                    addOne(guid);
                    addOne(guid);
                    alreadyCollected.add(guid);
                }
            } else if (next instanceof IExecTestCasePO) {
                for (ICompNamesPairPO pair : ((IExecTestCasePO) next).
                        getCompNamesPairs()) {
                    if (pair.isPropagated() && !alreadyCollected.
                            contains(pair.getSecondName())) {
                        addOne(pair.getSecondName());
                        addOne(pair.getSecondName());
                        alreadyCollected.add(pair.getSecondName());
                    }
                }
            }
        }
    }
    
    /**
     * Adds one usage
     * @param guid the guid of the Component Name
     */
    private void addOne(String guid) {
        if (guid != null)  {
            m_usageByGuid.add(m_man.resolveGuid(guid), 1);
        }
    }
}
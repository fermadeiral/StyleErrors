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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;


/**
 * This business process performs the override and propagate operations on
 * component names on the test execution node level.
 * 
 * @author BREDEX GmbH
 * @created 08.09.2005
 */
public class CompNamesBP {

    /**
     * Adds all propagated component name pairs of the test execution node to
     * the map <code>pairs</code>.
     * 
     * @param pairs
     *            The map
     * @param execNode
     *            The test execution node
     */
    private void addPropagatedPairs(Map<String, ICompNamesPairPO> pairs,
            IExecTestCasePO execNode) {
        for (ICompNamesPairPO pair : execNode.getCompNamesPairs()) {
            if (pair.isPropagated()) {
                String name = pair.getSecondName();
                if (!pairs.containsKey(name)) {
                    pairs.put(name, PoMaker.createCompNamesPairPO(name,
                            StringConstants.EMPTY));
                }
            }
        }
    }

    /**
     * Adds the component name of the passed test step to the map if the
     * component is not mapped by default.
     * 
     * @param pairs
     *            The map
     * @param capNode
     *            The test step
     */
    private void addCapComponentName(Map<String, ICompNamesPairPO> pairs,
        ICapPO capNode) {
        
        final Component component = capNode.getMetaComponentType();
        if (component instanceof ConcreteComponent
            && ((ConcreteComponent)component).hasDefaultMapping()) {
            return;
        }
        final String name = capNode.getComponentName();
        final String type = capNode.getComponentType();
        if (!pairs.containsKey(name)) {
            ICompNamesPairPO pair = PoMaker.createCompNamesPairPO(name, type);
            pairs.put(name, pair);
        }
    }

    /**
     * Gets all component name pairs of the passed test execution node. The list
     * contains all pairs of the passed node itself, and the propagated pairs of
     * the child test execution nodes, and the component names of the child test
     * steps.
     * 
     * @param execNode
     *            The test execution node
     * @return The list with all component name pairs that (directly or
     *         indirectly) belong to the passed node. The list is ordered by the
     *         first names in ascending order.
     */
    public List<ICompNamesPairPO> getAllCompNamesPairs(
            IExecTestCasePO execNode) {
        
        Map<String, ICompNamesPairPO> pairs = 
            new HashMap<String, ICompNamesPairPO>();

        for (ICompNamesPairPO pair : execNode.getCompNamesPairs()) {
            pairs.put(pair.getFirstName(), pair);
        }

        ISpecTestCasePO specNode = execNode.getSpecTestCase();

        if (specNode != null) {
            for (Iterator<INodePO> it = specNode.getAllNodeIter(); it
                    .hasNext();) {
                INodePO child = it.next();
                if (child instanceof IExecTestCasePO) {
                    addPropagatedPairs(pairs, (IExecTestCasePO) child);
                } else if (child instanceof ICapPO) {
                    addCapComponentName(pairs, (ICapPO) child);
                }
            }
        }

        List<ICompNamesPairPO> pairList =
            new ArrayList<ICompNamesPairPO>(pairs.values());
        // Sort the list by default
        Collections.sort(pairList, new Comparator<ICompNamesPairPO>() {
            public int compare(ICompNamesPairPO o1, ICompNamesPairPO o2) {
                return o1.getFirstName().compareTo(o2.getFirstName());
            }
        });
        
        return pairList;
    }

    /**
     * Updates the passed component name pair by setting the second name
     * property (that means, the overriding component name.
     * 
     * @param execNode The test execution node
     * @param pair The component name pair
     * @param secondCompName The second component name
     * @param cache cache for componentNames.    
     */
    public void updateCompNamesPairNew(IExecTestCasePO execNode,
        ICompNamesPairPO pair, String secondCompName, 
        IWritableComponentNameCache cache) {
        
        String secondName = cache.getGuidForName(secondCompName);
        if (StringUtils.equals(secondName, pair.getSecondName())) {
            return;
        }

        if (secondName == null) {
            final IComponentNamePO newComponentNamePO = 
                cache.createComponentNamePO(secondCompName, pair.getType(), 
                        CompNameCreationContext.OVERRIDDEN_NAME);
            newComponentNamePO.setParentProjectId(
                    execNode.getParentProjectId());
            secondName = newComponentNamePO.getGuid();
        }
        cache.changeReuse(pair, pair.getSecondName(), secondName);
        if (execNode.getCompNamesPair(pair.getFirstName()) == null) {
            execNode.addCompNamesPair(pair);
        }
    }

    /**
     * Finds the component name of the passed test step. The method searches for
     * the name in the passed tree path, which is exepected to be a top-down
     * path to the <code>capNode</code>. The <code>capNode</code> itself
     * may or may not be included as the last element of the list. Usually, the
     * tree path is determined by calling
     * {@link org.eclipse.jubula.client.core.utils.ITreeTraverserContext#getCurrentTreePath()}.
     * The result contains the component name and the node that is responsible
     * for defining the component name. This is the test step itself or the
     * execution node with the latest overriding. If a name is propagated
     * (overriden or not), the parent node is always responsible.
     * 
     * @param treePath
     *            The tree path
     * @param compNameDefiner
     *            The node that is using the component name
     * @param compNameCache
     *            The cache to use in order to resolve Component Name 
     *            references.
     * @param compNameGuid
     *            The GUID of the component name.
     * @return The result containing the component name and the responsible node
     */
    public CompNameResult findCompName(List<INodePO> treePath, 
            INodePO compNameDefiner, String compNameGuid, 
            IComponentNameCache compNameCache) {
        String currentName = compNameGuid;
        IComponentNamePO currentNamePo = 
            compNameCache.getResCompNamePOByGuid(currentName);
        if (currentNamePo != null) {
            currentName = currentNamePo.getGuid();
        }
        return findCompName(treePath, compNameCache, currentName,
                compNameDefiner);
    }

    /**
     * @param treePath
     *            The tree path
     * @param compNameCache
     *            The cache to use in order to resolve Component Name 
     *            references.
     * @param originalName
     *            The GUID of the component name.
     * @param originalCompNameDefiner
     *            The node that is using the component name
     * @return The result containing the component name and the responsible node
     */
    private CompNameResult findCompName(List<INodePO> treePath,
            IComponentNameCache compNameCache, String originalName,
            INodePO originalCompNameDefiner) {
        
        String currentName = originalName;
        INodePO compNameDefiner = originalCompNameDefiner;
        IComponentNamePO currentNamePo;
        ListIterator<INodePO> it = treePath.listIterator(treePath.size());
        while (it.hasPrevious()) {
            INodePO node = it.previous();
            if (node instanceof IExecTestCasePO) {
                IExecTestCasePO execNode = (IExecTestCasePO)node;
                ICompNamesPairPO pair = null;
                if (!StringUtils.isEmpty(currentName)) {
                    pair = execNode.getCompNamesPair(currentName);
                }
                if (pair != null) {
                    currentName = pair.getSecondName();
                    currentNamePo = 
                        compNameCache.getResCompNamePOByGuid(currentName);
                    if (currentNamePo != null) {
                        currentName = currentNamePo.getGuid();
                    }
                    if (pair.isPropagated()) {
                        int index = it.previousIndex();
                        if (index > -1) {
                            compNameDefiner = treePath.get(index);
                        }
                    } else {
                        compNameDefiner = execNode;
                        break;
                    }
                }
            }
        }

        return new CompNameResult(currentName, compNameDefiner);
    }

    /**
     * Removes incorrect CompNamePairs from children of the given node.
     * @param cache the Component Name Cache
     * @param node CompNamePairs for children of this node will be analyzed.
     */
    public static void removeIncorrectCompNamePairs(
            IWritableComponentNameCache cache, INodePO node) {
        if (!(node instanceof ISpecTestCasePO)) {
            return;
        }
        ISpecTestCasePO spec = (ISpecTestCasePO) node;
        CalcTypes.recalculateCompNamePairs(cache, spec);
        for (Iterator<INodePO> it = spec.getAllNodeIter(); it.hasNext(); ) {
            INodePO next = it.next();
            if (next instanceof IExecTestCasePO) {
                IExecTestCasePO exec = (IExecTestCasePO) next;
                // we need to iterate over a copy of the collection
                // because we are removing elements during iteration
                for (ICompNamesPairPO pair : new LinkedList<ICompNamesPairPO>(
                        exec.getCompNamesPairs())) {
                    if (!isValidCompNamePair(pair)) {
                        exec.removeCompNamesPair(pair.getFirstName());
                    }
                }
            }
        }
    }
    
    /**
     * @param pair the component name pair to check
     * @return true if the component name pair is valid
     */
    public static boolean isValidCompNamePair(ICompNamesPairPO pair) {
        return pair.getType().length() != 0;
    }

}

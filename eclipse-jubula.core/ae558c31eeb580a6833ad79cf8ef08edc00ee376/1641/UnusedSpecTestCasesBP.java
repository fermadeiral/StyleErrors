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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.NodePM;

/**
 * @author BREDEX GmbH
 * @created 05.10.2016
 */
public final class UnusedSpecTestCasesBP {

    /**
     * Constructor
     */
    private UnusedSpecTestCasesBP() {

    }

    /**
     * Uses a partial tree created by getSpecTestCaseMap to determine the
     * ISpecTestCasePO that are only used in this partial tree.
     * 
     * @param root
     *            the root ISpecTestCasePO that is observed
     * @return a List<ISpecTestCasePO> that are only use in this partial tree.
     */
    public static List<INodePO> getUnusedSpecTestCases(ISpecTestCasePO root) {
        Map<ISpecTestCasePO, ISpecTestCasePO[]> map = getSpecTestCaseMap(root);
        return getUnusedSpecTestCases(map);
    }

    /**
     * Uses a partial tree created by getSpecTestCaseMap to determine the
     * ISpecTestCasePO that are only used in this partial tree.
     * 
     * @param map
     *            the map for the key-value pairs
     * @return a List<ISpecTestCasePO> that are only use in this partial tree.
     */
    private static List<INodePO> getUnusedSpecTestCases(
            Map<ISpecTestCasePO, ISpecTestCasePO[]> map) {
        List<INodePO> deletable = new ArrayList<>();
        deletable.addAll(map.keySet());
        int i = 0;
        iter: while (i < deletable.size()) {
            for (ISpecTestCasePO value : map.get(deletable.get(i))) {
                if (!deletable.contains(value)) {
                    deletable.remove(i);
                    /*
                     * We reset the counter here, so we start over again, until
                     * we find no more deletable entries that can be removed
                     * from the list.
                     */
                    i = 0;
                    continue iter;
                }
            }
            i++;
        }
        return deletable;
    }

    /**
     * Uses a recursive depth-first search algorithm to create a Map of the
     * partial tree of an observed ISpecTestCasePO. The keys contain all
     * observed ISpecTestCasePO - we assume these are going to be deleted. The
     * values contain an array with the ISpecTestCasePO that use the
     * corresponding ISpecTestCasePO.
     * 
     * @param root
     *            the root ISpecTestCasePO that is observed
     * @return the map for the key-value pairs
     */
    private static Map<ISpecTestCasePO, ISpecTestCasePO[]>
            getSpecTestCaseMap(ISpecTestCasePO root) {
        /*
         * Create an empty map and return getSpecTestCaseMap.
         */
        Map<ISpecTestCasePO, ISpecTestCasePO[]> map =
                new HashMap<ISpecTestCasePO, ISpecTestCasePO[]>();
        return getSpecTestCaseMap(root, map);
    }

    /**
     * Uses a recursive depth-first search algorithm to create a Map of the
     * partial tree of an observed ISpecTestCasePO. The keys contain all
     * observed ISpecTestCasePO. The values contain arrays with the
     * ISpecTestCasePO that use the corresponding ISpecTestCasePO.
     * 
     * @param root
     *            the root ISpecTestCasePO that is observed
     * @param map
     *            the map for the key-value pairs
     * @return the map for the key-value pairs
     */
    private static Map<ISpecTestCasePO, ISpecTestCasePO[]> getSpecTestCaseMap(
            ISpecTestCasePO root, Map<ISpecTestCasePO, ISpecTestCasePO[]> map) {

        /*
         * Create a key-value pair with the root ISpecTestCasePO as the key, and
         * with array containing the referencing ISpecTestCasePO as the value.
         */
        List<IExecTestCasePO> listIExecTestCasePO =
                getInternalExecTestCases(root);
        ISpecTestCasePO[] values =
                new ISpecTestCasePO[listIExecTestCasePO.size()];
        for (int i = 0; i < values.length; i++) {
            INodePO top = listIExecTestCasePO.get(i).getSpecAncestor();

            /**
             * If parent is not an instance of ISpecTestCasePO, the node cannot
             * be deleted. Simply write null in the array.
             */
            if (top instanceof ISpecTestCasePO) {
                values[i] = (ISpecTestCasePO) top;
            } else {
                values[i] = null;
            }
        }
        map.put(root, values);

        /*
         * Iterate over the IExecTestCasePO of the root and do a recursive call
         * on the corresponding ISpecTestCasePO if it is not already contained
         * in the map.
         */
        Iterator iterator = root.getAllNodeIter();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof IExecTestCasePO) {
                IExecTestCasePO execTC = (IExecTestCasePO) obj;
                ISpecTestCasePO specTC = execTC.getSpecTestCase();
                if (specTC != null && !map.containsKey(specTC)
                        && specTC.getParentProjectId()
                                .equals(root.getParentProjectId())) {
                    map.putAll(getSpecTestCaseMap(specTC, map));
                }
            }
        }

        /*
         * Return the map.
         */
        return map;
    }

    /**
     * This is just a wrapper function to keep the code readable. It used NodePM
     * to get the IExecTestCasePO that reference the observed ISpecTestCasePO.
     * 
     * @param iSpecTestCasePO
     *            the ISpecTestCasePO that is observed
     * @return the List<IExecTestCasePO> that reference the iSpecTestCasePO
     */
    private static List<IExecTestCasePO>
            getInternalExecTestCases(ISpecTestCasePO iSpecTestCasePO) {
        return NodePM.getInternalExecTestCases(iSpecTestCasePO.getGuid(),
                iSpecTestCasePO.getParentProjectId());
    }

}

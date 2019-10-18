/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;

/**
 * Cleans up the object mapping by removing those unmapped CNs
 *    which are present several times
 *
 * @author BREDEX GmbH
 *
 */
public class CleanupObjectMapping {

    /** Private */
    private CleanupObjectMapping() {
    }

    /**
     * Does the cleanup
     * @param objMap the ObjectMappingPO
     * @return whether there was any change 
     */
    public static boolean cleanupObjectMapping(IObjectMappingPO objMap) {
        // The set of CN guids existing here
        Set<String> existing = new HashSet<>();
        recCollectMappedCNs(existing, objMap.getMappedCategory());
        return recRemoveDoubledCNs(existing,
                objMap.getUnmappedLogicalCategory());
    }

    /**
     * Recursively collects all mapped guids
     * @param mapped the set of mapped guids
     * @param cat the current OMCategory
     */
    private static void recCollectMappedCNs(Set<String> mapped,
            IObjectMappingCategoryPO cat) {
        for (IObjectMappingAssoziationPO assoc
            : cat.getUnmodifiableAssociationList()) {
            for (String guid : assoc.getLogicalNames()) {
                mapped.add(guid);
            }
        }
        for (IObjectMappingCategoryPO childCat
                : cat.getUnmodifiableCategoryList()) {
            recCollectMappedCNs(mapped, childCat);
        }
    }

    /**
     * Removes all doubled unmapped CNs
     * @param existing the set of already existing CNs
     * @param cat the current category
     * @return whether there was any change
     */
    private static boolean recRemoveDoubledCNs(Set<String> existing,
            IObjectMappingCategoryPO cat) {
        boolean wasChange = false;
        for (IObjectMappingCategoryPO child
            : cat.getUnmodifiableCategoryList()) {
            wasChange |= recRemoveDoubledCNs(existing, child);
        }
        List<IObjectMappingAssoziationPO> toRemove = new ArrayList<>();
        for (IObjectMappingAssoziationPO assoc
                : cat.getUnmodifiableAssociationList()) {
            for (String guid : assoc.getLogicalNames()) {
                if (existing.contains(guid)) {
                    toRemove.add(assoc);
                } else {
                    existing.add(guid);
                }
            }
        }
        for (IObjectMappingAssoziationPO assoc : toRemove) {
            cat.removeAssociation(assoc);
        }
        return wasChange || !toRemove.isEmpty();
    }

}
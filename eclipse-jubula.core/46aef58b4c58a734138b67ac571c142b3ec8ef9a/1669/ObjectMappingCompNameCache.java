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
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;

/**
 * The cache for an Object Mapping Editor
 * @author BREDEX GmbH
 *
 */
public class ObjectMappingCompNameCache extends BasicCompNameCache {

    /**
     * Constructor
     * @param context the context
     */
    public ObjectMappingCompNameCache(IPersistentObject context) {
        super(context);
    }

    /** {@inheritDoc} */
    public void handleExistingNames(Map<String, String> guidToCompNameMap) {
        for (IObjectMappingAssoziationPO assoc 
                : ((IAUTMainPO) getContext()).getObjMap().getMappings()) {
                
            Set<String> guidIntersection = 
                new HashSet<String>(assoc.getLogicalNames());
            guidIntersection.retainAll(guidToCompNameMap.keySet());
            for (String guid : guidIntersection) {
                assoc.removeLogicalName(guid);
                assoc.addLogicalName(guidToCompNameMap.get(guid));
            }
        }
    }
}
/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.api.converter.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.tools.internal.exception.JBException;

/**
 * @created 03.11.2014
 */
public class ProjectCache {
    
    /** containing cached projects */
    private static Map<Long, IProjectPO> map = new HashMap<Long, IProjectPO>();
    
    /** constructor */
    private ProjectCache() {
        // empty
    }
    
    /**
     * Returns a project for a given id.
     * If the project was not used before, it will be loaded and cached.
     * @param id project id
     * @return the project
     * @throws JBException if project could not be added to cache
     */
    public static IProjectPO get(Long id) throws JBException {
        if (!map.containsKey(id)) {
            map.put(id, ProjectPM.loadProjectById(id));
        }
        return map.get(id);
    }
}

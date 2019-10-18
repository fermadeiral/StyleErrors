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
package org.eclipse.jubula.client.core.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;


/**
 * Manages and persists Object Mappings for AUTs within the context 
 * of a single Test Case.
 *
 * @author BREDEX GmbH
 * @created Jun 16, 2010
 */
public class ObjectMappingManager {

    /** mapping from AUTs to Object Maps */
    private Map<IAUTMainPO, Map<IComponentIdentifier, String>> 
        m_objectMappings = 
        new HashMap<IAUTMainPO, Map<IComponentIdentifier, String>>();

    /**
     * Persists all mappings that have been added to this manager.
     * 
     * @throws PMException 
     *              If a database error occurs.
     * @throws ProjectDeletedException 
     *              If the project was deleted in another transaction.
     */
    public void saveMappings() throws PMException, ProjectDeletedException {
        try {
            for (IAUTMainPO aut : m_objectMappings.keySet()) {
                if (aut != null) {
                    EditSupport editSupport = new EditSupport(aut, null);
                    editSupport.reinitializeEditSupport();
                    editSupport.lockWorkVersion();
                    IAUTMainPO workVersion = 
                        (IAUTMainPO) editSupport.getWorkVersion();
                    IObjectMappingPO objMap = workVersion.getObjMap();

                    // add mappings
                    Map<IComponentIdentifier, String> autObjectMapping =
                        m_objectMappings.get(aut);
                    if (autObjectMapping != null) {
                        for (IComponentIdentifier ci 
                                : autObjectMapping.keySet()) {
                            objMap.addObjectMappingAssoziation(
                                    autObjectMapping.get(ci), ci);
                        }
                    }
                    
                    editSupport.saveWorkVersion();
                    
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                            objMap, 
                            DataState.StructureModified, UpdateState.all);
                }
            }
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForMasterSession(null, e);
        }
        
    }

    /**
     * Attempts to add the given mapping to the given AUT and returns the GUID 
     * of the Component Name for the mapping. If the Object Map
     * for the given AUT already contains a mapping for the given UI Element, 
     * then the GUID of the mapped Component Name will be returned. Otherwise,
     * the mapping is created using the provided Component Name GUID, which is 
     * then returned. 
     * 
     * @param aut The AUT to which the mapping should be added.
     * @param componentIdentifier The UI Element for the mapping.
     * @param componentNameGuid The GUID of the Component Name for the mapping.
     * @return The Component Name GUID for the mapping.
     */
    public String addMapping(IAUTMainPO aut, 
            IComponentIdentifier componentIdentifier, 
            String componentNameGuid) {

        Map<IComponentIdentifier, String> autObjectMap = 
            m_objectMappings.get(aut);
        if (autObjectMap == null) {
            autObjectMap = new HashMap<IComponentIdentifier, String>();
            m_objectMappings.put(aut, autObjectMap);
        }

        if (!autObjectMap.containsKey(componentIdentifier)) {
            autObjectMap.put(componentIdentifier, componentNameGuid);
        }

        return autObjectMap.get(componentIdentifier);
    }
    
    /**
     * Clears all mappings for the receiver.
     */
    public void clear() {
        m_objectMappings.clear();
    }
}

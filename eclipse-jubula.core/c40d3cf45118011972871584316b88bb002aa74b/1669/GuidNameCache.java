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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IAbstractGUIDNamePO;


/**
 * @author BREDEX GmbH
 * @created Apr 14, 2008
 * @param <GUID_NAME_PO> the data type of the chached names.
 */
public abstract class GuidNameCache<GUID_NAME_PO extends IAbstractGUIDNamePO> {

    /**
     * <code>m_namesToInsert</code> map with param names to insert in database
     */
    private Map<String, GUID_NAME_PO> m_namesToInsert = 
        new HashMap<String, GUID_NAME_PO>();
    
    /**
     * map with param names to update in database
     * key: GUID of parameter
     * value: new name of parameter
     */
    private Map<String, String> m_namesToUpdate = new HashMap<String, String>();
    
    /** 
     * cache for names of root node in an editor
     * key: guid of paramNamePO
     * value: parameter name
     */
    private Map<String, String> m_namesToCache = new HashMap<String, String>();
    
    /**
     * <code>m_namesToDelete</code> set with guids of names to delete from database
     */
    private Set<String> m_namesToDelete = new HashSet<String>();
    
    /**
     * 
     */
    protected final void updateLocalCache() {
        for (String guid : m_namesToInsert.keySet()) {
            m_namesToCache.put(guid, m_namesToInsert.get(guid).getName());
        }
        for (String guid : m_namesToDelete) {
            m_namesToCache.remove(guid);
        }
        for (String guid : m_namesToUpdate.keySet()) {
            if (m_namesToCache.containsKey(guid)) {
                m_namesToCache.put(guid, m_namesToUpdate.get(guid));
            }
        }
    }
    
    /**
     * Adds a new GUID_NAME_PO to persist in the database.
     * @param guidNamePO the GUID_NAME_PO to persist.
     */
    protected final void addNameToInsert(GUID_NAME_PO guidNamePO) {
        m_namesToInsert.put(guidNamePO.getGuid(), guidNamePO);
    }
    
    /**
     * Removes a GUID_NAME_PO
     * @param guidNamePO the GUID_NAME_PO to remove.
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for key.
     */
    protected final GUID_NAME_PO removeNameToInsert(GUID_NAME_PO guidNamePO) {
        return m_namesToInsert.remove(guidNamePO.getGuid());
    }
    
    /**
     * Removes a GUID_NAME_PO
     * @param guid the GUID of the GUID_NAME_PO to remove.
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for key.
     */
    protected final GUID_NAME_PO removeNameToInsert(String guid) {
        return m_namesToInsert.remove(guid);
    }
    
    /**
     * 
     * @param guid The GUID.
     * @param name The name.
     */
    protected final void addNameToCache(String guid, String name) {
        m_namesToCache.put(guid, name);
    }
    
    
    /**
     * 
     * @param guid the GUID of the name which is to delete.
     */
    protected final void addNameToDelete(String guid) {
        m_namesToDelete.add(guid);
    }
    
    /**
     * @param guid of the name to update
     * @param newName the new name for the given GUID.
     */
    public final void addNameToUpdate(String guid, String newName) {
        if (m_namesToInsert.containsKey(guid)) {
            IAbstractGUIDNamePO name = m_namesToInsert.get(guid);
            name.setName(newName);
        } else {
            m_namesToUpdate.put(guid, newName);
        }
    }
    
    /**
     * 
     * @param guid A GUID of the name which is to get.
     * @return The name of the given GUID of the given Project-ID or null
     * if no name was found.
     */
    protected final String getName(String guid) {
        String name = null;
        // look first in list of modified param names
        if (m_namesToUpdate.get(guid) != null) {
            name = m_namesToUpdate.get(guid);
        // look in local cache for parameters of root node
        // this statement must follow up the statement above, because updated names
        // are contained in two containers, even m_namesToUpdate and m_namesToCache
        // m_namesToUpdate is up to date, m_namesToCache caches the state to begin of editorSession 
        // respectively after save of session
        } else if (m_namesToCache.get(guid) != null) {
            name = m_namesToCache.get(guid);
            // look in new created param names
        } else if (m_namesToInsert.get(guid) != null) {
            name = m_namesToInsert.get(guid).getName();
            // look in map of already persistent param names
        }
        return name;
    }
    
    /**
     * @return all names which are to insert in the database.
     */
    protected final List<GUID_NAME_PO> getNamesToInsert() {
        return new ArrayList<GUID_NAME_PO>(m_namesToInsert.values());
    }
    
    /**
     * 
     * @return All GUIDs of the names which are to delete in database.
     */
    protected final List<String> getNameGuidsToDelete() {
        return new ArrayList<String>(m_namesToDelete);
    }
    
    /**
     * 
     * @return All GUIDs of the names which are to update in database.
     */
    protected final List<String> getNameGuidsToUpdate() {
        return new ArrayList<String>(m_namesToUpdate.keySet());
    }
    
    /**
     * Clears the caches of names which are to edit in the database.
     */
    public void clearAllNames() {
        m_namesToDelete.clear();
        m_namesToInsert.clear();
        m_namesToUpdate.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    protected final void removeNamePO(String guid) {
        // name is persistent and was registered for Update
        if (m_namesToUpdate.get(guid) != null) {
            m_namesToUpdate.remove(guid);
            m_namesToDelete.add(guid);
        // name is transient and was registered for first Insert operation in db
        } else if (m_namesToInsert.get(guid) != null) {
            m_namesToInsert.remove(guid);
        } else {
            m_namesToDelete.add(guid);
        }
    }
    
    /**
     * @param guid a GUID
     * @return the name to update with the given GUID.
     */
    protected final String getNameToUpdate(String guid) {
        return m_namesToUpdate.get(guid);
    }
}

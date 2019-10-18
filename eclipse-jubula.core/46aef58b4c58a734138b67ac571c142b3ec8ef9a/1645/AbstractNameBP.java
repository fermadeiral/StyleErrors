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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.model.IAbstractGUIDNamePO;


/**
 * @author BREDEX GmbH
 * @created Apr 17, 2008
 * @param <NAME_PO> the type of the namePO.
 */
public abstract class AbstractNameBP<NAME_PO extends IAbstractGUIDNamePO> {

    /**
     * <code>names</code> map with paramName objects
     * key: unique id of parameter
     * value: associated paramNameObject
     */
    private Map<String, NAME_PO> m_names = 
        new HashMap<String, NAME_PO>();
    
    
    
    /**
     * remove all entries from map
     */
    protected final void clearAllNamePOs() {
        m_names.clear();
    }
    
    
    /**
     * @return all NamePO objects, managed in map
     */
    protected final Collection<NAME_PO> getAllNamePOs() {
        return m_names.values();
    }
    
    /**
     * @param uniqueId uniqueId of name object to get
     * @return ParamNamePO object to given name
     */
    protected final NAME_PO getNamePO(String uniqueId) {
        return m_names.get(uniqueId);
    }
    
    
    /**
     * Adds the given NAME_PO to the chache.<br>
     * This method is null-safe!
     * @param namePO namePO object
     */
    protected final void addNamePO(NAME_PO namePO) {
        if (namePO != null) {
            m_names.put(namePO.getGuid(), namePO);            
        }
    }
    
    /**
     * @param guid guid of param name to remove
     */
    protected final void removeNamePO(String guid) {
        m_names.remove(guid);
    }
    
    /**
     * @param uniqueId unique id of parameter
     * @param rootProjId of project the parameter belongs to
     * @return name of parameter
     */
    protected abstract String getName(String uniqueId, Long rootProjId);
    
 
    /**
     * Gets the GUID of the given Component Name or null if no GUID was found. 
     * @param name a Component Name.
     * @return The GUID of the given Component Name or null if no GUID was found. 
     */
    public final String getGuidForName(String name) {
        for (NAME_PO namePO : m_names.values()) {
            final String currName = namePO.getName();
            if ((currName != null) && currName.equals(name)) {
                return namePO.getGuid();
            }
        }
        return null;
    }
}

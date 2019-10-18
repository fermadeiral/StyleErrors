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
package org.eclipse.jubula.client.core.datastructure;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.INodePO;


/**
 * In-memory mapping between used Component Names and their users.
 *
 * @author BREDEX GmbH
 * @created Mar 30, 2009
 */
public class CompNameUsageMap {

    /** mapping from "old name"s to their users */
    private Map<IComponentNamePO, Set<INodePO>> m_firstNameToUsers = 
        new HashMap<IComponentNamePO, Set<INodePO>>(); 

    /** mapping from "new name"s to their users */
    private Map<IComponentNamePO, Set<INodePO>> m_secondNameToUsers = 
        new HashMap<IComponentNamePO, Set<INodePO>>();

    /**
     * 
     * @param componentName The Component Name that is used.
     * @param user The node that uses <code>componentName</code>.
     */
    public void addFirstNameUser(
            IComponentNamePO componentName, INodePO user) {
        add(m_firstNameToUsers, componentName, user);
    }

    /**
     * 
     * @param componentName The Component Name that is used.
     * @param user The node that uses <code>componentName</code>.
     */
    public void addSecondNameUser(
            IComponentNamePO componentName, INodePO user) {
        add(m_secondNameToUsers, componentName, user);
    }

    /**
     * 
     * @return all Component Names used as a first name.
     */
    public Set<IComponentNamePO> getFirstCompNames() {
        return m_firstNameToUsers.keySet();
    }
    
    /**
     * 
     * @return all Component Names used as a second name.
     */
    public Set<IComponentNamePO> getSecondCompNames() {
        return m_secondNameToUsers.keySet();
    }

    /**
     * 
     * @param componentName A Component Name.
     * @return all nodes that use the given Component Name as an "old" name. 
     *         Will not return <code>null</code>.
     */
    public Set<INodePO> getFirstNameUsers(IComponentNamePO componentName) {
        return get(m_firstNameToUsers, componentName);
    }
    
    /**
     * 
     * @param componentName A Component Name.
     * @return all nodes that use the given Component Name as an "new" name. 
     *         Will not return <code>null</code>.
     */
    public Set<INodePO> getSecondNameUsers(IComponentNamePO componentName) {
        return get(m_secondNameToUsers, componentName);
    }

    /**
     * Adds all entries in the given usage map to this usage map.
     * 
     * @param usageMap The usage map from which to add all entries.
     */
    public void addAll(CompNameUsageMap usageMap) {
        for (IComponentNamePO compName : usageMap.getFirstCompNames()) {
            for (INodePO node : usageMap.getFirstNameUsers(compName)) {
                addFirstNameUser(compName, node);
            }
        }
        for (IComponentNamePO compName : usageMap.getSecondCompNames()) {
            for (INodePO node : usageMap.getSecondNameUsers(compName)) {
                addSecondNameUser(compName, node);
            }
        }
    }

    /**
     * 
     * @param nameToUsers The map to modify.
     * @param componentName The Component Name for which to add an instance of
     *                      use.
     * @param user The node using the given Component Name.
     */
    private void add(
            Map<IComponentNamePO, Set<INodePO>> nameToUsers, 
            IComponentNamePO componentName, INodePO user) {
        
        Validate.notNull(nameToUsers);

        Set<INodePO> users = nameToUsers.get(componentName);
        if (users == null) {
            users = new HashSet<INodePO>();
            nameToUsers.put(componentName, users);
        }
        users.add(user);
    }

    /**
     * 
     * @param nameToUsers The map to use.
     * @param componentName A Component Name.
     * @return null-safe value for the given Component Name key 
     *         <code>componentName</code> in <code>nameToUsers</code>.
     */
    private Set<INodePO> get(
            Map<IComponentNamePO, Set<INodePO>> nameToUsers, 
            IComponentNamePO componentName) {
        
        Validate.notNull(nameToUsers);

        Set<INodePO> toReturn = nameToUsers.get(componentName);
        if (toReturn == null) {
            toReturn = Collections.emptySet();
        }
        return toReturn;
    }
}

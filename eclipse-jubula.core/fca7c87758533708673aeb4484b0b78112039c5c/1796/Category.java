/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.checks;

import java.util.HashSet;
import java.util.Set;

/**
 * @author marcell
 * @created Oct 19, 2010
 */
public class Category {

    /** Name of the category */
    private String m_name;
    
    /** description of the category */
    private String m_description;

    /** List of checks */
    private Set<BaseCheck> m_checks = new HashSet<BaseCheck>();

    /**
     * 
     * @param name
     *            The name of the category
     */
    public Category(String name) {
        this.m_name = name;
    }

    /**
     * 
     * @return The name of the check
     */
    public String getName() {
        return m_name;
    }

    /**
     * 
     * @param check
     *            The check that will be added.
     */
    public void addCheck(BaseCheck check) {
        this.m_checks.add(check);
    }
    
    /**
     * 
     * @return all checks of this category
     */
    public Set<BaseCheck> getChecks() {
        return m_checks;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return m_name.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof Category) {
            Category other = (Category)obj;
            return this.m_name.equals(other.getName());
        } 
        return false;
    }
    
    /**
     * @return The description of the category
     */
    public String getDescription() {
        return m_description;
    }
    
    /**
     * @param description New description of the category
     */
    public void setDescription(String description) {
        this.m_description = description;
    }

}

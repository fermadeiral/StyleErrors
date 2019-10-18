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
package org.eclipse.jubula.tools.internal.xml.businessmodell;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the technical component class and it's properties
 * 
 * @author BREDEX GmbH
 * @created Nov 12, 2009
 * 
 */
public class ComponentClass {
    /** the component class name */
    private String m_name;

    /** the optional properties for each component class */
    private List m_properties = new ArrayList();

    /** default constructor used by xstream */
    public ComponentClass() {
        // default
    }
    
    /** @param componentClass the component class name */
    public ComponentClass(String componentClass) {
        setName(componentClass);
    }

    /** @param name the name to set */
    public void setName(String name) {
        m_name = name;
    }

    /** @return the name */
    public String getName() {
        return m_name;
    }

    /** @param properties the properties to set */
    public void setProperties(List properties) {
        m_properties = properties;
    }

    /** @return the properties */
    public List getProperties() {
        return m_properties;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
        result = prime * result
                + ((m_properties == null) ? 0 : m_properties.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {            
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ComponentClass other = (ComponentClass) obj;
        if (m_name == null) {
            if (other.m_name != null) {
                return false;
            }
        } else if (!m_name.equals(other.m_name)) {
            return false;
        }
        if (m_properties == null) {
            if (other.m_properties != null) {
                return false;
            }
        } else if (!m_properties.equals(other.m_properties)) {
            return false;
        }
        return true;
    }
}
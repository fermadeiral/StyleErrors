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
package org.eclipse.jubula.extensions.wizard.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An action's parameter
 * 
 * @author BREDEX GmbH
 */
public class Parameter {
    
    /** The parameter's name */
    private String m_name;

    /** The parameter's type */
    private String m_type;
    
    /** The parameter's value set*/
    private ValueSet m_valueSet;
    
    /**
     * The constructor
     * @param name the parameter's name
     * @param type the parameter's type
     */
    public Parameter(String name, String type) {
        m_name = name;
        m_type = type;
        m_valueSet = new ValueSet();
    }

    /**
     * @return the parameter's name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the parameter's name
     * @param name the parameter's name to be set
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * @return the parameter's type
     */
    public String getType() {
        return m_type;
    }

    /**
     * Sets the parameter's type
     * @param type the parameter's type to be set
     */
    public void setType(String type) {
        m_type = type;
    }
    
    /**
     * @return the parameter's valueset
     */
    public ValueSet getValueSet() {
        return m_valueSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_name)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof Parameter)) {
            Parameter param = (Parameter) obj;
            return new EqualsBuilder()
                .append(m_name, param.getName())                
                .isEquals();
        }
        return false;
    }
    
    
}

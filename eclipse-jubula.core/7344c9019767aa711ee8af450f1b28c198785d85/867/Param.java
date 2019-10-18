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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents the parameter which belongs to an action.
 * A parameter has a name and a type.
 *
 * @author BREDEX GmbH
 * @created 06.07.2004
 */
public class Param {
    /** I18NKey of the parameter */
    private String m_name = null;
    
    /** Type of the parameter */
    private String m_type = null;
    
    /** The default value. */
    private String m_defaultValue;
    
    /** A set of predefined values. */
    private ParamValueSet m_valueSet;
    
    /** a description key for this parameter */
    private String m_descriptionKey;
    
    /** whether this parameter is optional */
    private boolean m_optional = false;

    /**
     * Default constructor of param. Do nothing.
     */
    public Param() {
        super();
    }
    
    /**
     * Sets the member-variables m_name and m_type
     * @param name A <code>String</code> value.
     * @param type A <code>String</code> value.
     */
    public Param(String name, String type) {
        m_name = name;
        m_type = type;
    }
    
    /**
     * Sets the member-variables m_name and m_type
     * @param name A <code>String</code> value.
     * @param type A <code>String</code> value.
     * @param valueSet a ValueSet
     */
    public Param(String name, String type, Map<String, String> valueSet) {
        m_name = name;
        m_type = type;
        m_valueSet = new ParamValueSet(valueSet);
    }
    /**
     * @return The set of predefined values, may be of length <code>0</code>
     */
    public ParamValueSet getValueSet() {
        if (m_valueSet == null) {
            m_valueSet = new ParamValueSet();
        }
        return m_valueSet;
    }
    /**
     * @return Returns the name. A <code>String</code> value.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * @param name A <code>String</code> value. The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * @return Returns the type. A <code>String</code> value.
     */
    public String getType() {
        return m_type;
    }
    
    /**
     * @param type A <code>String</code> value. The type to set.
     */
    public void setType(String type) {
        m_type = type;
    }
    /**
     * @return The default value or <code>null</code>, if there is no default
     * value specified for the parameter
     */
    public String getDefaultValue() {
        return m_defaultValue;
    }
    /**
     * @return The iterator of the value set.<br>
     * The elements of the Iterator are of the type <code>ValueSetElement</code>
     */
    public Iterator<ValueSetElement> valueSetIterator() {
        return getValueSet().iterator();
    }
    /**
     * @return <code>true</code> if the value set is not empty,
     *         <code>false</code> otherwise
     */
    public boolean hasValueSet() {
        return !getValueSet().isEmpty();
    }
    /**
     * Finds a value set element by the value of a set element.
     * 
     * @param value The value
     * @return The value set element or <code>null</code>, if the value
     *         doesn't exist in the set
     */
    public ValueSetElement findValueSetElementByValue(String value) {
        ValueSetElement vs = null;
        for (Iterator it = valueSetIterator(); it.hasNext();) {
            ValueSetElement element = (ValueSetElement)it.next();
            if (element.getValue().equals(value)) {
                vs = element;
                break;
            }
        }
        return vs;
    }
    /**
     * Returns a string representation of the param object.
     * @return String
     */
    public String toString() {
        return new ToStringBuilder(this)
             .append("Name", m_name) //$NON-NLS-1$
             .append("Type", m_type) //$NON-NLS-1$
                 .toString();
    }
    
    /**
     * Compares this <code>Param</code> to the specified object.
     * @param object Object
     * @return <code>true</code> if both params are equal.
     */
    public boolean equals(Object object) {
        if (object instanceof Param) {
            Param theOther = (Param)object;
            return new EqualsBuilder().append(m_name, theOther.m_name)
                .append(m_type, theOther.m_type)
                     .isEquals();
        }
        return false;
    }
    
    /**
     * @return the hashCode
     */
    public int hashCode() {
        return new HashCodeBuilder().append(m_name).append(m_type)
            .toHashCode();
    }

    /**
     * @return the descriptionKey
     */
    public String getDescriptionKey() {
        return m_descriptionKey;
    }

    /**
     * @param descriptionKey the descriptionKey to set
     */
    public void setDescriptionKey(String descriptionKey) {
        m_descriptionKey = descriptionKey;
    }

    /**
     * @return the optional
     */
    public boolean isOptional() {
        return m_optional;
    }

    /**
     * @param optional the optional to set
     */
    public void setOptional(boolean optional) {
        m_optional = optional;
    }
}

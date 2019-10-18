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
package org.eclipse.jubula.rc.common.components;

import org.apache.commons.lang.Validate;


/**
 * @author BREDEX GmbH
 * @created 11.05.2006
 * 
 * @param <COMPONENT_TYPE>
 *            the type of the component
 */
public abstract class AUTComponent<COMPONENT_TYPE> {
    /** 
     * Component from the AUT. This may be null if no actual component
     * was used, i.e. the ID was generated for inheritance checking.
     */
    private COMPONENT_TYPE m_component = null;

    /** the name of the compID */
    private String m_name;
    
    /**
     * Create a wrapper instance from a UI component. This constructor is used
     * when working with real instances instead of mere class descriptions.
     * 
     * @param component
     *            the real UI toolkit component
     */
    public AUTComponent(COMPONENT_TYPE component) {
        Validate.notNull(component, "The component must not be null"); //$NON-NLS-1$
        setComponent(component);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof AUTComponent)) {
            return false;
        }
        if (obj == this) {
            return true; // a case of identity
        }
        AUTComponent o = (AUTComponent)obj;
        return getComponent().equals(o.getComponent());
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return getComponent().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("ComponentID: "); //$NON-NLS-1$
        Class<? extends Object> componentClass = getComponent().getClass();
        sb.append(componentClass.getName());
        sb.append(", CL: "); //$NON-NLS-1$
        sb.append(componentClass.getClassLoader());
        return sb.toString();
    }

    /**
     * @return the compID name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param name the compID name to set
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * @return the component
     */
    public COMPONENT_TYPE getComponent() {
        return m_component;
    }

    /**
     * @param component the component to set
     */
    protected void setComponent(COMPONENT_TYPE component) {
        m_component = component;
    }
}
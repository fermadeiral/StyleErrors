/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.toolkit.ui.utils;


/** @author BREDEX GmbH */
public class ComponentActionPair implements Comparable<ComponentActionPair> {
    
    /** component */
    private String m_component;

    /** action */
    private String m_action;
    
    /** 
     * @param component the component
     * @param action the action
     */
    public ComponentActionPair(String component, String action) {
        m_component = component;
        m_action = action;
    }

    /**
     * @return the component
     */
    public String getComponent() {
        return m_component;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return m_action;
    }
    
    @Override
    public int compareTo(ComponentActionPair o) {
        int componentComparison = m_component.toLowerCase().compareTo(
                o.getComponent().toLowerCase());
        return componentComparison != 0 
                ? componentComparison
                : m_action.toLowerCase().compareTo(
                        o.getAction().toLowerCase());
    }
    
    @Override
    public String toString() {
        return m_component + " - " + m_action; //$NON-NLS-1$
    }
}

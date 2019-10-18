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
package org.eclipse.jubula.client.core.functions;

import org.apache.commons.lang.Validate;

/**
 * Definition of a Function parameter, as read from an extension.
 */
public class ParameterDefinition {

    /** the name for this parameter, for use in tooling */
    private String m_name;
    
    /** 
     * the type for the parameter. possible types include string, boolean, 
     * and number 
     */
    private String m_type;
    
    /**
     * Constructor
     * 
     * @param name The name for the parameter. May not be <code>null</code>.
     * @param type The type for the parameter. May not be <code>null</code>.
     */
    public ParameterDefinition(String name, String type) {
        Validate.notNull(name);
        Validate.notNull(type);
        
        m_name = name;
        m_type = type;
    }

    /**
     * 
     * @return the name of the receiver. Never <code>null</code>.
     */
    public String getName() {
        return m_name;
    }

    /**
     * 
     * @return the type of the receiver. Never <code>null</code>.
     */
    public String getType() {
        return m_type;
    }

    
}

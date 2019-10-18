/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.model;


/**
 * @author BREDEX GmbH
 * @created Oct 28, 2011
 */
public final class TestResultParameter {

    /** the Parameter name (internationalized, if from an Action) */
    private String m_name;
    
    /** the Parameter type (internationalized) */
    private String m_type;
    
    /** the Parameter value */
    private String m_value;

    /**
     * Constructor
     * 
     * @param name  The name of the Parameter (internationalized, if 
     *              from an Action).
     * @param type  The type of the Parameter (internationalized).
     * @param value The value for the Parameter.
     */
    public TestResultParameter(String name, String type, String value) {
        m_name = name;
        m_type = type;
        m_value = value;
    }

    /**
     * Constructor
     * 
     * @param parameterToCopy The object from which to copy the Parameter data.
     */
    public TestResultParameter(IParameterDetailsPO parameterToCopy) {
        this(parameterToCopy.getParameterName(), 
                parameterToCopy.getParameterType(), 
                parameterToCopy.getParameterValue());
    }
    
    /**
     * @return the parameter name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * @return the parameter type.
     */
    public String getType() {
        return m_type;
    }

    /**
     * @return the parameter value.
     */
    public String getValue() {
        return m_value;
    }

}

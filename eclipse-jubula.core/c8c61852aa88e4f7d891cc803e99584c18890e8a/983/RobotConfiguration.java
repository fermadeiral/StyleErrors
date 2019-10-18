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
package org.eclipse.jubula.rc.common.driver;

import org.eclipse.jubula.toolkit.enums.ValueSets;

/**
 * Configuration of the robot 
 *
 * @author BREDEX GmbH
 * @created 10.02.2006
 */
public class RobotConfiguration {
    /** instance */
    private static RobotConfiguration instance = new RobotConfiguration();
    /** window activation method */
    private String m_activationMethod =
            ValueSets.AUTActivationMethod.none.rcValue();
    /**
     * whether to highlight erros that occur at a component
     */
    private Boolean m_errorHighlighting = false;
    
    /**
     * @return instance
     */
    public static RobotConfiguration getInstance() {
        return instance;
    }
    
    /**
     * @return window activation method
     */
    public String getDefaultActivationMethod() {
        return m_activationMethod;
    }
    /**
     * @return whether to highlight errors that occur when testing
     * a component
     */
    public Boolean isErrorHighlighting() {
        return m_errorHighlighting;
    }
    /**
     * @param activationMethod window activation method
     */
    public void setDefaultActivationMethod(String activationMethod) {
        m_activationMethod = activationMethod;
    }
    /**
     * @param errorHighlighting whether to highlight errors that occur when testing
     * a component
     */
    public void setErrorHighlighting(Boolean errorHighlighting) {
        m_errorHighlighting = errorHighlighting;
    }
}

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

import java.io.Serializable;

/**
 * This class represents the params which belongs to an action.
 * A param has a name and a type.
 * @author BREDEX GmbH
 * @created 25.06.2005
 */
public class Profile implements org.eclipse.jubula.tools.Profile, Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /** Name of the parameter */
    private String m_name = null;

    /** Name of the parameter */
    private double m_nameFactor = 0;

    /** Name of the parameter */
    private double m_pathFactor = 0;

    /** Name of the parameter */
    private double m_contextFactor = 0;

    /** Name of the parameter */
    private double m_threshold = 0;

    /**
     * @param name double
     * @param nameFactor double
     * @param pathFactor double
     * @param contextFactor double
     * @param threshold double
     */
    public Profile(String name, 
            double nameFactor, 
            double pathFactor, 
            double contextFactor, 
            double threshold) {
        setName(name);
        setNameFactor(nameFactor);
        setPathFactor(pathFactor);
        setContextFactor(contextFactor);
        setThreshold(threshold);
    }

    /** constructor */
    public Profile() {
        super();
    }

    /** @return Returns the name. A <code>String</code> value. */
    public String getName() {
        return m_name;
    }
    
    /** @param name A <code>String</code> value. The name to set. */
    public void setName(String name) {
        m_name = name;
    }

    /** @return Returns the contextFactor. */
    public double getContextFactor() {
        return m_contextFactor;
    }
    
    /** @param contextFactor The contextFactor to set. */
    public void setContextFactor(double contextFactor) {
        m_contextFactor = contextFactor;
    }
    
    /** @return Returns the nameFactor. */
    public double getNameFactor() {
        return m_nameFactor;
    }
    
    /** @param nameFactor The nameFactor to set. */
    public void setNameFactor(double nameFactor) {
        m_nameFactor = nameFactor;
    }
    
    /** @return Returns the pathFactor. */
    public double getPathFactor() {
        return m_pathFactor;
    }
    
    /** @param pathFactor The pathFactor to set. */
    public void setPathFactor(double pathFactor) {
        m_pathFactor = pathFactor;
    }
    
    /** @return Returns the threshold. */
    public double getThreshold() {
        return m_threshold;
    }
    
    /** @param threshold The threshold to set. */
    public void setThreshold(double threshold) {
        m_threshold = threshold;
    }
    
    /**
     * checks if a profile set is valid
     * @return boolean
     */
    public boolean isValid() {
        boolean valid = true;
        if (m_nameFactor < 0 
                || m_nameFactor > 1
                || m_pathFactor < 0
                || m_pathFactor > 1
                || m_contextFactor < 0
                || m_contextFactor > 1
                || m_threshold < 0
                || m_threshold > 1
                || m_name == null
                || m_name.length() == 0) {
            valid = false;
        }
        if (Math.abs(m_nameFactor
                + m_pathFactor
                + m_contextFactor - 1.0) > 1e-4) {
            valid = false;
        }
        return valid;
    }
    
    /** @return String */
    public String toString() {
        String returnVal;
        returnVal = this.getClass().getName() + "("  //$NON-NLS-1$
            + getName() + "," //$NON-NLS-1$
            + getNameFactor() + "," //$NON-NLS-1$
            + getPathFactor() + "," //$NON-NLS-1$
            + getContextFactor() + "," //$NON-NLS-1$
            + getThreshold() + ")"; //$NON-NLS-1$
        return returnVal;
    }
}
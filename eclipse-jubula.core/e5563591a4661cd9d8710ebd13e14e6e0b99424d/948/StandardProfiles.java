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
package org.eclipse.jubula.tools.internal.objects;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.tools.internal.constants.StandardProfileNames;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;

/**
 * 
 * @author BREDEX GmbH
 *
 */
public enum StandardProfiles {
    
    /** Standard Profile */
    STANDARD(StandardProfileNames.STANDARD, 0.60, 0.30, 0.10, 0.85), 
    /** Strict Profile */
    STRICT(StandardProfileNames.STRICT, 0.60, 0.30, 0.10, 1.00), 
    /** Given Names Profile */
    GIVEN_NAMES(StandardProfileNames.GIVEN_NAMES, 1.00, 0.00, 0.00, 1.00);
    
    /** Name of the parameter */
    private String m_name;

    /** Name of the parameter */
    private double m_nameFactor;

    /** Name of the parameter */
    private double m_pathFactor;

    /** Name of the parameter */
    private double m_contextFactor;

    /** Name of the parameter */
    private double m_threshold;

    /**
     * @param name
     *            double
     * @param nameFactor
     *            double
     * @param pathFactor
     *            double
     * @param contextFactor
     *            double
     * @param threshold
     *            double
     */
    private StandardProfiles(String name, double nameFactor, double pathFactor,
            double contextFactor, double threshold) {
        m_name = name;
        m_nameFactor = nameFactor;
        m_pathFactor = pathFactor;
        m_contextFactor = contextFactor;
        m_threshold = threshold;
    }

    /**
     * create an instance of the profile
     * @return the profile
     */
    public Profile instance() {
        return new Profile(m_name, m_nameFactor, m_pathFactor, m_contextFactor,
                m_threshold);
    }
    
    /**
     * @return list of strings containing the standard profile names
     */
    public static List<String> getProfileNames() {
        List<String> names = new ArrayList<String>();
        for (StandardProfiles profile : StandardProfiles.values()) {
            names.add(profile.instance().getName());
        }
        return names;
    }
}

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
package org.eclipse.jubula.client.core.model;

import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;

/**
 * Encapsulates the parameters used in finding GUI components during tests.
 *
 * @author BREDEX GmbH
 * @created Nov 4, 2008
 */
public interface IObjectMappingProfilePO extends IPersistentObject {
    
    /** name of the "context factor" property */
    public static final String PROP_CONTEXT_FACTOR = "contextFactor"; //$NON-NLS-1$

    /** name of the "name factor" property */
    public static final String PROP_NAME_FACTOR = "nameFactor"; //$NON-NLS-1$
    
    /** name of the "path factor" property */
    public static final String PROP_PATH_FACTOR = "pathFactor"; //$NON-NLS-1$

    /** name of the "threshold" property */
    public static final String PROP_THRESHOLD = "threshold"; //$NON-NLS-1$
    
    /** 
     * the minimum value for any of the percentage-based properties of 
     * objects of this type 
     */
    public static final double MIN_PERCENTAGE_VALUE = 0.0;

    /** 
     * the maximum value for any of the percentage-based properties of 
     * objects of this type 
     */
    public static final double MAX_PERCENTAGE_VALUE = 1.0;
    
    /**
     * @return Returns the contextFactor.
     */
    public double getContextFactor();
    
    /**
     * @param contextFactor The contextFactor to set.
     */
    public void setContextFactor(double contextFactor);
    
    /**
     * @return Returns the nameFactor.
     */
    public double getNameFactor();
    
    /**
     * @param nameFactor The nameFactor to set.
     */
    public void setNameFactor(double nameFactor);
    
    /**
     * @return Returns the pathFactor.
     */
    public double getPathFactor();
    
    /**
     * @param pathFactor The pathFactor to set.
     */
    public void setPathFactor(double pathFactor);
    
    /**
     * @return Returns the threshold.
     */
    public double getThreshold();
    
    /**
     * @param threshold The threshold to set.
     */
    public void setThreshold(double threshold);

    /**
     * Sets the parameter values for this map to those contained in the given
     * profile.
     * 
     * @param template the profile from which the parameter values will be
     *                 copied.
     */
    public void useTemplate(Profile template);
    
    /**
     * checks if the Profile matches the values of a template
     * @param template One of the predefined template
     * @return true if match
     */
    public boolean matchesTemplate(Profile template);
}

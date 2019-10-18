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
package org.eclipse.jubula.client.internal.impl;

import org.eclipse.jubula.client.ProfileFactory;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;

/**
 * Implementation of the factory for Heuristic Profiles
 * @author BREDEX GmbH
 * @created 24.11.2015
 */
public enum ProfileFactoryImpl implements ProfileFactory {
    /**
     * Enum Singleton
     */
    INSTANCE;
    
    /** {@inheritDoc} */
    public org.eclipse.jubula.tools.Profile createProfile(String name,
            double nameWeight, double pathWeight, double contextWeight,
            double threshold) {
        if (threshold > 100 || threshold < 0) {
            throw new IllegalArgumentException("Threshold has to be a value between 0 and 100"); //$NON-NLS-1$
        }
        double sum = nameWeight + pathWeight + contextWeight;
        double nameFactor = nameWeight / sum;
        double pathFactor = pathWeight / sum;
        double contextFactor = contextWeight / sum;
        double thresholdPercentage = threshold / 100;
        Profile p = new Profile(name, nameFactor, pathFactor, contextFactor,
                thresholdPercentage);
        if (p.isValid()) {
            return p;
        }
        //This is Impossible
        throw new IllegalArgumentException("On or more parameter are not correct"); //$NON-NLS-1$
    }
}

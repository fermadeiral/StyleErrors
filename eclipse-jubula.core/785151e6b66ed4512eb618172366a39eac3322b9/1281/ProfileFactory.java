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
package org.eclipse.jubula.client;

import org.eclipse.jubula.tools.Profile;

/**
 * Factory to create Heuristic Profiles
 * @author BREDEX GmbH
 * @since 3.2
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ProfileFactory {
    /**
     * The component lookup in the AUT is a heuristic process. During test
     * execution, a calculation is made for each component in the AUT to see how
     * similar it is to the originally mapped component. This calculation is
     * based primarily on the component type if you mapped a combo box, only
     * combo boxes will considered. For each component of the same type, the
     * similarity to the original is calculated using weighted properties. The
     * factors used in the calculation are: <br>
     * <ul>
     * <li>The name of the object within the AUT code, as given by the developer
     * (if a name was given).</li>
     * <li>The route through the AUT hierarchy to get to this component.</li>
     * <li>The components in the vicinity of this component.</li>
     * </ul>
     * 
     * @param name
     *            the name of the profile
     * @param nameWeight
     *            weighting for the name
     * @param pathWeight
     *            weighting for the path
     * @param contextWeight
     *            weighting for the context
     * @param threshold
     *            This determines what percentage value a component in the AUT
     *            must have in order to be considered as the originally mapped
     *            component. Components with a value under the threshold are not
     *            considered. The component with the highest value above the
     *            threshold is chosen during execution. This has to be a value
     *            between 0 and 100.
     * @return the profile
     * @throws IllegalArgumentException
     *             if the given values would not create a valid profile
     */
    public Profile createProfile(String name, double nameWeight,
            double pathWeight, double contextWeight, double threshold);
}

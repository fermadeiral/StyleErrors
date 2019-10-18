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
 * Standard Heuristic Profiles
 * @author BREDEX GmbH
 * @since 3.2
 */
public enum StandardProfiles {
    /** Standard Profile */
    STANDARD(
            org.eclipse.jubula.tools.internal.objects.StandardProfiles
            .STANDARD),
    /** Strict Profile */
    STRICT(org.eclipse.jubula.tools.internal.objects.StandardProfiles.STRICT),
    /** Given Names Profile */
    GIVEN_NAMES(org.eclipse.jubula.tools.internal.objects.StandardProfiles
            .GIVEN_NAMES);

    /** Reference to internal enum profile **/
    private org.eclipse.jubula.tools.internal.objects
        .StandardProfiles m_internalProfile;

    /**
     * Private Constructor
     * @param internalProfile the internal enum profile
     */
    private StandardProfiles(org.eclipse.jubula.tools.internal.objects
            .StandardProfiles internalProfile) {
        m_internalProfile = internalProfile;
    }

    /**
     * create an instance of the profile
     * 
     * @return the profile
     */
    public Profile instance() {
        return m_internalProfile.instance();
    }
}

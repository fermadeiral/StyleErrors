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
package org.eclipse.jubula.version;

import org.osgi.framework.Version;

/**
 * @author BREDEX GmbH
 */
public class SemanticVersionUtil {
    
    /**
     * private default constructor
     */
    private SemanticVersionUtil() {
        // empty
    }
    
    /**
     * Checks whether a version <code>v1</code> is compatible to a given version <code>v2</code>,
     * i.e. whether <code>v1</code> and <code>v2</code> have the same major version number
     * and that the minor version number of <code>v1</code> is less than <code>v2</code>'s.
     * @param v1 first version
     * @param v2 second version
     * @return whether the versions are compatible
     */
    public static boolean isCompatibleWith(Version v1, Version v2) {
        if (v1.getMajor() != v2.getMajor()) {
            return false;
        }
        return v1.getMinor() <= v2.getMinor();
    }
}

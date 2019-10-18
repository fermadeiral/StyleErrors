/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.archive;

import org.osgi.framework.Version;

/** @author BREDEX GmbH */
public class JsonVersion {

    /** The currently json version */
    public static final Version CURRENTLY_JSON_VERSION = new Version(1, 0, 1);
    
    
    /**
     * The default constructor is not necessary
     */
    private JsonVersion() { }

    /**
     * @param version what we want to compare with CURRENTLY_JSON_VERSION
     * @return false if major version number of input version is smaller
     *              than major version number of CURRENTLY_JSON_VERSION
     */
    public static boolean isCompatible(Version version) {
        return version.getMajor() <= CURRENTLY_JSON_VERSION.getMajor();
    }
}

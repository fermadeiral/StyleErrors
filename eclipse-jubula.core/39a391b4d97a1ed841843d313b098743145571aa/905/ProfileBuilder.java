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
package org.eclipse.jubula.tools.internal.xml.businessprocess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.tools.internal.objects.StandardProfiles;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;

/**
 * This class contains methods for reading the configuration file and for 
 * mapping the configuration file to java objects.
 *
 * @author BREDEX GmbH
 * @created 08.07.2004
 */
public class ProfileBuilder {

    /**
     * The System of components.
     */
    private static List<Profile> profiles = null;
    
    /** 
     * Default constructor
     */
    private ProfileBuilder() {
        super();
    }
    
    /**
     * Returns a List of all profiles
     * @return List
     */
    public static List<Profile> getProfiles() {
        if (profiles == null) {
            profiles = new ArrayList<Profile>();

            for (StandardProfiles profile : StandardProfiles.values()) {
                profiles.add(profile.instance());
            }

        }
        return profiles;
    }

    /**
     * Returns a List of all profiles
     * @return String Array
     */
    public static String[] getProfileNames() {
        Iterator<Profile> iter = getProfiles().iterator();
        String[] names = new String[getProfiles().size()];
        int index = 0;
        while (iter.hasNext()) {
            names[index] = iter.next().getName();
            index++;
        }
        return names;
    }

    /**
     * @param name
     *      String
     * @return
     *      Profile
     */
    public static Profile getProfile(String name) {
        Iterator<Profile> iter = getProfiles().iterator();
        while (iter.hasNext()) {
            Profile prof = iter.next();
            if (prof.getName().equals(name)) {
                return prof;
            }
        }
        return null;
    }

    /**
     * @return the default object mapping profile.
     */
    public static Profile getDefaultProfile() {
        return StandardProfiles.STANDARD.instance();
    }
}

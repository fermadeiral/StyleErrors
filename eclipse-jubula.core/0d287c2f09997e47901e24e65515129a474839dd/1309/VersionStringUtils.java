/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.cmd.utils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.ProjectVersion;
/**
 * This is a Util class which is used to create a {@link ProjectVersion} from a version string.
 * There are different formats possible
 * [majNum]
 * [majNum].[minNum]
 * [majNum].[minNum].[micNum]
 * [majNum].[minNum].[micNum]_[qualifier]
 * [qualifier]
 * @author BREDEX GmbH
 *
 */
public class VersionStringUtils {

    /** Separator between major and minor version numbers */
    public static final char VERSION_SEPARATOR = '.';
    /** Separator between version numbers and the Qualifier */
    public static final char VERSION_QUALIFIER_SEPARATOR = '_';
    
    /**
     * Exception for using with version strings.
     * @author BREDEX GmbH
     *
     */
    public static class MalformedVersionException extends Exception {

        /**
         * @param string string to clarify the reason
         */
        public MalformedVersionException(String string) {
            super(string);
        }
        // Nothing in here yet
    }
    
    /**
     * private constructor since this is a util class
     */
    private VersionStringUtils() {
        //Utils class
    }
    /**
     * There are different formats possible<br>
     * [majNum]<br>
     * [majNum].[minNum]<br>
     * [majNum].[minNum].[micNum]<br>
     * [majNum].[minNum].[micNum]_[qualifier]<br>
     * [qualifier]<br>
     * @param version a string representation of a project version
     * @return the {@link ProjectVersion} for the corresponding string
     * @throws MalformedVersionException when the version is not of the wanted format
     */
    public static ProjectVersion createProjectVersion(String version)
        throws MalformedVersionException {
        String[] tokens =  StringUtils
                .splitByWholeSeparatorPreserveAllTokens(version, "_", 2); //$NON-NLS-1$
        if (tokens.length == 1) {
            try {
                return createVersionsNumbers(tokens[0]);
            } catch (NumberFormatException nfe) {
                // It is only given a qualifier
                return new ProjectVersion(tokens[0]);
            } catch (MalformedVersionException nfe) {
                // It is only given a qualifier
                return new ProjectVersion(tokens[0]);
            }
        } else if (tokens.length == 2) {
            ProjectVersion projectNumbers = new ProjectVersion(null);
            if (StringUtils.isNotBlank(tokens[0])) {
                try {
                    projectNumbers = createVersionsNumbers(tokens[0]);
                } catch (NumberFormatException e) {
                    throw new MalformedVersionException("parsing the version:" //$NON-NLS-1$
                    + tokens[0] + " as numbers, was not possible"); //$NON-NLS-1$ 
                }
            }
            String qualifier = tokens[1];
            return new ProjectVersion(projectNumbers.getMajorNumber(),
                    projectNumbers.getMinorNumber(),
                    projectNumbers.getMicroNumber(), qualifier);
        }
        throw new MalformedVersionException("No version Token found"); //$NON-NLS-1$
    }

    /**
     * 
     * @param versionNumbers the number part of the project version
     * @return a {@link ProjectVersion} with the version numbers set
     * @throws NumberFormatException if one of the numbers is not an {@link Integer}
     * @throws MalformedVersionException 
     */
    private static ProjectVersion createVersionsNumbers(String versionNumbers)
        throws NumberFormatException, MalformedVersionException {
        String[] splittedString = versionNumbers.split("\\."); //$NON-NLS-1$
        Integer major = null;
        Integer minor = null;
        Integer micro = null;
        switch (splittedString.length) {
            case 3:
                micro = Integer.parseInt(splittedString[2]);
            case 2:
                minor = Integer.parseInt(splittedString[1]);
            case 1:
                major = Integer.parseInt(splittedString[0]);
                break;
            default:
                throw new MalformedVersionException("Version has more than 3 numbers"); //$NON-NLS-1$
        }
        return new ProjectVersion(major, minor, micro);
    }
}

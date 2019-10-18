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
package org.eclipse.jubula.rc.common.util;

/**
 * @author BREDEX GmbH
 * @created Mar 19, 2009
 */
public class NameValidator {
    
    /**
     * invisible constructor
     */
    private NameValidator() {
        // this is an utility class
    }
    
    /**
     * @param name name candidate
     * @return a string which contains an acceptable project name. Invalid
     * characters are cut
     */
    public static String convertToValidLogicalName(String name) {
        String trimName = name.trim();
        StringBuffer res = new StringBuffer(trimName.length());
        int len = trimName.length();
        for (int i = 0; i < len; ++i) {
            char ch = trimName.charAt(i);
            if (isValidChar(ch)) {
                res.append(ch);
            }
        }
        return res.toString();
    }
    
    /**
     * Checks if a given String is a valid project name. This includes the
     * ability to be used as a file name on supported operating systems.
     * @param name Name candidate
     * @param checkSpaces test for spaces at the beginning or end of name
     * 
     * @return true if the name is considered a valid project name.
     */
    public static boolean isValidLogicalName(String name, boolean checkSpaces) {
        if (checkSpaces) {
            if (name.startsWith(" ")) { //$NON-NLS-1$
                return false; // no leading spaces
            }
            if (name.endsWith(" ")) { //$NON-NLS-1$
                return false; // no trailing spaces
            }
        }
        int len = name.length();
        for (int i = 0; i < len; ++i) {
            char ch = name.charAt(i);
            if (!isValidChar(ch)) {
                return false;                
            }

        }
        return true;
    }
    
    /**
     * @param ch character
     * @return true if ch is a valid part of a project name
     */
    private static boolean isValidChar(char ch) {
        boolean valid = Character.isLetterOrDigit(ch) || (ch == '_');
        return valid;
    }   
}

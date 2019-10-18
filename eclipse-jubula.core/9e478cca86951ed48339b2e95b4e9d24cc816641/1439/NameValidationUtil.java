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
package org.eclipse.jubula.client.core.utils;

/**
 * Utility methods for validating names and IDs.
 *
 * @author BREDEX GmbH
 * @created Feb 10, 2010
 */
public class NameValidationUtil {

    /**
     * Private constructor for utility class.
     */
    private NameValidationUtil() {
        // Nothing to initialize
    }

    /**
     * Checks whether the given string contains "invalid" characters.
     * 
     * @param toValidate The string to check.
     * @return <code>true</code> if the given string contains *no* illegal 
     *         characters. Otherwise <code>false</code>.
     */
    public static boolean containsNoIllegalChars(String toValidate) {
        int len = toValidate.length();
        for (int i = 0; i < len; ++i) {
            char ch = toValidate.charAt(i);
            if (!isValidChar(ch)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * @param ch character
     * @return <code>true</code> if <code>ch</code> is a valid character for 
     *         a name.
     */
    private static boolean isValidChar(char ch) {
        return Character.isLetterOrDigit(ch) || (ch == ' ') || (ch == '_');
    }
}

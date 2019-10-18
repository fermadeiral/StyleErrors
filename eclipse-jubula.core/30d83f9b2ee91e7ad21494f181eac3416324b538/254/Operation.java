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
package org.eclipse.jubula.client.ui.rcp.search.query;


/**
 * @author BREDEX GmbH
 * @created Apr 29, 2013
 */
/**
 * The search operation mode.
 */
public enum Operation {
    /** Match case search: indexOf */
    MATCH_CASE,
    /** Ignore case search: indexOf(ignoreCase) */
    IGNORE_CASE,
    /** Regular expression search with matching case. */
    REGEX_MATCH_CASE,
    /** Regular expression search with ignoring case. */
    REGEX_IGNORE_CASE;
    /**
     * @param caseSensitive True for case sensitive search, otherwise false.
     * @param useRegex True for a regular expression search, otherwise false.
     * @return The corresponding search operation for the given case.
     */
    public static Operation create(
            boolean caseSensitive, boolean useRegex) {
        if (useRegex) {
            if (caseSensitive) {
                return REGEX_MATCH_CASE;
            }
            return REGEX_IGNORE_CASE;
        }
        if (caseSensitive) {
            return MATCH_CASE;
        }
        return IGNORE_CASE;
    }
}

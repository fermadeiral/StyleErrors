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
package org.eclipse.jubula.examples.aut.dvdtool.gui;

import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;

/**
 * This class contains common constants for the dvd tool.
 * 
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public final class Constants {
    
    /** the suffix for saved dvd libraries */
    public static final String SUFFIX = "dvd"; //$NON-NLS-1$ 
    
    /** name of the root node */
    public static final String TREE_ROOT_NAME = Resources.getString("category"); //$NON-NLS-1$
    /** all languages */
    static final String[] LANGUAGES = {
        Resources.getString("language.german"), Resources.getString("language.english"), //$NON-NLS-1$ //$NON-NLS-2$
        Resources.getString("language.french"), Resources.getString("language.italian"), Resources.getString("language.spanish") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** all region codes */
    static final String[] REGION_CODES = {
        Resources.getString("region_code.rc0"), Resources.getString("region_code.rc1"), Resources.getString("region_code.rc2"), Resources.getString("region_code.rc3"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Resources.getString("region_code.rc4"), Resources.getString("region_code.rc5"), Resources.getString("region_code.rc6"), Resources.getString("region_code.rc7") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    /**
     * private constructor, do not instantiate this class.
     */
    private Constants() {
    // empty
    }
}

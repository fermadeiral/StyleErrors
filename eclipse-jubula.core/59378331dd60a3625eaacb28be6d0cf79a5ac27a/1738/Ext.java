/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.constants;

/**
 * Constants for the extension point of the teststyle framework.
 * 
 * @author marcell
 * 
 */
public class Ext {

    //--------------------------------------------------------------------------
    // Constants for plugin stuff
    //--------------------------------------------------------------------------
    /** The plug-in ID */
    public static final String PLUGIN_ID = 
        "org.eclipse.jubula.client.teststyle"; //$NON-NLS-1$
    /** My own defined extension point id */
    public static final String DEFINE_ID =
        "org.eclipse.jubula.client.teststyle.definition"; //$NON-NLS-1$

    //--------------------------------------------------------------------------
    // Constants for Marker and stuff
    //--------------------------------------------------------------------------
    /** ID of the marker that teststyle uses */
    public static final String TSM_MARKER = 
        "org.eclipse.jubula.client.teststyle.csProblem"; //$NON-NLS-1$
    /** Location in the problems view */
    public static final String TSM_LOCATION = "location"; //$NON-NLS-1$
    /** Resource in the problems view */
    public static final String TSM_RESOURCE = "resource"; //$NON-NLS-1$
    /** Path in the problems view */
    public static final String TSM_PATH = "path"; //$NON-NLS-1$

    //--------------------------------------------------------------------------
    // Constants for categories in the extension
    //--------------------------------------------------------------------------
    /** Category */
    public static final String CAT = "category"; //$NON-NLS-1$ 
    /** Category name attribute for the extension */
    public static final String CAT_NAME = "name"; //$NON-NLS-1$
    /** Category description attribute for the extension */
    public static final String CAT_DESCR = "description"; //$NON-NLS-1$
    /** Normal Exception */
    public static final String EXCEPTION = "Exception"; //$NON-NLS-1$
    /** Core Exception */
    public static final String CORE_EXCEPTION = "CoreException"; //$NON-NLS-1$
    //--------------------------------------------------------------------------
    // Constants for categories in the extension
    //--------------------------------------------------------------------------
    /** Analyzes */
    public static final String ANALYZER = "Analyzer"; //$NON-NLS-1$
    /** contexts of analyzer */
    public static final String ANALYZER_CONTEXTS = "analyzerContexts"; //$NON-NLS-1$
    /** name */
    public static final String ANALYZER_NAME = "name"; //$NON-NLS-1$
    /** class */
    public static final String ANALYZER_CLASS = "class"; //$NON-NLS-1$
    /** id */
    public static final String ANALYZER_ID = "id"; //$NON-NLS-1$
    //--------------------------------------------------------------------------
    // Constants for checks in the extension
    //--------------------------------------------------------------------------
    /** Name of the attribute children of a check */
    public static final String CHK_ATTR = "attributes"; //$NON-NLS-1$
    /** Name of the contexts children of a check */
    public static final String CHK_CONT = "contexts"; //$NON-NLS-1$
    /** Name of the contexts children of a check */
    public static final String CHK_DECCONT = "decoratingContexts"; //$NON-NLS-1$
    /** Name of the attribute which represents the class of the check */
    public static final String CHK_CLASS = "class"; //$NON-NLS-1$
    /** Name of the chk */
    public static final String CHK_NAME = "name"; //$NON-NLS-1$
    /** ID of the check */
    public static final String CHK_ID = "id"; //$NON-NLS-1$
    /** ID of the check */
    public static final String CHK_ACTIVE = "activeByDefault"; //$NON-NLS-1$
    /** Name of the attribute which represents the default value for the severity */
    public static final String CHK_SEVERITY = "defaultSeverity"; //$NON-NLS-1$   
    /** Name of the attribute which represents the description of the check */
    public static final String CHK_DSCR = "description"; //$NON-NLS-1$   
    
    //--------------------------------------------------------------------------
    // Constants for contexts in the extension
    //--------------------------------------------------------------------------
    /** The name of the base class of the contexts (for user defined stuff) */
    public static final String CONT_BASE = "BaseContext"; //$NON-NLS-1$
    /** Attribute name for the implementing class of the context */
    public static final String CONT_CLASS = "class"; //$NON-NLS-1$
    /** Attribute name for the implementing class of the context */
    public static final String CONT_ID = "id"; //$NON-NLS-1$
    
    //--------------------------------------------------------------------------
    // Constants for attributes in the extension
    //--------------------------------------------------------------------------
    /** Name of the attribute which represents the name of attribute (sounds funny) */
    public static final String ATTR_NAME = "name"; //$NON-NLS-1$
    /** Name of the attribute which represents the default value of the atttribute */
    public static final String ATTR_VALUE = "defaultValue"; //$NON-NLS-1$
    /** name of the attribute which represents the description of an attribute */
    public static final String ATTR_DESCR = "description"; //$NON-NLS-1$   
    

    
    /**
     * Private, because of utility class
     */
    private Ext() {
        // If you're reading this - thank you.
    }
}

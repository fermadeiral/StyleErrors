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
package org.eclipse.jubula.tools.internal.constants;


/**
 * @author BREDEX GmbH
 * @created May 16, 2006
 */
public interface TestDataConstants {

    /** default character for reference */
    public static final char REFERENCE_CHAR_DEFAULT = '='; 
    
    /** default character for a escape symbol */
    public static final char ESCAPE_CHAR_DEFAULT = '\\';  

    /** default character for a path seperator symbol */
    public static final char PATH_CHAR_DEFAULT = '/';  

    /** default character for a value seperator symbol */
    public static final char VALUE_CHAR_DEFAULT = ',';  

    /** Key for reference character value to be stored as a resource property */
    public static final char FUNCTION_CHAR_DEFAULT = '?'; 
    
    /** default character for a variable */
    public static final char VARIABLE_CHAR_DEFAULT = '$'; 
    
    /** Constant for an empty symbol (\) */
    public static final String EMPTY_SYMBOL = "''"; //$NON-NLS-1$
    
    /** Constant for the I18N-Key of Menu name */
    public static final String MENU_DEFAULT_MAPPING_I18N_KEY = "CompSystem.LogicalMenuName"; //$NON-NLS-1$
    
    /** Constant for the I18N-Key of Application name */
    public static final String APPLICATION_DEFAULT_MAPPING_I18N_KEY = "CompSystem.LogicalApplicationName"; //$NON-NLS-1$
    
    /** symbol for single quote */
    public static final char COMMENT_SYMBOL = '\'';
    
    /** string for delimiting combinable values */
    public static final String COMBI_VALUE_SEPARATOR = " "; //$NON-NLS-1$

    // -------------------------------------------------------------
    // Key for several parameter value types
    // -------------------------------------------------------------
    /** Constant for an Integer as parameter value */
    public static final String INTEGER = "java.lang.Integer"; //$NON-NLS-1$
    
    /** Constant for a Variable as parameter value */
    public static final String VARIABLE = "guidancer.datatype.Variable";  //$NON-NLS-1$
    
    /** Constant for a String as parameter value */
    public static final String STR = "java.lang.String";  //$NON-NLS-1$
    /** Constant for a parameter of type Boolean */
    public static final String BOOLEAN = "java.lang.Boolean"; //$NON-NLS-1$

}

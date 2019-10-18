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
 * This class contains the keys for the extension point mechanism and other
 * monitoring constants.
 *
 * @author BREDEX GmbH
 * @created 27.07.2010
 */
public class MonitoringConstants {    
    
   
    /**the name of the extension point */
    public static final String MONITORING_EXT_REG = "org.eclipse.jubula.toolkit.common.monitoring"; ////$NON-NLS-1$
    //These are the id's of the attributes in the extension 
    /** the query syntax for the extensions id */
    public static final String M_ID = "id";  //$NON-NLS-1$
    /** the query syntax for the extensions name */
    public static final String M_NAME = "name";  //$NON-NLS-1$
    /** the query syntax for the extensions linkText */
    public static final String M_LINK_TEXT = "linkText";  //$NON-NLS-1$
    /** the query syntax for the extensions id */
    public static final String M_ATTR_ID = "id";  //$NON-NLS-1$
    /** the query syntax for the extensions type */
    public static final String M_ATTR_TYPE = "type"; //$NON-NLS-1$
    /** the query syntax for the extensions description */
    public static final String M_ATTR_DESCRIPTION = "description"; //$NON-NLS-1$
    /** the query syntax for the extensions default value */
    public static final String M_ATTR_DEFAULT_VALUE = "defaultvalue"; //$NON-NLS-1$
    /** the query syntax for the extensions default value */
    public static final String M_ATTR_NAME = "name"; //$NON-NLS-1$ 
    /** the query syntax for the extensions rendering parameter */
    public static final String M_ATTR_RENDER = "render"; //$NON-NLS-1$ 
    /** the query syntax for the extensions rendering parameter */
    public static final String M_INFO_TEXT = "infoBoobleText"; //$NON-NLS-1$ 
    /** the query syntax for the extensions validator */
    public static final String M_ATTR_VALIDATOR = "validator"; ////$NON-NLS-1$
    /** the query syntax for the extensions filter */
    public static final String M_ATTR_FILTER = "filter"; ////$NON-NLS-1$   
    
    /** a type "string" (set in the extension point) will be rendered as a Textfield*/
    public static final String RENDER_AS_TEXTFIELD = "string"; //$NON-NLS-1$
    /** a type "boolean" (set in the extension point) will be rendered as a checkbox*/
    public static final String RENDER_AS_CHECKBOX = "boolean"; //$NON-NLS-1$ 
    /** a type "filebrowse" (set in the extension point) will be rendered as a Textfield with additional "browse" button */
    public static final String RENDER_AS_FILEBROWSE = "filebrowse"; //$NON-NLS-1$
    /** a type "multidirbrowse" (set in extension point) will be rendered as a multi directory browser 
     * with add, edit, remove option.   */
    public static final String RENDER_AS_MULTIDIR_BROWSE = "multidirbrowse"; //$NON-NLS-1$
    /** key for ConfigMap */
    public static final String AGENT_CLASS = "AGENT_CLASS"; //$NON-NLS-1$   
    /** key for ConfigMap */
    public static final String RESET_AGENT = "RESET_AGENT"; //$NON-NLS-1$
    /** key for ConfigMap */
    public static final String INSTALL_DIR = "INSTALL_DIR"; //$NON-NLS-1$    
    /** key for ConfigMap */
    public static final String SOURCE_DIRS = "SOURCE_DIRS"; //$NON-NLS-1$    
    /** key for ConfigMap */
    public static final String BUNDLE_ID = "BUNDLE_ID"; //$NON-NLS-1$
    
       
    /** default type for monitoring value */
    public static final String DEFAULT_TYPE = "DEFAULT_TYPE"; //$NON-NLS-1$
    
    /** default value */
    public static final String EMPTY_MONITORING_ID = "none selected"; //$NON-NLS-1$
    /** default value */
    public static final byte[] EMPTY_REPORT = new byte[0];
    /** default value */
    public static final String EMPTY_TYPE = "none"; //$NON-NLS-1$
    
 
    /** Monitoring-Value type */ 
    public static final String DOUBLE_VALUE = "DOUBLE"; //$NON-NLS-1$
    /** Monitoring-Value type */ 
    public static final String PERCENT_VALUE = "PERCENT"; //$NON-NLS-1$
    /** Monitoring-Value type */ 
    public static final String INTEGER_VALUE = "INTEGER"; //$NON-NLS-1$
    /** Monitoring category  */
    public static final String NO_CATEGORY = "NO_CATEGORY"; //$NON-NLS-1$   
    /** Monitoring-Value type */ 
    public static final String STRING_VALUE = "STRING"; //$NON-NLS-1$
    
    /** Monitoring error message */
    public static final String MONITORING_ERROR = "MONITORING_ERROR"; //$NON-NLS-1$
    
    /** key to identify monitoring widgets */
    public static final String MONITORING_KEY = "MONITORING_KEY"; //$NON-NLS-1$
    
    /** folder name where temporary files can be stored to */
    public static final String TEMP_FOLDER_NAME = "c.b.g.m.j_"; //$NON-NLS-1$
    
    
    /** private constructor */
    private MonitoringConstants() {        
        //do nothing
    }
  
    
}

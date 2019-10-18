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
 * @created 20.04.2007
 */
public interface ToolkitConstants {
    /** The ID of the Extension Point */
    public static final String EXT_POINT_ID = "org.eclipse.jubula.toolkit.common.toolkitsupport"; //$NON-NLS-1$
    
    /** The ID of this Plugin */
    public static final String PLUGIN_ID = "org.eclipse.jubula.toolkit.common"; //$NON-NLS-1$
    
    /** Property name of the IToolkitProvider of the extension point */
    public static final String ATTR_ITOOLKITPROVIDER = "IToolkitProvider";  //$NON-NLS-1$
    
    /** Extension Point Attribute name of toolkitID */
    public static final String ATTR_TOOLKITID = "toolkitID"; //$NON-NLS-1$
    
    /** Extension Point Attribute name of toolkit name */
    public static final String ATTR_NAME = "name"; //$NON-NLS-1$
    
    /** Extension Point Attribute name of includes */
    public static final String ATTR_INCLUDES = "includes"; //$NON-NLS-1$
    
    /** Extension Point Attribute name of depends */
    public static final String ATTR_DEPENDS = "depends"; //$NON-NLS-1$
    
    /** Extension Point Attribute name of level */
    public static final String ATTR_LEVEL = "level"; //$NON-NLS-1$
    
    /** Extension Point Attribute name of order */
    public static final String ATTR_ORDER = "order"; //$NON-NLS-1$
    
    /** Extension Point Attribute name of isUserToolkit */
    public static final String ATTR_ISUSERTOOLKIT = "isUserToolkit"; //$NON-NLS-1$
    
    /** Constant for the Toolkit-Level "abstract" */
    public static final String LEVEL_ABSTRACT = "abstract"; //$NON-NLS-1$
    
    /** Constant for the Toolkit-Level "concrete" */
    public static final String LEVEL_CONCRETE = "concrete"; //$NON-NLS-1$
    
    /** Constant for the Toolkit-Level "toolkit" */
    public static final String LEVEL_TOOLKIT = "toolkit"; //$NON-NLS-1$

    /** Constant for an empty extension point entry */
    public static final String EMPTY_EXTPOINT_ENTRY = "null"; //$NON-NLS-1$
    
    /** Constant for a toolkit ID indicating a non valid include toolkit */
    public static final String NO_VALID_INCLUDE_TOOLKIT = "NoValidIncludeToolkit"; //$NON-NLS-1$
}

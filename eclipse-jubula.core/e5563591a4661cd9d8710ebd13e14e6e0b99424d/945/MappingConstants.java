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
package org.eclipse.jubula.tools.internal.objects;

/**
 * Interface for constants of Object-Mapping, e.g. Default-Mapping, etc.
 *
 * @author BREDEX GmbH
 * @created 30.03.2007
 */
public interface MappingConstants {
    
    /** Full qualified name of the virtual Swing Application class */
    public static final String SWING_APPLICATION_CLASSNAME = 
        "com.bredexsw.guidancer.autserver.swing.implclasses.GraphicApplication"; //$NON-NLS-1$
    
    /** Full qualified name of the virtual SWT Application class */
    public static final String SWT_APPLICATION_CLASSNAME = 
        "com.bredexsw.guidancer.autswtserver.implclasses.GraphicApplication"; //$NON-NLS-1$
    
    /** Full qualified name of the virtual Web Application class */
    public static final String WEB_APPLICATION_CLASSNAME =
        "com.bredexsw.guidancer.autieserver.implclasses.GraphicApplication"; //$NON-NLS-1$
    
    /** Full qualified name of the Concrete Application class */
    public static final String CONCR_APPLICATION_CLASSNAME =
        "guidancer.concrete.GraphicApplication"; //$NON-NLS-1$
    
    /** Full qualified name of the Swing Menu class */
    public static final String SWING_MENU_CLASSNAME = 
        "javax.swing.JMenuBar"; //$NON-NLS-1$
    
    /** Full qualified name of the JMenuBarDefaultMapping class */
    public static final String SWING_MENU_DEFAULT_MAPPING_CLASSNAME =
        "com.bredexsw.guidancer.autserver.swing.implclasses.JMenuBarDefaultMapping"; //$NON-NLS-1$
    
    /** Full qualified name of the MenuDefaultMapping class */
    public static final String SWT_MENU_DEFAULT_MAPPING_CLASSNAME =
        "com.bredexsw.guidancer.autswtserver.implclasses.MenuDefaultMapping"; //$NON-NLS-1$

    /** Full qualified name of the SWT Menu class */
    public static final String SWT_MENU_CLASSNAME = 
        "org.eclipse.swt.widgets.Menu"; //$NON-NLS-1$

    /** 
     * The old full qualified name of the virtual Swing Application class.<br>
     * <b>Note:</b> This is still existing because of compatibility reasons.<br>
     * Actually the Constant SWING_APPLICATION_IMPLCLASS_NAME should be used
     * but this would cause that old projects cannot be loaded from the 
     * data base.<br>
     * If we change this, we have to execute a SQL command on the users DB to
     * convert the existing projects! 
     */
    public static final String SWING_APPLICATION_COMPONENT_IDENTIFIER = 
        "com.bredexsw.guidancer.server.implclasses.GraphicApplication"; //$NON-NLS-1$

    /** 
     * Similar to <code>SWING_APPLICATION_COMPONENT_IDENTIFIER</code>,
     * allows mapping between the old name for SWT Application and the new one.
     */
    public static final String SWT_APPLICATION_COMPONENT_IDENTIFIER = 
        "org.eclipse.swt.GraphicApplication"; //$NON-NLS-1$
    
    /** The technical name (in the AUT) of the Menu-Default-Mapping-Component*/
    public static final String MENU_DEFAULTMAPPING_TECHNICAL_NAME = "Menu"; //$NON-NLS-1$
}

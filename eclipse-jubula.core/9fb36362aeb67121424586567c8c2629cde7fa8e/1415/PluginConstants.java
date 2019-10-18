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
package org.eclipse.jubula.client.core.constants;

/**
 * Constants for values in the plugin.xml.
 *
 * @author BREDEX GmbH
 * @created May 18, 2010
 */
public final class PluginConstants {

    /** ID of "Database Version Error Handler" extension point  */
    public static final String DB_VERSION_HANDLER_EXT_ID = "org.eclipse.jubula.client.core.databaseVersionErrorHandler"; //$NON-NLS-1$
    
    /** 
     * name of the "class" attribute for the "Database Version Error Handler" 
     * extension point 
     */
    public static final String DB_VERSION_HANDLER_CLASS_ATTR = "class"; //$NON-NLS-1$
    /**
     * Private constructor to avoid instantiation of constants class.
     */
    private PluginConstants() {
        // Nothing to initialize.
    }
}

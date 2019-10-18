/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.launch;

/**
 * Constants for launch integration.
 * 
 * @author BREDEX GmbH
 * @created 20.04.2011
 */
public final class AutLaunchConfigurationConstants {

    /** ID for launch mode for starting an AUT */
    public static final String LAUNCH_MODE_AUT = "ui_test"; //$NON-NLS-1$
    
    /** key for AUT ID property */
    public static final String AUT_ID_KEY = "org.eclipse.jubula.launch.AUT_ID"; //$NON-NLS-1$
    
    /** default value for AUT ID property */
    public static final String AUT_ID_DEFAULT_VALUE = ""; //$NON-NLS-1$
    
    /**
     * Private constructor to prevent instantiation of "constants" class.
     */
    private AutLaunchConfigurationConstants() {
        // Nothing to initialize
    }
}

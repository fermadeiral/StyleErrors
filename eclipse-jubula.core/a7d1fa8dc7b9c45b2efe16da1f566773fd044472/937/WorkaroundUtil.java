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
package org.eclipse.jubula.rc.common.util;

import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;

/**
 * This class contains utility methods for workarounds within the 
 * AUT Server component.
 *
 * @author BREDEX GmbH
 * @created Jun 23, 2009
 */
public class WorkaroundUtil {
    /**
     * <code>CHAR_9</code>
     */
    public static final char CHAR_9 = '9';
    /**
     * the lower case char <code>'b'</code>
     */
    public static final char CHAR_B = 'b';
    
    /** 
     * Name of the environment variable that defines whether the client should
     * ignore server-side timeouts that occur during test execution.
     */
    private static final String IGNORE_TIMEOUT_VAR = "TEST_RC_IGNORE_TIMEOUT"; //$NON-NLS-1$
    
    /**
     * Private constructor
     */
    private WorkaroundUtil() {
        // Nothing to initialize
    }
    
    /**
     * Allows server-side timeouts to be ignored. This is used, for example,
     * to work around the fact that a specific configuration of Linux/GTK/SWT
     * does not produce mouse click events for left click on a TabFolder.
     * 
     * @return <code>true</code> if server-side timeouts should be ignored. 
     *         Otherwise <code>false</code>.
     */
    public static boolean isIgnoreTimeout() {
        String value = EnvironmentUtils
                .getProcessOrSystemProperty(IGNORE_TIMEOUT_VAR);

        return Boolean.valueOf(value).booleanValue();
    }
}

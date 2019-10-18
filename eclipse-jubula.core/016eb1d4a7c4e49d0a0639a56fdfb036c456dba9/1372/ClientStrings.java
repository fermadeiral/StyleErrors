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
package org.eclipse.jubula.client.cmd.constants;

/**
 * This Class contains all the argument names, which can be set either by
 * command line or by the XML config file. Single space for all strings used in
 * CmdLineClient arguments
 * 
 * @author BREDEX GmbH
 * @created Feb 10, 2007
 */
public final class ClientStrings {
    /** Error message */
    public static final String ERR_UNEXPECTED = "An unexpected error occurred in command-line-client: "; //$NON-NLS-1$

    /** object attribute */
    public static final String QUIET = "q"; //$NON-NLS-1$

    /** object attribute */
    public static final String NORUN = "n"; //$NON-NLS-1$
    /** constant string */
    public static final String NORUN_MODE = "norun_mode"; //$NON-NLS-1$

    /** object attribute */
    public static final String CONFIG = "c"; //$NON-NLS-1$

    /** object attribute */
    public static final String HELP = "h"; //$NON-NLS-1$

    /** object attribute */
    public static final String VERSION = "v"; //$NON-NLS-1$
    
    /** object attribute */
    public static final String CONFIGFILE = "configfile"; //$NON-NLS-1$
    
    /** object attribute */
    public static final String RESULT_NAME = "resultname"; //$NON-NLS-1$

    /** to prevent instantiation */
    private ClientStrings() {
        // do nothing
    }
}

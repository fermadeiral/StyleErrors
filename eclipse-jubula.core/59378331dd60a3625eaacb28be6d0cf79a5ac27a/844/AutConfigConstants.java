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
 * This class contains the keys of the AUT configuration properties 
 * and the IDs of the toolkit plugins as public constants.
 *
 * @author BREDEX GmbH
 * @created Jun 24, 2008
 */
public class AutConfigConstants {
    /** 
     * the AUT ID to use when matching the running AUT started by the 
     * configuration with an AUT definition
     */
    public static final String AUT_ID = "AUT_ID"; //$NON-NLS-1$
    /** Key for the name of an AUT Configuration */
    public static final String AUT_CONFIG_NAME = "CONFIG_NAME"; //$NON-NLS-1$
    /** The name of the AUT to register */
    public static final String AUT_NAME = "com.bredexsw.guidancer.aut.register.autName"; //$NON-NLS-1$
    /** The hostname of the AUT */
    public static final String AUT_HOST = "AUT_HOST"; //$NON-NLS-1$
    /** The port at the remote hostname of the AUT */
    public static final String AUT_HOST_PORT = "AUT_HOST_PORT"; //$NON-NLS-1$
    /** The hostname for the AUT-Agent with which to register */
    public static final String AUT_AGENT_HOST = "com.bredexsw.guidancer.aut.register.autAgentHost"; //$NON-NLS-1$
    /** The port for the AUT Agent with which to register */
    public static final String AUT_AGENT_PORT = "com.bredexsw.guidancer.aut.register.autAgentPort"; //$NON-NLS-1$
    /** The AUT hostname set in the AUT config */
    public static final String AUT_CONFIG_AUT_HOST_NAME = "SERVER"; //$NON-NLS-1$
    /** The locale of the AUT */
    public static final String AUT_LOCALE = "AUT_LOCALE"; //$NON-NLS-1$
    /** The method for activation on test start (enum ActivationMethod) */
    public static final String ACTIVATION_METHOD = "ACTIVATION_METHOD"; //$NON-NLS-1$
    /** The MonitoringAgent ID*/
    public static final String MONITORING_AGENT_ID = "MONITORING_AGENT_ID";  //$NON-NLS-1$
    /** The XML/HTML report DIR (preferences)*/
    public static final String REPORT_DIR = "REPORT_DIR"; //$NON-NLS-1$
    /** The arguments of the AUT (main-args) */
    public static final String AUT_ARGUMENTS = "AUT_ARGUMENTS"; //$NON-NLS-1$    
    /** The arguments of the Aut-Run AUT (main-args) */
    public static final String AUT_RUN_AUT_ARGUMENTS = "AUT_RUN_AUT_ARGUMENTS"; //$NON-NLS-1$
    /** The arguments of the Aut-Run AUT (main-args) split character */
    public static final char AUT_RUN_AUT_ARGUMENTS_SEPARATOR_CHAR = '?';
    /** The environment of the AUT */
    public static final String ENVIRONMENT = "ENVIRONMENT"; //$NON-NLS-1$
    /** The key for the name of the Modern UI app used to start the AUT. */
    public static final String AUT_TYPE = "AUT_TYPE"; //$NON-NLS-1$
    /** The key for the name of the executable file used to start the AUT. */
    public static final String EXECUTABLE = "EXECUTABLE"; //$NON-NLS-1$
    /** The key for the name of property used to highlight error components during test execution. */
    public static final String ERROR_HIGHLIGHT = "ERROR_HIGHLIGHTING"; //$NON-NLS-1$
    /** The key for the name of the Modern UI app used to start the AUT. */
    public static final String APP_NAME = "APP_NAME"; //$NON-NLS-1$
    /** The executable jar file of the AUT */
    public static final String JAR_FILE = "JAR_FILE"; //$NON-NLS-1$
    /** The jre binary (executable file, e.g. javaw.exe, java, etc.) */
    public static final String JRE_BINARY = "JRE_BINARY"; //$NON-NLS-1$
    /** The parameter of the JRE */
    public static final String JRE_PARAMETER = "JRE_PARAMETER"; //$NON-NLS-1$
    /** The working directory of the AUT */
    public static final String WORKING_DIR = "WORKING_DIR"; //$NON-NLS-1$
    /** the id tag for the AUT */
    public static final String WEB_ID_TAG = "WEB_ID_TAG"; //$NON-NLS-1$
    /** the browser for the AUT */
    public static final String BROWSER = "BROWSER"; //$NON-NLS-1$
    /** the browser path for the AUT */
    public static final String BROWSER_PATH = "BROWSER_PATH"; //$NON-NLS-1$
    /** the browser path for the AUT */
    public static final String DRIVER_PATH = "DRIVER_PATH"; //$NON-NLS-1$
    /** the mode in which the AUT starts**/
    public static final String SINGLE_WINDOW_MODE = "SINGLE_WINDOW_MODE"; //$NON-NLS-1$
    /** whether selenium uses webdrivers **/
    public static final String WEBDRIVER_MODE = "WEBDRIVER_MODE"; //$NON-NLS-1$
    /** The URL of the AUT */
    public static final String AUT_URL = "AUT_ARGUMENTS"; //$NON-NLS-1$
    /** The key to set the automatically naming of technical components */
    public static final String NAME_TECHNICAL_COMPONENTS = "NAME_TECHNICAL_COMPONENTS"; //$NON-NLS-1$
    /** The keyboard layout of the AUT */
    public static final String KEYBOARD_LAYOUT = "KEYBOARD_LAYOUT"; //$NON-NLS-1$
    /** The key for allowing class file id collision*/
    public static final String ALLOW_CLASS_FILE_ID_COLLISION = "CLASS_FILE_ID_COLLISION"; //$NON-NLS-1$
    /** the size of the browser for the AUT */
    public static final String BROWSER_SIZE = "BROWSER_SIZE"; //$NON-NLS-1$
    /** Path where monitoring data will be saved.*/
    public static final String EXTERNAL_MONITORING_REPORT_PATH = "EXTERNAL_MONITORING_REPORT_PATH"; //$NON-NLS-1$
    
    /** to prevent instantiation */
    private AutConfigConstants() {
        // do nothing
    }
}

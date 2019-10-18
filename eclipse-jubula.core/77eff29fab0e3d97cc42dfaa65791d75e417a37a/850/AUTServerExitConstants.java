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
 * This class contains the exit codes of the AUTServer as public constants.
 *
 * @author BREDEX GmbH
 * @created 21.10.2004
 */
public class AUTServerExitConstants {
    /** normal exit state, e.g closing the AUT */
    public static final int EXIT_OK = 0;
    
    /** exit state in case of IllegalArgumentException caused by an arg */
    public static final int EXIT_INVALID_ARGS = 1;

    /** exit state in case of not enough arguments */
    public static final int EXIT_INVALID_NUMBER_OF_ARGS = 2;
    
    /** exit state when the AUT could not loaded */
    public static final int EXIT_AUT_NOT_FOUND = 3;
    /** exit state when the AUT could not loaded */
    public static final int EXIT_AUT_WRONG_CLASS_VERSION = 4;
    /** exit state when the process was already closed **/
    public static final int EXIT_AUT_STOP_FAILED = 5;
    
    /** exit state in case of an UnknownHostException during connecting the ITE client */
    public static final int EXIT_UNKNOWN_ITE_CLIENT = 10;
    
    /** exit state in case of a communication error */
    public static final int EXIT_COMMUNICATION_ERROR = 11;

    /** exit code in case of missing AUT Agent connection information */
    public static final int EXIT_MISSING_AGENT_INFO = 12;

    /** exit state in case of a SecurityException during connecting the 
     * JubulaClient 
     */ 
    public static final int EXIT_SECURITY_VIOLATION_COMMUNICATION = 20;
    
    /** exit state in case of a SecurityException during installing/removing an 
     * AWTEventListener
     */ 
    public static final int EXIT_SECURITY_VIOLATION_AWT_EVENT_LISTENER = 21;
    
    /** exit state in case of a SecurityException during installing a
     *  shutDown hook
     */ 
    public static final int EXIT_SECURITY_VIOLATION_SHUTDOWN = 22;
    
    /** exit state in case of an SecurityException from reflection */
    public static final int EXIT_SECURITY_VIOLATION_REFLECTION = 23;    
    
    /** exit state in case of a restart while test execution */
    public static final int RESTART = 24;
    
    /** exit state in case of an error while starting the AUT */
    public static final int AUT_START_ERROR = 25;
    
    /** exit state in case of an ClassNotFoundException while starting the AUT */
    public static final int AUT_START_ERROR_CNFE = 27;

    /** exit state in case of an IllegalAccessException while starting the AUT */
    public static final int AUT_START_ERROR_IACCE = 28;

    /** exit state in case of an NoSuchMethodException while starting the AUT */
    public static final int AUT_START_ERROR_NSME = 29;

    /** exit state in case of an IllegalArgumentException while starting the AUT */
    public static final int AUT_START_ERROR_IARGE = 30;

    /**exit state in case of an InvocationTargetException while starting the AUT */
    public static final int AUT_START_ERROR_INVTE = 31;
    
    /** exit state in case that the started AUT is not supported
     * through the UI automation framework */
    public static final int AUT_NOT_UIA_SUPPORTED = 26;
    
    /** exit state in case that the started AUT is cannot be started cause of an already
     *  used address*/
    public static final int AUT_START_ADDRESS_ALREADY_IN_USE = 134;

    /**
     * do not instantiate
     */
    private AUTServerExitConstants() {
        super();
    }
}

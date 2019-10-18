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
package org.eclipse.jubula.client.core.events;

import org.apache.commons.lang.StringUtils;

/**
 * The event class containing state concerning the AUTServer.
 *
 * @author BREDEX GmbH
 * @created 13.08.2004
 */
public class AUTServerEvent extends ServerEvent {
    /** constant no connection could opend for accepting */
    public static final int COULD_NOT_ACCEPTING = NUMBER_OF_CONSTANTS + 1; //3
    
    /** constant: the communication client - autserver could not established */ 
    public static final int COMMUNICATION = COULD_NOT_ACCEPTING + 1; //4
    
    /** constant: jar does not contain a main class */
    public static final int NO_MAIN_IN_JAR = COMMUNICATION + 1; //5

    /** constant: classpath does not reference to jar */
    public static final int INVALID_JAR = NO_MAIN_IN_JAR + 1; //6

    /** constant: no valid java executable found */
    public static final int INVALID_JAVA = INVALID_JAR + 1; //7

    /** constant: starting a test suite */
    public static final int TESTING_MODE = INVALID_JAVA + 1; //8

    /** constant: entering mapping mode */
    public static final int MAPPING_MODE = TESTING_MODE + 1; //9

    /** constant: entering record mode */
    public static final int RECORD_MODE = MAPPING_MODE + 1; //10

    /** constant: AUT server could not be instantiated */
    public static final int SERVER_NOT_INSTANTIATED = RECORD_MODE + 1; //11

    /** constant: the dotNet runtime is not properly installed */
    public static final int DOTNET_INSTALL_INVALID = //12
        SERVER_NOT_INSTANTIATED + 1;

    /** the JDK used by the AUT is probably older than 1.5, javaagent is unknown */
    public static final int JDK_INVALID = DOTNET_INSTALL_INVALID + 1; //13
    
    /** constant: entering check mode */
    public static final int CHECK_MODE = JDK_INVALID + 1; //14
    
    /** description for logging purpose */
    private static final String CNA_DESCRIPTION = 
        "connection could not be accepted";  //$NON-NLS-1$

    /** description for logging purpose */
    private static final String COM_DESCRIPTION = 
        "the communication between client and aut server could not established";  //$NON-NLS-1$

    /** description for logging purpose */
    private static final String NMIJ_DESCRIPTION = 
        "jar does not contain a main class";  //$NON-NLS-1$

    /** description for logging purpose */
    private static final String IJ_DESCRIPTION = 
        "classpath does not reference to jar";  //$NON-NLS-1$

    /** description for logging purpose */
    private static final String SNI_DESCRIPTION = 
        "AUT Server could not be created";  //$NON-NLS-1$

    /** description of unknown state (this means it's an programming error)
     *  for logging purpose */
    private static final String US_DESCRIPTION =
        "unknown state"; //$NON-NLS-1$

    /** description of state */
    private static final String DOTNET_DESCRIPTION =
        "the dotNet runtime is not properly installed"; //$NON-NLS-1$
    
    /** description of state */
    private static final String JDK_INVALID_DESCRIPTION =
        "Unrecognized option while trying to use -javaagent."; //$NON-NLS-1$
    
    /** Inner message */
    private String m_additionalInfo = StringUtils.EMPTY;
    
    /**
     * constructor with paramerter for the state, see defined constants in
     * <code>ServerEvent</code>.
     * 
     * @param state
     *            the new state of the AUTServer
     */
    public AUTServerEvent(int state) {
        super(state);
    }
    
    /**
     * constructor with paramerter for the state, see defined constants in
     * <code>ServerEvent</code>.
     * 
     * @param state
     *            the new state of the AUTServer
     * @param additionalInfo
     *            additional message of the state
     */
    public AUTServerEvent(int state, String additionalInfo) {
        super(state);
        this.m_additionalInfo = additionalInfo;
    }
    
    /**
     * @return a readable description of the event
     */
    public String toString() {
        if (getState() <= NUMBER_OF_CONSTANTS) {
            return super.toString();
        }
        
        switch (getState()) {
            case COULD_NOT_ACCEPTING:
                return CNA_DESCRIPTION;
            case COMMUNICATION:
                return COM_DESCRIPTION;
            case INVALID_JAR:
                return IJ_DESCRIPTION;
            case NO_MAIN_IN_JAR:
                return NMIJ_DESCRIPTION;
            case SERVER_NOT_INSTANTIATED:
                return SNI_DESCRIPTION;
            case DOTNET_INSTALL_INVALID:
                return DOTNET_DESCRIPTION;
            case JDK_INVALID:
                return JDK_INVALID_DESCRIPTION;
                
            default:
                return US_DESCRIPTION;
        }
    }
    
    /**
     * @return a readable description of the event
     */
    public String toStringWithAdditionalInformation() {
        StringBuilder builder = new StringBuilder();
        builder.append(this);
        if (StringUtils.isNotBlank(m_additionalInfo)) {
            builder.append(" - "); //$NON-NLS-1$
            builder.append(m_additionalInfo);
        }
        return builder.toString();
    }
    
    /**
     * @return additional information about the state
     */
    public String getAdditionalInfo() {
        return m_additionalInfo;
    }
}

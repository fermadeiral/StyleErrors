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

/**
 * This class contains detailed information about the occurred event. 
 * (IAUTEventListener.stateChanged())
 *
 * @author BREDEX GmbH
 * @created 16.07.2004
 *
 */
public class AUTEvent {
    /** constant signaling that the AUT is now running */
    public static final int AUT_STARTED = 1;
    
    /** constant signaling that the AUT was stopped */
    public static final int AUT_STOPPED = AUT_STARTED + 1;
    
    /** constant signaling that the AUT was unexpected stopped */ 
    public static final int AUT_ABORTED = AUT_STOPPED + 1;

    /** constant signaling that the AUT was not found */
    public static final int AUT_NOT_FOUND = AUT_ABORTED + 1;
    
    /** constant signaling that the main method of the AUT was not found */
    public static final int AUT_MAIN_NOT_FOUND = AUT_NOT_FOUND + 1;
    
    /** constant signaling that the AUT could not started */
    public static final int AUT_START_FAILED = AUT_MAIN_NOT_FOUND + 1;

    /** constant signaling that the AUT could not started */
    public static final int AUT_CLASS_VERSION_ERROR = AUT_START_FAILED + 1;
    
    /** constant signaling that the AUT is restarted and running */
    public static final int AUT_RESTARTED = AUT_CLASS_VERSION_ERROR + 1;

    /** constant signaling that the AUT is about to terminate */
    public static final int AUT_ABOUT_TO_TERMINATE = AUT_RESTARTED + 1;
    
    /** description of AUT_STARTED for logging purpose */
    private static final String AUT_STARTED_DESCRIPTION = 
        "AUT started";  //$NON-NLS-1$
    
    /** description of AUT_RESTARTED for logging purpose */
    private static final String AUT_RESTARTED_DESCRIPTION =
        "AUT restarted";  //$NON-NLS-1$
    
    /** description of AUT_ABOUT_TO_TERMINATE_DESCRIPTION for logging purpose */
    private static final String AUT_ABOUT_TO_TERMINATE_DESCRIPTION =
        "AUT about to terminate";  //$NON-NLS-1$
    
    /** description of AUT_STOPPED for logging purpose */
    private static final String AUT_STOPPED_DESCRIPTION = 
        "AUT stopped";  //$NON-NLS-1$

    /** description of AUT_ABORTED for logging purpose */
    private static final String AUT_ABORTED_DESCRIPTION = 
        "AUT aborted";  //$NON-NLS-1$

    /** description of AUT_NOT_FOUND for logging purpose */
    private static final String AUT_NOT_FOUND_DESCRIPTION = 
        "starting AUT failed: AUT not found";  //$NON-NLS-1$

    /** description of AUT_MAIN_NOT_FOUND for logging purpose */
    private static final String AUT_MAIN_NOT_FOUND_DESCRIPTION = 
        "class with main method of the AUT not found";  //$NON-NLS-1$

    /** description of AUT_START_FAILED for logging purpose */
    private static final String AUT_START_FAILED_DESCRIPTION = 
        "starting AUT failed";  //$NON-NLS-1$

    /** constant signaling that the AUT could not started */
    private static final String AUT_CLASS_VERSION_ERROR_DECRIPTION = 
        "AUT class version is not compatible to used JRE";  //$NON-NLS-1$

    /** description of unknown state (this means it's an programming error)
     *  for logging purpose */
    private static final String US_DESCRIPTION =
        "unknown state"; //$NON-NLS-1$

    
    /** the new state */
    private int m_state;
    
    /**
     * constructor with parameter state, see constants
     * @param state the new state
     */
    public AUTEvent(int state) {
        m_state = state;
    }
    
    /**
     * @return Returns the state.
     */
    public int getState() {
        return m_state;
    }
    
    /**
     * @return a readable description of the event
     */
    public String toString() {
        int state = getState();
        switch (state) {
            case AUT_STARTED:
                return AUT_STARTED_DESCRIPTION;
            case AUT_STOPPED:
                return AUT_STOPPED_DESCRIPTION;
            case AUT_ABORTED:
                return AUT_ABORTED_DESCRIPTION;
            case AUT_NOT_FOUND:
                return AUT_NOT_FOUND_DESCRIPTION;
            case AUT_MAIN_NOT_FOUND:
                return AUT_MAIN_NOT_FOUND_DESCRIPTION;
            case AUT_START_FAILED:
                return AUT_START_FAILED_DESCRIPTION;
            case AUT_CLASS_VERSION_ERROR:
                return AUT_CLASS_VERSION_ERROR_DECRIPTION;
            case AUT_RESTARTED:
                return AUT_RESTARTED_DESCRIPTION;
            case AUT_ABOUT_TO_TERMINATE:
                return AUT_ABOUT_TO_TERMINATE_DESCRIPTION;
            default:
                return US_DESCRIPTION;
        }
    }
}

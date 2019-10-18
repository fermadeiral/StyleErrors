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
package org.eclipse.jubula.rc.common;

import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.toolkit.enums.ValueSets.SearchType;

/**
 * Constants for all toolkit server
 * @author BREDEX GmbH
 * @created 07.07.2006
 */
public class Constants {
    //  ----------------------------------------------------
    // --------- COLORS ------------------------------------
    //  ----------------------------------------------------
    /** RGB-value "R" for object mapping */
    public static final int MAPPING_R = 20;
    /** RGB-value "G" for object mapping */
    public static final int MAPPING_G = 170;
    /** RGB-value "B" for object mapping */
    public static final int MAPPING_B = 140;
    /** RGB-value "R" for observing */
    public static final int OBSERVING_R = 200;
    /** RGB-value "G" for observing */
    public static final int OBSERVING_G = 0;
    /** RGB-value "B" for observing */
    public static final int OBSERVING_B = 0;
    // ----------------------------------------------------
    // --------- GENERAL ----------------------------------
    // ----------------------------------------------------
    /** the seperator between the class name and a number, used by<code>createName()</code>. */
    public static final String CLASS_NUMBER_SEPERATOR = "_"; //$NON-NLS-1$ 
    /** the initial capacity of the HashTable m_hierarchyMap*/
    public static final int INITIAL_CAPACITY_HIERARCHY = 1000;
    /** the initial capacity of the Hashtable m_topLevelContainerMap */
    public static final int INITIAL_CAPACITY_TOPLEVEL = 100;

    // ----------------------------------------------------
    // --------- HEURISTIK --------------------------------
    // ----------------------------------------------------
    /** the factor how strong name equivalenz will be decreased if name is generated */
    public static final double GENERATED_NAME_MALUS = 0.15;
    /** the factor how strong path equivalenz will increase total equivalenz */
    public static final double PATH_FACTOR = 0.30;
    /** the factor how strong context equivalenz will increase total equivalenz */
    public static final double CONTEXT_FACTOR = 0.10;
    /** the threshold value, when a component will be selected as equivalents */
    public static final double THRESHOLD_VALUE = 0.85;
    /**  the factor how strong name equivalenz will increase total equivalenz */
    public static final double NAME_FACTOR = 0.60;
    // ----------------------------------------------------
    // ---------  For HTML  --------------------------------
    // ----------------------------------------------------
    /** the string for xpath (locator starts with this string) */
    public static final String XPATH = "xpath="; //$NON-NLS-1$
    /** Constant for follow link in current window */
    public static final String FOLLOW_LINK_HERE = "here"; //$NON-NLS-1$
    /** Constant for follow link in new tab */
    public static final String FOLLOW_LINK_TAB = "newTab"; //$NON-NLS-1$
    /** Constant for follow link in new window */
    public static final String FOLLOW_LINK_WINDOW = "newWindow"; //$NON-NLS-1$

    // ----------------------------------------------------
    // ---------  Observation Parameters  -------------------
    // ----------------------------------------------------
    /** Constant for absolute search type in observation mode */
    public static final String REC_SEARCH_MODE = SearchType.absolute.rcValue();
    /** Constant for operator type in observ mode*/
    public static final String REC_OPERATOR = MatchUtil.EQUALS;
    /** Constant for ExtendSelection (Lists) in observ mode*/
    public static final String REC_EXT_SELECTION = "no"; //$NON-NLS-1$
    /** Constant for empty Modes, MenuItems etc */
    public static final String EMPTY_ITEM = "'^$'"; //$NON-NLS-1$
    /** Constant for units for ClickInComponent */
    public static final String REC_UNITS = "percent"; //$NON-NLS-1$
    /** Constant for ClickCount-fixed-Message */
    public static final String REC_CLICK_MSG = "ClickCount of observed Action set from 0 to 1"; //$NON-NLS-1$
    /** Constant for maximum-string-length-message */
    public static final String REC_MAX_STRING_MSG = "Actions with Strings larger than 3999 are not supported"; //$NON-NLS-1$
    /** Constant for multiline-not-supported-Message */
    public static final String REC_MULTILINE_MSG = "Multi-line text is not supported for Replace Text"; //$NON-NLS-1$
    /** Constant for timeout for "WaitForWindow-action */
    public static final int REC_WAIT_TIMEOUT = 30000;
    /** Constant for delay for "WaitForWindow-action */
    public static final int REC_WAIT_DELAY = 500;
    /** Constant for maximum length of technical names for Objectmapping and CAP-Names */
    public static final int REC_MAX_NAME_LENGTH = 200;
    /** Constant for maximum length of string values */
    public static final int REC_MAX_STRING_LENGTH = 3999;
    // ----------------------------------------------------
    // ---------  AutServer Arguments  --------------------
    // ----------------------------------------------------
    /** position number of the port number in args */
    public static final int ARG_SERVERPORT = 0;
    /** position number of the main class of the AUT in args */
    public static final int ARG_AUTMAIN = ARG_SERVERPORT + 1;
    /** position number of the classpath of the AutServer in argsposition number of the main class of the AUT in args */
    public static final int ARG_AUTSERVER_CLASSPATH = ARG_AUTMAIN + 1;
    /** position number of the <code>AUTSERVER_NAME</code> */
    public static final int ARG_AUTSERVER_NAME = ARG_AUTSERVER_CLASSPATH + 1;
    /** position number for information if agent is activated */
    public static final int ARG_REG_HOST = ARG_AUTSERVER_NAME + 1;
    /** position number for information if agent is activated */
    public static final int ARG_REG_PORT = ARG_REG_HOST + 1;
    /** position number for information if agent is activated */
    public static final int ARG_AUT_NAME = ARG_REG_PORT + 1;
    /** position number for information if agent is activated */
    public static final int ARG_AGENT_SET = ARG_AUT_NAME + 1;
    
    /** classpath of the AutServer */
    public static final String AUT_SERVER_CLASSPATH = "AUT_SERVER_CLASSPATH"; //$NON-NLS-1$
    /** aut server name */
    public static final String AUT_SERVER_NAME = "AUT_SERVER_NAME"; //$NON-NLS-1$
    /**the port */
    public static final String AUT_SERVER_PORT = "AUT_SERVER_PORT"; //$NON-NLS-1$
    /** */
    public static final String AUT_MAIN = "AutMain";
    /** the Method name for public static void main(String[] args) */
    public static final String MAIN_METHOD_NAME = "main"; //$NON-NLS-1$

    /** 
     * the minimum number of arguments in args, see class description, 
     * also position number of the first argument for the AUT in args
     */
    public static final int MIN_ARGS_REQUIRED = ARG_AGENT_SET + 1;
    
    /** classname of autServer */
    public static final String AUTSERVER_CLASSNAME = "org.eclipse.jubula.rc.common.AUTServer"; //$NON-NLS-1$

    /** to prevent instantiation */
    private Constants() {
        // do nothing
    }
}
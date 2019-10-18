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
 * @author BREDEX GmbH
 * @created Oct 1, 2010
 */
public abstract class TimingConstantsServer {
    /**
     * <code>DEFAULT_FIND_COMPONENT_TIMEOUT</code>
     */
    public static final int DEFAULT_FIND_COMPONENT_TIMEOUT = 500;
    /**
     * <code>POLLING_DELAY_FIND_COMPONENT</code>
     */
    public static final int POLLING_DELAY_FIND_COMPONENT = 100;
    
    /** timeout between retries */
    public static final int GET_LOCATION_RETRY_DELAY = 1000;
    
    /**
     * <code>POLLING_DELAY_AUT_SERVER_STARTUP</code>
     */
    public static final int POLLING_DELAY_AUT_START = 100;
    
    /**
     * <code>POLLING_DELAY_AUT_REGISTER</code>
     */
    public static final int POLLING_DELAY_AUT_REGISTER = 500;
    
    /**
     * <code>POLLING_DELAY_EXECUTE_EXTERNAL_COMMAND</code>
     */
    public static final int POLLING_DELAY_EXECUTE_EXTERNAL_COMMAND = 750;
    
    /**
     * <code>POLLING_DELAY_WAIT_FOR_COMPONENT</code>
     */
    protected static final int POLLING_DELAY_WAIT_FOR_COMPONENT = 100;
    
    /** Default for time between mouse down and mouse up */
    protected static final int DEFAULT_DELAY_POST_MOUSE_DOWN = 50;

    /** Default for max AUT response time  */
    protected static final int DEFAULT_MAX_AUT_RESPONSE_TIME = 5000;
    
    /** Default for wait after mouse up */
    protected static final int DEFAULT_DELAY_POST_MOUSE_UP = 
        DEFAULT_DELAY_POST_MOUSE_DOWN;

    /** Default for pre click delay */
    protected static final int DEFAULT_DELAY_PRE_CLICK = 1000;
    
    /** Default value for incrementing the system double click speed */
    protected static final int DEFAULT_DELAY_PRE_CLICK_INCREMENT = 50;
    
    /** time to wait before the popup is shown */
    protected static final int PRE_SHOW_POPUP_DELAY = 100;
    
    /** time to wait after the sub menu item has been clicked */
    protected static final int POST_SHOW_SUB_MENU_DELAY = 100;
    
    /**
     * delay after activate in ms
     */
    protected static final int POST_WINDOW_ACTIVATION_DELAY = 200;

    /** Default for wait after mouse up */
    protected static final int DEFAULT_KEY_INPUT_POST_DELAY = 300;

    /**
     * Constructor
     */
    protected TimingConstantsServer() {
        // hide
    }
}

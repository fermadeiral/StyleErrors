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
 * This interface holds timeout constants for client <-> server communication
 * and other actions which may time out, e.g. waiting for a popup
 * 
 * @author BREDEX GmbH
 * @created Oct 1, 2010
 */
public interface TimeoutConstants {
    /** The default timeout for cap-test-requests in MILLISECONDS! */
    public static final int CLIENT_SERVER_TIMEOUT_DEFAULT_REQUEST = 600000;
    
    /** timeout for reporting after a job aborting failure( cap test request timeout) */
    public static final int CLIENT_REPORTING_AFTER_FAILURE_TIMEOUT = 1800000;
    
    /**
     * <code>CLIENT_SERVER_TIMEOUT_TAKE_SCREENSHOT</code>
     */
    public static final int CLIENT_SERVER_TIMEOUT_TAKE_SCREENSHOT = 10000;
    
    /** The timeout */
    public static final int SERVER_CLIENT_TIMEOUT_CAP_RECORDED = 6000;
    
    /** timeout for waiting for the popup */
    public static final int SERVER_TIMEOUT_WAIT_FOR_POPUP = 10000;
    
    /** Default for event confirm timeout */
    public static final int SERVER_TIMEOUT_EVENTCONFIRM_DEFAULT = 2000;
    
    /** Amount of milliseconds to delay the AUTs termination */
    public static final int AUT_KEEP_ALIVE_DELAY_DEFAULT = 5000;
}

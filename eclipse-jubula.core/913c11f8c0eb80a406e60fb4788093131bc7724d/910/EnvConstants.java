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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 17.07.2007
 */
public final class EnvConstants {
    /** Constants for the Environment Variable to get the AUT-Agent port */
    public static final String AUT_AGENT_PORT = "TEST_AUT_AGENT_PORT"; //$NON-NLS-1$
    
    /** <code>LOCALHOST_IP_ALIAS</code> */
    public static final String LOCALHOST_IP_ALIAS = "127.0.0.1"; //$NON-NLS-1$

    /** <code>LOCALHOST_ALIAS</code> */
    public static final String LOCALHOST_ALIAS = "localhost"; //$NON-NLS-1$
    
    /** <code>AUT_AGENT_DEFAULT_PORT</code> */
    public static final int AUT_AGENT_DEFAULT_PORT = 60000;
    
    /** <code>EMBEDDED_AUT_AGENT_DEFAULT_PORT</code> */
    public static final int EMBEDDED_AUT_AGENT_DEFAULT_PORT = 
        AUT_AGENT_DEFAULT_PORT + 1;

    /** <code>LOCALHOST</code>; maybe <code>null</code> if retrieval failed */
    public static final InetAddress LOCALHOST;
    
    /** <code>LOCALHOST_FQDN</code> */
    public static final String LOCALHOST_FQDN;
    
    /** Key for setting the client port */
    public static final String CLIENTPORT_KEY = "jubClientPort"; //$NON-NLS-1$
    
    /** Key for setting the client IP address */
    public static final String CLIENTIP_KEY = "jubClientAddr"; //$NON-NLS-1$
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(EnvConstants.class);
    
    static {
        InetAddress localhost = null;
        String localhostFQDN = LOCALHOST_ALIAS;
        try {
            localhost = InetAddress.getLocalHost();
            if (localhost != null)  {
                localhostFQDN = localhost.getCanonicalHostName();
            }
        } catch (UnknownHostException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        LOCALHOST = localhost;
        LOCALHOST_FQDN = localhostFQDN;
    }
    
    /**
     * Constructor
     */
    private EnvConstants() {
        // hide
    }
}
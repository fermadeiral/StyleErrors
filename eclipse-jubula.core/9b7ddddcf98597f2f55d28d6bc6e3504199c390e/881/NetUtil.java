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
package org.eclipse.jubula.tools.internal.utils;

import java.io.IOException;
import java.net.ServerSocket;

import org.eclipse.jubula.tools.internal.constants.IOConstants;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class contains net utility methods
 * 
 * @author BREDEX GmbH
 * @created 10.11.2009
 */
public final class NetUtil {
    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger(NetUtil.class);

    /** hide constructor */
    private NetUtil() {
    // nothing in here
    }

    /**
     * 
     * @return a free port on the local machine.
     */
    public static int getFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e) {
            LOG.error("Error occurred while searching for available port. Invalid port will be returned.", e); //$NON-NLS-1$
            return -1;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOG.error("Error occurred while searching for available port.", e); //$NON-NLS-1$
                }
            }
        }
    }
    
    /**
     * validates the port number
     * 
     * @param value
     *            the value to be validated
     * @return a string indicating whether the given value is valid; null means
     *         valid, and non-null means invalid, with the result being the
     *         error message to display to the end user
     */
    public static String isPortNumberValid(String value) {
        try {
            int portValue = Integer.parseInt(value);
            if (portValue < IOConstants.MIN_PORT_NUMBER
                || portValue > IOConstants.MAX_PORT_NUMBER) {
                return I18n.getString("ErrorMessage.INVALID_PORT_NUMBER"); //$NON-NLS-1$
            }
            return null;
        } catch (NumberFormatException nfe) {
            // Fall through
        }
        return I18n.getString("ErrorMessage.INVALID_PORT_NUMBER"); //$NON-NLS-1$
    }
}

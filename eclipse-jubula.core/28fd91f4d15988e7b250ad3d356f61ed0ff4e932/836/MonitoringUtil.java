/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.tools.internal.utils;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;

/**
 * Class contains monitoring utility methods
 */
public final class MonitoringUtil {
    /** hide constructor */
    private MonitoringUtil() {
    // nothing in here
    }

    /**
     * @param parameters
     *            The parameters for starting the AUT.
     * @return <code>true</code> if the AUT is supposed to be started with a
     *         monitoring agent and all necessary information are available.
     *         Otherwise <code>false</code>. This method does not know anything
     *         about the actual availability of e.g. the JARs required for
     *         monitoring on AUT-Agent side.
     */
    public static boolean shouldAndCanRunWithMonitoring(Map parameters) {
        String[] requiredInformation = new String[] {
            (String) parameters.get(AutConfigConstants.MONITORING_AGENT_ID),
            (String) parameters.get(AutConfigConstants.AUT_ID),
            (String) parameters.get(MonitoringConstants.BUNDLE_ID),
            (String) parameters.get(MonitoringConstants.AGENT_CLASS) };

        for (int i = 0; i < requiredInformation.length; i++) {
            if (StringUtils.isEmpty(requiredInformation[i])) {
                return false;
            }
        }
        return true;
    }
}

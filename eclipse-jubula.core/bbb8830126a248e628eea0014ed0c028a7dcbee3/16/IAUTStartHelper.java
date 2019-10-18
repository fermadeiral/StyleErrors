/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent.common.utils;

import java.io.File;
import java.util.Map;

import org.eclipse.jubula.autagent.common.monitoring.IMonitoring;

/**
 * @author BREDEX GmbH
 *
 */
public interface IAUTStartHelper {
    /**
     * @return the AUTAgent or jar installation directory
     */
    public File getInstallationDirectory();

    /**
     * This method will load the class which implements the {@link IMonitoring} 
     * interface, and will invoke the "getAgent" method. 
     * @param parameters The AutConfigMap
     * @return agentString The agent String like -javaagent:myagent.jar
     * or null if the monitoring agent String couldn't be generated
     */
    public String getMonitoringAgent(Map<String, String> parameters);

    /**
     * 
     * @param bundleId The ID of the bundle to search for classpath entries.
     * @return classpath entries contained within the bundle with the given ID.
     *         If
     *         <ul> 
     *           <li>the bundle cannot be resolved to a file, or</li> 
     *           <li>the bundle is not a JAR file and the bundle's directory contains no JAR files</li>
     *         </ul>
     *         an empty array will be returned.
     */
    public String[] getClasspathEntriesForBundleId(String bundleId);

    /**
     * @param rcBundleID the Bundle ID to search for the Fragments
     * @return a map of Path to name
     */
    public Map<String, String> getFragmentPathforBundleID(String rcBundleID);
}

/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.launch.rcp;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jubula.launch.AutLaunchUtils;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.AutEnvironmentConstants;
import org.eclipse.pde.launching.EclipseApplicationLaunchConfiguration;

/**
 * Launch delegate for starting Eclipse RCP AUTs.
 * 
 * @author BREDEX GmbH
 * @created 18.07.2011
 */
public class RcpAutLaunchConfigurationDelegate 
        extends EclipseApplicationLaunchConfiguration {

    @Override
    public void launch(ILaunchConfiguration configuration, String mode,
            ILaunch launch, IProgressMonitor monitor) throws CoreException {

        InetSocketAddress agentAddress = 
            AutLaunchUtils.verifyConnectedAgentAddress();
        String autId = AutLaunchUtils.getAutId(configuration);
        
        ILaunchConfigurationWorkingCopy workingCopy = 
            configuration.getWorkingCopy();
        
        Map<String, String> environmentVariables = workingCopy.getAttribute(
                ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, 
                new HashMap<String, String>());

        environmentVariables.put(
                AutConfigConstants.AUT_AGENT_HOST, agentAddress.getHostName());
        environmentVariables.put(
                AutConfigConstants.AUT_AGENT_PORT, 
                Integer.toString(agentAddress.getPort()));
        environmentVariables.put(
                AutConfigConstants.AUT_NAME, autId);
        environmentVariables.put(
            AutConfigConstants.KEYBOARD_LAYOUT,
                configuration.getAttribute(
                        RcpAutLaunchConfigurationConstants.KEYBOARD_LAYOUT_KEY, 
                        Locale.getDefault().toString()));
        environmentVariables.put(
                AutEnvironmentConstants.GENERATE_COMPONENT_NAMES,
                Boolean.valueOf(configuration.getAttribute(
                        AutEnvironmentConstants.GENERATE_COMPONENT_NAMES,
                        Boolean.TRUE)).toString());
                
        
        workingCopy.setAttribute(
                ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, 
                environmentVariables);
        
        super.launch(workingCopy, ILaunchManager.DEBUG_MODE, launch, monitor);
    }
    
}

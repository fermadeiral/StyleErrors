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
package org.eclipse.jubula.launch.java;

import java.net.InetSocketAddress;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jubula.autagent.commands.IStartAut;
import org.eclipse.jubula.autagent.commands.StartSwingAutServerCommand;
import org.eclipse.jubula.launch.AutLaunchUtils;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * Launch delegate for starting Java / Swing AUTs.
 * 
 * @author BREDEX GmbH
 * @created 13.07.2011
 */
public class SwingAutLaunchConfigurationDelegate extends JavaLaunchDelegate {

    @Override
    public void launch(ILaunchConfiguration configuration, String mode,
            ILaunch launch, IProgressMonitor monitor) throws CoreException {
        
        String autMainType = verifyMainTypeName(configuration);
        String autArgs = getProgramArguments(configuration);
        String autId = AutLaunchUtils.getAutId(configuration);
        
        ILaunchConfigurationWorkingCopy workingCopy = 
            configuration.getWorkingCopy();
        workingCopy.setAttribute(
                IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, 
                CommandConstants.AUT_SERVER_LAUNCHER);
        

        InetSocketAddress agentAddr = 
            AutLaunchUtils.verifyConnectedAgentAddress();

        String [] args = {
            Integer.toString(agentAddr.getPort()), autMainType, 
            StringUtils.join(
                    new StartSwingAutServerCommand().getLaunchClasspath(), 
                    IStartAut.PATH_SEPARATOR), 
            CommandConstants.AUT_SWING_SERVER, agentAddr.getHostName(),
            Integer.toString(agentAddr.getPort()), autId, 
            CommandConstants.RC_COMMON_AGENT_INACTIVE, autArgs
        };
        
        workingCopy.setAttribute(
                IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, 
                StringUtils.join(args, " ")); //$NON-NLS-1$
        
        super.launch(workingCopy, ILaunchManager.DEBUG_MODE, launch, monitor);
    }

    @Override
    public String[] getClasspath(ILaunchConfiguration configuration)
        throws CoreException {
        
        String[] rcClasspath = 
            new StartSwingAutServerCommand().getLaunchClasspath();
        String[] autClasspath = super.getClasspath(configuration);
        String[] combinedClasspath = 
            new String [rcClasspath.length + autClasspath.length];
        for (int i = 0; i < autClasspath.length; i++) {
            combinedClasspath[i] = autClasspath[i];
        }
        
        for (int i = 0; i < rcClasspath.length; i++) {
            combinedClasspath[i + autClasspath.length] = rcClasspath[i];
        }

        return combinedClasspath;
    }

}

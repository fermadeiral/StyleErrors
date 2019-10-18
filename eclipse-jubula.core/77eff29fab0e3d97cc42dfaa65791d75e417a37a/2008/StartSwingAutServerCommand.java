/*******************************************************************************
 * Copyright (c) 2004, 2010, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.utils.MonitoringUtil;
import org.osgi.framework.Bundle;


/**
 * @author BREDEX GmbH
 * @created Jul 6, 2007
 */
public class StartSwingAutServerCommand extends AbstractStartJavaAutServer {
    /** the classpath of the AUT Server */
    private String m_autServerClasspath = "AutServerClasspath"; //$NON-NLS-1$
    
    /**
     * {@inheritDoc}
     */
    protected String[] createCmdArray(String baseCmd,
        Map<String, String> parameters) {
        List<String> cmds = new Vector<String>();
        cmds.add(baseCmd);
        
        StringBuffer autServerClasspath = new StringBuffer();
        createServerClasspath(autServerClasspath);
        
        List<String> autAgentArgs = new ArrayList<String>();
        autAgentArgs.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_AGENT_HOST)));
        autAgentArgs.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_AGENT_PORT)));
        autAgentArgs.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_NAME)));
        
        if (!isRunningFromExecutable(parameters)) {
            createAutServerLauncherClasspath(
                    cmds, autServerClasspath, parameters);
            createAutServerClasspath(autServerClasspath, cmds, parameters);
            cmds.addAll(autAgentArgs);
            // information for AUT server that agent is not used
            cmds.add(CommandConstants.RC_COMMON_AGENT_INACTIVE);
        } else { 
            String serverBasePath = createServerBasePath(); 
            autServerClasspath.append(PATH_SEPARATOR)
                .append(serverBasePath).append(PATH_SEPARATOR)
                .append(getRcBundleClassPath());
            m_autServerClasspath = autServerClasspath.toString();
                       
        }
        cmds.addAll(createAutArguments(parameters));
        return cmds.toArray(new String[cmds.size()]);
    }

    @Override
    protected String[] createEnvArray(Map<String, String> parameters, 
        boolean isAgentSet) {
        
        if (isRunningFromExecutable(parameters) 
                || MonitoringUtil.shouldAndCanRunWithMonitoring(parameters)) {
            setEnv(parameters, m_autServerClasspath);
            boolean agentActive = true;
            return super.createEnvArray(parameters, agentActive);
        }       
          
        return super.createEnvArray(parameters, isAgentSet);
    }
    
    /**
     * 
     * @return the class path corresponding to the receiver's RC bundle. 
     */ 
    protected String getRcBundleClassPath() {
        Bundle rcBundle = AbstractStartToolkitAut
                .getBundleForID(getRcBundleId());
        List<String> classList = AbstractStartToolkitAut
                .getPathforBundle(rcBundle);
        return AbstractStartToolkitAut.createClassPath(classList
                .toArray(new String[classList.size()]));
    }
    
    /**
     * 
     * @return the ID of the receiver's RC bundle.
     */
    public String getRcBundleId() {
        return CommandConstants.RC_SWING_BUNDLE_ID;
    }
    
    /**
     * Gets the absolute path of the RCAgent.jar file.
     * @return the absolute path
     */
    protected String getAbsoluteAgentJarPath() {
        return AbstractStartToolkitAut.getClasspathForBundleId(
                CommandConstants.RC_COMMON_AGENT_BUNDLE_ID);
    }
    
    /**
     * {@inheritDoc}
     */
    protected String getServerClassName() {
        return CommandConstants.AUT_SWING_SERVER;
    }
    
}

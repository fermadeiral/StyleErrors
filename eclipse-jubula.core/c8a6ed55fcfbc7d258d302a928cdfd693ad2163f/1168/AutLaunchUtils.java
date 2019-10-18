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
package org.eclipse.jubula.launch;

import java.net.InetSocketAddress;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jubula.client.autagent.handlers.ConnectToEmbeddedAutAgentHandler;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.launch.i18n.Messages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for starting AUTs using Launch Configurations.
 * 
 * @author BREDEX GmbH
 * @created 19.07.2011
 */
public class AutLaunchUtils {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(AutLaunchUtils.class);

    /**
     * Private constructor for utility class.
     */
    private AutLaunchUtils() {
        // Nothing to initialize
    }
    
    /**
     * Checks that there is an active connection to an AUT Agent. If not,
     * attempts to connect to the embedded AUT Agent (starting it first, 
     * if necessary). 
     * 
     * @return the address for the currently (at end of method execution) 
     *         connected AUT Agent. Guaranteed not to be <code>null</code>.
     * @throws CoreException if there is no connection to an AUT Agent and no
     *                       connection could be established.
     */
    public static InetSocketAddress verifyConnectedAgentAddress() 
        throws CoreException {
        
        InetSocketAddress addr = getConnectedAgentAddress();
        
        if (addr != null) {
            return addr;
        }

        LOG.info("Not connected to an AUT Agent. Connecting to embedded AUT Agent."); //$NON-NLS-1$
        IHandlerService handlerServce = PlatformUI.getWorkbench().getService(
                IHandlerService.class);
        try {
            handlerServce.executeCommand(
                ConnectToEmbeddedAutAgentHandler
                    .CONNECT_TO_EMBEDDED_AGENT_CMD_ID, null);
        } catch (CommandException e) {
            LOG.error("Error occurred while trying to connect to embedded AUT Agent.", e); //$NON-NLS-1$
        }
        
        Job[] connectToAgentJobs = Job.getJobManager().find(
                AutAgentConnection.CONNECT_TO_AGENT_JOB_FAMILY_ID);
        for (Job connectJob : connectToAgentJobs) {
            try {
                connectJob.join();
            } catch (InterruptedException ie) {
                LOG.warn("Interrupt occurred while waiting for connection to AUT Agent.", ie); //$NON-NLS-1$
                Thread.currentThread().interrupt();
            }
        }

        addr = getConnectedAgentAddress();
        
        if (addr == null) {
            throw new CoreException(new Status(
                    IStatus.ERROR, Activator.PLUGIN_ID, 
                    Messages.LaunchAutError_NoAgentConnection));
        }
        
        return addr;
    }

    /**
     * 
     * @return the address for the currently connected AUT Agent, or 
     *         <code>null</code> if there is no connection to an AUT Agent.
     */
    private static InetSocketAddress getConnectedAgentAddress() {
        try {
            Communicator agentCommunicator = 
                AutAgentConnection.getInstance().getCommunicator();
            if (agentCommunicator.getConnection() != null) {
                return new InetSocketAddress(
                        agentCommunicator.getHostName(), 
                        agentCommunicator.getPort());
            }
        } catch (ConnectionException e) {
            // No connection exists. We can safely ignore this, fall through,
            // and return null to indicate that there is no active connection
            // to an AUT Agent.
        }
        
        return null;
    }

    /**
     * 
     * @param configuration The Launch Configuration to examine.
     * @return the AUT ID defined in the given Launch Configuration, or the 
     *         name of the Launch Configuration if no AUT ID is defined or if 
     *         an error occurs.
     */
    public static String getAutId(ILaunchConfiguration configuration) {

        String autId = configuration.getName();
        try {
            String definedAutId = configuration.getAttribute(
                    AutLaunchConfigurationConstants.AUT_ID_KEY, 
                    StringUtils.EMPTY);
            if (!StringUtils.isEmpty(definedAutId)) {
                autId = definedAutId;
            }
        } catch (CoreException ce) {
            LOG.error(Messages.GetAutIdFromLaunchConfigError, ce);
        }
        
        return autId;
    }
}

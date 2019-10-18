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
package org.eclipse.jubula.client.ui.rcp.controllers.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent;
import org.eclipse.jubula.client.core.agent.IAutRegistrationListener;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionContributor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.osgi.util.NLS;


/**
 * Job responsible for starting an AUT.
 *
 * @author BREDEX GmbH
 * @created Feb 11, 2010
 */
public class StartAutJob extends Job {

    /** the AUT to start */
    private IAUTMainPO m_aut;
    
    /** the AUT Configuration to use for startup */
    private IAUTConfigPO m_autConfig;
    
    /** flag to indicate whether the AUT has been successfully started */
    private boolean m_isAutStarted;

    /**
     * Constructor
     * 
     * @param aut The AUT to start.
     * @param autConfig The AUT Configuration to use for startup.
     */
    public StartAutJob(final IAUTMainPO aut, final IAUTConfigPO autConfig) {
        super(NLS.bind(Messages.StartAutJobJobName,
                    autConfig.getConfigMap().get(AutConfigConstants.AUT_ID)));
        m_aut = aut;
        m_autConfig = autConfig;
    }
    
    /**
     * {@inheritDoc}
     */
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(null, IProgressMonitor.UNKNOWN);
        IAutRegistrationListener l = new IAutRegistrationListener() {
            public void handleAutRegistration(AutRegistrationEvent event) {
                if (event.getAutId().getExecutableName().equals(
                        m_autConfig.getConfigMap().get(
                                AutConfigConstants.AUT_ID))) {
                    m_isAutStarted = true;
                }
            }
        };
        AutAgentRegistration.getInstance().addListener(l);
        try {
            TestExecutionContributor.getInstance().getClientTest().startAut(
                    m_aut, m_autConfig);
        } catch (ToolkitPluginException e) {
            return new Status(IStatus.ERROR, Plugin.PLUGIN_ID, 
                    e.getLocalizedMessage());
        }
        
        while (!monitor.isCanceled() && !m_isAutStarted) {
            TimeUtil.delay(500);
        }

        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }
        
        monitor.done();
        return Status.OK_STATUS;
    }

}

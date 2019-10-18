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
package org.eclipse.jubula.autagent.commands;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.autagent.monitoring.IMonitoring;
import org.eclipse.jubula.autagent.monitoring.MonitoringDataStore;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.RestartAutMessage;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author BREDEX GmbH
 * @created Mar 25, 2010
 */
public class RestartAutCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(RestartAutCommand.class);
    
    /** the message */
    private RestartAutMessage m_message;
    /** DataStore */
    private MonitoringDataStore m_cm = MonitoringDataStore.getInstance();
    /**
     * {@inheritDoc}
     */
    public Message execute() {        
        AutIdentifier autId = m_message.getAutId();
        String monitoringId = m_cm.getConfigValue(
            autId.getExecutableName(), 
            AutConfigConstants.MONITORING_AGENT_ID);
        if (!StringUtils.isEmpty(monitoringId)) { 
            invokeMonitoringRestartMethod();
        }
        AutStarter.getInstance().getAgent()
                .restartAut(autId, m_message.getTimeout());
        return null;
    }
    /**
     * invokes the restart method specified in IMonitoring
     * 
     */
    private void invokeMonitoringRestartMethod() {        
                                  
        IMonitoring agent = m_cm.getMonitoringAgent(
                m_message.getAutId().getExecutableName());        
        agent.autRestartOccurred();
        
    }    
    /**
     * {@inheritDoc}
     */
    public RestartAutMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (RestartAutMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

}

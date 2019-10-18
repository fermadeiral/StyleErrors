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
package org.eclipse.jubula.autagent.monitoring;

import java.util.Map;

import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.GetMonitoringDataMessage;
import org.eclipse.jubula.communication.internal.message.GetMonitoringDataResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.objects.IMonitoringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 
 * This command is calling the "getMonitoringData" method specified by the
 * IMonitoring interface.
 * @author BREDEX GmbH
 * @created 13.09.2010
 */
public class GetMonitoringDataCommand implements ICommand {
   
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(GetMonitoringDataCommand.class);    
   
    /** message */
    private GetMonitoringDataMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        
        MonitoringDataStore cm = MonitoringDataStore.getInstance();   
        IMonitoring agent = cm.getMonitoringAgent(m_message.getAutId());
        cm.getConfigMap(m_message.getAutId()).put(
                AutConfigConstants.EXTERNAL_MONITORING_REPORT_PATH,
                m_message.getReportPath());
        Map<String, IMonitoringValue> monitoringValues = 
            agent.getMonitoringData();        
        String monitoringId = cm.getConfigValue(m_message.getAutId(), 
                AutConfigConstants.MONITORING_AGENT_ID);
     
        GetMonitoringDataResponseMessage message = 
            new GetMonitoringDataResponseMessage();
        message.setMonitoringId(monitoringId);
        message.setMonitoringValues(monitoringValues);        
       
        Communicator m = AutStarter.getInstance().getCommunicator();
        try {
            m.send(message);
        } catch (CommunicationException e) {
            LOG.error("failed to send " + message.getClass().getName(), e); //$NON-NLS-1$
           
        }
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        
        return m_message;
    }
    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (GetMonitoringDataMessage)message;

    }
    /**
     * {@inheritDoc}
     */
    public void timeout() {
        
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$

    }

}

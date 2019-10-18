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
package org.eclipse.jubula.autagent.common.monitoring;

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.ResetMonitoringDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This command is calling the "resetMonitoringData" method specified by the
 * IMonitoring interface.
 * @author BREDEX GmbH
 * @created 13.09.2010
 */
public class ResetMonitoringDataCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(ResetMonitoringDataCommand.class);    
   
    /** message */
    private ResetMonitoringDataMessage m_message;
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        
        MonitoringDataStore cm = MonitoringDataStore.getInstance();        
        IMonitoring agent = cm.getMonitoringAgent(m_message.getAutId());
        agent.resetMonitoringData();
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
        m_message = (ResetMonitoringDataMessage)message;
        
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
       
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}

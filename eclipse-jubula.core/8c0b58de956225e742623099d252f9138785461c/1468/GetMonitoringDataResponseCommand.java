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
package org.eclipse.jubula.client.core.commands;

import java.util.Map;

import org.eclipse.jubula.client.core.businessprocess.TestResultBP;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.GetMonitoringDataResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.IMonitoringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 07.09.2010
 */
public class GetMonitoringDataResponseCommand implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(GetMonitoringDataResponseCommand.class);

    /** the message */
    private GetMonitoringDataResponseMessage m_message;
    /**
     * {@inheritDoc}
     */
    public Message execute() {
               
        TestResult result = TestResultBP.getInstance().getResultTestModel();
        String monitoringId = m_message.getMonitoringId();        
        if (monitoringId == null) {
            result.setMonitoringId(MonitoringConstants.EMPTY_MONITORING_ID);
        } else {
            result.setMonitoringId(m_message.getMonitoringId());
        }           
        Map<String, IMonitoringValue> monitoringValue = 
            m_message.getMonitoringValues();      
        if (monitoringValue == null) {
            result.setMonitoringValues(null);
        } else {
            result.setMonitoringValues(m_message.getMonitoringValues());
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
        m_message = (GetMonitoringDataResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        
        log.error(this.getClass().getName() + StringConstants.DOT
                + Messages.TimeoutCalled);

    }

}

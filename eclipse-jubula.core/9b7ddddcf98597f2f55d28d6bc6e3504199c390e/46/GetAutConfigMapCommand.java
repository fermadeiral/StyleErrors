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
package org.eclipse.jubula.autagent.common.commands;

import java.util.Map;

import org.eclipse.jubula.autagent.common.monitoring.MonitoringDataStore;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.GetAutConfigMapMessage;
import org.eclipse.jubula.communication.internal.message.GetAutConfigMapResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 05.08.2010
 */
public class GetAutConfigMapCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(GetAutConfigMapCommand.class);

    /** The GetAutConfigMapMessage */
    private GetAutConfigMapMessage m_message;

    /** The autConfigMap to return */
    private Map<String, String> m_autConfigMap;

    /**
     * {@inheritDoc}
     */
    public Message execute() {

        MonitoringDataStore cm = MonitoringDataStore.getInstance();
        m_autConfigMap = cm.getConfigMap(m_message.getAutId());
        GetAutConfigMapResponseMessage message = 
                new GetAutConfigMapResponseMessage(
                        m_autConfigMap);
        return message;
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

        m_message = (GetAutConfigMapMessage) message;

    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {

        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$

    }

}

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

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.GetAutConfigMapResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This command is handling the incoming autConfigMap from the Agent.
 * 
 * @author BREDEX GmbH
 * @created 05.08.2010
 * */
public class GetAutConfigMapResponseCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(GetAutConfigMapResponseCommand.class);

    /** the message */
    private GetAutConfigMapResponseMessage m_message;

    /** The returned AutConfigMap from the agent */
    private Map<String, String> m_autConfigMap;

    /**
     * wheter a response has been received or not
     */
    private boolean m_receivedResponse = false;
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        setReceivedResponse(true);
        setAutConfigMap(m_message.getAutConfigMap());
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
        m_message = (GetAutConfigMapResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {

        LOG.error(this.getClass().getName() + StringConstants.DOT
                + Messages.TimeoutCalled);

    }

    /**
     * @param autConfigMap
     *            the autConfigMap to set
     */
    private void setAutConfigMap(Map autConfigMap) {
        m_autConfigMap = autConfigMap;
    }

    /**
     * @return the autConfigMap
     */
    public Map<String, String> getAutConfigMap() {
        return m_autConfigMap;
    }

    /**
     * @return the receivedResponse
     */
    public boolean hasReceivedResponse() {
        return m_receivedResponse;
    }

    /**
     * @param receivedResponse the receivedResponse to set
     */
    public void setReceivedResponse(boolean receivedResponse) {
        m_receivedResponse = receivedResponse;
    }
}

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
package org.eclipse.jubula.client.internal.commands;

import org.eclipse.jubula.client.core.agent.AutRegistrationEvent;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent.RegistrationStatus;
import org.eclipse.jubula.client.core.agent.IAutRegistrationListener;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.internal.APICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.RegisteredAutListMessage;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Updates a given listener with a collection of all currently running AUTs.
 *
 * @author BREDEX GmbH
 * @created Jan 26, 2010
 */
public class RegisteredAutListCommand implements APICommand {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(RegisteredAutListCommand.class);
    
    /** the listener that will be notified by this command */
    private IAutRegistrationListener m_listener;
    
    /** the message */
    private RegisteredAutListMessage m_message;
    
    /**
     * Constructor
     * 
     * @param l The listener that will be notified by this command.
     */
    public RegisteredAutListCommand(IAutRegistrationListener l) {
        m_listener = l;
    }
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        for (AutIdentifier id : m_message.getAutIds()) {
            m_listener.handleAutRegistration(
                    new AutRegistrationEvent(id, RegistrationStatus.Register));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public RegisteredAutListMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (RegisteredAutListMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + StringConstants.DOT
                + Messages.TimeoutCalled);
    }

}

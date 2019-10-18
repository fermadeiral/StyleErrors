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

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.DisconnectFromAutAgentResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Apr 19, 2010
 * 
 */
public class DisconnectFromAutAgentResponseCommand implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(DisconnectFromAutAgentResponseCommand.class);

    /**
     * <code>m_message</code>
     */
    private DisconnectFromAutAgentResponseMessage m_message;

    /**
     * constructor
     * 
     */
    public DisconnectFromAutAgentResponseCommand() {
    // empty
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        try {
            AutAgentConnection.getInstance().close();
        } catch (ConnectionException e) {
            if (log.isInfoEnabled()) {
                log.info(e.getLocalizedMessage(), e);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public DisconnectFromAutAgentResponseMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (DisconnectFromAutAgentResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + StringConstants.DOT 
            + Messages.TimeoutCalled);
    }
}

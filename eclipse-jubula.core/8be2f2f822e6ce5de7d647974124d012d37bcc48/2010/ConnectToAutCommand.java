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

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.autagent.agent.AutAgent;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.ConnectToAutMessage;
import org.eclipse.jubula.communication.internal.message.ConnectToAutResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Informs a specified AUT that it should initiate a connection.
 *
 * @author BREDEX GmbH
 * @created Feb 12, 2010
 */
public class ConnectToAutCommand implements ICommand {

    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(StartAUTServerCommand.class);

    /** the message for this command */
    private ConnectToAutMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        AutAgent agent = AutStarter.getInstance().getAgent();
        ConnectToAutResponseMessage response = 
            agent.sendConnectToClientMessage(m_message.getAutId(), 
                m_message.getClientHostName(), m_message.getClientPort());
        return response;
    }

    /**
     * {@inheritDoc}
     */
    public ConnectToAutMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        Validate.isTrue(message instanceof ConnectToAutMessage);
        m_message = (ConnectToAutMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}

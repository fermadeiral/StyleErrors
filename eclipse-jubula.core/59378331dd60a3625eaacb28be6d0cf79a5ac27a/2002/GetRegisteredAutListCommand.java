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

import java.util.Collection;

import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.autagent.agent.AutAgent;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.GetRegisteredAutListMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.RegisteredAutListMessage;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Retrieves the list of all currently running AUTs and sends the list as a 
 * response.
 *
 * @author BREDEX GmbH
 * @created Jan 26, 2010
 *
 */
public class GetRegisteredAutListCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(GetRegisteredAutListCommand.class);
    
    /** the message */
    private GetRegisteredAutListMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        AutAgent agent = AutStarter.getInstance().getAgent();
        Collection<AutIdentifier> auts = agent.getAuts();
        AutIdentifier [] autIdArray = 
            auts.toArray(new AutIdentifier [auts.size()]);
        RegisteredAutListMessage response = 
            new RegisteredAutListMessage(autIdArray);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    public GetRegisteredAutListMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (GetRegisteredAutListMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}

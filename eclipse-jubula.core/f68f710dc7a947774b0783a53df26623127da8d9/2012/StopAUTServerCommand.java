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

import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.autagent.agent.AutAgent;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.StopAUTServerMessage;
import org.eclipse.jubula.communication.internal.message.StopAUTServerStateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for stopping the AUTServer. The method execute() returns a
 * StopAUTServerStateMessage which contains a state. In case of not OK, the
 * message always contains a short description.
 *
 * @author BREDEX GmbH
 * @created 18.12.2007
 *
 */
public class StopAUTServerCommand implements ICommand {

    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(StopAUTServerCommand.class);

    /** the data */
    private StopAUTServerMessage m_message;

    /**
     * empty default constructor
     */
    public StopAUTServerCommand() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public StopAUTServerMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        try {
            m_message = (StopAUTServerMessage)message;
        } catch (ClassCastException cce) {
            if (log.isErrorEnabled()) {
                log.error("Cannot convert from " //$NON-NLS-1$
                        + message.getClass().toString() + " to " //$NON-NLS-1$
                        + m_message.getClass().toString(), cce);
            }
            throw cce;
        }
    }

    /**
     * The method stops the AUT server.
     *
     * @return a <code>StopAutServerStateMessage</code> which tells the
     *         originator that the AUT was stopped correctly.
     */
    public Message execute() {
        log.debug("execute() called"); //$NON-NLS-1$

        final AutAgent agent = AutStarter.getInstance().getAgent();
        if (agent != null) {
            agent.stopAut(m_message.getAutId(), 0);
            return new StopAUTServerStateMessage();
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called when it shouldn't (no response)"); //$NON-NLS-1$
    }
}
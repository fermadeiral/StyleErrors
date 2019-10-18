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

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.DisconnectFromAutAgentMessage;
import org.eclipse.jubula.communication.internal.message.DisconnectFromAutAgentResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created May 3, 2010
 * 
 */
public class DisconnectFromAutAgentCommand implements ICommand {

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
            DisconnectFromAutAgentCommand.class);

    /** the data */
    private DisconnectFromAutAgentMessage m_message;

    /**
     * empty default constructor
     */
    public DisconnectFromAutAgentCommand() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public DisconnectFromAutAgentMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        try {
            m_message = (DisconnectFromAutAgentMessage)message;
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
        return new DisconnectFromAutAgentResponseMessage();
    }


    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called when it shouldn't (no response)"); //$NON-NLS-1$
    }
}
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
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.StopAUTServerStateMessage;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 18.12.2007
 * 
 */
public class StopAUTServerStateCommand implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory
        .getLogger(StopAUTServerStateCommand.class);

    /** the message */
    private StopAUTServerStateMessage m_message;

    /** whether a timeout has occurred */
    private boolean m_isTimeout = false;

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
        m_message = (StopAUTServerStateMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        setMessage(new StopAUTServerStateMessage());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        m_isTimeout = true;
        log.error(this.getClass().getName() + StringConstants.DOT 
            + Messages.TimeoutCalled);
    }

    /**
     * @return <code>true</code> if this command has timed out. Otherwise 
     *         <code>false</code>.
     */
    public boolean isTimeout() {
        return m_isTimeout;
    }
}

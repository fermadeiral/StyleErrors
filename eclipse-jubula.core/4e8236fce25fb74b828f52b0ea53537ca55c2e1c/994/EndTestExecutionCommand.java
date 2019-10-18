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
package org.eclipse.jubula.rc.common.commands;

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.EndTestExecutionMessage;
import org.eclipse.jubula.communication.internal.message.EndTestExecutionResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Apr 19, 2010
 * 
 */
public class EndTestExecutionCommand implements ICommand {
    /** Logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(EndTestExecutionCommand.class);

    /** message */
    private EndTestExecutionMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        return new EndTestExecutionResponseMessage();
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(Message message) {
        m_message = (EndTestExecutionMessage)message;
    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return m_message;
    }
}

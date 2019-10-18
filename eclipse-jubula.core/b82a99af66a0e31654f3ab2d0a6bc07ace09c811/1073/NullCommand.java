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
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.NullMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Command that does nothing.
 *
 * @author BREDEX GmbH
 * @created 02.02.2006
 * 
 */
public class NullCommand implements ICommand {
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(NullCommand.class);
    /** the message */
    private NullMessage m_message;

    /**
     * {@inheritDoc}
     * @return
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     * @param message
     */
    public void setMessage(Message message) {
        m_message = (NullMessage)message;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public Message execute() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}
/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.internal.commands;

import java.util.concurrent.Exchanger;

import org.eclipse.jubula.client.internal.Synchronizer;
import org.eclipse.jubula.communication.internal.APICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.TakeScreenshotResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 */
public class TakeScreenshotResponseCommand implements APICommand {
    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(TakeScreenshotResponseCommand.class);

    /** the message */
    private TakeScreenshotResponseMessage m_message;

    /**
     * Constructor
     */
    public TakeScreenshotResponseCommand() {
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        Exchanger<Object> exchanger = Synchronizer.instance();
        try {
            exchanger.exchange(getMessage());
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public TakeScreenshotResponseMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (TakeScreenshotResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName());
    }
}
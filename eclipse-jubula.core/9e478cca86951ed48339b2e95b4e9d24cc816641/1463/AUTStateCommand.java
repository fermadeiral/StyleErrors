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

import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.internal.commands.AUTStartedCommand;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.AUTStateMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for the AUTStateMessage, which is send by the AUTServer
 * reporting the result of the AUTSwingStartMessage. <br>
 * 
 * The execute() - methods notifies the listeners, returns always null.
 * 
 * @author BREDEX GmbH
 * @created 12.08.2004
 * 
 */
public class AUTStateCommand implements ICommand {
    /**
     * timeout for requesting all components from AUT. 500000 is useful for RCP
     * AUTs
     */
    public static final int AUT_COMPONENT_RETRIEVAL_TIMEOUT = 500000;

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AUTStateCommand.class);
    
    /** the message */
    private AUTStateMessage m_message;
    
    /**
     * default constructor
     */
    public AUTStateCommand() {
        super();
    }

    /** {@inheritDoc} */
    public Message getMessage() {
        return m_message;
    }

    /** {@inheritDoc} */
    public void setMessage(Message message) {
        m_message = (AUTStateMessage)message;
    }
    
    /** {@inheritDoc} */
    public Message execute() {
        // listener processing the component info
        AUTStartedCommand callback = new AUTStartedCommand();
        callback.setStateMessage(m_message);
        try {
            AUTConnection.getInstance().setup(callback);
        } catch (CommunicationException bce) {
            log.error(Messages.CommunicationErrorSettingUpAUT, bce);
        }
        return null;
    }

    /** {@inheritDoc} */
    public void timeout() {
        log.info(Messages.TimeoutExpired);
    }
}

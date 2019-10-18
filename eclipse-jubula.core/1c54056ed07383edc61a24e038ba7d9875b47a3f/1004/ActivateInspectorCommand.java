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
import org.eclipse.jubula.communication.internal.message.ActivateInspectorMessage;
import org.eclipse.jubula.communication.internal.message.ActivateInspectorResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;


/**
 * The command object for ActivateInspectorMessage.
 * 
 * Activates the Inspector.
 *
 * @author BREDEX GmbH
 * @created Jun 10, 2009
 * 
 */
public class ActivateInspectorCommand implements ICommand {

    /** Logger */
    private static final AutServerLogger LOG =
        new AutServerLogger(ActivateInspectorCommand.class);

    /** message */
    private ActivateInspectorMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        try {
            AUTServer.getInstance().startInspector();
        } catch (Throwable t) {
            LOG.error("Error occurred while starting the Inspector.", t); //$NON-NLS-1$
        }
        return new ActivateInspectorResponseMessage();
    }
    
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
        m_message = (ActivateInspectorMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}

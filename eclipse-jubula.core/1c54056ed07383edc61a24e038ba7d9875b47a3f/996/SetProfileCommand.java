/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
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
import org.eclipse.jubula.communication.internal.message.SetProfileMessage;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Sets the Profile which will be used in the AUT Server
 * timeout() should never be called. <br>
 * @author BREDEX GmbH
 * @created 12.01.2016
 * 
 */
public final class SetProfileCommand 
    implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
        SetProfileCommand.class);
    /** the (empty) message */
    private SetProfileMessage m_message;
    
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
        m_message = (SetProfileMessage)message;
    }
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        log.info("Entering method " + getClass().getName() + ".execute()."); //$NON-NLS-1$ //$NON-NLS-2$
        // Register the supported components and their implementation classes.
        final AUTServerConfiguration serverConfig = AUTServerConfiguration
            .getInstance();
        serverConfig.setProfile(m_message.getProfile());
        return null;
    }

    /** 
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}
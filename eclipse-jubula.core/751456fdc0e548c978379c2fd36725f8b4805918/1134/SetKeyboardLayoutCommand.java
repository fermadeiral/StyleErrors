/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.commands;

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.SetKeyboardLayoutMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.swt.driver.RobotSwtImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets the keyboard layout used for the AUT.
 *
 * @author BREDEX GmbH
 * @created Aug 2, 2011
 */
public class SetKeyboardLayoutCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(SetKeyboardLayoutCommand.class);

    /** the message */
    private SetKeyboardLayoutMessage m_message;
    
    /**
     * 
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (SetKeyboardLayoutMessage)message;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Message execute() {
        ((RobotSwtImpl)AUTServer.getInstance().getRobot()).setKeyboardLayout(
                m_message.getKeyboardLayout());
        return null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

}

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
package org.eclipse.jubula.rc.common.commands;

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.GetKeyboardLayoutNameMessage;
import org.eclipse.jubula.communication.internal.message.GetKeyboardLayoutNameResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves the name of the keyboard layout used for the AUT.
 *
 * @author BREDEX GmbH
 * @created Aug 2, 2011
 */
public class GetKeyboardLayoutNameCommand implements ICommand {
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(GetKeyboardLayoutNameCommand.class);
    
    /** the message */
    private GetKeyboardLayoutNameMessage m_message;
    
    /** {@inheritDoc} */
    public Message getMessage() {
        return m_message;
    }

    /** {@inheritDoc} */
    public void setMessage(Message message) {
        m_message = (GetKeyboardLayoutNameMessage)message;
    }
    
    /** {@inheritDoc} */
    public Message execute() {
        return new GetKeyboardLayoutNameResponseMessage(
                EnvironmentUtils.getProcessEnvironment().getProperty(
                    AutConfigConstants.KEYBOARD_LAYOUT));
    }

    /** {@inheritDoc} */
    public void timeout() {
        LOG.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }
}
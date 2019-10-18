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

import org.eclipse.jubula.autagent.common.gui.ManualTestStepOptionPane;
import org.eclipse.jubula.autagent.common.gui.ManualTestStepOptionPane.ManualTestStepResult;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.DisplayManualTestStepMessage;
import org.eclipse.jubula.communication.internal.message.DisplayManualTestStepResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Aug 19, 2010
 * 
 */
public class DisplayManualTestStepCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(DisplayManualTestStepCommand.class);
    
    /** the message */
    private DisplayManualTestStepMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        ManualTestStepResult result = 
            ManualTestStepOptionPane.showDialog(
                getMessage().getActionToPerfom(), 
                getMessage().getExpectedBehavior(),
                getMessage().getTimeout());
        
        return new DisplayManualTestStepResponseMessage(
                result.getComment(), 
                result.isSuccessful());
    }

    /**
     * {@inheritDoc}
     */
    public DisplayManualTestStepMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (DisplayManualTestStepMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }
}

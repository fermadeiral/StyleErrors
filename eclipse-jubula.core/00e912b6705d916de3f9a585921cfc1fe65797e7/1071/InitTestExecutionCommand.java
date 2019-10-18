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
import org.eclipse.jubula.communication.internal.message.InitTestExecutionMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.RobotConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 07.02.2006
 */
public class InitTestExecutionCommand implements ICommand {
    /** Logger */
    private static final Logger LOG =
        LoggerFactory.getLogger(InitTestExecutionCommand.class);
    
    /** message */
    private InitTestExecutionMessage m_message;
    
    /** {@inheritDoc} */
    public Message execute() {
        final RobotConfiguration robotConfig = RobotConfiguration.getInstance();
        robotConfig.setDefaultActivationMethod(m_message
            .getDefaultActivationMethod());
        robotConfig.setErrorHighlighting(m_message.isErrorHighlighting());

        try {
            IRobot robot = AUTServer.getInstance().getRobot();
            robot.activateApplication(robotConfig.getDefaultActivationMethod());
        } catch (Exception exc) {
            LOG.error("error in activation of the AUT", exc); //$NON-NLS-1$
        }
        return null;
    }
    
    /** {@inheritDoc} */
    public Message getMessage() {
        return m_message;
    }
    
    /** {@inheritDoc} */
    public void setMessage(Message message) {
        m_message = (InitTestExecutionMessage)message;
    }
    
    /** {@inheritDoc} */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}
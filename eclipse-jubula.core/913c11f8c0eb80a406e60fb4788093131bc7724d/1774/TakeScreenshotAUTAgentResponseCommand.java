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
package org.eclipse.jubula.client.internal.commands;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.internal.APICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.TakeScreenshotAUTAgentResponseMessage;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Storing the received screenshot in the Test Result Node
 * @author Miklos Hartmann
 * @created Jun 24, 2016
 */
public class TakeScreenshotAUTAgentResponseCommand implements APICommand {

    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(TakeScreenshotAUTAgentResponseCommand.class);

    /** the message */
    private TakeScreenshotAUTAgentResponseMessage m_message;
    
    /** the Test Result Node */
    private TestResultNode m_testResultNode;
    
    /**
     * @param resultNode the Test Result Node to store the screenshot
     */
    public TakeScreenshotAUTAgentResponseCommand(TestResultNode resultNode) {
        m_testResultNode = resultNode;
    }
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        if (getMessage().getScreenshot() != null) {
            m_testResultNode.setScreenshot(getMessage().
                    getScreenshot().getData());
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public TakeScreenshotAUTAgentResponseMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (TakeScreenshotAUTAgentResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + StringConstants.DOT 
                + Messages.TimeoutCalled);
    }

}

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

import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent.State;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.EndTestExecutionResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Apr 19, 2010
 * 
 */
public class EndTestExecutionResponseCommand implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(EndTestExecutionResponseCommand.class);

    /**
     * <code>m_message</code>
     */
    private EndTestExecutionResponseMessage m_message;

    /**
     * constructor
     * 
     */
    public EndTestExecutionResponseCommand() {
    // empty
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        
        ClientTest.instance().fireTestExecutionChanged(
                new TestExecutionEvent(State.TEST_EXEC_FINISHED));
        TestExecution.getInstance().stopExecution();
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public EndTestExecutionResponseMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (EndTestExecutionResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + StringConstants.DOT
                + Messages.TimeoutCalled);
    }

}

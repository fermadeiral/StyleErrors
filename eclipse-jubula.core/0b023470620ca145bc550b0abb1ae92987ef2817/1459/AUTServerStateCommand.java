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
import org.eclipse.jubula.client.core.events.AUTEvent;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.AUTServerStateMessage;
import org.eclipse.jubula.communication.internal.message.AUTStartMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for AUTServerStateMessage, which is send by the
 * AUTServer at starting time. <br>
 * The execute() - method returns an AUTSwingStartMessage in case of the state is not
 * an error state. In case of an error, execute() returns null.
 * 
 * @author BREDEX GmbH
 * @created 06.08.2004
 * 
 */
public class AUTServerStateCommand implements ICommand {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(AUTServerStateCommand.class);
    
    /** the message */
    private AUTServerStateMessage m_message;
    
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
        m_message = (AUTServerStateMessage)message;
    }

    /**
     * logs the state, notifies the AUTEventlisteners in case of an error. <br>
     * In case of READY, the AUT is started by returning the message
     * AUTStartMessage.
     * The state "READY" is mapped to "AUT_RESTARTED" because we cannot differ
     * whether the AUT is started for the first time or if it was a restart.
     * So these two events are treated equally.
     * 
     * {@inheritDoc}
     */
    public Message execute() {
        int state = m_message.getState();
        
        switch (state) {
            case AUTServerStateMessage.READY: 
                log.info(Messages.AUTServerIsReady);
                // State "Ready" mapped to "AUT_RESTARTED" (see JavaDoc)
                ClientTest.instance().fireAUTStateChanged(
                    new AUTEvent(AUTEvent.AUT_RESTARTED));
                return new AUTStartMessage();
            case AUTServerStateMessage.AUT_NOT_FOUND:
                log.info(Messages.TheAUTCouldNotFound + StringConstants.DOT 
                    + StringConstants.SPACE + m_message.getDescription());
                ClientTest.instance().fireAUTStateChanged(
                    new AUTEvent(AUTEvent.AUT_NOT_FOUND));
                break;
            case AUTServerStateMessage.MAIN_METHOD_NOT_FOUND:
                log.info(Messages.TheMainMethodCouldNotLoaded
                        + StringConstants.DOT + StringConstants.SPACE);
                ClientTest.instance().fireAUTStateChanged(
                    new AUTEvent(AUTEvent.AUT_MAIN_NOT_FOUND));    
                break;
            case AUTServerStateMessage.EXIT_AUT_WRONG_CLASS_VERSION:
                log.info(Messages.AUTClassFormatNotSupportedByUsedJRE 
                        + StringConstants.DOT + StringConstants.SPACE);
                ClientTest.instance().fireAUTStateChanged(
                    new AUTEvent(AUTEvent.AUT_CLASS_VERSION_ERROR));    
                break;
            default:
                // nothing here
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

}

/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.communication.internal.message;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.communication.internal.ICommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Response command. Retrieves the errors and warnings from the response message
 * 
 * @author BREDEX GmbH
 * @created 21.7.2015
 */
public class AUTErrorsResponseCommand implements ICommand {
    
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(AUTErrorsResponseCommand.class);
    
    /** The message this command belongs to */
    private AUTErrorsResponseMessage m_message;
    
    /** Another obvious comment. I like Checkstyle */
    private List<String> m_errors = new ArrayList<String>();
    
    /** If you don not know what this is, stop reading */
    private List<String> m_warnings = new ArrayList<String>();

    /**
     * @return the message this command belongs to 
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * @param message the message which this command belongs to.
     */
    public void setMessage(Message message) {
        m_message = (AUTErrorsResponseMessage) message;

    }

    /**
     * Retrieves the errors and warnings from the response message.
     * 
     * @return null, because this command is the end of the errors and warnings
     *         communication between the client and the AUT.
     */
    public Message execute() {
        m_errors = m_message.getErrors();
        m_warnings = m_message.getWarnings();
        return null;
    }

    /**
     * This is called if a timeout occurred, in the communicator which is
     * awaiting this command in a response message
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

    /**
     * 
     * @return list of errors
     */
    public List<String> getErrors() {
        return m_errors;
    }
    
    /**
     * 
     * @return list of warnings
     */
    public List<String> getWarnings() {
        return m_warnings;
    }

}

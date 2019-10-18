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
package org.eclipse.jubula.tools.internal.exception;

import java.util.List;

/**
 * This exception should be thrown if there is a version conflict between the
 * client and AUT-Agent.
 *
 * @author BREDEX GmbH
 * @created 10.07.2006
 */
public class JBVersionException extends JBException {
    
    /** list with Strings of detailed error messages */
    private List m_errorMsgs;

    /**
     * @param message
     * @param id
     * {@inheritDoc}
     */
    public JBVersionException(String message, Integer id) {
        super(message, id);
    }
    
    /**
     * @param msg log message
     * @param id An ErrorMessage.ID
     * @param errorMsgs detailed error messages
     */
    public JBVersionException(String msg, Integer id, 
        List errorMsgs) {
        super(msg, id);
        m_errorMsgs = errorMsgs;
    }

    /**
     * @param message
     * @param cause
     * @param id
     * {@inheritDoc}
     */
    public JBVersionException(String message, Throwable cause, Integer id) {
        super(message, cause, id);
    }
    
    /**
     * @return Returns the errorMsgs.
     */
    public List getErrorMsgs() {
        return m_errorMsgs;
    }
    
    /**
     * @return error messages concatenated
     */
    public String getErrorMessagesString() {
        StringBuilder builder = new StringBuilder();
        for (Object message : m_errorMsgs) {
            builder.append(message);
            builder.append("\n");
        }
        return builder.toString();
    }

}

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
package org.eclipse.jubula.autagent.test;

import org.eclipse.jubula.communication.internal.message.Message;

/**
 * @author BREDEX GmbH
 * @created 03.08.2004
 * 
 */
public class ResponseMessage extends Message {
    /** the respponse */
    private String m_response;
    
    /**
     *  empty constructor 
     */
    public ResponseMessage() {
        super();
    }
    
    /**
     * 
     * @param response - the answer
     */
    public ResponseMessage(String response) {
        m_response = response;
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return "org.eclipse.jubula.autagent.test.ResponseCommand"; //$NON-NLS-1$
    }

    /**
     * @return Returns the response.
     */
    public String getResponse() {
        return m_response;
    }
    
    /**
     * @param response The response to set.
     */
    public void setResponse(String response) {
        m_response = response;
    }
}

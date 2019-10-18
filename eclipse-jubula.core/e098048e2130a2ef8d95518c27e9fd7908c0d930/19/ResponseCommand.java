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
package org.eclipse.jubula.autagent.common.test;

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;

/**
 * @author BREDEX GmbH
 * @created 03.08.2004
 * 
 */
public class ResponseCommand implements ICommand {

    /** the response  */
    private ResponseMessage m_responseMessage;
    
    /**
     * 
     */
    public ResponseCommand() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_responseMessage;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_responseMessage = (ResponseMessage) message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        System.out.print(System.currentTimeMillis() + ": "); //$NON-NLS-1$
        System.out.println(this.getClass().getName()
                + ".execute():" //$NON-NLS-1$
                + m_responseMessage.getResponse());
        // no answer
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        System.err.println(this.getClass().getName() 
                + ".timeout()"); //$NON-NLS-1$
    }

}

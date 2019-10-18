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

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;

/**
 * @author BREDEX GmbH
 * @created 03.08.2004
 * 
 */
public class AskCommand implements ICommand {

    /** question */
    private AskMessage m_question;
    
    /**
     * 
     */
    public AskCommand() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_question;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_question = (AskMessage) message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        System.out.print(System.currentTimeMillis() + ": "); //$NON-NLS-1$
        System.out.println(this.getClass().getName()
                + ".execute():" //$NON-NLS-1$
                + m_question.getQuestion());
        return new ResponseMessage("I'm fine."); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        System.err.println(this.getClass().getName() 
                + ".timeout()"); //$NON-NLS-1$
    }

}

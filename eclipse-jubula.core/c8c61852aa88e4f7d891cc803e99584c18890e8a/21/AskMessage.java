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

import org.eclipse.jubula.communication.internal.message.Message;

/**
 * @author BREDEX GmbH
 * @created 03.08.2004
 * 
 */
public class AskMessage extends Message {
    /** the question */
    private String m_question;
    
    /**
     *  empty constructor
     */
    public AskMessage() {
        super();
    }

    /**
     * 
     * @param question the data
     */
    public AskMessage(String question) {
        this();
        m_question = question;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return "org.eclipse.jubula.autagent.common.test.AskCommand"; //$NON-NLS-1$
    }

    /**
     * @return Returns the question.
     */
    public String getQuestion() {
        return m_question;
    }
    
    /**
     * @param question The question to set.
     */
    public void setQuestion(String question) {
        m_question = question;
    }
}

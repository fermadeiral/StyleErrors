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
package org.eclipse.jubula.communication.internal.message;

/**
 * This class is the abstract base class for all messages which could be send
 * through an Communicator. It contains all the data belonging to a message (and
 * it's command object).
 * <p>
 * A message is marked as an answer with the bindId property (set by the
 * communicator).
 * <p>
 * All properties to transmit must follow the bean introspection conventions.
 * <p>
 * Subclasses must implement: <br>
 * a public constructor with no parameter <br>
 * getCommandClass(): the fully qualified class name that's used by
 * createCommand(). <br>
 * 
 * @author BREDEX GmbH
 * @created 09.07.2004
 */
public abstract class Message {
    /** the identifier for this message */
    private MessageIdentifier m_messageId;

    /** the message this message is bounded to */
    private MessageIdentifier m_bindId;

    /** default constructor */
    public Message() {
        super();
    }

    /**
     * Implement this method in subclasses. The return value is used in
     * createCommand() to instantiate the appropriate command object.
     * 
     * @return the fully qualified name of the command object for this message
     */
    public abstract String getCommandClass();

    /** @return Returns the bindId. */
    public MessageIdentifier getBindId() {
        return m_bindId;
    }

    /**
     * @param bindId
     *            The bindId to set.
     */
    public void setBindId(MessageIdentifier bindId) {
        m_bindId = bindId;
    }

    /** @return Returns the messageId. */
    public MessageIdentifier getMessageId() {
        return m_messageId;
    }

    /**
     * @param messageId
     *            The messageId to set.
     */
    public void setMessageId(MessageIdentifier messageId) {
        m_messageId = messageId;
    }
}
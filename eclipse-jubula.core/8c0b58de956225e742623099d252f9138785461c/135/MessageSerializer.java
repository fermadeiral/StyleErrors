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
package org.eclipse.jubula.communication.internal.parser;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.MessageHeader;
import org.eclipse.jubula.tools.internal.exception.SerialisationException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.serialisation.IXmlSerializer;


/**
 * (De)serializes a message by delegating to the serializer which is created by
 * the {@link org.eclipse.jubula.tools.internal.serialisation.IXmlSerializer.Factory}.
 * 
 * {@inheritDoc}
 * 
 * @author BREDEX GmbH
 * @created 29.07.2005
 */
public class MessageSerializer {
    /**
     * The serializer instance.
     */
    private IXmlSerializer m_serializer;

    /**
     * The default constructor. It creates a serializer by calling
     * <code>IXmlSerializer.Factory.create()</code>.
     */
    public MessageSerializer() {
        m_serializer = IXmlSerializer.Factory.create();
    }

    /**
     * checks the parameter for <code>deserialize()</code>.
     * 
     * @param header -
     *            a message header
     * @param message -
     *            a message
     * @throws IllegalArgumentException
     *             see description of <code>deserialize()</code>
     */
    private void checkParseParameters(MessageHeader header, String message)
        throws IllegalArgumentException {
        Validate.notNull(header, "header must not be null"); //$NON-NLS-1$
        Validate.notNull(message, "message must not be null"); //$NON-NLS-1$
        Validate.notNull(header.getMessageClassName(),
            "given header contains no class information"); //$NON-NLS-1$ 
    }

    /**
     * Serializes a message to a XML string.
     * 
     * @param message
     *            The message to serialize
     * @return The serialized message as XML string
     * @throws SerialisationException
     *             If the serialization fails
     */
    public String serialize(Message message)
        throws SerialisationException {
        Validate.notNull(message, "message must not be null"); //$NON-NLS-1$
        return m_serializer.serialize(message, true);
    }

    /**
     * Deserializes a message represented by the passed string with XML content.
     * The message header is used to determine the message class name. So, this
     * property, the header itself and the message string must not be
     * <code>null</code>.
     * 
     * @param header
     *            The message header
     * @param message
     *            The XML string to deserialize
     * @return The deserialized message
     * @throws SerialisationException
     *             If the deserialization fails
     */
    public Message deserialize(MessageHeader header, String message)
        throws SerialisationException {

        checkParseParameters(header, message);
        try {
            Class messageClass = Class.forName(header.getMessageClassName());
            return (Message)m_serializer.deserialize(message, messageClass);
        } catch (ClassCastException cce) {
            // messageClass is not of type Message
            throw new SerialisationException(cce.getMessage(),
                MessageIDs.E_SERILIZATION_FAILED);
        } catch (ClassNotFoundException cnfe) {
            // message class not found
            throw new SerialisationException(cnfe.getMessage(),
                MessageIDs.E_SERILIZATION_FAILED);
        }
    }
}

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
import org.eclipse.jubula.communication.internal.message.MessageHeader;
import org.eclipse.jubula.tools.internal.exception.SerialisationException;
import org.eclipse.jubula.tools.internal.serialisation.IXmlSerializer;


/**
 * This class (de)serializes a message header by delegating to the serializer
 * which is created by the
 * {@link org.eclipse.jubula.tools.internal.serialisation.IXmlSerializer.Factory}.
 * 
 * {@inheritDoc}
 * 
 * @author BREDEX GmbH
 * @created 29.07.2005
 */
public class MessageHeaderSerializer {
    /**
     * The serializer instance.
     */
    private IXmlSerializer m_serializer;

    /**
     * The default constructor. It creates a serializer by calling
     * <code>IXmlSerializer.Factory.create()</code>.
     */
    public MessageHeaderSerializer() {
        m_serializer = IXmlSerializer.Factory.create();
    }

    /**
     * Serializes a message header to a string. A XML header is not included.
     * 
     * @param header
     *            The message header
     * @return The XML serialization as a string
     * @throws SerialisationException
     *             If the serialization fails
     */
    public String serialize(MessageHeader header)
        throws SerialisationException {
        Validate.notNull(header, "header must not be null"); //$NON-NLS-1$
        return m_serializer.serialize(header, false);
    }

    /**
     * Deserializes the given XML content and creates a new message header
     * object.
     * 
     * @param header
     *            The serialized message header as XML string
     * @return The created message header
     * @throws SerialisationException
     *             If the deserialization fails
     */
    public MessageHeader deserialize(String header)
        throws SerialisationException {
        Validate.notNull(header, "header must not be null"); //$NON-NLS-1$
        return (MessageHeader)m_serializer.deserialize(header,
            MessageHeader.class);
    }
}

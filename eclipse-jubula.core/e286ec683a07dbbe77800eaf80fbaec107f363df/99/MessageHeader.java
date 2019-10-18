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

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * The MessageHeader contains all header information for a message.
 * 
 * @author BREDEX GmbH
 * @created 13.07.2004
 */
public class MessageHeader {
    /** the first character of a message as an int */
    public static final int HEADER_START = '#';

    /** constant for a plain message */
    public static final int MESSAGE = 2;

    /** constant for a message which excepts an answer */
    public static final int REQUEST = 3;

    /** constant for a message which is a response */
    public static final int RESPONSE = 4;

    /** header version */
    private static final int HEADER_VERSION = 1;

    /**
     * the version of the header, used to compare with HEADER_VERSION to
     * determine version conflicts
     */
    private int m_version;

    /** the type of the message */
    private int m_messageType;

    /** the name of the class type of the message */
    private String m_messageClassName;

    /** message length in lines, will not serialized in the header */
    private int m_messageLength = -1;

    /**
     * empty default constructor. Do not call this directly, it's used by XML
     * serialization.
     */
    public MessageHeader() {
        super();
    }

    /**
     * Constructor with the type and the message as parameter. Use this
     * constructor for an outgoing message. The message is used to determine the
     * class name of the message.
     * @param messageType
     *            - the type of this message, see constants
     * @param message
     *            - the message to create a header for, must not be
     *            <code>null</code>
     * @throws IllegalArgumentException
     *             if an unknown type is used or if the message is
     *             <code>null</code>.
     */
    public MessageHeader(int messageType, Message message)
        throws IllegalArgumentException {

        if (messageType < MESSAGE || messageType > RESPONSE) {
            throw new IllegalArgumentException(
                    "invalid message type: " + messageType); //$NON-NLS-1$
        }
        Validate.notNull(message);

        m_version = HEADER_VERSION;
        m_messageType = messageType;
        m_messageClassName = message.getClass().getName();
    }

    /** @return The message type. */
    public int getMessageType() {
        return m_messageType;
    }

    /**
     * @param messageType
     *            The message type to set.
     */
    public void setMessageType(int messageType) {
        m_messageType = messageType;
    }

    /** @return The message length. */
    public int getMessageLength() {
        return m_messageLength;
    }

    /**
     * @param messageLength
     *            - the message length
     */
    public void setMessageLength(int messageLength) {
        m_messageLength = messageLength;
    }

    /** @return Returns the version. */
    public int getVersion() {
        return m_version;
    }

    /**
     * @param version The version to set.
     */
    public void setVersion(int version) {
        m_version = version;
    }

    /**
     * Validates the header version. {@inheritDoc}
     * @throws InvalidHeaderVersionException
     *             If the version member is not equal to
     *             <code>HEADER_VERSION</code>.
     */
    public void validateVersion() throws InvalidHeaderVersionException {
        if (m_version != HEADER_VERSION) {
            throw new InvalidHeaderVersionException("Invalid version " //$NON-NLS-1$
                    + m_version + ". Valid is: " + HEADER_VERSION, //$NON-NLS-1$
                    MessageIDs.E_INVALID_HEADER);

        }
    }

    /** @return Returns the class name of the message. */
    public String getMessageClassName() {
        return m_messageClassName;
    }

    /** @param messageClass The message classname to set. */
    public void setMessageClassName(String messageClass) {
        m_messageClassName = messageClass;
    }

    /**
     * overrides equals().
     * @param object - the object
     * @return true if these porperties are equals: version and messageType
     */
    public boolean equals(Object object) {
        if (object instanceof MessageHeader) {
            MessageHeader theOther = (MessageHeader) object;
            return new EqualsBuilder().append(m_version, theOther.m_version)
                    .append(m_messageType, theOther.m_messageType).isEquals();
        }
        return false;
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return new HashCodeBuilder().append(m_version).append(m_messageType)
                .toHashCode();
    }

    /** Exception to throw if the header version is wrong. {@inheritDoc} */
    public static class InvalidHeaderVersionException extends
            CommunicationException {
        /**
         * constructor
         * @param message The detailed message.
         * @param id An ErrorMessage.ID. {@inheritDoc}
         */
        public InvalidHeaderVersionException(String message, Integer id) {
            super(message, id);
        }
    }
}
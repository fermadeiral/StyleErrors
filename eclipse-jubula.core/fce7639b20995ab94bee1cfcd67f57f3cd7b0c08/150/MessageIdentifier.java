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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * This class contains the information to identify a message. These are: a
 * sequence number as String and a timestamp.
 * 
 * @author BREDEX GmbH
 * @created 19.07.2004
 * */
public class MessageIdentifier {
    /** sequence number */
    private String m_sequenceNumber;

    /** timestamp from sending host */
    private long m_timestamp;

    /** default constructor */
    public MessageIdentifier() {
        super();
        m_sequenceNumber = StringConstants.EMPTY;
        m_timestamp = 0;
    }

    /**
     * constructor
     * 
     * @param sequence
     *            - The sequence number
     */
    public MessageIdentifier(String sequence) {
        this();
        m_sequenceNumber = sequence;
        m_timestamp = System.currentTimeMillis();
    }

    /** @return Returns the sequenceNumber. */
    public String getSequenceNumber() {
        return m_sequenceNumber;
    }

    /**
     * @param sequenceNumber
     *            The sequenceNumber to set.
     */
    public void setSequenceNumber(String sequenceNumber) {
        m_sequenceNumber = sequenceNumber;
    }

    /** @return Returns the timestamp. */
    public long getTimestamp() {
        return m_timestamp;
    }

    /**
     * @param timestamp
     *            The timestamp to set.
     */
    public void setTimestamp(long timestamp) {
        m_timestamp = timestamp;
    }

    /**
     * overrides equals(): use all properties {@inheritDoc}
     */
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object);
    }

    /**
     * use sequencenumber and timestamp for calculating the hashcode
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(m_sequenceNumber)
                .append(m_timestamp).toHashCode();
    }
}
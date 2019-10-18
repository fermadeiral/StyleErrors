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
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/**
 * Message from Client to AUT Agent. The specified AUT should connect to the
 * provided address.
 * 
 * @author BREDEX GmbH
 * @created Feb 12, 2010
 */
public class ConnectToAutMessage extends Message {
    /** host name where the client is waiting */
    private String m_clientHostName;

    /** port number where the client is waiting */
    private int m_clientPort;

    /** Timeout for the AUTServer to wait for a confirmation for a sent event */
    private long m_eventConfirmTimeOut;

    /** ID of the Running AUT that should receive the connection request */
    private AutIdentifier m_autId;

    /**
     * @deprecated Default constructor for transportation layer. Don't use for
     *             normal programming.
     * 
     */
    public ConnectToAutMessage() {
        super();
    }

    /**
     * Constructs a complete message. No null values are allowed as parameters.
     * 
     * @param clientHostName
     *            Client host address, i.e. the address the AUT Server should
     *            connect to.
     * @param clientPort
     *            Client port, i.e.the port the AUT Server should connect to.
     * @param autId
     *            The ID of the AUT to which to connect.
     */
    public ConnectToAutMessage(String clientHostName, int clientPort,
            AutIdentifier autId) {
        setClientHostName(clientHostName);
        setClientPort(clientPort);
        setAutId(autId);
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.CONNECT_TO_AUT_COMMAND;
    }

    /**
     * @return the client host name.
     */
    public String getClientHostName() {
        return m_clientHostName;
    }

    /**
     * @param clientHostName
     *            The client host name to set.
     */
    public void setClientHostName(String clientHostName) {
        Validate.notEmpty(clientHostName);
        m_clientHostName = clientHostName;
    }

    /**
     * @return the client port.
     */
    public int getClientPort() {
        return m_clientPort;
    }

    /**
     * @param clientPort
     *            The client port to set.
     */
    public void setClientPort(int clientPort) {
        Validate.isTrue(clientPort > 0);
        m_clientPort = clientPort;
    }

    /**
     * Gets the timeout for the AUTServer to wait for a confirmation for a
     * sended event
     * 
     * @return Returns the eventConfirmTimeOut.
     */
    public long getEventConfirmTimeOut() {
        return m_eventConfirmTimeOut;
    }

    /**
     * Sets the timeout for the AUTServer to wait for a confirmation for a
     * sended event
     * 
     * @param eventConfirmTimeOut
     *            The eventConfirmTimeOut to set.
     */
    public void setEventConfirmTimeOut(long eventConfirmTimeOut) {
        m_eventConfirmTimeOut = eventConfirmTimeOut;
    }

    /**
     * @param autId
     *            the AUT ID to set.
     */
    public void setAutId(AutIdentifier autId) {
        m_autId = autId;
    }

    /**
     * @return the ID of the AUT to which to connect.
     */
    public AutIdentifier getAutId() {
        return m_autId;
    }
}
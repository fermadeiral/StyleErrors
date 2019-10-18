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

import java.util.Map;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * Message sent to an AUT Server containing information necessary to establish a
 * connection to a waiting client.
 * 
 * @author BREDEX GmbH
 * @created Mar 22, 2010
 * 
 */
public class ConnectToClientMessage extends Message {
    /** the host name at which the client is listening */
    private String m_clientHostName;

    /** the port number on which the client is listening */
    private int m_clientPort;
    
    /** Key: path to fragment jar. Value: fragment name*/
    private Map<String, String> m_fragments;

    /**
     * Default constructor for transportation layer. Don't use for normal
     * programming.
     * 
     * @deprecated
     */
    public ConnectToClientMessage() {
        super();
    }

    /**
     * Constructor
     * 
     * @param clientHostName
     *            The host name at which the client is listening.
     * @param clientPort
     *            The port number on which the client is listening.
     * @param fragments
     *            Map of fragments which should be loaded by the AUT     
     */
    public ConnectToClientMessage(String clientHostName, int clientPort,
            Map<String, String> fragments) {
        m_clientHostName = clientHostName;
        m_clientPort = clientPort;
        m_fragments = fragments;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.CONNECT_TO_CLIENT_COMMAND;
    }

    /** @return the host name at which the client is listening. */
    public String getClientHostName() {
        return m_clientHostName;
    }

    /** @return the port nubmer on which the client is listening. */
    public int getClientPort() {
        return m_clientPort;
    }

    /**
     * 
     * @return Key: path to fragment jar. Value: fragment name
     */
    public Map<String, String> getFragments() {
        return m_fragments;
    }
}
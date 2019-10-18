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
package org.eclipse.jubula.client.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class represents the connection to the AUT Agent. A call to
 * createInstance() instantiate this class. <br>
 * Call createInstance() before getting the instance with getInstance().
 * 
 * @author BREDEX GmbH
 * @created 22.07.2004
 */
public class AutAgentConnection extends BaseConnection {
    /**
     * Job Family ID. All Jobs dealing with connecting to the AUT Agent
     * will return <code>true</code> when their {@link Job#belongsTo(Object)}
     * method is called with this Object as the argument. 
     */
    public static final Object CONNECT_TO_AGENT_JOB_FAMILY_ID = new Object();
    
    /** the logger */
    private static Logger log = 
            LoggerFactory.getLogger(AutAgentConnection.class);
    
    /** the singleton instance */
    private static AutAgentConnection instance = null;

    /**
     * private constructor
     * @param inetAddress the host to connect to
     * @param port the port the remote host is listening to
     */
    private AutAgentConnection(InetAddress inetAddress, int port) {
        super();
        
        Communicator communicator = new Communicator(inetAddress, port, 
                this.getClass().getClassLoader()); 
        communicator.setIsServerSocketClosable(false);
        setCommunicator(communicator);
    }

    /**
     * creates the "singleton".
     * 
     * @param serverName
     *            The name of the server.
     * @param port
     *            The port number.
     * @throws ConnectionException
     *             if a connection to the given server could not made, e.g. the
     *             name from AUTConfigPO can not resolved to a host.
     */
    public static synchronized void createInstance(String serverName, 
        String port) throws ConnectionException {
        
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(serverName);
            instance = new AutAgentConnection(inetAddress, 
                new Integer(port).intValue());
        } catch (UnknownHostException uhe) {
            // log on info level, the configuration may be bad typed
            log.info(uhe.getLocalizedMessage(), uhe);
            throw new ConnectionException(uhe.getMessage(),
                MessageIDs.E_UNKNOWN_HOST);
        }
    }
    
    /**
     * Method to get the single instance of this class. <br>
     * !!! Call first createInstance() !!!
     * 
     * @throws ConnectionException
     *             when the instance was not created with createInstance()
     * @return the instance of this Singleton
     */
    public static synchronized AutAgentConnection getInstance() throws 
        ConnectionException {
        if (instance == null) {
            String message = "ServerConnection is not initialized"; //$NON-NLS-1$
            throw new ConnectionException(message, 
                MessageIDs.E_NO_SERVER_CONNECTION_INIT);
        }

        return instance;
    }
}
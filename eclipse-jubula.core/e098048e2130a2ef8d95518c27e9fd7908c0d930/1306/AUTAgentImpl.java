/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.internal.impl;

import java.net.ConnectException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.Remote;
import org.eclipse.jubula.client.exceptions.CommunicationException;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.BaseConnection.AlreadyConnectedException;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.internal.Synchronizer;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.internal.message.GetRegisteredAutListMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.StartAUTServerMessage;
import org.eclipse.jubula.communication.internal.message.StopAUTServerMessage;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.internal.constants.AUTStartResponse;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author BREDEX GmbH */
public class AUTAgentImpl implements AUTAgent {
    /** @author BREDEX GmbH */
    public static class ErrorListener implements ICommunicationErrorListener {
        /** the logger */
        private static Logger logger = LoggerFactory.getLogger(
            ErrorListener.class);
        
        /** the thread */
        private Thread m_thread;

        /**
         * Constructor
         * 
         * @param thread
         *            the thread to interrupt on communication problems
         */
        public ErrorListener(Thread thread) {
            m_thread = thread;
        }

        /** {@inheritDoc} */
        public void connectionGained(InetAddress inetAddress, int port) {
            // currently empty
        }

        /** {@inheritDoc} */
        public void shutDown() {
            logger.debug("shutdown() called. Interrupting thread: " //$NON-NLS-1$ 
                + m_thread.getName());
            m_thread.interrupt();
        }

        /** {@inheritDoc} */
        public void sendFailed(Message message) {
            logger.error("sendFailed() called. Interrupting thread: " //$NON-NLS-1$ 
                + m_thread.getName());
            m_thread.interrupt();
        }

        /** {@inheritDoc} */
        public void acceptingFailed(int port) {
            logger.error("acceptingFailed() called. Interrupting thread: " //$NON-NLS-1$ 
                + m_thread.getName());
            m_thread.interrupt();
        }
        
        /** {@inheritDoc} */
        public void connectingFailed(InetAddress inetAddress, int port) {
            logger.error("connectingFailed() called. Interrupting thread: " //$NON-NLS-1$ 
                + m_thread.getName());
            m_thread.interrupt();
        }
    }
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AUTAgentImpl.class);
    /** the hosts name */
    private String m_hostname;
    /** the port */
    private String m_port;
    /** the connection to the AUT-Agent */
    private AutAgentConnection m_agent;

    /**
     * @param hostname
     *            the hosts name
     * @param iPort
     *            the port
     */
    public AUTAgentImpl(String hostname, int iPort) {
        Validate.notEmpty(hostname, "The hostname must not be empty."); //$NON-NLS-1$
        final String port = String.valueOf(iPort);
        String portNumberMessage = NetUtil.isPortNumberValid(port);
        Validate.isTrue(portNumberMessage == null, portNumberMessage);
        
        m_hostname = hostname;
        m_port = port;
    }

    /** {@inheritDoc} */
    public void connect() throws CommunicationException {
        if (!isConnected()) {
            try {
                AutAgentConnection.createInstance(m_hostname, m_port);
                m_agent = AutAgentConnection.getInstance();
                m_agent.getCommunicator()
                    .addCommunicationErrorListener(new ErrorListener(
                        Thread.currentThread()));
                m_agent.run();
                if (!isConnected()) {
                    printlnConsoleError("Could not connect to AUT-Agent: " //$NON-NLS-1$
                                + m_hostname + ":" + m_port); //$NON-NLS-1$
                    throw new CommunicationException(
                        new ConnectException(
                            "Could not connect to AUT-Agent: " //$NON-NLS-1$
                                + m_hostname + ":" + m_port)); //$NON-NLS-1$
                }
            } catch (ConnectionException e) {
                printlnConsoleError("The connection to the AUTServer could not initialized."); //$NON-NLS-1$
                throw new CommunicationException(e);
            } catch (AlreadyConnectedException e) {
                printlnConsoleError("This connection is already connected"); //$NON-NLS-1$
                throw new CommunicationException(e);
            } catch (JBVersionException e) {
                printlnConsoleError("There is a version conflict between the client and " //$NON-NLS-1$
                        + "the AUT agent."); //$NON-NLS-1$
                log.error(e.getLocalizedMessage(), e);
                throw new CommunicationException(e);
            }
        } else {
            throw new IllegalStateException("AUT-Agent connection is already made"); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    public void disconnect() {
        if (isConnected()) {
            m_agent.close();
        } else {
            throw new IllegalStateException("AUT-Agent connection is already disconnected"); //$NON-NLS-1$
        }
        Thread.interrupted();
    }

    /** {@inheritDoc} */
    public boolean isConnected() {
        return m_agent != null ? m_agent.isConnected() : false;
    }

    /** {@inheritDoc} */
    public AUTIdentifier startAUT(
        @NonNull AUTConfiguration configuration)
        throws CommunicationException {
        Validate.notNull(configuration, "The configuration must not be null."); //$NON-NLS-1$
        checkConnected(this);

        Map<String, String> autConfigMap = new HashMap<String, String>(
                configuration.getLaunchInformation());

        // add relevant information for the AUT-Agent
        final Communicator communicator = m_agent.getCommunicator();
        autConfigMap.put(AutConfigConstants.AUT_AGENT_PORT,
            String.valueOf(communicator.getPort()));
        autConfigMap.put(AutConfigConstants.AUT_AGENT_HOST,
            communicator.getHostName());
        autConfigMap.put(AutConfigConstants.AUT_NAME,
            autConfigMap.get(AutConfigConstants.AUT_ID));

        String toolkitID = autConfigMap.get(ToolkitConstants.ATTR_TOOLKITID);
        StartAUTServerMessage startAUTMessage = new StartAUTServerMessage(
            autConfigMap, toolkitID);

        try {
            m_agent.send(startAUTMessage);
            Object genericStartResponse = Synchronizer.instance()
                .exchange(null);
            if (genericStartResponse instanceof Integer) {
                int startResponse = (Integer) genericStartResponse;
                return handleResponse(startResponse);
            }
            log.error("Unexpected start response code received: " //$NON-NLS-1$
                + String.valueOf(genericStartResponse));
        } catch (NotConnectedException e) {
            printlnConsoleError(e.getLocalizedMessage());
            throw new CommunicationException(e);
        } catch (org.eclipse.jubula.tools.internal.
                exception.CommunicationException e) {
            printlnConsoleError(e.getLocalizedMessage());
            log.error(e.getLocalizedMessage(), e);
            throw new CommunicationException(e);
        } catch (InterruptedException e) {
            printlnConsoleError(e.getLocalizedMessage());
            log.error(e.getLocalizedMessage(), e);
            throw new CommunicationException(e);
        }
        
        return null;
    }

    /**
     * @param startResponse
     *            the AUT start response
     * @return the AUT or <code>null<code> if problem during start
     */
    private AutIdentifier handleResponse(int startResponse) 
        throws CommunicationException {
        if (startResponse == AUTStartResponse.OK) {
            Object autIdentifier;
            try {
                autIdentifier = Synchronizer.instance().exchange(null);
                if (autIdentifier instanceof AutIdentifier) {
                    return (AutIdentifier) autIdentifier;
                }
                log.error("Unexpected AUT identifier received: " //$NON-NLS-1$
                    + String.valueOf(autIdentifier));
                if (autIdentifier instanceof Integer) {
                    int autStartResponseCode = (Integer) autIdentifier;
                    handleErrorResponse(autStartResponseCode);
                }
               
            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage(), e);
                throw new CommunicationException(e);
            }
        } else {
            handleErrorResponse(startResponse);
        }

        return null;
    }
    
    /**
     * Process the AUT start error codes
     * @param startResponse response code of the AUT start
     */
    private void handleErrorResponse(int startResponse) {
        switch (startResponse) {
            case AUTStartResponse.IO:
                printlnConsoleError("No Java found"); //$NON-NLS-1$
                break;
            case AUTStartResponse.DATA:
            case AUTStartResponse.EXECUTION:
            case AUTStartResponse.SECURITY:
            case AUTStartResponse.ERROR:
            case AUTStartResponse.COMMUNICATION:
                printlnConsoleError("AUTServer could not start."); //$NON-NLS-1$
                break;
            case AUTStartResponse.INVALID_ARGUMENTS:
                printlnConsoleError("AUTServer could not start, " //$NON-NLS-1$
                        + " because parameters are invalid."); //$NON-NLS-1$
                break;
            case AUTStartResponse.AUT_MAIN_NOT_DISTINCT_IN_JAR:
                printlnConsoleError(
                        "AUTServer could not start, because main is not distinct in jar."); //$NON-NLS-1$
                break;
            case AUTStartResponse.AUT_MAIN_NOT_FOUND_IN_JAR:
                printlnConsoleError(
                        "AUTServer could not start,"  //$NON-NLS-1$
                        + " because no main class found in the jar."); //$NON-NLS-1$
                break;
            case AUTStartResponse.NO_JAR_AS_CLASSPATH:
            case AUTStartResponse.SCANNING_JAR_FAILED:
                printlnConsoleError(
                        "AUTServer could not start, "  //$NON-NLS-1$
                        + " because the given jar is invalid."); //$NON-NLS-1$
                break;
            case AUTStartResponse.NO_SERVER_CLASS:
                printlnConsoleError(
                        "AUT server could not be instantiated"); //$NON-NLS-1$
                break;
            case AUTStartResponse.DOTNET_INSTALL_INVALID:
                printlnConsoleError(
                        "The .NET runtime is not properly installed"); //$NON-NLS-1$
                break;
            case AUTStartResponse.JDK_INVALID:
                printlnConsoleError(
                        "the JDK used by the AUT is probably older than 1.5," //$NON-NLS-1$
                                + " or javaagent is unknown "); //$NON-NLS-1$
                break;
            default:
                break;
        }
        
       
        
    }

    /** {@inheritDoc} */
    public void stopAUT(
        @NonNull AUTIdentifier aut) 
        throws CommunicationException {
        Validate.notNull(aut, "The AUT-Identifier must not be null."); //$NON-NLS-1$
        checkConnected(this);
        
        try {
            m_agent.send(new StopAUTServerMessage((AutIdentifier)aut));
        } catch (NotConnectedException e) {
            throw new CommunicationException(e);
        } catch (org.eclipse.jubula.tools.internal.
                exception.CommunicationException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new CommunicationException(e);
        }
    }

    /** {@inheritDoc} */
    @NonNull
    public List<AUTIdentifier> getAllRegisteredAUTIdentifier()
        throws CommunicationException {
        checkConnected(this);
        
        try {
            m_agent.send(new GetRegisteredAutListMessage());
            Object arrayOfAutIdentifier = Synchronizer.instance()
                .exchange(null);
            if (arrayOfAutIdentifier instanceof AutIdentifier[]) {
                final List<AUTIdentifier> unmodifiableList = Collections
                    .unmodifiableList(Arrays
                        .asList((AUTIdentifier[]) arrayOfAutIdentifier));
                if (unmodifiableList != null) {
                    return unmodifiableList;
                }
            }

            log.error("Unexpected AUT identifiers received: " //$NON-NLS-1$
                + String.valueOf(arrayOfAutIdentifier));
        } catch (NotConnectedException e) {
            throw new CommunicationException(e);
        } catch (org.eclipse.jubula.tools.internal.
                exception.CommunicationException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new CommunicationException(e);
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new CommunicationException(e);
        }

        return new ArrayList<AUTIdentifier>(0);
    }

    /** {@inheritDoc} */
    @NonNull public AUT getAUT (
        @NonNull AUTIdentifier autID, 
        @NonNull ToolkitInfo information) 
        throws CommunicationException {
        Validate.notNull(autID, "The AUT-Identifier must not be null."); //$NON-NLS-1$
        Validate.notNull(information, "The toolkit information must not be null."); //$NON-NLS-1$
        checkConnected(this);
        
        return new AUTImpl((AutIdentifier) autID, information);
    }
    
    /**
     * @param side
     *            the side to check the connection state for
     */
    static void checkConnected(Remote side) {
        if (!side.isConnected()) {
            throw new IllegalStateException("There is currently no connection established to the remote side - call connect() first!"); //$NON-NLS-1$
        }
    }
    

    /**
     * writes an output to console
     * @param text
     *      the message to log and println to sys.err
     */
    public static void printlnConsoleError(String text) {
        System.err.println("An error ocurred: " + StringConstants.NEWLINE //$NON-NLS-1$
                + StringConstants.TAB
                + text); 
    }
}
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

import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An abstract class for the connections. Wraps a Communicator. It's thread
 * safe.
 * 
 * @author BREDEX GmbH
 * @created 22.07.2004
 */
public abstract class BaseConnection {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(BaseConnection.class);
    
    /** the communicator to use */
    private Communicator m_communicator;

    /** flag if this Connection is connected */
    private boolean m_connected;

    /**
     * protected constructor
     * 
     * call in subclass super()
     */
    protected BaseConnection() {
        super();

        m_connected = false;
    }

    /**
     * @return Returns the communicator.
     */
    public synchronized Communicator getCommunicator() {
        return m_communicator;
    }
    
    /**
     * synchronized setter for communicator.
     * 
     * @param communicator -
     *            a new communicator, must not be null
     */
    protected synchronized void setCommunicator(Communicator communicator) {
        // check parameter
        if (communicator == null) {
            throw new IllegalArgumentException("Communicator must not be null"); //$NON-NLS-1$
        }
        m_communicator = communicator;
        // add an private listener for monitoring connection state
        m_communicator.addCommunicationErrorListener(
            new CommunicationListener());
    }
    
    /**
     * synchronized method for starting the communication.
     * 
     * @throws AlreadyConnectedException
     *             if this connection is already connected.
     * @throws JBVersionException
     *             in case of version error between client and remote side
     */
    public synchronized void run() throws AlreadyConnectedException, 
        JBVersionException {
        
        if (isConnected()) {
            log.error("run() called to an already connected connection"); //$NON-NLS-1$
            throw new AlreadyConnectedException(
                "This connection is already connected", //$NON-NLS-1$
                MessageIDs.E_CONNECTED_CONNECTION);
        }
        m_communicator.run();
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getName() 
                + ": Connection established on port: " //$NON-NLS-1$
                + getCommunicator().getPort() + " and local port: "  //$NON-NLS-1$
                + getCommunicator().getLocalPort());
        }
    }

    /**
     * Sends the given Message. Delegates to the Communicator
     * 
     * @param message
     *            the message to send, must not be null
     * @throws IllegalArgumentException
     *             if Message is null
     * @throws CommunicationException
     *             when the message could not send
     * @throws NotConnectedException
     *             when no connection is available either not yet set or lost
     * {@inheritDoc}
     */
    public synchronized void send(Message message)
        throws IllegalArgumentException,
        NotConnectedException, CommunicationException {
        if (!isConnected()) {
            if (log.isWarnEnabled()) {
                log.warn("send() called to an unconnected connection"); //$NON-NLS-1$
            }
            throw new NotConnectedException(
                    "This connection is not connected", //$NON-NLS-1$
                    MessageIDs.E_UNCONNECTED_CONNECTION);
        }

        m_communicator.send(message);
    }

    /**
     * Sends the given message as a request and expects an answer of type
     * command. If the answer arrive after the timeout (in seconds) the method
     * timeout() of command will be called.
     * 
     * Delegates to the Communicator.
     * 
     * @param message -
     *            the message to send, must not be null
     * @param response -
     *            the expected answer, must not be null
     * @param timeout -
     *            max milliseconds to wait for a response. Only values greater than
     *            zero are valid.
     * @throws NotConnectedException
     *             when no connection is available either not yet set or lost
     * @throws CommunicationException
     *             when the message could not send
     * {@inheritDoc}
     */
    public synchronized void request(Message message, ICommand response,
            int timeout) throws NotConnectedException,
            CommunicationException {
        if (!isConnected()) {
            log.error("request() called to an unconnected connection"); //$NON-NLS-1$
            throw new NotConnectedException(
                    "This connection is not connected", //$NON-NLS-1$
                    MessageIDs.E_UNCONNECTED_CONNECTION); 
        }

        m_communicator.request(message, response, timeout);
    }

    /**
     * Closes the connection, calls to an unconnected connection are ignored.
     */
    public void close() {
        if (log.isDebugEnabled()) {
            log.debug("close() called on " + this.getClass().getName() //$NON-NLS-1$
                + "on port: " + getCommunicator().getPort() + " and local port: " //$NON-NLS-1$ //$NON-NLS-2$
                + getCommunicator().getLocalPort()); 
        }
        if (!isConnected()) {
            if (log.isDebugEnabled()) {
                log.debug("close() called to an unconnected connection"); //$NON-NLS-1$
            }
        } else {
            m_communicator.close();
            setConnected(false);
        }
        if (log.isDebugEnabled()) {
            log.debug("leaving close() in: "  + this.getClass().getName()); //$NON-NLS-1$
        }
    }


    /**
     * @return Returns the connected.
     */
    public synchronized boolean isConnected() {
        return m_connected;
    }

    /**
     * @param connected The connected to set.
     */
    private synchronized void setConnected(boolean connected) {
        m_connected = connected;
        if (log.isDebugEnabled()) {
            log.debug("setConnected() set to " + connected); //$NON-NLS-1$
        }
    }

    /**
     * listener monitoring the connection state of communicator. Sets the
     * connected flag.
     * 
     * @author BREDEX GmbH
     * @created 12.08.2004
     */
    private class CommunicationListener implements ICommunicationErrorListener {
        /** {@inheritDoc} */
        public void acceptingFailed(int port) {
            setConnected(false);
        }
        
        /** {@inheritDoc} */
        public void connectingFailed(InetAddress inetAddress, int port) {
            setConnected(false);
        }
        
        /** {@inheritDoc} */
        public void connectionGained(InetAddress inetAddress, int port) {
            setConnected(true);
        }

        /** {@inheritDoc} */
        public void sendFailed(Message message) {
            // do nothing
        }

        /** {@inheritDoc} */
        public void shutDown() {
            setConnected(false);
        }
    }

    
    /**
     * Exception thrown when there is no connection to a server and a send() or
     * request are called.
     * 
     * @author BREDEX GmbH
     * @created 22.07.2004
     */
    public static class NotConnectedException 
        extends CommunicationException {

        /**
         * public constructor
         * 
         * @param message The detailed message.
         * @param id An ErrorMessage.ID.
         * {@inheritDoc}
         */
        public NotConnectedException(String message, Integer id) {
            super(message, id);
        }
    }

    /**
     * This exception will be thrown when an instance of a connection was
     * requested to connect to a server (or accepting connections), but it's
     * already connected.
     * 
     * @author BREDEX GmbH
     * @created 22.07.2004
     */
    public static class AlreadyConnectedException extends
            CommunicationException {
        /**
         * public constructor
         * 
         * @param message The detailed message.
         * @param id An ErrorMessage.ID.
         * {@inheritDoc}
         */
        public AlreadyConnectedException(String message, Integer id) {
            super(message, id);
        }
    }
}

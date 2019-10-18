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
package org.eclipse.jubula.communication.internal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.communication.internal.connection.Connection;
import org.eclipse.jubula.communication.internal.connection.ConnectionState;
import org.eclipse.jubula.communication.internal.connection.DefaultClientSocket;
import org.eclipse.jubula.communication.internal.connection.DefaultServerSocket;
import org.eclipse.jubula.communication.internal.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.internal.listener.IErrorHandler;
import org.eclipse.jubula.communication.internal.listener.IMessageHandler;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.MessageHeader;
import org.eclipse.jubula.communication.internal.message.MessageIdentifier;
import org.eclipse.jubula.communication.internal.parser.MessageSerializer;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.AssertException;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.exception.SerialisationException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.utils.IsAliveThread;
import org.slf4j.LoggerFactory;


/**
 * This class is the interface to the module communication. Responsibility:
 * <p>
 * <ul>
 * <li>create connections</li>
 * <li>sending/receiving messages of type 'Message'</li>
 * <li>identifying message as response of send messages with request()</li>
 * <li>creating corresponding command objects for received messages, calling
 * execute() for these command objects and sending back the response message.
 * <li>
 * </ul>
 * <p>
 * Instances of this class can act like a server or like a client. Use the
 * appropriate constructor. As a server, the instance will accept connections
 * without any timeout. The request to connect are not queued, so if a
 * connection indication arrives when the instance is already connected, the
 * connection is refused by the operating system.
 * <p>
 * How to use:
 * <ul>
 * <li>instantiate a communicator</li>
 * <li>add listeners</li>
 * <li>call run</li>
 * <li>send messages with send() or request()</li>
 * <li>to close the connection call close()</li>
 * See the package documentation for a detailed description.
 * 
 * @author BREDEX GmbH
 * @created 16.07.2004
 */
public class Communicator {
    /** timeout used as default value for establishing a connection in seconds */
    public static final int DEFAULT_CONNECTING_TIMEOUT = 20;

    /** timeout for so server socket */
    private static final int INFINITE = 0;

    /** timeout used as default value for request in seconds */
    private static final int DEFAULT_REQUEST_TIMEOUT = 10;

    /** constant to convert from seconds to milliseconds */
    private static final int THOUSAND = 1000;

    /** the logger instance */
    private static ConfigurableLogger log = new ConfigurableLogger(
            LoggerFactory.getLogger(Communicator.class));

    /** the port for connecting a server (this instance will act as a client) */
    private int m_port = 0;

    /**
     * the server to connect (this instance will act as a client), defaults to
     * null to distinguish whether this instance is a server or a client
     */
    private InetAddress m_inetAddress = null;
    
    /** class loader to get later the command Object for a message */
    private ClassLoader m_classLoader = null;

    /** the server socket (this instance will act as a server) */
    private DefaultServerSocket m_serverSocket = null;

    /** the local port, this instance uses, regardless which constructor was called */
    private int m_localPort;

    /** the connection to use */
    private Connection m_connection;

    /** listener listening to the connection for new message */
    private ConnectionListener m_connectionListener;

    /** listener listening to the connection for errors */
    private ErrorListener m_errorListener;
    
    /** a map for storing awaiting commands */
    private Map<MessageIdentifier, AwaitingCommand> m_awaitingCommands;

    /** Set with ICommunicationErrorListeners listening to this communicator */
    private LinkedHashSet<ICommunicationErrorListener> m_errorListeners;

    /** the connection manager, implementing the strategy for accepting connections */
    private ConnectionManager m_connectionManager;
    
    /** the exception handler for reading from the network, set to the used Connection */
    private IExceptionHandler m_exceptionHandler =
        new AbortingExceptionHandler();
    
    /** the parser converting from String to Message and vice versa */
    private MessageSerializer m_serializer;

    /** flag for accepting thread to continue / stop accepting connections */
    private boolean m_accepting = false;

    /** 
     * Mapping from client type to object responsible for initializing the 
     * connection. Connections for client types not contained within this 
     * mapping will be initialized in the default manner.
     *  
     * <code>String</code> => <code>IConnectionInitializer</code> 
     */
    private Map<String, IConnectionInitializer> m_responseToInitializer;
    
    /**
     * The commandFactory for this communicator
     */
    private CommandFactory m_commandFactory;

    /**
     * boolean if the server socket could be closed, default is
     * <code>true</code>. Set this variable only to <code>false</code> if the
     * communicator will be reused for other connections since than the
     * serverSocket would not be closed
     */
    private boolean m_isServerSocketClosable = true;
    /**
     * Constructor explicitly setting the commandFactory.
     * @param inetAddress IP of target
     * @param port Port of target
     * @param cl class loader
     * @param cf CommandFactory for the communicator
     */
    public Communicator(InetAddress inetAddress, int port, 
            ClassLoader cl, CommandFactory cf) {
        this(inetAddress, port, cl);
        m_commandFactory = cf;
    }
    
    /**
     * Use this constructor if the instance should act as client. The connection
     * will be established in run(). throws AssertException if parameters are null
     * @param inetAddress the inetAdress to connect
     * @param port the port the server listens
     * @param cl class loader to get the command object
     * @throws AssertException AssertException if parameters are null
     */
    public Communicator(InetAddress inetAddress, int port, 
            ClassLoader cl) throws AssertException {
        super();
        // check parameter
        Assert.verify(inetAddress != null, 
                "inetAddress must not be null"); //$NON-NLS-1$
        Assert.verify(port >= 0, 
                "port must not be negativ"); //$NON-NLS-1$
        Assert.verify(cl != null, 
            "no class loader for creation of command " + //$NON-NLS-1$
            "object available"); //$NON-NLS-1$
        
        // store inetAddress, port
        m_inetAddress = inetAddress; 
        m_port = port;
        m_classLoader = cl;

        init();
    } 
    
    /**
     * Use this constructor if the instance should act as a server. run() will start a thread accepting connections.
     * @param port the port to use, must not be negative, if port is zero, any
     *             free port will be used, query the opened port with getLocalPort()
     * @param cl class loader to get the command object
     * @throws AssertException if port is negative or factory is null.
     * @throws IOException if the given port can not used
     * @throws SecurityException if the security manager does not allow connections.
     */
    public Communicator(int port, ClassLoader cl) 
        throws IOException, SecurityException, AssertException {
        
        this(port, cl, null);
    }
    /**
     * Use this constructor if the instance should act as a server. run() will start a thread accepting connections.
     * 
     * @param port the port to use, must not be negative, if port is zero, any
     *             free port will be used, query the opened port with getLocalPort()
     * @param cl class loader to get the command object
     * @param responseToInitializer Mapping from client type to connection 
     *                              initializer. Connections initiated by client
     *                              types not contained within this mapping will
     *                              be initialized in the default manner. May
     *                              be <code>null</code>.
     * @throws AssertException if port is negative or factory is null.
     * @throws IOException if the given port can not used
     * @throws SecurityException if the security manager does not allow connections.
     */
    public Communicator(int port, ClassLoader cl, 
            Map<String, IConnectionInitializer> responseToInitializer) 
        throws IOException, SecurityException, AssertException {

        super();
        // check parameter
        Assert.verify(port >= 0, 
                "port must not be negativ"); //$NON-NLS-1$
        Assert.verify(cl != null, 
            "no class loader for creation of command " + //$NON-NLS-1$
            "object available"); //$NON-NLS-1$
        // create a server socket
        m_serverSocket = new DefaultServerSocket(port);
        m_serverSocket.setSoTimeout(INFINITE);

        // store the opened socket to LOCAL Port
        m_localPort = m_serverSocket.getLocalPort();
        m_classLoader = cl;

        m_responseToInitializer = new HashMap<String, IConnectionInitializer>();
        if (responseToInitializer != null) {
            m_responseToInitializer.putAll(responseToInitializer);
        }
        
        Validate.allElementsOfType(
                m_responseToInitializer.keySet(), String.class);
        Validate.allElementsOfType(m_responseToInitializer.values(), 
                IConnectionInitializer.class);

        init();
    }
        
    /**
     * private method for initialization, called from the constructors.
     */
    private void init() {
        m_serializer = new MessageSerializer();
        m_connection = null;
        setConnectionManager(new DefaultConnectionManager());
        // initialize map for awaiting commands
        m_awaitingCommands = new HashMap<MessageIdentifier, AwaitingCommand>();
        // using a LinkedHashSet, because LinkedHashSet supports
        // ordered iteration and also supports remove, see
        // removeErrorHandler AND fire*-methods
        m_errorListeners = new LinkedHashSet<ICommunicationErrorListener>();
        // create a connection listener for incoming commands
        m_connectionListener = new ConnectionListener();
        // create an error handler
        m_errorListener = new ErrorListener();
        // set default commandFactory
        m_commandFactory = new CommandFactory(m_classLoader);
    }

    /**
     * establish the connection, either connecting to a server or accepting
     * connections. This method will not block. If a connection could not made,
     * the listeners are notified with connectingFailed() and acceptingFailed
     * respectively.
     * 
     * @return the Thread responsible for accepting connections, or 
     *         <code>null</code> if the the receiver is acting as a client.
     * @throws SecurityException
     *             if the security manager does not allow connections.
     * @throws JBVersionException
     *             in case of version error between Client and AutStarter
     */
    public synchronized Thread run() throws SecurityException, 
        JBVersionException {
        
        Thread acceptingThread = null;
        if (m_serverSocket != null && !isAccepting()) {
            // it's a server that hasn't yet started accepting connections
            setAccepting(true);
            acceptingThread = new AcceptingThread();
            acceptingThread.setDaemon(true);
            acceptingThread.start();
        } else if (m_inetAddress != null) {
            // it's a client
            try {
                DefaultClientSocket socket = new DefaultClientSocket(
                    m_inetAddress, m_port,
                        DEFAULT_CONNECTING_TIMEOUT * THOUSAND); 
                if (socket.isConnectionEstablished()) {
                    setup(socket);
                } else {
                    log.info("connecting failed with server state: "  //$NON-NLS-1$
                            + String.valueOf(socket.getState())); 
                    fireConnectingFailed(m_inetAddress, m_port);
                }
            } catch (IllegalArgumentException iae) {
                log.debug(iae.getLocalizedMessage(), iae);
                fireConnectingFailed(m_inetAddress, m_port);
            } catch (IOException ioe) {
                log.debug(ioe.getLocalizedMessage(), ioe);
                fireConnectingFailed(m_inetAddress, m_port);
            } catch (SecurityException se) {
                log.debug(se.getLocalizedMessage(), se);
                fireConnectingFailed(m_inetAddress, m_port);
                throw se;
            } 
        }
        
        return acceptingThread;
    }

    /**
     * 
     * @param isServerSocketClosable
     *            the default is <code>true</code>, if you want to have a
     *            {@link Communicator} which server socket would not be closed
     *            after it was closed, set it to <code>false</code>
     */
    public void setIsServerSocketClosable(boolean isServerSocketClosable) {
        m_isServerSocketClosable = isServerSocketClosable;
    }
    /**
     * creates the appropriate command object for this message per reflection.
     * The message is set to the command.
     * @param msg message object
     * @throws UnknownCommandException -
     *             the exception thrown if the instantiation of command failed.
     * @return the created command
     */
    private ICommand createCommand(Message msg)
        throws UnknownCommandException {
        String commandClassName = msg.getCommandClass();
        ICommand result = m_commandFactory.createCommandObject(
                commandClassName);
        result.setMessage(msg);
        return result;
    }
    
    /**
     * registers the given listener
     * @param listener the listener, null objects are ignored
     */
    public void addCommunicationErrorListener(
        ICommunicationErrorListener listener) {
        if (listener != null) {
            synchronized (m_errorListeners) {
                m_errorListeners.add(listener);
            }
        }
    }

    /**
     * de-registers the given listener
     * @param listener the listener, null objects are ignored
     */
    public void removeCommunicationErrorListener(
        ICommunicationErrorListener listener) {
        if (listener != null) {
            synchronized (m_errorListeners) {
                m_errorListeners.remove(listener);
            }
        }
    }

    /**
     * @return Returns the connectionManager.
     */
    public synchronized ConnectionManager getConnectionManager() {
        return m_connectionManager;
    }
    
    /**
     * @param connectionManager The connectionManager to set.
     */
    public synchronized void setConnectionManager(
            ConnectionManager connectionManager) {
        m_connectionManager = connectionManager;
    }
    
    /**
     * @return Returns the exceptionHandler.
     */
    public synchronized IExceptionHandler getExceptionHandler() {
        return m_exceptionHandler;
    }
    
    /**
     * @param exceptionHandler The exceptionHandler to set.
     */
    public synchronized void setExceptionHandler(
            IExceptionHandler exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
    }
    /**
     * @return Returns the accepting.
     */
    private synchronized boolean isAccepting() {
        return m_accepting;
    }
    
    /**
     * @param accepting The accepting to set.
     */
    private synchronized void setAccepting(boolean accepting) {
        m_accepting = accepting;
    }
    
    /**
     * @return Returns the port this instance use.
     */
    public int getLocalPort() {
        return m_localPort;
    }

    /**
     * private method to check the state of this communicator 
     * @param methodName  the caller, for debugging purpose
     * @throws CommunicationException  if this communicator is not connected
     */
    private void checkConnectionState(String methodName)
        throws CommunicationException {
        if (m_connection == null) {
            log.debug("method " + methodName + //$NON-NLS-1$ 
                " called to an unconnected " + //$NON-NLS-1$
                "communicator"); //$NON-NLS-1$
            throw new CommunicationException(
                "Communicator not connected", //$NON-NLS-1$
                MessageIDs.E_COMMUNICATOR_CONNECTION);
        }
    }

    /**
     * send a message through this communicator. ICommand.execute will be called on the other site.
     * @param message  the message object, must not be null, otherwise an CommunicationException is thrown.
     * @throws CommunicationException
     *             if any error or exception occurs. A CommnunicationException
     *             is also thrown if this communicator is not connected, e.g.
     *             run() was not called, or exceptions at creation time were
     *             ignored
     */
    public void send(Message message) throws CommunicationException {
        checkConnectionState("send()"); //$NON-NLS-1$
        // check parameter
        if (message == null) {
            log.debug("method send() with null parameter called"); //$NON-NLS-1$
            throw new CommunicationException("no message to send", //$NON-NLS-1$
                MessageIDs.E_NO_MESSAGE_TO_SEND); 
        }
        try {
            message.setMessageId(new MessageIdentifier(m_connection
                .getNextSequenceNumber()));
            // create string message
            String messageToSend = m_serializer.serialize(message);
            // send message
            m_connection.send(new MessageHeader(MessageHeader.MESSAGE,
                message), messageToSend);
        } catch (SerialisationException se) {
            log.debug(se.getLocalizedMessage(), se);
            throw new CommunicationException(
                "could not send message:" //$NON-NLS-1$
                + se.getMessage(), se, MessageIDs.E_MESSAGE_NOT_SEND);
        } catch (IOException ioe) {
            log.debug(ioe.getLocalizedMessage(), ioe);
            throw new CommunicationException(
                "io error occurred during sending a message:" //$NON-NLS-1$
                    + ioe.getMessage(), ioe, MessageIDs.E_MESSAGE_SEND);
        } catch (IllegalArgumentException iae) {
            log.debug(iae.getLocalizedMessage(), iae);
            throw new CommunicationException(
                "message could not send", iae, MessageIDs.E_MESSAGE_NOT_SEND); //$NON-NLS-1$
        }
    }

    /**
     * Send a message through this communicator and expect a response of type
     * command. ICommand.request will be called at the other site. If a response
     * was received the corresponding data will be set to the command object via
     * setData(). If the response was received during the given timeout,
     * command.response will be called. Otherwise command.timeout will be
     * called. This method will not block.
     * 
     * @param message -
     *            the Message to send, must not be null otherwise an
     *            CommunicationException is thrown.
     * @param command -
     *            the expected command, must not be null or a
     *            CommunicationException is thrown. If the command arrives in
     *            good time, the method execute() of the given instance will be
     *            called. If the commands arrives to late timeout() will be
     *            called.
     * @param timeout -
     *            max milliseconds to wait for a response. Only values greater 
     *            than zero are valid. For values less or equals to zero the
     *            configured default timeout will be used. If the timeout
     *            expires, the method timeout() in command will be called.
     * @throws CommunicationException
     *             if any error/exception occurs. A CommnunicationException is
     *             also thrown if this communicator is not connected, e.g. run()
     *             was not called, or exceptions at creation time were ignored
     */
    public void request(Message message, ICommand command, int timeout)
        throws CommunicationException {
        try {
            requestImpl(message, command, timeout, false);
        } catch (InterruptedException e) {
            // this can not happen due to calling requestImpl block = false
            log.error(e.getLocalizedMessage(), e);
        }
    }
    
    /**
     * Send a message through this communicator and expect a response of type
     * command. ICommand.request will be called at the other site. If a response
     * was received the corresponding data will be set to the command object via
     * setData(). If the response was received during the given timeout,
     * command.response will be called. Otherwise command.timeout will be
     * called. The calling thread of this method will wait until a response is
     * received or the timeout is reached.
     * 
     * @param message
     *            - the Message to send, must not be null otherwise an
     *            CommunicationException is thrown.
     * @param command
     *            - the expected command, must not be null or a
     *            CommunicationException is thrown. If the command arrives in
     *            good time, the method execute() of the given instance will be
     *            called. If the commands arrives to late timeout() will be
     *            called.
     * @param timeout
     *            - max milliseconds to wait for a response. Only values greater
     *            than zero are valid. For values less or equals to zero the
     *            configured default timeout will be used. If the timeout
     *            expires, the method timeout() in command will be called.
     * @throws CommunicationException
     *             if any error/exception occurs. A CommnunicationException is
     *             also thrown if this communicator is not connected, e.g. run()
     *             was not called, or exceptions at creation time were ignored
     */
    public void requestAndWait(Message message, ICommand command, int timeout)
            throws CommunicationException, InterruptedException {
        requestImpl(message, command, timeout, true);
    }
    
    /**
     * Send a message through this communicator and expect a response of type
     * command. ICommand.request will be called at the other site. If a response
     * was received the corresponding data will be set to the command object via
     * setData(). If the response was received during the given timeout,
     * command.response will be called. Otherwise command.timeout will be
     * called. The calling thread of this method will wait until a response is
     * received or the timeout is reached.
     * 
     * @param message
     *            - the Message to send, must not be null otherwise an
     *            CommunicationException is thrown.
     * @param command
     *            - the expected command, must not be null or a
     *            CommunicationException is thrown. If the command arrives in
     *            good time, the method execute() of the given instance will be
     *            called. If the commands arrives to late timeout() will be
     *            called.
     * @param timeout
     *            - max milliseconds to wait for a response. Only values greater
     *            than zero are valid. For values less or equals to zero the
     *            configured default timeout will be used. If the timeout
     *            expires, the method timeout() in command will be called.
     * @param wait 
     *            whether this method should block (wait) for the command being executed
     * @throws CommunicationException
     *             if any error/exception occurs. A CommnunicationException is
     *             also thrown if this communicator is not connected, e.g. run()
     *             was not called, or exceptions at creation time were ignored
     */
    private void requestImpl(Message message, ICommand command, int timeout,
            boolean wait) throws CommunicationException, 
            InterruptedException {
        checkConnectionState("request()"); //$NON-NLS-1$
        // check parameter
        if (message == null) {
            log.debug("method request with null for parameter" //$NON-NLS-1$
                    + "message called."); //$NON-NLS-1$
            throw new CommunicationException(
                    "no message to send as request", MessageIDs.E_MESSAGE_NOT_TO_REQUEST); //$NON-NLS-1$
        }
        if (command == null) {
            log.debug("method request with null for parameter " //$NON-NLS-1$
                    + "command called"); //$NON-NLS-1$
            throw new CommunicationException(
                    "no command for receiving response", //$NON-NLS-1$
                    MessageIDs.E_NO_RECEIVING_COMMAND);
        }
        int timeoutToUse = DEFAULT_REQUEST_TIMEOUT;
        if (timeout <= 0) {
            log.debug("invalid timeout given to request: " + //$NON-NLS-1$
                    "using default timeout"); //$NON-NLS-1$
        } else {
            timeoutToUse = timeout;
        }
        MessageIdentifier messageIdentifier = new MessageIdentifier(
                m_connection.getNextSequenceNumber());
        try {
            message.setMessageId(messageIdentifier);
            // create string message
            String messageToSend = m_serializer.serialize(message);
            // put command into awaiting responses
            AwaitingCommand awaitingCommand = new AwaitingCommand(command,
                    timeoutToUse);
            synchronized (m_awaitingCommands) {
                m_awaitingCommands.put(messageIdentifier, awaitingCommand);
            }
            // send message and start thread
            m_connection.send(
                    new MessageHeader(MessageHeader.REQUEST, message),
                    messageToSend);
            awaitingCommand.start();
            if (wait) {
                awaitingCommand.join(timeoutToUse);
            }
        } catch (SerialisationException se) {
            log.error(se.getLocalizedMessage(), se);
            throw new CommunicationException(
                    "could not send message as request:" //$NON-NLS-1$
                            + se.getMessage(),
                    MessageIDs.E_MESSAGE_NOT_TO_REQUEST);
        } catch (IOException ioe) {
            log.error(ioe.getLocalizedMessage(), ioe);
            synchronized (m_awaitingCommands) {
                m_awaitingCommands.remove(messageIdentifier);
            }
            throw new CommunicationException(
                    "io error occurred during requesting a message: "//$NON-NLS-1$
                            + ioe.getMessage(), MessageIDs.E_MESSAGE_REQUEST);
        } catch (IllegalArgumentException iae) {
            log.error(iae.getLocalizedMessage(), iae);
            synchronized (m_awaitingCommands) {
                m_awaitingCommands.remove(messageIdentifier);
            }
            log.debug(iae.getLocalizedMessage(), iae);
            throw new CommunicationException(
                    "message could not send as a request", //$NON-NLS-1$
                    MessageIDs.E_MESSAGE_NOT_TO_REQUEST);
        }
    }

    /**
     * Closes the connection, calls to an unconnected connection will be ignored.
     */
    public synchronized void close() {
        if (m_connection != null) {
            Connection toClose = m_connection;
            m_connection = null;
            toClose.removeErrorHandler(m_errorListener);
            toClose.close();
            getConnectionManager().remove(toClose);
            m_errorListener.shutDown();
        } else {
            if (log.isDebugEnabled()) {
                log.debug("close() called for an unconnected communicator"); //$NON-NLS-1$
            }
        }
        if (m_serverSocket != null && m_isServerSocketClosable) {
            try {
                m_serverSocket.close();
            } catch (IOException e) {
                log.info("Exception in closing server Socket", e); //$NON-NLS-1$
            }
        }
    }

    /**
     * notifies the error listener with sendFailed(); 
     * @param header  the header
     * @param message  the message
     */
    private synchronized void fireSendFailed(MessageHeader header,
        String message) {
        if (log.isInfoEnabled()) {
            log.info("firing sendFailed, message=" + message); //$NON-NLS-1$
        }
        try {
            Message data = m_serializer.deserialize(header, message);
            Iterator iter = ((HashSet)((HashSet)m_errorListeners).clone())
                .iterator();
            while (iter.hasNext()) {
                try {
                    ((ICommunicationErrorListener)iter.next()).sendFailed(data);
                } catch (Throwable t) {
                    log.error("Exception while calling listener", //$NON-NLS-1$
                            t);
                }
            }
        } catch (SerialisationException se) {
            // serialization failed -> log
            log.error("deserialisation of\n" + message + //$NON-NLS-1$
                "\nduring notifying " + //$NON-NLS-1$
                "CommunicationErrorListeners " + //$NON-NLS-1$
                "with sendFailed() failed", se); //$NON-NLS-1$
        }
    }

    /**
     * notifies the error listener with shutDown <br>
     * notifies the connection manager also
     */
    private synchronized void fireShutDown() {
        log.info("firing shutDown"); //$NON-NLS-1$ 
        // notify the error listener
        Iterator iter = ((HashSet)((HashSet)m_errorListeners).clone())
            .iterator(); 
        while (iter.hasNext()) {
            try {
                ((ICommunicationErrorListener)iter.next()).shutDown();
            } catch (Throwable t) {
                log.error("Exception while calling listener", //$NON-NLS-1$
                        t);
            }
        } 
        // notify the connection manager
        getConnectionManager().remove(m_connection);
    }

    /**
     * notifies the error listener with acceptingFailed 
     * @param port  the used port for accepting
     */
    private synchronized void fireAcceptingFailed(int port) {
        if (log.isInfoEnabled()) {
            log.info("firing acceptingFailed"); //$NON-NLS-1$
        }
        Iterator iter = ((HashSet)((HashSet)m_errorListeners).clone())
            .iterator();

        while (iter.hasNext()) {
            try {
                ((ICommunicationErrorListener)iter.next())
                        .acceptingFailed(port);
            } catch (Throwable t) {
                log.error("Exception while calling listener", //$NON-NLS-1$
                        t);
            }
        }
    }

    /**
     * notifies the listener with connectingFailed() 
     * @param inetAddress the remote address
     * @param port the remote port
     */
    private synchronized void fireConnectingFailed(InetAddress inetAddress,
        int port) {
             
        if (log.isDebugEnabled()) {
            log.debug("firing connectingFailed"); //$NON-NLS-1$
        } 
        Iterator iter = ((HashSet)((HashSet)m_errorListeners).clone())
            .iterator(); 
        while (iter.hasNext()) {
            try {
                ((ICommunicationErrorListener)iter.next()).connectingFailed(
                        inetAddress, port);
            } catch (Throwable t) {
                log.error("Exception while calling listener", t); //$NON-NLS-1$
            }
        }
    }

    /**
     * notifies the listeners with connectionGained <br>
     * @param inetAddress the remote address
     * @param port the remote port
     */
    private synchronized void fireConnectionGained(InetAddress inetAddress,
        int port) {
        
        log.info("firing connectionGained"); //$NON-NLS-1$
        
        // notify the listeners
        Iterator iter = ((HashSet)((HashSet)m_errorListeners).clone())
            .iterator();
        while (iter.hasNext()) {
            try {
                ((ICommunicationErrorListener)iter.next()).connectionGained(
                        inetAddress, port);
            } catch (Throwable t) {
                log.error("Exception while calling listener", //$NON-NLS-1$
                        t);
            }
        }
    }

    /**
     * setting the up the connection with the given socket
     * @param socket the socket, gained by connecting (Socket.Constructor)
     */
    private void setup(DefaultClientSocket socket) throws IOException {
        m_connection = new Connection(socket);
        setup(m_connection, socket);
    }

    /**
     * setting the up the connection with the given socket
     * @param socket the socket, gained by accepting (on a ServerSocket)
     * @param bufferedReader The input stream reader for the given socket.
     */
    private void setup(Socket socket, BufferedReader bufferedReader) {
        m_connection = new Connection(socket, bufferedReader);
        setup(m_connection, socket);
    }

    /**
     * Initializes the given connection and socket, adding necessary listeners
     * and starting to read the input stream of the socket.
     * 
     * @param conn The connection to initialize.
     * @param socket The socket associated with the connection.
     */
    private void setup(Connection conn, Socket socket) {
        // add listener
        conn.addMessageHandler(m_connectionListener);
        conn.addErrorHandler(m_errorListener); 
        // set an exceptionHandler
        conn.setExceptionHandler(getExceptionHandler()); 
        // start reading from connection
        String id = socket.toString();
        conn.startReading(id); 
        fireConnectionGained(socket.getInetAddress(), socket.getPort());
    }
    
    /**
     * Listener implementing IMessageHandler. creates the command objects and
     * calls the execute method in the command objects. uses Hashmap with awaiting commands synchronized
     * @author BREDEX GmbH
     * @created 16.07.2004
     */
    private class ConnectionListener implements IMessageHandler {

        /**
         * {@inheritDoc}
         */
        public void received(MessageHeader header, String message) {
            if (log.isDebugEnabled()) {
                log.debug("received message:" + message); //$NON-NLS-1$
            }
            try {
                // deserialize message
                Message data = m_serializer.deserialize(header, message);
                MessageIdentifier receivedMessageId = data.getMessageId();
                MessageIdentifier boundedId = data.getBindId();
                // boundedId is the key in the awaiting commands map
                ICommand command = null;
                AwaitingCommand awaitingCommand = null;
                if (boundedId != null) {
                    // data is an answer
                    // it's an awaiting command too ?
                    synchronized (m_awaitingCommands) {
                        awaitingCommand = m_awaitingCommands.get(boundedId);
                    }
                }
                if (awaitingCommand != null) {
                    // yes, it's an awaiting command
                    // remove it from the map
                    synchronized (m_awaitingCommands) {
                        m_awaitingCommands.remove(boundedId);
                    }
                    if (!data.getCommandClass().equals(
                        awaitingCommand.getCommand().getClass().getName())) {
                        log.error("answer is of wrong type"); //$NON-NLS-1$
                        // RETURN FROM HERE
                        return;
                    }
                    if (awaitingCommand.isTimeoutExpired()) {
                        // timeout expired, method timeout() already called
                        // just finish
                        // RETURN FROM HERE
                        log.warn("Received response " + awaitingCommand.getCommand() + " *after* timeout expired."); //$NON-NLS-1$ //$NON-NLS-2$
                        return;
                    }
                    // timeout not expired, stop the thread
                    log.debug("Received command response for " + awaitingCommand.getCommand()); //$NON-NLS-1$
                    awaitingCommand.commandReceived();
                    command = awaitingCommand.getCommand();
                }
                if (command == null) {
                    // create a new command, the message is set in createCommand
                    command = createCommand(data);
                } else {                    
                    command.setMessage(data); // fill message
                }
                // call execute(), catch any exception
                Message response = null;
                try {
                    response = command.execute();
                } catch (Throwable t) {
                    log.error("caught exception from '" //$NON-NLS-1$
                            + command.getClass().getName()
                            + ".execute()'", t); //$NON-NLS-1$
                }
                if (response != null) {
                    log.debug("Sending response: " + response); //$NON-NLS-1$
                    // mark response as answer of the received message
                    response.setBindId(receivedMessageId);                    
                    send(response); // send message back
                }
            } catch (ClassCastException cce) {
                log.error("wrong type in the map of awaiting responses", cce); //$NON-NLS-1$
            } catch (SerialisationException se) {
                log.error("deserialisation of a received message failed", se); //$NON-NLS-1$
            } catch (UnknownCommandException uce) {
                log.error("received message with unknown command", uce); //$NON-NLS-1$
            } catch (CommunicationException ce) {
                log.error("could not send answer ", ce); //$NON-NLS-1$
            }
        }
    }

    /**
     * class for listening to the connection for errors, notifies the listeners, registered to the communicator
     * @author BREDEX GmbH
     * @created 23.07.2004
     */
    private class ErrorListener implements IErrorHandler {

        /**
         * {@inheritDoc}
         */
        public void sendFailed(MessageHeader header, String message) {
            fireSendFailed(header, message);
        }

        /**
         * {@inheritDoc}
         */
        public void shutDown() {
            fireShutDown();
        }
    }

    /**
     * class to put instances of into the hash map of awaiting commands. It's also the thread to handle timeouts for requests.
     * @author BREDEX GmbH
     * @created 20.07.2004
     */
    private static class AwaitingCommand extends IsAliveThread {
        /** flag if timeout has expires */
        private boolean m_timeoutExpired;

        /** reference to the command */
        private ICommand m_command;

        /** the timeout in milliseconds */
        private long m_timeout;

        /** indicates whether the awaited command has been received */
        private boolean m_wasCommandReceived;
        
        /**
         * default constructor 
         * @param command the awaiting command
         * @param timeout the time in milliseconds when command.timeout() will be called
         */
        public AwaitingCommand(ICommand command, int timeout) {
            super("Awaiting command: " + command.getClass()); //$NON-NLS-1$
            m_command = command;
            m_timeout = timeout;
            m_wasCommandReceived = false;
            setTimeoutExpired(false);
        }

        /**
         * @return Returns the command.
         */
        public ICommand getCommand() {
            return m_command;
        }

        /**
         * @return Returns the timeoutExpired.
         */
        public synchronized boolean isTimeoutExpired() {
            return m_timeoutExpired;
        }

        /**
         * @param timeoutExpired The timeoutExpired to set.
         */
        private synchronized void setTimeoutExpired(boolean timeoutExpired) {
            m_timeoutExpired = timeoutExpired;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            // only go through the sleep process if the command has 
            // not yet arrived
            if (!wasCommandReceived()) {
                long startTime = System.currentTimeMillis();
                while (!wasCommandReceived() 
                    && startTime + m_timeout >= System.currentTimeMillis()) {
                    
                    try {
                        sleep(200); 
                    } catch (InterruptedException ie) {
                        // Do nothing.
                        // If a valid interrupt occurred, then the loop will end
                        // on next iteration because the command was received.
                    }
                }
                 
                // one last check whether the command was received after
                // the waiting period
                if (!wasCommandReceived()) {
                    // timeout occurs: set flag
                    setTimeoutExpired(true); 
                    // call timeout in the command, catch any exception
                    try {
                        m_command.timeout();
                    } catch (Exception e) {
                        log.error("caught exception from '" //$NON-NLS-1$
                                + m_command.getClass().getName()
                                + ".timeout()'", e); //$NON-NLS-1$
                    }
                }
            }
        }

        /**
         * Notes that the command was received in good time.
         */
        public synchronized void commandReceived() {
            m_wasCommandReceived = true;
            interrupt();
        }

        /**
         * @return the wasCommandReceived
         */
        public synchronized boolean wasCommandReceived() {
            return m_wasCommandReceived;
        }
    }

    /**
     * a thread accepting connections, so Communicator.run() will not block 
     * @author BREDEX GmbH
     * @created 29.07.2004
     */
    private class AcceptingThread extends IsAliveThread {

        /**
         * Constructor
         */
        public AcceptingThread() {
            super("Accepting Thread - listening on port "  //$NON-NLS-1$
                    + m_serverSocket.getLocalPort());
        }
        
        /**
         * {@inheritDoc}
         */
        public void run() {
            while (isAccepting() && !Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = m_serverSocket.accept();
                    final InputStream inputStream = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream, 
                                    Connection.IO_STREAM_ENCODING));

                    // find out what kind of client initiated the connection
                    String response = DefaultServerSocket.requestClientType(
                            socket, reader, inputStream,
                            DEFAULT_CONNECTING_TIMEOUT * THOUSAND);
                    
                    if (response != null) {
                        IConnectionInitializer initializer = 
                                m_responseToInitializer.get(response);
                        if (initializer != null) {
                            initializer.initConnection(socket, reader);
                        } else {
                            // use default initialization strategy
                            int nextState = 
                                getConnectionManager().getNextState();
                            DefaultServerSocket.send(socket, nextState);
                            if (nextState == ConnectionState.SERVER_OK) {
                                setup(socket, reader);
                                // notify the connection manager
                                getConnectionManager().add(m_connection);
                            }
                        }
                    } else {
                        // No useful response from client. 
                        // Just close the socket.
                        socket.close();
                    }

                } catch (IOException ioe) {
                    log.debug(ioe.getLocalizedMessage(), ioe);
                    fireAcceptingFailed(m_serverSocket.getLocalPort());
                    // HERE exception manager if the IOExceptions
                    // are numerous, at the moment stop accepting
                    setAccepting(false);
                } catch (Throwable t) {
                    log.error(t.getLocalizedMessage(), t);
                    setAccepting(false);
                    // HERE exception handler for accepting ?
                    // setAccepting(getAcceptingExceptionHandler().handle(t));
                }
            }
        }
    }
    
    /**
     * Interface for the connection manager. 
     * @author BREDEX GmbH
     * @created 21.09.2004
     */
    public interface ConnectionManager {
        /** the property name for the change listener*/
        public static String PROP_CONNECTION_CHANGE = "connection_changed"; //$NON-NLS-1$
        /**
         * the state to send at next accept
         * @return a constant from ConnectionState
         */
        public int getNextState();
        
        /**
         * a new connection was created
         * @param connection the new connection
         */
        public void add(Connection connection);

        /**
         * the connection was closed
         * @param connection the closed connection
         */
        public void remove(Connection connection);

        /**
         * @param listener add the listener
         */
        public void addPropertyChangedListener(PropertyChangeListener listener);

        /**
         * 
         * @param listener removes the listener
         */
        public void removePropertyChangedListener(
                PropertyChangeListener listener);

    }
    
    
    /**
     * Default manager, accepts exact one connection 
     * @author BREDEX GmbH
     * @created 21.09.2004
     */
    private static class DefaultConnectionManager implements ConnectionManager {
        /** number of maximum connections to accept, this implementation accepts just one */
        private static final int BACKLOG = 1;
        /** logger */
        private static ConfigurableLogger cmLogger = new ConfigurableLogger(
                LoggerFactory.getLogger(DefaultConnectionManager.class));
        /** list with accepted connections */
        private List<Connection> m_connections; 
        /** property change support for adding and removing connections */
        private PropertyChangeSupport m_propertyChangeSupport =
                new PropertyChangeSupport(this);
        
        /**
         * default constructor <br>
         */
        public DefaultConnectionManager() {
            m_connections = new ArrayList<Connection>(BACKLOG);
        }
        
        /**
         * @return the state to send at next accept
         */
        public int getNextState() {
            if (m_connections.size() < BACKLOG) {
                return ConnectionState.SERVER_OK;
            }
            return ConnectionState.SERVER_BUSY;
        }
        
        /**
         * start managing the given connection, just put it into the list
         * @param connection the connection to manage
         */
        public void add(Connection connection) {
            m_connections.add(connection);
            try {
                m_propertyChangeSupport.firePropertyChange(
                        ConnectionManager.PROP_CONNECTION_CHANGE,
                        m_connections.size() - 1, m_connections.size());
            } catch (Exception e) {
                cmLogger.warn("exception during calling of listeners", e); //$NON-NLS-1$
            }
        }
        
        /**
         * stop managing the given connection, just remove it from the list
         * @param connection the connection to remove
         */
        public void remove(Connection connection) {
            m_connections.remove(connection);
            try {
                m_propertyChangeSupport.firePropertyChange(
                        ConnectionManager.PROP_CONNECTION_CHANGE,
                        m_connections.size() + 1, m_connections.size());
            } catch (Exception e) {
                cmLogger.warn("exception during calling of listeners", e); //$NON-NLS-1$
            }
        }

        /**
         * {@inheritDoc}
         */
        public void addPropertyChangedListener(
                PropertyChangeListener listener) {
            m_propertyChangeSupport.addPropertyChangeListener(listener);
        }

        /**
         * {@inheritDoc}
         */
        public void removePropertyChangedListener(
                PropertyChangeListener listener) {
            m_propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    /**
     * prepare the communicator for connection problems
     */
    public void prepareForConnectionProblems() {
        setEnablementOfConnectionLogger(false);
    }

    /**
     * @param enable
     *            the loggers enablement
     * 
     */
    private void setEnablementOfConnectionLogger(boolean enable) {
        Connection c = getConnection();
        if (c != null) {
            c.getLogger().setEnabled(enable);
        }
    }
    
    /**
     * Interrupts all timeouts
     * Call this when the TestExecution gets interrupted.
     */
    public void interruptAllTimeouts() {
        Set<MessageIdentifier> keys = new HashMap
            <MessageIdentifier, AwaitingCommand>(
                    m_awaitingCommands).keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            final Object key = iter.next();
            AwaitingCommand cmd = m_awaitingCommands.get(key);
            cmd.commandReceived();
            m_awaitingCommands.remove(key);
        }
    }
    
    /**
     * @return Returns the port.
     */
    public int getPort() {
        return m_port;
    }

    /**
     * @return Returns the inetAddress.
     */
    public String getHostName() {
        return m_inetAddress.getHostName();
    }

    /**
     * @return Returns the connection.
     */
    public Connection getConnection() {
        return m_connection;
    }

    /**
     * Clears the list of error listeners.
     */
    public void clearListeners() {
        m_errorListeners.clear();
        if (m_connection != null) {
            m_connection.clearListeners();
        }
    }
}
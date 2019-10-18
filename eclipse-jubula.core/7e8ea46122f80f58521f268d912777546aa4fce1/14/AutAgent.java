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
package org.eclipse.jubula.autagent.common.agent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.autagent.common.commands.AbstractStartToolkitAut;
import org.eclipse.jubula.autagent.common.i18n.Messages;
import org.eclipse.jubula.autagent.common.utils.AutStartHelperRegister;
import org.eclipse.jubula.autagent.common.utils.IAUTStartHelper;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.IConnectionInitializer;
import org.eclipse.jubula.communication.internal.connection.ConnectionState;
import org.eclipse.jubula.communication.internal.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.internal.message.ConnectToAutResponseMessage;
import org.eclipse.jubula.communication.internal.message.ConnectToClientMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.PrepareForShutdownMessage;
import org.eclipse.jubula.communication.internal.message.StartAUTServerMessage;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.IsAliveThread;
import org.eclipse.jubula.tools.internal.utils.StringParsing;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A server that allows AUTs to be registered with and accessed from a 
 * centralized location. 
 *
 * @author BREDEX GmbH
 * @created Dec 1, 2009
 */
public class AutAgent {
    
    /**
     * the default value to wait after a proper AUT termination (== de-registration) 
     */
    public static final int AUT_POST_DEREGISTRATION_DELAY_DEFAULT = 2000;
    /**
     * Name of the variable to override the AUTs proper AUT de-registration delay
     */
    public static final String AUT_POST_DEREGISTRATION_DELAY_VAR = "TEST_AUT_POST_DEREGISTRATION_DELAY"; //$NON-NLS-1$

    /** property name for collection of registered AUTs */
    public static final String PROP_NAME_AUTS = "auts"; //$NON-NLS-1$
    
    /** property name for AUT ID mode */
    public static final String PROP_KILL_DUPLICATE_AUTS = "killDuplicateAuts"; //$NON-NLS-1$
    
    /** the log */
    private static final Logger LOG = LoggerFactory.getLogger(AutAgent.class);
    
    /**
     * Initializes the connection to a registering AUT.
     *
     * @author BREDEX GmbH
     * @created Mar 26, 2010
     */
    private class AutRegistrationInitializer 
            implements IConnectionInitializer {

        /**
         * {@inheritDoc}
         */
        public void initConnection(final Socket socket, 
                final BufferedReader reader) {
            new IsAliveThread("Register AUT") { //$NON-NLS-1$
                /**
                 * {@inheritDoc}
                 */
                @SuppressWarnings("synthetic-access")
                public void run() {
                    try {
                        String autInfoLine = reader.readLine();
                        if (autInfoLine != null 
                                && autInfoLine.length() > 0) {

                            final AutIdentifier autId = 
                                AutIdentifier.decode(autInfoLine);
                            
                            final Communicator autCommunicator = 
                                new Communicator(0, 
                                        this.getClass().getClassLoader());
                            autCommunicator.addCommunicationErrorListener(
                                    new AutCommunicationErrorListener(
                                            autId, autCommunicator));
                            autCommunicator.run();

                            PrintStream printStream = 
                                new PrintStream(socket.getOutputStream());
                            printStream.println(EnvConstants.LOCALHOST_FQDN);
                            printStream.println(autCommunicator.getLocalPort());
                            printStream.flush();

                        } else {
                            LOG.debug("AUT did not send information and so will not be registered."); //$NON-NLS-1$
                        }
                    
                    } catch (IOException ioe) {
                        // Error occurred while constructing the stream
                        // or reading AUT information. The AUT was not 
                        // successfully registered.
                        // Let thread execution continue in order to
                        // close the connection.
                        LOG.error("Error occurred while establishing communication with AUT.", ioe); //$NON-NLS-1$
                    } catch (SecurityException se) {
                        LOG.error("Error occurred while establishing communication with AUT.", se); //$NON-NLS-1$
                    } catch (JBVersionException gdve) {
                        LOG.error("Error occurred while establishing communication with AUT.", gdve); //$NON-NLS-1$
                    }

                    // Try to cleanup
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // Socket could not be closed.
                        // Do nothing.
                    }
                }
            } .start();
        }

    }

    /**
     * Initializes the connection to an instance of autrun.
     *
     * @author BREDEX GmbH
     * @created Mar 26, 2010
     */
    private class AutRunConnectionInitializer 
        implements IConnectionInitializer {

        /**
         * {@inheritDoc}
         */
        public void initConnection(final Socket socket,
                final BufferedReader reader) {

            new IsAliveThread("Register autrun") { //$NON-NLS-1$
                /**
                 * {@inheritDoc}
                 */
                @SuppressWarnings("synthetic-access")
                public void run() {
                    try {
                        String autID = reader.readLine();
                        String toolkit = reader.readLine();
                        if (autID != null 
                                && autID.length() > 0
                                && toolkit != null 
                                && toolkit.length() > 0) {

                            AutIdentifier autId = new AutIdentifier(autID);
                            m_autIdToRestartHandler.put(autId, 
                                    new RestartAutAutRun(
                                            autId, socket, reader, toolkit));
                        }
                    } catch (IOException ioe) {
                        // Error occurred while constructing the stream
                        // or reading autrun information. autrun was not 
                        // successfully registered.
                        // Let thread execution continue in order to
                        // close the connection.
                        LOG.error("Error occurred while establishing communication with autrun.", ioe); //$NON-NLS-1$

                        // Try to cleanup
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Socket could not be closed.
                            // Do nothing.
                        }
                    } catch (SecurityException se) {
                        LOG.error("Error occurred while establishing communication with autrun.", se); //$NON-NLS-1$

                        // Try to cleanup
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Socket could not be closed.
                            // Do nothing.
                        }
                    }

                }
            } .start();
        }
        
    }
    
    /**
     * Error listener for communication with the AUT Server. Handles 
     * registration / de-registration when the connection is gained / lost.
     *
     * @author BREDEX GmbH
     * @created Mar 22, 2010
     */
    private class AutCommunicationErrorListener 
            implements ICommunicationErrorListener {

        /** 
         * the ID of the Running AUT with which communication is being 
         * established 
         */
        private AutIdentifier m_autId;
        
        /** 
         * the communicator being listened to 
         */
        private Communicator m_autCommunicator;

        /**
         * Constructor
         * 
         * @param autId The ID of the Running AUT with which communication is 
         *              being established.
         * @param autCommunicator   The communicator being listened to.
         */
        public AutCommunicationErrorListener(
                AutIdentifier autId, Communicator autCommunicator) {

            m_autId = autId;
            m_autCommunicator = autCommunicator;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public void shutDown() {
            removeAut(m_autId);
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public void sendFailed(Message message) {
            // Do nothing
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public void connectionGained(InetAddress inetAddress, int port) {
            boolean registeredAutSetWasModified = 
                addAut(m_autId, m_autCommunicator);
            
            while (!registeredAutSetWasModified && !m_killDuplicateAuts) {
                m_autId = new AutIdentifier(StringParsing.incrementSequence(
                        m_autId.getExecutableName()));
                registeredAutSetWasModified = 
                    addAut(m_autId, m_autCommunicator);
            }

            if (!registeredAutSetWasModified && m_killDuplicateAuts) {
                try {
                    m_autCommunicator.send(new PrepareForShutdownMessage());
                } catch (CommunicationException e) {
                    LOG.info(e.getLocalizedMessage(), e);
                    // As a result of not being able to send the message,
                    // the AUT will end with a different exit code. This
                    // may result in an unnecessary error dialog.
                }
                m_autCommunicator.clearListeners();
                m_autCommunicator.close();
            }
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public void connectingFailed(InetAddress inetAddress, int port) {
            // Do nothing
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public void acceptingFailed(int port) {
            // Do nothing
        }
    }
    
    /** the socket that accepts incoming connections */
    private ServerSocket m_serverSocket;

    /** property change support */
    private PropertyChangeSupport m_propertyChangeSupport;
    
    /** 
     * maps registered AUTs to their corresponding communicator
     */
    private Map<AutIdentifier, Communicator> m_auts = 
        new HashMap<AutIdentifier, Communicator>();

    /** whether the AUT Agent is running */
    private boolean m_isRunning = true;

    /** 
     * flag indicating whether AUTs that attempt to register with an AUT ID 
     * that is already registered should be shutdown 
     */
    private boolean m_killDuplicateAuts = true;
    
    /** 
     * mapping from AUT ID to information about how to start the 
     * corresponding AUT 
     */
    private Map<AutIdentifier, IRestartAutHandler> m_autIdToRestartHandler = 
        new HashMap<AutIdentifier, IRestartAutHandler>();

    /** mapping from client type to connection initializer */
    private Map<String, IConnectionInitializer> m_connectionInitializers =
        new HashMap<String, IConnectionInitializer>();
    
    /** Cache for {@link AutIdentifier} to toolkit */
    private Map<AutIdentifier, String> m_autToolkits =
        new LinkedHashMap<AutIdentifier, String>(5) {
        protected boolean removeEldestEntry(
                Map.Entry<AutIdentifier, String> eldest) {
            return size() > 10; // caching only 10 autIdentifiers
        }
    };
    /**
     * Constructor
     * 
     * Creates an agent that can be used as part of a server. This agent will
     * not open its own server socket.
     * 
     */
    public AutAgent() {
        m_propertyChangeSupport = new PropertyChangeSupport(this);
        initConnectionInitializers();
    }
    
    /**
     * Constructor
     * 
     * Starts the constructed agent on the given port.
     * 
     * @param port The port number on which to start the agent. A port number 
     *             of <code>0</code> starts the agent on an available port. 
     * 
     * @throws IOException if an error occurs while initializing the 
     *                     agent's server socket.
     */
    public AutAgent(int port) throws IOException {
        m_propertyChangeSupport = new PropertyChangeSupport(this);
        m_serverSocket = new ServerSocket(port);
        initConnectionInitializers();
    }

    /**
     * Initializes the connection initializer map.
     */
    @SuppressWarnings("synthetic-access")
    private void initConnectionInitializers() {
        m_connectionInitializers.put(ConnectionState.CLIENT_TYPE_AUT, 
                new AutRegistrationInitializer());
        m_connectionInitializers.put(ConnectionState.CLIENT_TYPE_AUTRUN, 
                new AutRunConnectionInitializer());
    }
    
    /**
     * 
     * @return the port number on which the agent is running.
     */
    public int getPort() {
        return m_serverSocket.getLocalPort();
    }

    /**
     * Waits for and delegates incoming connections. This method does not return
     * until the agent is shut down. 
     * 
     * @throws IOException 
     */
    public void waitForConnections() throws IOException {
        while (m_isRunning) {
            try {
                final Socket socket = m_serverSocket.accept();
                m_connectionInitializers.get(ConnectionState.CLIENT_TYPE_AUT)
                    .initConnection(socket, new BufferedReader(
                        new InputStreamReader(socket.getInputStream())));
            } catch (SocketException se) {
                // Server is shutting down
                // Do nothing. The loop will end on the next iteration.
            }
        }
    }
    
    /**
     * 
     * @return a copy of the collection of registered AUTs. Changes made to the
     *         returned copy do not write through to the internal collection 
     *         maintained by the agent.
     */
    public Set<AutIdentifier> getAuts() {
        synchronized (m_auts) {
            return new HashSet<AutIdentifier>(m_auts.keySet());
        }
    }

    /**
     * Adds the given AUT to the collection of registered AUTs.
     * 
     * @param autId The ID of the AUT to register.
     * @param autCommunicator The communicator to use for the registered
     *                        AUT.
     * @return <code>true</code> if the set of Registered AUTs was changed as 
     *         a result of this call (i.e. the AUT was not already registered).
     *         Otherwise <code>false</code>.
     */
    private boolean addAut(AutIdentifier autId, Communicator autCommunicator) {
        boolean wasSetChanged = false;
        synchronized (m_auts) {
            if (m_isRunning) {
                wasSetChanged = !m_auts.containsKey(autId);
                if (wasSetChanged) {
                    m_auts.put(autId, autCommunicator);
                }
            }
        }
        if (wasSetChanged) {
            m_propertyChangeSupport.firePropertyChange(
                    PROP_NAME_AUTS, null, autId);
        }
        
        return wasSetChanged;
    }

    /**
     * Removes the given AUT from the collection of registered AUTs.
     * 
     * @param autId
     *            The ID of the AUT to de-register.
     */
    private void removeAut(AutIdentifier autId) {
        boolean wasSetChanged = false;
        synchronized (m_auts) {
            if (m_isRunning) {
                m_autIdToRestartHandler.remove(autId);                
                Communicator autCommunicator = m_auts.remove(autId);
                if (autCommunicator != null) {
                    autCommunicator.prepareForConnectionProblems();
                    try {
                        autCommunicator.send(
                                new PrepareForShutdownMessage());
                    } catch (CommunicationException e) {
                        LOG.info(e.getLocalizedMessage(), e);
                        // As a result of not being able to send the message,
                        // the AUT will end with a different exit code. This
                        // may result in an unnecessary error dialog.
                    }
                    autCommunicator.clearListeners();
                    autCommunicator.close();
                }
                wasSetChanged = autCommunicator != null;
            }
        }
        if (wasSetChanged) {
            m_propertyChangeSupport.firePropertyChange(
                    PROP_NAME_AUTS, autId, null);
        }
    }

    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     *
     * @param propertyName  The name of the property to listen on.
     * @param listener  The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(
            String propertyName, PropertyChangeListener listener) {
        m_propertyChangeSupport.addPropertyChangeListener(
                propertyName, listener);
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * The same listener object may be added more than once, and will be called
     * as many times as it is added.
     * If <code>listener</code> is null, no exception is thrown and no action
     * is taken.
     *
     * @param listener  The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        m_propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(
            String propertyName, PropertyChangeListener listener) {
        m_propertyChangeSupport.removePropertyChangeListener(
                propertyName, listener);
    }

    /**
     * Shuts down the agent. 
     */
    public void shutdown() {
        synchronized (m_auts) {
            m_isRunning = false;
            try {
                if (m_serverSocket != null) {
                    m_serverSocket.close();
                }
            } catch (IOException e) {
                // Unable to close server socket.
                // Do nothing.
            }
            m_auts = Collections.emptyMap();
        }
    }

    /**
     * 
     * @param args program arguments
     */
    public static void main(String[] args) throws IOException {
        int port = 0;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.err.println("Port argument '"  //$NON-NLS-1$
                        + args[0] 
                        + "' is not an integer. A different port will be used."); //$NON-NLS-1$
            }
        }

        AutAgent agent = new AutAgent(port);
        System.out.println("Agent started on port: " + agent.getPort()); //$NON-NLS-1$

        // AUT Registration listener. Prints registration changes to the 
        // console.
        agent.addPropertyChangeListener(
                PROP_NAME_AUTS, new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        Object newValue = evt.getNewValue();
                        if (newValue instanceof AutIdentifier) {
                            System.out.println("Registered AUT: " + ((AutIdentifier)newValue).getExecutableName()); //$NON-NLS-1$
                        }
                        
                        Object oldValue = evt.getOldValue();
                        if (oldValue instanceof AutIdentifier) {
                            System.out.println("Deregistered AUT: " + ((AutIdentifier)oldValue).getExecutableName()); //$NON-NLS-1$
                        }
                    }
            
                });


        agent.waitForConnections();
    }

    /**
     * Stops the given AUT.
     * 
     * @param autId
     *            The ID of the Running AUT to stop.
     * @param timeout
     *            indicates whether the AUT should be forced to quit (timeout ==
     *            0) or whether the AUT should terminate by itself (timeout > 0)
     */
    public void stopAut(AutIdentifier autId, int timeout) {
        boolean force = timeout == 0;
        if (!force) {
            long startTime = System.currentTimeMillis();
            boolean timedOut = false;
            while (m_auts.containsKey(autId) && !timedOut) {
                timedOut = (startTime + timeout) < System.currentTimeMillis();
                TimeUtil.delay(250);
            }
            if (!timedOut) {
                // The AUT has just unregistered itself - which must not be exactly
                // the same as terminated - therefore we wait for another moment
                // of time
                TimeUtil.delayDefaultOrExternalTime(
                        AUT_POST_DEREGISTRATION_DELAY_DEFAULT,
                        AUT_POST_DEREGISTRATION_DELAY_VAR);
            } else {
                force = true;
            }
        }
        synchronized (m_auts) {
            Communicator autCommunicator = m_auts.get(autId);
            if (autCommunicator != null) {
                if (force) {
                    removeAut(autId);
                }
            }
        }
    }

    /**
     * Restarts the AUT with the given ID.
     * 
     * @param autId
     *            The ID of the Running AUT to restart.
     * @param timeout
     *            indicates whether the AUT should be forced to quit (timeout ==
     *            0) or whether the AUT should terminate by itself (timeout > 0)
     */
    public void restartAut(AutIdentifier autId, int timeout) {
        // cache the start method
        final IRestartAutHandler message = m_autIdToRestartHandler.get(autId);

        message.restartAut(this, timeout);
    }
    
    /**
     * Assigns the given startup information to the corresponding AUT ID. This
     * information will be used when restarting an AUT.
     * 
     * @param message The startup information.
     */
    public void setStartAutMessage(StartAUTServerMessage message) {
        Map<String, String> autConfig = message.getAutConfiguration();
        String autExecName = autConfig.get(AutConfigConstants.AUT_ID);
        AutIdentifier autId = new AutIdentifier(autExecName);
        m_autToolkits.put(autId, message.getAutToolKit());
        synchronized (m_auts) {
            if (!m_auts.containsKey(autId)) {
                m_autIdToRestartHandler.put(
                    autId, new RestartAutConfiguration(autId, message));
            }
        }
    }
    
    /**
     * Sends a request to a Running AUT. The AUT should then connect to the
     * Client using the provided connection information.
     * 
     * @param autId The ID of the AUT to which the message should be sent.
     * @param clientHostName The host name to which the AUT should connect.
     * @param clientPort The port number to which the AUT should connect.
     * @return a response that indicates whether the message was successfully 
     *         sent. This response does <b>not</b> indicate whether the message 
     *         was successfully received and processed.
     */
    public ConnectToAutResponseMessage sendConnectToClientMessage(
            AutIdentifier autId, String clientHostName, int clientPort) {

        synchronized (m_auts) {
            Communicator autSocket = m_auts.get(autId);
            if (autSocket == null) {
                LOG.error(Messages.AutConnectionError);
                return new ConnectToAutResponseMessage(
                        Messages.AutConnectionError);
            }

            try {
                Map<String, String> fragmentMap = new HashMap<>();
                //Create fragment classpath for on demand fragment loading
                synchronized (m_autIdToRestartHandler) {
                    //Determine toolkit of the AUT
                    String startClass = m_autIdToRestartHandler.get(autId)
                            .getAUTStartClass();
                    Class autServerClass = Class.forName(startClass);
                    AbstractStartToolkitAut autStarter = 
                            (AbstractStartToolkitAut) autServerClass
                            .newInstance();
                    String rcBundleID = autStarter.getRcBundleId();
                    //Only for Java Toolkits
                    if (CommandConstants.RC_JAVAFX_BUNDLE_ID.equals(rcBundleID)
                            || CommandConstants.RC_SWING_BUNDLE_ID.equals(
                                    rcBundleID)
                            || CommandConstants.RC_SWT_BUNDLE_ID.equals(
                                    rcBundleID)) {
                        IAUTStartHelper autStartHelper =
                                AutStartHelperRegister.INSTANCE
                                        .getAutStartHelper();
                        fragmentMap.putAll(autStartHelper
                                .getFragmentPathforBundleID(rcBundleID));
                    }
                }
                autSocket.send(new ConnectToClientMessage(clientHostName,
                        clientPort, fragmentMap));
            } catch (CommunicationException | ClassNotFoundException
                    | InstantiationException | IllegalAccessException ce) {
                LOG.error(ce.getLocalizedMessage(), ce);
                return new ConnectToAutResponseMessage(
                        ce.getLocalizedMessage());
            }
            return new ConnectToAutResponseMessage(null);
        }
    }

    /**
     * 
     * @return a copy of the connection initializer map used by the receiver.
     */
    public Map<String, IConnectionInitializer> getConnectionInitializers() {
        return new HashMap<String, IConnectionInitializer>(
                m_connectionInitializers);
    }

    /**
     * Configures how the Agent will handle attempts to register an AUT with
     * an ID that is already registered.
     * 
     * @param killDuplicateAuts <code>true</code> if the Agent should shutdown 
     *                          duplicate AUTs. <code>false</code> if duplicate 
     *                          AUTs should be allowed to continue running 
     *                          (they will not, however, be registered).
     */
    public void setKillDuplicateAuts(boolean killDuplicateAuts) {
        boolean oldValue = m_killDuplicateAuts;
        m_killDuplicateAuts = killDuplicateAuts;
        m_propertyChangeSupport.firePropertyChange(
                PROP_KILL_DUPLICATE_AUTS, oldValue, m_killDuplicateAuts);
    }

    /**
     * 
     * @return <code>true</code> if the Agent should shutdown 
     *         duplicate AUTs. <code>false</code> if duplicate AUTs should be 
     *         allowed to continue running (they will not, however, 
     *         be registered).
     */
    public boolean isKillDuplicateAuts() {
        return m_killDuplicateAuts;
    }

    /**
     * 
     * @param id the {@link AUTIdentifier}
     * @return the {@link Communicator} from the AUTagent to the AUT
     */
    public Communicator getAutCommunicator(AUTIdentifier id) {
        return m_auts.get(id);
    }

    /**
     * This method is using a cache which is saving the {@link AutIdentifier} to ToolkitName
     * @param connectedAutId the {@link AutIdentifier to check}
     * @return the ToolkitName if it is in the Cache
     */
    public String getToolkitForAutID(AutIdentifier connectedAutId) {
        String toolkit =  m_autToolkits.get(connectedAutId);
        if (StringUtils.isBlank(toolkit)) {
            return getToolkitFromRestartHandler(connectedAutId);
        }
        return toolkit;
    }
    
    /**
     * 
     * @param autIdentifier the {@link AutIdentifier} from which you want to know the Toolkit
     * @return the toolkit string or <code>null</code>
     */
    private String getToolkitFromRestartHandler(AutIdentifier autIdentifier) {
        IRestartAutHandler iRestartAutHandler = m_autIdToRestartHandler
                .get(autIdentifier);
        if (iRestartAutHandler != null) {
            String startCommand = iRestartAutHandler
                    .getAUTStartClass();
            if (StringUtils.containsIgnoreCase(startCommand, "swing")) { //$NON-NLS-1$
                return CommandConstants.SWING_TOOLKIT;
            } else if (StringUtils.containsIgnoreCase(startCommand, "swt")) { //$NON-NLS-1$
                return CommandConstants.SWT_TOOLKIT;
            } else if (StringUtils.containsIgnoreCase(startCommand, "rcp")) { //$NON-NLS-1$
                return CommandConstants.RCP_TOOLKIT;
            } else if (StringUtils.containsIgnoreCase(startCommand, "javafx")) { //$NON-NLS-1$
                return CommandConstants.JAVAFX_TOOLKIT;
            } else if (StringUtils.containsIgnoreCase(startCommand, "html")) { //$NON-NLS-1$
                return CommandConstants.HTML_TOOLKIT;
            }
        }
        return null;
    }
}

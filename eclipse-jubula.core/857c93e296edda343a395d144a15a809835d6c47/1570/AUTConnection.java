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
package org.eclipse.jubula.client.core.communication;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.IClientTest;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.commands.AUTStateCommand;
import org.eclipse.jubula.client.core.events.AUTEvent;
import org.eclipse.jubula.client.core.events.AUTServerEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.events.ServerEvent;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingProfilePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.client.core.progress.ProgressConsoleRegistry;
import org.eclipse.jubula.client.core.status.TimeMultiStatus;
import org.eclipse.jubula.client.core.status.TimeStatus;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.BaseAUTConnection;
import org.eclipse.jubula.client.internal.commands.AUTStartedCommand;
import org.eclipse.jubula.client.internal.commands.ConnectToAutResponseCommand;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.internal.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.internal.message.AUTErrorsMessage;
import org.eclipse.jubula.communication.internal.message.AUTErrorsResponseCommand;
import org.eclipse.jubula.communication.internal.message.AUTStateMessage;
import org.eclipse.jubula.communication.internal.message.ConnectToAutMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.SendAUTListOfSupportedComponentsMessage;
import org.eclipse.jubula.communication.internal.message.SendCompSystemI18nMessage;
import org.eclipse.jubula.communication.internal.message.UnknownMessageException;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class represents the connection to the AUTServer which controls the
 * application under test.
 * 
 * This class is implemented as a singleton. The server configuration contains
 * detailed information, how this instance can be contacted.
 * 
 * @author BREDEX GmbH
 * @created 22.07.2004
 */
public class AUTConnection extends BaseAUTConnection {
    
    /** the logger */
    static final Logger LOG = LoggerFactory.getLogger(AUTConnection.class);

    /** the singleton instance */
    private static AUTConnection instance = null;

    /** The m_autConnectionListener */
    private AUTConnectionListener m_autConnectionListener;
    
   
    
    /**
     * private constructor. creates a communicator
     * 
     * @param portNum the port number - 0 if random
     * 
     * @throws ConnectionException
     *             containing a detailed message why the connection could not
     *             initialized
     */
    private AUTConnection(int portNum) throws ConnectionException {
        super(portNum);
        m_autConnectionListener = new AUTConnectionListener();
        getCommunicator().addCommunicationErrorListener(
            m_autConnectionListener);
    }

    /**
     * Method to get the single instance of this class.
     * 
     * @throws ConnectionException
     *             if an error occurs during initialization.
     * @return the instance of this Singleton
     */
    public static synchronized AUTConnection getInstance()
        throws ConnectionException {

        if (instance == null) {
            String port = EnvironmentUtils.getProcessOrSystemProperty(
                    EnvConstants.CLIENTPORT_KEY);
            int portNum = 0;
            try {
                if (port != null) {
                    portNum = Integer.parseInt(port);
                }
            } catch (NumberFormatException e) {
                LOG.error("Unable to parse the Client Port number: " + port); //$NON-NLS-1$
            }
            instance = new AUTConnection(portNum);
        }
        return instance;
    }
    
    /**
     * Resets this singleton: Closes the communicator
     * removes the listeners.<br>
     * <b>Note: </b><br>
     * This method is used by the Restart-AUT-Action only to avoid errors while
     * reconnecting with the AUTServer.<br>
     * This is necessary because the disconnect from the AUTServer is implemented
     * badly which will be corrected in a future version!
     */
    public synchronized void reset() {
        super.reset();

        instance = null;
    }

    /**
     * @param autId AutIdentifier of AUT
     * @param monitor 
     * @return <code>true</code> if a connection to the AUT could be
     *         established. Otherwise <code>false</code>.
     */
    public IStatus connectToAut(AutIdentifier autId, 
            IProgressMonitor monitor) {
        
        int timeOut = CONNECT_TO_AUT_TIMEOUT;
        
        IAUTMainPO autMain = getAUTMain(autId);
        if (autMain != null) {
            try {
                String propValue = getAUTProperty(autMain,
                        IAUTMainPO.Property.TIME_OUT.getValue());
                timeOut = Integer.parseInt(propValue);
            } catch (Exception e) {
                //Do nothing. Default time out value will be used
            }
        }
        
        return connectToAutImpl(autId, monitor, timeOut);
    }
    
    /**
     * @param autId AutIdentifier of AUT
     * @return IAUTMainPO 
     */
    public IAUTMainPO getAUTMain(AutIdentifier autId) {
        Iterator<IAUTMainPO> auts = GeneralStorage.getInstance()
                .getProject().getAutMainList().iterator();
        while (auts.hasNext()) {
            IAUTMainPO aut = auts.next();
            if (aut.getName().equals(autId.getExecutableName())) {
                return aut;
            }
        }
        return null;
    }
    
    /**
     * @param autMain the AUT which properties will be checked 
     * @param propName the name of parameter
     * @return value of property which name is equal with param propSrt
     */
    public String getAUTProperty(IAUTMainPO autMain, String propName) {
        Iterator<String> props = autMain.getPropertyKeys().iterator();
        
        while (props.hasNext()) {
            String prop = props.next();
            if (prop.toLowerCase().equals(propName.toLowerCase())) {
                return autMain.getPropertyMap().get(prop);
            }
        }
        return null;
    }

    /**
     * Establishes a connection to the Running AUT with the given ID. 
     * 
     * @param autId The ID of the Running AUT to connect to.
     * @param monitor The progress monitor.
     * @param timeOut 
     * @return <code>true</code> if a connection to the AUT could be 
     *         established. Otherwise <code>false</code>.
     */
    private IStatus connectToAutImpl(AutIdentifier autId,
            IProgressMonitor monitor, int timeOut) {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        TimeMultiStatus status;
        IProgressConsole pc = ProgressConsoleRegistry.INSTANCE.getConsole();
        if (!isConnected()) {
            ded.fireAutServerConnectionChanged(ServerState.Connecting);
            try {
                TimeStatus s = sendRequestToAgent(autId, monitor, ded, pc);
                if (s.getSeverity() != IStatus.OK) {
                    return s;
                }
                long startTime = System.currentTimeMillis();
                while (!monitor.isCanceled()
                        && !isConnected()
                        && AutAgentConnection.getInstance().isConnected()
                        && startTime + timeOut > System
                                .currentTimeMillis()) {
                    TimeUtil.delay(200);
                }
                if (isConnected()) {
                    TimeMultiStatus connect = new TimeMultiStatus(
                            Activator.PLUGIN_ID, IStatus.OK, 
                            "Connection to AUT: \"" + autId.encode() + "\" established", //$NON-NLS-1$ //$NON-NLS-2$
                            null);
                    pc.writeStatus(connect, autId.encode());
                    TimeMultiStatus ext = getExtensionStatus();
                    pc.writeStatus(ext, autId.encode());
                    connect.add(ext);
                    setConnectedAutId(autId);
                    LOG.info(Messages.ConnectionToAUTEstablished);
                    IAUTMainPO aut = AutAgentRegistration.getAutForId(autId,
                            GeneralStorage.getInstance().getProject());
                    if (aut != null) {
                        AUTStartedCommand response = new AUTStartedCommand();
                        response.setStateMessage(new AUTStateMessage(
                                AUTStateMessage.RUNNING));
                        setup(response);
                    } else {
                        LOG.warn(Messages.ErrorOccurredActivatingObjectMapping);
                        connect.add(new TimeStatus(IStatus.WARNING,
                                Activator.PLUGIN_ID,
                                Messages.ErrorOccurredActivatingObjectMapping));
                    }
                    return connect;
                }
                LOG.error(Messages.ConnectionToAUTCouldNotBeEstablished);
                status = new TimeMultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
                        Messages.ConnectionToAUTCouldNotBeEstablished, null);
            } catch (CommunicationException e) {
                LOG.error(Messages.ErrorOccurredEstablishingConnectionToAUT, e);
                status = new TimeMultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
                       Messages.ErrorOccurredEstablishingConnectionToAUT, null);
            } catch (JBVersionException e) {
                LOG.error(Messages.ErrorOccurredEstablishingConnectionToAUT, e);
                status = new TimeMultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
                       Messages.ErrorOccurredEstablishingConnectionToAUT, null);
            } finally {
                monitor.done();
            }
        } else {
            LOG.warn(Messages.CannotEstablishNewConnectionToAUT);
            status = new TimeMultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
                    Messages.CannotEstablishNewConnectionToAUT, null);
        }
        ded.fireAutServerConnectionChanged(ServerState.Disconnected);
        pc.writeStatus(status, autId.encode());
        return status;
    }

    /**
     * @param autId the autID
     * @param monitor the monitor
     * @param ded DataEventDispatcher
     * @param pc IProgressConsole
     * @throws AlreadyConnectedException
     * @throws JBVersionException
     * @throws CommunicationException
     * @throws ConnectionException
     * @return Status indicating if sending the request to the agent was successful
     */
    private TimeStatus sendRequestToAgent(AutIdentifier autId,
            IProgressMonitor monitor, DataEventDispatcher ded,
            IProgressConsole pc) throws AlreadyConnectedException,
            JBVersionException, CommunicationException, ConnectionException {
        monitor.subTask(NLS.bind(Messages.ConnectingToAUT,
                autId.getExecutableName()));
        LOG.info(Messages.EstablishingConnectionToAUT);
        run();
        getCommunicator()
                .addCommunicationErrorListener(m_autConnectionListener);
        ConnectToAutResponseCommand responseCommand = sendConnectToAUT(autId);
        if (responseCommand.getMessage() != null
                && responseCommand.getMessage().getErrorMessage() != null) {
            // Connection has failed
            ded.fireAutServerConnectionChanged(ServerState.Disconnected);
            TimeStatus s = new TimeStatus(IStatus.ERROR, Activator.PLUGIN_ID,
                    IStatus.ERROR, responseCommand
                    .getMessage().getErrorMessage(), null);
            pc.writeStatus(s, autId.encode());
            return s;
        }
        return new TimeStatus(IStatus.OK, Activator.PLUGIN_ID, "Request send to Agent"); //$NON-NLS-1$
    }

    /**
     * Sends a message to the AUT-Agent which starts the connect to AUT
     * procedure. The Invoker will wait for a response from the AUT-Agent
     * whether or not a "connectToITE" message could be send to the AUT. If that
     * was successful AUT will try to setup a connection with the client.
     * 
     * @param autId the autId of the AUT to connect to
     * @return the response from the AUT-Agent
     * @throws CommunicationException
     * @throws ConnectionException
     */
    private ConnectToAutResponseCommand sendConnectToAUT(AutIdentifier autId)
            throws CommunicationException, ConnectionException {
        ConnectToAutResponseCommand responseCommand =
            new ConnectToAutResponseCommand();
        try {
            String ipAddr = EnvironmentUtils.getProcessOrSystemProperty(
                    EnvConstants.CLIENTIP_KEY);
            if (StringUtils.isEmpty(ipAddr)) {
                ipAddr = EnvConstants.LOCALHOST_FQDN; 
            }
            AutAgentConnection.getInstance().getCommunicator()
                .requestAndWait(new ConnectToAutMessage(
                    ipAddr, getCommunicator().getLocalPort(), autId), 
                responseCommand, 10000);
        } catch (InterruptedException e) {
            LOG.error("connect to AUT: " + e); //$NON-NLS-1$
        }
        return responseCommand;
    }

    /**
     * Communicates with the AUT, therefore this has to be called after a
     * connection to the AUT communicator has been made. Sends a Message to the
     * AUT and awaits a response with errors and warnings which occurred during
     * the connection.
     * 
     * @return MultiStatus containing information about loaded and not loaded extensions
     * @throws CommunicationException
     */
    private TimeMultiStatus getExtensionStatus()
            throws CommunicationException {
        AUTErrorsResponseCommand resp = 
                new AUTErrorsResponseCommand();
        try {
            TimeMultiStatus status = new TimeMultiStatus(Activator.PLUGIN_ID,
                    IStatus.INFO, "Extension Status", null); //$NON-NLS-1$
            this.getCommunicator().requestAndWait(
                    new AUTErrorsMessage(),
                    resp, 10000);
            List<String> err = resp.getErrors();
            for (String string : err) {
                status.add(new TimeStatus(IStatus.WARNING,
                        Activator.PLUGIN_ID, string));
            }
            List<String> war = resp.getWarnings();
            for (String string : war) {
                status.add(new TimeStatus(IStatus.INFO,
                        Activator.PLUGIN_ID, string));
            }
            return status;
        } catch (InterruptedException e) {
            LOG.error("AUT Connection, could not recieve AUT Extension errors" + e); //$NON-NLS-1$
            return new TimeMultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
                    "AUT Connection, could not recieve AUT Extension errors", e); //$NON-NLS-1$
        }
    }
    
    /**
     * setup the connection between ITE and AUT
     * 
     * @param command
     *            the command to execute on callback
     * @throws NotConnectedException
     *             if there is no connection to an AUT.
     * @throws ConnectionException
     *             if no connection to an AUT could be initialized.
     * @throws CommunicationException
     *             if an error occurs while communicating with the AUT.
     */
    public void setup(AUTStartedCommand command)
        throws NotConnectedException, ConnectionException,
        CommunicationException {
        sendKeyboardLayoutToAut();
        sendResourceBundlesToAut();
        getAllComponentsFromAUT(command);
    }
    
    /**
     * Sends the i18n resource bundles to the AUT Server.
     */
    private void sendResourceBundlesToAut() {
        SendCompSystemI18nMessage i18nMessage = new SendCompSystemI18nMessage();
        i18nMessage.setResourceBundles(CompSystemI18n.bundlesToString());
        try {
            send(i18nMessage);
        } catch (CommunicationException e) {
            LOG.error(Messages.CommunicationErrorWhileSettingResourceBundle, e);
        }
    }

    /**
     * Query the AUTServer for all supported components.
     * <code>listener.componentInfo()</code> will be called when the answer
     * receives.
     * 
     * @param command
     *            the command to execute as a callback
     * 
     * @throws CommunicationException
     *             if an error occurs while communicating with the AUT.
     */
    private void getAllComponentsFromAUT(AUTStartedCommand command)
        throws CommunicationException {
        
        LOG.info(Messages.GettingAllComponentsFromAUT);

        try {
            SendAUTListOfSupportedComponentsMessage message = 
                new SendAUTListOfSupportedComponentsMessage();
            // Send the supported components and their implementation classes
            // to the AUT server to get registered.
            CompSystem compSystem = ComponentBuilder.getInstance()
                    .getCompSystem();
            IAUTMainPO connectedAut = TestExecution.getInstance()
                    .getConnectedAut();
            String autToolkitId = connectedAut.getToolkit();
            try {
                // Add simple extensions to comp-system
                ToolkitDescriptor toolkitDescriptor =
                        ToolkitSupportBP.getToolkitDescriptor(autToolkitId);
                String supportedClasses = connectedAut.getPropertyMap().get(
                        "SimpleExtensions"); //$NON-NLS-1$
                compSystem.addSimpleExtensions(supportedClasses != null
                            ? Arrays.asList(supportedClasses.split(",")) //$NON-NLS-1$
                            : new ArrayList<String>(),
                        toolkitDescriptor);
            } catch (ToolkitPluginException e) {
                LOG.error("Problem while loading simple extensions " + e); //$NON-NLS-1$
            }
            List<Component> components = compSystem.getComponents(
                    autToolkitId, true);

            // optimization: only concrete components need to be registered,
            // as abstract components do not have a corresponding tester class
            components.retainAll(compSystem.getConcreteComponents());
            message.setComponents(components);
            
            Profile profile = new Profile();
            IObjectMappingProfilePO profilePo = connectedAut.getObjMap()
                    .getProfile();
            profile.setNameFactor(profilePo.getNameFactor());
            profile.setPathFactor(profilePo.getPathFactor());
            profile.setContextFactor(profilePo.getContextFactor());
            profile.setThreshold(profilePo.getThreshold());
            message.setProfile(profile);
            
            int timeoutToUse = AUTStateCommand.AUT_COMPONENT_RETRIEVAL_TIMEOUT;
            request(message, command, timeoutToUse);

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() <= startTime + timeoutToUse
                    && !command.wasExecuted() && isConnected()) {
                TimeUtil.delay(500);
            }
            if (!command.wasExecuted() && isConnected()) {
                throw new CommunicationException(
                        Messages.CouldNotRequestComponentsFromAUT,
                        MessageIDs.E_COMMUNICATOR_CONNECTION);
            }
        } catch (UnknownMessageException ume) {
            ClientTest.instance().fireAUTServerStateChanged(
                    new AUTServerEvent(ume.getErrorId()));
        } 
    }
    
    /**
     * The listener listening to the communicator.
     * 
     * @author BREDEX GmbH
     * @created 12.08.2004
     */
    private class AUTConnectionListener implements ICommunicationErrorListener {

        /**
         * {@inheritDoc}
         */
        public void connectionGained(InetAddress inetAddress, int port) {
            if (LOG.isInfoEnabled()) {
                try {
                    String logMessage = Messages.ConnectedTo 
                            + inetAddress.getHostName()
                            + StringConstants.COLON + String.valueOf(port);
                    LOG.info(logMessage);
                } catch (SecurityException se) {
                    LOG.debug(Messages.SecurityViolationGettingHostNameFromIP);
                }
            }
            ClientTest.instance().
                fireAUTServerStateChanged(new AUTServerEvent(
                    ServerEvent.CONNECTION_GAINED));
        }

        /**
         * {@inheritDoc}
         */
        public void shutDown() {
            if (LOG.isInfoEnabled()) {
                LOG.info(Messages.ConnectionToAUTServerClosed);
                LOG.info(Messages.ClosingConnectionToTheAutStarter);
            }
            disconnectFromAut();
            DataEventDispatcher.getInstance().fireAutServerConnectionChanged(
                    ServerState.Disconnected);
            IClientTest clientTest = ClientTest.instance();
            clientTest.fireAUTServerStateChanged(new AUTServerEvent(
                    AUTServerEvent.TESTING_MODE));
            clientTest.fireAUTStateChanged(new AUTEvent(AUTEvent.AUT_STOPPED));
            clientTest.fireAUTServerStateChanged(new AUTServerEvent(
                    ServerEvent.CONNECTION_CLOSED));
        }

        /**
         * {@inheritDoc}
         */
        public void sendFailed(Message message) {
            LOG.error(Messages.SendingMessageFailed + StringConstants.COLON 
                    + message.toString());
            LOG.error(Messages.ClosingConnectionToTheAUTServer);
            close();
        }

        /**
         * {@inheritDoc}
         */
        public void acceptingFailed(int port) {
            LOG.warn(Messages.AcceptingFailed + StringConstants.COLON 
                    + String.valueOf(port));
        }

        /**
         * {@inheritDoc}
         */
        public void connectingFailed(InetAddress inetAddress, int port) {
            StringBuilder msg = new StringBuilder();
            msg.append(Messages.ConnectingFailed);
            msg.append(StringConstants.LEFT_PARENTHESIS);
            msg.append(StringConstants.RIGHT_PARENTHESIS);
            msg.append(StringConstants.SPACE);
            msg.append(Messages.CalledAlthoughThisIsServer);
            LOG.error(msg.toString());
        }
    }
}

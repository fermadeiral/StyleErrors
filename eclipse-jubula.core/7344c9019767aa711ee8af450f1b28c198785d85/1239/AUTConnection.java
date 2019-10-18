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
package org.eclipse.jubula.client.internal;

import java.util.Map;

import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.client.internal.impl.AUTAgentImpl.ErrorListener;
import org.eclipse.jubula.communication.internal.message.ConnectToAutMessage;
import org.eclipse.jubula.communication.internal.message.SendAUTListOfSupportedComponentsMessage;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;
import org.eclipse.jubula.tools.internal.xml.businessprocess.ProfileBuilder;
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
    static final Logger LOGGER = LoggerFactory.getLogger(AUTConnection.class);

    /** the singleton instance */
    private static AUTConnection instance = null;
    
    /**
     * private constructor. creates a communicator
     * @param port the port or 0 if automatically a port should be assigned
     * @throws ConnectionException
     *             containing a detailed message why the connection could not
     *             initialized
     */
    private AUTConnection(int port) throws ConnectionException {
        super(port);
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
            instance = new AUTConnection(0);
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
     * @param typeMapping 
     * @return <code>true</code> if a connection to the AUT could be
     *         established. Otherwise <code>false</code>.
     */
    public boolean connectToAut(AutIdentifier autId, 
            Map<ComponentClass, String> typeMapping) {
        
        return connectToAutImpl(autId, typeMapping, CONNECT_TO_AUT_TIMEOUT);
    }
    
    /**
     * @param autId AutIdentifier of AUT
     * @param typeMapping 
     * @param timeOut the time out value
     * @return <code>true</code> if a connection to the AUT could be
     *         established. Otherwise <code>false</code>.
     */
    public boolean connectToAut(AutIdentifier autId, 
            Map<ComponentClass, String> typeMapping, int timeOut) {
        
        return timeOut <= 0 ? connectToAut(autId, typeMapping)
                : connectToAutImpl(autId, typeMapping, timeOut);
    }

    /**
     * Establishes a connection to the Running AUT with the given ID.
     * 
     * @param autId
     *            The ID of the Running AUT to connect to.
     * @param typeMapping
     *            the type mapping to use
     * @param timeOut until this time wait for connecting to the AUT
     * @return <code>true</code> if a connection to the AUT could be
     *         established. Otherwise <code>false</code>.
     */
    private boolean connectToAutImpl(AutIdentifier autId, 
        Map<ComponentClass, String> typeMapping, int timeOut) {
        if (!isConnected()) {
            try {
                LOGGER.info("Establishing connection to AUT..."); //$NON-NLS-1$
                run();
                getCommunicator().addCommunicationErrorListener(
                    new ErrorListener(Thread.currentThread()));
                final AutAgentConnection autAgent = AutAgentConnection
                    .getInstance();
                autAgent.getCommunicator().send(
                    new ConnectToAutMessage(EnvConstants.LOCALHOST_FQDN,
                        getCommunicator().getLocalPort(), autId));

                long startTime = System.currentTimeMillis();
                while (!isConnected()
                    && autAgent.isConnected()
                    && startTime + timeOut > System
                        .currentTimeMillis()) {
                    TimeUtil.delay(200);
                }
                if (isConnected()) {
                    setConnectedAutId(autId);
                    LOGGER.info("Connection to AUT established."); //$NON-NLS-1$
                    setup(typeMapping);
                    return true;
                }
                LOGGER.error("Connection to AUT could not be established."); //$NON-NLS-1$
            } catch (CommunicationException e) {
                LOGGER.error("Error occurred while establishing connection to AUT.", e); //$NON-NLS-1$
            } catch (JBVersionException e) {
                LOGGER .error("Version error occurred while establishing connection to AUT.", e); //$NON-NLS-1$
            }
        } else {
            LOGGER.warn("Cannot establish new connection to AUT: Connection to AUT already exists."); //$NON-NLS-1$
        }
        return false;
    }
    
    /**
     * setup the connection between API and AUT
     * 
     * @param technicalTypeMapping
     *            the technical type mapping to use
     * @throws NotConnectedException
     *             if there is no connection to an AUT.
     * @throws ConnectionException
     *             if no connection to an AUT could be initialized.
     * @throws CommunicationException
     *             if an error occurs while communicating with the AUT.
     */
    public void setup(Map<ComponentClass, String> technicalTypeMapping)
        throws NotConnectedException, ConnectionException,
        CommunicationException {
        sendKeyboardLayoutToAut();
        // FIXME MT: workaround for racing condition
        TimeUtil.delay(1000);
        // The ITEs delay is - afaics - by default big enough to cover this
        sendComponentSupportToAUT(technicalTypeMapping);
    }

    /**
     * send the list of supported components to the AUT
     * 
     * @param technicalTypeMapping
     *            the technicalTypeMapping to use
     */
    private void sendComponentSupportToAUT(
        Map<ComponentClass, String> technicalTypeMapping)
        throws CommunicationException {
        SendAUTListOfSupportedComponentsMessage message = 
            new SendAUTListOfSupportedComponentsMessage();

        message.setTechTypeToTesterClassMapping(technicalTypeMapping);
        message.setProfile(ProfileBuilder.getDefaultProfile());

        send(message);
    }
}
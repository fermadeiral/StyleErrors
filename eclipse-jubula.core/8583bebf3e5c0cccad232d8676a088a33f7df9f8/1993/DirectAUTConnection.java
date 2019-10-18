/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.autagent.desktop.connection;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.client.core.commands.AUTStateCommand;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.internal.BaseAUTConnection;
import org.eclipse.jubula.client.internal.commands.AUTStartedCommand;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.message.AUTStateMessage;
import org.eclipse.jubula.communication.internal.message.SendAUTListOfSupportedComponentsMessage;
import org.eclipse.jubula.communication.internal.message.SendCompSystemI18nMessage;
import org.eclipse.jubula.communication.internal.message.UnknownMessageException;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a direct connection to the AUT. This class is only usable fo 
 * existent connections to the AUT. E.g. the existen connection from the
 * AutAgent to the AUT is used for the Object mapping if done from the autagent
 * @author BREDEX GmbH
 *
 */
public class DirectAUTConnection extends BaseAUTConnection {
    
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(DirectAUTConnection.class);

    /**
     * 
     * @param communicator the {@link Communicator} to the aut
     * @param autID the {@link AutIdentifier} for the Connection
     * @throws ConnectionException if an error did occur
     */
    public DirectAUTConnection(Communicator communicator,
            AutIdentifier autID) throws ConnectionException {
        setCommunicator(communicator);
        setConnectedAutId(autID);
    }
    
    /**
     * setup the connection between ITE and AUT
     * 
     * @throws NotConnectedException
     *             if there is no connection to an AUT.
     * @throws ConnectionException
     *             if no connection to an AUT could be initialized.
     * @throws CommunicationException
     *             if an error occurs while communicating with the AUT.
     */
    public void setup()
        throws NotConnectedException, ConnectionException,
        CommunicationException {
        AUTStartedCommand response = new AUTStartedCommand();
        response.setStateMessage(new AUTStateMessage(
                AUTStateMessage.RUNNING));
        sendKeyboardLayoutToAut();
        sendResourceBundlesToAut();
        getAllComponentsFromAUT(response);
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
    public void getAllComponentsFromAUT(AUTStartedCommand command)
        throws CommunicationException {
        
        LOG.info(Messages.GettingAllComponentsFromAUT);

        try {
            SendAUTListOfSupportedComponentsMessage message = 
                new SendAUTListOfSupportedComponentsMessage();
            // Send the supported components and their implementation classes
            // to the AUT server to get registered.
            CompSystem compSystem = ComponentBuilder.getInstance()
                    .getCompSystem();
            String autToolkitId = AutStarter.getInstance().getAgent()
                    .getToolkitForAutID(getConnectedAutId());
            if (StringUtils.isBlank(autToolkitId)) {
                throw new IllegalArgumentException("toolkit id is not given"); //$NON-NLS-1$
            }
            List<Component> components = compSystem.getComponents(
                    autToolkitId, true);

            // optimization: only concrete components need to be registered,
            // as abstract components do not have a corresponding tester class
            components.retainAll(compSystem.getConcreteComponents());
            message.setComponents(components);
            
            Profile profile = new Profile();
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
            LOG.error("An exception occurred during sending the message", ume); //$NON-NLS-1$
        } 
    }
    
    /**
     * @return true since we are using a Communicator from the autagent to the AUT
     */
    public boolean isConnected() {
        return true;
    }
}

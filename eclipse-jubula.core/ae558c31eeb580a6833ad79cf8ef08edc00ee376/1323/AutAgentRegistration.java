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
package org.eclipse.jubula.client.core.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent.RegistrationStatus;
import org.eclipse.jubula.client.core.events.AutAgentEvent;
import org.eclipse.jubula.client.core.events.IServerEventListener;
import org.eclipse.jubula.client.core.events.ServerEvent;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.commands.RegisteredAutListCommand;
import org.eclipse.jubula.communication.internal.message.GetRegisteredAutListMessage;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manages a collection of listeners and informs those listeners when an
 * event related to AUT registration with an AUT Agent occurs.
 *
 * @author BREDEX GmbH
 * @created Jan 26, 2010
 */
public class AutAgentRegistration 
        implements IServerEventListener, IAutRegistrationListener {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(AutAgentRegistration.class);
    
    /** the single instance */
    private static AutAgentRegistration instance;

    /** listeners for AUT registration events */
    private Set<IAutRegistrationListener> m_listeners = 
        new CopyOnWriteArraySet<IAutRegistrationListener>();
    
    /** list of all currently registered AUTs */
    private List<AutIdentifier> m_registeredAuts = 
        new ArrayList<AutIdentifier>();
    
    /**
     * Private constructor for singleton.
     */
    private AutAgentRegistration() {
        ClientTest.instance().addAutAgentEventListener(this);
    }
    
    /**
     * 
     * @return the single instance.
     */
    public static synchronized AutAgentRegistration getInstance() {
        if (instance == null) {
            instance = new AutAgentRegistration();
        }
        
        return instance;
    }

    /**
     * Adds the given listener to the receiver. If the receiver already contains
     * the given listener, this method does nothing.
     * 
     * @param l The listener to add.
     */
    public void addListener(IAutRegistrationListener l) {
        m_listeners.add(l);
    }

    /**
     * Removes the given listener from the receiver. If the receiver does not 
     * contain the given listener, this method does nothing.
     * 
     * @param l The listener to remove.
     */
    public void removeListener(IAutRegistrationListener l) {
        m_listeners.remove(l);
    }

    /**
     * Notifies all currently registered listeners of the given event.
     * 
     * @param event The event to forward.
     */
    public synchronized void fireAutRegistration(AutRegistrationEvent event) {
        switch (event.getStatus()) {
            case Register:
                m_registeredAuts.add(event.getAutId());
                break;
            case Deregister:
                m_registeredAuts.remove(event.getAutId());
                break;
            default:
                break;
        }

        for (IAutRegistrationListener l : m_listeners) {
            try {
                l.handleAutRegistration(event);
            } catch (Throwable t) {
                LOG.error(Messages.ErrorWhileNotifyingListeners
                        + StringConstants.DOT, t);
            }
        }
    }
    
    /**
     * 
     * @return a list of all currently registered AUTs.
     */
    public synchronized List<AutIdentifier> getRegisteredAuts() {
        return new LinkedList<AutIdentifier>(m_registeredAuts);
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(AutAgentEvent event) {
        switch (event.getState()) {
            case ServerEvent.CONNECTION_CLOSED:
                clearRegisteredAuts();
                break;
            case ServerEvent.CONNECTION_GAINED:
                try {
                    AutAgentConnection.getInstance().request(
                        new GetRegisteredAutListMessage(), 
                        new RegisteredAutListCommand(this), 5000);
                } catch (CommunicationException ce) {
                    LOG.error(Messages.ErrorWhileGettingListOfRegisteredAUTs, 
                            ce);
                }
                break;
            default:
                // Event does not deal with connection or disconnection.
                // Do nothing.
                break;
        }

    }

    /**
     * {@inheritDoc}
     */
    public void handleAutRegistration(AutRegistrationEvent event) {
        fireAutRegistration(event);
    }

    /**
     * Clears the list of registered AUTs by deregistering them and notifying
     * the corresponding listeners. 
     */
    private synchronized void clearRegisteredAuts() {
        for (AutIdentifier autId : getRegisteredAuts()) {
            fireAutRegistration(new AutRegistrationEvent(autId, 
                    RegistrationStatus.Deregister));
        }
    }
    
    /**
     * Returns a mapping from AUT to Running AUTs.
     * 
     * @param project The project containing the AUT definitions to use. 
     *                If <code>null</code>, no AUTs will be found.
     * @param availableAutIds The AUT IDs for which to find corresponding AUTs.
     *                        If <code>null</code> or empty, all currently 
     *                        running AUTs will be used.
     * @return the running AUTs.
     */
    public static Map<IAUTMainPO, Collection<AutIdentifier>> getRunningAuts(
            IProjectPO project, Collection<AutIdentifier> availableAutIds) {

        Set<AutIdentifier> availableIds = new HashSet<AutIdentifier>();
        if (availableAutIds == null || availableAutIds.isEmpty()) {
            availableIds.addAll(
                AutAgentRegistration.getInstance().getRegisteredAuts());
        } else {
            availableIds.addAll(availableAutIds);
        }
        
        Map<IAUTMainPO, Collection<AutIdentifier>> runningAuts = 
            new HashMap<IAUTMainPO, Collection<AutIdentifier>>();
        for (AutIdentifier autId : availableIds) {
            IAUTMainPO aut = getAutForId(autId, project);
            if (aut != null) {
                if (runningAuts.get(aut) == null) {
                    runningAuts.put(aut, new HashSet<AutIdentifier>());
                }
                runningAuts.get(aut).add(autId);
            }
        }

        return runningAuts;
    }

    /**
     * 
     * @param autId The ID to use. If <code>null</code>, no AUT will be
     *              found.
     * @param project The project in which to search. If <code>null</code>,
     *                no AUT will be found.
     * @return the AUT with the given ID in the given Project, or 
     *         <code>null</code> if no such AUT can be found.
     */
    public static IAUTMainPO getAutForId(
            AutIdentifier autId, IProjectPO project) {
        
        if (project != null && autId != null) {
            String autIdString = autId.getExecutableName();
            for (IAUTMainPO aut : project.getAutMainList()) {
                if (aut.getAutIds().contains(autIdString)) {
                    return aut;
                }
                for (IAUTConfigPO autConfig : aut.getAutConfigSet()) {
                    if (autIdString.equals(autConfig.getConfigMap().get(
                            AutConfigConstants.AUT_ID))) {
                        return aut;
                    }
                }
            }
        }
        
        return null;
    }

}

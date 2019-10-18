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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.AutState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IAutStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IServerConnectionListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;


/**
 * @author BREDEX GmbH
 * @created 13.07.2006
 */
public class StartAutBP {
    /** single instance of StartAutBP */
    private static StartAutBP instance = null;
    
    /** last used AUT */
    private IAUTMainPO m_lastUsedAut;

    /** last used AutConfiguration */
    private IAUTConfigPO m_lastUsedConf;

    /** describes, if a project is loaded or not */
    private boolean m_isProjectLoaded = false;
    
    /** describes the state of server connection */
    private boolean m_isServerConnected = false;
    
    /** describes the state of AUT */
    private AutState m_autState = AutState.notRunning;
    
    /** flag to signal if at least one aut is available */
    private boolean m_atLeastOneAutAvailable = false;
    
    /** flag to signal that the AUTStartButton was clicked
     * it doesn't mean that the AUT is really running (see m_autState)
     */
    private boolean m_autStarted = false;
    
    /**
     * <code>m_hostNameCache</code>
     */
    private Map<String, String> m_hostNameCache = new HashMap<String, String>();
    
   
   /** listener for loading of project */
    private IProjectLoadedListener m_projLoadedListener = 
        new IProjectLoadedListener() {
        
        /**
         * {@inheritDoc}
         */
            @SuppressWarnings("synthetic-access")  
            public void handleProjectLoaded() {
                m_isProjectLoaded = true;
                m_lastUsedAut = null;
                m_lastUsedConf = null;
                m_autState = AutState.notRunning;
                m_autStarted = false;
                m_atLeastOneAutAvailable = false;
                fireAUTButtonStateCouldBeChanged();
            }

        };
        
    /**
     * <code>m_currentProjDeletedListener</code>listener for deletion of current project
     */
    private IDataChangedListener m_currentProjDeletedListener =
        new IDataChangedListener() {
            /** {@inheritDoc} */
            public void handleDataChanged(DataChangedEvent... events) {
                for (DataChangedEvent e : events) {
                    handleDataChanged(e.getPo(), e.getDataState(),
                            e.getUpdateState());
                }
            }
            
            @SuppressWarnings("synthetic-access")     
            public void handleDataChanged(IPersistentObject po, 
                DataState dataState, 
                UpdateState updateState) {
                if (updateState == UpdateState.onlyInEditor) {
                    return;
                }
                if (dataState == DataState.Deleted && po instanceof IProjectPO
                    && GeneralStorage.getInstance().getProject() == null) {
                    m_isProjectLoaded = false;
                    m_lastUsedAut = null;
                    m_lastUsedConf = null;
                    m_autState = AutState.notRunning;
                    m_autStarted = false;
                    m_atLeastOneAutAvailable = false;
                }
            }
        };

   
     
    /**
     * <code>m_serverConnectListener</code>listener for modification of server connection
     */
    private IServerConnectionListener m_serverConnectListener = 
        new IServerConnectionListener() {
            
            @SuppressWarnings("synthetic-access") 
            public void handleServerConnStateChanged(ServerState state) {
                switch (state) {
                    case Connected:
                        m_isServerConnected = true;
                        break;
                    case Disconnected:
                    case Connecting:
                        m_isServerConnected = false;
                        m_autState = AutState.notRunning;
                        m_autStarted = false;
                        break;
                    default:
                        Assert.notReached(Messages
                                .UnhandledConnectionStateForServer);
                }
                fireAUTButtonStateCouldBeChanged();
            }
        };

    /**
     * <code>m_autStateListener</code> listener for modification of aut state
     */
    private IAutStateListener m_autStateListener = new IAutStateListener() {
        /**
         * @param state state from AUT
         */
        @SuppressWarnings("synthetic-access") 
        public void handleAutStateChanged(AutState state) {
            switch (state) {
                case running:
                    m_autState = AutState.running;
                    break;
                case notRunning:
                    m_autState = AutState.notRunning;
                    m_autStarted = false;
                    break;
                default:
                    Assert.notReached(Messages.UnhandledAutState);
            }
            fireAUTButtonStateCouldBeChanged();
        }
    };
    
    /**
     * <code>m_projPropModifyListener</code> listener for modification of project properties
     */
    private IProjectStateListener m_projPropModifyListener =
        new IProjectStateListener() {
            /** {@inheritDoc} */
            public void handleProjectStateChanged(ProjectState state) {
                if (ProjectState.prop_modified.equals(state)) {
                    if (getLastUsedAut() == null || getLastUsedConf() == null) {
                        fireAUTButtonStateCouldBeChanged();
                        return;
                    }
                    String autName = getLastUsedAut().getName();
                    String confName = getLastUsedConf().getName();
                    for (IAUTMainPO aut : GeneralStorage.getInstance()
                            .getProject().getAutMainList()) {
    
                        if (autName.equals(aut.getName())) {
                            for (IAUTConfigPO conf : aut.getAutConfigSet()) {
                                if (confName.equals(conf.getName())) {
                                    setLastUsedAut(aut);
                                    setLastUsedAutConf(conf);
                                    return;
                                }
                            }
                        }
                    }
                    fireAUTButtonStateCouldBeChanged();
                }
            }
        };
    
    
    /**
     * private constructor
     */
    private StartAutBP() {
        init();
    }
    
    /**
     * init this BP instance
     */
    private void init() {
        final DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addProjectLoadedListener(m_projLoadedListener, true);
        ded.addDataChangedListener(m_currentProjDeletedListener, true);
        ded.addAutAgentConnectionListener(m_serverConnectListener, true);
        ded.addAutStateListener(m_autStateListener, true);
        ded.addProjectStateListener(m_projPropModifyListener);
    }

    /**
     * get single instance
     * @return single instance of StartAutBP
     */
    public static StartAutBP getInstance() {
        if (instance == null) {
            instance = new StartAutBP();
        }
        return instance;
    }
    
    /**
     * @return all AUTs for connected aut agent
     */
    public SortedMap<IAUTMainPO, SortedSet<IAUTConfigPO>> getAllAUTs() {
        if (GeneralStorage.getInstance().getProject() != null) {
            Set<IAUTMainPO> autsForLang = getAUTs();
            return getAutsForAutAgent(autsForLang);
        }
        return new TreeMap<IAUTMainPO, SortedSet<IAUTConfigPO>>();
    }

    /**
     * @param auts
     *            all auts
     * @return map with all auts applicable for given
     *         configurations for started server
     */
    private SortedMap<IAUTMainPO, SortedSet<IAUTConfigPO>> 
        getAutsForAutAgent(Set<IAUTMainPO> auts) {
        SortedMap<IAUTMainPO, SortedSet<IAUTConfigPO>> autMap = 
            new TreeMap<IAUTMainPO, SortedSet<IAUTConfigPO>>();
        String agentHostname = resolveAUTAgentHostName();
        if (agentHostname != null) {
            agentHostname = agentHostname.toLowerCase();
            String agentIp = resolveIpUsingCache(agentHostname);
            Set<String> validHosts = new HashSet<String>();
            validHosts.add(agentIp);
            validHosts.add(agentHostname);
            final InetAddress lh = EnvConstants.LOCALHOST;
            if (isConnectedToLocalhost(agentIp, agentHostname, lh)) {
                validHosts.add(EnvConstants.LOCALHOST_ALIAS);
                validHosts.add(EnvConstants.LOCALHOST_IP_ALIAS);
                validHosts.add(lh.getHostAddress());
                validHosts.add(lh.getHostName().toLowerCase());
                validHosts.add(EnvConstants.LOCALHOST_FQDN.toLowerCase());
            }
            for (IAUTMainPO autForLang : auts) {
                Set<IAUTConfigPO> confs = autForLang.getAutConfigSet();
                SortedSet<IAUTConfigPO> validConfs = 
                    new TreeSet<IAUTConfigPO>();
                for (IAUTConfigPO conf : confs) {
                    String confAgentName = conf.getConfiguredAUTAgentHostName()
                        .toLowerCase();
                    String confAgentIp = resolveIpUsingCache(confAgentName);
                    if (validHosts.contains(confAgentIp)
                            || validHosts.contains(confAgentName)) {
                        validConfs.add(conf);
                    }
                }
                if (!validConfs.isEmpty()) {
                    autMap.put(autForLang, validConfs);
                }
            }
        }
        return autMap;
    }


    /**
     * @param agentIp
     *            the agent IP address
     * @param agentHostname
     *            the agent host name
     * @param localhost
     *            the local host
     * @return whether the current connection is a connection to the local host
     */
    private boolean isConnectedToLocalhost(String agentIp,
            String agentHostname, InetAddress localhost) {
        if (agentIp.equals(EnvConstants.LOCALHOST_IP_ALIAS)
            || agentHostname.equals(EnvConstants.LOCALHOST_ALIAS)) {
            return true;
        }
        if (localhost == null) {
            return false;
        }
        
        final String hostAddress = localhost.getHostAddress();
        final String hostName = localhost.getHostName().toLowerCase();
        final String fqHostName = localhost.getCanonicalHostName()
                .toLowerCase();
        return hostAddress.equals(agentIp) || hostName.equals(agentHostname)
                || fqHostName.equals(agentHostname);
    }

    /**
     * @return the server host name
     */
    private String resolveAUTAgentHostName() {
        String serverHostName = null;
        try {
            AutAgentConnection serverConn = AutAgentConnection.getInstance();
            if (serverConn.isConnected()
                    && serverConn.getCommunicator() != null) {
                serverHostName = serverConn.getCommunicator().getHostName();
            }
        } catch (ConnectionException ce) {
            // Indicates that no connection has yet been established
        }
        return serverHostName;
    }
    
    /**
     * @param hostname
     *            the hostname to resolve the ip for
     * @return the corresponding ip adress; if not resolvable --> null
     */
    private String resolveIpUsingCache(String hostname) {
        String ip = null;
        if (!m_hostNameCache.containsKey(hostname)) {
            try {
                ip = InetAddress.getByName(hostname).getHostAddress();
            } catch (UnknownHostException e) {
                // ignore
            }
            m_hostNameCache.put(hostname, ip);
        } else {
            ip = m_hostNameCache.get(hostname);
        }
        return ip;
    }

    /**
     * @return AUTs, which support working language
     */
    private Set<IAUTMainPO> getAUTs() {
        return GeneralStorage.getInstance().getProject().getAutMainList();
    }

    /**
     * @return last used AUT
     */
    public IAUTMainPO getLastUsedAut() {
        return m_lastUsedAut;
    }

    /**
     * @param lastUsedAut The lastUsedAut to set.
     */
    public void setLastUsedAut(IAUTMainPO lastUsedAut) {
        m_lastUsedAut = lastUsedAut;
    }

    /**
     * @param conf last used AutConfiguration
     */
    public void setLastUsedAutConf(IAUTConfigPO conf) {
        m_lastUsedConf = conf;        
    }

    /**
     * @return Returns the lastUsedConf.
     */
    public IAUTConfigPO getLastUsedConf() {
        return m_lastUsedConf;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return m_isProjectLoaded 
            && m_isServerConnected 
            && m_autState == AutState.notRunning
            && !m_autStarted
            && m_atLeastOneAutAvailable;
    }


    /**
     * 
     */
    protected void validateNumberOfAuts() {
        final SortedMap<IAUTMainPO, SortedSet<IAUTConfigPO>> allAUTs = 
            getAllAUTs();
        if (allAUTs.size() < 1) {
            m_atLeastOneAutAvailable = false;
        } else {
            m_atLeastOneAutAvailable = true;
            if (m_lastUsedAut != null && m_lastUsedConf != null) {
                validateLastUsedAut(allAUTs);
            }
        }
    }
    
    /**
     * validates, if the current last used AUT is still available
     * @param allAUTs all available AUTs
     */
    private void validateLastUsedAut(
        SortedMap<IAUTMainPO, SortedSet<IAUTConfigPO>> allAUTs) {
        if (!(allAUTs.containsKey(m_lastUsedAut) 
            && allAUTs.get(m_lastUsedAut).contains(m_lastUsedConf))) {
            m_lastUsedAut = null;
            m_lastUsedConf = null;
        }
        
    }

    /**
     * signals, that at least one parameter for computing of enabled state of
     * start aut button is changed
     */
    private void fireAUTButtonStateCouldBeChanged() {
        final String jobName = Messages.UIJobUpdatingRecentlyUsedAuts;
        Job resolveStartableAUTs = new Job(jobName) {
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                validateNumberOfAuts();
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        JobUtils.executeJob(resolveStartableAUTs, null);
    }
    
    /**
     * invoke this method to signal, that the AUTStartButton was clicked
     */
    public void fireAutStarted() {
        m_autStarted = true;
    }

}

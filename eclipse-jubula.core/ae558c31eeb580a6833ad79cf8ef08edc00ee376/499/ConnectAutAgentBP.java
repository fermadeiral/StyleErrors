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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.utils.AutAgentManager;
import org.eclipse.jubula.client.ui.rcp.utils.AutAgentManager.AutAgent;

/**
 * @author BREDEX GmbH
 * @created 07.04.2006
 * 
 */
public final class ConnectAutAgentBP {
    /**
     * <code>instance</code>single instance of ConnectAutAgentBP
     */
    private static ConnectAutAgentBP instance = null;
    
    /**
     * <code>m_currentAutAgent</code> current used server
     */
    private AutAgent m_currentAutAgent = null;

    /**
     * <code>m_autAgentFromPref</code> list with all configured servers from
     * preference store
     */
    private Set <AutAgent> m_autAgentFromPref = 
        AutAgentManager.getInstance().getAutAgents();

    /**
     * <code>m_autAgentPrefListener</code> listener for modification of server
     * preferences
     */
    private IPropertyChangeListener m_autAgentPrefListener = 
            new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (m_currentAutAgent != null
                        && !AutAgentManager.getInstance().getAutAgents()
                                .contains(m_currentAutAgent)) {
                    m_currentAutAgent = null;
                }
            }
        };
    
    /**
     * private constructor
     */
    private ConnectAutAgentBP() {
        Plugin.getDefault().getPreferenceStore()
            .addPropertyChangeListener(m_autAgentPrefListener);
        
    }

    /**
     * @return the single instance
     */
    public static ConnectAutAgentBP getInstance() {
        if (instance == null) {
            instance = new ConnectAutAgentBP();
        }
        return instance;
    }


    /**
     * @param server server to set
     */
    public void setCurrentAutAgent(AutAgent server) {
        m_currentAutAgent = server;
        AutAgentManager.getInstance().setLastUsedAutAgent(server);
        AutAgentManager.getInstance().storeAutAgentList();
    }

    /**
     * @return Returns the current AUT Agent.
     */
    public AutAgent getCurrentAutAgent() {
        return m_currentAutAgent;
    }
    
    /**
     * @return the currently used server, either the last used server or the
     * single server is available in preference store or null!
     */
    public AutAgent getWorkingAutAgent() {
        AutAgent currentautAgent = null;
        // in current session was eventually started a server
        AutAgentManager autAgentManager = AutAgentManager.getInstance();
        final AutAgent lastUsedAutAgent = 
            autAgentManager.getLastUsedAutAgent();
        if (m_currentAutAgent != null) {
            currentautAgent = m_currentAutAgent;
        // not yet started server in current session, but last used server
        // from last session saved in preference store
        } else if (lastUsedAutAgent != null) {
            currentautAgent = lastUsedAutAgent;
            // no last used server available in preference store, but exactly one
            // server from preference store (maybe the default server)
        } else if (autAgentManager.getAutAgents().size() == 1) {
            currentautAgent = autAgentManager.getAutAgents()
                    .iterator().next();
        } 
        // no error dialog because a default server is ever available
        // see org.eclipse.jubula.client.ui.rcp.Plugin.getServerAndPort()*/
        return currentautAgent;
    }

    /**
     * @return list of all AUTConfigs without a corresponding entry for their
     * server in AUT Agents preferences
     */
    public List<IAUTConfigPO> computeUnconfiguredAutAgents() {
        List<IAUTConfigPO> unconfAutAgents = new ArrayList<IAUTConfigPO>();
        Set<IAUTConfigPO> confs = new HashSet<IAUTConfigPO>();
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            // get all AUTs from project
            Set<IAUTMainPO> auts = project.getAutMainList();
            // get all AUTConfigs of available AUTs
            for (IAUTMainPO aut : auts) {
                if (!aut.getAutConfigSet().isEmpty()) {
                    Iterator<IAUTConfigPO> it = aut.getAutConfigSet()
                            .iterator();
                    while (it.hasNext()) {
                        confs.add(it.next());
                    }
                }
            }
            for (IAUTConfigPO conf : confs) {
                if (isUnconfiguredAutAgent(conf
                        .getConfiguredAUTAgentHostName())) {
                    unconfAutAgents.add(conf);
                }                
            }
        }
        return unconfAutAgents;
    }

    /**
     * @param autAgentName name of server to validate
     * @return if servername is not contained in preferences
     */
    private boolean isUnconfiguredAutAgent(String autAgentName) {
        for (AutAgent server : m_autAgentFromPref) {
            if (server.getName().equals(autAgentName)) {
                return false;
            }
        }
        return true;
    }
}

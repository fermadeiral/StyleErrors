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
package org.eclipse.jubula.client.ui.rcp.utils;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Helper-class to manage the AUT Agent preferences.
 *
 * @author BREDEX GmbH
 * @created 08.12.2005
 */
public class AutAgentManager {
    
    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(AutAgentManager.class);
    
    /**
     * <code>instance</code>single instance of AutAgentManager
     */
    private static AutAgentManager instance = null;    
    
    /**
     * <code>m_autAgents</code> all server read from preference store
     */
    private SortedSet<AutAgent> m_autAgents = new TreeSet<AutAgent>();
    
    /** last used AUT Agent object*/
    private AutAgent m_lastUsedAutAgent = null;
    
    /**
     * <p>The constructor.</p>
     * <p>Fills the list with all stored server settings.</p>
     * <p>If there are no stored values, the default values will filled in the list</p>
     */
    private AutAgentManager() {
        readFromPrefStore();
    }
    
    
    /**
     * @return single instance of AutAgentManager
     */
    public static AutAgentManager getInstance() {
        if (instance == null) {
            instance = new AutAgentManager();
        }
        return instance;
    }
    
    

    /**
     * 
     * Reads the pref storage to build the serverList.
     */
    private void readFromPrefStore() {

        IPreferenceStore prefStore = Plugin.getDefault().getPreferenceStore();
        String autAgentValue = 
                prefStore.getString(Constants.AUT_AGENT_SETTINGS_KEY);
        String lastUsedAutAgentValue = 
                prefStore.getString(Constants.LAST_USED_AUT_AGENT_KEY);
        
        try {
            decodeAutAgentPrefs(autAgentValue);
        } catch (JBException jbe) {
            LOG.error("Error occurred while loading AUT Agent preferences. Resetting to default values.", jbe); //$NON-NLS-1$
            prefStore.setToDefault(
                    Constants.AUT_AGENT_SETTINGS_KEY);
            try {
                decodeAutAgentPrefs(autAgentValue);
            } catch (JBException e) {
                LOG.error("Error occurred while reading AUT Agent preferences default values.", jbe); //$NON-NLS-1$
            }
        }
        
        // set last used server
        if (!StringUtils.isEmpty(lastUsedAutAgentValue)) {
            m_lastUsedAutAgent = new AutAgent(
                lastUsedAutAgentValue.substring(0, lastUsedAutAgentValue.indexOf(":")), //$NON-NLS-1$
                (new Integer(
                    lastUsedAutAgentValue.substring(lastUsedAutAgentValue.indexOf(":") + 1)))); //$NON-NLS-1$
        } else {
            m_lastUsedAutAgent = null;
        }

    }

    /**
     * load the server list from the preference store into m_autAgents
     * @param store string read from preference store
     * @throws JBException in case of problem with preference store
     */
    private void decodeAutAgentPrefs(String store) throws JBException {
        m_autAgents.clear();
        String[] autAgentStrings = StringUtils.split(store, ';');
        // We expect the length to be divisible by 2 (hostname;ports;)
        if (autAgentStrings.length % 2 == 0) {
            for (int i = 0; i < autAgentStrings.length; i += 2) {
                String hostname = decodeString(autAgentStrings[i]);

                // May be multiple ports. If so, then we create a server for each port.
                String[] encodedPorts = 
                        StringUtils.split(autAgentStrings[i + 1], ',');
                for (String encodedPort : encodedPorts) {
                    String port = decodeString(encodedPort);
                    m_autAgents.add(
                            new AutAgent(hostname, Integer.valueOf(port)));
                }
            }
            
        } else {
            throw new JBException("Number of entries in server list must be even.", Integer.valueOf(0)); //$NON-NLS-1$
        }

    }

    
    /**
     * @param encodedString A base64 encoded string.
     * @return the decoded string.
     * @throws JBException in case of not base64 encoded string
     */
    String decodeString(String encodedString) throws JBException {
        if (!Base64.isBase64(encodedString.getBytes())) {
            throw new JBException(StringConstants.EMPTY, new Integer(0));
        }
        return new String(Base64.decodeBase64(encodedString.getBytes()));
    }

    /**
     * Adds a server to the list.
     * @param autAgent The server to add.
     */
    public void addServer(AutAgent autAgent) {
        Validate.notNull(autAgent, Messages.ServerObjectMustNotBeNull 
                + StringConstants.DOT);
        if (!autAgent.getName().equals(StringConstants.EMPTY)
            && !m_autAgents.contains(autAgent)) {
            m_autAgents.add(autAgent);
        }
    }
    
    /**
     * Removes a server from the list.
     * @param autAgent The autAgent to remove.
     */
    public void removeAutAgent(AutAgent autAgent) {
        if (m_lastUsedAutAgent != null 
                && m_lastUsedAutAgent.equals(autAgent)) {
            m_lastUsedAutAgent = null;
        }
        m_autAgents.remove(autAgent);
    }
    
    
    /**
     * Stores the server list in the preferences.
     * Old format (base64-encoded):
     *  hostname1;port1,port2,...;hostname2;port1,port2,...;
     *  
     * Current Format (base64-encoded):
     *  hostname1;port;hostname2;port;
     */
    public void storeAutAgentList() {
        StringBuilder storage = new StringBuilder();
        for (AutAgent autAgent : m_autAgents) {
            // servername;port;
            byte[] autAgentArray = autAgent.getName().getBytes();
            String autAgentEncoded = new String(
                    Base64.encodeBase64(autAgentArray));
            storage.append(autAgentEncoded).append(";"); //$NON-NLS-1$
            storage.append(new String(Base64.encodeBase64(
                    autAgent.getPort().toString().getBytes())));
            storage.append(";"); //$NON-NLS-1$
        }
        IPreferenceStore preferenceStore = 
                Plugin.getDefault().getPreferenceStore();
        preferenceStore.setValue(
                Constants.AUT_AGENT_SETTINGS_KEY, storage.toString());
        if (m_lastUsedAutAgent != null) {
            if (m_autAgents.contains(m_lastUsedAutAgent)) {                
                preferenceStore.setValue(
                    Constants.LAST_USED_AUT_AGENT_KEY, 
                    buildLastUsedAutAgentPortString(m_lastUsedAutAgent));
            } else {
                m_lastUsedAutAgent = null;
            }
        }
    }

    /**
     * @param lastUsedAutAgent last used server as server object
     * @return last used server as string (servername:port)
     */
    private String buildLastUsedAutAgentPortString(AutAgent lastUsedAutAgent) {
        if (lastUsedAutAgent != null) {
            return lastUsedAutAgent.getName() + ":"  //$NON-NLS-1$
                + lastUsedAutAgent.getPort();
        } 
        Integer port = new Integer(-1);
        return StringConstants.EMPTY + ":" + port; //$NON-NLS-1$
    }
    
    
    
    
    /**
     * @param autAgentName The name of the wanted AUT Agent.
     * @param port port of wanted AUT Agent
     * @return The AUT Agent object for the given AUT Agent name.
     */
    public AutAgent getAutAgent(String autAgentName, Integer port) {
        AutAgent serv = null;
        for (AutAgent server : m_autAgents) {
            if (autAgentName.equals(server.getName())
                && server.getPort().equals(port)) {
                serv = server;
            }
        }
        return serv;
    }
    
    /**
     * validates, if a server name exists in server preferences
     * @param autAgentName name of server to validate
     * @return if server name exists in server preferences
     */
    public boolean containsAutAgent(String autAgentName) {
        Validate.notNull(autAgentName);
        for (AutAgent autAgent : m_autAgents) {
            if (autAgentName.equals(autAgent.getName())) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * @return all AUT Agent names
     */
    public SortedSet <String> getAutAgentNames() {
        SortedSet<String> autAgent = new TreeSet<String>();
        for (AutAgent server : m_autAgents) {
            if (!StringConstants.EMPTY.equals(server.getName())) {
                autAgent.add(server.getName());
            }
        }
        return autAgent;
    }
    
    /**
     * @return Returns the last used server, if available in Preference Store
     * or null.
     */
    public AutAgent getLastUsedAutAgent() {
        return m_lastUsedAutAgent;
    }

    /**
     * @return Returns the servers.
     */
    public SortedSet<AutAgent> getAutAgents() {
        return m_autAgents;
    }
    
    /**
     * @author BREDEX GmbH
     * @created 19.04.2006
     */
    public static class AutAgent implements Comparable {
        /**
         * <code>m_name</code>server name
         */
        private String m_name;
        /**
         * <code>m_port</code>port number
         */
        private Integer m_port = new Integer(-1);
        
        /**
         * @param name server name
         * @param port associated port
         */
        public AutAgent(String name, Integer port) {
            m_name = name;
            m_port = port;
        }

        /**
         * @return Returns the name.
         */
        public String getName() {
            return m_name;
        }

        /**
         * @return Returns the port.
         */
        public Integer getPort() {
            return m_port;
        }
        
        /**
         * @param name The name to set.
         */
        public void setName(String name) {
            m_name = name;
        }

        /**
         * @param port The port to set.
         */
        public void setPort(Integer port) {
            m_port = port;
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(Object o) {
            AutAgent autAgent = (AutAgent)o;
            if (this.getName().compareTo(autAgent.getName()) == 0) {
                return this.getPort().compareTo(autAgent.getPort());
            } 
            return this.getName().compareTo(autAgent.getName());           
        }

    }

    /**
     * @param autAgents The servers to set.
     */
    public void setAutAgents(SortedSet<AutAgent> autAgents) {
        m_autAgents = autAgents;
    }

    /**
     * @param autAgent last used server
     */
    public void setLastUsedAutAgent(AutAgent autAgent) {
        m_lastUsedAutAgent = autAgent;
    }
}
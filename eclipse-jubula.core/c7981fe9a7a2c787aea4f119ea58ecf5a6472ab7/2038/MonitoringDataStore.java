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
package org.eclipse.jubula.autagent.monitoring;
import java.util.HashMap;
import java.util.Map;

/**
 * This MonitoringDataStore is a singleton and can be instantiated in the 
 * AutAgent. The AUTConfigMap for each AUT will be saved separately in this
 * DataManager.
 *
 * @author BREDEX GmbH
 * @created 25.08.2010
 */
public class MonitoringDataStore {
    
    /** the static instance of the DataStore */
    private static MonitoringDataStore instance;

    /** The list for data storage */
    private Map<String, Map<String, String>> m_dataStoreMap = 
        new HashMap<String, Map<String, String>>();   
    
    /** The list off current monitoring agents */
    private Map<String, IMonitoring> m_monitoringAgentMap = 
        new HashMap<String, IMonitoring>();
    
    
        
    /** to prevent instantiation */
    private MonitoringDataStore() {
        //do nothing
    }
    
    /** this method will return the MonitoringDataStore instance 
     * @return The MonitoringDataStore instance*/
    public static synchronized MonitoringDataStore getInstance() {
        
        if (MonitoringDataStore.instance == null) {
            MonitoringDataStore.instance = new MonitoringDataStore();    
        }
        return MonitoringDataStore.instance;
    }
        
    /**
     * Adds a AutConfigMap to the MonitoringDataStore
     * @param autId The AutId
     * @param map The AutConfigMap for the given AutID
     */
    public synchronized void putConfigMap(
            String autId, Map<String, String> map) {   
        m_dataStoreMap.put(autId, map); 
    }
    /**
     * 
     * @param autId The autId 
     * @return The config map for the given autId
     */
    public Map<String, String> getConfigMap(String autId) {
        
        return m_dataStoreMap.get(autId);
    }    
    
    /**
     * 
     * @param autId The AutId 
     * @param monitoringInstance A IMonitoring instance
     */    
    public synchronized void putMonitoringAgent(String autId, 
            IMonitoring monitoringInstance) {
        
        m_monitoringAgentMap.put(autId, monitoringInstance);    
        
    }    
    /** 
     * @param autId The autId
     * @return A IMonitoring instance or null if there is no agent registered
     * for this autId
     */
    public synchronized IMonitoring getMonitoringAgent(String autId) {
        
        if (m_monitoringAgentMap.containsKey(autId)) {
            return m_monitoringAgentMap.get(autId);
        }
        return null;
        
    }
    /**
     * @param autId The stored DataObject to this autId will be removed
     */
    public synchronized void removeConfigMap(String autId) {

        if (m_dataStoreMap.containsKey(autId)) {
            m_dataStoreMap.remove(autId);
        }     
    }
    /**
     * removes a monitoring agent by a given autId
     * @param autId The autId 
     */
    public void removeAgent(String autId) {
    
        m_monitoringAgentMap.remove(autId);
    }
    
    /**
     * Deletes all referenced agents.
     */
    public void removeAllAgents() {
        
        m_monitoringAgentMap.clear();
    }
    /**
     * Deletes all saved config maps.
     */
    public void removeAllConfigMaps() {
        
        m_dataStoreMap.clear();
    }
    
    /** use this method to store data in the autConfigMap
     * @param key The key for the value
     * @param value The (String)data to store 
     * @param autId The AutID
     */
    public synchronized void putConfigValue(
            String autId, String key, String value) {
          
        m_dataStoreMap.get(autId).put(key, value);

    }
    /** use this method to get data from the MonitoringDataStore 
     *  @param key The key for the stored data
     *  @param autID the AutID 
     *  @return The String data for the key or null if key don't exists
     */
    public synchronized String getConfigValue(String autID, String key) {    
        
        if (m_dataStoreMap.containsKey(autID)) {
            return m_dataStoreMap.get(autID).get(key);
        }
        return null;
                
    }
    
    
}   

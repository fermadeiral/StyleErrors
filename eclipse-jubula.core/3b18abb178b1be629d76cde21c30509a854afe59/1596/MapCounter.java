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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple key => int counter
 * @author BREDEX GmbH
 * @created Aug. 11, 2016
 */
public class MapCounter {
    /** The counter map */
    private Map<String, Integer> m_counter;
    
    /** Constructor */
    public MapCounter() {
        m_counter = new HashMap<>();
    }
    
    /**
     * Adds amount to the value of the key
     * @param key the key
     * @param amount the amount
     */
    public void add(String key, int amount) {
        if (key == null) {
            return;
        }
        Integer current = m_counter.get(key);
        if (current == null) {
            current = 0;
        }
        m_counter.put(key, current + amount);
    }
    
    /**
     * Adds a MapCounter to the current one
     * @param toAdd the MapCounter to add
     */
    public void add(MapCounter toAdd) {
        for (String key : toAdd.getCounter().keySet()) {
            add(key, toAdd.getCounter().get(key));
        }
    }
    
    /**
     * Returns the counter map
     * @return the counter
     */
    public Map<String, Integer> getCounter() {
        return m_counter;
    }
    
    /**
     * Gets a value
     * @param key the key
     * @return the value
     */
    public Integer get(String key) {
        return m_counter.get(key);
    }
    
    /**
     * Puts a value
     * @param key the key
     * @param value the value
     */
    public void put(String key, Integer value) {
        m_counter.put(key, value);
    }
    
    /** Clears the counter */
    public void clear() {
        m_counter.clear();
    }
}
/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;

/**
 * 
 * @author BREDEX GmbH
 *
 * This is basically a Map used as a cache which supports 
 * invalidation based on predefined events.
 * 
 * @param <TKey> the type of the key component
 * @param <TValue> the type of the value component
 */
public class ControlledCache<TKey, TValue> implements IDataChangedListener,
        IProjectLoadedListener {
    
    /** List of data changed events supported for cache invalidation */
    public enum ControlTypes {
        /** project was (re)loaded */
        PROJECT_LOADED, 
        /** some data changed */
        DATA_CHANGED
    }
    
    /** cache storage */
    private Map<TKey, TValue> m_cache;
    
    /** 
     * @see ControlledCache#ControlledCache(long, int)
     * @param controlledBy the events which will invalidate the cache
     */
    public ControlledCache(ControlTypes... controlledBy) {
        this(17, controlledBy);
    }
    
    /**
     * @param controlledByList the events which will invalidate the cache
     * @param size the initial size of the Map
     */
    public ControlledCache(int size, ControlTypes... controlledByList) {
        m_cache = new HashMap<TKey, TValue>(size);
        
        for (ControlTypes controlledBy : controlledByList) {
            registerHandler(controlledBy);
        }
    }
    
    /**
     * store some data in the cache
     * @param key Key into the underlying map
     * @param value data to be stored
     * @return @see {@link Map}
     */
    public TValue add(TKey key, TValue value) {
        return m_cache.put(key, value);
    }
    
    /**
     * fetch data from the cache
     * @param key Key into the underlying map
     * @return @see {@link Map}
     */
    public TValue get(TKey key) {
        return m_cache.get(key);
    }
    
    /**
     * remove the data from the cache
     * @param key Key into the underlying map
     * @return @see {@link Map}
     */
    public TValue remove(TKey key) {
        return m_cache.remove(key);
    }
    
    /**
     * @return keyset of the cache
     */
    public Set<TKey> getKeySet() {
        return m_cache.keySet();
    }
    
    /**
     * register the event handler for this cache
     * @param controlledBy the events which will invalidate the cache
     */
    private void registerHandler(ControlTypes controlledBy) {
        if (controlledBy == ControlTypes.PROJECT_LOADED) {
            DataEventDispatcher.getInstance().addProjectLoadedListener(this,
                    true);
        }
        if (controlledBy == ControlTypes.DATA_CHANGED) {
            DataEventDispatcher.getInstance()
                    .addDataChangedListener(this, true);
        }
    }

    /**
     * @see org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener#handleProjectLoaded()
     */
    public void handleProjectLoaded() {
        m_cache.clear();
    }

    /**
     * @param events Possible events for this handler
     * @see org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener#handleDataChanged(org.eclipse.jubula.client.core.events.DataChangedEvent[])
     */
    public void handleDataChanged(DataChangedEvent... events) {
        m_cache.clear();
    }

}

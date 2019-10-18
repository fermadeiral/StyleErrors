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
package org.eclipse.jubula.client.ui.rcp.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** 
 * This class is necessary to displaying and handling the properties of AUT 
 * 
 * @author BREDEX GmbH */
public class AutPropertyManager {

    /** <code>instance</code>single instance of AutPropertyManager */
    private static AutPropertyManager instance = null;
    
    /** */
    private AutPropertyManager() { }

    /**
     * @return single instance of AutAgentManager
     */
    public static AutPropertyManager getInstance() {
        if (instance == null) {
            instance = new AutPropertyManager();
        }
        return instance;
    }

    /**
     * @param list of properties
     * @return map of properties
     */
    public static Map<String, String> convertPropertyListToMap(
            List<AutProperty> list) {
        Map<String, String> map = new HashMap<String, String>();
        for (AutProperty prop : list) {
            map.put(prop.getName(), prop.getValue());
        } 
        return map;
    }
    
    /**
     * @param map of properties
     * @return list of properties
     */
    public static List<AutProperty> convertProprtyMapToList(
            Map<String, String> map) {
        
        List<AutProperty> list = new ArrayList<AutProperty>();
        Iterator<Entry<String, String>> itr = map.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, String> propEntry = itr.next();
            AutProperty prop = new AutProperty();
            prop.setName(propEntry.getKey());
            prop.setValue(propEntry.getValue());
            list.add(prop);
        }
        return list;
    }

    /** @author BREDEX GmbH */
    public static class AutProperty {
        
        /** name of property */
        private String m_name = StringUtils.EMPTY;
        
        /** value of property  */
        private String m_value = StringUtils.EMPTY;
        
        /**
         * @return name of property 
         */
        public String getName() {
            return m_name;
        }
        
        /**
         * @param name set on property name 
         */
        public void setName(String name) {
            this.m_name = name;
        }
        
        /**
         * @return value of property
         */
        public String getValue() {
            return m_value;
        }
        
        /**
         * @param value set on property value
         */
        public void setValue(String value) {
            this.m_value = value;
        }
    }
    
    /** @author BREDEX GmbH */
    public static class AutPropertiesContentProvider 
        implements IStructuredContentProvider {
    
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object input) {
            return ((List<AutProperty>)input).toArray();
        }
        
        /**
         * {@inheritDoc}
         */
        public void dispose() {
            // Nothing to dispose
        }
        
        /**
         * {@inheritDoc}
         */
        public void inputChanged(
                Viewer viewer, Object oldInput, Object newInput) {
            
            // No listeners to deregister / register
        }
    }
}

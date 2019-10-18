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
package org.eclipse.jubula.client.core.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.status.ITimeStatus;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ValueSetElement;


/**
 * @author BREDEX GmbH
 * @created 21.10.2004
 */
public class StringHelper {
    /**
     * single instance from StringHelper
     */
    private static StringHelper instance = null;

    /** the currentToolKit */
    private static String toolkit = StringConstants.EMPTY;
    
    /** Pattern for Time as String **/
    private static final String TIME_PATTERN = "dd/MM/yy hh:mm:ss a"; //$NON-NLS-1$
    
    /**
     * Comment for <code>m_map</code>
     */
    private Map<String, String> m_map = null;
    
    

    /**
     * private constructor
     */
    private StringHelper() {
        initMap();
    }

    /**
     * getter for Singleton
     * @return the single instance
     */
    public static StringHelper getInstance() {
        // toolkit set to empty String for compatibility reasons (old projects)
        if (toolkit == null) {
            toolkit = StringConstants.EMPTY;
        }
        if (GeneralStorage.getInstance().getProject() != null 
                && !toolkit.equals(GeneralStorage.getInstance().getProject()
                    .getToolkit())) {
            
            instance = null;
            if (GeneralStorage.getInstance().getProject().getToolkit() 
                != null) {
                
                toolkit = GeneralStorage.getInstance().getProject()
                    .getToolkit();                
            } else {
        // toolkit set to "swing" for compatibility reasons (old projects)
                GeneralStorage.getInstance().getProject().setToolkit(
                    CommandConstants.SWING_TOOLKIT);
            }
        }
        if (instance == null) {
            instance = new StringHelper();
        }
        return instance;
    }

    /**
     * Initializes m_map with name strings from xml file and its translations.
     */
    private void initMap() {
        m_map = new HashMap<String, String>(10007);
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        for (Iterator itComp = compSystem.getComponents().iterator(); itComp
            .hasNext();) {
            Component component = (Component)itComp.next();
            String compType = component.getType();
            final List realizedTypes = component.getRealizedTypes();
            if (!component.isVisible() && !realizedTypes.isEmpty()) {
                String i18nType = realizedTypes.get(0).toString();
                if (!m_map.containsKey(compType)) {
                    String value = CompSystemI18n.getString(i18nType);
                    m_map.put(compType, value);
                    m_map.put(value, compType);
                }
            } else {
                if (!m_map.containsKey(compType)) {
                    String value = CompSystemI18n.getString(compType);
                    m_map.put(compType, value);
                    m_map.put(value, compType);
                }
            }
            final List<Action> actions = component.getActions();
            for (Action action : actions) {
                String actionName = action.getName();
                if (!m_map.containsKey(actionName)) {
                    m_map.put(actionName, CompSystemI18n.getString(actionName));
                }
                List<Param> params = action.getParams();
                for (Param param : params) {
                    String paramName = param.getName();
                    if (!m_map.containsKey(paramName)) {
                        String value = CompSystemI18n.getString(paramName);
                        m_map.put(paramName, value);
                        m_map.put(value, paramName);
                    }
                    String paramType = param.getType();
                    if (!m_map.containsKey(paramType)) {
                        String value = CompSystemI18n.getString(paramType);
                        m_map.put(paramType, value);
                        m_map.put(value, paramType);
                    }
                    Iterator iter = param.valueSetIterator();
                    while (iter.hasNext()) {
                        ValueSetElement vSet = (ValueSetElement)iter.next();
                        String paramValue = vSet.getValue();
                        // i18n only when default values are i18nable
                        if (!m_map.containsKey(paramValue)) {
                            String value = CompSystemI18n.getString(
                                paramValue, true);
                            m_map.put(paramValue, value);
                            m_map.put(value, paramValue);
                        }
                    }
                }
            }
        }
        for (Object o : compSystem.getEventTypes().keySet()) {
            String value = I18n.getString(o.toString());
            m_map.put(o.toString(), value);
            m_map.put(value, o.toString());
        }
    }

    /**
     * @return Returns the m_map.
     */
    public Map<String, String> getMap() {
        return m_map;
    }
    /**
     * Returns the string value to which the internal map maps the specified
     * key. If the key doesn't exist, and <code>fallback</code> is
     * <code>true</code>, the method returns the key as a fallback. If the
     * key doesn't exist and <code>fallback</code> is <code>false</code>,
     * the method returns <code>null</code>.
     * 
     * @param key
     *            The key.
     * @param fallBack
     *            If <code>true</code>, the method returns the key if it
     *            doesn't exist in the internal map.
     * @return The value to which this map maps the specified key,
     *         <code>null</code> or the key itself
     */
    public String get(String key, boolean fallBack) {
        String value = m_map.get(key);
        return value != null ? value : (fallBack ? key : value);
    }
    
    /**
     * Finds keys which will yield the specified value
     * @param value a text value
     * @return set of keys which will yield this value
     */
    public Set<String> reverseLookup(String value) {
        Set<String> res = new HashSet<String>();
        
        for (Entry<String, String> entry : m_map.entrySet()) {
            if (entry.getValue().equals(value)) {
                res.add(entry.getKey());
            }
        }
        return res;
    }

    /**
     * Returns the first key to which the value is mapped. If the key doesn't 
     * exist, and <code>fallback</code> is <code>true</code>, the method 
     * returns the value as a fallback. If the key doesn't exist and 
     * <code>fallback</code> is <code>false</code>, the method returns 
     * <code>null</code>.
     * 
     * @param value
     *            The value.
     * @param fallback
     *            If <code>true</code>, the method returns the value if the key
     *            doesn't exist in the internal map.
     * @return The key to which this value is mapped,
     *         <code>null</code> or the value itself
     */
    public String reverseLookupUnique(String value, boolean fallback) {
        Set<String> keys = reverseLookup(value);
        if (keys.size() > 0) {
            return keys.iterator().next();
        } else if (fallback) {
            return value;
        }
        
        return null;
    }
    
    /**
     * Gets a string representation of the given status, but this includes only
     * the severity and the message and if the Status is an ITimeStatus and
     * outputTime is true, a time stamp is also added. The pattern is yyyy-mm-dd
     * hh:mm:ss
     * 
     * @param s
     *            the status
     * @param outputTime
     *            add Timestamp
     * @return string representation
     */
    public static String getStringOf(IStatus s, boolean outputTime) {
        String result = ""; //$NON-NLS-1$
        switch (s.getSeverity()) {
            case IStatus.OK:
                result += "OK:"; //$NON-NLS-1$
                break;
            case IStatus.ERROR:
                result += "ERROR:"; //$NON-NLS-1$
                break;
            case IStatus.WARNING:
                result += "WARNING:"; //$NON-NLS-1$
                break;
            case IStatus.CANCEL:
                result += "CANCEL:"; //$NON-NLS-1$
                break;
            case IStatus.INFO:
                result += "INFO:"; //$NON-NLS-1$
                break;
            default:
                //Don't print out severity code
                break;
        }
        result += " " + s.getMessage(); //$NON-NLS-1$
        if (s instanceof ITimeStatus && outputTime) {
            result +=  " " + DateFormatUtils.format(((ITimeStatus)s).getTime(), //$NON-NLS-1$
                    TIME_PATTERN); 
        }
        return result;
    }
}

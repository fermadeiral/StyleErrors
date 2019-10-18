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
package org.eclipse.jubula.client.core.model;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * @author BREDEX GmbH
 * @created 20.12.2005
 */
public interface IAUTConfigPO extends IPersistentObject, Comparable {
    /** Activation method for window at TS start */
    public static enum ActivationMethod {
        /** No activation */
        NONE, 
        /** Click on titlebar */
        TITLEBAR, 
        /** Click in corner */
        NE, 
        /** Click in corner */
        NW, 
        /** Click in corner */
        SE, 
        /** Click in corner */
        SW, 
        /** click in the center of the window */
        CENTER;
        
        /**
         * @param method
         *            the activation method; may be <code>null</code>
         * @return a valid string which may be used in our rc components
         */
        public static String getRCString(ActivationMethod method) {
            ActivationMethod m = method;
            if (method == null) {
                m = ActivationMethod.NONE;
            }
            return m.name().toUpperCase();
        }

        /**
         * @param stringMethod
         *            a string representation of the activation method; may also
         *            be <code>null</code>
         * @return a valid string which may be used in our rc components
         */
        public static String getRCString(String stringMethod) {
            return getRCString(getEnum(stringMethod));
        }

        /**
         * @param stringMethod
         *            a string representation of the activation method; may also
         *            be <code>null</code>
         * @return a valid activation method; if string method conversion fails
         *         ActivationMethod.NONE is returned.
         */
        public static ActivationMethod getEnum(String stringMethod) {
            ActivationMethod m = NONE;
            if (StringUtils.isNotBlank(stringMethod)) {
                String name = StringUtils.trim(stringMethod).toUpperCase();
                try {
                    m = ActivationMethod.valueOf(name);
                } catch (IllegalArgumentException e) {
                    // ignore
                }
            }
            return m;
        }
    }
    
    /**
     * Gets a value of this AutConfig.
     * Keys are defined in {@link IAutConfigKeys}.
     * @param key an AutConfigKey enum.
     * @param defaultValue a defaut value to return if the given key is unknown.
     * @return the value of the given key.
     */
    public String getValue(String key, String defaultValue);
    
    /**
     * Sets the given value with the given key.
     * The Keys are defined in {@link IAutConfigKeys}.
     * @param key an AutConfigKey enum.
     * @param value the value to set.
     */
    public void setValue(String key, String value);

    /**
     * @return Returns the GUID.
     */
    public abstract String getGuid();

    /**
     * @return the AUT-Agent host name
     */
    public abstract String getConfiguredAUTAgentHostName();

    /**
     * @return a Set of all keys of the AutConfig.
     */
    public Set<String> getAutConfigKeys();
    
    /**
     * @return the Map<String, String> of the AUT configuration
     */
    public Map<String, String> getConfigMap();
   
    /**
     * @param config the Map<String, String> of the AUT configuration
     */
    public void setConfigMap(Map<String, String> config);
}
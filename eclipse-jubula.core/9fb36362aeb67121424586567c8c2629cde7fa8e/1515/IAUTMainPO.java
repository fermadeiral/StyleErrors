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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author BREDEX GmbH
 * @created 20.12.2005
 */
public interface IAUTMainPO extends IPersistentObject, Comparable {
    
    /** AUT possible properties*/
    public static enum Property {
        /** connection time*/
        TIME_OUT("timeout"); //$NON-NLS-1$
        
        /** value of property */
        private final String m_value;
        
        /**
         * @param value set on the value of property
         */
        private Property(String value) {
            this.m_value = value;
        }
        
        /**
         * @return value of property
         */
        public String getValue() {
            return m_value;
        }
        
        /**
         * @return value of property
         */
        @Override
        public String toString() {
            return m_value;
        }
    }
    
    /**
     * @return Returns the GUID.
     */
    public abstract String getGuid();

    /**
     * @return Returns the autName.
     */
    public abstract String getName();

    /**
     * @param autName
     *            The autName to set.
     */
    public abstract void setName(String autName);

    /**
     * @return Returns the autConfigSet.
     */
    public abstract Set<IAUTConfigPO> getAutConfigSet();

    /**
     * Adds a aut configuration to the list.
     * @param autConfig The aut configuration to add.
     */
    public abstract void addAutConfigToSet(IAUTConfigPO autConfig);

    /**
     * Removes a aut configuratio from the list.
     * @param autConfig The autConfig to remove.
     */
    public abstract void removeAutConfig(IAUTConfigPO autConfig);

    /**
     * @return Returns the objMap.
     */
    public abstract IObjectMappingPO getObjMap();

    /**
     * 
     * @param objMap The objMap to set.
     */
    public abstract void setObjMap(IObjectMappingPO objMap);

    /**
     * {@inheritDoc}
     */
    public abstract String toString();

    /**
     * @param toolkit the toolkit.
     */
    public abstract void setToolkit(String toolkit);
    
    /**
     * @return the toolkit of this AUT.
     */
    public abstract String getToolkit();
    
    /**
     * @param generateNames true, if AUT component names should be generated
     */
    public void setGenerateNames(boolean generateNames);

    /**
     * @return whether names for this AUT should be generated
     */
    public boolean isGenerateNames();
    
    /**
     * @return the AUT IDs associated with this AUT.
     */
    public List<String> getAutIds();

    /**
     * @return a Set of all keys of the property.
     */
    public Set<String> getPropertyKeys();
    
    /**
     * @return get AUT property key - value pars 
     */
    public abstract Map<String, String> getPropertyMap();
    
    /**
     * @param properties AUT property key - value pars 
     */
    public void setPropertyMap(Map<String, String> properties);
}
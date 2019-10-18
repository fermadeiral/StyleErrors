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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.collections.MapUtils;
import org.eclipse.jubula.client.core.persistence.PersistenceUtil;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;




/**
 * @author BREDEX GmbH
 * @created 19.07.2004
 */
@Entity
@Table(name = "AUT_CONF")
class AUTConfigPO implements IAUTConfigPO {
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
        
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;
    
    /** The values of the AutConfig */
    private Map<String, String> m_config = new HashMap<String, String>();

    /** Globally Unique Identifier for recognizing nodes across databases */
    private String m_guid = null;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /** default constructor */
    AUTConfigPO() {
        // Persistence (JPA / EclipseLink)
    }
    
    /** copy constructor
     * @param orig the original value 
     */
    AUTConfigPO(IAUTConfigPO orig) {
        this(PersistenceUtil.generateUUID());
        
        duplicateFrom(orig);
    }
    /** 
     * Constructor with GUID
     * @param guid The GUID of this AUT Config.
     */
    AUTConfigPO(String guid) {
        m_guid = guid;
    }

    /**
     * Copy parameters from original to duplicate. ID's and GUID's are 
     * not copied.
     * @param orig The original configuration
     */
    private void duplicateFrom(IAUTConfigPO orig) {
        this.setConfigMap(new HashMap<String, String>(orig.getConfigMap()));
        this.setParentProjectId(orig.getParentProjectId());        
    }
    
    /**
     * Gets a value of this AutConfig. Keys are defined in
     * {@link IAutConfigKeys}.<br>
     * If the given key does not exists, it returns an empty String!
     * 
     * @param key
     *            an AutConfigKey enum.
     * @param defaultValue
     *            a defaut value to return if the given key is unknown.
     * @return the value of the given key.
     */
    public String getValue(String key, String defaultValue) {
        return MapUtils.getString(getHbmConfigMap(), key, defaultValue);
    }
    
    /**
     * Sets the given value with the given key.
     * The Keys are defined in {@link IAutConfigKeys}.
     * @param key an AutConfigKey enum.
     * @param value the value to set.
     */
    public void setValue(String key, String value) {
        if (value == null || value.length() == 0) {
            getHbmConfigMap().remove(key);
        } else {
            getHbmConfigMap().put(key, value);
        }
    }
    
    /**
     * @return Returns the aut config name.
     */
    @Transient
    public String getName() {
        return getHbmConfigMap().get(AutConfigConstants.AUT_CONFIG_NAME);
    }
   
    /**
     * only for Persistence (JPA / EclipseLink)
     * 
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    
    /**
     * only for Persistence (JPA / EclipseLink)
     * 
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
    }

    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * {@inheritDoc}
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /** 
     * {@inheritDoc}
     */
    @Version
    public Integer getVersion() {        
        return m_version;
    }

    /** 
     * {@inheritDoc}
     */
    void setVersion(Integer version) {
        m_version = version;
    }


    /**
     * Checks the equality of the given Object with this Object.
     * {@inheritDoc}
     * @param obj the object to check
     * @return if there is a database ID it returns true if the ID is equal.
     * If there is no ID it will be compared to identity.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AUTConfigPO  || obj instanceof IAUTConfigPO)) {
            return false;
        }
        IAUTConfigPO o = (IAUTConfigPO)obj;

        return getGuid().equals(o.getGuid());
    }
    
    /**
     * 
     * {@inheritDoc}
     * @return
     */
    public int hashCode() {
        return getGuid().hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public int compareTo(Object o) {
        IAUTConfigPO conf = (IAUTConfigPO)o;
        return this.getName().compareTo(conf.getName());
    }


    /**
     * only for Persistence (JPA / EclipseLink)!
     * 
     * @return the config
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "AUT_CONF_ATTR", 
                     joinColumns = @JoinColumn(name = "AUT_CONF"))
    @MapKeyColumn(name = "ATTR_KEY")
    @Column(name = "ATTR_VALUE", length = MAX_STRING_LENGTH)
    Map<String, String> getHbmConfigMap() {
        return m_config;
    }

    /**
     * @return the Map<String, String> of the aut configuration
     */
    @Transient
    public Map<String, String> getConfigMap() {
        return getHbmConfigMap();
    }
    
    /**
     * @param config the Map<String, String> of the aut configuration
     */
    public void setConfigMap(Map<String, String> config) {
        setHbmConfigMap(config);
    }
    
    /**
     * @return a Set of all keys of the AutConfig.
     */
    @Transient
    public Set<String> getAutConfigKeys() {
        return getHbmConfigMap().keySet();
    }

    /**
     * only for Persistence (JPA / EclipseLink)!
     * 
     * @param config the config to set
     */
    void setHbmConfigMap(Map<String, String> config) {
        m_config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getConfiguredAUTAgentHostName() {
        return getValue(AutConfigConstants.AUT_CONFIG_AUT_HOST_NAME, 
            StringConstants.EMPTY);
    }

    /**
     * 
     * @return the GUID.
     */
    @Basic
    @Column(name = "GUID")
    public String getGuid() {
        return m_guid;
    }

    /**
     * @param guid The new guid.
     */
    void setGuid(String guid) {
        m_guid = guid;
    }

}
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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * @author BREDEX GmbH
 * @created Nov 17, 2010
 */
@Entity
@Table(name = "CHECK_CONF_CONT")
class CheckConfContPO implements ICheckConfContPO {
    
    /** is teststyle enabled? */
    private boolean m_enabled = true;

    /** map of the checkconf and the checkid */
    private Map<String, CheckConfPO> m_confMap = 
        new HashMap<String, CheckConfPO>();

    /** Persistence (JPA / EclipseLink) version */
    private transient Integer m_version;

    /** Persistence (JPA / EclipseLink) id*/
    private transient Long m_id;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /**
     * 
     * {@inheritDoc}
     */
    @Version
    public Integer getVersion() {
        return m_version;
    }

    /**
     * @param version version
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }

    /**
     * {@inheritDoc}
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "CHECK_CONF_CONT_MAP")
    @MapKeyColumn(name = "CHECK_CONF_KEY", nullable = false, 
                  table = "CHECK_CONF_CONT_MAP")
    public Map<String, CheckConfPO> getConfMap() {
        return m_confMap;
    }
    
    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "ENABLED")
    public boolean getEnabled() {
        return m_enabled;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setEnabled(boolean enabled) {
        m_enabled = enabled;
    }
    

    /**
     * @param confMap
     *            the confMap to set
     */
    public void setConfMap(Map<String, CheckConfPO> confMap) {
        m_confMap = confMap;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return toString();
    }

    /**
     * {@inheritDoc}
     */
    public void addCheckConf(String chkId, ICheckConfPO cfg) {
        m_confMap.put(chkId, (CheckConfPO)cfg);
    }

    /**
     * {@inheritDoc}
     */
    public CheckConfPO getCheckConf(String chkId) {
        return m_confMap.get(chkId);
    }

    /**
     * {@inheritDoc}
     */
    public ICheckConfPO createCheckConf() {
        return new CheckConfPO();
    }
    
    /**
     * {@inheritDoc}
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
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
     * 
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * 
     * {@inheritDoc}
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }
}

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

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * @author BREDEX GmbH
 * @created Nov 16, 2010
 */
@Entity
@Table(name = "CHECK_CONF")
class CheckConfPO implements ICheckConfPO {

    /** id for this configuration */
    private Long m_id;
    /** version */
    private Integer m_version;
    
    /** is this check active? */
    private Boolean m_active = false;
    
    /** what is the severity of this check */
    private String m_severity = "INFO"; //$NON-NLS-1$
    
    /** What are its attributes */
    private Map<String, String> m_attr = new HashMap<String, String>();
    
    /** What are its active contexts */
    private Map<String, Boolean> m_contexts = new HashMap<String, Boolean>();
    
    /**
     * {@inheritDoc}
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        m_id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Version
    public Integer getVersion() {
        return m_version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        m_version = version;
    }
    
    /**
     * {@inheritDoc}
     */
    public Boolean isActive() {
        return m_active;
    }
    
    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        m_active = active;
    }
    
    /**
     * @return the severity
     */
    public String getSeverity() {
        return m_severity;
    }
    
    /**
     * @param severity the severity to set
     */
    public void setSeverity(String severity) {
        m_severity = severity;
    }
    
    /**
     * @return the attributes
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "CHECK_CONF_ATTRIBUTES")
    public Map<String, String> getAttr() {
        return m_attr;
    }
    
    /**
     * @param attributes the attributes to set
     */
    public void setAttr(Map<String, String> attributes) {
        m_attr = attributes;
    }
    
    /**
     * @return the contexts
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "CHECK_CONF_CONTEXTS")
    public Map<String, Boolean> getContexts() {
        return m_contexts;
    }
    
    /**
     * @param contexts the contexts to set
     */
    public void setContexts(Map<String, Boolean> contexts) {
        m_contexts = contexts;
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
    @Transient
    public Long getParentProjectId() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        // not needed
    }
    
}

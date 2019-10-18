/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.jubula.client.core.utils.ReportRuleType;

/**
 * @author BREDEX GmbH
 * @created Jul 09, 2014
 */
@Entity
@Table(name = "ALM_REPORTING_RULE")
public class ALMReportingRulePO implements IALMReportingRulePO {
    
    /** the name */
    private String m_name;
    
    /** the fieldID */
    private String m_fieldID;
    
    /** the value */
    private String m_value;

    /** id of the rule */
    private Long m_id;

    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /** type of the rule */
    private ReportRuleType m_type;
    
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version;
    
    /** 
     * default constructor 
     * only for Persistence (JPA / EclipseLink)
     */
    public ALMReportingRulePO() {
        // only for Persistence (JPA / EclipseLink)
    }
    
    /** constructor 
     * @param name the name of the rule
     * @param fieldID the fieldID
     * @param value the value
     * @param type the type
     */
    public ALMReportingRulePO(String name, String fieldID, String value,
            ReportRuleType type) {
        this.m_name = name;
        this.m_fieldID = fieldID;
        this.m_value = value;
        this.m_type = type;
    }
    
    /**
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    
    /**
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "NAME")
    public String getName() {
        return m_name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "FIELD_ID")
    public String getAttributeID() {
        return m_fieldID;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttributeID(String fieldID) {
        m_fieldID = fieldID;
    }
    
    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "VALUE")
    public String getValue() {
        return m_value;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String value) {
        m_value = value;
    }

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
     *    
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    public Long getParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Object o) {
        ALMReportingRulePO rule = (ALMReportingRulePO)o;
        return this.getName().compareTo(rule.getName());
    }
    
    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "TYPE")
    public ReportRuleType getType() {
        return m_type;
    }

    /**
     * {@inheritDoc}
     */
    public void setType(ReportRuleType type) {
        m_type = type;
    }
    
    /** {@inheritDoc} */
    public IALMReportingRulePO copy() {
        return new ALMReportingRulePO(
                getName(), getAttributeID(), 
                getValue(), getType());
    }
}

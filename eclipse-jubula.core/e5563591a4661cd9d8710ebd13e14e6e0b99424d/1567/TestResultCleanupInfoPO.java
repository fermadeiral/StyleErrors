/*******************************************************************************
 * Copyright (c) 2018 BREDEX GmbH.
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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Index;

/**
 * @author BREDEX GmbH
 * This class keeps track if a cleanup of testresult details has already been started.
 */
@Entity
@Table(name = "TESTRESULT_CLEANUP_INFO")
class TestResultCleanupInfoPO implements ITestResultCleanupInfoPO {

    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;
    /** The timestamp */
    private long m_timestamp = 0;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;
    
    /**
     * 
     * @param parentProjectId the parent project id
     */
    protected TestResultCleanupInfoPO(long parentProjectId) {
        setParentProjectId(parentProjectId);
        setTimestamp(System.currentTimeMillis());
    }
    /**
     * 
     */
    TestResultCleanupInfoPO() {
        // for jpa
    }
    /**
     * {@inheritDoc}
     */
    @Version
    @Column(name = "VERSION")
    public Integer getVersion() {
        return m_version;
    }
    
    /**
     * @param version the version
     */
    void setVersion(Integer version) {
        m_version = version;
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
     * @param id the id
     */
    void setId(Long id) {
        m_id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return "TestResult Cleanup Information"; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ", nullable = false, unique = true)
    @Index(name = "PI_NODE_PARENT_PROJ")
    public Long getParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
        
    }

    /**
     * {@inheritDoc}
     */
    public void setTimestamp(long timestamp) {
        m_timestamp = timestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "TIMESTAMP", nullable = false)
    public long getTimestamp() {
        return m_timestamp;
    }

}

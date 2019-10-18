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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

/**
 * @author BREDEX GmbH
 * @created Jun 11, 2007
 */
@Entity
@Table(name = "AUT_CONT")
class AUTContPO implements IAUTContPO {
    /**
     * <code>DEFAULT_NUMBER_OF_AUTS</code> the default number of AUTs to hold
     */
    public static final int DEFAULT_NUMBER_OF_AUTS = 2;
    
    /** the list of AUTs, that belong to a project */ 
    private Set<IAUTMainPO> m_autMainList = 
        new HashSet<IAUTMainPO>(DEFAULT_NUMBER_OF_AUTS);

    /** Persistence (JPA / EclipseLink) version */
    private transient Integer m_version;
    
    /** Persistence (JPA / EclipseLink) id*/
    private transient Long m_id;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;
    
    /**
     * Persistence (JPA / EclipseLink) constructor
     */
    AUTContPO() {
        // only for Persistence (JPA / EclipseLink)
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
     * @return Returns the autMainList.
     */
    @OneToMany(cascade = CascadeType.ALL, 
               fetch = FetchType.EAGER, 
               targetEntity = AUTMainPO.class,
               orphanRemoval = true)
    @JoinColumn(name = "FK_AUT_CONT")
    @BatchFetch(value = BatchFetchType.JOIN)
    public Set<IAUTMainPO> getAutMainList() {
        return m_autMainList;
    }
    /**
     * only for Persistence (JPA / EclipseLink)
     * @param autMainSet The autMainList to set.
     */
    void setAutMainList(Set<IAUTMainPO> autMainSet) {
        m_autMainList = autMainSet;
    }
    /**
     * Adds an AUT to a project.
     * @param aut The AUT to add.
     */
    public void addAUTMain(IAUTMainPO aut) {
        getAutMainList().add(aut);
        aut.setParentProjectId(getParentProjectId());
    }
    /**
     * Removes an AUT from this container.
     * @param aut The AUT to remove.
     */
    public void removeAUTMain(IAUTMainPO aut) {
        getAutMainList().remove(aut);
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
        for (IAUTMainPO aut : getAutMainList()) {
            aut.setParentProjectId(projectId);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return "AUTContPO"; //$NON-NLS-1$
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
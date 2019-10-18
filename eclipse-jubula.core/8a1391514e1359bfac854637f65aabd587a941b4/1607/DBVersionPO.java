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

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.jubula.client.core.i18n.Messages;

/**
 * read only class to manage consistency between application version and (update)
 * state of database
 * hint: this class will not be serialized within the scope of a project import
 *      
 * @author BREDEX GmbH
 * @created 27.10.2005
 */
@Entity
@Table(name = "DB_VERSION")
public class DBVersionPO implements IPersistentObject {

    /** Persistence (JPA / EclipseLink) id */
    private transient Long m_id;
    
    /** version of this in db*/
    private transient Integer m_version;
    
    /**
     * <code>m_minorVersion</code> minor version for db state
     */
    private transient Integer m_minorVersion = null;
    
    
    /**
     * <code>m_majorVersion</code> major version for db state
     */
    private transient Integer m_majorVersion = null;
    
    /**
     * only for Persistence (JPA / EclipseLink)
     */
    public DBVersionPO() {
        // nothing
    }
    
    /**
     * @param majorVersion major Version for DB
     * @param minorVersion minor Version for DB
     */
    public DBVersionPO(Integer majorVersion, Integer minorVersion) {
        setMajorVersion(majorVersion);
        setMinorVersion(minorVersion);
    }

    /**
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
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return "DB-Version"; //$NON-NLS-1$
    }

    /**
     * 
     * @return Returns the majorVersion.
     */
    @Basic
    public Integer getMajorVersion() {
        return m_majorVersion;
    }

    /**
     * only for Persistence (JPA / EclipseLink)
     * @param majorVersion The majorVersion to set.
     */
    public void setMajorVersion(Integer majorVersion) {
        m_majorVersion = majorVersion;
    }

    /**
     * 
     * @return Returns the minorVersion.
     */
    @Basic
    public Integer getMinorVersion() {
        return m_minorVersion;
    }

    /**
     * only for Persistence (JPA / EclipseLink)
     * @param minorVersion The minorVersion to set.
     */
    public void setMinorVersion(Integer minorVersion) {
        m_minorVersion = minorVersion;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        m_id = id;
    }

    /**
     * 
     * @return nothing. Instead, throws an <code>UnsupportedOperationException</code>
     *         since DBVersion does not have a parent project
     * @throws UnsupportedOperationException always
     */
    @Transient
    public Long getParentProjectId() 
        throws UnsupportedOperationException {

        throw new UnsupportedOperationException(
            Messages.DBVersionDoesNotHaveAParentProject);
    }

    /**
     * Throws an <code>UnsupportedOperationException</code> because DBVersion
     * does not have a parent project.
     * @param projectId The ID of a project. This parameter is ignored.
     * @throws UnsupportedOperationException always
     */
    public void setParentProjectId(Long projectId) 
        throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                Messages.DBVersionDoesNotHaveAParentProject);
    }
}

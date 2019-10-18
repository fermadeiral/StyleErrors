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
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author BREDEX GmbH
 * @created 25.06.2007
 */
@MappedSuperclass
abstract class AbstractGuidNamePO implements IAbstractGUIDNamePO {
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;

    /** The GUID associated with this name */
    private String m_guid = null;
    /** The name */
    private String m_name = null;
    
    /**
     * For Persistence (JPA / EclipseLink)
     */
    AbstractGuidNamePO() {
        // only for Persistence (JPA / EclipseLink)
    }
    
    /**
     * 
     * @param guid The GUID
     * @param name The name of the object
     */
    public AbstractGuidNamePO(String guid, String name) {
        setHbmGuid(guid);
        setName(name);
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
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return getHbmName();
    }

    /**
     * This method is not used, as this PO does not have a parent project.
     * @param projectId ignored
     */
    public void setParentProjectId(Long projectId) {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getGuid() {
        return getHbmGuid();
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String newName) {
        Validate.notEmpty(newName);
        setHbmName(newName);
    }

    /**
     * 
     * @return the name of the object
     */
    @Basic(optional = false)
    @Column(name = "NAME")
    String getHbmName() {
        return m_name;
    }

    /**
     * For Persistence (JPA / EclipseLink)
     * Sets the value of the m_name property.
     * 
     * @param name
     *            the new value of the m_name property
     */
    void setHbmName(String name) {
        m_name = name;
    }

    /**
     * 
     * @return The GUID associated with this name.
     */
    @Basic
    @Column(name = "GUID", unique = true)
    String getHbmGuid() {
        return m_guid;
    }
    
    /**
     * For Persistence (JPA / EclipseLink)
     * 
     * @param guid The new Guid
     */
    void setHbmGuid(String guid) {
        m_guid = guid;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IProjectNamePO)) {
            return false;
        }
        AbstractGuidNamePO otherName = (AbstractGuidNamePO)obj;
        return new EqualsBuilder()
            .append(getHbmGuid(), otherName.getHbmGuid())
            .append(getHbmName(), otherName.getHbmName())
            .isEquals();
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder()
            .append(getHbmGuid())
            .append(getHbmName())
            .toHashCode();
    }
}

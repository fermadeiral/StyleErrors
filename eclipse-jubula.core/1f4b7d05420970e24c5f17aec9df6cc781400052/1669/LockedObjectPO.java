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
package org.eclipse.jubula.client.core.persistence.locking;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.eclipse.jubula.client.core.model.IPersistentObject;



/**
 * @author BREDEX GmbH
 * @created Apr 21, 2008
 */
@Entity
@Table(name = "LOCKED_OBJECTS")
public class LockedObjectPO implements IPersistentObject {

    
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    
    /** the name of the object table to lock. */
    private String m_objectName = null;
   
   
    /**
     * Only for Persistence (JPA / EclipseLink)!
     */
    LockedObjectPO() {
        // for Persistence (JPA / EclipseLink)
    }
    
    /**
     * 
     * @param objectName the name of the object table to lock.
     */
    public LockedObjectPO(String objectName) {
        setHbmObjectName(objectName);
    }
    
    /**
     * @return Returns the id.
     */
    @Id
    @TableGenerator(name = "LOCKED_OBJ_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, 
                    generator = "LOCKED_OBJ_SEQ")
    public Long getId() {
        return m_id;
    }

    /**
     * 
     * @param id id The id to set.
     */
    public void setId(Long id) {
        m_id = id;
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return getHbmObjectName();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return Long.valueOf(-1L);
    }

    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "VERSION")
    public Integer getVersion() {
        return Integer.valueOf(0);
    }

    /**
     * @param version The version to set.
     */
    void setVersion(Integer version) {
        // nothing here, getVersion returns a constant value.
    }
    
    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        // nothing
    }

    /**
     * @return the lockName
     */
    @Basic
    @Column(name = "OBJECT_NAME")
    String getHbmObjectName() {
        return m_objectName;
    }

    /**
     * @param lockName the lockName to set
     */
    void setHbmObjectName(String lockName) {
        m_objectName = lockName;
    }
    
   
    
   
}

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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;


/**
 * @author BREDEX GmbH
 * @created 30.11.2005
 */
@Entity
@Table(name = "DB_LOCKS")
public class DbLockPO {
    
    /** Persistence (JPA / EclipseLink) OID */
    private Long m_id = null;
    
    /** Which application does this lock belongs to? */
    private ApplicationPO m_application = null;
    
    /** Which session should the lock support */
    private Integer m_sessionId = null;
    
    /** Id of the PO which is to be locked */
    private Long m_poId = null;

    /** internal construcotr for Persistence (JPA / EclipseLink) */
    DbLockPO() {
        // Persistence (JPA / EclipseLink) use
    }
    
    /**
     * Instantiate a db lock for a PO using a specific session.
     * 
     * @param appl Which application instance is running?
     * @param sess Session for which the PO should be locked.
     * @param id the id of the PO to be locked
     */
    public DbLockPO(ApplicationPO appl, EntityManager sess, 
            Long id) {
        setApplication(appl);
        setSessionId(System.identityHashCode(sess));
        setPoId(id);
    }
    /**
     * @return Returns the id.
     */
    @Id
    @TableGenerator(name = "DB_LOCK_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "DB_LOCK_SEQ")
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
     * @return Returns the application.
     */
    @ManyToOne(targetEntity = ApplicationPO.class, fetch = FetchType.EAGER,
            cascade = CascadeType.MERGE)
    @JoinColumn(name = "FK_APPLICATION")
    public ApplicationPO getApplication() {
        return m_application;
    }

    /**
     * @param application The application to set.
     */
    void setApplication(ApplicationPO application) {
        m_application = application;
    }

    /**
     * @return Returns the sessionId.
     */
    @Basic
    public Integer getSessionId() {
        return m_sessionId;
    }

    /**
     * @param sessionId The sessionId to set.
     */
    void setSessionId(Integer sessionId) {
        m_sessionId = sessionId;
    }

    /**
     * @return Returns the poId.
     */
    @Basic
    @Column(name = "PO_ID", unique = true)
    public Long getPoId() {
        return m_poId;
    }

    /**
     * @param poId The poId to set.
     */
    void setPoId(Long poId) {
        m_poId = poId;
    }


}

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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class will act as a guardian for the locking subsystem. There should 
 * only be one column in the table and this column should be used to apply
 * a lock for all subsequent action on the locking tables.
 *
 * @author BREDEX GmbH
 * @created 30.11.2005
 */
@Entity
@Table(name = "DB_GUARD")
public class DbGuardPO {
    
    /** Persistence (JPA / EclipseLink) OID */
    private Long m_id = null;

    /**
     * No instance of this class should ever be created in the application. It
     * is the responsibility of the db create script to provide an instance
     * with an ID of 1.
     */
    DbGuardPO() {
        // only for Persistence (JPA / EclipseLink)
    }
    
    /**
     * 
     * @return Returns the id.
     */
    @Id
    public Long getId() {
        return m_id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        m_id = id;
    }
    

}

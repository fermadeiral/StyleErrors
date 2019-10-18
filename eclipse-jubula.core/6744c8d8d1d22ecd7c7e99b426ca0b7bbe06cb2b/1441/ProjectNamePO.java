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

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


/**
 * Represents the one-to-one mapping of GUID to Project Name.
 * 
 * @author BREDEX GmbH
 * @created Jun 20, 2007
 */
@Entity
@Table(name = "PROJECT_NAMES")
@AttributeOverride(name = "hbmName", 
                   column = @Column(name = "NAME", unique = true))
class ProjectNamePO extends AbstractGuidNamePO 
    implements IProjectNamePO {

    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version;

    /**
     * default constructor
     */
    public ProjectNamePO() {
        super();
    }
    
    /**
     * @param guid of the project
     * @param name of the project
     */
    public ProjectNamePO(String guid, String name) {
        super(guid, name);
    }

    /**
     * This method is not used, as this PO does not have a parent project.
     * @return null
     */
    @Transient
    public Long getParentProjectId() {
        return null;
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
     * {@inheritDoc}
     */
    void setVersion(Integer version) {
        m_version = version;
    }

}

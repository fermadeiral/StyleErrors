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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;


/**
 * Represents the one-to-one mapping of GUID to Parameter Name.
 * only to use for parameter of Testcases 
 * Caps use the keys of the ComponentConfig.xml
 *
 * @author BREDEX GmbH
 * @created 25.06.2007
 */
@Entity
@Table(name = "PARAM_NAMES", 
       uniqueConstraints = @UniqueConstraint(
               columnNames = { "GUID", "PARENT_PROJ" }))
// "@AttributeOverride" is used here in order to remove the unique constraint 
// on "hbmGuid" that is defined in the superclass
@AttributeOverride(name = "hbmGuid", column = @Column(name = "GUID"))
class ParamNamePO extends AbstractGuidNamePO implements IParamNamePO {
    
    /**
     * <code>m_parentProjectId</code>id of associated project
     */
    private Long m_parentProjectId = null;

    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version;

    /**
     * default constructor
     */
    ParamNamePO() {
        super();
    }
    
    
    /**
     * use this constructor for parameter already have a guid
     * 
     * @param name name of parameter
     * @param guid guid of parameter
     */
    ParamNamePO(String guid, String name) {
        super(guid, name);
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
     * {@inheritDoc}
     */
    public void setParentProjectId(Long id) {
        m_parentProjectId = id;
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

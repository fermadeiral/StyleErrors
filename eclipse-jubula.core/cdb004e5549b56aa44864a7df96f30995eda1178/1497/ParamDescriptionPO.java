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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.persistence.annotations.Index;


/**
 * utility class for package of parameter description
 * 
 * @author BREDEX GmbH
 * @created 30.11.2004
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CLASS_ID", 
                     discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue(value = "P")
@Index(name = "PARAM_NODE_IDX", columnNames = "PARAM_NODE")
@Table(name = "PARAM_DESC")
abstract class ParamDescriptionPO {
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    
    /** global unique id or I18N-Key, managed by Jubula client */
    private String m_uniqueId = null;
    
    /**
     * <code>m_type</code>: type of parameter
     */
    private String m_type = null;
    
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /** Persistence (JPA / EclipseLink) constructor
     * 
     */
    ParamDescriptionPO() {
        // Persistence (JPA / EclipseLink)
    }
     
    
    /**
     * @param type parameter type
     * @param uniqueId GUID or I18NKey for parameter
     */
    protected ParamDescriptionPO(String type, String uniqueId) {
        setType(type);
        setUniqueId(uniqueId);
    }
    


    /**
     * 
     * @return Returns the type.
     */
    @Basic
    public String getType() {
        return m_type;
    }
    
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        Validate.notEmpty(Messages.MissingParameterType, type);
        m_type = type;
    }

    /**
     * 
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
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
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
     * Returns the displayable name for the receiver.
     * @return a name that can be shown to the user.
     */
    @Transient
    public abstract String getName();
    
    /**
     * 
     * @return the guid
     */
    @Basic
    @Column(name = "UNIQUE_ID")
    public String getUniqueId() {
        return m_uniqueId;
    }


    /**
     * @param uniqueId the guid to set
     */
    public void setUniqueId(String uniqueId) {
        m_uniqueId = uniqueId;
    }
    
    /**
     * Checks the equality of the given Object with this Object.
     * {@inheritDoc}
     * @param obj the object to check
     * @return if there is a database ID it returns true if the ID is equal.
     * If there is no ID it will be compared to identity.
     */
    public boolean equals(Object obj) { // NOPMD
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ParamDescriptionPO  
            || obj instanceof IParamDescriptionPO)) {
            return false;
        }
        IParamDescriptionPO o = (IParamDescriptionPO)obj;
        if (getUniqueId() != null) {
            return getUniqueId().equals(o.getUniqueId());
        }
        return super.equals(obj);
    }
    
    /**
     * computes the hashCode
     * @return the hashCode for object
     */
    public int hashCode() {
        if (getUniqueId() != null) {
            return getUniqueId().hashCode();
        }
        return super.hashCode();
    }
    

}

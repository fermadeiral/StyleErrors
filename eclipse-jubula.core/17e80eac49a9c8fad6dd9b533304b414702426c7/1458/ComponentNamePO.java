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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;

/**
 * Represents the one-to-one mapping of GUID to logical Component Name.
 * 
 * @author BREDEX GmbH
 * @created Apr 7, 2008
 * 
 */
@Entity
@Table(name = "COMPONENT_NAMES", 
       uniqueConstraints = @UniqueConstraint(columnNames = 
           { "PARENT_PROJ",  "GUID" }))
// "@AttributeOverride" is used here in order to remove the unique constraint 
// on "hbmGuid" that is defined in the superclass
@AttributeOverride(name = "hbmGuid", column = @Column(name = "GUID"))
class ComponentNamePO extends AbstractGuidNamePO 
    implements IComponentNamePO {

    /** The ID of the depending Project */
    private Long m_parentProjectId = null;
    
    /** The transient component type */
    private transient String m_componentType = null;
    
    /** The referenced GUID */
    private String m_referencedGuid = null;
    
    /** The context of creation */
    private String m_creationContext = null;

    /** version of this in db*/
    private transient Integer m_version;
    
    /** Type problem if there is any */
    private transient IProblem m_typeProblem;
    
    /** The calculated usage type of the CN */
    private transient String m_usageType;
    
    /**
     * Default Constructor. Only for Persistence (JPA / EclipseLink)!
     */
    ComponentNamePO() {
     // only for Persistence (JPA / EclipseLink)
    }
    
    /**
     * Constructor.
     * @param guid the GUID
     * @param name the logical Component Name.
     * @param type the Component Type.
     * @param creationContext the context of creation.
     */
    ComponentNamePO(String guid, String name, String type, 
            CompNameCreationContext creationContext) {
        
        setHbmGuid(guid);
        setHbmName(name);
        setHbmCreationContext(creationContext.toString());
        setComponentType(type);
    }
    
    /**
     * Clones a ComponentNamePO object
     * @param toClone the object to clone
     */
    ComponentNamePO(ComponentNamePO toClone) {
        setHbmGuid(toClone.getHbmGuid());
        setHbmName(toClone.getHbmName());
        m_parentProjectId = toClone.getHbmParentProjectId();
        m_referencedGuid = toClone.getHbmReferencedGuid();
        m_creationContext = toClone.getHbmCreationContext();
        m_version = toClone.getVersion();
        m_typeProblem = toClone.getTypeProblem();
        m_usageType = toClone.getUsageType();
        setId(toClone.getId());
    }

    /**
     * 
     * @return the referencedGuid
     */
    @Basic
    @Column(name = "REF_GUID")
    String getHbmReferencedGuid() {
        return m_referencedGuid;
    }

    /**
     * 
     * @param referencedGuid the referencedGuid to set
     */
    void setHbmReferencedGuid(String referencedGuid) {
        m_referencedGuid = referencedGuid;
    }
    
    /**
     * @return the componentType
     */
    @Transient
    public String getComponentType() {
        return m_componentType;
    }

    /**
     * @param componentType the componentType to set
     */
    public void setComponentType(String componentType) {
        m_componentType = componentType;
    }

    /**
     * 
     * @return the referencedGuid
     */
    @Transient
    public String getReferencedGuid() {
        return getHbmReferencedGuid();
    }

    /**
     * 
     * @param referencedGuid the referencedGuid to set
     */
    public void setReferencedGuid(String referencedGuid) {
        setHbmReferencedGuid(referencedGuid);
    }

    /**
     * 
     * @return the parentProjectId
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
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }

    /**
     * 
     * @param parentProjectId the parentProjectId to set
     */
    void setHbmParentProjectId(Long parentProjectId) {
        m_parentProjectId = parentProjectId;
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
    @Column(name = "CREATION_CONTEXT")
    String getHbmCreationContext() {
        return m_creationContext;
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public CompNameCreationContext getCreationContext() {
        return CompNameCreationContext.forName(getHbmCreationContext());
    }
    
    /**
     * 
     * @param creationContext the creationContext to set
     */
    void setHbmCreationContext(String creationContext) {
        m_creationContext = creationContext;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public final boolean isNameEqual(ComponentNamePO compNamePO) {
        return getHbmGuid().equals(compNamePO.getHbmGuid());
    }
    
    
    /**
     * {@inheritDoc}
     * Two ComponentNamePOs are equal if their Parent GUIDs and their 
     * ComponentNamePO GUIDs are equal!
     */
    public boolean equals(Object obj) {
        if (getHbmParentProjectId() == null) {
            return super.equals(obj);
        }
        
        if (!(obj instanceof ComponentNamePO)) {
            return false;
        }
        final ComponentNamePO c = (ComponentNamePO)obj;
        return new EqualsBuilder()
            .append(this.getHbmParentProjectId(), c.getHbmParentProjectId())
            .append(this.getHbmGuid(), c.getHbmGuid())
            .isEquals();
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        Long hbmParentProjectId = getHbmParentProjectId();
        String hbmGuid = getHbmGuid();
        if (hbmParentProjectId == null || hbmGuid == null) {
            return super.hashCode();
        }
        
        return hbmParentProjectId.hashCode() + hbmGuid.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()) //$NON-NLS-1$
            .append("type", getComponentType()).append("guid", getGuid())  //$NON-NLS-1$//$NON-NLS-2$
            .append("parentProjectId", getParentProjectId()).toString(); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Version
    public Integer getVersion() {
        return m_version;
    }

    /** {@inheritDoc} */
    void setVersion(Integer version) {
        m_version  = version;        
    }
    
    /** {@inheritDoc} */
    public void setId(Long id) {
        super.setId(id);
    }

    /** {@inheritDoc} */
    @Transient
    public IProblem getTypeProblem() {
        return m_typeProblem;
    }

    /** {@inheritDoc} */
    public void setTypeProblem(IProblem problem) {
        m_typeProblem = problem;
    }

    /** {@inheritDoc} */
    public void setUsageType(String type) {
        m_usageType = type;
    }

    /** {@inheritDoc} */
    @Transient
    public String getUsageType() {
        return m_usageType;
    }
}
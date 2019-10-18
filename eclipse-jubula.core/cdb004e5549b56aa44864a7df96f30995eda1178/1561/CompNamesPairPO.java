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

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This persistent object represents a pair of a component name (first name) and
 * a new (overriding) name, the second name. The pairs are stored in a map in
 * the test execution nodes. It also holds the information if the pair of names
 * is propagated. If it is propagated, it will be visible in all parent
 * execution nodes of the node it is stored in.
 * 
 * @author BREDEX GmbH
 * @created 08.09.2005
 */
@Entity
@Table(name = "COMP_NAME_PAIRS")
class CompNamesPairPO implements ICompNamesPairPO {
    
    /** The logger */
    private static transient Logger log = 
        LoggerFactory.getLogger(CompNamesPairPO.class);
    
    /** The first name. */
    private String m_firstName;
    /** The second (overriding) name. */
    private String m_secondName;
    /** The propagated property. */
    private boolean m_propagated = false;
    /** the current comp type - not to persist */
    private String m_type = StringConstants.EMPTY;
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    /** The ID of the parent project */
    private Long m_parentProjectId = null;
    
    /**
     * For Persistence (JPA / EclipseLink)
     */
    CompNamesPairPO() {
        // OK
    }
    
    /**
     * The constructor. It initializes the first and the second name with the
     * passed name.
     * 
     * @param name The name
     * @param type the current type to set
     */
    CompNamesPairPO(String name, String type) {
        this(name, name, type);
    }
    
    /**
     * The constructor.
     * 
     * @param firstName The first name
     * @param secondName The second name
     * @param type the current type to set
     */
    CompNamesPairPO(String firstName, String secondName, String type) {
        
        Validate.notNull(firstName, Messages.TheFirstNameMustNotBeNull);
        Validate.notNull(secondName, Messages.TheSecondNameMustNotBeNull);
        setFirstName(firstName);
        setSecondName(secondName);
        setType(type);
    }
    /**
     * @param id The ID
     */
    @SuppressWarnings("unused")
    private void setId(Long id) {
        m_id = id;
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
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
     * {@inheritDoc}
     * @return The first name
     */
    @Transient
    public String getName() {
        return getFirstName();
    }

    /**
     *     
     * @return the propagated property.
     */
    @Basic
    @Column(name = "PROPAGATED")
    public boolean isPropagated() {
        return m_propagated;
    }

    /**
     * @param propagated
     *            The propagated property to set.
     */
    public void setPropagated(boolean propagated) {
        m_propagated = propagated;
    }
    /**
     * {@inheritDoc}
     */
    public void setFirstName(String firstName) {
        m_firstName = firstName;
    }
    /**
     *     
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "FIRST_NAME", length = MAX_STRING_LENGTH)
    public String getFirstName() {
        return m_firstName;
    }

   
    /**
     *     
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "SECOND_NAME", length = MAX_STRING_LENGTH)
    public String getSecondName() {
        return m_secondName;
    }

    /**
     * {@inheritDoc}
     */
    public void setSecondName(String secondName) {
        m_secondName = secondName;
    }
    /**
     * @return <code>true</code> if the first and the second name are equal,
     *         <code>false</code> otherwise
     */
    public boolean areNamesEqual() {
        return StringUtils.equals(getFirstName(), getSecondName());
    }

    /**
     * not to persist
     * {@inheritDoc}
     */
    @Transient
    public String getType() {
        if (m_type == null) {
            m_type = StringConstants.EMPTY;
        }
        return m_type;
    }
    
    /**
     * not to persist
     * {@inheritDoc}
     */
    public void setType(String type) {
        m_type = type;
    }

    /**
     * {@inheritDoc}
     */
    public void changeCompName(String oldCompNameGuid, String newCompNameGuid) {
        setSecondName(newCompNameGuid);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getComponentType(IWritableComponentNameCache compNameCache, 
            Collection<Component> availableComponents) {
        IComponentNamePO compNamePo = 
            compNameCache.getResCompNamePOByGuid(getFirstName());
        if (compNamePo == null) {
            StringBuilder msg = new StringBuilder();
            msg.append(Messages.CouldNotFindComponentNameWithGUID);
            msg.append(StringConstants.SPACE);
            msg.append(StringConstants.LEFT_PARENTHESIS);
            msg.append(getFirstName());
            msg.append(StringConstants.RIGHT_PARENTHESIS);
            msg.append(StringConstants.SPACE);
            msg.append(Messages.WhileTryingToIdentifyItsType);
            msg.append(StringConstants.DOT);
            log.info(msg.toString());
            return null;
        }

        return compNamePo.getComponentType();
    }

    /**
     * {@inheritDoc}
     * 
     * Version is not used for optimistic locking for this class.
     */
    @Transient
    public Integer getVersion() {
        return 0;
    }
}
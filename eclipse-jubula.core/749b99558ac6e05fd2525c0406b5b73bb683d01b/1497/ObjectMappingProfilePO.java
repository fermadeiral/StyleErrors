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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;


/**
 * Persistent representation of an object mapping profile.
 * 
 * @author BREDEX GmbH
 * @created Nov 4, 2008
 */
@Entity
@Table(name = "OM_PROFILE")
class ObjectMappingProfilePO implements IObjectMappingProfilePO {

    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;

    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;

    /**
     * weight for the component name
     */
    private double m_nameFactor = 0;

    /**
     * weight for the component path to root
     */
    private double m_pathFactor = 0;

    /**
     * weight for the component context
     */
    private double m_contextFactor = 0;

    /**
     * minimum value for a component to be considered a possible match
     */
    private double m_threshold = 0;

    /** property change support */
    private final PropertyChangeSupport m_pcs = new PropertyChangeSupport(this);

    /**
     * Default constructor
     */
    ObjectMappingProfilePO() {
        // Nothing to initialize
    }

    /**
     * For Persistence (JPA / EclipseLink).
     *          
     * @return the context factor weight in object recognition comparisons.          
     */
    @Basic
    @Column(name = "CONTEXT_FACTOR")
    private double getHbmContextFactor() {
        return m_contextFactor;
    }

    /**
     * For Persistence (JPA / EclipseLink).
     *          
     * @return the name factor weight in object recognition comparisons.
     */
    @Basic
    @Column(name = "NAME_FACTOR")
    private double getHbmNameFactor() {
        return m_nameFactor;
    }

    /**
     * For Persistence (JPA / EclipseLink).
     * 
     * @return the path factor weight in object recognition comparisons.
     */
    @Basic
    @Column(name = "PATH_FACTOR")
    private double getHbmPathFactor() {
        return m_pathFactor;
    }

    /**
     *          
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "THRESHOLD")
    public double getThreshold() {
        return m_threshold;
    }

    /**
     * For Persistence (JPA / EclipseLink).
     * 
     * @param contextFactor the new percentage weight for the context factor.
     */
    private void setHbmContextFactor(double contextFactor) {
        double oldValue = m_contextFactor;
        m_contextFactor = contextFactor;
        m_pcs.firePropertyChange("contextFactor", oldValue, m_contextFactor); //$NON-NLS-1$
    }

    /**
     * For Persistence (JPA / EclipseLink).
     * 
     * @param nameFactor the new percentage weight for the name factor.
     */
    private void setHbmNameFactor(double nameFactor) {
        double oldValue = m_nameFactor;
        m_nameFactor = nameFactor;
        m_pcs.firePropertyChange("nameFactor", oldValue, m_nameFactor); //$NON-NLS-1$
    }

    /**
     * For Persistence (JPA / EclipseLink).
     * 
     * @param pathFactor the new percentage weight for the path factor.
     */
    private void setHbmPathFactor(double pathFactor) {
        double oldValue = m_pathFactor;
        m_pathFactor = pathFactor;
        m_pcs.firePropertyChange("pathFactor", oldValue, m_pathFactor); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public void setThreshold(double threshold) {
        double oldValue = m_threshold;
        m_threshold = threshold;
        m_pcs.firePropertyChange("threshold", oldValue, m_threshold); //$NON-NLS-1$
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
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
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
     * 
     * @return nothing. Instead, throws an <code>UnsupportedOperationException</code>
     *         since objects of this type do not have a parent project
     * @throws UnsupportedOperationException always
     */
    @Transient
    public Long getParentProjectId() 
        throws UnsupportedOperationException {

        throw new UnsupportedOperationException(
            getClass().getName() + StringConstants.SPACE 
                + Messages.DoesNotHaveAParentProject);
    }

    /**
     * Throws an <code>UnsupportedOperationException</code> because objects of
     * this type do not have a parent project.
     * 
     * @param projectId The ID of a project. This parameter is ignored.
     * @throws UnsupportedOperationException always
     */
    public void setParentProjectId(Long projectId) 
        throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
            getClass().getName() + StringConstants.SPACE 
                + Messages.DoesNotHaveAParentProject);
    }

    /**
     * For Persistence (JPA / EclipseLink).
     * 
     * @param id id The id to set.
     */
    @SuppressWarnings("unused")
    private void setId(Long id) {
        m_id = id;
    }
    
    /**
     * For Persistence (JPA / EclipseLink)
     * 
     * @param version The version to set.
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }
    
    /**
     * {@inheritDoc}
     */
    public void useTemplate(Profile template) {
        setContextFactor(template.getContextFactor());
        setNameFactor(template.getNameFactor());
        setPathFactor(template.getPathFactor());
        setThreshold(template.getThreshold());
    }

    /**
     * {@inheritDoc}
     */
    public boolean matchesTemplate(Profile template) {
        return (getContextFactor() == template.getContextFactor())
            && (getNameFactor() == template.getNameFactor())
            && (getPathFactor() == template.getPathFactor())
            && (getThreshold() == template.getThreshold());
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public double getPathFactor() {
        return getHbmPathFactor();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public double getContextFactor() {
        return getHbmContextFactor();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public double getNameFactor() {
        return getHbmNameFactor();
    }

    /**
     * {@inheritDoc}
     */
    public void setPathFactor(double pathFactor) {
        validateFactor(pathFactor);
        setHbmPathFactor(pathFactor);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setContextFactor(double contextFactor) {
        validateFactor(contextFactor);
        setHbmContextFactor(contextFactor);
    }

    /**
     * {@inheritDoc}
     */
    public void setNameFactor(double nameFactor) {
        validateFactor(nameFactor);
        setHbmNameFactor(nameFactor);
    }

    /**
     * Validates the given factor value, throwing an 
     * <code>IllegalArgumentException</code> if the value is invalid.
     * 
     * @param factorValue The value to validate.
     * @throws IllegalArgumentException if the given value is invalid.
     */
    private void validateFactor(double factorValue) 
        throws IllegalArgumentException {
        
        Validate.isTrue(
                factorValue >= IObjectMappingProfilePO.MIN_PERCENTAGE_VALUE);
        Validate.isTrue(
                factorValue <= IObjectMappingProfilePO.MAX_PERCENTAGE_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof IObjectMappingProfilePO) {
            IObjectMappingProfilePO profile = (IObjectMappingProfilePO)obj;
            return new EqualsBuilder()
                .append(getName(), profile.getName())
                .append(getNameFactor(), profile.getNameFactor())
                .append(getContextFactor(), profile.getContextFactor())
                .append(getPathFactor(), profile.getPathFactor())
                .append(getThreshold(), profile.getThreshold())
                .isEquals();
        }
        
        return false;
    }
    
    /** {@inheritDoc} */
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(getName());
        hcb.append(getNameFactor());
        hcb.append(getContextFactor());
        hcb.append(getPathFactor());
        hcb.append(getThreshold());
        return hcb.toHashCode();
    }
    
    /**
     * standard bean support
     * @param listener standard bean support
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.m_pcs.addPropertyChangeListener(listener);
    }

    /**
     * standard bean support
     * 
     * @param listener
     *            standard bean support
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.m_pcs.removePropertyChangeListener(listener);
    }

}

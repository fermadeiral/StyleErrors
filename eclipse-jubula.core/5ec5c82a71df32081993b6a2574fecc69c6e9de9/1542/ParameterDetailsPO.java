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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.persistence.annotations.Index;

/**
 * @author BREDEX GmbH
 * @created 22.01.2010
 */
@Entity
@Table(name = "PARAMETER_DETAILS")
@Index(name = "PI_PARAM_LIST_CHILD", columnNames = { "FK_TESTRESULT" })
public class ParameterDetailsPO implements IParameterDetailsPO {

    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;
    
    /** parameter Name */
    private String m_parameterName;
    
    /** internal parameter type */
    private String m_internalParameterType;
    
    /** parameter type */
    private String m_parameterType;
    
    /** parameter value */
    private String m_parameterValue;
    
    /** summary id*/
    private long m_testResultSummaryId;
    
    /**
     * only for Persistence (JPA / EclipseLink)
     */
    ParameterDetailsPO() {
        //default
    }
    

    

    /**
     * only for Persistence (JPA / EclipseLink)
     * 
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    @Column(name = "ID")
    public Long getId()  {
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
     * 
     * {@inheritDoc}
     */
    @Version
    @Column(name = "INTERNAL_VERSION")
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
     * 
     * @return the parameterName
     */
    @Basic
    @Column(
            name = "NAME", 
            length = IPersistentObject.MAX_STRING_LENGTH)
    public String getParameterName() {
        return m_parameterName;
    }

    /**
     * @param parameterName the parameterName to set
     */
    public void setParameterName(String parameterName) {
        m_parameterName = parameterName;
    }

    /**
     * 
     * @return the internalParameterType
     */
    @Basic
    @Column(
            name = "INTERNAL_TYPE", 
            length = IPersistentObject.MAX_STRING_LENGTH)
    public String getInternalParameterType() {
        return m_internalParameterType;
    }

    /**
     * @param internalParameterType the internalParameterType to set
     */
    public void setInternalParameterType(String internalParameterType) {
        m_internalParameterType = internalParameterType;
    }

    /**
     * 
     * @return the parameterType
     */
    @Basic
    @Column(name = "TYPE", length = IPersistentObject.MAX_STRING_LENGTH)
    public String getParameterType() {
        return m_parameterType;
    }

    /**
     * @param parameterType the parameterType to set
     */
    public void setParameterType(String parameterType) {
        m_parameterType = parameterType;
    }

    /**
     * 
     * @return the parameterValue
     */
    @Basic
    @Column(name = "VALUE", length = IPersistentObject.MAX_STRING_LENGTH)
    public String getParameterValue() {
        return m_parameterValue;
    }

    /**
     * @param parameterValue the parameterValue to set
     */
    public void setParameterValue(String parameterValue) {
        m_parameterValue = parameterValue;
    }
    
    /**
     * @return the m_testResultSummaryId
     */
    @Basic
    @Column(name = "INTERNAL_TESTRUN_ID")
    @Index(name = "PD_TESTRUN_ID")
    public Long getInternalTestResultSummaryID() {
        return m_testResultSummaryId;
    }
    
    /**
     * @param testResultSummaryId the testResultSummaryId to set
     */
    public void setInternalTestResultSummaryID(Long testResultSummaryId) {
        m_testResultSummaryId = testResultSummaryId;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return super.toString() + StringConstants.SPACE 
            + StringConstants.LEFT_PARENTHESIS + m_id.toString() 
            + StringConstants.RIGHT_PARENTHESIS;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ParameterDetailsPO 
                || obj instanceof IParameterDetailsPO)) {
            return false;
        }
        IParameterDetailsPO o = (IParameterDetailsPO)obj;
        if (getId() != null) {
            return getId().equals(o.getId());
        }
        return super.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }
    
}

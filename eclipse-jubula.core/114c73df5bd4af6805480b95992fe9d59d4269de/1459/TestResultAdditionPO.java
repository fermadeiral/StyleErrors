/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.SerializationUtils;
import org.eclipse.persistence.annotations.Index;

/**
 * 
 * @author BREDEX GmbH
 *
 */
@Entity
@Table(name = "TESTRESULT_ADDITIONS")
class TestResultAdditionPO implements ITestResultAdditionPO {
       
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    /** the type for this additional data */
    private ITestResultAdditionPO.TYPE m_type;
    /** data */
    private byte[] m_data;
    /** testresult summary id*/
    private Long m_testResultSummaryId;
    
    
    /**
     * only for Persistence (JPA / EclipseLink)
     */
    TestResultAdditionPO() {
       //default
    }
    /**
     * 
     * @param commandLineText the commandlineText
     */
    TestResultAdditionPO(String commandLineText) {
        m_data = SerializationUtils.serialize(commandLineText);
        m_type = ITestResultAdditionPO.TYPE.OUT_AND_ERR;
    }
    
    /**
     * @param isJunitTestSuite dummy
     */
    TestResultAdditionPO(Boolean isJunitTestSuite) {
        setData(isJunitTestSuite);
        setType(TYPE.JUNIT_TEST_SUITE);
    }
    
    /**
     * 
     * only for Persistence (JPA / EclipseLink)
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    @Column(name = "ID")
    public Long getId() {
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
     * @return the saved data
     */
    @Basic
    @Lob
    @Column(name = "DATA")
    private byte[] getDataEL() {
        return m_data;
    }
    /**
     * {@inheritDoc}
     */
    @Transient
    public Object getData() {
        return SerializationUtils.deserialize(getDataEL());
    }
    
    /**
     * @param bytearray the bytearray to save
     */
    public void setDataEL(byte[] bytearray) {
        m_data = bytearray;
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public void setData(Serializable object) {
        setDataEL(SerializationUtils.serialize(object));            
    }
    
    /**
     * @return the type for the data {@link ITestResultAdditionPO.TYPE}
     */
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "Type")
    public ITestResultAdditionPO.TYPE getType() {
        return m_type;
    }
    
    /**
     * 
     * @param type the type for the data {@link ITestResultAdditionPO.TYPE}
     */
    public void setType(ITestResultAdditionPO.TYPE type) {
        this.m_type = type;
    }
    
    /**
     * @return the m_testResultSummaryId
     */
    @Basic
    @Column(name = "INTERNAL_TESTRUN_ID")
    @Index(name = "TRA_TESTRUN_ID")
    public Long getInternalTestResultSummaryID() {
        return m_testResultSummaryId;
    }
    
    /**
     * @param testResultSummaryId the testResultSummaryId to set
     */
    public void setInternalTestResultSummaryID(Long testResultSummaryId) {
        m_testResultSummaryId = testResultSummaryId;
    }
    
}

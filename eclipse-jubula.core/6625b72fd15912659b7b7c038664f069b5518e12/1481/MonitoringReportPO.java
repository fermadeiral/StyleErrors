/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This class represents a monitoring report
 * @author BREDEX GmbH
 * @created 19.12.2011
 */

@Entity
@Table(name = "MONITORING_REPORT")
public class MonitoringReportPO {
    /**
     * the id
     */
    private Long m_id; 
    
    /**
     * The monitoring report 
     */
    private byte[] m_report;

    /**
     * the corresponding test result summary
     */
    private TestResultSummaryPO m_summary;
    
    
    /**
     * default constructor
     */
    public MonitoringReportPO() {
        //default
    }
    /**
     * @param report the report to store in database
     */
    public MonitoringReportPO(byte[] report) {
        this.m_report = report;
    }
    
    /**
     * 
     * @return returns the monitoring report
     */
    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(name = "M_REPORT")
    public byte[] getReport() {
        return m_report;
    
    }
    /**
     * 
     * @param report sets the monitoring report 
     */
    public void setReport(byte[] report) {
        this.m_report = report;
    }
    /**
     * 
     * @return the corresponding summary for this monitoring report
     */
    @OneToOne(mappedBy = "monitoringReport", fetch = FetchType.LAZY)
    public TestResultSummaryPO getSummary() {
        return m_summary;
    }
    /**
     * 
     * @param summary the corresponding summary
     */
    public void setSummary(TestResultSummaryPO summary) {
        this.m_summary = summary;
    }
    /**
     * 
     * @return the id (auto generated)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return m_id;
    }
    /**
     * 
     * @param id the id (auto generated)
     */
    public void setId(Long id) {
        this.m_id = id;
    }
}

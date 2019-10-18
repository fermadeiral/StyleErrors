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

import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.businessprocess.TestresultSummaryBP;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.IMonitoringValue;


/**
 * Test Result implementation that provides data based on the current state of
 * Test Execution.
 *
 * @author BREDEX GmbH
 * @created Aug 6, 2010
 */
public class TestResult extends AbstractTestResult {
    /** the profiling agent id*/
    private String m_monitoringId;
    
    /** the calculated monitoring values*/
    private Map<String, IMonitoringValue> m_monitoringValues;
    /** the monitoring report blob as byte array */
    private byte[] m_reportData;   
    /** is monitoring report written? */
    private boolean m_reportWritten;    
    
    /**
     * <code>autConfigMap</code> the aut config map
     */
    private Map<String, String> m_autConfigMap;
    /** the project */
    private IProjectPO m_project;

    /**
     * Constructor
     * 
     * @param rootResultNode
     *            The root of the Test Result tree. Must not be
     *            <code>null</code>.
     * @param autConfigMap
     *            the aut config map
     */
    public TestResult(TestResultNode rootResultNode,
            Map<String, String> autConfigMap) {
        super(rootResultNode);
        setProject(GeneralStorage.getInstance().getProject());
        setAutConfigMap(autConfigMap);
    }

    /**
     * {@inheritDoc}
     */
    public String getAutAgentHostName() {
        return MapUtils.getString(getAutConfigMap(),
                AutConfigConstants.AUT_CONFIG_AUT_HOST_NAME, 
                StringConstants.EMPTY);
    }

    /**
     * {@inheritDoc}
     */
    public String getAutArguments() {
        return MapUtils.getString(getAutConfigMap(),
                AutConfigConstants.AUT_ARGUMENTS, StringConstants.EMPTY);
    }

    /**
     * {@inheritDoc}
     */
    public String getAutConfigName() {
        return MapUtils.getString(getAutConfigMap(),
                AutConfigConstants.AUT_CONFIG_NAME, TestresultSummaryBP.AUTRUN);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getAutId() {
        return MapUtils.getString(getAutConfigMap(),
                AutConfigConstants.AUT_ID, TestresultSummaryBP.AUTRUN);
    }

    /**
     * {@inheritDoc}
     */
    public Date getEndTime() {
        return ClientTest.instance().getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    public int getExpectedNumberOfSteps() {
        return TestExecution.getInstance().getExpectedNumberOfSteps();
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfEventHandlerSteps() {
        return TestExecution.getInstance().getNumberOfEventHandlerSteps()
            + TestExecution.getInstance().getNumberOfRetriedSteps();
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfFailedSteps() {
        return TestExecution.getInstance().getNumberOfFailedSteps();
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfTestedSteps() {
        return TestExecution.getInstance().getNumberOfTestedSteps();
    }

    /**
     * {@inheritDoc}
     */
    public String getProjectGuid() {
        return getProject().getGuid();
    }

    /**
     * {@inheritDoc}
     */
    public long getProjectId() {
        return getProject().getId();
    }

    /**
     * {@inheritDoc}
     */
    public Integer getProjectMajorVersion() {
        return getProject().getMajorProjectVersion();
    }

    /**
     * {@inheritDoc}
     */
    public Integer getProjectMinorVersion() {
        return getProject().getMinorProjectVersion();
    }

    /**
     * {@inheritDoc}
     */
    public String getProjectName() {
        return getProject().getName();
    }

    /**
     * {@inheritDoc}
     */
    public Date getStartTime() {
        return ClientTest.instance().getTestsuiteStartTime();
    }

    /**
     * @return the monitoringId
     */
    public String getMonitoringId() {
        return m_monitoringId;
    }
    /**
     * 
     * @return The monitored values
     */
    public Map<String, IMonitoringValue> getMonitoringValues() {
        return m_monitoringValues;
    }
    /**
     * 
     * @param monitoringValues the MonitoringValues to set
     */
    public void setMonitoringValues(
            Map<String, IMonitoringValue> monitoringValues) {
        this.m_monitoringValues = monitoringValues;
    }

    /**
     * @param monitoringId the monitoringId to set
     */
    public void setMonitoringId(String monitoringId) {
        m_monitoringId = monitoringId;
    } 

    /**
     * @return the reportData
     */
    public byte[] getReportData() {
        return m_reportData;
    }

    /**
     * @param reportData the reportData to set
     */
    public void setReportData(byte[] reportData) {
        m_reportData = reportData;
    }
    /**
     * 
     * @return true if report was written, false otherwise.
     */
    public boolean isReportWritten() {
        return m_reportWritten;
    }
    /**
     * 
     * @param reportWritten set reportWritten
     */
    public void setReportWritten(boolean reportWritten) {
        this.m_reportWritten = reportWritten;
    }    

    /**
     * @param autConfigMap the autConfigMap to set
     */
    private void setAutConfigMap(Map<String, String> autConfigMap) {
        m_autConfigMap = autConfigMap;
    }

    /**
     * @return the autConfigMap
     */
    public Map<String, String> getAutConfigMap() {
        return m_autConfigMap;
    }

    /**
     * @return the project
     */
    public IProjectPO getProject() {
        return m_project;
    }

    /**
     * @param project the project to set
     */
    private void setProject(IProjectPO project) {
        m_project = project;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getProjectMicroVersion() {
        return m_project.getMicroProjectVersion();
    }

    /**
     * {@inheritDoc}
     */
    public String getProjectVersionQualifier() {
        return m_project.getProjectVersionQualifier();
    }
}
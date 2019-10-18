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

import org.apache.commons.lang.StringUtils;

/**
 * Test Result implementation that provides data based on a given Test Result
 * Summary.
 *
 * @author BREDEX GmbH
 * @created Aug 13, 2010
 */
public class SummarizedTestResult extends AbstractTestResult {

    /** the summary providing the actual result data */
    private IArchivableTestResultSummary m_summary;
    
    /**
     * Constructor
     *  
     * @param summary Test Result Summary to which the Test Result belongs.
     * @param rootResultNode The root of the Test Result tree. Must not be 
     *                       <code>null</code>.
     */
    public SummarizedTestResult(IArchivableTestResultSummary summary, 
            TestResultNode rootResultNode) {
        super(rootResultNode);
        m_summary = summary;
    }

    
    /**
     * {@inheritDoc}
     */
    public String getAutAgentHostName() {
        return m_summary.getAutAgentName();
    }

    /**
     * {@inheritDoc}
     */
    public String getAutArguments() {
        return StringUtils.defaultString(m_summary.getAutCmdParameter());
    }

    /**
     * {@inheritDoc}
     */
    public String getAutConfigName() {
        return m_summary.getAutConfigName();
    }

    /**
     * {@inheritDoc}
     */
    public Date getEndTime() {
        return m_summary.getTestsuiteEndTime();
    }

    /**
     * {@inheritDoc}
     */
    public int getExpectedNumberOfSteps() {
        return m_summary.getTestsuiteExpectedTeststeps();
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfEventHandlerSteps() {
        return m_summary.getTestsuiteEventHandlerTeststeps();
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfFailedSteps() {
        return m_summary.getTestsuiteFailedTeststeps();
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfTestedSteps() {
        return m_summary.getTestsuiteExecutedTeststeps();
    }

    /**
     * {@inheritDoc}
     */
    public String getProjectGuid() {
        return m_summary.getInternalProjectGuid();
    }

    /**
     * {@inheritDoc}
     */
    public long getProjectId() {
        return m_summary.getInternalProjectID();
    }

    /**
     * {@inheritDoc}
     */
    public Integer getProjectMajorVersion() {
        return m_summary.getProjectMajorVersion();
    }

    /**
     * {@inheritDoc}
     */
    public Integer getProjectMinorVersion() {
        return m_summary.getProjectMinorVersion();
    }
    
    /**
     * {@inheritDoc}
     */
    public Integer getProjectMicroVersion() {
        return m_summary.getProjectMicroVersion();
    }

    /**
     * {@inheritDoc}
     */
    public String getProjectVersionQualifier() {
        return m_summary.getProjectVersionQualifier();
    }

    /**
     * {@inheritDoc}
     */
    public String getProjectName() {
        return m_summary.getProjectName();
    }

    /**
     * {@inheritDoc}
     */
    public Date getStartTime() {
        return m_summary.getTestsuiteStartTime();
    }
}
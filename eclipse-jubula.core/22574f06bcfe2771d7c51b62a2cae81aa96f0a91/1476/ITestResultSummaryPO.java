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

import java.util.Map;

import org.eclipse.jubula.tools.internal.objects.IMonitoringValue;

/**
 * @author BREDEX GmbH
 * @created Jan 22, 2010
 */
public interface ITestResultSummaryPO extends IArchivableTestResultSummary, 
    IALMReportingProperties {
    /** @author BREDEX GmbH */
    public enum AlmReportStatus {
        /** Do not change - persisted to database */
        NOT_YET_REPORTED,
        /** Do not change - persisted to database */
        REPORTED,
        /** Do not change - persisted to database */
        NOT_CONFIGURED,
        /** Do not change - persisted to database */
        REPORT_DISCARDED
    }
    
    /**
     * <code>DEFAULT_NUMBER_OF_FAILED_TEST_STEPS</code>
     */
    public static final int DEFAULT_NUMBER_OF_FAILED_TEST_STEPS = -1;
    
    /** testrun state ok */
    public static final String STATE_OK = "OK"; //$NON-NLS-1$
    
    /** testrun state failed */
    public static final String STATE_FAILED = "FAILED"; //$NON-NLS-1$
    
    /** testrun state testing */
    public static final String STATE_STOPPED = "STOPPED"; //$NON-NLS-1$

    /**
     * only for Persistence (JPA / EclipseLink)
     * @return Returns the id.
     */
    public abstract Long getId();

    /** 
     * {@inheritDoc}
     */
    public abstract Integer getVersion();
    
    /**
     * @param monitoringValue the monitoringValue to set
     */
    public abstract void setMonitoringValues(
            Map<String, IMonitoringValue> monitoringValue);
    
    /**
     * @return the monitoringValue
     */
    public abstract Map<String, IMonitoringValue> getMonitoringValues();
    /**
     * 
     * @param report the monitoring report which will be stored in the database
     */
    public abstract void setMonitoringReport(MonitoringReportPO report);
    /**
     * @return the monitoring report for this test result summary
     */
    public abstract MonitoringReportPO getMonitoringReport();
    
    /**
     * @return whether this result has corresponding test result details
     */
    public boolean hasTestResultDetails();
    
    /**
     * @return additional information
     */
    public String getAdditionalInformation();
    
    /**
     * @param info the information to add
     */
    public void addAdditionalInformation(String info);
    
    /**
     * @param key the key
     * @param value the value
     */
    public void addKeyValuePair(String key, String value);
    
    /**
     * writes the Key Values pair added via {@link #addKeyValuePair(String, String)} to the {@link #addAdditionalInformation(String)}
     */
    public void writeKeyValuePairsIntoAdditionalInformation();
}

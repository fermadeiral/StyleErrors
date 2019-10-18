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

/**
 * Interface for model object: Test Result Summary.
 * <br/><br/>
 * Be very careful when changing method names, adding methods, or removing 
 * methods from this interface, as this interface is used for determining 
 * exportable/importable properties.
 * 
 * @see org.apache.commons.beanutils.PropertyUtils
 *
 * @author BREDEX GmbH
 * @created Jul 22, 2010
 */
public interface IArchivableTestResultSummary {
    /**
     * @param jobName the jobName to set
     */
    public abstract void setTestJobName(String jobName);

    /**
     * @return the jobName
     */
    public abstract String getTestJobName();
    
    /**
     * @param jobGuid the jobGuid to set
     */
    public abstract void setInternalTestJobGuid(String jobGuid);

    /**
     * @return the jobGuid
     */
    public abstract String getInternalTestJobGuid();
    
    /**
     * @return the Job startTime
     */
    public abstract Date getTestJobStartTime();

    /**
     * @param jobStartTime the jobStartTime to set
     */
    public abstract void setTestJobStartTime(Date jobStartTime);
    
    /**
     * @return Returns the autName.
     */
    public abstract String getAutName();

    /**
     * @param autName
     *            The autName to set.
     */
    public abstract void setAutName(String autName);
    
    /**
     * @return Returns the autId.
     */
    public abstract String getAutId();

    /**
     * @param autId The autId to set.
     */
    public abstract void setAutId(String autId);
    
    /**
     * @return Returns the aut guid.
     */
    public abstract String getInternalAutGuid();

    /**
     * @param autGuid
     *            The aut guid to set.
     */
    public abstract void setInternalAutGuid(String autGuid);
    
    /**
     * {@inheritDoc}
     */
    public abstract String getAutToolkit();

    /**
     * {@inheritDoc}
     */
    public abstract void setAutToolkit(String toolkit);

    /**
     * @param testrunDate the date to set
     */
    public abstract void setTestsuiteDate(Date testrunDate);

    /**
     * @return the date
     */
    public abstract Date getTestsuiteDate();

    /**
     * @param startTime the startTime to set
     */
    public abstract void setTestsuiteStartTime(Date startTime);

    /**
     * @return the startTime
     */
    public abstract Date getTestsuiteStartTime();

    /**
     * @param endTime the endTime to set
     */
    public abstract void setTestsuiteEndTime(Date endTime);
    
    /**
     * @return the endTime
     */
    public abstract Date getTestsuiteEndTime();

    /**
     * @param duration the duration to set
     */
    public abstract void setTestsuiteDuration(String duration);

    /**
     * @return the duration
     */
    public abstract String getTestsuiteDuration();

    /**
     * @param projectID the projectID to set
     */
    public abstract void setInternalProjectID(Long projectID);

    /**
     * @return the projectID
     */
    public abstract Long getInternalProjectID();

    /**
     * @param projectGuid the projectGuid to set
     */
    public abstract void setInternalProjectGuid(String projectGuid);

    /**
     * @return the projectGuid
     */
    public abstract String getInternalProjectGuid();

    /**
     * @param projectName the projectName to set
     */
    public abstract void setProjectName(String projectName);

    /**
     * @return the projectName
     */
    public abstract String getProjectName();
    
    /**
     * @param projectMajorVersion the projectMajorVersion to set
     */
    public abstract void setProjectMajorVersion(Integer projectMajorVersion);

    /**
     * @return the projectMajorVersion
     */
    public abstract Integer getProjectMajorVersion();
    
    /**
     * @param projectMinorVersion the ProjectMinorVersion to set
     */
    public abstract void setProjectMinorVersion(Integer projectMinorVersion);

    /**
     * @return the ProjectMinorVersion
     */
    public abstract Integer getProjectMinorVersion();
    
    /**
     * @param projectMicrorVersion the ProjectMicroVersion to set
     */
    public abstract void setProjectMicroVersion(Integer projectMicrorVersion);

    /**
     * @return the ProjectMicroVersion
     */
    public abstract Integer getProjectMicroVersion();
    
    /**
     * @param projectVersionQualifier the ProjectVersionQualifier to set
     */
    public abstract void setProjectVersionQualifier(
            String projectVersionQualifier);

    /**
     * @return the ProjectVersionQualifier
     */
    public abstract String getProjectVersionQualifier();



    /**
     * @param expecCaps the expected caps to set
     */
    public abstract void setTestsuiteExpectedTeststeps(int expecCaps);

    /**
     * @return the expecCaps
     */
    public abstract int getTestsuiteExpectedTeststeps();

    /**
     * @param execCaps the executed caps to set
     */
    public abstract void setTestsuiteExecutedTeststeps(int execCaps);
    
    /**
     * @return the number of failed test steps during test execution
     */
    public abstract int getTestsuiteFailedTeststeps();
    
    /**
     * @param failedCaps the number of failed caps to set
     */
    public abstract void setTestsuiteFailedTeststeps(int failedCaps);

    /**
     * @return the execCaps
     */
    public abstract int getTestsuiteExecutedTeststeps();
    
    /**
     * @return the EventHandler caps
     */
    public abstract int getTestsuiteEventHandlerTeststeps();
    
    /**
     * @param handlerCaps the EventHandler caps to set
     */
    public abstract void setTestsuiteEventHandlerTeststeps(int handlerCaps);

    /**
     * @param tsName the tsName to set
     */
    public abstract void setTestsuiteName(String tsName);

    /**
     * @return the tsName
     */
    public abstract String getTestsuiteName();

    /**
     * @param tsGuid the tsGuid to set
     */
    public abstract void setInternalTestsuiteGuid(String tsGuid);

    /**
     * @return the tsGuid
     */
    public abstract String getInternalTestsuiteGuid();

    /**
     * @param tsStatus the tsStatus to set
     */
    public abstract void setTestsuiteStatus(int tsStatus);

    /**
     * @return the tsStatus
     */
    public abstract int getTestsuiteStatus();

    /**
     * @param autConf the autConf to set
     */
    public abstract void setAutConfigName(String autConf);

    /**
     * @return the autConf
     */
    public abstract String getAutConfigName();
    
    /**
     * @param autConfGuid the autConfGuid to set
     */
    public abstract void setInternalAutConfigGuid(String autConfGuid);

    /**
     * @return the autConfGuid
     */
    public abstract String getInternalAutConfigGuid();

    /**
     * @param autAgent the autAgent to set
     */
    public abstract void setAutAgentName(String autAgent);

    /**
     * @return the autServer
     */
    public abstract String getAutAgentName();
    
    /**
     * @param autHostname the autHostname to set
     */
    public abstract void setAutHostname(String autHostname);

    /**
     * @return the autHostname
     */
    public abstract String getAutHostname();
    
    /**
     * @param autOS the autOS to set
     */
    public abstract void setAutOS(String autOS);

    /**
     * @return the autOS
     */
    public abstract String getAutOS();

    /**
     * @param cmdParam the cmdParam to set
     */
    public abstract void setAutCmdParameter(String cmdParam);

    /**
     * @return the cmdParam
     */
    public abstract String getAutCmdParameter();
    
    /**
     * @param relevant true, if testrun is relevant, false otherwise
     */
    public abstract void setTestsuiteRelevant(boolean relevant);

    /**
     * @return true, if testrun is relevant, false otherwise
     */
    public abstract boolean isTestsuiteRelevant();

    /**
     * @return the execution state of the receiver.
     * @see ITestResultSummaryPO#STATE_OK
     * @see ITestResultSummaryPO#STATE_FAILED
     * @see ITestResultSummaryPO#STATE_STOPPED
     */
    public String getTestRunState();

    /**
     * @return status text suitable for displaying to the user.
     */
    public String getStatusString();
    
    /**
     * 
     * @return the receiver's GUID.
     */
    public String getInternalGuid();
    
    /**
     * @param commentTitle the title of the comment to set
     */
    public abstract void setCommentTitle(String commentTitle);

    /**
     * @return the commentTitle
     */
    public abstract String getCommentTitle();
    
    /**
     * @param commentDetail the detail of the comment to set
     */
    public abstract void setCommentDetail(String commentDetail);

    /**
     * @return the commentDetail
     */
    public abstract String getCommentDetail();

    /**
     * @param monitoringId the monitoringId to set
     */
    public abstract void setInternalMonitoringId(String monitoringId);
    
    /**
     * @return the monitoringId
     */
    public abstract String getInternalMonitoringId();   
    /**
     * @param isWritten 
     */
    public abstract void setReportWritten(boolean isWritten);
    /**
     * @return true, if report was written, false otherwise
     */
    public abstract boolean isReportWritten();
    /**
     * @return the type of the monitoring value
     */
    public abstract String getMonitoringValueType();
    /**
     * 
     * @param type to monitoring type to set
     */
    public abstract void setMonitoringValueType(String type);
    /**
     * @param monitoringValue The value
     */
    public abstract void setMonitoringValue(String monitoringValue);
    /**
     * @return Returns the significant value
     */
    public abstract String getMonitoringValue();
}
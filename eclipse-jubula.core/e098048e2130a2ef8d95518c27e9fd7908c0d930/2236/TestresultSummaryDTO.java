/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.archive.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO.AlmReportStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class TestresultSummaryDTO {
    /** */
    private List<MonitoringValuesDTO> m_monitoringValues =
            new ArrayList<MonitoringValuesDTO>();

    /** ALM reported flag */
    private AlmReportStatus m_almStatus = AlmReportStatus.NOT_CONFIGURED;
    
    /** AUT server */
    private String m_autAgentName;

    /** commandline parameter */
    private String m_autCmdParameter;
    
    /** AUT configuration name */
    private String m_autConfigName;
    
    /** AUT hostname */
    private String m_autHostname;

    /** AUT ID */
    private String m_autId;
    
    /** AUT name */
    private String m_autName;

    /** AUT OS */
    private String m_autOS;
    
    /** information about the used Toolkit */
    private String m_autToolkit;
    
    /** AUT conf uuid */
    private String m_autConfigUuid;
    
    /** AUT Uuid */
    private String m_autUuid;

    /** the Uuid */
    private String m_uuid;
    
    /** used coverage tooling for this summary */
    private String m_monitoringId;
    
    /** project uuid */
    private String m_projectUuid;
    
    /** project id */
    private Long m_projectID;
    
    /** Ts uuid */
    private String m_testsuiteUuid;

    /** monitoring value type */
    private String m_monitoringValueType;

    /** monitoring value */
    private String m_monitoringValue;
    
    /** project Major Version */
    private Integer m_projectMajorVersion;
    
    /** project Minor Version */
    private Integer m_projectMinorVersion;
    
    /** project Micro Version */
    private Integer m_projectMicroVersion;
    
    /** project version qualifier */
    private String m_projectVersionQualifier;
    
    /** project name */
    private String m_projectName;
    
    /** true if blob was written, false otherwise */
    private boolean m_blobWritten = false;
    
    /** Date of test run */
    private Date m_testsuiteDate;
    
    /** duration */
    private String m_testsuiteDuration;
    
    /** end time */
    private Date m_testsuiteEndTime;
    
    /** eventhandler caps */
    private int m_testsuiteEventHandlerTeststeps;

    /** executed caps */
    private int m_testsuiteExecutedTeststeps;
    
    /** expected caps */
    private int m_testsuiteExpectedTeststeps;

    /** number of failed test steps */
    private int m_testsuiteFailedTeststeps;

    /** language */
    private String m_testsuiteLanguage;

    /** Ts name */
    private String m_testsuiteName;

    /** Tj name */
    private String m_testJobName;

    /** Tj uuid */
    private String m_testJobUuid;

    /** Tj Start time **/
    private Date m_testJobStartTime;

    /** Ts Start time **/
    private Date m_testsuiteStartTime;

    /** Ts status */
    private int m_testsuiteStatus;
    
    /** Comment detail */
    private String m_commentDetail;
    
    /** Comment title */
    private String m_commentTitle;

    /** needed because JSON mapping */
    public TestresultSummaryDTO() { }
    
    /**
     * @param trs 
     */
    public TestresultSummaryDTO(ITestResultSummaryPO trs) {
        m_almStatus = trs.getAlmReportStatus();
        m_autAgentName = trs.getAutAgentName();
        m_autCmdParameter = trs.getAutCmdParameter();
        m_autConfigName = trs.getAutConfigName();
        m_autHostname = trs.getAutHostname();
        m_autId = trs.getAutId();
        m_autName = trs.getAutName();
        m_autOS = trs.getAutOS();
        m_autToolkit = trs.getAutToolkit();
        m_autConfigUuid = trs.getInternalAutConfigGuid();
        m_autUuid = trs.getInternalAutGuid();
        m_uuid = trs.getInternalGuid();
        m_monitoringId = trs.getInternalMonitoringId();
        m_projectUuid = trs.getInternalProjectGuid();
        m_projectID = trs.getInternalProjectID();
        m_testsuiteUuid = trs.getInternalTestsuiteGuid();
        m_monitoringValueType = trs.getMonitoringValueType();
        m_monitoringValue = trs.getMonitoringValue();
        m_projectMajorVersion = trs.getProjectMajorVersion();
        m_projectMinorVersion = trs.getProjectMinorVersion();
        m_projectMicroVersion = trs.getProjectMicroVersion();
        m_projectVersionQualifier = trs.getProjectVersionQualifier();
        m_projectName = trs.getProjectName();
        m_blobWritten = trs.isReportWritten();
        m_testsuiteDate = trs.getTestsuiteDate();
        m_testsuiteDuration = trs.getTestsuiteDuration();
        m_testsuiteEndTime = trs.getTestsuiteEndTime();
        m_testsuiteEventHandlerTeststeps =
                trs.getTestsuiteEventHandlerTeststeps();
        m_testsuiteExecutedTeststeps = trs.getTestsuiteExecutedTeststeps();
        m_testsuiteExpectedTeststeps = trs.getTestsuiteExpectedTeststeps();
        m_testsuiteFailedTeststeps = trs.getTestsuiteFailedTeststeps();
        m_testsuiteName = trs.getTestsuiteName();
        m_testJobName = trs.getTestJobName();
        m_testJobStartTime = trs.getTestJobStartTime();
        m_testJobUuid = trs.getInternalTestJobGuid();
        m_testsuiteStartTime = trs.getTestsuiteStartTime();
        m_testsuiteStatus = trs.getTestsuiteStatus();
        m_commentDetail = trs.getCommentDetail();
        m_commentTitle = trs.getCommentTitle();
    }
    
    
    /**
     * @return monitoringValues
     */
    @JsonProperty("monitoringValues")
    public List<MonitoringValuesDTO> getMonitoringValues() {
        return m_monitoringValues;
    }
    /**
     * @param monitoringValue 
     */
    public void addMonitoringValue(MonitoringValuesDTO monitoringValue) {
        this.m_monitoringValues.add(monitoringValue);
    }
    
    /**
     * @return almStatus
     */
    @JsonProperty("almStatus")
    public AlmReportStatus getAlmStatus() {
        return m_almStatus;
    }
    
    /**
     * @param almStatus 
     */
    public void setAlmStatus(AlmReportStatus almStatus) {
        this.m_almStatus = almStatus;
    }
    
    /**
     * @return autAgentName
     */
    @JsonProperty("autAgentName")
    public String getAutAgentName() {
        return m_autAgentName;
    }
    
    /**
     * @param autAgentName 
     */
    public void setAutAgentName(String autAgentName) {
        this.m_autAgentName = autAgentName;
    }
    
    /**
     * @return autCmdParameter
     */
    @JsonProperty("autCmdParameter")
    public String getAutCmdParameter() {
        return m_autCmdParameter;
    }
    
    /**
     * @param autCmdParameter 
     */
    public void setAutCmdParameter(String autCmdParameter) {
        this.m_autCmdParameter = autCmdParameter;
    }
    
    /**
     * @return autConfigName
     */
    @JsonProperty("autConfigName")
    public String getAutConfigName() {
        return m_autConfigName;
    }
    
    /**
     * @param autConfigName 
     */
    public void setAutConfigName(String autConfigName) {
        this.m_autConfigName = autConfigName;
    }
    
    /**
     * @return autHostname
     */
    @JsonProperty("autHostname")
    public String getAutHostname() {
        return m_autHostname;
    }
    
    /**
     * @param autHostname 
     */
    public void setAutHostname(String autHostname) {
        this.m_autHostname = autHostname;
    }
    
    /**
     * @return autId
     */
    @JsonProperty("autId")
    public String getAutId() {
        return m_autId;
    }
    
    /**
     * @param autId 
     */
    public void setAutId(String autId) {
        this.m_autId = autId;
    }
    
    /**
     * @return autName
     */
    @JsonProperty("autName")
    public String getAutName() {
        return m_autName;
    }
    
    /**
     * @param autName 
     */
    public void setAutName(String autName) {
        this.m_autName = autName;
    }
    
    /**
     * @return autOS
     */
    @JsonProperty("autOS")
    public String getAutOS() {
        return m_autOS;
    }
    
    /**
     * @param autOS 
     */
    public void setAutOS(String autOS) {
        this.m_autOS = autOS;
    }
    
    /**
     * @return autToolkit
     */
    @JsonProperty("autToolkit")
    public String getAutToolkit() {
        return m_autToolkit;
    }
    
    /**
     * @param autToolkit 
     */
    public void setAutToolkit(String autToolkit) {
        this.m_autToolkit = autToolkit;
    }
    
    /**
     * @return autConfigUuid
     */
    @JsonProperty("autConfigUuid")
    public String getAutConfigUuid() {
        return m_autConfigUuid;
    }
    
    /**
     * @param autConfigUuid 
     */
    public void setAutConfigUuid(String autConfigUuid) {
        this.m_autConfigUuid = autConfigUuid;
    }
    
    /**
     * @return autUuid
     */
    @JsonProperty("autUuid")
    public String getAutUuid() {
        return m_autUuid;
    }
    
    /**
     * @param autUuid 
     */
    public void setAutUuid(String autUuid) {
        this.m_autUuid = autUuid;
    }
    
    /**
     * @return uuid
     */
    @JsonProperty("uuid")
    public String getUuid() {
        return m_uuid;
    }
    
    /**
     * @param uuid 
     */
    public void setUuid(String uuid) {
        this.m_uuid = uuid;
    }
    
    /**
     * @return monitoringId
     */
    @JsonProperty("monitoringId")
    public String getMonitoringId() {
        return m_monitoringId;
    }
    
    /**
     * @param monitoringId 
     */
    public void setMonitoringId(String monitoringId) {
        this.m_monitoringId = monitoringId;
    }
    
    /**
     * @return projectUuid
     */
    @JsonProperty("projectUuid")
    public String getProjectUuid() {
        return m_projectUuid;
    }
    
    /**
     * @param projectUuid 
     */
    public void setProjectUuid(String projectUuid) {
        this.m_projectUuid = projectUuid;
    }
    
    /**
     * @return projectID
     */
    @JsonProperty("projectID")
    public Long getProjectID() {
        return m_projectID;
    }
    
    /**
     * @param projectID 
     */
    public void setProjectID(Long projectID) {
        this.m_projectID = projectID;
    }
    
    /**
     * @return testsuiteUuid
     */
    @JsonProperty("testsuiteUuid")
    public String getTestsuiteUuid() {
        return m_testsuiteUuid;
    }
    
    /**
     * @param testsuiteUuid 
     */
    public void setTestsuiteUuid(String testsuiteUuid) {
        this.m_testsuiteUuid = testsuiteUuid;
    }
    
    /**
     * @return monitoringValueType
     */
    @JsonProperty("monitoringValueType")
    public String getMonitoringValueType() {
        return m_monitoringValueType;
    }
    
    /**
     * @param monitoringValueType 
     */
    public void setMonitoringValueType(String monitoringValueType) {
        this.m_monitoringValueType = monitoringValueType;
    }

    /**
     * @return monitoringValue 
     */
    @JsonProperty("monitoringValue")
    public String getMonitoringValue() {
        return m_monitoringValue;
    }

    /**
     * @param monitoringValue 
     */
    public void setMonitoringValue(String monitoringValue) {
        this.m_monitoringValue = monitoringValue;
    }

    /**
     * @return projectMajorVersion
     */
    @JsonProperty("projectMajorVersion")
    public Integer getProjectMajorVersion() {
        return m_projectMajorVersion;
    }
    
    /**
     * @param projectMajorVersion 
     */
    public void setProjectMajorVersion(Integer projectMajorVersion) {
        this.m_projectMajorVersion = projectMajorVersion;
    }
    
    /**
     * @return projectMinorVersion
     */
    @JsonProperty("projectMinorVersion")
    public Integer getProjectMinorVersion() {
        return m_projectMinorVersion;
    }
    
    /**
     * @param projectMinorVersion 
     */
    public void setProjectMinorVersion(Integer projectMinorVersion) {
        this.m_projectMinorVersion = projectMinorVersion;
    }
    
    /**
     * @return projectMicroVersion
     */
    @JsonProperty("projectMicroVersion")
    public Integer getProjectMicroVersion() {
        return m_projectMicroVersion;
    }
    
    /**
     * @param projectMicroVersion 
     */
    public void setProjectMicroVersion(Integer projectMicroVersion) {
        this.m_projectMicroVersion = projectMicroVersion;
    }
    
    /**
     * @return projectName
     */
    @JsonProperty("projectName")
    public String getProjectName() {
        return m_projectName;
    }
    
    /**
     * @param projectName 
     */
    public void setProjectName(String projectName) {
        this.m_projectName = projectName;
    }
    
    /**
     * @return blobWritten
     */
    @JsonProperty("blobWritten")
    public boolean isBlobWritten() {
        return m_blobWritten;
    }
    
    /**
     * @param blobWritten 
     */
    public void setBlobWritten(boolean blobWritten) {
        this.m_blobWritten = blobWritten;
    }
    
    /**
     * @return testsuiteDate
     */
    @JsonProperty("testsuiteDate")
    public Date getTestsuiteDate() {
        return m_testsuiteDate;
    }
    
    /**
     * @param testsuiteDate 
     */
    public void setTestsuiteDate(Date testsuiteDate) {
        this.m_testsuiteDate = testsuiteDate;
    }
    
    /**
     * @return testsuiteDuration
     */
    @JsonProperty("testsuiteDuration")
    public String getTestsuiteDuration() {
        return m_testsuiteDuration;
    }
    
    /**
     * @param testsuiteDuration 
     */
    public void setTestsuiteDuration(String testsuiteDuration) {
        this.m_testsuiteDuration = testsuiteDuration;
    }
    
    /**
     * @return testsuiteEndTime
     */
    @JsonProperty("testsuiteEndTime")
    public Date getTestsuiteEndTime() {
        return m_testsuiteEndTime;
    }
    
    /**
     * @param testsuiteEndTime 
     */
    public void setTestsuiteEndTime(Date testsuiteEndTime) {
        this.m_testsuiteEndTime = testsuiteEndTime;
    }
    
    /**
     * @return testsuiteEventHandlerTeststeps
     */
    @JsonProperty("testsuiteEventHandlerTeststeps")
    public int getTestsuiteEventHandlerTeststeps() {
        return m_testsuiteEventHandlerTeststeps;
    }
    
    /**
     * @param testsuiteEventHandlerTeststeps 
     */
    public void setTestsuiteEventHandlerTeststeps(
            int testsuiteEventHandlerTeststeps) {
        this.m_testsuiteEventHandlerTeststeps = testsuiteEventHandlerTeststeps;
    }
    
    /**
     * @return testsuiteExecutedTeststeps
     */
    @JsonProperty("testsuiteExecutedTeststeps")
    public int getTestsuiteExecutedTeststeps() {
        return m_testsuiteExecutedTeststeps;
    }
    
    /**
     * @param testsuiteExecutedTeststeps 
     */
    public void setTestsuiteExecutedTeststeps(int testsuiteExecutedTeststeps) {
        this.m_testsuiteExecutedTeststeps = testsuiteExecutedTeststeps;
    }
    
    /**
     * @return testsuiteExpectedTeststeps
     */
    @JsonProperty("testsuiteExpectedTeststeps")
    public int getTestsuiteExpectedTeststeps() {
        return m_testsuiteExpectedTeststeps;
    }
    
    /**
     * @param testsuiteExpectedTeststeps 
     */
    public void setTestsuiteExpectedTeststeps(int testsuiteExpectedTeststeps) {
        this.m_testsuiteExpectedTeststeps = testsuiteExpectedTeststeps;
    }
    
    /**
     * @return testsuiteFailedTeststeps
     */
    @JsonProperty("testsuiteFailedTeststeps")
    public int getTestsuiteFailedTeststeps() {
        return m_testsuiteFailedTeststeps;
    }
    
    /**
     * @param testsuiteFailedTeststeps 
     */
    public void setTestsuiteFailedTeststeps(int testsuiteFailedTeststeps) {
        this.m_testsuiteFailedTeststeps = testsuiteFailedTeststeps;
    }
    
    /**
     * @return testsuiteLanguage
     */
    @JsonProperty("testsuiteLanguage")
    public String getTestsuiteLanguage() {
        return m_testsuiteLanguage;
    }
    
    /**
     * @param testsuiteLanguage 
     */
    public void setTestsuiteLanguage(String testsuiteLanguage) {
        this.m_testsuiteLanguage = testsuiteLanguage;
    }
    
    /**
     * @return testsuiteName
     */
    @JsonProperty("testsuiteName")
    public String getTestsuiteName() {
        return m_testsuiteName;
    }
    
    /**
     * @param testsuiteName 
     */
    public void setTestsuiteName(String testsuiteName) {
        this.m_testsuiteName = testsuiteName;
    }
    
    /**
     * @return testJobName
     */
    @JsonProperty("testJobName")
    public String getTestJobName() {
        return m_testJobName;
    }

    /**
     * @param testJobName 
     */
    public void setTestJobName(String testJobName) {
        this.m_testJobName = testJobName;
    }

    /**
     * @return testJobStartTime
     */
    @JsonProperty("testJobStartTime")
    public Date getTestJobStartTime() {
        return m_testJobStartTime;
    }
    
    /**
     * @param testJobStartTime 
     */
    public void setTestJobStartTime(Date testJobStartTime) {
        this.m_testJobStartTime = testJobStartTime;
    }
    
    /**
     * @return testJobUuid 
     */
    @JsonProperty("testJobUuid")
    public String getTestJobUuid() {
        return m_testJobUuid;
    }

    /**
     * @param testJobUuid 
     */
    public void setTestJobUuid(String testJobUuid) {
        this.m_testJobUuid = testJobUuid;
    }

    /**
     * @return testsuiteStartTime
     */
    @JsonProperty("testsuiteStartTime")
    public Date getTestsuiteStartTime() {
        return m_testsuiteStartTime;
    }
    
    /**
     * @param testsuiteStartTime 
     */
    public void setTestsuiteStartTime(Date testsuiteStartTime) {
        this.m_testsuiteStartTime = testsuiteStartTime;
    }
    
    /**
     * @return testsuiteStatus
     */
    @JsonProperty("testsuiteStatus")
    public int getTestsuiteStatus() {
        return m_testsuiteStatus;
    }
    
    /**
     * @param testsuiteStatus 
     */
    public void setTestsuiteStatus(int testsuiteStatus) {
        this.m_testsuiteStatus = testsuiteStatus;
    }

    /**
     * @return Comment Derail
     */
    @JsonProperty("commentDetail")
    public String getCommentDetail() {
        return m_commentDetail;
    }

    /**
     * @param commentDetail 
     */
    public void setCommentDetail(String commentDetail) {
        this.m_commentDetail = commentDetail;
    }

    /**
     * @return Comment title
     */
    @JsonProperty("commentTitle")
    public String getCommentTitle() {
        return m_commentTitle;
    }

    /**
     * @param commentTitle 
     */
    public void setCommentTitle(String commentTitle) {
        this.m_commentTitle = commentTitle;
    }

    /**
     * @return the projectVersionQualifier
     */
    @JsonProperty("projectVersionQualifier")
    public String getProjectVersionQualifier() {
        return m_projectVersionQualifier;
    }

    /**
     * @param projectVersionQualifier the projectVersionQualifier to set
     */
    public void setProjectVersionQualifier(String projectVersionQualifier) {
        m_projectVersionQualifier = projectVersionQualifier;
    }
}
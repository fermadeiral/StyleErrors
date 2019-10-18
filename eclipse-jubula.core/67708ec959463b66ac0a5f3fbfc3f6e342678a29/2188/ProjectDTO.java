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
import java.util.List;

import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ProjectVersion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class ProjectDTO extends NodeDTO {
    /** */
    private List<TestDataCategoryDTO> m_testDataCategories =
            new ArrayList<TestDataCategoryDTO>();
    /** */
    private List<NamedTestDataDTO> m_namedTestDatas =
            new ArrayList<NamedTestDataDTO>();
    /** */
    private List<AutDTO> m_auts = new ArrayList<AutDTO>();
    /** */
    private List<NodeDTO> m_categories = new ArrayList<NodeDTO>();
    /** */
    private List<NodeDTO> m_execCategories = new ArrayList<NodeDTO>();
    /** */
    private Integer m_majorProjectVersion, m_minorProjectVersion,
        m_trackingSpan, m_microProjectVersion,
        m_metaDataVersion, m_testResultDetailsCleanupInterval;
    /** */
    private boolean m_isReusable = false;
    /** */
    private boolean m_isProtected = false;
    /** */
    private List<ReusedProjectDTO> m_reusedProjects =
            new ArrayList<ReusedProjectDTO>();
    /** */
    private List<UsedToolkitDTO> m_usedToolkits =
            new ArrayList<UsedToolkitDTO>();
    /** */
    private List<ComponentNameDTO> m_componentNames =
            new ArrayList<ComponentNameDTO>();
    /** */
    private List<TestresultSummaryDTO> m_testresultSummaries =
            new ArrayList<TestresultSummaryDTO>();
    /** */
    private List<CheckConfigurationDTO> m_checkConfigurations =
            new ArrayList<CheckConfigurationDTO>();
    /** */
    private boolean m_teststyleEnabled = false;
    /** */
    private boolean m_isReportOnSuccess = false;
    /** */
    private boolean m_isReportOnFailure = false;
    /** */
    private boolean m_trackingEnabled = false;
    /** */
    private String m_almRepositoryName, m_dashboardURL, m_trackingAttribute,
        m_trackingUnit, m_markupLanguage, m_projectVersionQualifier,
        m_autToolKit;
    /** */
    private List<ReportingRuleDTO> m_reportingRules =
            new ArrayList<ReportingRuleDTO>();
    
    /** needed because JSON mapping */
    public ProjectDTO() { }
    
    /**
     * @param project 
     */
    public ProjectDTO(IProjectPO project) {
        super(project);
    }

    /**
     * @return testDataCategories
     */
    @JsonProperty("testDataCategories")
    public List<TestDataCategoryDTO> getTestDataCategories() {
        return m_testDataCategories;
    }

    /**
     * @param testDataCategorie 
     */
    public void addTestDataCategorie(TestDataCategoryDTO testDataCategorie) {
        this.m_testDataCategories.add(testDataCategorie);
    }

    /**
     * @return namedTestDatas
     */
    @JsonProperty("namedTestDatas")
    public List<NamedTestDataDTO> getNamedTestDatas() {
        return m_namedTestDatas;
    }

    /**
     * @param namedTestData 
     */
    public void addNamedTestData(NamedTestDataDTO namedTestData) {
        this.m_namedTestDatas.add(namedTestData);
    }

    /**
     * @return auts
     */
    @JsonProperty("auts")
    public List<AutDTO> getAuts() {
        return m_auts;
    }

    /**
     * @param aut 
     */
    public void addAut(AutDTO aut) {
        this.m_auts.add(aut);
    }

    /**
     * @return categories
     */
    @JsonProperty("categories")
    public List<NodeDTO> getCategories() {
        return m_categories;
    }

    /**
     * @param node 
     */
    public void addCategory(NodeDTO node) {
        if (!(node instanceof TestCaseDTO || node instanceof CategoryDTO)) {
            throw new IllegalArgumentException();
        }
        this.m_categories.add(node);
    }

    /**
     * @return execCategories
     */
    @JsonProperty("execCategories")
    public List<NodeDTO> getExecCategories() {
        return m_execCategories;
    }

    /**
     * @param node 
     */
    public void addExecCategorie(NodeDTO node) {
        if (!(node instanceof TestSuiteDTO
                || node instanceof TestJobDTO
                || node instanceof ExecCategoryDTO)) {
            
            throw new IllegalArgumentException();
        }
        this.m_execCategories.add(node);
    }

    /**
     * @return the version of project
     */
    @JsonIgnore
    public ProjectVersion getProjectVersion() {
        return new ProjectVersion(m_majorProjectVersion, m_minorProjectVersion,
                m_microProjectVersion);
    }

    /**
     * @return metaDataVersion
     */
    @JsonProperty("metaDataVersion")
    public Integer getMetaDataVersion() {
        return m_metaDataVersion;
    }

    /**
     * @param metaDataVersion 
     */
    public void setMetaDataVersion(Integer metaDataVersion) {
        this.m_metaDataVersion = metaDataVersion;
    }

    /**
     * @return microProjectVersion
     */
    @JsonProperty("microProjectVersion")
    public Integer getMicroProjectVersion() {
        return m_microProjectVersion;
    }

    /**
     * @param microProjectVersion 
     */
    public void setMicroProjectVersion(Integer microProjectVersion) {
        this.m_microProjectVersion = microProjectVersion;
    }

    /**
     * @return majorProjectVersion
     */
    @JsonProperty("majorProjectVersion")
    public Integer getMajorProjectVersion() {
        return m_majorProjectVersion;
    }

    /**
     * @param majorProjectVersion 
     */
    public void setMajorProjectVersion(Integer majorProjectVersion) {
        this.m_majorProjectVersion = majorProjectVersion;
    }

    /**
     * @return minorProjectVersion
     */
    @JsonProperty("minorProjectVersion")
    public Integer getMinorProjectVersion() {
        return m_minorProjectVersion;
    }

    /**
     * @param minorProjectVersion 
     */
    public void setMinorProjectVersion(Integer minorProjectVersion) {
        this.m_minorProjectVersion = minorProjectVersion;
    }

    /**
     * @return trackingSpan
     */
    @JsonProperty("trackingSpan")
    public Integer getTrackingSpan() {
        return m_trackingSpan;
    }

    /**
     * @param trackingSpan 
     */
    public void setTrackingSpan(Integer trackingSpan) {
        this.m_trackingSpan = trackingSpan;
    }

    /**
     * @return testResultDetailsCleanupInterval
     */
    @JsonProperty("testResultDetailsCleanupInterval")
    public Integer getTestResultDetailsCleanupInterval() {
        return m_testResultDetailsCleanupInterval;
    }

    /**
     * @param testResultDetailsCleanupInterval 
     */
    public void setTestResultDetailsCleanupInterval(
            Integer testResultDetailsCleanupInterval) {
        this.m_testResultDetailsCleanupInterval =
                testResultDetailsCleanupInterval;
    }

    /**
     * @return isReusable
     */
    @JsonProperty("isReusable")
    public boolean isReusable() {
        return m_isReusable;
    }

    /**
     * @param isReusable 
     */
    public void setReusable(boolean isReusable) {
        this.m_isReusable = isReusable;
    }

    /**
     * @return isProtected
     */
    @JsonProperty("isProtected")
    public boolean isProtected() {
        return m_isProtected;
    }

    /**
     * @param isProtected 
     */
    public void setProtected(boolean isProtected) {
        this.m_isProtected = isProtected;
    }

    /**
     * @return reusedProjects
     */
    @JsonProperty("reusedProjects")
    public List<ReusedProjectDTO> getReusedProjects() {
        return m_reusedProjects;
    }

    /**
     * @param reusedProject 
     */
    public void addReusedProject(ReusedProjectDTO reusedProject) {
        this.m_reusedProjects.add(reusedProject);
    }

    /**
     * @return usedToolkits
     */
    @JsonProperty("usedToolkits")
    public List<UsedToolkitDTO> getUsedToolkits() {
        return m_usedToolkits;
    }

    /**
     * @param usedToolkit 
     */
    public void addUsedToolkit(UsedToolkitDTO usedToolkit) {
        this.m_usedToolkits.add(usedToolkit);
    }

    /**
     * @return componentNames
     */
    @JsonProperty("componentNames")
    public List<ComponentNameDTO> getComponentNames() {
        return m_componentNames;
    }

    /**
     * @param componentName 
     */
    public void addComponentName(ComponentNameDTO componentName) {
        this.m_componentNames.add(componentName);
    }

    /**
     * @return testresultSummaries
     */
    @JsonProperty("testresultSummaries")
    public List<TestresultSummaryDTO> getTestresultSummaries() {
        return m_testresultSummaries;
    }

    /**
     * @param testresultSummary 
     */
    public void addTestresultSummary(TestresultSummaryDTO testresultSummary) {
        this.m_testresultSummaries.add(testresultSummary);
    }

    /**
     * @param testresultSummaries 
     */
    public void setTestresultSummaries(
            List<TestresultSummaryDTO> testresultSummaries) {
        this.m_testresultSummaries = testresultSummaries;
    }

    /**
     * @return checkConfigurations
     */
    @JsonProperty("checkConfigurations")
    public List<CheckConfigurationDTO> getCheckConfigurations() {
        return m_checkConfigurations;
    }

    /**
     * @param checkConfiguration 
     */
    public void addCheckConfiguration(
            CheckConfigurationDTO checkConfiguration) {
        this.m_checkConfigurations.add(checkConfiguration);
    }

    /**
     * @return teststyleEnabled
     */
    @JsonProperty("teststyleEnabled")
    public boolean isTeststyleEnabled() {
        return m_teststyleEnabled;
    }

    /**
     * @param teststyleEnabled 
     */
    public void setTeststyleEnabled(boolean teststyleEnabled) {
        this.m_teststyleEnabled = teststyleEnabled;
    }

    /**
     * @return isReportOnSuccess
     */
    @JsonProperty("isReportOnSuccess")
    public boolean isReportOnSuccess() {
        return m_isReportOnSuccess;
    }

    /**
     * @param isReportOnSuccess 
     */
    public void setReportOnSuccess(boolean isReportOnSuccess) {
        this.m_isReportOnSuccess = isReportOnSuccess;
    }

    /**
     * @return isReportOnFailure
     */
    @JsonProperty("isReportOnFailure")
    public boolean isReportOnFailure() {
        return m_isReportOnFailure;
    }

    /**
     * @param isReportOnFailure 
     */
    public void setReportOnFailure(boolean isReportOnFailure) {
        this.m_isReportOnFailure = isReportOnFailure;
    }

    /**
     * @return trackingEnabled
     */
    @JsonProperty("trackingEnabled")
    public boolean isTrackingEnabled() {
        return m_trackingEnabled;
    }

    /**
     * @param trackingEnabled 
     */
    public void setTrackingEnabled(boolean trackingEnabled) {
        this.m_trackingEnabled = trackingEnabled;
    }

    /**
     * @return almRepositoryName
     */
    @JsonProperty("almRepositoryName")
    @Deprecated
    public String getAlmRepositoryName() {
        return m_almRepositoryName;
    }

    /**
     * @param almRepositoryName 
     */
    @Deprecated
    public void setAlmRepositoryName(String almRepositoryName) {
    }

    /**
     * @return dashboardURL
     */
    @JsonProperty("dashboardURL")
    @Deprecated
    public String getDashboardURL() {
        return m_dashboardURL;
    }

    /**
     * @param dashboardURL 
     */
    @Deprecated
    public void setDashboardURL(String dashboardURL) {
    }

    /**
     * @return trackingAttribute
     */
    @JsonProperty("trackingAttribute")
    public String getTrackingAttribute() {
        return m_trackingAttribute;
    }

    /**
     * @param trackingAttribute 
     */
    public void setTrackingAttribute(String trackingAttribute) {
        this.m_trackingAttribute = trackingAttribute;
    }

    /**
     * @return trackingUnit
     */
    @JsonProperty("trackingUnit")
    public String getTrackingUnit() {
        return m_trackingUnit;
    }

    /**
     * @param trackingUnit 
     */
    public void setTrackingUnit(String trackingUnit) {
        this.m_trackingUnit = trackingUnit;
    }

    /**
     * @return markupLanguage
     */
    @JsonProperty("markupLanguage")
    public String getMarkupLanguage() {
        return m_markupLanguage;
    }

    /**
     * @param markupLanguage 
     */
    public void setMarkupLanguage(String markupLanguage) {
        this.m_markupLanguage = markupLanguage;
    }

    /**
     * @return projectVersionQualifier
     */
    @JsonProperty("projectVersionQualifier")
    public String getProjectVersionQualifier() {
        return m_projectVersionQualifier;
    }

    /**
     * @param projectVersionQualifier 
     */
    public void setProjectVersionQualifier(String projectVersionQualifier) {
        this.m_projectVersionQualifier = projectVersionQualifier;
    }

    /**
     * @return autToolKit
     */
    @JsonProperty("autToolKit")
    public String getAutToolKit() {
        return m_autToolKit;
    }
    
    /**
     * @param autToolKit 
     */
    public void setAutToolKit(String autToolKit) {
        this.m_autToolKit = autToolKit;
    }

    /**
     * @return reportingRules
     */
    @JsonProperty("reportingRules")
    public List<ReportingRuleDTO> getReportingRules() {
        return m_reportingRules;
    }

    /**
     * @param reportingRule 
     */
    public void addReportingRule(ReportingRuleDTO reportingRule) {
        this.m_reportingRules.add(reportingRule);
    }
}

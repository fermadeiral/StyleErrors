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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class ReusedProjectDTO {
    /** */
    private String m_projectName, m_projectUuid, m_projectVersionQualifier;
    /** */
    private Integer m_majorProjectVersion, 
        m_minorProjectVersion, m_microProjectVersion;
    
    
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
}

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

import org.eclipse.jubula.client.core.model.INodePO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class RefTestCaseDTO extends ParameterDTO {
    /** */
    private String m_testcaseUuid, m_projectUuid;
    /** */
    private boolean m_hasOwnTestdata;
    /** */
    private List<ComponentNamesPairDTO> m_overriddenNames =
            new ArrayList<ComponentNamesPairDTO>();

    
    /** needed because JSON mapping */
    public RefTestCaseDTO() { }
    
    /**
     * @param node 
     */
    public RefTestCaseDTO(INodePO node) {
        super(node);
    }

    /**
     * @return testcaseUuid
     */
    @JsonProperty("testcaseUuid")
    public String getTestcaseUuid() {
        return m_testcaseUuid;
    }

    /**
     * @param testcaseUuid 
     */
    public void setTestcaseUuid(String testcaseUuid) {
        this.m_testcaseUuid = testcaseUuid;
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
     * @return hasOwnTestdata
     */
    @JsonProperty("hasOwnTestdata")
    public boolean isHasOwnTestdata() {
        return m_hasOwnTestdata;
    }

    /**
     * @param hasOwnTestdata 
     */
    public void setHasOwnTestdata(boolean hasOwnTestdata) {
        this.m_hasOwnTestdata = hasOwnTestdata;
    }

    /**
     * @return overriddenNames
     */
    @JsonProperty("overriddenNames")
    public List<ComponentNamesPairDTO> getOverriddenNames() {
        return m_overriddenNames;
    }

    /**
     * @param overriddenName 
     */
    public void addOverriddenNames(ComponentNamesPairDTO overriddenName) {
        m_overriddenNames.add(overriddenName);
    }
}
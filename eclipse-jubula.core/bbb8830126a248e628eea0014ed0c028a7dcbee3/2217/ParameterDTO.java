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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

/**
 * @author BREDEX GmbH
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,  
        include = JsonTypeInfo.As.PROPERTY,  
        property = "type") 
@JsonSubTypes({
    @Type(value = CapDTO.class, name = "cap"),
    @Type(value = TestCaseDTO.class, name = "tc"),
    @Type(value = RefTestCaseDTO.class, name = "rtc"),
    @Type(value = IterateDTO.class, name = "itc")})
public class ParameterDTO extends NodeDTO {
    /** */
    private List<ParamDescriptionDTO> m_parameterDescriptions =
            new ArrayList<ParamDescriptionDTO>();
    /** */
    private TDManagerDTO m_tdManager = new TDManagerDTO();
    /** */
    private String m_datafile, m_referencedTestData;
    
    
    /** needed because JSON mapping */
    public ParameterDTO() { }
    
    /**
     * @param node 
     */
    public ParameterDTO(INodePO node) {
        super(node);
    }

    /**
     * @return parameterDescription
     */
    @JsonProperty("parameterDescription")
    public List<ParamDescriptionDTO> getParameterDescription() {
        return m_parameterDescriptions;
    }

    /**
     * @param parameterDescription 
     */
    public void addParameterDescription(
            ParamDescriptionDTO parameterDescription) {
        m_parameterDescriptions.add(parameterDescription);
    }

    /**
     * @return tdManager
     */
    @JsonProperty("tdManager")
    public TDManagerDTO getTDManager() {
        return m_tdManager;
    }
    
    /**
     * @param tdManager 
     */
    public void setTDManager(TDManagerDTO tdManager) {
        this.m_tdManager = tdManager;
    }

    /**
     * @return datafile
     */
    @JsonProperty("datafile")
    public String getDatafile() {
        return m_datafile;
    }

    /**
     * @param datafile 
     */
    public void setDatafile(String datafile) {
        this.m_datafile = datafile;
    }

    /**
     * @return referencedTestData
     */
    @JsonProperty("referencedTestData")
    public String getReferencedTestData() {
        return m_referencedTestData;
    }
    
    /**
     * @param referencedTestData 
     */
    public void setReferencedTestData(String referencedTestData) {
        this.m_referencedTestData = referencedTestData;
    }
}

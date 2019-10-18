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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class NamedTestDataDTO {
    
    /** */
    private TDManagerDTO m_tdManager;
    /** */
    private List<ParamDescriptionDTO> m_parameterDescriptions =
            new ArrayList<ParamDescriptionDTO>();
    /** */
    private String m_name;
    
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
     * @return parameterDescriptions
     */
    @JsonProperty("parameterDescriptions")
    public List<ParamDescriptionDTO> getParameterDescriptions() {
        return m_parameterDescriptions;
    }
    
    /**
     * @param parameterDescription 
     */
    public void addParameterDescriptions(
            ParamDescriptionDTO parameterDescription) {
        this.m_parameterDescriptions.add(parameterDescription);
    }
    
    /**
     * @return name
     */
    @JsonProperty("name")
    public String getName() {
        return m_name;
    }
    
    /**
     * @param name 
     */
    public void setName(String name) {
        this.m_name = name;
    }
}

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
public class OmEntryDTO {

    /** */
    private TechnicalNameDTO m_technicalName;
    /** */
    private String m_type;
    /** */
    private List<String> m_logicalNames = new ArrayList<String>();
    /** */
    private List<String> m_categorys = new ArrayList<String>();
    
    /**
     * @return technicalName
     */
    @JsonProperty("technicalName")
    public TechnicalNameDTO getTechnicalName() {
        return m_technicalName;
    }
    
    /**
     * @param technicalName 
     */
    public void setTechnicalName(TechnicalNameDTO technicalName) {
        this.m_technicalName = technicalName;
    }
    
    /**
     * @return type
     */
    @JsonProperty("type")
    public String getType() {
        return m_type;
    }
    
    /**
     * @param type 
     */
    public void setType(String type) {
        this.m_type = type;
    }
    
    /**
     * @return logicalName
     */
    @JsonProperty("logicalName")
    public List<String> getLogicalNames() {
        return m_logicalNames;
    }
    
    /**
     * @param logicalName 
     */
    public void addLogicalName(String logicalName) {
        this.m_logicalNames.add(logicalName);
    }
    
    /**
     * @return categorys
     */
    @JsonProperty("categorys")
    public List<String> getCategorys() {
        return m_categorys;
    }
    
    /**
     * @param category 
     */
    public void addCategory(String category) {
        this.m_categorys.add(category);
    }
}

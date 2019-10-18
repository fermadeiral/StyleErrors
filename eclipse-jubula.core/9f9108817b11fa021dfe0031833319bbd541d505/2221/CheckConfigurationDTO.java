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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jubula.client.core.model.ICheckConfPO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class CheckConfigurationDTO {

    /** */
    private String m_checkId, m_severity;
    /** */
    private boolean m_activated;
    /** */
    private List<CheckAttributeDTO> m_checkAttributes =
            new ArrayList<CheckAttributeDTO>();
    /** */
    private List<CheckActivatedContextDTO> m_checkActivatedContextes =
            new ArrayList<CheckActivatedContextDTO>();
    
    /** needed because JSON mapping */
    public CheckConfigurationDTO() { }

    /**
     * @param chkConf 
     */
    public CheckConfigurationDTO(ICheckConfPO chkConf) {
        this.setActivated(chkConf.isActive());
        this.setSeverity(chkConf.getSeverity());
        fillCheckAttribute(chkConf.getAttr());
        fillCheckContext(chkConf.getContexts());
    }
    
    /**
     * @return checkId
     */
    @JsonProperty("checkId")
    public String getCheckId() {
        return m_checkId;
    }
    
    /**
     * @param checkId 
     */
    public void setCheckId(String checkId) {
        this.m_checkId = checkId;
    }
    
    /**
     * @return severity
     */
    @JsonProperty("severity")
    public String getSeverity() {
        return m_severity;
    }
    
    /**
     * @param severity 
     */
    public void setSeverity(String severity) {
        this.m_severity = severity;
    }
    
    /**
     * @return activated
     */
    @JsonProperty("activated")
    public boolean isActivated() {
        return m_activated;
    }
    
    /**
     * @param activated 
     */
    public void setActivated(boolean activated) {
        this.m_activated = activated;
    }
    
    /**
     * @return checkAttributes
     */
    @JsonProperty("checkAttributes")
    public List<CheckAttributeDTO> getCheckAttributes() {
        return m_checkAttributes;
    }
    
    /**
     * @param checkAttribut 
     */
    public void addCheckAttribut(CheckAttributeDTO checkAttribut) {
        this.m_checkAttributes.add(checkAttribut);
    }
    
    /**
     * @return checkActivatedContextes
     */
    @JsonProperty("checkActivatedContextes")
    public List<CheckActivatedContextDTO> getCheckActivatedContextes() {
        return m_checkActivatedContextes;
    }
    
    /**
     * @param checkActivatedContext 
     */
    public void addCheckActivatedContext(
            CheckActivatedContextDTO checkActivatedContext) {
        this.m_checkActivatedContextes.add(checkActivatedContext);
    }
    
    /**
     * @param attr 
     */
    private void fillCheckAttribute(Map<String, String> attr) {
        for (Entry<String, String> e : attr.entrySet()) {
            CheckAttributeDTO chkAttr = new CheckAttributeDTO();
            chkAttr.setName(e.getKey());
            chkAttr.setValue(e.getValue());
            m_checkAttributes.add(chkAttr);
        }
    }
    
    /**
     * @param contexts 
     */
    private void fillCheckContext(Map<String, Boolean> contexts) {
        for (Entry<String, Boolean> e : contexts.entrySet()) {
            CheckActivatedContextDTO chkConf = new CheckActivatedContextDTO();
            chkConf.setClazz(e.getKey());
            Object obj = e.getValue();
            if (obj instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal)obj;
                chkConf.setActive(bd.equals(BigDecimal.ONE) ? true : false);
            } else {
                chkConf.setActive(e.getValue());
            }
            m_checkActivatedContextes.add(chkConf);
        }
    }
}

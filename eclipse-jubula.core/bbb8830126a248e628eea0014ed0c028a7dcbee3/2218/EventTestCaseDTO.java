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

import org.eclipse.jubula.client.core.model.INodePO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class EventTestCaseDTO extends RefTestCaseDTO {

    /** */
    private String m_eventType, m_reentryProperty;
    /** */
    private Integer m_maxRetries;

    
    /** needed because JSON mapping */
    public EventTestCaseDTO() { }
    
    /**
     * @param node 
     */
    public EventTestCaseDTO(INodePO node) {
        super(node);
    }
    
    /**
     * @return eventType
     */
    @JsonProperty("eventType")
    public String getEventType() {
        return m_eventType;
    }

    /**
     * @param eventType 
     */
    public void setEventType(String eventType) {
        this.m_eventType = eventType;
    }

    /**
     * @return reentryProperty
     */
    @JsonProperty("reentryProperty")
    public String getReentryProperty() {
        return m_reentryProperty;
    }

    /**
     * @param reentryProperty 
     */
    public void setReentryProperty(String reentryProperty) {
        this.m_reentryProperty = reentryProperty;
    }

    /**
     * @return maxRetries
     */
    @JsonProperty("maxRetries")
    public Integer getMaxRetries() {
        return m_maxRetries;
    }

    /**
     * @param maxRetries 
     */
    public void setMaxRetries(Integer maxRetries) {
        this.m_maxRetries = maxRetries;
    }
}

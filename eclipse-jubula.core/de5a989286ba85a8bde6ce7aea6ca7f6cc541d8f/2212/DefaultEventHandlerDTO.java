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
public class DefaultEventHandlerDTO {
    /** */
    private String m_event, m_reentryProperty;
    /** */
    private int m_maxRetries;
    
    /**
     * @return event
     */
    @JsonProperty("event")
    public String getEvent() {
        return m_event;
    }
    
    /**
     * @param event 
     */
    public void setEvent(String event) {
        this.m_event = event;
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
    public int getMaxRetries() {
        return m_maxRetries;
    }
    
    /**
     * @param maxRetries 
     */
    public void setMaxRetries(int maxRetries) {
        this.m_maxRetries = maxRetries;
    }
}
    

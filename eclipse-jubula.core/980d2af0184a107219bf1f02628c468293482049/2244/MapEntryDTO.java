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
public class MapEntryDTO {
    
    /** */
    private String m_key, m_value;
    
    
    /** needed because JSON mapping */
    public MapEntryDTO() { }
    
    /**
     * @param key 
     * @param value 
     */
    public MapEntryDTO(String key, String value) {
        this.m_key = key;
        this.m_value = value;
    }

    /**
     * @return key
     */
    @JsonProperty("key")
    public String getKey() {
        return m_key;
    }
    
    /**
     * @param key 
     */
    public void setKey(String key) {
        this.m_key = key;
    }

    /**
     * @return value
     */
    @JsonProperty("value")
    public String getValue() {
        return m_value;
    }

    /**
     * @param value 
     */
    public void setValue(String value) {
        this.m_value = value;
    }
}

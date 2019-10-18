/*******************************************************************************
 * Copyright (c) 2018 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.jubula.client.archive.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 *
 */
public class ValueSetDTO {
    
    /** */
    private String m_defaultValue;
    /** values with comment */
    private List<MapEntryDTO> m_valueComment;
    /** */
    public ValueSetDTO() {
        
    }
    /**
     * @param defaultValue default value
     * @param valueComment values with comment
     */
    public ValueSetDTO(String defaultValue, List<MapEntryDTO> valueComment) {
        m_defaultValue = defaultValue;
        m_valueComment = valueComment;
    }
    /**
     * @return the default value
     */
    @JsonProperty("defaultValue")
    public String getDefaultValue() {
        return m_defaultValue;
    }
    /**
     * @param defaultValue the default value
     */
    public void setDefaultValue(String defaultValue) {
        m_defaultValue = defaultValue;
    }
    /**
     * @return the values with comment
     */
    @JsonProperty("valueSet")
    public List<MapEntryDTO> getValueComment() {
        return m_valueComment;
    }
    /**
     * @param valueComment the values with comment
     */
    public void setValueComment(List<MapEntryDTO> valueComment) {
        m_valueComment = valueComment;
    }

}

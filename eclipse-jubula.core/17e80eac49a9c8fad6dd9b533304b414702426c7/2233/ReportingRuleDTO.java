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
public class ReportingRuleDTO {
    /** */
    private String m_name, m_fieldID, m_value, m_type;

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

    /**
     * @return fieldID
     */
    @JsonProperty("fieldID")
    public String getFieldID() {
        return m_fieldID;
    }

    /**
     * @param fieldID 
     */
    public void setFieldID(String fieldID) {
        this.m_fieldID = fieldID;
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
}
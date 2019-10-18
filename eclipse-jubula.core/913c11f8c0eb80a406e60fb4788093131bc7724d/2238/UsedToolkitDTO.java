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
public class UsedToolkitDTO {

    /** */
    private String m_name;
    /** */
    private Integer m_majorVersion, m_minorVersion;
    
    
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
     * @return majorVersion
     */
    @JsonProperty("majorVersion")
    public Integer getMajorVersion() {
        return m_majorVersion;
    }
    
    /**
     * @param majorVersion 
     */
    public void setMajorVersion(Integer majorVersion) {
        this.m_majorVersion = majorVersion;
    }
    
    /**
     * @return minorVersion
     */
    @JsonProperty("minorVersion")
    public Integer getMinorVersion() {
        return m_minorVersion;
    }
    
    /**
     * @param minorVersion 
     */
    public void setMinorVersion(Integer minorVersion) {
        this.m_minorVersion = minorVersion;
    }
}

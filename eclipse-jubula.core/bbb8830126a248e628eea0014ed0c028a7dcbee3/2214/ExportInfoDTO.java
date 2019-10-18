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

import org.osgi.framework.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/** @author BREDEX GmbH */
public class ExportInfoDTO {
    
    /** */
    private String m_encoding;
    /** */
    private int m_majorVersion, m_minorVersion, m_microVersion;
    /** */
    private String m_qualifier;
    
    
    /** needed because JSON mapping */
    public ExportInfoDTO() { }
    
    /**
     * @return encoding
     */
    @JsonProperty("encoding")
    public String getEncoding() {
        return m_encoding;
    }
    
    /**
     * @param encoding 
     */
    public void setEncoding(String encoding) {
        this.m_encoding = encoding;
    }
    
    /**
     * @return version of JSON export
     */
    @JsonIgnore
    public Version getVersion() {
        return new Version(
                getMajorVersion(), 
                getMinorVersion(), 
                getMicroVersion(), 
                getQualifier());
    }
    
    /**
     * @return majorVersion
     */
    @JsonProperty("majorVersion")
    public int getMajorVersion() {
        return m_majorVersion;
    }
    
    /**
     * @param majorVersion 
     */
    public void setMajorVersion(int majorVersion) {
        this.m_majorVersion = majorVersion;
    }
    
    /**
     * @return minorVersion
     */
    @JsonProperty("minorVersion")
    public int getMinorVersion() {
        return m_minorVersion;
    }
    
    /**
     * @param minorVersion 
     */
    public void setMinorVersion(int minorVersion) {
        this.m_minorVersion = minorVersion;
    }
    
    /**
     * @return microVersion
     */
    @JsonProperty("microVersion")
    public int getMicroVersion() {
        return m_microVersion;
    }
    
    /**
     * @param microVersion 
     */
    public void setMicroVersion(int microVersion) {
        this.m_microVersion = microVersion;
    }
    
    /**
     * @param version 
     */
    public void setVersion(Version version) {
        setMajorVersion(version.getMajor());
        setMinorVersion(version.getMinor());
        setMicroVersion(version.getMicro());
        setQualifier(version.getQualifier());
    }
    
    /**
     * @return qualifier
     */
    @JsonProperty("qualifier")
    public String getQualifier() {
        return m_qualifier;
    }
    
    /**
     * @param qualifier 
     */
    public void setQualifier(String qualifier) {
        this.m_qualifier = qualifier;
    }
}
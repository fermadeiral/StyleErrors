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
public class ObjectMappingProfileDTO {

    /** */
    private String m_name;
    /** */
    private double m_contextFactor, m_nameFactor, m_pathFactor,
        m_threshold;
    
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
     * @return contextFactor
     */
    @JsonProperty("contextFactor")
    public double getContextFactor() {
        return m_contextFactor;
    }
    
    /**
     * @param contextFactor 
     */
    public void setContextFactor(double contextFactor) {
        this.m_contextFactor = contextFactor;
    }
    
    /**
     * @return nameFactor
     */
    @JsonProperty("nameFactor")
    public double getNameFactor() {
        return m_nameFactor;
    }
    
    /**
     * @param nameFactor 
     */
    public void setNameFactor(double nameFactor) {
        this.m_nameFactor = nameFactor;
    }
    
    /**
     * @return pathFactor
     */
    @JsonProperty("pathFactor")
    public double getPathFactor() {
        return m_pathFactor;
    }
    
    /**
     * @param pathFactor 
     */
    public void setPathFactor(double pathFactor) {
        this.m_pathFactor = pathFactor;
    }
    
    /**
     * @return threshold
     */
    @JsonProperty("threshold")
    public double getThreshold() {
        return m_threshold;
    }
    
    /**
     * @param threshold 
     */
    public void setThreshold(double threshold) {
        this.m_threshold = threshold;
    }
}

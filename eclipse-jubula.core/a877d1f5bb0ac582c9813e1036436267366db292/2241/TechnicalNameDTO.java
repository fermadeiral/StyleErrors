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
public class TechnicalNameDTO {

    /** */
    private String m_componentClassName, m_supportedClassName,
        m_alternativeDisplayName;
    /** */
    private List<String> m_neighbours = new ArrayList<String>();
    /** */
    private List<String> m_hierarchyNames = new ArrayList<String>();
    /** */
    private ObjectMappingProfileDTO m_objectMappingProfile;
    
    /**
     * @return componentClassName
     */
    @JsonProperty("componentClassName")
    public String getComponentClassName() {
        return m_componentClassName;
    }
    
    /**
     * @param componentClassName 
     */
    public void setComponentClassName(String componentClassName) {
        this.m_componentClassName = componentClassName;
    }
    
    /**
     * @return supportedClassName
     */
    @JsonProperty("supportedClassName")
    public String getSupportedClassName() {
        return m_supportedClassName;
    }
    
    /**
     * @param supportedClassName 
     */
    public void setSupportedClassName(String supportedClassName) {
        this.m_supportedClassName = supportedClassName;
    }
    
    /**
     * @return alternativeDisplayName
     */
    @JsonProperty("alternativeDisplayName")
    public String getAlternativeDisplayName() {
        return m_alternativeDisplayName;
    }
    
    /**
     * @param alternativeDisplayName 
     */
    public void setAlternativeDisplayName(String alternativeDisplayName) {
        this.m_alternativeDisplayName = alternativeDisplayName;
    }
    
    /**
     * @return neighbours
     */
    @JsonProperty("neighbours")
    public List<String> getNeighbours() {
        return m_neighbours;
    }
    
    /**
     * @param neighbour 
     */
    public void addNeighbour(String neighbour) {
        this.m_neighbours.add(neighbour);
    }
    
    /**
     * @return hierarchyName
     */
    @JsonProperty("hierarchyName")
    public List<String> getHierarchyNames() {
        return m_hierarchyNames;
    }
    
    /**
     * @param hierarchyName 
     */
    public void addHierarchyName(String hierarchyName) {
        this.m_hierarchyNames.add(hierarchyName);
    }

    /**
     * @return objectMappingProfile 
     */
    @JsonProperty("objectMappingProfile")
    public ObjectMappingProfileDTO getObjectMappingProfile() {
        return m_objectMappingProfile;
    }

    /**
     * @param objectMappingProfile 
     */
    public void setObjectMappingProfile(ObjectMappingProfileDTO
            objectMappingProfile) {
        this.m_objectMappingProfile = objectMappingProfile;
    }
}

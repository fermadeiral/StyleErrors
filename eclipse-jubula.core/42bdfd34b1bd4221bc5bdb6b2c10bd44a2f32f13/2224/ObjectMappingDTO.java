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
public class ObjectMappingDTO {
    
    /** */
    private ObjectMappingProfileDTO m_profile;
    /** */
    private OmCategoryDTO m_mapped, m_unmappedComponent, m_unmappedTechnical;
    
    /**
     * @return profile
     */
    @JsonProperty("profile")
    public ObjectMappingProfileDTO getProfile() {
        return m_profile;
    }
    
    /**
     * @param profile 
     */
    public void setProfile(ObjectMappingProfileDTO profile) {
        this.m_profile = profile;
    }
    
    /**
     * @return mapped
     */
    @JsonProperty("mapped")
    public OmCategoryDTO getMapped() {
        return m_mapped;
    }
    
    /**
     * @param mapped 
     */
    public void setMapped(OmCategoryDTO mapped) {
        this.m_mapped = mapped;
    }
    
    /**
     * @return unmappedComponent
     */
    @JsonProperty("unmappedComponent")
    public OmCategoryDTO getUnmappedComponent() {
        return m_unmappedComponent;
    }
    
    /**
     * @param unmappedComponent 
     */
    public void setUnmappedComponent(OmCategoryDTO unmappedComponent) {
        this.m_unmappedComponent = unmappedComponent;
    }
    
    /**
     * @return unmappedTechnical
     */
    @JsonProperty("unmappedTechnical")
    public OmCategoryDTO getUnmappedTechnical() {
        return m_unmappedTechnical;
    }
    
    /**
     * @param unmappedTechnical 
     */
    public void setUnmappedTechnical(OmCategoryDTO unmappedTechnical) {
        this.m_unmappedTechnical = unmappedTechnical;
    }
}
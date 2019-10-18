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
public class OmCategoryDTO {
    
    /** */
    private String m_name;
    /** */
    private List<OmEntryDTO> m_associations = new ArrayList<OmEntryDTO>();
    /** */
    private List<OmCategoryDTO> m_categories = new ArrayList<OmCategoryDTO>();
    
    
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
     * @return association
     */
    @JsonProperty("association")
    public List<OmEntryDTO> getAssociations() {
        return m_associations;
    }
    
    /**
     * @param association 
     */
    public void addAssociation(OmEntryDTO association) {
        this.m_associations.add(association);
    }
    
    /**
     * @return categories
     */
    @JsonProperty("categories")
    public List<OmCategoryDTO> getCategories() {
        return m_categories;
    }
    
    /**
     * @param categorie 
     */
    public void addCategories(OmCategoryDTO categorie) {
        this.m_categories.add(categorie);
    }
}

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
public class TestDataCategoryDTO {
    /** */
    private String m_name;
    /** */
    private List<TestDataCategoryDTO> m_testDataCategories =
            new ArrayList<TestDataCategoryDTO>();
    /** */
    private List<NamedTestDataDTO> m_namedTestDatas =
            new ArrayList<NamedTestDataDTO>();
    
    
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
     * @return testDataCategorie
     */
    @JsonProperty("testDataCategorie")
    public List<TestDataCategoryDTO> getTestDataCategories() {
        return m_testDataCategories;
    }
    
    /**
     * @param testDataCategorie 
     */
    public void addTestDataCategorie(TestDataCategoryDTO testDataCategorie) {
        this.m_testDataCategories.add(testDataCategorie);
    }
    
    /**
     * @return namedTestDatas
     */
    @JsonProperty("namedTestDatas")
    public List<NamedTestDataDTO> getNamedTestDatas() {
        return m_namedTestDatas;
    }

    /**
     * @param namedTestData 
     */
    public void addNamedTestData(NamedTestDataDTO namedTestData) {
        this.m_namedTestDatas.add(namedTestData);
    }
}

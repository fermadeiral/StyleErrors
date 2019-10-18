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
public class TDManagerDTO {

    /** */
    private List<String> m_uniqueIds = new ArrayList<String>();
    /** */
    private List<DataRowDTO> m_dataSets = new ArrayList<DataRowDTO>();

    
    /**
     * @return uniqueIds
     */
    @JsonProperty("uniqueIds")
    public List<String> getUniqueIds() {
        return m_uniqueIds;
    }

    /**
     * @param uniqeIds 
     */
    public void setUniqueIds(List<String> uniqeIds) {
        this.m_uniqueIds = uniqeIds;
    }
    
    /**
     * @param uniqeId 
     */
    public void addUniqueId(String uniqeId) {
        this.m_uniqueIds.add(uniqeId);
    }

    /**
     * @return dataSets
     */
    @JsonProperty("dataSets")
    public List<DataRowDTO> getDataSets() {
        return m_dataSets;
    }

    /**
     * @param dataSets 
     */
    public void setDataSets(List<DataRowDTO> dataSets) {
        this.m_dataSets = dataSets;
    }

    /**
     * @param dataSet 
     */
    public void addDataSet(DataRowDTO dataSet) {
        this.m_dataSets.add(dataSet);
    }
}

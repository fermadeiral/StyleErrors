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
public class AutConfigDTO {

    /** */
    private String m_name, m_uuid;
    /** */
    private List<MapEntryDTO> m_confAttrMapEntrys =
            new ArrayList<MapEntryDTO>();

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
     * @return uuid
     */
    @JsonProperty("uuid")
    public String getUuid() {
        return m_uuid;
    }

    /**
     * @param uuid 
     */
    public void setUuid(String uuid) {
        this.m_uuid = uuid;
    }

    /**
     * @return confAttrMapEntrys
     */
    @JsonProperty("confAttrMapEntrys")
    public List<MapEntryDTO> getConfAttrMapEntry() {
        return m_confAttrMapEntrys;
    }

    /**
     * @param confAttrMapEntry 
     */
    public void addConfAttrMapEntry(MapEntryDTO confAttrMapEntry) {
        this.m_confAttrMapEntrys.add(confAttrMapEntry);
    }
}

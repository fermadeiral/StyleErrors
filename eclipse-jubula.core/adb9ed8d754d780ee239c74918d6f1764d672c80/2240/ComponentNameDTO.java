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
public class ComponentNameDTO {

    /** */
    private String m_compType, m_uuid, m_creationContext, m_refUuid, m_compName;

    /**
     * @return compType
     */
    @JsonProperty("compType")
    public String getCompType() {
        return m_compType;
    }

    /**
     * @param compType 
     */
    public void setCompType(String compType) {
        this.m_compType = compType;
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
     * @return creationContext
     */
    @JsonProperty("creationContext")
    public String getCreationContext() {
        return m_creationContext;
    }

    /**
     * @param creationContext 
     */
    public void setCreationContext(String creationContext) {
        this.m_creationContext = creationContext;
    }

    /**
     * @return refUuid
     */
    @JsonProperty("refUuid")
    public String getRefUuid() {
        return m_refUuid;
    }

    /**
     * @param refUuid 
     */
    public void setRefUuid(String refUuid) {
        this.m_refUuid = refUuid;
    }

    /**
     * @return compName
     */
    @JsonProperty("compName")
    public String getCompName() {
        return m_compName;
    }

    /**
     * @param compName 
     */
    public void setCompName(String compName) {
        this.m_compName = compName;
    }
}

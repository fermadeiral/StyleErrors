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

import org.eclipse.jubula.client.core.model.IRefTestSuitePO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class RefTestSuiteDTO extends NodeDTO {

    /** */
    private String m_tsUuid, m_autId;

    
    /** needed because JSON mapping */
    public RefTestSuiteDTO() { }
    
    /**
     * @param node 
     */
    public RefTestSuiteDTO(IRefTestSuitePO node) {
        super(node);
        this.m_tsUuid = node.getTestSuiteGuid();
        this.m_autId = node.getTestSuiteAutID();
    }

    /**
     * @return tsUuid
     */
    @JsonProperty("tsUuid")
    public String getTsUuid() {
        return m_tsUuid;
    }

    /**
     * @param tsUuid 
     */
    public void setTsUuid(String tsUuid) {
        this.m_tsUuid = tsUuid;
    }

    /**
     * @return autId
     */
    @JsonProperty("autId")
    public String getAutId() {
        return m_autId;
    }

    /**
     * @param autId 
     */
    public void setAutId(String autId) {
        this.m_autId = autId;
    }
}

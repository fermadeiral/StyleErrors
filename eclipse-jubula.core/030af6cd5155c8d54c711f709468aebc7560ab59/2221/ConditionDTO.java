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

import org.eclipse.jubula.client.core.model.INodePO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

/**
 * @author BREDEX GmbH
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,  
        include = JsonTypeInfo.As.PROPERTY,  
        property = "type") 
@JsonSubTypes({
    @Type(value = ConditionalStatementDTO.class, name = "cs"),
    @Type(value = WhileDTO.class, name = "whi")})
public class ConditionDTO extends NodeDTO {

    /** is negated */
    private boolean m_isNegated = false;
    
    /** needed because JSON mapping */
    public ConditionDTO() { }
    
    /**
     * @param node condition node
     */
    public ConditionDTO(INodePO node) {
        super(node);
    }
    
    /**
     * @return interfaceLocked
     */
    @JsonProperty("isNegated")
    public boolean isNegated() {
        return m_isNegated;
    }

    /**
     * @param isNegated 
     */
    public void setNegated(boolean isNegated) {
        m_isNegated = isNegated;
    }

}

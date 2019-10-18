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

/**
 * @author BREDEX GmbH
 */
public class CapDTO extends ParameterDTO {

    /** */
    private String m_actionName, m_componentName, m_componentType;
    
    
    /** needed because JSON mapping */
    public CapDTO() { }

    /**
     * @param node 
     */
    public CapDTO(INodePO node) {
        super(node);
    }

    /**
     * @return actionName
     */
    @JsonProperty("actionName")
    public String getActionName() {
        return m_actionName;
    }

    /**
     * @param actionName 
     */
    public void setActionName(String actionName) {
        this.m_actionName = actionName;
    }

    /** 
     * @return componentName
     */
    @JsonProperty("componentName")
    public String getComponentName() {
        return m_componentName;
    }

    /**
     * @param componentName 
     */
    public void setComponentName(String componentName) {
        this.m_componentName = componentName;
    }

    /**
     * @return componentType
     */
    @JsonProperty("componentType")
    public String getComponentType() {
        return m_componentType;
    }

    /**
     * @param componentType 
     */
    public void setComponentType(String componentType) {
        this.m_componentType = componentType;
    }
}

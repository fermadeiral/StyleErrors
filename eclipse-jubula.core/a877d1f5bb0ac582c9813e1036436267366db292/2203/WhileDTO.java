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
public class WhileDTO extends ConditionDTO {
    
    /** is negated */
    private boolean m_isDoWhile = false;
    
    /** needed because JSON mapping */
    public WhileDTO() { }
    
    /**
     * @param node condition node
     */
    public WhileDTO(INodePO node) {
        super(node);
    }
    /**
     * @return the negation
     */
    @JsonProperty("isDoWhile")
    public boolean isDoWhile() {
        return m_isDoWhile;
    }
    /**
     * @param isDoWhile is it a Do While loop?
     */
    public void setDoWhile(boolean isDoWhile) {
        m_isDoWhile = isDoWhile;
    }

}

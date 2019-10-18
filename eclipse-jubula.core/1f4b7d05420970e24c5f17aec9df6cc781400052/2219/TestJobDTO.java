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

import org.eclipse.jubula.client.core.model.INodePO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class TestJobDTO extends NodeDTO {
    /** */
    private List<NodeDTO> m_refTestSuites =
            new ArrayList<NodeDTO>();

    
    /** needed because JSON mapping */
    public TestJobDTO() { }
    
    /**
     * @param node 
     */
    public TestJobDTO(INodePO node) {
        super(node);
    }
    
    /**
     * @param rtsDTO 
     */
    public void addRefTestSuite(RefTestSuiteDTO rtsDTO) {
        m_refTestSuites.add(rtsDTO);
    }
    
    /**
     * @param commentDTO 
     */
    public void addComment(CommentDTO commentDTO) {
        m_refTestSuites.add(commentDTO);
    }

    /**
     * @return refTestSuites
     */
    @JsonProperty("refTestSuites")
    public List<NodeDTO> getRefTestSuites() {
        return m_refTestSuites;
    }
}

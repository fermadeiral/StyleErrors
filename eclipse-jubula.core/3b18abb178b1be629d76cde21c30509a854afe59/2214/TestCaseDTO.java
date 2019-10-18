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
public class TestCaseDTO extends ParameterDTO {
    /** it could contain CAP or RefTestCase */
    private List<NodeDTO> m_testSteps = new ArrayList<NodeDTO>();
    /** */
    private List<EventTestCaseDTO> m_eventTestcases =
            new ArrayList<EventTestCaseDTO>(4);
    /** */
    private boolean m_interfaceLocked = false;
    
    
    /** needed because JSON mapping */
    public TestCaseDTO() { }
    
    /**
     * @param node 
     */
    public TestCaseDTO(INodePO node) {
        super(node);
    }

    /**
     * @return testSteps
     */
    @JsonProperty("testSteps")
    public List<NodeDTO> getTestSteps() {
        return m_testSteps;
    }

    /**
     * 
     * @param testStep 
     */
    public void addTestStep(NodeDTO testStep) {
        if (!(testStep instanceof RefTestCaseDTO
                || testStep instanceof CapDTO
                || testStep instanceof CommentDTO
                || testStep instanceof ConditionalStatementDTO
                || testStep instanceof WhileDTO
                || testStep instanceof IterateDTO)) {
            throw new IllegalArgumentException();
        }
        this.m_testSteps.add(testStep);
    }

    /**
     * @return eventTestcases
     */
    @JsonProperty("eventTestcases")
    public List<EventTestCaseDTO> getEventTestcases() {
        return m_eventTestcases;
    }

    /**
     * @param eventTestcase 
     */
    public void addEventTestcase(EventTestCaseDTO eventTestcase) {
        this.m_eventTestcases.add(eventTestcase);
    }

    /**
     * @return interfaceLocked
     */
    @JsonProperty("interfaceLocked")
    public boolean isInterfaceLocked() {
        return m_interfaceLocked;
    }

    /**
     * @param interfaceLocked 
     */
    public void setInterfaceLocked(boolean interfaceLocked) {
        this.m_interfaceLocked = interfaceLocked;
    }
}
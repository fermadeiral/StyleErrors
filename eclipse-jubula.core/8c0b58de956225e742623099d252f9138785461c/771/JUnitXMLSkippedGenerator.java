/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.exporter.junit;

import org.eclipse.jubula.client.core.exporter.junitmodel.ObjectFactory;
import org.eclipse.jubula.client.core.exporter.junitmodel.Skipped;
import org.eclipse.jubula.client.core.exporter.junitmodel.Testcase;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;

/**
 * @author Bredex Gmbh
 *
 */
public class JUnitXMLSkippedGenerator  implements
    ITreeNodeOperation<TestResultNode> {

    /**
     * the rootNode from where the traversion starts
     */
    private TestResultNode m_rootNode;
    
    /**
     * the testCase that gets filled with information
     */
    private Testcase m_testCase;

    /**
     * @param testRes rootNode for traversion
     * @param testCase the testCase that gets filled with information
     */
    public JUnitXMLSkippedGenerator(TestResultNode testRes,
            Testcase testCase) {
        setTestCase(testCase);
        setRootNode(testRes);
    }
    
    

    /**
     * @return the root at which the traversion started
     */
    public TestResultNode getRootNode() {
        return m_rootNode;
    }

    /**
     * @param rootNode the root at which the traversion started
     */
    public void setRootNode(TestResultNode rootNode) {
        this.m_rootNode = rootNode;
    }

    /**
     * @return testcase that is to be filled with information
     */
    public Testcase getTestC() {
        return m_testCase;
    }

    /**
     * @param testC testcase that is to be filled with information
     */
    public void setTestCase(Testcase testC) {
        this.m_testCase = testC;
    }



    @Override
    public boolean operate(ITreeTraverserContext<TestResultNode> ctx,
            TestResultNode parent, TestResultNode node,
            boolean alreadyVisited) {
        if (!alreadyVisited) {
            ObjectFactory obFskip = new ObjectFactory();
            Skipped skip = obFskip.createSkipped();
            m_testCase.getSkippedOrErrorOrFailure().add(skip);
            return false;
        }
        return false;
    }



    @Override
    public void postOperate(ITreeTraverserContext<TestResultNode> ctx,
            TestResultNode parent, TestResultNode node,
            boolean alreadyVisited) {
        //not used
    }

}

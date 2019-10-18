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

import java.util.List;

import org.eclipse.jubula.client.core.exporter.junitmodel.ObjectFactory;
import org.eclipse.jubula.client.core.exporter.junitmodel.Testsuite;
import org.eclipse.jubula.client.core.exporter.junitmodel.Testsuites;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ITestResult;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;

/**
 * @author Bredex Gmbh
 */
public class SuiteFinderOperation implements
    ITreeNodeOperation<TestResultNode> {
    
/**
 * the TestResultNode that is used as the rootNode for traversing the tree
 */
    private TestResultNode m_testRes;
    
    /**
     * JAXB object containing a list of all testsuites
     */
    private Testsuites m_project;
    
    
    /**
     * list of TestResultNodes representing Testsuites
     */
    private List<TestResultNode> m_suiteList;
    
    /**
     * The result of the executed test that is being processed
     */
    private ITestResult m_testResult;
    
    /**
     * Constructor
     * Calls multiple set-methods for initilization
     * 
     * @param testRes the TestResultNode that is used as the rootNode for traversing the tree
     * @param project JAXB object containing a list of all testsuites
     * @param list a list of TestResultNodes
     * @param testResult TestResult
     */
    public SuiteFinderOperation(TestResultNode testRes, Testsuites project,
            List<TestResultNode> list, ITestResult testResult) {
        setTestRes(testRes);
        setProject(project);
        setSuiteList(list);
        setTestresult(testResult);
    }

    /**
     * Sets the TestResultNode that will be used the rootNode 
     * for the traversion of the content tree
     * 
     * @param testRes2 a TestResultNode
     */
    private void setTestresult(ITestResult testRes2) {
        m_testResult = testRes2;
    }

    @Override
public boolean operate(ITreeTraverserContext<TestResultNode> ctx,
        TestResultNode parent, TestResultNode node,
        boolean alreadyVisited) {

        if (node instanceof ICapPO) {
            return false;
        }
        if (node.isJunitTestSuite()) {
            createAndAddTestSuite(node);
            return false;
        }
        
        return true;
    }

    /**
     * Creates TestSuite object and fills it with the information gathered
     * from the provided node
     * 
     * @param node the node representing a testsuite
     * 
     */
    private void createAndAddTestSuite(TestResultNode node) {
        ObjectFactory obF = new ObjectFactory();
        Testsuite suite = obF.createTestsuite();
        suite.setName(node.getName());
        if (node.getTimeStamp() != null) {
            suite.setTimestamp(node.getTimeStamp().toString());
            suite.setTime(convertTime
                    (node.getDuration(m_testResult.getEndTime())));
        }
        m_suiteList.add(node);        
        m_project.getTestsuite().add(suite);
    }


    
    /**
     * @param l the time value (long) to be converted
     * @return the converted time value as a String
     */
    private String convertTime(long l) {
        String convertedtime;
        convertedtime = Double.toString((l) / 1000.0); //$NON-NLS-1$
        return convertedtime;
    }
    
    @Override
public void postOperate(ITreeTraverserContext<TestResultNode> ctx,
        TestResultNode parent, TestResultNode node,
        boolean alreadyVisited) {
        //not used
    }

/**
 * @return the TestResultNode that is used as the rootNode for traversing the tree
 */
    public TestResultNode getTestRes() {
        return m_testRes;
    }

/**
 * @param testResult the TestResultNode that is used as the rootNode for traversing the tree
 */
    public void setTestRes(TestResultNode testResult) {
        m_testRes = testResult;
    }

/**
 * @return JAXB object containing a list of all testsuites
 */
    public Testsuites getProject() {
        return m_project;
    }

/**
 * @param project contains list of all testsuites
 */
    public void setProject(Testsuites project) {
        m_project = project;
    }

/**
 * @return list of testsuites
 */
    public List<TestResultNode> getSuiteList() {
        return m_suiteList;
    }

/**
 * @param suiteList list of testsuites
 */
    public void setSuiteList(List<TestResultNode> suiteList) {
        this.m_suiteList = suiteList;
    }

}
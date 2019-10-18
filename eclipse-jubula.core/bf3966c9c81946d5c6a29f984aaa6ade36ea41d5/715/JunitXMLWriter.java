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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.CharEncoding;
import org.eclipse.jubula.client.core.exporter.junitmodel.ObjectFactory;
import org.eclipse.jubula.client.core.exporter.junitmodel.Testcase;
import org.eclipse.jubula.client.core.exporter.junitmodel.Testsuite;
import org.eclipse.jubula.client.core.exporter.junitmodel.Testsuites;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IControllerPO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.ITestResult;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.testresult.export.ITestResultExporter;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.TestResultNodeTraverser;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author marcelk
 * 2017
 */
public class JunitXMLWriter implements ITestResultExporter {
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(JunitXMLWriter.class);
    
    /**
     * Objectfactory for generating JAXB objects like Testsuites, Testsuite and
     * Testcase aswell as Error or Failure.
     */
    private ObjectFactory m_objF;

    /**
     * The project which contains the testsuites
     */
    private Testsuites m_project;

    /**
     * The result of the Jubula test
     */
    private ITestResult m_testResult;

    /**
     * The testResultNode to be used as rootnode
     */
    private TestResultNode m_resultNode;

    /**
     * list of TestSuites
     */
    private List<TestResultNode> m_testSuiteList;

    /**
     * Generating the Head of the XMLfile (TestSuites object)
     * 
     */
    private void generateHead() {
        //Creating TestSuites object
        setTestSuites();
        setSuitesData(m_resultNode, m_project, m_testSuiteList);
    }

    /**
     * Creates Testsuites node via Objectfactory
     */
    private void setTestSuites() {
        m_project = m_objF.createTestsuites();
        m_project.setTime((m_testResult.getStartTime()).toString());
        m_project.setName(m_testResult.getProjectName());
    }

    /**
     * Sets the TestResult element
     * 
     * @param testResult
     *            the test result
     */
    private void setTestResult(ITestResult testResult) {
        m_testResult = testResult;
    }

    /**
     * Creates a file to write in and returns it
     * 
     * @param path the path of the target location
     * @param filename the name of the created file
     * 
     * @throws IOException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @return fileToWrite the file created
     */
    public File generateFileForExport(String path, String filename) throws
        UnsupportedEncodingException, FileNotFoundException, IOException {

        File fileToWrite = new File(path,
                filename
            + "-junit" + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
        return fileToWrite;
    }

    /**
     * Converts Time from long into a string with the format: "1234.567"
     * 
     * @param l the time that is to be converted
     * @return a string representing the converted time
     */
    private String convertTime(long l) {
        String convertedtime;
        convertedtime = (Double.toString(l / 1000.0));
        return convertedtime;
    }

    /**
     * Searches in the childs of the testsuite for Testcases and saves them in a
     * list object
     */
    private void findTestcasesForTestsuite() {
        //counter for handling the filling of the different Testsuite-JAXB objects in relation to their TestResultNode equivalent
        int count = 0;
        List<Testsuite> tempSuiteList = m_project.getTestsuite();
        //
        for (TestResultNode testResultNode : m_testSuiteList) {
            List<TestResultNode>testcaseList =
                    testResultNode.getResultNodeList();
            for (TestResultNode testRes : testcaseList) {
                if (testRes.getNode() instanceof ITestCasePO) {
                    createJAXBTestCase(testRes, tempSuiteList, count); 
                } else if (testRes.getNode() instanceof ICapPO) {
                    //Handling of the sitaution that a suite has no CAPs/Steps
                    createJAXBTestCase(testRes, tempSuiteList, count); 
                } else if (testRes.getNode() instanceof IControllerPO) {
                    //Handling of the occurence of conditional statements as
                    //direct children of the JUnitTestSutie
                    createJAXBTestCase(testRes, tempSuiteList, count);
                }
            }
            //For filling the next suite once the current one has been worked through
            count++;
        }
    }

    /**Creates a JAXB TestCase object based on the state of the TestResultNode
     * it belongs to
     * 
     * @param testRes the TestResultNode that is being processed
     * @param tempSuiteList list of TestSuites
     * @param count a counter for filling the next suite once the current one has been worked through
     */
    private void createJAXBTestCase(TestResultNode testRes,
            List<Testsuite> tempSuiteList, int count) {
        Testcase testCase = m_objF.createTestcase();
        if (testRes.getNode() instanceof IControllerPO) {
            testCase.setName("(Condition)" + StringConstants.SPACE  //$NON-NLS-1$
                    + testRes.getName());
        } else {
            testCase.setName(testRes.getName());
        }
        testCase.setTime(convertTime
                (testRes.getDuration(m_testResult.getEndTime())));
        testCase.setStatus(testRes.getStatusString());
        if (testRes.getStatus() == TestResultNode.ERROR_IN_CHILD) {
            testCaseHandling(testRes, testCase, false);
        } else if (testRes.getStatus() == TestResultNode.SKIPPED 
                || testRes.getStatus() == TestResultNode.NOT_YET_TESTED) {
            //Handling of skipped TestCases
            testCaseHandling(testRes, testCase, true);
            testCase.setTime("0"); //$NON-NLS-1$
        } else if (testRes.getStatus() != TestResultNode.SUCCESS) {
            //Handling of all other possible situations
            testCaseHandling(testRes, testCase, false);
        }
        //Gets the TestSuite that is currently being filled and adds the newly 
        //created element to its list which can contain elements of the type:
        //TestSuite, TestCase or Properties.
        //It will however only get filled with TestCase elements
        tempSuiteList.get(count).
        getTestsuiteOrPropertiesOrTestcase().add(testCase);
    }
    
    /**
     * Finds the elements that are defined as TestSuite elements via an Attribute in Jubula
     * and adds them to the list of TestSuite objects of the TestSuites object
     * 
     * @param project contains data about the project and its testsuites
     * @param testRes the rootNode of the traversion
     * @param list list of testsuites
     */
    private void setSuitesData(TestResultNode testRes,
            Testsuites project, List<TestResultNode> list) {
        SuiteFinderOperation suiteFinder = new SuiteFinderOperation(testRes,
                project, list, m_testResult);
        TestResultNodeTraverser testResTraverser = 
                new TestResultNodeTraverser(testRes, suiteFinder);
        testResTraverser.traverse();
    }

    /**
     * Used for creating and adding information about errors and failures
     * to a testcase
     * 
     * @param node the rootnode for the resultNodetraverser
     * @param testCase the testcase that is to receive information
     * @param skipped boolean value whether the TestCase was skipped
     */
    private void testCaseHandling(TestResultNode node,
            Testcase testCase, boolean skipped) {
        ITreeNodeOperation<TestResultNode> jxgOperat = null;
        if (skipped) {
            jxgOperat =
                    new JUnitXMLSkippedGenerator(node, testCase);
        } else {
            jxgOperat = new JUnitXMLGeneratorOperations(node, testCase);
        }
        TestResultNodeTraverser testResTraverser =
                new TestResultNodeTraverser(node, jxgOperat);
        testResTraverser.traverse();
    }

    /* 
     * Initiliazing of the JUnitReportWriter (previously done in Constructor)
     * 
     * 
     * @param result the result of the test, which is to be exported
     */
    @Override
    public void initiliaze(ITestResult result) {
        m_objF = new ObjectFactory();
        setTestResult(result);
        m_resultNode = result.getRootResultNode();
        m_testSuiteList = new ArrayList<TestResultNode>();
        generateHead();
    }

    /**
     * Writes the generated content tree into a file provided by the
     * generateFileForExport() function
     * 
     * @param path targetpath for the file
     * 
     * @throws JAXBException 
     * @throws IOException {@link JunitXMLWriter#generateFileForExport(String, String)}
     * @throws UnsupportedEncodingException
     */
    @Override
    public void writeTestResult(String path, String filename) {
        
        //Finds and adds the TestCases of the TestSuite elements, that were defined in 
        findTestcasesForTestsuite();

        //Init of of JACBContext object
        javax.xml.bind.JAXBContext jaxbCtx = null;
        try {
            //Setup
            jaxbCtx = javax.xml.bind.JAXBContext
                    .newInstance(m_project.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = null;
            marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.
                    JAXB_ENCODING, CharEncoding.UTF_8);
            marshaller.setProperty(javax.xml.bind.Marshaller.
                    JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            //Execution of filling a file with information
            marshaller.marshal(m_project, generateFileForExport(path, filename).
                    getAbsoluteFile());
        } catch (IOException | JAXBException e) {
            log.error("Exception while writing TestResults to file", e); //$NON-NLS-1$
        }
        
    }


}

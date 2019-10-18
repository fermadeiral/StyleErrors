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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.exporter.junitmodel.Error;
import org.eclipse.jubula.client.core.exporter.junitmodel.Failure;
import org.eclipse.jubula.client.core.exporter.junitmodel.ObjectFactory;
import org.eclipse.jubula.client.core.exporter.junitmodel.Testcase;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.model.TestResultParameter;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * @author Bredex Gmbh
 *
 */
public class JUnitXMLGeneratorOperations implements
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
     * @param node rootNode for traversion
     * @param tCase the testCase that gets filled with information
     */
    public JUnitXMLGeneratorOperations(TestResultNode node, Testcase tCase) {
        setRootNode(node);
        setTestCase(tCase);
    }

    @Override
    public boolean operate(ITreeTraverserContext<TestResultNode> ctx,
            TestResultNode parent, TestResultNode node,
            boolean alreadyVisited) {
        if (alreadyVisited) {
            return false;
        } else if (!alreadyVisited) {
            switch (node.getStatus()) {
            
                case TestResultNode.SUCCESS_ONLY_SKIPPED:
                //succesfull test
                    return false;
               
                case TestResultNode.ERROR:
                    ObjectFactory obF = new ObjectFactory();
                    StringBuilder sb = new StringBuilder();
                    sb.append(StringConstants.NEWLINE + "Path: "  //$NON-NLS-1$
                        + getTreePathString(ctx)
                        + StringConstants.NEWLINE);
                    if (TestErrorEvent.ID.VERIFY_FAILED.
                            equals(node.getEvent().getId())) {
                        Error err = obF.createError();
                        sb.append(collectInformationForMessage(node, true));
                        err.setContent(sb.toString());
                        m_testCase.getSkippedOrErrorOrFailure().add(err);
                    } else {
                        Failure fail = obF.createFailure();
                        sb.append(collectInformationForMessage(node, false));
                        fail.setContent(sb.toString());
                        m_testCase.getSkippedOrErrorOrFailure().add(fail);
                    }
                //an error occurred
                    return false;
                
                case TestResultNode.ERROR_IN_CHILD:
                //error in children
                    return true;
                
                default:
                    return false;
            }
            
        }
        //shouldnt be reached!!!!
        return false;
    }
    
    /**
     * generates the String that is being used for the content
     * (the part of the testcase, that is displayed in the Failure trace) of the Testcase
     * @param node the node at which the Error or Failure occurred
     * @param errorOrFailure true when an Error occurred and fales when a Failure occurred
     * @return the collect information in a String
     */
    private String collectInformationForMessage(TestResultNode node,
            Boolean errorOrFailure) {
        StringBuilder sb = new StringBuilder();
        sb.append(org.eclipse.jubula.client.core.exporter.i18n
                .Messages.stepName + StringConstants.SPACE
                + node.getName() + StringConstants.NEWLINE); 
        sb.append(org.eclipse.jubula.client.core.exporter.i18n
                .Messages.stepStatus + StringConstants.SPACE
                + node.getStatusString() + StringConstants.NEWLINE); 
        sb.append(org.eclipse.jubula.client.core.exporter.i18n
                .Messages.timestamp + StringConstants.SPACE
                + node.getTimeStamp() + StringConstants.NEWLINE); 
        if (node.getComponentName() != null && !"".//$NON-NLS-1$
                equals(node.getComponentName())) { 
            sb.append(org.eclipse.jubula.client.core.exporter.i18n
                    .Messages.componentName + StringConstants.SPACE
                    + node.getComponentName() + StringConstants.NEWLINE); 
        }
        if (StringUtils.isNotBlank(node.getComponentType())) {
            sb.append(org.eclipse.jubula.client.core.exporter.i18n
                    .Messages.componentType + StringConstants.SPACE
                    + node.getComponentType() + StringConstants.NEWLINE); 
        }
        sb.append("_______________" + StringConstants.NEWLINE); //$NON-NLS-1$
        if (errorOrFailure) {
            TestErrorEvent errorEvent = node.getEvent();
            sb.append(org.eclipse.jubula.client.core.exporter.i18n
                    .Messages.errorType
                    + StringConstants.SPACE
                    + I18n.getString(errorEvent.getId()) 
                    + StringConstants.NEWLINE);
            sb.append(collectPropertyInformation(errorEvent));
        }
        if (!errorOrFailure) {
            TestErrorEvent errorEvent = node.getEvent();
            sb.append(org.eclipse.jubula.client.core.exporter.i18n
                    .Messages.failureType + StringConstants.SPACE
                + I18n.getString(errorEvent.getId()) 
                + StringConstants.NEWLINE);
            sb.append(collectPropertyInformation(errorEvent));
        }
        sb.append("________________" + StringConstants.NEWLINE); //$NON-NLS-1$
        List<TestResultParameter> paraList = node.getParameters();
        for (TestResultParameter testResultParameter : paraList) {
            sb.append(org.eclipse.jubula.client.core.exporter.i18n
                    .Messages.parameterName + StringConstants.SPACE
                + testResultParameter.getName() + StringConstants.NEWLINE);
            sb.append(org.eclipse.jubula.client.core.exporter.i18n
                    .Messages.parameterType + StringConstants.SPACE
                + testResultParameter.getType() + StringConstants.NEWLINE);
            sb.append(org.eclipse.jubula.client.core.exporter.i18n
                    .Messages.parameterValue + StringConstants.SPACE
                + testResultParameter.getValue() + StringConstants.NEWLINE); 
            sb.append("______" + StringConstants.NEWLINE);  //$NON-NLS-1$
        }
        sb.append("=================" + StringConstants.NEWLINE); //$NON-NLS-1$
        String failureTraceMessage = sb.toString();
        
        return failureTraceMessage;
    }
    
    /**Collects and returns the information of the properties for the
     * given occurred Error/Failure
     * 
     * @param errorEvent the Error/Failure that occurred during the test execution
     * @return a String of the collected information
     */
    private String collectPropertyInformation(TestErrorEvent errorEvent) {
        StringBuilder sb = new StringBuilder();
        sb.append(org.eclipse.jubula.client.core.exporter.i18n
                .Messages.expectedValue + StringConstants.SPACE
                + errorEvent.getProps().get(
                        org.eclipse.jubula.client.core.exporter.i18n
                        .Messages.guidancerExpectedValue) 
                + StringConstants.NEWLINE);
        sb.append(org.eclipse.jubula.client.core.exporter.i18n
                .Messages.actualValue + StringConstants.SPACE
                + errorEvent.getProps().get(org.eclipse.jubula
                        .client.core.exporter.i18n
                        .Messages.guidancerActualValue) 
                + StringConstants.NEWLINE);
        return sb.toString();
    }

    /**
     * fetches the Callpath to the current node
     * @param ctx theTreeTraverserContext
     * @return path to current node as String
     */
    private String getTreePathString(
            ITreeTraverserContext<TestResultNode> ctx) {
        List<TestResultNode> resNodes = ctx.getCurrentTreePath();
        StringBuilder stringBuilder = new StringBuilder();
        for (TestResultNode testResultNode : resNodes) {
            stringBuilder.append(StringConstants.SLASH 
                    + testResultNode.getName());
        }
        return stringBuilder.toString();
    }

    @Override
    public void postOperate(ITreeTraverserContext<TestResultNode> ctx,
            TestResultNode parent, TestResultNode node,
            boolean alreadyVisited) {
        //not used
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
    public Testcase getTestCase() {
        return m_testCase;
    }

    /**
     * @param testC testcase that is to be filled with information
     */
    public void setTestCase(Testcase testC) {
        this.m_testCase = testC;
    }

}

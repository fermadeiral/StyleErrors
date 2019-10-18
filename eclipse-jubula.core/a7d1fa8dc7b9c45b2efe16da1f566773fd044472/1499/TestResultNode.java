/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.businessprocess.ITestResultEventListener;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.propertytester.NodePropertyTester;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * class for creation of resultNodes
 * @author BREDEX GmbH
 * @created 21.04.2005
 */
public class TestResultNode {
    /**
     * Status if not yet tested
     */
    public static final int NOT_YET_TESTED = 0;
    /**
     * Status if test is successful
     */
    public static final int SUCCESS = 1;
    /**
     * Status if test is not successful
     */
    public static final int ERROR = 2;
    /**
     * Status if no verify must do.
     */
    public static final int NO_VERIFY = 3;
    /**
     * Status if test is not tested
     */
    public static final int NOT_TESTED = 4;
    /**
     * Error in child
     */
    public static final int ERROR_IN_CHILD = 5;

    /**
     * Status if currently being tested
     */
    public static final int TESTING = 6;

    /**
     * Status if not successful, but will be retried later
     */
    public static final int RETRYING = 7;

    /**
     * Status if test is successful after 1 or more retries
     */
    public static final int SUCCESS_RETRY = 8;
    
    /**
     * Status if test was aborted due to internal AutServer errors
     */
    public static final int ABORT = 9;
    
    /** Status if Condition failed */
    public static final int CONDITION_FAILED = 10;
    
    /** Status if an infinite loop was encountered */
    public static final int INFINITE_LOOP = 11;

    /**
     * Status if the test was skipped
     */
    public static final int SKIPPED = 20;
    
    /**
     * Status if the test is successful but contains only skipped test cases
     */
    public static final int SUCCESS_ONLY_SKIPPED = 21;
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(TestResultNode.class);
    
    /** separator for Parameter values */
    private static final String SEPARATOR = ", "; //$NON-NLS-1$
    
    /** length of Parameter value separator string */
    private static final int SEPARATOR_LEN = SEPARATOR.length();
    
    /** */
    private static final String NEGATED = "Negated"; //$NON-NLS-1$
    
    /**
     * index for Tree Tracker
     */
    private int m_childIndex = -1;
    
    /**
     * The status
     */
    private int m_status = 0;
    
    /**
     * Time of Teststep Execution
     */
    private Date m_timestamp = null;
    
    /**
     * <code>m_screenshot</code> the screenshot in case of a test error event
     */
    private byte[] m_screenshot = null; 
    
    /**
     * errorEvent, indicated from server 
     */
    private TestErrorEvent m_event;
    
    /**
     * <code>m_node</code> associated node in testexecution tree
     */
    private INodePO m_node;
    
    /** the Component Name name for this result node */
    private String m_componentName;

    /** the Component Name type for this result node */
    private String m_componentType;
    
    /**
     * whether the Node acts as a JUnit testsuite
     * true = this testResultNode wll be used as a testsuite
     * false = testResultNode is treated as the kind of node it is normally
     */
    private boolean m_isJunitTestSuite;

    /**
     * <code>m_resultNodeList</code> childList
     */
    private List < TestResultNode > m_resultNodeList  = 
        new ArrayList < TestResultNode > ();
    
    /**
     * <code>m_parent</code> parent resultNode
     */
    private TestResultNode m_parent;
    
    /**
     * Parameters used during test execution for the corresponding keyword.
     */
    private List<TestResultParameter> m_parameters = 
            new LinkedList<TestResultParameter>();

    /** the name of the Action that caused this Test Result */
    private String m_actionName;
    
    /**
     * The listener
     */
    private List < ITestResultEventListener > m_listener = 
        new ArrayList < ITestResultEventListener > ();
    
    /** 
     * flag indicating whether the backing node / keyword for this result 
     * can be found
     */
    private boolean m_hasBackingNode;

    /**
     * <code>m_omHeuristicEquivalence</code>
     */
    private double m_omHeuristicEquivalence = -1.0d;
    
    /**
     * <code>m_noOfSimilarComponents</code>
     */
    private int m_noOfSimilarComponents = -1;
    
    /** the task Id of the result node */
    private String m_taskId;
    
    /** logging string*/
    private String m_commandLog;
    
    /**
     * Constructor
     * 
     * @param hasBackingNode <code>true</code> if the backing node for the 
     *                       result can be found. Otherwise, <code>false</code>.
     * @param node The Test Execution node (i.e. Test Suite, Test Case, 
     *             Test Step, etc.) associated with this result. If this value 
     *             is <code>null</code>, the <code>fallbackName</code>
     *             will be used for display purposes, and there will be no 
     *             reference to a Test Execution node.
     * @param parent The parent Test Result node. May be <code>null</code>, in 
     *               which case this node is the root of a Test Result tree.
     */
    public TestResultNode(boolean hasBackingNode, INodePO node, 
            TestResultNode parent) {
        
        m_node = node;
        m_parent = parent;
        if (m_parent != null) {
            m_parent.addChild(this);
        }
        m_status = NOT_YET_TESTED;
        m_event = null;
        m_hasBackingNode = hasBackingNode;
        m_isJunitTestSuite = node.isJUnitTestSuite();
    }
    
    
    /**
     * Constructor
     * 
     * @param node The Test Execution node (i.e. Test Suite, Test Case, 
     *             Test Step, etc.) associated with this result.
     * @param parent The parent Test Result node. May be <code>null</code>, in 
     *               which case this node is the root of a Test Result tree.
     */
    public TestResultNode(INodePO node, TestResultNode parent) {
        this(node, parent, -1);
    }
    
    /**
     * @param node
     *            associated node in testexecution tree
     * @param parent
     *            parent resultNode (in case of testsuite null)
     * @param pos
     *            inserts this into the parents child list; if a negative
     *            position is given its added to the child list
     */
    public TestResultNode(INodePO node, TestResultNode parent, int pos) {
        m_node = node;
        m_parent = parent;
        if (m_parent != null) {
            if (pos > -1) {
                m_parent.addChildAtPosition(pos, this);
            } else {
                m_parent.addChild(this);
            }
        }
        m_status = NOT_YET_TESTED;
        m_event = null;
        m_hasBackingNode = true;
        m_taskId = NodePropertyTester.getTaskIdforNode(node);
        m_isJunitTestSuite = node.isJUnitTestSuite();
    }

    /**
     * add a child to resultNode and set actual resultNode as parent
     * @param resultNode resultNode to add
     */
    public void addChild(TestResultNode resultNode) {
        m_resultNodeList.add(resultNode);
        resultNode.m_parent = this;
    }

    /**
     * add a child to resultNode and set actual resultNode as parent
     * @param resultNode resultNode to add
     * @param pos
     *      position where to add
     */
    public void addChildAtPosition(int pos, TestResultNode resultNode) {
        m_resultNodeList.add(pos, resultNode);
        resultNode.m_parent = this;
    }

    /**
     * @return Returns the node.
     */
    public INodePO getNode() {
        return m_node;
    }
    /**
     * @return Returns the parent.
     */
    public TestResultNode getParent() {
        return m_parent;
    }
    /**
     * @return Returns the resultNodeList.
     */
    public List < TestResultNode > getResultNodeList() {
        return m_resultNodeList;
    }
    
    /**
     * @return Return the name of the execTestCase 
     */
    public String getName() {
        if (m_node != null) {
            return m_node.getName();
        }
        
        return StringConstants.LEFT_INEQUALITY_SING 
            + Messages.TestResultNodeGUINoNode 
            + StringConstants.RIGHT_INEQUALITY_SING;
    }
    
    
    /**
     * Adds the listener
     * @param listener ITestResultEventListener
     */
    public void addTestResultChangedListener(
        ITestResultEventListener listener) {
        if (!m_listener.contains(listener)) {
            m_listener.add(listener);
        }
    }

    /**
     * Remove the listener from ResultCap
     * @param listener to be removed
     */
    public void removeTestResultChangedListener(
        ITestResultEventListener listener) {
        m_listener.remove(listener);
    }
    
    /**
     * Trigger the listeners
     * @param res the changed TestResultNode
     */
    private void fireTestResultChanged(TestResultNode res) {
        Iterator<ITestResultEventListener> iter = m_listener.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            ITestResultEventListener item = (ITestResultEventListener) obj;
            item.testResultChanged(res);
        }
    }
    
    /**
     * Updates the parent;
     * 
     * @param pos
     *      index
     * @param node
     *      TestResultNode
     */
    public void updateResultNode(int pos, TestResultNode node) {
        for (ITestResultEventListener item : m_listener) {
            item.testResultNodeUpdated(this, pos, node);
        }
    }
    /**
     * @return Returns the status.
     */
    public int getStatus() {
        return m_status;
    }
    /**
     * @return Returns the event.
     */
    public TestErrorEvent getEvent() {
        return m_event;
    }

    /**
     * Set the results from an execution
     * 
     * @param status
     *            Specifies the kind of result
     * @param event
     *            On a CAP this may be the event from the server.
     *            <code>null</code> is allowed.
     */
    public void setResult(int status, TestErrorEvent event) {
        boolean changed = m_status != status
            || (m_event != null && !m_event.equals(event))
            || (m_event == null && event != null);
        if (changed) { 
            m_status = status;
            m_event = event;
            if (m_status == TESTING && m_timestamp == null) {
                m_timestamp = new Date();
            }
        }
        
        if (isError(status) && status != CONDITION_FAILED
                && status != INFINITE_LOOP) {
            if (getParent() != null) { // NOPMD by al on 3/19/07 1:37 PM
                getParent().setResult(ERROR_IN_CHILD, null);
            }
        }               
        if (changed) {
            fireTestResultChanged(this);
        }
    }
    
    /**
     * @param status to be checked
     * @return true if the status means an error condition
     */
    private boolean isError(int status) {        
        return status == ERROR || status == ERROR_IN_CHILD
            || status == NO_VERIFY || status == NOT_TESTED || status == ABORT
            || status == CONDITION_FAILED || status == INFINITE_LOOP;
    }
    /**
     * {@inheritDoc}
     */
    public String toString() {        
        return new ToStringBuilder(this).append(getName()).append(getStatus())
            .toString();
    }

    /**
     * @return Returns the childIndex.
     */
    public int getNextChildIndex() {
        m_childIndex++;
        return m_childIndex;
    }


    /**
     *
     * @return  String to a status
     */
    public String getStatusString() {
        return getStatusString(m_status);
    }
    
    /**
    * @param status the status
    * @return  String to a status
    */
    public static String getStatusString(int status) {
        switch (status) {
            case ERROR : 
                return Messages.TestResultNodeStepfailed;
            case ERROR_IN_CHILD :
                return Messages.TestResultNodeErrorInChildren;
            case NOT_YET_TESTED :
                return Messages.TestResultNodeNotYetTested;
            case SUCCESS :
                return Messages.TestResultNodeSuccessfullyTested;
            case TESTING :
                return Messages.TestResultNodeTesting;
            case RETRYING :
                return Messages.TestResultNodeRetrying;
            case SUCCESS_RETRY :
                return Messages.TestResultNodeSuccessRetry;
            case ABORT : 
                return Messages.TestResultNodeAbort;
            case CONDITION_FAILED:
                return Messages.TestResultNodeConditionFailed;
            case INFINITE_LOOP:
                return Messages.TestResultNodeInfiniteLoop;
            case SKIPPED : 
                return Messages.TestResultNodeSkipped;
            case SUCCESS_ONLY_SKIPPED :
                return Messages.TestResultNodeSuccessOnlySkipped;
            default : 
                break;
        } 
        return Messages.TestResultNodeUnknown;
    }

    /**
     * 
     * @return the receiver's Parameters.
     */
    public List<TestResultParameter> getParameters() {
        return Collections.unmodifiableList(m_parameters);
    }

    /**
     * 
     * @param parameter The Parameter to add.
     */
    public void addParameter(TestResultParameter parameter) {
        m_parameters.add(parameter);
    }
    
    /**
     * 
     * @param componentName The Component Name name to set. This must be a name,
     *                      <b>not</b> a GUID.
     */
    public void setComponentName(String componentName) {
        m_componentName = componentName;
    }
    
    /**
     * 
     * @return the Component Name name for this result node. 
     *         This is a name, <b>not</b> a GUID.
     */
    public String getComponentName() {
        return m_componentName;
    }
    
    /**
     * 
     * @param componentType The Component Name type to set. 
     *                      This is a human-readable component type.
     */
    public void setComponentType(String componentType) {
        m_componentType = componentType;
    }
    
    /**
     * 
     * @return the Component Name type for this result node. 
     *         This is a human-readable component type.
     */
    public String getComponentType() {
        return m_componentType;
    }

    /**
     * 
     * @param actionName The name of the executed Action that caused this 
     *                   Test Result. May be <code>null</code>, which means that
     *                   the Test Result was not caused by any Action 
     *                   (e.g. Test Case Reference, as opposed to Test Step).
     */
    public void setActionName(String actionName) {
        m_actionName = actionName;
    }

    /**
     * 
     * @return  The name of the executed Action that caused this Test Result, 
     *          or <code>null</code> if the Test Result was not caused by any 
     *          Action (e.g. Test Case Reference, as opposed to Test Step).
     */
    public String getActionName() {
        return m_actionName;
    }
    
    /**
     * 
     * @return the time at which the keyword execution corresponding to the 
     *         receiver began, or <code>null</code> if the receiver does not 
     *         correspond to an executed keyword.
     */
    public Date getTimeStamp() {
        return m_timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        m_timestamp = timestamp;
    }

    /**
     * @param screenshot the screenshot to set
     */
    public void setScreenshot(byte[] screenshot) {
        m_screenshot = screenshot;
    }

    /**
     * @return the screenshot
     */
    public byte[] getScreenshot() {
        return m_screenshot;
    }

    /**
     * 
     * @return <code>true</code> if the backing node for the 
     *         result can be found. Otherwise, <code>false</code>.
     */
    public boolean hasBackingNode() {
        return m_hasBackingNode;
    }

    /**
     * @param omHeuristicEquivalence the omHeuristicEquivalence to set
     */
    public void setOmHeuristicEquivalence(double omHeuristicEquivalence) {
        m_omHeuristicEquivalence = omHeuristicEquivalence;
    }

    /**
     * @return the omHeuristicEquivalence
     */
    public double getOmHeuristicEquivalence() {
        return m_omHeuristicEquivalence;
    }

    /**
     * @param noOfSimilarComponents the noOfSimilarComponents to set
     */
    public void setNoOfSimilarComponents(int noOfSimilarComponents) {
        m_noOfSimilarComponents = noOfSimilarComponents;
    }

    /**
     * @return the noOfSimilarComponents
     */
    public int getNoOfSimilarComponents() {
        return m_noOfSimilarComponents;
    }

    /**
     * 
     * @return the sibling immediately following the receiver in the 
     *         receiver's parent's child list, or <code>null</code> if the 
     *         receiver is the last element in this list.
     */
    private TestResultNode getNextSibling() {
        TestResultNode parent = getParent();
        if (parent != null) {
            List<TestResultNode> siblingList = parent.getResultNodeList();
            int nodeIndex = siblingList.indexOf(this);
            if (nodeIndex == -1 && !ClientTest.instance().isTrimming()) {
                LOG.error(NLS.bind(
                        Messages.ParentChildInconsistency, getName()));
            } else {
                int nextSiblingIndex = nodeIndex + 1;
                if (siblingList.size() > nextSiblingIndex) {
                    return siblingList.get(nextSiblingIndex);
                }
            }
            
        }
        
        return null;
    }

    /**
     * 
     * @return the {@link TestResultNode} corresponding to the Keyword that was 
     *         executed after the receiver's Keyword, or <code>null</code> if 
     *         the receiver's Keyword was the last executed element in a 
     *         Test Suite.
     */
    private TestResultNode getNextExecutedNode() {
        TestResultNode nextSibling = getNextSibling();
        TestResultNode currentNode = this;
        // having a timestamp indicates that the keyword was executed
        while ((nextSibling == null || nextSibling.getTimeStamp() == null) 
                && currentNode.getParent() != null) {
            currentNode = currentNode.getParent();
            nextSibling = currentNode.getNextSibling();
        }
        
        if (nextSibling != null && nextSibling.getTimeStamp() == null) {
            // corner case: Last executed Keyword, but not the last Keyword
            //              in the Test Suite (e.g. test aborted).
            return null;
        }
        
        return nextSibling;
    }
    
    /**
     * 
     * @param testEndTime The time at which the Test Suite execution containing 
     *                    the receiver was ended. May be <code>null</code> if 
     *                    the Test Suite end time is not available.
     * @return the duration of the receiver's execution (in milliseconds), 
     *         or <code>-1</code> if the duration cannot be calculated 
     *         (e.g. the Keyword was not executed, or the keyword was the last
     *         executed and no Test Suite end time is available).
     */
    public long getDuration(Date testEndTime) {
        Date start = getTimeStamp();
        if (start != null) {
            TestResultNode nextExecutedNode = getNextExecutedNode();
            if (nextExecutedNode != null) {
                Date end = nextExecutedNode.getTimeStamp();
                return end.getTime() - start.getTime();
            } 

            // Receiver was the last executed node in the Test Suite Execution,
            // so use Test Suite end time (if available) to determine duration.
            if (testEndTime != null) {
                return testEndTime.getTime() - start.getTime();
            }
        } 
        
        // keyword was not executed, so duration cannot be calculated
        return -1;
    }
    
    /**
     * @return a human readable type description for the given node
     */
    public String getTypeOfNode() {
        INodePO node = getNode();
        if (node instanceof IEventExecTestCasePO) {
            return Messages.TestResultNodeTypeEventTestCase;
        } else if (node instanceof ITestCasePO) {
            return Messages.TestResultNodeTypeTestCase;
        } else if (node instanceof ICapPO) {
            return Messages.TestResultNodeTypeTestStep;
        } else if (node instanceof ITestSuitePO) {
            return Messages.TestResultNodeTypeTestSuite;
        } else if (node instanceof ICommentPO) {
            return Messages.TestResultNodeTypeComment;
        } else if (node instanceof IConditionalStatementPO) {
            return Messages.TestResultNodeTypeCondition;
        } else if (node instanceof IDoWhilePO) {
            return Messages.TestResultNodeTypeDoWhile;
        } else if (node instanceof IWhileDoPO) {
            return Messages.TestResultNodeTypeWhileDo;
        } else if (node instanceof IAbstractContainerPO) {
            return Messages.TestResultNodeTypeContainer;
        } else if (node instanceof IIteratePO) {
            return Messages.TestResultNodeTypeIterate;
        }
        return Messages.TestResultNodeTypeUnknown;
    }
    
    /**
     * @return a human readable parameter description for the given node
     */
    public String getParameterDescription() {
        StringBuilder paramValueBuilder = new StringBuilder();
        List<TestResultParameter> parameters = getParameters();
        // use index based loop to avoid ConcurrentModificationException
        boolean isCondStruct = getNode() instanceof ICondStructPO;
        for (int index = 0; index < parameters.size(); index++) {
            TestResultParameter parameter = parameters.get(index);
            if (!isCondStruct) {
                paramValueBuilder
                        .append(StringUtils.defaultString(parameter.getValue()))
                        .append(SEPARATOR);
            } else {
                if (Boolean.parseBoolean(parameter.getValue())) {
                    paramValueBuilder.append(NEGATED).append(SEPARATOR);
                }
            }
        }
        if (paramValueBuilder.length() > 0) {
            int builderLength = paramValueBuilder.length();
            paramValueBuilder.delete(builderLength - SEPARATOR_LEN,
                    builderLength);
            paramValueBuilder.insert(0, " ["); //$NON-NLS-1$
            paramValueBuilder.append("]"); //$NON-NLS-1$
            return paramValueBuilder.toString();
        }
        return StringConstants.EMPTY;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return m_taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(String taskId) {
        m_taskId = taskId;
    }

    /**
     * @return the log from the command or null
     */
    public String getCommandLog() {
        return m_commandLog;
    }

    /**
     * 
     * @param commandLog sets the log from a command
     */
    public void setCommandLog(String commandLog) {
        this.m_commandLog = commandLog;
    }

    /** Removes all children of the node */
    public void removeChildren() {
        m_resultNodeList.clear();
        m_childIndex = -1;
    }

    /**
     * @return whether the Node acts as a JUnit testsuite
     */
    public boolean isJunitTestSuite() {
        return m_isJunitTestSuite;
    }


    /**
     * @param isJunitTestSuite whether the Node acts as a JUnit testsuite
     * true = the Node is used as a JUnitTestSuite
     */
    public void setJunitTestSuite(boolean isJunitTestSuite) {
        m_isJunitTestSuite = isJunitTestSuite;
    }
}
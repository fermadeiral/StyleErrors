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
package org.eclipse.jubula.client.alm.mylyn.core.model;

import java.util.Date;

import org.eclipse.jubula.client.alm.mylyn.core.i18n.Messages;
import org.eclipse.jubula.client.core.constants.Constants;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * Class which represents a change, e.g. a comment or a field update,
 * which has to be applied to an ALM system.
 * @author BREDEX GmbH
 */
public class ALMChange {

    /** the max length for data */
    protected static final int MAX_DATA_STRING_LENGTH = 200;
    /** the timestamp */
    private String m_timestamp;
    /** the node type */
    private String m_nodeType;
    /** the node name and parameter description */
    private String m_nodeNameAndParams;
    /** the dashboard base URL */
    private String m_dashboardURL;
    /** the summary ID */
    private String m_summaryId;
    /** the node count */
    private Long m_nodeNumber;
    /** the result node*/
    private TestResultNode m_node;
    /** the summary */
    private ITestResultSummaryPO m_summary;

    /**
     * Constructor
     * @param resultNode the node
     * @param dashboardURL the URL
     * @param summary the id
     * @param nodeCount the node count
     */
    public ALMChange(TestResultNode resultNode, String dashboardURL,
            ITestResultSummaryPO summary, Long nodeCount) {
        setDashboardURL(dashboardURL);
        setNode(resultNode);
        setSummary(summary);
        setSummaryId(summary.getId().toString());
        setNodeNumber(nodeCount);
        
        Date executionTime = resultNode.getTimeStamp();
        // e.g. when not executed
        if (executionTime != null) {
            setTimestamp(executionTime.toString());
        } else {
            setTimestamp(Messages.NotAvailable);
        }

        setNodeType(resultNode.getTypeOfNode());
        
    }

    /**
     * @return the dashboard URL for this comment entry
     */
    public String getDashboardURL() {
        return m_dashboardURL + StringConstants.QUESTION_MARK
                + Constants.DASHBOARD_SUMMARY_PARAM
                + StringConstants.EQUALS_SIGN + getSummaryId()
                + StringConstants.AMPERSAND
                + Constants.DASHBOARD_RESULT_NODE_PARAM
                + StringConstants.EQUALS_SIGN + String.valueOf(getNodeNumber());
    }

    /**
     * @return the timestamp
     */
    public String getTimestamp() {
        return m_timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        m_timestamp = timestamp;
    }

    /**
     * @return the nodeType
     */
    public String getNodeType() {
        return m_nodeType;
    }
    
    /**
     * @return the node
     */
    public TestResultNode getNode() {
        return m_node;
    }
    
    /**
     * @param node the result node
     */
    public void setNode(TestResultNode node) {
        m_node = node;
    }

    /**
     * @param nodeType the nodeType to set
     */
    public void setNodeType(String nodeType) {
        m_nodeType = nodeType;
    }

    /**
     * @return the nodeNameAndParams
     */
    public String getNodeNameAndParams() {
        return m_nodeNameAndParams;
    }

    /**
     * @param nodeNameAndParams the nodeNameAndParams to set
     */
    public void setNodeNameAndParams(String nodeNameAndParams) {
        m_nodeNameAndParams = nodeNameAndParams;
    }

    /**
     * @param dashboardURL the dashboardURL to set
     */
    public void setDashboardURL(String dashboardURL) {
        m_dashboardURL = dashboardURL;
    }

    /**
     * @return the summaryId
     */
    public String getSummaryId() {
        return m_summaryId;
    }

    /**
     * @param summaryId the summaryId to set
     */
    public void setSummaryId(String summaryId) {
        m_summaryId = summaryId;
    }
    
    /**
     * Sets the summary for this fieldUpdate
     * @param summary summary for this fieldUpdate
     */
    private void setSummary(ITestResultSummaryPO summary) {
        m_summary = summary;
    }
    
    /**
     * @return summary for this fieldUpdate
     */
    public ITestResultSummaryPO getSummary() {
        return m_summary;
    }

    /**
     * @return the nodeNumber
     */
    public Long getNodeNumber() {
        return m_nodeNumber;
    }

    /**
     * @param nodeNumber the nodeNumber to set
     */
    public void setNodeNumber(Long nodeNumber) {
        m_nodeNumber = nodeNumber;
    }

}
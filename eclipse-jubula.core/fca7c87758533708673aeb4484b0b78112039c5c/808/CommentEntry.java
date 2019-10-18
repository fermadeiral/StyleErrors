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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.alm.mylyn.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.osgi.util.NLS;

/**
 * @author BREDEX GmbH
 */
public class CommentEntry extends ALMChange {
    
    /** the status */
    private String m_status;
    
    /**
     * Constructor
     * @param resultNode the node
     * @param dashboardURL the URL
     * @param summary the summary
     * @param nodeCount the node count
     */
    public CommentEntry(TestResultNode resultNode, String dashboardURL,
            ITestResultSummaryPO summary, Long nodeCount) {
        super(resultNode, dashboardURL, summary, nodeCount);
        
        setNodeNameAndParams(getName(resultNode));
        
        if (hasPassed(resultNode.getStatus())) {
            m_status = Messages.StatusPassed;
        } else {
            m_status = Messages.StatusFailed;
        }
        String paramDescription = StringUtils.abbreviate(
                resultNode.getParameterDescription(), MAX_DATA_STRING_LENGTH);
        if (!StringUtils.isBlank(paramDescription)) {
            setNodeNameAndParams(getNodeNameAndParams()
                    + StringConstants.SPACE + paramDescription);
        }
    }
    
    @Override
    public String toString() {
        return NLS.bind(Messages.NodeComment, new String[] { getTimestamp(),
            getNodeType(), getNodeNameAndParams(), m_status });
    }
    
    /** {@inheritDoc} */
    protected String getName(TestResultNode resultNode) {
        StringBuilder nameBuilder = new StringBuilder();
        INodePO node = resultNode.getNode();
        if (node != null) {
            if (node instanceof IExecTestCasePO) {
                IExecTestCasePO testCaseRef = (IExecTestCasePO) node;
                String realName = testCaseRef.getRealName();
                ISpecTestCasePO testCase = testCaseRef.getSpecTestCase();
                String testCaseName = testCase != null ? testCase.getName()
                        : StringUtils.EMPTY;
                if (!StringUtils.isBlank(realName)) {
                    nameBuilder.append(realName);
                    nameBuilder.append(StringConstants.SPACE)
                            .append(StringConstants.LEFT_PARENTHESIS)
                            .append(testCaseName)
                            .append(StringConstants.RIGHT_PARENTHESIS);

                } else {
                    nameBuilder.append(testCaseName);
                }
            } else {
                nameBuilder.append(node.getName());
            }
        }
        return nameBuilder.toString();
    }
    
    /**
     * @param statusCode
     *            the m_status code
     * @return failure m_status
     */
    public static boolean hasPassed(int statusCode) {
        return statusCode == TestResultNode.SUCCESS
                || statusCode == TestResultNode.SUCCESS_RETRY;
    }
}
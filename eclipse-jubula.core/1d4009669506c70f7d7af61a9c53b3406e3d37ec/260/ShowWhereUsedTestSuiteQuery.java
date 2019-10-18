/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.search.query;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * @author BREDEX GmbH
 * @created 18.10.2013
 */
public class ShowWhereUsedTestSuiteQuery
    extends AbstractShowWhereUsedQuery {
    /**
     * <code>m_specTC</code>
     */
    private ITestSuitePO m_testSuite;

    /**
     * @param testSuite
     *            the test suite to search the reuse for
     */
    public ShowWhereUsedTestSuiteQuery(ITestSuitePO testSuite) {
        super(null);
        m_testSuite = testSuite;
    }

    /**
     * {@inheritDoc}
     */
    public IStatus run(IProgressMonitor monitor) {
        calculateReuseOfTestSuite(m_testSuite, monitor);
        return Status.OK_STATUS;
    }

    /**
     * calculates and show the places of reuse for a test suite
     *
     * @param testSuite
     *            the test suite
     * @param monitor
     *            the progress monitor
     */
    private void calculateReuseOfTestSuite(ITestSuitePO testSuite,
            IProgressMonitor monitor) {
        IProjectPO currentProject = GeneralStorage.getInstance().getProject();
        List<IRefTestSuitePO> reuseList = NodePM.getInternalRefTestSuites(
                testSuite.getGuid(), currentProject.getId());
        INodePO[] reuse = new INodePO[reuseList.size()];
        reuseList.toArray(reuse);
        monitor.beginTask("Searching for reusage of Test Suite", reuseList.size()); //$NON-NLS-1$
        final List<SearchResultElement> reuseLoc = getResultElementsFromNodes(
                monitor, reuse);

        setSearchResult(reuseLoc);
        monitor.done();
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTimestamp());
        sb.append(StringConstants.COLON);
        sb.append(StringConstants.SPACE);
        sb.append(Messages.UIJobSearchingTestSuites);
        sb.append(" \""); //$NON-NLS-1$
        sb.append(m_testSuite.getName());
        sb.append("\""); //$NON-NLS-1$
        return sb.toString();
    }

}

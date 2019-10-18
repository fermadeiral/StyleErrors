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
package org.eclipse.jubula.client.ui.rcp.search.query;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;


/**
 * @author BREDEX GmbH
 * @created Jul 27, 2010
 */
public class ShowWhereUsedSpecTcQuery 
    extends AbstractShowWhereUsedQuery {
    /**
     * <code>m_specTC</code>
     */
    private ISpecTestCasePO m_specTC;

    /**
     * @param specTC
     *            the spec tc to search the reuse for
     */
    public ShowWhereUsedSpecTcQuery(ISpecTestCasePO specTC) {
        super(null);
        m_specTC = specTC;
    }

    /**
     * {@inheritDoc}
     */
    public IStatus run(IProgressMonitor monitor) {
        calculateReuseOfSpecTestCase(m_specTC, monitor);
        return Status.OK_STATUS;
    }
    
    /**
     * calculates and show the places of reuse for a spectestcase
     * 
     * @param specTC
     *            ISpecTestCasePO
     * @param monitor
     *            the progress monitor
     */
    private void calculateReuseOfSpecTestCase(ISpecTestCasePO specTC,
            IProgressMonitor monitor) {
        IProjectPO currentProject = GeneralStorage.getInstance().getProject();
        List<Long> parentProjectIDs = new LinkedList<Long>();
        parentProjectIDs.add(currentProject.getId());
        for (IReusedProjectPO rp : currentProject.getUsedProjects()) {
            try {
                Long projID = ProjectPM.findProjectId(rp.getProjectGuid(), rp
                        .getMajorNumber(), rp.getMinorNumber(),
                        rp.getMicroNumber(), rp.getVersionQualifier());
                if (projID != null) {
                    parentProjectIDs.add(projID);
                }
            } catch (JBException e) {
                // ignore
            }
        }
        List<IExecTestCasePO> reuseList =
                NodePM.getExecTestCases(specTC.getGuid(), parentProjectIDs);
        INodePO[] reuse = new INodePO[reuseList.size()];
        reuseList.toArray(reuse);
        monitor.beginTask("Searching for reusage of Test Case", reuseList.size()); //$NON-NLS-1$
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
        sb.append(Messages.UIJobSearchingTestCases);
        sb.append(" \""); //$NON-NLS-1$
        sb.append(m_specTC.getName());
        sb.append("\""); //$NON-NLS-1$
        return sb.toString();
    }
    
}

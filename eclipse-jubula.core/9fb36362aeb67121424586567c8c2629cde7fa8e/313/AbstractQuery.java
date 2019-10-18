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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.NodeSearchResultElementAction;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.TestDataCubeSearchResultElementAction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;


/**
 * @author BREDEX GmbH
 * @created Jul 27, 2010
 */
public abstract class AbstractQuery implements ISearchQuery {

    /** The view ID to open before jumping to search result; may be null.*/
    private String m_viewId;

    /**
     * <code>m_timestamp</code>
     */
    private String m_timestamp;

    /** The progress monitor while searching. */
    private IProgressMonitor m_monitor;

    /** The set of result nodes. */
    private Set<INodePO> m_resultNodeSet = new HashSet<INodePO>();

    /** The list of result test data cubes. */
    private List<SearchResultElement<?>> m_resultTestDataCubes =
            new ArrayList<SearchResultElement<?>>();

    /**
     * <code>m_searchResult</code>
     */
    private BasicSearchResult<SearchResultElement<?>> m_searchResult =
            new BasicSearchResult<>(this);

    /**
     * Default constructor sets the time stamp.
     * @see #getTimestamp()
     */
    public AbstractQuery() {
        m_timestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss") //$NON-NLS-1$
                .format(new Date());
    }

    /**
     * Default constructor sets the time stamp.
     * @param viewId The default view ID to open.
     * @see #getTimestamp()
     */
    public AbstractQuery(String viewId) {
        m_viewId = viewId;
        m_timestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss") //$NON-NLS-1$
                .format(new Date());
    }

    /**
     * @return The time stamp created when the constructor is called.
     */
    public String getTimestamp() {
        return m_timestamp;
    }
    
    
    /**
     * @return Get the result set node.
     */
    public Set<INodePO> getResultNodeSet() {
        return m_resultNodeSet;
    }

    /**
     * @param monitor The process monitor.
     */
    protected void setMonitor(IProgressMonitor monitor) {
        monitor.beginTask(Messages.SimpleSearchBeginTask,
                IProgressMonitor.UNKNOWN);
        m_monitor = monitor;
        m_resultNodeSet.clear();
        m_resultTestDataCubes.clear();
    }

    /**
     * @return The process monitor.
     */
    protected IProgressMonitor getMonitor() {
        return m_monitor;
    }


    /**
     * @param node The node adding to the result list.
     * @see #add(IParameterInterfacePO)
     * @see #finished()
     */
    protected void add(INodePO node) {
        m_resultNodeSet.add(node);
    }

    /**
     * @param testDataCube The test data cube adding to the result list.
     * @see #add(INodePO)
     * @see #finished()
     */
    protected void add(IParameterInterfacePO testDataCube) {
        m_resultTestDataCubes.add(new SearchResultElement<Long>(testDataCube
                .getName(), testDataCube.getId(),
                GeneralLabelProvider.getImageImpl(testDataCube),
                new TestDataCubeSearchResultElementAction(),
                null,
                Constants.JB_DATASET_VIEW_ID));
    }

    /**
     * @param nodes The nodes to add to the result list.
     */
    protected void addAll(Set<INodePO> nodes) {
        for (INodePO node: nodes) {
            add(node);
        }
    }

    /**
     * @param reuseLoc the list of reuse locations
     */
    protected void setSearchResult(List<SearchResultElement<?>> reuseLoc) {
        m_searchResult.setResultList(reuseLoc);
    }

    /**
     * Sets the search result list with the previously added nodes followed by
     * the added test data cubes.
     * @see #add(INodePO)
     * @see #add(IParameterInterfacePO)
     */
    protected void finished() {
        List<SearchResultElement<?>> result = getSearchResultListFromNodes(
                m_resultNodeSet);
        result.addAll(m_resultTestDataCubes);
        setSearchResult(result);
        getMonitor().done();
    }

    /**
     * @param reuse
     *            the reusing node po's
     * @return a list of SearchResultElements for the given NodePOs
     */
    protected List<SearchResultElement<?>> getSearchResultListFromNodes(
            Set<INodePO> reuse) {
        final List<SearchResultElement<?>> searchResult =
            new ArrayList<SearchResultElement<?>>(
                reuse.size());
        for (INodePO node : reuse) {
            INodePO parent = node.getSpecAncestor();
            if (parent == null || parent == node) {
                parent = node.getParentNode();
            }
            String resultName;
            String nodeName = GeneralLabelProvider.getTextImpl(node);
            if (validParent(parent)) {
                resultName = NLS.bind(Messages.SearchResultPageElementLabel,
                        new Object[] { parent.getName(), nodeName });
            } else {
                resultName = nodeName;
            }
            searchResult.add(new SearchResultElement<Long>(resultName, node
                    .getId(), GeneralLabelProvider.getImageImpl(node),
                    new NodeSearchResultElementAction(), node.getComment(),
                    m_viewId));
        }
        return searchResult;
    }

    /**
     * @param parent the parent to check
     * @return true if valid parent to display
     */
    private static boolean validParent(INodePO parent) {
        return parent != null && !(parent instanceof IProjectPO);
    }

    /**
     * @return True, if the result list of nodes and test data cubes are empty.
     */
    public boolean isEmpty() {
        return m_resultNodeSet.isEmpty() && m_resultTestDataCubes.isEmpty();
    }

    /** {@inheritDoc} */
    public boolean canRerun() {
        return true;
    }

    /** {@inheritDoc} */
    public boolean canRunInBackground() {
        return true;
    }
    /**

    /** {@inheritDoc} */
    public ISearchResult getSearchResult() {
        return m_searchResult;
    }

}

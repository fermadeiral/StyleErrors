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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.search.data.SearchOptions;
import org.eclipse.jubula.client.ui.rcp.search.data.TestSuiteBrowserWrapper;
import org.eclipse.jubula.client.ui.rcp.search.data.TreeViewSelectionRetriever;
import org.eclipse.jubula.client.ui.rcp.search.data.TypeName;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;


/**
 * @author BREDEX GmbH
 * @created April 29, 2013
 */
public abstract class AbstractTraverserQuery
        extends AbstractStringQuery
        implements ITreeNodeOperation<INodePO> {

    /**
     * @param searchData The search options.
     * @param viewId The view Id to open.
     */
    protected AbstractTraverserQuery(SearchOptions searchData,
            String viewId) {
        super(searchData, viewId);
    }

    /**
     * Traverse the whole project or all selected nodes including all children
     * using the {@link TreeTraverser}. The method {@link #operate(INodePO)}
     * is called on every visited node.
     */
    protected void traverse() {
        boolean searchInCurrentSelection = getSearchOptions()
                .hasNodesToBeSelected();
        if (searchInCurrentSelection) {
            // search in Test Suite Browser
            if (getSearchOptions().isSearchingInTestSuiteBrowser()) {
                traverseStructuredSelection(
                        TreeViewSelectionRetriever.getStructuredSelection(
                                TestSuiteBrowserWrapper.getInstance()));
            }
            // search in Test Case Browser
            if (getSearchOptions().isSearchingInTestCaseBrowser()) {
                traverseTestCaseBrowsers();
            }
        } else {
            IProjectPO activeProject = GeneralStorage.getInstance()
                    .getProject();
            TreeTraverser tt = new TreeTraverser(
                    activeProject, this, true, true);
            tt.traverse(true);
        }
    }

    /**
     * Traverse all selected nodes in all Test Case Browsers.
     */
    private void traverseTestCaseBrowsers() {
        List<TestCaseBrowser> tcbs = new ArrayList<TestCaseBrowser>();
        MultipleTCBTracker tracker = MultipleTCBTracker.getInstance();
        if (getSearchOptions().isSearchinInTestCaseBrowsersAll()) {
            tcbs = tracker.getOpenTCBs();
        } else {
            TestCaseBrowser masterTCB = tracker.getMainTCB();
            if (masterTCB != null) {
                tcbs.add(masterTCB);
            }
        }
        traverseTestCaseBrowserList(tcbs);
    }

    /**
     * Traverse all selected nodes in the given list of Test Case Browsers.
     * @param tcbs The list of Test Case Browsers.
     */
    private void traverseTestCaseBrowserList(List<TestCaseBrowser> tcbs) {
        for (TestCaseBrowser tcb: tcbs) {
            traverseStructuredSelection(
                    TreeViewSelectionRetriever.getStructuredSelection(tcb));
        }
    }

    /**
     * Search in structured selection by using {@link TreeTraverser}
     * to search in selected nodes including there children.
     * @param structuredSelection The structured selection.
     */
    private void traverseStructuredSelection(IStructuredSelection
            structuredSelection) {
        if (structuredSelection != null) {
            @SuppressWarnings("unchecked")
            List<INodePO> selectionList =
                    structuredSelection.toList();
            for (INodePO node: selectionList) {
                traverseNodePO(node);
            }
        }
    }

    /**
     * @param node
     *            The node starting the {@link TreeTraverser}.
     */
    private void traverseNodePO(INodePO node) {
        TreeTraverser tt = new TreeTraverser(node, this);
        tt.traverse(true);
    }

    /**
     * @param node The node for type check.
     * @return True, if the given node has a selected type, otherwise false.
     */
    protected boolean matchingSearchType(INodePO node) {
        for (TypeName type: getSearchOptions().getSelectedSearchableTypes()) {
            if (type.getType().equals(node.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean operate(ITreeTraverserContext<INodePO> ctx, INodePO parent,
            INodePO node, boolean alreadyVisited) {
        if (alreadyVisited) {
            return false; // do not search in children, if node has been visited
        }
        if (getSearchOptions().isSearchingInReusedProjects()
                // parent == null, if node is root of search
                || parent == null
                // parent and node have to be in the same project
                || node.getParentProjectId()
                        .equals(getSearchOptions().getProject().getId())
            ) {
            IProgressMonitor monitor = getMonitor();
            monitor.worked(1);
            return operate(node) && !monitor.isCanceled(); // search in children
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void postOperate(ITreeTraverserContext<INodePO> ctx, INodePO parent,
            INodePO node, boolean alreadyVisited) {
        // do nothing
    }

    /**
     * Notification method on traversing the current node.
     * @param node The current node operating on.
     * @return True, if children has to be visited.
     */
    protected abstract boolean operate(INodePO node);

}

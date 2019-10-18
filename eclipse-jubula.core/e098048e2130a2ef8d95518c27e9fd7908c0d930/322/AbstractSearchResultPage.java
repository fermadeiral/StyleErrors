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
package org.eclipse.jubula.client.ui.rcp.search;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.part.Page;


/**
 * @author BREDEX GmbH
 * @created Jul 26, 2010
 */
public abstract class AbstractSearchResultPage extends Page implements
        ISearchResultPage {
    /**
     * <code>m_treeViewer</code>
     */
    private TreeViewer m_treeViewer;

    /**
     * <code>m_viewPart</code>
     */
    private ISearchResultViewPart m_viewPart;

    /**
     * <code>m_id</code>
     */
    private String m_id;

    /**
     * @param id
     *            the id to set
     */
    public void setID(String id) {
        m_id = id;
    }

    /**
     * @return the id
     */
    public String getID() {
        return m_id;
    }

    /** {@inheritDoc} */
    public void restoreState(IMemento memento) {
        // FIXME MT: There is currently no state support
    }

    /** {@inheritDoc} */
    public void saveState(IMemento memento) {
        // FIXME MT: There is currently no state support
    }

    /** {@inheritDoc} */
    public Object getUIState() {
        // FIXME MT: There is currently no state support
        return null;
    }
    
    /**
     * @param treeViewer
     *            the treeViewer to set
     */
    protected void setTreeViewer(TreeViewer treeViewer) {
        m_treeViewer = treeViewer;
    }

    /**
     * @return the treeViewer
     */
    protected TreeViewer getTreeViewer() {
        return m_treeViewer;
    }

    /** {@inheritDoc} */
    public void setFocus() {
        getTreeViewer().getControl().setFocus();
    }

    /**
     * @param viewPart
     *            the viewaPart to set
     */
    public void setViewPart(ISearchResultViewPart viewPart) {
        m_viewPart = viewPart;
    }

    /**
     * @return the viewaPart
     */
    public ISearchResultViewPart getViewPart() {
        return m_viewPart;
    }
    
    /** {@inheritDoc} */
    public void setInput(ISearchResult search, Object uiState) {
        if (search instanceof BasicSearchResult) {
            getTreeViewer().setInput(search);
        }
    }
}

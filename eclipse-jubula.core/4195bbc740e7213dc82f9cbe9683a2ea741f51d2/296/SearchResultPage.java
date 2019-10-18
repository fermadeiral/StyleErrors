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

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.filter.JBPatternFilter;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.filter.JBFilteredTree;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.contentprovider.AbstractTreeViewContentProvider;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.dialogs.FilteredTree;


/**
 * @author BREDEX GmbH
 * @created 07.12.2005
 */
public class SearchResultPage extends AbstractSearchResultPage 
    implements IProjectLoadedListener {
    /** double click listener */
    private DoubleClickListener m_doubleClickListener = 
        new DoubleClickListener();

    /**
     * <code>m_control</code>
     */
    private Control m_control;

    /** {@inheritDoc} */
    public void createControl(Composite parent) {
        Composite topLevelComposite = new Composite(parent, SWT.NONE);
        setControl(topLevelComposite);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 2;
        layout.marginWidth = LayoutUtil.MARGIN_WIDTH;
        layout.marginHeight = LayoutUtil.MARGIN_HEIGHT;
        topLevelComposite.setLayout(layout);

        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.grabExcessHorizontalSpace = true;
        topLevelComposite.setLayoutData(layoutData);

        final FilteredTree ft = new JBFilteredTree(topLevelComposite, SWT.MULTI
                | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
                new JBPatternFilter(), true);

        setTreeViewer(ft.getViewer());

        ColumnViewerToolTipSupport.enableFor(getTreeViewer());
        getTreeViewer().addDoubleClickListener(m_doubleClickListener);
        getTreeViewer().setContentProvider(
                new SearchResultContentProvider());
        getTreeViewer().setLabelProvider(
                new DecoratingLabelProvider(new LabelProvider(), Plugin
                        .getDefault().getWorkbench().getDecoratorManager()
                        .getLabelDecorator()));
        getTreeViewer().setComparator(new ViewerComparator());
        getSite().setSelectionProvider(getTreeViewer());
        
        DataEventDispatcher.getInstance().addProjectLoadedListener(this, true);
        
        Plugin.getHelpSystem().setHelp(parent,
                ContextHelpIds.JB_SEARCH_RESULT_VIEW);
        
        // Create menu manager and menu
        MenuManager menuMgr = new MenuManager();
        Menu menu = menuMgr.createContextMenu(
                getTreeViewer().getControl());
        getTreeViewer().getControl().setMenu(menu);
        // Register menu for extension.
        getSite().registerContextMenu(getID(), menuMgr, getTreeViewer());
        
    }

    /**
     * The label provider of the table.
     * 
     * @author BREDEX GmbH
     * @created 07.12.2005
     */
    private static class LabelProvider extends ColumnLabelProvider {
        /**
         * {@inheritDoc}
         */
        public String getText(Object element) {
            if (element instanceof SearchResultElement) {
                return ((SearchResultElement)element).getName();
            }
            return super.getText(element);
        }

        /**
         * {@inheritDoc}
         */
        public Image getImage(Object element) {
            if (element instanceof SearchResultElement) {
                SearchResultElement elem = (SearchResultElement)element;
                return elem.getImage();
            }
            return super.getImage(element);
        }

        /**
         * {@inheritDoc}
         */
        public String getToolTipText(Object element) {
            if (element instanceof SearchResultElement) {
                SearchResultElement sr = (SearchResultElement)element;
                String comment = sr.getComment();
                if (comment != null) {
                    return comment;
                }
            }
            return super.getToolTipText(element);
        }
    }

    /** {@inheritDoc} */
    public void setFocus() {
        getTreeViewer().getControl().setFocus();
    }

    /**
     * The content provider of the table.
     */
    private static class SearchResultContentProvider extends
            AbstractTreeViewContentProvider {
        /** {@inheritDoc} */
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof BasicSearchResult) {
                BasicSearchResult sr = (BasicSearchResult)parentElement;
                return sr.getResultList().toArray();
            }
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
    }

    /**
     * DoubleClickListener for the TableViewer
     * 
     * @author BREDEX GmbH
     * @created 07.12.2005
     */
    private static class DoubleClickListener implements IDoubleClickListener {

        /** {@inheritDoc} */
        public void doubleClick(DoubleClickEvent event) {
            if (!(event.getSelection() instanceof IStructuredSelection)) {
                return;
            }
            SearchResultElement element = 
                (SearchResultElement)((IStructuredSelection)event
                    .getSelection()).getFirstElement();
            if (element != null) {
                element.jumpToResult();
            }
        }

    }

    /** {@inheritDoc} */
    public void dispose() {
        getSite().setSelectionProvider(null);
        getTreeViewer().removeDoubleClickListener(m_doubleClickListener);
        DataEventDispatcher.getInstance().removeProjectLoadedListener(this);
        super.dispose();
    }

    /** {@inheritDoc} */
    public String getLabel() {
        int resultSize = 0;
        String queryLabel = StringConstants.EMPTY;
        Object viewerInput = getTreeViewer().getInput();
        if (viewerInput != null) {
            BasicSearchResult sr = (BasicSearchResult) viewerInput;
            resultSize = sr.getResultList().size();
            ISearchQuery query = sr.getQuery();
            if (query != null) {
                queryLabel = query.getLabel();
            }
        }
        
        return NLS.bind(Messages.SearchResultPageResultPageLabel, 
            resultSize, queryLabel); 
    }

    /**
     * @param control
     *            the control to set
     */
    private void setControl(Control control) {
        m_control = control;
    }

    /**
     * @return the control
     */
    public Control getControl() {
        return m_control;
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        if (GeneralStorage.getInstance().getProject() == null) {
            Plugin.getDisplay().syncExec(new Runnable() {
                public void run() {
                    getTreeViewer().setInput(null);
                }
            });
        }
    }

    /**
     * @param selection
     *            the selection to set
     */
    public void setSelection(ISelection selection) {
        getTreeViewer().setSelection(selection, true);
    }

}

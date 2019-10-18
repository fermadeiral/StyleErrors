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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IConditionalStatementPO;
import org.eclipse.jubula.client.core.model.IDoWhilePO;
import org.eclipse.jubula.client.core.model.IIteratePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.IWhileDoPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.DependencyFinderOp;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.filter.JBPatternFilter;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.filter.JBFilteredTree;
import org.eclipse.jubula.client.ui.rcp.provider.contentprovider.TestCaseTreeCompositeContentProvider;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.jubula.client.ui.rcp.sorter.NodeNameViewerSorter;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;


/**
 * A tree viewer wrapper to show a mildly configurable subtree of the
 *      SpecTC tree
 * Currently either SpecTestCases referencable from certain SpecTCs are shown
 *      or only categories either including reused projects or not
 * @author Markus Tiede
 * @created Jul 20, 2011
 */
public class TestCaseTreeComposite extends Composite {
    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;
    
    /** the local tree viewer */
    private TreeViewer m_treeViewer;

    /**
     * <code>m_parentTestCases</code>
     * Used to exclude TestCases which might cause circular dependencies.
     */
    private Set<INodePO> m_parentTestCases;

    /** Whether to show only categories */
    private boolean m_onlyCategories = false;

    /** Whether to show reused projects */
    private boolean m_reusedProjects = true;
    
    /** a list with the item numbers of circular dependend test cases */
    private Set < INodePO > m_circDependList = new HashSet < INodePO > ();

    /**
     * @param parent
     *            the parent
     * @param treeStyle
     *            the tree style to use
     * @param parentTestCases
     *            the parent test cases
     */
    public TestCaseTreeComposite(Composite parent, int treeStyle, 
            Set<INodePO> parentTestCases) {
        this(parent, treeStyle);
        m_parentTestCases = parentTestCases;
        initTreeViewer();
    }
    
    /**
     * @param parent
     *            the parent
     * @param treeStyle
     *            the tree style to use
     */
    private TestCaseTreeComposite(Composite parent, int treeStyle) {
        super(parent, SWT.NONE);

        // use Gridlayout
        final GridLayout gridLayout = new GridLayout();

        this.setLayout(gridLayout);

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;

        this.setLayoutData(gridData);

        final FilteredTree ft = new JBFilteredTree(this, treeStyle,
                new JBPatternFilter(), true);

        m_treeViewer = ft.getViewer();

        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.heightHint = WIDTH_HINT;
        LayoutUtil.addToolTipAndMaxWidth(layoutData, m_treeViewer.getControl());
        m_treeViewer.getControl().setLayoutData(layoutData);
        
    }    /**
     * @param parent
     *            the parent
     * @param treeStyle
     *            the tree style to use
     * @param parentTestCase
     *            the parent test case
     */
    public TestCaseTreeComposite(Composite parent, int treeStyle, 
        INodePO parentTestCase) {
        this(parent, treeStyle);
        m_parentTestCases = new HashSet<INodePO>();
        m_parentTestCases.add(parentTestCase);
        initTreeViewer();
    }

    /**
     * @param parent the parent composite
     * @param treeStyle the style of the tre
     * @param reuseds whether to show reused projects
     * @param onlyCategories whether to show only categories
     */
    public TestCaseTreeComposite(Composite parent, int treeStyle,
            boolean reuseds, boolean onlyCategories) {
        this(parent, treeStyle);
        m_reusedProjects = reuseds;
        m_onlyCategories = onlyCategories;
        initTreeViewer();
    }
    /**
     * Initialization of the TreeViewer with data
     */
    private void initTreeViewer() {
        m_treeViewer.setUseHashlookup(true);
        getInitialInput();
        m_treeViewer.setLabelProvider(new LabelProvider());
        m_treeViewer.setContentProvider(
                new TestCaseTreeCompositeContentProvider(
                        m_reusedProjects, m_onlyCategories));
        m_treeViewer.setInput(GeneralStorage.getInstance().getProject());
        m_treeViewer.setComparator(new NodeNameViewerSorter());
    }

    /**
     * gets a list of all test cases
     */
    private void getInitialInput() {
        if (m_parentTestCases != null) { 
            for (Iterator iterator = m_parentTestCases.iterator();
                    iterator.hasNext();) {
                ISpecTestCasePO type = (ISpecTestCasePO) iterator.next();
                DependencyFinderOp op = 
                        new DependencyFinderOp(type);
                TreeTraverser traverser = new TreeTraverser(GeneralStorage.
                    getInstance().getProject(), op, true);
                traverser.traverse(true);
                if (m_circDependList == null) {
                    m_circDependList = op.getDependentNodes();
                } else {
                    m_circDependList.addAll(op.getDependentNodes());
                }
            }
        }
    }

    /**
     * @return the tree viewer
     */
    public TreeViewer getTreeViewer() {
        return m_treeViewer;
    }

    /**
     * @return a flag indicating whether the selection is valid (e.g. no
     *         category or a node which would cause recursive loops)
     */
    public boolean hasValidSelection() {
        IStructuredSelection selection = 
                (IStructuredSelection)getTreeViewer().getSelection();
        if (m_onlyCategories) {
            return (selection.size() == 1)
                    && (selection.getFirstElement() instanceof ICategoryPO);
        }
        for (Object selectedObj : selection.toArray()) {
            if (m_circDependList.contains(selectedObj)
                    || selectedObj instanceof ICategoryPO
                    || selectedObj instanceof IReusedProjectPO) {
                return false;
            }
        }
        return !selection.isEmpty();
    }
    
    /**
     * LabelProvider for m_treeViewer
     *
     * @author BREDEX GmbH
     * @created 14.06.2005
     */
    private class LabelProvider implements IColorProvider, ILabelProvider {

        /**
         * {@inheritDoc}
         */
        public Image getImage(Object element) {
            if (element instanceof ISpecTestCasePO) {
                if (m_circDependList.contains(element)) {
                    return Plugin.TC_DISABLED_IMAGE; 
                } 
                return IconConstants.TC_IMAGE;
            }

            if (element instanceof ICategoryPO
                    || element instanceof IReusedProjectPO) {
                return IconConstants.CATEGORY_IMAGE;
            }
            
            if (element instanceof IConditionalStatementPO) {
                return IconConstants.CONDITION;
            }
            
            if (element instanceof IDoWhilePO) {
                return IconConstants.DO_WHILE;
            }
            if (element instanceof IWhileDoPO) {
                return IconConstants.DO_WHILE;
            }
            if (element instanceof IIteratePO) {
                return IconConstants.ITERATE;
            }
            
            if (element instanceof IAbstractContainerPO) {
                return IconConstants.CONTAINER;
            }
            
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public String getText(Object element) {
            if (element instanceof INodePO
                    && ((INodePO) element).isSpecObjCont()) {
                // A bit weird, but the GeneralLabelProvider is
                // designed for the TSB / TCB, so its returned String
                // (Test Cases:) is not suitable here - we'll rather use
                // the project's name
                return GeneralLabelProvider.getTextImpl(
                        ((INodePO) element).getParentNode());
            }
            return GeneralLabelProvider.getTextImpl(element);
        }

        /**
         * {@inheritDoc}
         */
        public void addListener(ILabelProviderListener listener) {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public void dispose() {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public boolean isLabelProperty(Object element, String property) {
            // do nothing
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public void removeListener(ILabelProviderListener listener) {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public Color getForeground(Object element) {
            if (element instanceof ISpecTestCasePO) {
                if (m_circDependList.contains(element)) {
                    return LayoutUtil.GRAY_COLOR; 
                } 
                return LayoutUtil.DEFAULT_OS_COLOR;
            }
            
            if (element instanceof ICategoryPO
                    || element instanceof IReusedProjectPO) {
                return LayoutUtil.GRAY_COLOR;
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Color getBackground(Object element) {
            return null;
        }        
    }
}

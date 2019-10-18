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
package org.eclipse.jubula.client.inspector.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.inspector.ui.model.InspectedComponent;
import org.eclipse.jubula.client.inspector.ui.model.InspectorTreeNode;
import org.eclipse.jubula.client.inspector.ui.provider.labelprovider.InspectorLabelProvider;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.views.NonSortedPropertySheetPage;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;


/**
 * Displays content describing a GEF component. 
 *
 * @author BREDEX GmbH
 * @created Jun 10, 2009
 */
public class InspectorView extends ViewPart {

    /** the ID of this view */
    public static final String VIEW_ID = "org.eclipse.jubula.client.inspector.views.inspectorView"; //$NON-NLS-1$
    
    /** the tree viewer showing information collected from the Inspector */
    private TreeViewer m_treeViewer;

    /** informs the part when the inspected component has changed */
    private PropertyChangeListener m_propChangeListener = 
            new PropertyChangeListener() {

        @SuppressWarnings("synthetic-access")
        public void propertyChange(PropertyChangeEvent evt) {
            final TreeNode[] input = convertToViewerModel(
                    InspectedComponent.getInstance().getCompId());
            Display display = PlatformUI.getWorkbench().getDisplay();
            display.syncExec(new Runnable() {

                public void run() {
                    m_treeViewer.setInput(input);
                    m_treeViewer.expandAll();
                }

            });
            if (input != null) {
                display.syncExec(new Runnable() {
                    public void run() {
                        TreeNode element = input[0];
                        while (element != null && element.hasChildren()) {
                            element = element.getChildren()[0];
                        }
                        if (element != null) {
                            m_treeViewer.setSelection(
                                    new StructuredSelection(element));
                        }
                    }

                });
            }
        }

    };
    
    /**
     * 
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        m_treeViewer = new TreeViewer(parent);
        m_treeViewer.setContentProvider(new TreeNodeContentProvider());
        m_treeViewer.setLabelProvider(new InspectorLabelProvider());
        getViewSite().setSelectionProvider(m_treeViewer);
        m_treeViewer.setInput(convertToViewerModel(
                InspectedComponent.getInstance().getCompId()));
        m_treeViewer.expandAll();
        InspectedComponent.getInstance().addPropertyChangeListener(
                m_propChangeListener);

        // Create menu manager and menu
        MenuManager menuMgr = new MenuManager();
        Menu menu = menuMgr.createContextMenu(m_treeViewer.getTree());
        m_treeViewer.getTree().setMenu(menu);
        // Register menu for extension.
        getViewSite().registerContextMenu(VIEW_ID, menuMgr, m_treeViewer);
        
        PlatformUI.getWorkbench().getHelpSystem().setHelp(
                m_treeViewer.getControl(),
                ContextHelpIds.INSPECTOR_VIEW);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setFocus() {
        m_treeViewer.getTree().setFocus();
    }

    /** {@inheritDoc} */
    public Object getAdapter(Class key) {
        if (key.equals(IPropertySheetPage.class)) {
            return new NonSortedPropertySheetPage();
        }
        return super.getAdapter(key);
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        InspectedComponent.getInstance().removePropertyChangeListener(
                m_propChangeListener);
        super.dispose();
    }

    /**
     * Creates and returns an Inspector viewer model corresponding to the
     * given component identifier. 
     * 
     * @param compId The component identifier for which to generate a
     *               viewer model.
     * @return the top-level elements of the converted viewer model.
     */
    private InspectorTreeNode[] convertToViewerModel(
            IComponentIdentifier compId) {
        if (compId == null) {
            return null;
        }
        List<String> hierarchyList = compId.getHierarchyNames();
        if (hierarchyList == null || hierarchyList.isEmpty()) {
            return null;
        }
        String [] hierarchy = 
            hierarchyList.toArray(new String[hierarchyList.size()]);

        InspectorTreeNode rootNode = new InspectorTreeNode(hierarchy);
        TreeNode node = rootNode;
        // Start iteration at index 1 because we already have the first element
        for (int i = 1; i < hierarchy.length; i++) {
            String [] childData = new String [hierarchy.length - i];
            System.arraycopy(hierarchy, i, childData, 0, 
                    childData.length);
            TreeNode child = new InspectorTreeNode(childData);
            TreeNode [] nodeChildren = node.getChildren();
            if (nodeChildren == null) {
                nodeChildren = new TreeNode[0];
            }
            TreeNode [] newNodeChildren = 
                new TreeNode [nodeChildren.length + 1];
            System.arraycopy(nodeChildren, 0, newNodeChildren, 0, 
                    nodeChildren.length);
            newNodeChildren[newNodeChildren.length - 1] = child;
            node.setChildren(newNodeChildren);
            child.setParent(node);
            node = child;
        }

        return new InspectorTreeNode [] {rootNode};
    }
}

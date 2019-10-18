/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.toolkit.ui.view;

import java.util.Iterator;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.filter.JBPatternFilter;
import org.eclipse.jubula.client.ui.rcp.filter.JBFilteredTree;
import org.eclipse.jubula.client.ui.rcp.provider.contentprovider.AbstractTreeViewContentProvider;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ParamValueSet;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ValueSetElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.part.ViewPart;

/**
 * @author BREDEX GmbH
 */
public class ToolkitOverView extends ViewPart {
    /**
     * @author BREDEX GmbH
     */
    private final class OverViewLabelProvider extends
        GeneralLabelProvider {
        /** {@inheritDoc} */
        public String getText(Object element) {
            final StringHelper sh = StringHelper.getInstance();
            if (element instanceof ToolkitDescriptor) {
                ToolkitDescriptor toolkitDescriptor = 
                    (ToolkitDescriptor) element;
                StringBuilder sb = new StringBuilder(
                    toolkitDescriptor.getName());
                final String parent = toolkitDescriptor.getIncludes();
                if (!StringUtils.isBlank(parent)) {
                    final ToolkitDescriptor parentDescriptor = COMP_SYSTEM
                        .getToolkitDescriptor(parent);
                    if (parentDescriptor != null) {
                        sb.append(" --> ").append(parentDescriptor.getName()); //$NON-NLS-1$
                    }
                }
                return sb.toString();
            } else if (element instanceof Component) {
                Component component = (Component) element;
                StringBuilder sb = new StringBuilder(sh.get(
                    component.getType(), true));
                if (component instanceof ConcreteComponent) {
                    ConcreteComponent concreteComponent = 
                        (ConcreteComponent) component;
                    final String componentClass = 
                        concreteComponent.getComponentClass().getName();
                    if (!StringUtils.isBlank(componentClass)) {
                        sb.append(" - ").append(componentClass); //$NON-NLS-1$
                    }
                }
                String description = component.getDescriptionKey();
                if (description != null) {
                    sb.append(" (" + CompSystemI18n.getString(description) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                final String since = component.getSince();
                if (!StringUtils.isBlank(since)) {
                    sb.append(" @since ").append(since); //$NON-NLS-1$
                }
                return sb.toString();
            } else if (element instanceof Action) {
                Action action = (Action) element;
                String description = action.getDescriptionKey();
                if (description != null) {
                    return sh.get(action.getName(), true) + " (" //$NON-NLS-1$
                            + CompSystemI18n.getString(description) + ")"; //$NON-NLS-1$
                }
                return sh.get(action.getName(), true);
            } else if (element instanceof Param) {
                Param param = (Param) element;
                StringBuilder sb = new StringBuilder();
                if (param.isOptional()) {
                    sb.append("optional: "); //$NON-NLS-1$
                }
                sb.append(CompSystemI18n.getString(param.getType())).append(" : "); //$NON-NLS-1$;
                sb.append(sh.get(param.getName(), true));
                if (param.hasValueSet()) {
                    sb.append(" ["); //$NON-NLS-1$
                    ParamValueSet valueSet = param.getValueSet();
                    Iterator<ValueSetElement> iterator = valueSet.iterator();
                    while (iterator.hasNext()) {
                        sb.append(CompSystemI18n.getString(iterator.next()
                            .getValue(), true));
                        if (iterator.hasNext()) {
                            sb.append(", "); //$NON-NLS-1$
                        }
                    }
                    sb.append("]"); //$NON-NLS-1$
                }
                String description = param.getDescriptionKey();
                if (description != null) {
                    sb.append(" (" + CompSystemI18n.getString(description) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                return sb.toString();
            }
            return super.getText(element);
        }

        /** {@inheritDoc} */
        public Image getImage(Object element) {
            if (element instanceof ToolkitDescriptor) {
                return IconConstants.CATEGORY_IMAGE;
            }
            if (element instanceof Component) {
                if (element instanceof ConcreteComponent) {
                    ConcreteComponent concreteComponent = 
                        (ConcreteComponent) element;
                    final String componentClass = 
                        concreteComponent.getComponentClass().getName();
                    if (!StringUtils.isBlank(componentClass)) {
                        return IconConstants.TECHNICAL_NAME_IMAGE;
                    }
                }
                return IconConstants.LOGICAL_NAME_IMAGE;
            }
            if (element instanceof Action) {
                return IconConstants.CAP_IMAGE;
            }
            if (element instanceof Param) {
                return IconConstants.UP_ARROW_DIS_IMAGE;
            }
            return null;
        }
        
        @Override
        public Color getForeground(Object element) {
            if (element instanceof ConcreteComponent) {
                ConcreteComponent concreteComponent = 
                    (ConcreteComponent) element;
                if (!concreteComponent.isSupported()) {
                    return LayoutUtil.GRAY_COLOR;
                }
            }
            return super.getForeground(element);
        }
    }

    /**
     * @author BREDEX GmbH
     */
    private final class OverViewContentProvider extends
        AbstractTreeViewContentProvider {
        /** {@inheritDoc} */
        public Object[] getChildren(Object parent) {
            if (parent instanceof CompSystem) {
                CompSystem compSystem = (CompSystem) parent;
                return compSystem.getAllToolkitDescriptors()
                    .toArray();
            }
            if (parent instanceof ToolkitDescriptor) {
                ToolkitDescriptor toolkitDescriptor = 
                    (ToolkitDescriptor) parent;
                return COMP_SYSTEM.getComponents(
                    toolkitDescriptor.getToolkitID(), true).toArray();
            }
            if (parent instanceof Component) {
                Component component = (Component) parent;
                return component.getActions().toArray();
            }
            if (parent instanceof Action) {
                Action action = (Action) parent;
                return action.getParams().toArray();
            }
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }

        /** {@inheritDoc} */
        public Object getParent(Object element) {
            return null;
        }
    }

    /** the component system instance */
    private static final CompSystem COMP_SYSTEM = ComponentBuilder
        .getInstance().getCompSystem();
    
    /** the viewer */
    private TreeViewer m_viewer;

    /** Constructor */
    public ToolkitOverView() {
    }

    /** {@inheritDoc} */
    public void createPartControl(Composite parent) {
        final FilteredTree ft = new JBFilteredTree(parent, SWT.MULTI
            | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, new JBPatternFilter(),
            true);
        setTreeViewer(ft.getViewer());

        final TreeViewer treeViewer = getTreeViewer();
        treeViewer.setContentProvider(new OverViewContentProvider());
        treeViewer.setLabelProvider(new OverViewLabelProvider());
        treeViewer.setComparator(new ViewerComparator());
        treeViewer.setInput(COMP_SYSTEM);
        
        getSite().setSelectionProvider(treeViewer);
    }

    /**
     * @return the tree viewer
     */
    private TreeViewer getTreeViewer() {
        return m_viewer;
    }

    /**
     * @param viewer
     *            the tree viewer
     */
    private void setTreeViewer(TreeViewer viewer) {
        m_viewer = viewer;
    }

    /** {@inheritDoc} */
    public void setFocus() {
        getTreeViewer().getTree().setFocus();
    }
}
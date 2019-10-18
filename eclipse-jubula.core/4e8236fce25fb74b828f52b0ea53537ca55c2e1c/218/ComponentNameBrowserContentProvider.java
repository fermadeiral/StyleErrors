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
package org.eclipse.jubula.client.ui.rcp.provider.contentprovider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.TooltipLabelProvider;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;



/**
 * The ContentProvider for the Component Names Browser including the Label
 * and Color Provider
 * 
 * @author BREDEX GmbH
 * @created 06.02.2009
 */

public class ComponentNameBrowserContentProvider extends TooltipLabelProvider
        implements ITreeContentProvider, IColorProvider, 
        IProjectLoadedListener, IDataChangedListener {

    /** The color for disabled elements */
    private static final Color DISABLED_COLOR = LayoutUtil.GRAY_COLOR;
    
    /** dummy object to return */
    private static final Object[] DUMMY = new Object[0];

    /** the tree viewer */
    private TreeViewer m_viewer;

    /** the child <-> parent relationship map */
    private Map<Object, Object> m_relationShip = 
            new HashMap<Object, Object>(117);
    
    /** the parent->children cache */
    private Map<Object, List<Object>> m_children = 
            new HashMap<Object, List<Object>>(79);
    
    
    /**
     * register handler for project load and data change to clear caches
     */
    public ComponentNameBrowserContentProvider() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addProjectLoadedListener(this, true);
        ded.addDataChangedListener(this, false);
    }
    /**
     * Drops all cached data.
     */
    private void clearCaches() {
        m_relationShip.clear();
        m_children.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        clearCaches();
    }

    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        boolean refreshView = false;
        for (DataChangedEvent e : events) {
            if (e.getUpdateState() != UpdateState.onlyInEditor
                    && (e.getPo() instanceof IComponentNamePO
                        || ((e.getDataState() == DataState.Saved)
                        && (e.getPo() instanceof IObjectMappingPO)
                            || (e.getPo() instanceof ITestCasePO)
                            || (e.getPo() instanceof ITestSuitePO)))) {
                refreshView = true;
                break;
            }
        }
        if (refreshView) {
            clearCaches();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentO) {
        List<Object> cachedChildren = m_children.get(parentO);
        if (cachedChildren != null) {
            return cachedChildren.toArray();
        }
        Object[] children = DUMMY;
        if (parentO instanceof IProjectPO
                || parentO instanceof IReusedProjectPO) {
            children = handleProjectTree(parentO); 
        }

        if (parentO instanceof AbstractCompNamesCategory) {
            AbstractCompNamesCategory pe = 
                (AbstractCompNamesCategory)parentO;
            if (pe instanceof UsedCompnamesCategory) {
                children = CompNameManager.getInstance().
                        getUsedCompNames(pe.m_parentProjectID);
            }
            if (pe instanceof UnusedCompnamesCategory) {
                children = CompNameManager.getInstance().
                        getUnusedCompNames(pe.m_parentProjectID);
            }
            if (pe instanceof ReusedCompnamesCategory) {
                children = findAllReusedProjects(pe);
            }
        }

        if (children != DUMMY) {
            List <Object>childrenList = new ArrayList<Object>(children.length);
            for (Object o : children) {
                m_relationShip.put(o, parentO);
                childrenList.add(o);
            }
            m_children.put(parentO, childrenList);
        }

        return children;
    }
    /**
     * @param parentO a Project or ReusedProject as root
     * @return an array of the parent children
     */
    private Object[] handleProjectTree(Object parentO) {
        Long parentProjectID = null;
        Object[] children = DUMMY;
        if (parentO instanceof IProjectPO) {
            parentProjectID = ((IProjectPO)parentO).getId();
        } else {
            IReusedProjectPO rp = ((IReusedProjectPO)parentO);
            try {
                parentProjectID = ProjectPM.findProjectId(rp
                        .getProjectGuid(), rp.getMajorNumber(), rp
                        .getMinorNumber(), rp.getMicroNumber(), rp
                        .getVersionQualifier());
            } catch (JBException e) {
                // do nothing
            }
        }
        if (parentProjectID != null) {
            UsedCompnamesCategory usedCat = new UsedCompnamesCategory(
                    parentProjectID, parentO);
            UnusedCompnamesCategory unCat = new UnusedCompnamesCategory(
                    parentProjectID, parentO);
            try {
                boolean areThereReusedProjects;
                if (parentO instanceof IProjectPO) {
                    IProjectPO project = (IProjectPO) parentO;
                    areThereReusedProjects = project.getUsedProjects()
                            .size() > 0;
                } else {
                    areThereReusedProjects =
                            ProjectPM.getReusedProjectsForProjectRO(
                                    parentProjectID).size() > 0;
                }
                if (areThereReusedProjects) {
                    ReusedCompnamesCategory reusedCat = 
                        new ReusedCompnamesCategory(
                            parentProjectID, parentO);
                    children = 
                        new AbstractCompNamesCategory[] { usedCat,
                                                          unCat, 
                                                          reusedCat };
                } else {
                    children = 
                        new AbstractCompNamesCategory[] { usedCat, 
                                                          unCat };
                }
            } catch (JBException e) {
                // nothing to catch here
            }
        }
        return children;
    }

    /**
     * @param pe
     *            the parent element
     * @return a list of all reused project pos
     */
    private Object[] findAllReusedProjects(AbstractCompNamesCategory pe) {
        try {
            List<IReusedProjectPO> usedProjects = ProjectPM
                    .getReusedProjectsForProjectRO(pe.getParentProjectID());
            return usedProjects.toArray();
        } catch (JBException e) {
            // nothing to catch here
        }
        return DUMMY;
    }

    /** {@inheritDoc} */
    public Object getParent(Object element) {
        return m_relationShip.get(element);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasChildren(Object element) {
        List<Object> children = m_children.get(element);
        if (children != null) {
            return !children.isEmpty();
        }
        return getChildren(element).length != 0;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getElements(Object inputElement) {
        List<Object> children = m_children.get(inputElement);
        if (children != null) {
            return children.toArray();
        }
        return getChildren(inputElement);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        clearCaches();
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.removeProjectLoadedListener(this);
        ded.removeDataChangedListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (viewer instanceof TreeViewer) {
            setViewer((TreeViewer)viewer);
        }
        if (oldInput != newInput) {
            clearCaches();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String getText(Object element) {
        if (element instanceof AbstractCompNamesCategory) {
            return ((AbstractCompNamesCategory)element).getName();
        }

        if (element instanceof IComponentNamePO) {
            IComponentNamePO cName = ((IComponentNamePO)element);
            cName = CompNameManager.getInstance().getResCompNamePOByGuid(
                    cName.getGuid());
            String cType = CompSystemI18n.getString(cName.getComponentType());
            String displayName = cName.getName() 
                 + StringConstants.SPACE + StringConstants.LEFT_BRACKET 
                 + cType + StringConstants.RIGHT_BRACKET;
            
            int count = CompNameManager.getInstance().
                    getUsageByGuid(cName.getGuid());
            
            if (Plugin.getDefault().getPreferenceStore()
                    .getBoolean(Constants.SHOWCOUNTERS_KEY)) {
                displayName += StringConstants.SPACE
                        + StringConstants.LEFT_PARENTHESIS
                        + CompNameManager.getInstance().
                        getUsageByGuid(cName.getGuid())
                        + StringConstants.RIGHT_PARENTHESIS;
            }

            return displayName;
        }
        
        if (element instanceof IPersistentObject) {
            IPersistentObject node = ((IPersistentObject)element);
            if (node.getName() != null) {
                return node.getName();
            }
            return node.toString();
        }
        
        Assert.notReached(
                org.eclipse.jubula.client.ui.i18n.Messages
                    .UnknownTypeOfElementInTreeOfType 
                + StringConstants.SPACE
                + element.getClass().getName());
        return super.getText(element);
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage(Object element) {
        if (element instanceof IComponentNamePO) {
            return IconConstants.LOGICAL_NAME_IMAGE;
        }
        if (element instanceof IReusedProjectPO) {
            return IconConstants.PROJECT_IMAGE;
        }
        if (element instanceof AbstractCompNamesCategory) {
            return IconConstants.CATEGORY_IMAGE;
        }

        return super.getImage(element);
    }
    

    /**
     * @return the viewer
     */
    public TreeViewer getViewer() {
        return m_viewer;
    }

    /**
     * @param viewer the viewer to set
     */
    private void setViewer(TreeViewer viewer) {
        m_viewer = viewer;
    }

    /**
     * {@inheritDoc}
     */
    public Color getBackground(Object element) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Color getForeground(Object element) {
        IProjectPO cp = GeneralStorage.getInstance().getProject();
        if (cp != null) {
            // only local component names
            if (element instanceof IComponentNamePO
                    && cp.getId().equals(
                        ((IComponentNamePO)element).getParentProjectId())) {
                return null;
            }
            // and local component name categories should appear editable
            if (element instanceof UsedCompnamesCategory
                    || element instanceof UnusedCompnamesCategory) {
                AbstractCompNamesCategory ac = 
                    (AbstractCompNamesCategory)element;
                if (ac.getParentProjectID() == cp.getId()) {
                    return null;
                }
            }
        }

        return DISABLED_COLOR;
    }
    
    /**
     * A synthetic GUI class
     * 
     * @author BREDEX GmbH
     * @created 10.02.2009
     */
    public abstract class AbstractCompNamesCategory {
        /** The name of this node */
        private String m_name;

        /** The parent of this node */
        private long m_parentProjectID;
        
        /** the parent object of this node */
        private Object m_parent;
        
        /**
         * {@inheritDoc}
         */
        public boolean equals(Object obj) {
            if (obj instanceof AbstractCompNamesCategory) {
                AbstractCompNamesCategory sg = 
                    (AbstractCompNamesCategory)obj;
                return new EqualsBuilder().
                    append(m_name, sg.getName()).
                        append(m_parent, sg.getParent()).
                            isEquals();
            }
            
            return super.equals(obj);
        }
        
        /**
         * {@inheritDoc}
         */
        public int hashCode() {
            return new HashCodeBuilder().
                append(m_name).
                    append(m_parent).
                        toHashCode();
        }
        
        /**
         * @return the parent
         */
        public Object getParent() {
            return m_parent;
        }

        /**
         * @param parent the parent to set
         */
        public void setParent(Object parent) {
            m_parent = parent;
        }

        /**
         * @return the parentProjectID
         */
        public long getParentProjectID() {
            return m_parentProjectID;
        }

        /**
         * @param parentProjectID
         *            the parentProjectID to set
         */
        public void setParentProjectID(long parentProjectID) {
            m_parentProjectID = parentProjectID;
        }

        /**
         * @return the name
         */
        public String getName() {
            return m_name;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(String name) {
            m_name = name;
        }
    }

    /**
     * A synthetic GUI class for used component names
     * 
     * @author BREDEX GmbH
     * @created 16.02.2009
     */
    public class UsedCompnamesCategory extends AbstractCompNamesCategory {
                
        /**
         * @param parentID
         *            the parent project id
         * @param parent
         *            the parent object
         */
        public UsedCompnamesCategory(long parentID, Object parent) {
            setName(Messages.CompNameBrowserUsedCat);
            setParentProjectID(parentID);
            setParent(parent);
        }
    }
    
    /**
     * A synthetic GUI class for unused component names
     * 
     * @author BREDEX GmbH
     * @created 16.02.2009
     */
    public class UnusedCompnamesCategory extends AbstractCompNamesCategory {
        
        /**
         * @param parentID
         *            the parent project id
         * @param parent
         *            the parent object
         */
        public UnusedCompnamesCategory(long parentID, Object parent) {
            setName(Messages.CompNameBrowserUnusedCat);
            setParentProjectID(parentID);
            setParent(parent);
        }
    }
    
    /**
     * A synthetic GUI class for unused component names
     * 
     * @author BREDEX GmbH
     * @created 16.02.2009
     */
    public class ReusedCompnamesCategory extends AbstractCompNamesCategory {
        /**
         * @param parentID
         *            the parent project id
         * @param parent
         *            the parent object
         */
        public ReusedCompnamesCategory(long parentID, Object parent) {
            setName(Messages.CompNameBrowserReusedCat);
            setParentProjectID(parentID);
            setParent(parent);
        }
    }
}
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
package org.eclipse.jubula.client.ui.rcp.search.result;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.client.ui.rcp.search.query.ShowWhereReferencedCTDSValueQuery.CTDSReference;
import org.eclipse.jubula.client.ui.rcp.views.AbstractJBTreeView;
import org.eclipse.jubula.client.ui.rcp.views.dataset.DataSetView;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;


/**
 * @author BREDEX GmbH
 * @created Jul 26, 2010
 * 
 * @param <DATATYPE> the data type
 */
public class BasicSearchResult<DATATYPE> implements ISearchResult {
    /**
     * <code>resultList</code>
     */
    private List<DATATYPE> m_resultList = Collections.emptyList();
    
    /**
     * <code>m_searchQuery</code>
     */
    private ISearchQuery m_query;

    /**
     * Construtor
     * @param query the query which has been performed to get this result
     */
    public BasicSearchResult(ISearchQuery query) {
        setQuery(query);
    }
    
    /**
     * @param query the query to set
     */
    public void setQuery(ISearchQuery query) {
        m_query = query;
    }

    /**
     * @return the query
     */
    public ISearchQuery getQuery() {
        return m_query;
    }

    /**
     * @param resultList the resultList to set
     */
    public void setResultList(List<DATATYPE> resultList) {
        this.m_resultList = resultList;
    }

    /**
     * @return the resultList
     */
    public List<DATATYPE> getResultList() {
        return m_resultList;
    }
    
    /** {@inheritDoc} */
    public void addListener(ISearchResultListener l) {
        // currently no listener support
    }

    /** {@inheritDoc} */
    public ImageDescriptor getImageDescriptor() {
        // currently no image descriptor support
        return null;
    }

    /** {@inheritDoc} */
    public String getTooltip() {
        return StringUtils.EMPTY;
    }

    /** {@inheritDoc} */
    public void removeListener(ISearchResultListener l) {
        // currently no listener support
    }
    
    /** {@inheritDoc} */
    public String getLabel() {
        return getQuery().getLabel();
    }
    
    /**
     * Class for search results
     * @param <DATATYPE> the data type of the element
     * @author BREDEX GmbH
     * @created 07.12.2005
     * 
     * @param <DATATYPE> the data type
     */
    public static class SearchResultElement <DATATYPE> {

        /** The name of this element */
        private String m_name;
        
        /** Any data */
        private DATATYPE m_data;

        /** The Image */
        private Image m_image;

        /** delegate for "jump to" functionality */
        private ISearchResultElementAction<DATATYPE> m_action;

        /**
         * <code>m_comment</code>
         */
        private String m_comment;

        /**
         * <code>m_viewId</code>
         */
        private String m_viewId;
        
        /**
         * Constructor
         * 
         * @param name
         *            the name of this element to display
         * @param data
         *            any data
         * @param image
         *            an image to display, may be null
         * @param action
         *            delegate for "jumpt to" functionality
         * @param comment
         *            the comment to display for this element
         */
        public SearchResultElement(String name, DATATYPE data, Image image,
                ISearchResultElementAction<DATATYPE> action, String comment) {
            this(name, data, image, action, comment, null);
        }

        /**
         * Constructor
         * 
         * @param name
         *            the name of this element to display
         * @param data
         *            any data
         * @param image
         *            an image to display, may be null
         * @param action
         *            delegate for "jumpt to" functionality
         * @param comment
         *            the comment to display for this element
         * @param viewId
         *            the id of the view to show before executing jump action
         */
        public SearchResultElement(String name, DATATYPE data, Image image,
                ISearchResultElementAction<DATATYPE> action, String comment,
                String viewId) {
            m_name = name;
            m_data = data;
            m_image = image;
            m_action = action;
            m_comment = comment;
            m_viewId = viewId;
        }
        
        /**
         * Constructor
         * 
         * @param name
         *            the name of this element to display
         * @param data
         *            any data
         * @param image
         *            an image to display, may be null
         * @param action
         *            delegate for "jump to" functionality
         */
        public SearchResultElement(String name, DATATYPE data, Image image,
                ISearchResultElementAction<DATATYPE> action) {
            this(name, data, image, action, null);
        }
        
        /**
         * @return the action of this search result element
         */
        public ISearchResultElementAction<DATATYPE> getAction() {
            return m_action;
        }
        
        /**
         * @return the name of this search result element
         */
        public String getName() {
            return m_name;
        }

        /**
         * @return Returns the data.
         */
        public DATATYPE getData() {
            return m_data;
        }

        /**
         * @return Returns the image.
         */
        public Image getImage() {
            return m_image;
        }
        
        /**
         * @return Returns the comment.
         */
        public String getComment() {
            return m_comment;
        }

        /**
         * "Jumps" to the result of this element. This may activate or open
         * Views and/or Editors, as well as change the selection.
         */
        public void jumpToResult() {
            if (getViewId() != null) {
                m_action.openView(getViewId());
            }
            m_action.jumpTo(getData());
        }

        /**
         * @return the view id
         */
        public String getViewId() {
            return m_viewId;
        }

        /**
         * @return the relevant object (can be null!)
         */
        public Object getObject() {
            return m_action.getObject(m_data);
        }
    }
    
    /**
     * Encapsulates the ability to "jump to" a search result. This can include
     * opening Views / Editors and setting the selection.
     *
     * @param <DATATYPE> the data type of the element
     * @author BREDEX GmbH
     * @created Mar 10, 2009
     * 
     * @param <DATATYPE> the data type
     */
    public static interface ISearchResultElementAction <DATATYPE> {
        
        /**
         * "Jumps to" the given data. This can include opening Views / Editors 
         * and setting the selection.
         * 
         * @param data Information regarding how the "jump" should be performed.
         */
        public void jumpTo(DATATYPE data);

        /**
         * @param viewId the view id
         */
        public void openView(String viewId);

        /**
         * @param data the data
         * @return the relevant object (can be null)
         */
        public Object getObject(DATATYPE data);
    }

    /**
     * Action to use for "jumping" to an Object Mapping Association.
     *
     * @author BREDEX GmbH
     * @created Mar 10, 2009
     */
    public static class ObjectMappingSearchResultElementAction 
            implements ISearchResultElementAction <Long>, Serializable {

        /** Field to transfer data between two methods... */
        private IAUTMainPO m_aut = null;
        /**
         * {@inheritDoc}
         */
        public void openView(String viewId) {
            // no view opening support
        }

        /**
         * {@inheritDoc}
         */
        public void jumpTo(Long id) {
            // if we haven't yet identified the AUT...
            if (m_aut == null) {
                getObject(id);
            }
            // AUT may still be null due to the assoc having been deleted, etc.
            if (m_aut != null) {
                IEditorPart editor = 
                    AbstractOpenHandler.openEditor(m_aut);
                if (editor instanceof ObjectMappingMultiPageEditor) {
                    ObjectMappingMultiPageEditor omEditor =
                        (ObjectMappingMultiPageEditor)editor;
                    IObjectMappingAssoziationPO editorAssoc = 
                        getAssocForId(omEditor.getAut(), id);
                    if (editorAssoc != null) {
                        for (TreeViewer viewer 
                                : omEditor.getTreeViewers()) {
                            viewer.reveal(editorAssoc);
                            viewer.setSelection(
                                new StructuredSelection(editorAssoc));
                        }
                    }
                }
            }
        }

        /** {@inheritDoc} */
        public Object getObject(Long id) {
            for (IAUTMainPO aut : GeneralStorage.getInstance()
                    .getProject().getAutMainList()) {
                for (IObjectMappingAssoziationPO assoc 
                        : aut.getObjMap().getMappings()) {
                    if (id.equals(assoc.getId())) {
                        m_aut = aut;
                        return assoc;
                    }
                }
            }
            return null;
        }

        /**
         * Used in order to get a session-appropriate 
         * Object Mapping Association.
         * 
         * @param aut The AUT in which to search for the association.
         * @param id The ID of the association to find.
         * @return The association with the given ID in the Object Mapping for 
         *         the given AUT, or <code>null</code> if no such association
         *         can be found.
         */
        private IObjectMappingAssoziationPO getAssocForId(
                IAUTMainPO aut, Long id) {
            
            for (IObjectMappingAssoziationPO editorAssoc 
                    : aut.getObjMap().getMappings()) {
                if (id.equals(editorAssoc.getId())) {
                    return editorAssoc;
                }
            }
            
            return null;
        }
    }

    /**
     * Opens the Editor for a Test Data Cube, and selects a cell
     *      if it is given
     * @author BREDEX GmbH
     */
    public static class TestDataCubeExtendedAction
        implements ISearchResultElementAction<CTDSReference> {

        @Override
        public void jumpTo(CTDSReference data) {
            Object res = getObject(data);
            if (!(res instanceof INodePO)) {
                return;
            }
            INodePO spec = ((INodePO) res).getSpecAncestor();
            IEditorPart ed = AbstractOpenHandler.openEditorAndSelectNode(
                    spec, data.getNode());
            IViewPart v = Plugin.showView(DataSetView.ID);
            if (!(v instanceof DataSetView)) {
                return;
            }
            ((DataSetView) v).navigateToCellUsingRowCol(
                    data.getRow(), data.getColumn());
        }

        @Override
        public void openView(String viewId) {
            // empty, the jumpTo is responsible for all actions
        }

        /** {@inheritDoc} */
        public Object getObject(CTDSReference data) {
            return data.getNode();
        }
    }
    
    /**
     * Action to use for "jumping" to an Central Test Data Set
     * 
     * @author BREDEX GmbH
     * @created Aug 11, 2010
     */
    public static class TestDataCubeSearchResultElementAction 
            implements ISearchResultElementAction <Long> {
        /**
         * {@inheritDoc}
         */
        public void openView(String viewId) {
            Plugin.showView(viewId);
        }

        /**
         * {@inheritDoc}
         */
        public void jumpTo(Long id) {
            Object obj = getObject(id);
            if (!(obj instanceof ITestDataCubePO)) {
                return;
            }
            ITestDataCubePO testdatacube = (ITestDataCubePO) obj; 
            IEditorPart editor = AbstractOpenHandler
                    .openEditor(GeneralStorage.getInstance().
                            getProject().getTestDataCubeCont());
            if (editor instanceof CentralTestDataEditor) {
                CentralTestDataEditor ctdEditor = 
                    (CentralTestDataEditor)editor;
                ctdEditor.getTreeViewer().setSelection(
                    new StructuredSelection(ctdEditor.getEditorHelper()
                            .getEditSupport().getSession().find(
                                    testdatacube.getClass(), id)));
            }
        }

        /** {@inheritDoc} */
        public Object getObject(Long id) {
            IProjectPO activeProject = 
                    GeneralStorage.getInstance().getProject();
            for (ITestDataCubePO testdatacube 
                    : TestDataCubeBP.getAllTestDataCubesFor(activeProject)) {
                if (id.equals(testdatacube.getId())) {
                    return testdatacube;
                }
            }
            return null;
        }
    }
    
    /**
     * Action to use for "jumping" to a node.
     *
     * @author BREDEX GmbH
     * @created Mar 10, 2009
     */
    public static class NodeSearchResultElementAction 
            implements ISearchResultElementAction <Long> {
        /**
         * {@inheritDoc}
         */
        public void openView(String viewId) {
            Plugin.showView(viewId);
        }
        
        /** {@inheritDoc} */
        public void jumpTo(Long id) {
            AbstractJBTreeView jbtv = MultipleTCBTracker.getInstance()
                    .getMainTCB();
            if (jbtv == null) {
                jbtv = (AbstractJBTreeView) Plugin
                        .showView(Constants.TC_BROWSER_ID);
            } else {
                Plugin.activate(jbtv);
            }
            Object ob = getObject(id);
            if (!(ob instanceof INodePO)) {
                return;
            }
            INodePO node = (INodePO) ob;
            INodePO specNode = node.getSpecAncestor();
            if (specNode != null && NodeBP.isEditable(specNode)) {
                IEditorPart openEditor = AbstractOpenHandler
                        .openEditor(specNode);
                if (openEditor instanceof AbstractJBEditor) {
                    AbstractJBEditor jbEditor =
                            (AbstractJBEditor) openEditor;
                    jbEditor.setSelection(
                            new StructuredSelection(node));
                }
            }
        }

        /** {@inheritDoc} */
        public Object getObject(Long id) {
            return NodePM.findNodeById(id);
        }
    }
}
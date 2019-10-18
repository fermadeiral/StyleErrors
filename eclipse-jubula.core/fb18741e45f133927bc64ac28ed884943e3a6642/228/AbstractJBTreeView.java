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
package org.eclipse.jubula.client.ui.rcp.views;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.IEntityManagerProvider;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.UINodeBP;
import org.eclipse.jubula.client.ui.rcp.controllers.AbstractPartListener;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.filter.JBBrowserPatternFilter;
import org.eclipse.jubula.client.ui.rcp.filter.JBFilteredTree;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.sorter.NodeNameViewerSorter;
import org.eclipse.jubula.client.ui.rcp.utils.UIIdentitiyElementComparer;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;


/**
 * @author BREDEX GmbH
 * @created 21.10.2005
 */
public abstract class AbstractJBTreeView extends ViewPart implements
    IProjectLoadedListener, IDataChangedListener, IEntityManagerProvider {
    /** Default expansion for the tree */
    public static final int DEFAULT_EXPANSION = 2;
    /** number of columns = 1 */
    protected static final int NUM_COLUMNS_1 = 1;  
    /** vertical spacing = 2 */
    protected static final int VERTICAL_SPACING = 3;
    
    /**
     * <code>m_treeViewer</code>tree Viewer
     */
    private TreeViewer m_treeViewer;
    
    /**
     * <code>m_treeFilterText</code>tree Viewer
     */
    private Text m_treeFilterText;
    
    /** 
     * This part's reference to the clipboard.
     * Note that the part shares this clipboard with the entire operating 
     * system, and this instance is only for easier access to the clipboard. 
     * The clipboard does not exclusively belong to the part.
     */
    private Clipboard m_clipboard;
    
    /** flag whether the view is linked with the editor or not */
    private boolean m_isLinkedWithEditor = false;

    /** The partListener of this view */
    private PartListener m_partListener = new PartListener();
    
    /** The preference store to hold the existing preference values. */
    private IPreferenceStore m_store = Plugin.getDefault().getPreferenceStore();

    /**
     * This listener updates the selection of the view based on the activated
     * part.
     * 
     * @author BREDEX GmbH
     * @created 20.09.2006
     */
    private final class PartListener extends AbstractPartListener {
        /**
         * {@inheritDoc}
         */
        public void partActivated(IWorkbenchPart part) {
            setSelectionToEditorNode(part);
            super.partActivated(part);
        }
    }
    

    /**
     * Toggles "Link with Editor" functionality for this view. When linked,
     * the selection of the view can be changed automatically based on the 
     * currently active editor.
     * 
     * @author BREDEX GmbH
     * @created Nov 8, 2006
     */
    private final class ToggleLinkingAction extends Action {
        
        /**
         * Constructor
         */
        public ToggleLinkingAction() {
            super(Messages.TestCaseBrowserLinkWithEditor, IAction.AS_CHECK_BOX);
            setImageDescriptor(IconConstants.LINK_WITH_EDITOR_DESCRIPTOR);
            m_isLinkedWithEditor = Plugin.getDefault().getPreferenceStore()
                .getBoolean(Constants.LINK_WITH_EDITOR_TCVIEW_KEY);
            setChecked(m_isLinkedWithEditor);
        }
        
        /**
         * {@inheritDoc}
         */
        public void run() {
            m_isLinkedWithEditor = isChecked();
            Plugin.getDefault().getPreferenceStore().setValue(
                    Constants.LINK_WITH_EDITOR_TCVIEW_KEY, 
                    m_isLinkedWithEditor);
            if (Plugin.getActiveEditor() instanceof AbstractTestCaseEditor
                && m_isLinkedWithEditor) {
                
                setSelectionToEditorNode(Plugin.getActiveEditor());
            }
        }
    }
    
    /**
     * Sets the selection to the node of the current active editor if the
     * linking is enabled
     * @param part the current activated IWorkbenchPart
     */
    private void setSelectionToEditorNode(IWorkbenchPart part) {
        if (part != null) {
            Object obj = part.getAdapter(AbstractTestCaseEditor.class);
            AbstractTestCaseEditor tce = (AbstractTestCaseEditor)obj;
            if (obj != null && m_isLinkedWithEditor && tce != null) {
                final IPersistentObject editorWorkVersion = 
                    tce.getEditorHelper().getEditSupport().getWorkVersion();

                if (editorWorkVersion != null) {
                    UINodeBP.selectNodeInTree(editorWorkVersion.getId(),
                            getTreeViewer(), getEntityManager());
                }
            }
        }
    }

    /**
     * 
     */
    protected abstract void rebuildTree();

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapter) {
        if (adapter.equals(AbstractJBTreeView.class)) {
            return this;
        } else if (adapter.equals(IPropertySheetPage.class)) {
            return new JBPropertiesPage(false, null);
        }
        return super.getAdapter(adapter);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public final void handleProjectLoaded() {
        if (GeneralStorage.getInstance().getProject() == null) {
            // project-loaded fired for clearing the current project
            // do not rebuild the tree
            return;
        }
        Plugin.startLongRunning();
        
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                try {
                    rebuildTree();
                } catch (OperationCanceledException oce) {
                    getTreeViewer().setInput(null);
                }
            }
        });

        Plugin.stopLongRunning();
    }

    /**
     * @return tree Viewer
     */
    public TreeViewer getTreeViewer() {
        return m_treeViewer;
    }

    /**
     * @return selection
     */
    public ISelection getSelection() {
        return m_treeViewer.getSelection();
    }

    /**
     * @param selection selection to set
     */
    public void setSelection(ISelection selection) {
        m_treeViewer.setSelection(selection, true);
    }

    /**
     * @param treeViewer The treeViewer to set.
     */
    public void setTreeViewer(TreeViewer treeViewer) {
        m_treeViewer = treeViewer;
    }
    
    /** {@inheritDoc} */
    public void createPartControl(Composite parent) {
        m_clipboard = new Clipboard(parent.getDisplay());
        
        final FilteredTree ft = new JBFilteredTree(parent, SWT.MULTI
                | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, 
                new JBBrowserPatternFilter(), true);
        setTreeViewer(ft.getViewer());
        setTreeFilterText(ft.getFilterControl());
        addTreeListener();
        getTreeViewer().setUseHashlookup(true);
        getTreeViewer().setComparator(new NodeNameViewerSorter());
        getTreeViewer().setComparer(new UIIdentitiyElementComparer());

        getSite().setSelectionProvider(getTreeViewer());
        getTreeViewer().setAutoExpandLevel(DEFAULT_EXPANSION);
        addFilterBackgroundColoring(ft);
        
        final DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addProjectLoadedListener(this, false);
        ded.addDataChangedListener(this, true);
        
        getViewSite().getActionBars().getToolBarManager().add(
                new ToggleLinkingAction());
        getViewSite().getWorkbenchWindow().getPartService().addPartListener(
                m_partListener);
        
        setFocus();
        registerContextMenu();
    }

    /**
     * Adds Filter Background Coloring functionality
     * @param ft the Filtered Tree the functionality is supposed to be added to
     */
    private void addFilterBackgroundColoring(final FilteredTree ft) {
        ft.getFilterControl().addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (!getTreeFilterText().getText().isEmpty()
                    && m_store.getBoolean(Constants.BACKGROUND_COLORING_KEY)) {
                        getTreeViewer().getControl().setBackground(
                            new Color(Display.getCurrent(),
                                Utils.intToRgb(m_store.getInt(
                                    Constants.BACKGROUND_COLOR_KEY))));
                } else {
                    getTreeViewer().getControl().setBackground(
                        Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
                }
            }
        });
    }
    
    /**
     * Register the context menu for the receiver so that commands may be added
     * to it.
     */
    protected void registerContextMenu() {
        MenuManager contextMenu = new MenuManager();
        createContextMenu(contextMenu);
        contextMenu.add(new GroupMarker(
                IWorkbenchActionConstants.MB_ADDITIONS));
        Control control = getTreeViewer().getControl();
        Menu menu = contextMenu.createContextMenu(control);
        control.setMenu(menu);
        getSite().registerContextMenu(contextMenu, getTreeViewer());
    }
    
    /**
     * @param contextMenu the MenuManager to create the context menu for
     */
    protected abstract void createContextMenu(IMenuManager contextMenu);

    /**
     * Adds DoubleClickListener to Treeview.
     */
    protected abstract void addTreeListener();
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        try {
            m_clipboard.dispose();
            getViewSite().getWorkbenchWindow().getPartService()
                .removePartListener(m_partListener);
            final DataEventDispatcher dispatcher = 
                DataEventDispatcher.getInstance();
            // clear corresponding views
            dispatcher.firePartClosed(this);
            dispatcher.removeProjectLoadedListener(this);
        } finally {
            getSite().setSelectionProvider(null);
            super.dispose();
        }
    }
    
    /**
     * @return a reference to the clipboard.
     */
    public Clipboard getClipboard() {
        return m_clipboard;
    }
    
    /**
     * @param treeFilterText the treeFilterText to set
     */
    public void setTreeFilterText(Text treeFilterText) {
        m_treeFilterText = treeFilterText;
    }

    /**
     * @return the treeFilterText
     */
    public Text getTreeFilterText() {
        return m_treeFilterText;
    }
    
    /**
     * {@inheritDoc}
     */
    public EntityManager getEntityManager() {
        GeneralStorage gs = GeneralStorage.getInstance();
        if (gs != null) {
            return gs.getEntityManager();
        }
        return null;
    }
    
    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        DataChangedEvent previousEvent = null;
        for (DataChangedEvent e : events) {
            final DataState dataState = e.getDataState();
            if (previousEvent != null
                    && previousEvent.getDataState() == dataState
                    && (dataState == DataState.StructureModified 
                     || dataState == DataState.Deleted)) {
                continue;
            }
            handleDataChanged(e.getPo(), dataState, e.getUpdateState());
            previousEvent = e;
        }
    }

    /**
     * 
     * @param objects the objects to search the object in the current master 
     *        session, should be of type {@link IPersistentObject} all other are ignored
     * @return a {@link List} of found items in the master session
     */
    protected List<Object> getListOfItemsFromOldPO(Object[] objects) {
        EntityManager masterSession = GeneralStorage.getInstance()
                .getMasterSession();
        List<Object> foundObjects = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof IPersistentObject) {
                IPersistentObject cat = (IPersistentObject) object;
                Object find =
                        masterSession.find(object.getClass(), cat.getId());
                if (find != null) {
                    foundObjects.add(find);
                }
            }
        }
        return foundObjects;
    }
    /**
     * handle a single data changed event
     * 
     * @param po
     *            the changed po
     * @param dataState
     *            the data state
     * @param updateState
     *            the update state
     */
    protected abstract void handleDataChanged(final IPersistentObject po,
            final DataState dataState, final UpdateState updateState);
}
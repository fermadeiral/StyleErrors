/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.bridge.listener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.alm.mylyn.ui.bridge.bridge.EditorContextStructureBridge;
import org.eclipse.jubula.client.alm.mylyn.ui.bridge.constants.ContentType;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.controllers.TreeViewContainerGUIController;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Nov 10, 2010
 */
public class TaskActivationListener implements ITaskActivationListener,
        IProjectStateListener {
    /** standard logging */
    private static final Logger LOG = 
        LoggerFactory.getLogger(DataEventDispatcher.class);

    /** {@inheritDoc} */
    public void preTaskActivated(ITask task) {
        // do nothing
    }

    /** {@inheritDoc} */
    public void preTaskDeactivated(ITask task) {
        // do nothing
    }

    /** {@inheritDoc} */
    public void taskActivated(ITask task) {
        closeAllEditors();
        if (GeneralStorage.getInstance().getProject() != null) {
            restoreOpenEditors(task);
        }
    }

    /**
     * @param task
     *            - the active task
     */
    private void restoreOpenEditors(ITask task) {
        List<IInteractionElement> interesting = ContextCore.getContextManager()
                .getActiveContext().getInteresting();
        List<String> handles = new ArrayList<String>();
        for (IInteractionElement element : interesting) {
            handles.add(element.getHandleIdentifier());
            IProjectPO project = GeneralStorage.getInstance().getProject();
            if (project != null) {
                if (element.getContentType().equals(ContentType.CTD_EDITOR)) {
                    ITestDataCategoryPO centralTestData = project
                            .getTestDataCubeCont();
                    if (centralTestData != null) {
                        IEditorPart editor = AbstractOpenHandler
                                .openEditor(centralTestData);
                        if (editor != null) {
                            editor.getSite().getPage().activate(editor);
                        }
                    }
                } else if (element.getContentType().equals(
                        ContentType.OM_EDITOR)) {
                    for (IAUTMainPO po : project.getAutMainList()) {
                        if (po != null
                                && po.getGuid().equals(
                                        element.getHandleIdentifier())) {
                            IEditorPart editor = AbstractOpenHandler
                                    .openEditor(po);
                            if (editor != null) {
                                editor.getSite().getPage().activate(editor);
                            }
                        }
                    }
                }
            }
        }
        for (String id : handles) {
            if (id.endsWith(EditorContextStructureBridge.EDITOR_ID_POSTFIX)) {
                AbstractContextStructureBridge bridge = ContextCore
                        .getStructureBridge(EditorContextStructureBridge.
                                EDITOR_CONTENT_TYPE);
                Object objForHandle = bridge.getObjectForHandle(id);
                if (objForHandle instanceof INodePO) {
                    AbstractOpenHandler.openEditor((INodePO)objForHandle);
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void taskDeactivated(ITask task) {
        collapseTree((ITreeViewerContainer) Plugin
                .getView(Constants.TS_BROWSER_ID));

        for (TestCaseBrowser tcb : MultipleTCBTracker.getInstance()
                .getOpenTCBs()) {
            collapseTree(tcb);
        }
    }

    /**
     * @param tvc
     *            - the tree view container to collapse
     */
    private void collapseTree(ITreeViewerContainer tvc) {
        if (tvc != null) {
            TreeViewContainerGUIController.collapseExpandTree(tvc);
        }
    }

    /** {@inheritDoc} */
    public void handleProjectStateChanged(ProjectState state) {
        if (ProjectState.opened.equals(state)) {
            ITask currentTask = TasksUi.getTaskActivityManager()
                    .getActiveTask();
            if (currentTask != null) {
                restoreOpenEditors(currentTask);
            }
        }
    }

    /**
     * close all editors
     */
    public void closeAllEditors() {
        try {
            if (PlatformUI.getWorkbench().isClosing()) {
                return;
            }
            for (IWorkbenchWindow window : MonitorUi.getMonitoredWindows()) {
                IWorkbenchPage page = window.getActivePage();
                if (page != null) {
                    IEditorReference[] references = page.getEditorReferences();
                    List<IEditorReference> toClose = 
                        new ArrayList<IEditorReference>();
                    for (IEditorReference reference : references) {
                        toClose.add(reference);
                    }
                    page.closeEditors(toClose.toArray(
                            new IEditorReference[toClose.size()]), true);
                }
            }
        } catch (Throwable t) {
            LOG.error("Unhandled exception while closing all editors", t); //$NON-NLS-1$
        }
    }
}

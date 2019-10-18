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
package org.eclipse.jubula.client.alm.mylyn.ui.handler;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.alm.mylyn.ui.dialogs.InspectALMAttributesDialog;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.mylyn.utils.MylynAccess;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class InspectAlmAttributes extends AbstractSelectionBasedHandler {
    
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(InspectAlmAttributes.class);
    /**
     * This class loads the attributes from the corresponding ALM system
     * @author BREDEX GmbH
     */
    private class LoadAttributesFromAlm implements IRunnableWithProgress {
        /** the task for which we get the attributes*/
        private ITask m_task;

        /**
         * @param task the task to inspect
         */
        public LoadAttributesFromAlm(ITask task) {
            m_task = task;
        }

        @Override
        public void run(IProgressMonitor monitor) 
            throws InvocationTargetException {
            IRepositoryManager repositoryManager = TasksUi
                    .getRepositoryManager();
            final TaskRepository repo = repositoryManager.getRepository(
                    m_task.getConnectorKind(), m_task.getRepositoryUrl());
            IStatus ok = MylynAccess.testConnection(repo.getRepositoryLabel());
            if (ok.getSeverity() != IStatus.OK) {
                throw new InvocationTargetException(null, ok.getMessage());
            }
            final AbstractRepositoryConnector connector = TasksUi
                    .getRepositoryConnector(repo.getConnectorKind());
            TaskAttribute attribute = null;
            if (attribute == null) {

                try {
                    TaskData taskData = connector.getTaskData(repo,
                            m_task.getTaskId(), new NullProgressMonitor());
                    attribute = taskData.getRoot();
                    m_attributes = taskData.getRoot();
                } catch (CoreException e) {
                    LOG.error("Unexpected error occurred", e); //$NON-NLS-1$
                    throw new InvocationTargetException(e);
                }
            }
        }

    }
    /** the task attribute of the task which should be inspected */
    private TaskAttribute m_attributes;

    @Override
    protected Object executeImpl(ExecutionEvent event) {
        IStructuredSelection selection = getSelection();
        Object o = selection.getFirstElement();
        if (o instanceof ITask) {
            ITask task = (ITask) o;

            IRunnableWithProgress run = new LoadAttributesFromAlm(task);
            try {
                PlatformUI.getWorkbench().getProgressService()
                        .busyCursorWhile(run);
                InspectALMAttributesDialog dialog = 
                        new InspectALMAttributesDialog(getActiveShell(),
                                m_attributes);
                dialog.open();
            } catch (InvocationTargetException | InterruptedException e) {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_ERROR_VIEW_TASK_ATTRIBUTES, null, 
                        new String[]{e.getMessage()}); 
            }

        }
        return null;
    }

}

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
package org.eclipse.jubula.mylyn.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.mylyn.Activator;
import org.eclipse.jubula.mylyn.exceptions.InvalidALMAttributeException;
import org.eclipse.jubula.mylyn.i18n.Messages;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 */
public final class MylynAccess {

    /**
     * @author BREDEX GmbH
     */
    public enum CONNECTOR {
        /** default handling type */
        DEFAULT, 
        /** custom handling type */
        HP_ALM,
        /** tasktop connector */
        TASKTOP;
    }

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(MylynAccess.class);

    /** Constructor */
    private MylynAccess() {
        // hide
    }

    /**
     * @param repoLabel
     *            the label of the repository
     * @return the task repository or <code>null</code> if not found
     */
    public static TaskRepository getRepositoryByLabel(String repoLabel) {
        List<TaskRepository> allRepositories = getAllRepositories();

        for (TaskRepository repo : allRepositories) {
            if (repo.getRepositoryLabel().equals(repoLabel)) {
                return repo;
            }
        }
        return null;
    }

    /**
     * @return a list of all available task repositories
     */
    public static List<TaskRepository> getAllRepositories() {
        IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
        return repositoryManager.getAllRepositories();
    }

    /**
     * @param repo
     *            the task repository
     * @param taskId
     *            the taskId
     * @param monitor
     *            the monitor to use
     * @return the task or <code>null</code> if not found
     * @throws CoreException
     *             in case of a problem
     */
    public static ITask getTaskByID(TaskRepository repo, String taskId,
            IProgressMonitor monitor) throws CoreException {
        ITask task = null;
        if (validRepository(repo)) {
            IRepositoryModel repositoryModel = TasksUi.getRepositoryModel();
            task = repositoryModel.getTask(repo, taskId);
            if (task == null) {
                task = repositoryModel.createTask(repo, taskId);
            }

        }
        return task;
    }

    /**
     * @param repo
     *            the repository to check
     * @return if the repository is valid
     */
    private static boolean validRepository(TaskRepository repo) {
        return repo != null && !repo.isOffline();
    }

    /**
     * @param repo
     *            the task repository
     * @param taskId
     *            the taskId
     * @param monitor
     *            the monitor to use
     * @return the tasks data or <code>null</code> if not found
     * @throws CoreException
     *             in case of a problem
     */
    public static TaskData getTaskDataByID(TaskRepository repo, String taskId,
            IProgressMonitor monitor) throws CoreException {
        TaskData taskData = null;
        
        
        if (validRepository(repo)) {
            AbstractRepositoryConnector connector = TasksUi
                    .getRepositoryConnector(repo.getConnectorKind());

            if (connector.getConnectorKind().toLowerCase()
                    .contains(CONNECTOR.TASKTOP.name().toLowerCase())) {
                // In Tasktop connector using dash in id is not allowed, so need 
                // to search by task key
                if (connector.supportsSearchByTaskKey(repo)) {
                    // Fetch partial data to get the task id
                    TaskData partialTaskData = connector.searchByTaskKey(repo,
                            taskId, monitor);
                    if (partialTaskData != null) {
                        // need to fetch the full task data to modify
                        taskData = connector.getTaskData(repo,
                                partialTaskData.getTaskId(), monitor);
                    }
                }
            } else {
                taskData = connector.getTaskData(repo, taskId, monitor);
            }
        }
        return taskData;
    }

    /**
     * @param repoLabel
     *            the repository to test the connection for
     * @return a status reflecting the current connection state
     */
    public static IStatus testConnection(String repoLabel) {
        TaskRepository repository = getRepositoryByLabel(repoLabel);
        if (repository == null) {
            return new Status(IStatus.ERROR, Activator.ID, NLS.bind(
                    Messages.TaskRepositoryNotFound, repoLabel));
        }
        if (repository.isOffline()) {
            return new Status(IStatus.ERROR, Activator.ID, NLS.bind(
                    Messages.TaskRepositoryOffline, repoLabel));
        }
        
        boolean savePassword = repository
                .getSavePassword(AuthenticationType.REPOSITORY);
        if (!savePassword) {
            return new Status(IStatus.ERROR, Activator.ID, NLS.bind(
                    Messages.TaskRepositoryNoCredentialsStored, repoLabel));
        }
        
        AbstractRepositoryConnector connector = TasksUi
                .getRepositoryConnector(repository.getConnectorKind());
        if (connector == null) {
            return new Status(IStatus.ERROR, Activator.ID, NLS.bind(
                    Messages.TaskRepositoryNoConnectorFound, repoLabel));
        }
        
        try {
            connector.updateRepositoryConfiguration(repository,
                    new NullProgressMonitor());
        } catch (CoreException e) {
            return new Status(IStatus.ERROR, Activator.ID,
                    e.getLocalizedMessage().replace("\n\n", " ")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        IStatus repoStatus = repository.getStatus();
        if (repoStatus != null) {
            return repoStatus;
        }
        return Status.OK_STATUS;
    }
    
    /**
     * Updates fields in ALM system
     * @param repo the task repository to perform the update in
     * @param taskId task id
     * @param attributeUpdates list of field updates
     * @param monitor monitor
     * @return a list of status information per performed attribute update
     */
    public static IStatus updateAttributes(TaskRepository repo,
            String taskId, List<Map<String, String>> attributeUpdates,
            IProgressMonitor monitor) {
        MultiStatus updateInfo = new MultiStatus(Activator.ID,
                IStatus.INFO, "Task update is about to start...", null); //$NON-NLS-1$
        try {
            TaskData taskData = getTaskDataByID(repo, taskId, monitor);
            if (taskData != null) {
                ITask task = getTaskByID(repo, taskData.getTaskId(),
                        monitor);
                if (task != null) {
                    String connectorKind = repo.getConnectorKind();
                    AbstractRepositoryConnector connector = TasksUi
                            .getRepositoryConnector(connectorKind);
                    AbstractTaskDataHandler taskDataHandler = connector
                            .getTaskDataHandler();
                    ITaskDataManager taskDataManager = 
                            TasksUi.getTaskDataManager();
                    ITaskDataWorkingCopy taskWorkingCopy = taskDataManager
                            .createWorkingCopy(task, taskData);
                    TaskDataModel taskModel = new TaskDataModel(repo, task,
                            taskWorkingCopy);
                    TaskAttribute rootData = taskModel.getTaskData()
                            .getRoot();
                    for (Map<String, String> udpate : attributeUpdates) {
                        List<TaskAttribute> changes = attributeUpdateHandling(
                                udpate, rootData);
                        if (changes.isEmpty()) {
                            updateInfo.add(new Status(IStatus.INFO,
                                    Activator.ID, "No changes for Task.")); //$NON-NLS-1$
                            continue;
                        }
                        for (TaskAttribute change : changes) {
                            taskModel.attributeChanged(change);
                        }
                        RepositoryResponse response = taskDataHandler
                            .postTaskData(
                                taskModel.getTaskRepository(), 
                                taskModel.getTaskData(),
                                taskModel.getChangedOldAttributes(), monitor);
                        if (response != null
                                && RepositoryResponse.ResponseKind.TASK_UPDATED
                                .equals(response.getReposonseKind())) {
                            updateInfo.add(new Status(IStatus.OK,
                                    Activator.ID, "Task has been updated.")); //$NON-NLS-1$
                        } else {
                            updateInfo.add(new Status(IStatus.WARNING,
                                    Activator.ID,
                                    "Task might not have been updated successfully.")); //$NON-NLS-1$
                        }
                        if (monitor != null && monitor.isCanceled()) {
                            updateInfo.add(new Status(IStatus.OK,
                                    Activator.ID, "Task update has been cancelled.")); //$NON-NLS-1$
                            break;
                        }
                    }
                }
            } else {
                updateInfo.add(new Status(IStatus.ERROR, Activator.ID,
                        "No task data found in the given repository!")); //$NON-NLS-1$
            }
        } catch (InvalidALMAttributeException e) {
            updateInfo.add(new Status(IStatus.ERROR, Activator.ID,
                    e.getMessage()));
        } catch (CoreException e) {
            updateInfo.add(new Status(IStatus.ERROR, Activator.ID,
                    e.getLocalizedMessage()));
        } catch (IllegalArgumentException e) {
            LOG.error(e.getLocalizedMessage(), e);
            // This is necessary due to an IllegalArgumentException which might be
            // thrown in the TaskDataHandler
            updateInfo.add(new Status(IStatus.ERROR, Activator.ID,
                    e.getLocalizedMessage(), e));
        }
        return updateInfo;
    }

    /**
     * Creates list of task attributes to change for default repository type
     * @param attributesToChange the attributes to update
     * @param rootAttr root attribute
     * @return list of task attributes to change
     * @throws InvalidALMAttributeException 
     */
    private static List<TaskAttribute> attributeUpdateHandling(
            Map<String, String> attributesToChange, TaskAttribute rootAttr)
        throws InvalidALMAttributeException {
        List<TaskAttribute> changes = new ArrayList<TaskAttribute>();

        for (String key : attributesToChange.keySet()) {
            if (StringUtils.isBlank(key)) {
                throw new InvalidALMAttributeException(
                        Messages.BlankAttributeID);
            }
            TaskAttribute attributeUpdate = rootAttr.getAttribute(key);
            if (attributeUpdate == null) {
                throw new InvalidALMAttributeException(NLS.bind(
                        Messages.InvalidAttributeID, key));
            }
            if (attributeUpdate.getMetaData().isReadOnly()) {
                throw new InvalidALMAttributeException(NLS.bind(
                        Messages.ReadOnlyAttributeID, key));
            }
            String value = attributesToChange.get(key);
            Map<String, String> options = attributeUpdate.getOptions();
            if (options != null && !options.isEmpty()) {
                if (!options.containsKey(value)) {
                    throw new InvalidALMAttributeException(NLS.bind(
                            Messages.InvalidValue, value, key));
                }
            }
            attributeUpdate.setValue(value);

            changes.add(attributeUpdate);
        }
        return changes;
    }
}
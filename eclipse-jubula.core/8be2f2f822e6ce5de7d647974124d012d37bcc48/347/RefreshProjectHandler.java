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
package org.eclipse.jubula.client.ui.rcp.handlers.project;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.core.businessprocess.progress.ProgressMonitorTracker;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.handlers.project.AbstractProjectHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 01.06.2006
 */
public class RefreshProjectHandler extends AbstractProjectHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        return refreshProject();
    }
    
    /**
     * @author BREDEX GmbH
     * @created 25.01.2008
     */
    public static class RefreshProjectOperation 
            implements IRunnableWithProgress {

        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) {

            int totalWork = getTotalWork();

            monitor.beginTask(
                    Messages.RefreshProjectOperationRefreshing,
                    totalWork);

            ProgressMonitorTracker instance = ProgressMonitorTracker.SINGLETON;
            instance.setProgressMonitor(monitor);
            Plugin.clearAllEditorsClipboard();
            try {
                GeneralStorage.getInstance().reloadMasterSession(
                        monitor);
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleProjectDeletedException();
            } finally {
                instance.setProgressMonitor(null);
                monitor.done();
            }
        }
        
        /**
         * @return the amount of work required to complete the operation. This
         *         value can then be used when creating a progress monitor.
         */
        private int getTotalWork() {
            int totalWork = 0;
            EntityManager masterSession = 
                GeneralStorage.getInstance().getMasterSession();
            IProjectPO currentProject = 
                GeneralStorage.getInstance().getProject();
            long currentProjectId = currentProject.getId();
            
            // (node=1)
            totalWork += NodePM.getNumNodes(currentProjectId, 
                    masterSession);

            // (tdMan=1)
            totalWork += NodePM.getNumTestDataManagers(
                    currentProjectId, 
                    masterSession);
            
            // (execTC=1 [each corresponding specTC needs to be fetched])
            totalWork += NodePM.getNumExecTestCases(
                    currentProjectId, 
                    masterSession);
            
            for (IReusedProjectPO reused 
                    : currentProject.getUsedProjects()) {
                
                try {
                    IProjectPO reusedProject = 
                        ProjectPM.loadReusedProject(reused);
                    if (reusedProject != null) {
                        long reusedId = reusedProject.getId();
                        
                        // (node=1)
                        totalWork += NodePM.getNumNodes(reusedId, 
                                masterSession);

                        // (tdMan=1)
                        totalWork += NodePM.getNumTestDataManagers(
                                reusedId, 
                                masterSession);

                        // (execTC=1 [each corresponding specTC needs to be fetched])
                        totalWork += NodePM.getNumExecTestCases(
                                reusedId, 
                                masterSession);
                    }
                } catch (JBException e) {
                    // Do nothing
                }
            }
            return totalWork;
        }

    }

    /**
     * Refreshes the currently open Project.
     * 
     * @return the result of the operation.
     */
    public IStatus refreshProject() {
        Plugin.startLongRunning(Messages
                .RefreshTSBrowserActionProgressMessage);
        try {
            PlatformUI.getWorkbench().getProgressService().run(true, false,
                    new RefreshProjectOperation());
        } catch (InvocationTargetException e) {
            // Already handled within the operation.
            return new Status(IStatus.ERROR, 
                    Plugin.PLUGIN_ID, e.getLocalizedMessage());
        } catch (InterruptedException e) {
            // Operation canceled.
            return Status.CANCEL_STATUS;
        } finally {
            Plugin.stopLongRunning();
            DataEventDispatcher.getInstance().fireProjectStateChanged(
                    ProjectState.opened);
        }
        
        return Status.OK_STATUS;
    }
}
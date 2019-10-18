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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.businessprocess.compcheck.CompletenessGuard;
import org.eclipse.jubula.client.core.businessprocess.compcheck.ProblemPropagator.ProblemCleanupOperation;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.rules.SingleJobRule;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 12.03.2007
 */
public class CompletenessBP implements IProjectStateListener {
    /** for log messages */
    private static Logger log = LoggerFactory.getLogger(CompletenessBP.class);
    
    /** this instance */
    private static CompletenessBP instance; 

    /**
     * private constructor
     */
    private CompletenessBP() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addProjectStateListener(this);
        ICommandService commandService = PlatformUI.getWorkbench().getService(
                ICommandService.class);

        IExecutionListener saveListener = new IExecutionListener() {
            /** {@inheritDoc} */
            public void preExecute(String commandId, ExecutionEvent event) {
                // empty is ok
            }
            /** {@inheritDoc} */
            public void postExecuteSuccess(String commandId, 
                    Object returnValue) {
                if (isInteresting(commandId)) {
                    completeProjectCheck();
                }
            }
            /** {@inheritDoc} */
            public void postExecuteFailure(String commandId,
                    ExecutionException exception) {
                if (isInteresting(commandId)) {
                    completeProjectCheck();
                }
            }
            /** {@inheritDoc} */
            public void notHandled(String commandId, 
                NotHandledException exception) {
                // empty is ok
            }
            
            /** whether the corresponding command is "interesting" */
            private boolean isInteresting(String commandId) {
                boolean isInteresting = false;
                if (IWorkbenchCommandConstants.FILE_SAVE.equals(commandId)
                        || IWorkbenchCommandConstants.FILE_SAVE_ALL
                                .equals(commandId)) {
                    isInteresting = true;
                }
                return isInteresting;
            }
        };
        commandService.addExecutionListener(saveListener);
    }

    /**
     * @return the ComponentNamesList
     */
    public static CompletenessBP getInstance() {
        if (instance == null) {
            instance = new CompletenessBP();
        }
        return instance;
    }

    /** {@inheritDoc} */
    public void handleProjectStateChanged(ProjectState state) {
        if (ProjectState.opened.equals(state)) {
            completeProjectCheck(); 
        }
    }

    /**
     * checks the project regardless of user preferences
     */
    public void completeProjectCheck() {
        final INodePO root = GeneralStorage.getInstance().getProject();
        if (root != null) {
            try {
                // Temporarily disable completenessCheckDecorator to prevent
                // a ConcurrentModificationException while checking the project
                IWorkbench workbench = PlatformUI.getWorkbench();
                workbench.getDecoratorManager().setEnabled(
                        Constants.CC_DECORATOR_ID, false);
                Job cc = new UICompletenessCheckOperation("Completeness Check"); //$NON-NLS-1$
                cc.setRule(SingleJobRule.COMPLETENESSRULE);
                JobUtils.executeJob(cc, null);
                for (Job job : Job.getJobManager().find(cc)) {
                    if (job != cc) {
                        job.cancel();
                    }
                }
            } catch (CoreException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }
    
    /**
     * @author Markus Tiede
     * @created 07.11.2011
     */
    public static class UICompletenessCheckOperation extends Job {

        /**
         * @param name the name of the job
         */
        public UICompletenessCheckOperation(String name) {
            super(name);
        }

        /** {@inheritDoc} */
        public boolean belongsTo(Object family) {
            if (family instanceof UICompletenessCheckOperation) {
                return true;
            }
            return super.belongsTo(family);
        }
        
        /** {@inheritDoc} */
        public IStatus run(IProgressMonitor monitor) {
            DataEventDispatcher ded = DataEventDispatcher.getInstance();
            
            monitor.beginTask(Messages.CompletenessCheckRunningOperation,
                    IProgressMonitor.UNKNOWN);

            int status = IStatus.OK;
            try {
                final IProjectPO project = GeneralStorage.getInstance()
                        .getProject();
                
                TreeTraverser treeTraverser = new TreeTraverser(project,
                        new ProblemCleanupOperation(), true, true);
                treeTraverser.setMonitor(monitor);
                treeTraverser.traverse(true);
                
                ded.fireCompletenessCheckStarted();
                CompletenessGuard.checkAll(project, monitor);
            } finally {
                if (monitor.isCanceled()) {
                    status = IStatus.CANCEL;
                } else {
                    ded.fireCompletenessCheckFinished();
                }
                monitor.done();
            }
            return new Status(status, Activator.PLUGIN_ID, getName());
        }
    }
}
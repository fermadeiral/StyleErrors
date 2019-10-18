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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.archive.businessprocess.ProjectBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.VersionDialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;


/**
 * @author BREDEX GmbH
 * @created Jun 29, 2007
 */
@SuppressWarnings("synthetic-access")
public class CreateNewProjectVersionHandler extends AbstractHandler {
    /**
     * call this if the "save as" has ended to update the GUI.
     */
    private void fireReady() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.fireProjectLoadedListener(new NullProgressMonitor());
        ded.fireProjectStateChanged(ProjectState.opened);
    }

    /**
     * @param projectVersion the new version 
     * @return new WorkerThread
     */
    private IRunnableWithProgress createOperation(
        ProjectVersion projectVersion) {
        
        return new ProjectBP.NewVersionOperation(
            GeneralStorage.getInstance().getProject(),
            projectVersion);
    }

    /**
     * Opens the dialog to change the project name
     * @return the dialog, or <code>null</code> if an error prevents the dialog 
     *         from opening
     */
    private VersionDialog openVersionDialog() {
        ProjectVersion actualVersion = new ProjectVersion(1, 0, null);
        try {
            GeneralStorage.getInstance().validateProjectExists(
                    GeneralStorage.getInstance().getProject());
            actualVersion = GeneralStorage.getInstance()
                    .getProject().getProjectVersion();
        } catch (ProjectDeletedException e) {
            PMExceptionHandler
                .handleProjectDeletedException();
            return null;
        }

        VersionDialog dialog = new VersionDialog(
            getActiveShell(),
            Messages.CreateNewProjectVersionActionTitle,
            actualVersion,
            Messages.CreateNewProjectVersionActionMessage,
            IconConstants.BIG_PROJECT_STRING, 
            Messages.CreateNewProjectVersionActionShellTitle) { 

            /**
             * {@inheritDoc}
             */
            protected boolean isInputAllowed() {
                ProjectVersion version = getFieldVersion();
                if (ProjectPM.doesProjectVersionExist(
                    GeneralStorage.getInstance().getProject().getGuid(),
                    version.getMajorNumber(), 
                    version.getMinorNumber(),
                    version.getMicroNumber(),
                    version.getVersionQualifier())) {
                    setErrorMessage(Messages.
                            CreateNewProjectVersionActionDoubleVersion);
                    return false;
                }
                return true;
            }

            /**
             * {@inheritDoc}
             */
            protected void okPressed() {
                ProjectVersion version = getFieldVersion();
                if (ProjectPM.doesProjectVersionExist(
                    GeneralStorage.getInstance().getProject().getGuid(),
                    version.getMajorNumber(), 
                    version.getMinorNumber(),
                    version.getMicroNumber(),
                    version.getVersionQualifier())) {
                    
                    ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_PROJECTVERSION_ALREADY_EXISTS, 
                        new Object[]{
                            getProjectVersion()}, 
                        null);
                    return;
                }
                super.okPressed();
            }
        };
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        Plugin.getHelpSystem().setHelp(dialog.getShell(), 
            ContextHelpIds.DIALOG_PROJECT_CREATENEWVERSION);
        dialog.open();
        return dialog;
    }


    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        Plugin.startLongRunning(Messages.SaveProjectAsActionWaitWhileSaving);
        VersionDialog dialog = openVersionDialog();
        if (dialog != null && dialog.getReturnCode() == Window.OK) {
            ProjectVersion version = dialog.getProjectVersion();
            IRunnableWithProgress op = createOperation(version);
            try {
                IProgressService progressService = 
                    PlatformUI.getWorkbench().getProgressService();
                progressService.busyCursorWhile(op);
                fireReady();
                
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof PMSaveException) {
                    PMExceptionHandler.handlePMExceptionForMasterSession(
                            new PMSaveException(targetException.getMessage(), 
                                    MessageIDs.E_CREATE_NEW_VERSION_FAILED));
                } else if (targetException instanceof PMException) {
                    PMExceptionHandler.handlePMExceptionForMasterSession(
                            (PMException) targetException);
                } else if (targetException instanceof ProjectDeletedException) {
                    PMExceptionHandler.handleProjectDeletedException();
                }
            } catch (InterruptedException e) {
                // Operation was canceled.
                // We have to clear the GUI because all of 
                // the save work was done in the Master Session, which has been 
                // rolled back.
                Utils.clearClient();
            }
        } else {
            Plugin.stopLongRunning();
        }
        return null;
    }
}

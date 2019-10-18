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

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.archive.businessprocess.FileStorageBP;
import org.eclipse.jubula.client.archive.errorhandling.IProjectNameConflictResolver;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.progress.AbstractRunnableWithProgress;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.client.core.utils.DatabaseStateDispatcher;
import org.eclipse.jubula.client.core.utils.DatabaseStateEvent;
import org.eclipse.jubula.client.core.utils.IDatabaseStateListener;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.ComboBoxDialog;
import org.eclipse.jubula.client.ui.rcp.handlers.project.OpenProjectHandler;
import org.eclipse.jubula.client.ui.rcp.handlers.project.OpenProjectHandler.OpenProjectOperation;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 08.11.2004
 */
public class ImportFileBP implements IProjectNameConflictResolver,
        IDatabaseStateListener {
    /**
     * Interface for classes wishing to provide information for an import 
     * operation (i.e. which projects to import and which parts to import).
     *
     * @author BREDEX GmbH
     * @created May 20, 2010
     */
    public static interface IProjectImportInfoProvider {
        
        /**
         * @return the file names of the projects to import
         */
        public List<URL> getFileURLs();
        
        /**
         * @return the selection state of the open project checkbox
         */
        public boolean getIsOpenProject();

    }
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(ImportFileBP.class);
    
    /** single instance of ImExportCvsBP */
    private static ImportFileBP instance = null;

    /** Constructor */
    public ImportFileBP() {
        FileStorageBP.setProjectNameConflictResolver(this);
        DatabaseStateDispatcher.addDatabaseStateListener(this);
    }

    /**
     * get single instance
     * 
     * @return single instance of ImportFileBP
     */
    public static ImportFileBP getInstance() {
        if (instance == null) {
            instance = new ImportFileBP();
        }
        return instance;
    }     

    /**
     * Imports a chosen project from a file.
     * @param fileURLs
     *            The URLs of the files to import.
     * @param openProject
     *            Flag indicating whether the imported project should be 
     *            immediately opened after import.
     */
    public void importProject(
            final List<URL> fileURLs, 
            final boolean openProject) {
        try {
            if (fileURLs == null) {
                return;
            }
            AbstractRunnableWithProgress<IProjectPO> importProjectRunnable =
                new AbstractRunnableWithProgress<IProjectPO>() {

                    public void run(IProgressMonitor monitor)
                        throws InterruptedException {
                        monitor.setTaskName(Messages.
                                ImportFileBPWaitWhileImporting);
                        setResult(FileStorageBP.importProject(
                            fileURLs, monitor, 
                            Plugin.getDefault(), openProject));
                        Persistor.instance().updateDbStatistics();
                        ProjectPM.clearCaches();
                    }
                
                };
            PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
                    importProjectRunnable);
            final IProjectPO projectToOpen = importProjectRunnable.getResult();
            if (projectToOpen != null) { 
                OpenProjectOperation openOp = 
                    new OpenProjectHandler.OpenProjectOperation(
                            projectToOpen);
                try {
                    PlatformUI.getWorkbench().getProgressService()
                        .busyCursorWhile(openOp);
                } catch (InvocationTargetException ite) {
                    // Exception occurred during operation
                    log.error(Messages.ErrorOccurredDuringImport, 
                            ite.getCause());
                    openOp.handleOperationException();
                    ErrorHandlingUtil.createMessageDialogException(
                            ite.getCause());
                } catch (InterruptedException e) {
                    // Operation was canceled.
                    openOp.handleOperationException();
                }
            }
        } catch (InvocationTargetException ite) {
            // Exception occurred during operation
            log.error(Messages.ErrorOccurredDuringImport, ite.getCause());
            ErrorHandlingUtil.createMessageDialogException(ite.getCause());
        } catch (InterruptedException e) {
            // Operation was canceled.
            showCancelImport(Plugin.getDefault());
        } finally {
            Plugin.stopLongRunning();
        }
    }

    /**
     * Performs an import using the information provided by the argument.
     * 
     * @param importInfo Provides information relevant to the import.
     * @param monitor The progress monitor for the operation.
     * 
     * @throws InterruptedException if the operation was canceled or the thread
     *                              was interrupted.
     */
    public void importProjects(IProjectImportInfoProvider importInfo, 
            IProgressMonitor monitor) throws InterruptedException {
        List<URL> fileURLs = importInfo.getFileURLs();
        boolean openProject = importInfo.getIsOpenProject();

        FileStorageBP.importProject(fileURLs, monitor, Plugin.getDefault(), 
                openProject);
    }
    
    /**
     * 
     * Import the unbound modules library into a database
     * 
     * @author BREDEX GmbH
     * @created 05.06.2008
     */
    private final class ImportUnboundModulesJob extends Job {
        /**
         * @param name
         *            the name of the job
         */
        public ImportUnboundModulesJob(String name) {
            super(name);
        }

        /**
         * {@inheritDoc}
         */
        protected IStatus run(IProgressMonitor monitor) {
            Plugin.getDisplay().syncExec(new Runnable() {
                public void run() {
                    Bundle b = Platform.getBundle(Plugin.PLUGIN_ID);
                    Enumeration e = b.findEntries(
                            "resources/library/", "*.jub", false); //$NON-NLS-1$//$NON-NLS-2$
                    List<URL> unboundModuleURLs = new ArrayList<URL>();

                    while (e.hasMoreElements()) {
                        unboundModuleURLs.add((URL)e.nextElement());
                    }
                    // load all elements;
                    importProject(unboundModuleURLs, false);
                }
            });
            return Status.OK_STATUS;
        }
    }
    
    /**
     * @return URL of project template if is exist otherwise null
     */
    public URL getProjectTemplateUrl() {
        Bundle b = Platform.getBundle(Plugin.PLUGIN_ID);
        Enumeration e = b.findEntries(
                "resources/template", "*.jub", false); //$NON-NLS-1$//$NON-NLS-2$
        return e.hasMoreElements() ? (URL)e.nextElement() : null;
    }

    /**
     * {@inheritDoc}
     */
    public String resolveNameConflict(List<String> availableNames) {
        ComboBoxDialog dialog = new ComboBoxDialog(
                Plugin.getShell(), 
                new ArrayList<String>(availableNames), 
                Messages.ImportFileComboActionProjMessage,
                Messages.ImportFileActionProjTitle,
                IconConstants.IMPORT_PROJECT,
                Messages.ImportFileActionProjShell,
                Messages.ImportFileActionProjLabel);
            
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        Plugin.getHelpSystem().setHelp(dialog.getShell(),
                ContextHelpIds.DIALOG_PROJECT_IMPORT_RENAME);
        dialog.open();
        return (dialog.getReturnCode() == Window.OK) ? dialog.getSelection()
                : null;
    }

    /**
     * @param string @see Plugin#writeLineToConsole(String, boolean)
     * @param b @see Plugin#writeLineToConsole(String, boolean)
     */
    protected void writeLineToConsole(String string, boolean b) {
        Plugin.getDefault().writeLineToConsole(string, b);
    }

    /**
     * @param string @see Plugin#writeErrorLineToConsole(String, boolean)
     * @param b @see Plugin#writeErrorLineToConsole(String, boolean)
     */
    protected void writeErrorLineToConsole(String string, boolean b) {
        Plugin.getDefault().writeErrorLineToConsole(string, b);
    }

    /**
     * Report to the user that the import operation was cancelled.
     * 
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     */
    private void showCancelImport(IProgressConsole console) {
        console.writeErrorLine(NLS.bind(
                Messages.ImportFileActionErrorImportFailed,
                Messages.ImportOperationCancelledByUser));
    }

    /**
     * {@inheritDoc}
     */
    public void reactOnDatabaseEvent(DatabaseStateEvent e) {
        switch (e.getState()) {
            case DB_SCHEME_CREATED:
                Job importUnboundModules = new ImportUnboundModulesJob(
                        org.eclipse.jubula.client.archive.i18n
                            .Messages.ImportUnboundModulesJob);
                importUnboundModules.setUser(true);
                importUnboundModules.schedule();
                break;
            default:
                break;
        }
    }
}
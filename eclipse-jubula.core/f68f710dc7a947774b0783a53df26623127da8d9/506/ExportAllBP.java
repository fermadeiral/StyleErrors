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

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.archive.businessprocess.FileStorageBP;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.osgi.util.NLS;


/**
 * @author BREDEX GmbH
 * @created 14.04.2008
 */
public class ExportAllBP {

    /** single instance */
    private static ExportAllBP instance = null;

    /**
     * private constructor
     */
    private ExportAllBP() {
        // Nothing to initialize
    }

    /**
     * @return single instance
     */
    public static ExportAllBP getInstance() {
        if (instance == null) {
            instance = new ExportAllBP();
        }
        return instance;
    }
    
    /**
     * Exports all projects from projectList to the export directory.
     * The currently active project is written in a different directory,
     * if exportCurrentDirName is different from exportOtherDirName.
     * NOTE: If there is no currently active project, then the parameter
     *       exportCurrentDirName has no effect.
     * @param projectList The list of projects to export
     * @param exportDirName The export directory of the projects
     * @param exportSession The session to be used for Persistence (JPA / EclipseLink)
     * @param monitor The progress monitor
     * @param writeToSystemTempDir Indicates whether the projects have to be 
     *                             written to the system temp directory
     * @param listOfProjectFiles The written project files are added to this 
     *                           list, if the temp dir was used and the list  
     *                           is not null.
     */
    public void exportProjectList(List<IProjectPO> projectList, 
            String exportDirName, EntityManager exportSession, 
            IProgressMonitor monitor, boolean writeToSystemTempDir, 
            List<File> listOfProjectFiles) 
        throws InterruptedException, JBException {

        FileStorageBP.exportProjectList(projectList, exportDirName, 
                exportSession, monitor, writeToSystemTempDir, 
                listOfProjectFiles, Plugin.getDefault());
    }

    /**
     * Report to the user that the export operation was aborted due to an
     * error.
     * 
     * @param gde The error that caused the export operation to abort.
     */
    public void showAbortExport(JBException gde) {
        Plugin.getDefault().writeErrorLineToConsole(
            NLS.bind(Messages.ExportAllBPErrorExportFailed,
                    gde.getMessage()),
            true);
    }

    /**
     * Report to the user that the export operation was cancelled.
     * 
     */
    public void showCancelExport() {
        Plugin.getDefault().writeErrorLineToConsole(NLS.bind(
                Messages.ExportAllBPErrorExportFailed,
                Messages.ImportOperationCancelledByUser),
            true);
    }

    /**
     * Report to the user that all projects have been exported.
     */
    public void showFinishedExport() {
        Plugin.getDefault().writeLineToConsole(
            Messages.ExportAllBPInfoFinishedExport, true);
    }

    /**
     * Report to the user that the export process is beginning.
     * 
     */
    public void showStartingExport() {
        Plugin.getDefault().writeLineToConsole(
            Messages.ExportAllBPInfoStartingExport, true);
    }
}

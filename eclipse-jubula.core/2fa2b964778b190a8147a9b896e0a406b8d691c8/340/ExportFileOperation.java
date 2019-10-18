/*******************************************************************************
 * Copyright (c) 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers.project;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.archive.JsonStorage;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;

/**
 * @author BREDEX GmbH
 * @created Jan 22, 2010
 */
public final class ExportFileOperation implements
        IRunnableWithProgress {

    /** the filename to use for the exported file */
    private final String m_fileName;
    
    /** the console to use to display progress and error messages */
    private IProgressConsole m_console;

    /**
     * @param fileName The filename to use for the exported file.
     * @param console 
     */
    public ExportFileOperation(String fileName, IProgressConsole console) {
        m_fileName = fileName;
        m_console = console;
    }

    /**
     * {@inheritDoc}
     */
    public void run(IProgressMonitor monitor) {
        if (m_fileName != null) {
            try {
                m_console.writeStatus(new Status(IStatus.INFO,
                    Activator.PLUGIN_ID, Messages
                            .RefreshProjectOperationRefreshing));
                GeneralStorage gstorage = GeneralStorage.getInstance();
                gstorage.validateProjectExists(gstorage.getProject());
                final AtomicReference<IStatus> statusOfRefresh =
                    new AtomicReference<IStatus>();
                Plugin.getDisplay().syncExec(new Runnable() {
                    public void run() {
                        RefreshProjectHandler rph = 
                            new RefreshProjectHandler();
                        statusOfRefresh.set((IStatus)rph.executeImpl(null));
                    }
                });
                // Only proceed with the export if the refresh was
                // successful.
                if (statusOfRefresh.get() != null
                        && statusOfRefresh.get().isOK()) {

                    m_console.writeStatus(new Status(IStatus.INFO,
                        Activator.PLUGIN_ID, Messages
                                .ExportFileActionExporting));
                    SubMonitor subMonitor = SubMonitor.convert(monitor,
                            Messages.ExportFileActionExporting, 1);
                    
                    JsonStorage.save(gstorage.getProject(), m_fileName,
                            true, subMonitor.newChild(1), m_console);
                }
            } catch (final PMException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
            } catch (final ProjectDeletedException e) {
                PMExceptionHandler.handleProjectDeletedException();
            } finally {
                monitor.done();
                Plugin.stopLongRunning();
            }
        } else {
            monitor.done();
        }
    }
}

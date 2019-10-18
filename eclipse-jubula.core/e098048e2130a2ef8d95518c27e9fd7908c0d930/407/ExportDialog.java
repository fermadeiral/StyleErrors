/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers.project;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 01.07.2016
 */
public class ExportDialog {

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(ExportDialog.class);
    
    /** Constructor */
    private ExportDialog() {
        // nothing
    }

    /**
     * @param activeShell 
     */
    public static final void showExportDialog(Shell activeShell) {
        final FileDialog fileDialog = new FileDialog(activeShell, 
            SWT.SAVE | SWT.APPLICATION_MODAL);
        fileDialog.setText(Messages.ActionBuilderSaveAs);
        String[] filters = new String[]{StringConstants.STAR
                + ExportProjectHandler.JUB};
        fileDialog.setFilterExtensions(filters);
        fileDialog.setFilterPath(Utils.getLastDirPath());
        
        StringBuilder sb = new StringBuilder(
            GeneralStorage.getInstance().getProject().getDisplayName());
        fileDialog.setFileName(sb.toString());
        String fileNameTemp = fileDialog.open();
        
        if (fileNameTemp == null) { // Cancel pressed
            return;
        }
        String extension = filters[fileDialog.getFilterIndex()]
                .replace(StringConstants.STAR, StringConstants.EMPTY);
        fileNameTemp = fileNameTemp.endsWith(extension)
                ? fileNameTemp : fileNameTemp + extension;
        File file = new File(fileNameTemp);
        if (file.exists()) {
            MessageBox mb = new MessageBox(fileDialog.getParent(),
                    SWT.ICON_WARNING | SWT.YES | SWT.NO);
            mb.setText(Messages.ExportFileActionConfirmOverwriteTitle);
            mb.setMessage(NLS.bind(Messages.ExportFileActionConfirmOverwrite,
                    fileNameTemp));
            if (mb.open() == SWT.NO) {
                return;
            }
        }

        Plugin.startLongRunning(Messages.ExportFileActionWaitWhileExporting);
        
        final String fileName = fileNameTemp;
        Utils.storeLastDirPath(fileDialog.getFilterPath());

        IProgressConsole console = Plugin.getDefault();
        IRunnableWithProgress op = new ExportFileOperation(fileName, console);

        try {
            PlatformUI.getWorkbench().getProgressService().busyCursorWhile(op);
        } catch (InvocationTargetException ite) {
            // Exception occurred during operation
            log.error(ite.getLocalizedMessage(), ite.getCause());
        } catch (InterruptedException ie) {
            // Operation canceled. 
            // Do nothing.
        }
    }
}

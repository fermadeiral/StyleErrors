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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ImportFileBP;
import org.eclipse.jubula.client.ui.rcp.handlers.project.ExportProjectHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * Class used to respond to a user dropping files on the Jubula client
 * @author BREDEX GmbH
 *
 */
public class DropFileOperation {

    /** constructor */
    private DropFileOperation() {
        // empty
    }
    
    /**
     * Asks the user for confirmation for importing JUB files
     * @param files the array of file names
     */
    public static void dropFiles(String[] files) {
        if (Persistor.instance() == null) {
            Object result = CommandHelper
                    .executeCommand(CommandIDs.SELECT_DATABASE_COMMAND_ID);
            if (!Status.OK_STATUS.equals(result)) {
                return;    
            }
        }
        if (!Plugin.getDefault().showSaveEditorDialog(null)) {
            return;
        }
        List<URL> fileURLs = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].toLowerCase().endsWith(ExportProjectHandler.JUB)) {
                try {
                    fileURLs.add(new File(files[i]).toURI().toURL());
                } catch (MalformedURLException e) {
                    // ok, we don't add the file...
                }
            }
        }
        if (fileURLs.isEmpty()) {
            return;
        }
        if (canProceed(fileURLs)) {
            boolean open = fileURLs.size() == 1;
            ImportFileBP.getInstance().importProject(fileURLs, open);
        }
    }
    
    /**
     * Asks the user to confirm the import operation
     * @param files the list of file URLs
     * @return whether the operation can proceed
     */
    private static boolean canProceed(List<URL> files) {
        StringBuilder builder = new StringBuilder();
        builder.append(Messages.ConfirmImportDialogText);
        File f;
        URI uri;
        
        for (URL url : files) {
            builder.append(StringConstants.SPACE);
            builder.append(StringConstants.SPACE);
            try {
                uri = url.toURI();
                builder.append(Paths.get(uri).getFileName().toString());
                f = new File(uri);
                builder.append(StringConstants.SPACE);
                builder.append(StringConstants.LEFT_PARENTHESIS);
                builder.append(f.length());
                builder.append(StringConstants.SPACE);
                builder.append(Messages.Bytes);
                builder.append(StringConstants.RIGHT_PARENTHESIS);
                builder.append(StringConstants.NEWLINE);
            } catch (URISyntaxException e) {
                // nothing, we just don't list this 'file's
            }
        }
        
        MessageDialog dialog = new MessageDialog(null, 
                Messages.ConfirmImportDialogTitle, null, 
            builder.toString(), MessageDialog.QUESTION, new String[] {
                Messages.DialogMessageButton_YES,
                Messages.DialogMessageButton_NO }, 0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog.getReturnCode() == 0;
    }
    
}

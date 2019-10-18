/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.views.logview.LogView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 */

public class SaveLogAsHandler extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    protected Object executeImpl(ExecutionEvent event)
            throws ExecutionException {
        IViewPart view = HandlerUtil.getActiveSite(event).
                getPage().findView(Constants.LOGVIEW_ID);
        if (view instanceof LogView) {
            LogView logView = (LogView)view;
            int maxFileNameLength = 255;
            
            String commandLog = logView.getCommandLog();
                        
            FileDialog saveDialog = new FileDialog(getActiveShell(), SWT.SAVE);
            String fileEnding = ".log"; //$NON-NLS-1$ 
            String fileName = StringUtils.substring("command", 0, //$NON-NLS-1$
                    maxFileNameLength
                    - fileEnding.length()
                    - saveDialog.getFilterPath().length());
            fileName = fileName + fileEnding;
            
            saveDialog.setFileName(fileName);
            saveDialog.setFilterExtensions(new String[] { "*.log" }); //$NON-NLS-1$
            saveDialog.setOverwrite(true);
            String path = saveDialog.open();

            if (path != null) {
                PrintWriter out;
                try {
                    out = new PrintWriter(path);
                    out.write(commandLog);
                    out.close();
                } catch (FileNotFoundException e) {
                    throw new ExecutionException("File not found", e); //$NON-NLS-1$
                }
            }

        }
        return null;
    }

}

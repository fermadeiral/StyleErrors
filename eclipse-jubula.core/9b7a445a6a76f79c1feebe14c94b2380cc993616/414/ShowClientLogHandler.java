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
package org.eclipse.jubula.client.ui.rcp.handlers.open;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ShowClientLogBP;
import org.eclipse.jubula.client.ui.rcp.editors.ClientLogInput;
import org.eclipse.jubula.client.ui.rcp.editors.LogViewer;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class ShowClientLogHandler extends AbstractHandler {

    /** single instance of the ClientLogInput */
    private IEditorInput m_clientLogInput = null;

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        File clientLogFile = ShowClientLogBP.getInstance().getClientLogFile();
        
        if (clientLogFile != null && clientLogFile.canRead()) {
            IWorkbenchPage currentPage = Plugin.getActivePage();
            
            if (currentPage != null) {
                if (m_clientLogInput != null 
                    && currentPage.findEditor(m_clientLogInput) != null) {
                    currentPage.closeEditor(
                        currentPage.findEditor(m_clientLogInput), false);
                }
                
                m_clientLogInput = new ClientLogInput(clientLogFile);

                try {
                    currentPage.openEditor(m_clientLogInput, LogViewer.ID);
                } catch (PartInitException e) {
                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.E_CANNOT_OPEN_EDITOR);
                }
            }
        } else {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.I_NO_CLIENT_LOG_FOUND);
        }
        return null;
    }

}

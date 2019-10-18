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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ShowServerLogBP;
import org.eclipse.jubula.client.ui.rcp.editors.ISimpleEditorInput;
import org.eclipse.jubula.client.ui.rcp.editors.ServerLogInput;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.communication.internal.message.ServerLogResponseMessage;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * 
 * @author BREDEX GmbH
 *
 */
public class ShowServerLogHandler extends AbstractHandler {

    /** single instance of the ServerLogInput */
    private ISimpleEditorInput m_serverLogInput = null;

    /**
     * {@inheritDoc}
     */
    protected Object executeImpl(ExecutionEvent event) {
        ServerLogResponseMessage response = ShowServerLogBP.getInstance()
                .requestServerLog();

        if (response != null) {
            int status = response.getStatus();
            if (status == ServerLogResponseMessage.OK) {
                IWorkbenchPage currentPage = Plugin.getActivePage();
                if (currentPage != null) {
                    if (m_serverLogInput != null
                            && currentPage.findEditor(m_serverLogInput) 
                            != null) {
                        currentPage.closeEditor(
                            currentPage.findEditor(m_serverLogInput), false);
                    }

                    m_serverLogInput = new ServerLogInput(
                            response.getServerLog());
                    try {
                        currentPage.openEditor(m_serverLogInput,
                                        "org.eclipse.jubula.client.ui.rcp.editors.LogViewer"); //$NON-NLS-1$
                    } catch (PartInitException e) {
                        ErrorHandlingUtil.createMessageDialog(
                                MessageIDs.E_CANNOT_OPEN_EDITOR);
                    }
                }
            }
        }
        return null;
    }

}

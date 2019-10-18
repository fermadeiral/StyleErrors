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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionGUIController;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;


/**
 * @author BREDEX GmbH
 * @created 11.05.2005
 */
public class AUTAgentDisconnectHandler extends AbstractHandler {
    /** The eclipse job manager */
    private IJobManager m_jobManager = Job.getJobManager();
    /** The job family String */
    private String m_jobFamily = Messages.ClientCollectingInformation;

    /**
     * Checks whether a monitoring job is running or not.
     * @return true if jobs are running, or false if no monitoring job is running
     */
    private boolean isJobRunning() {
        Job[] jobs = m_jobManager.find(m_jobFamily);
        return jobs.length > 0;

    }
    /**
     * @return a confirm Dialog, if monitoring job is still running.
     */
    private MessageDialog getConfirmDialog() {      
                
        MessageDialog dialog = new MessageDialog(getActiveShell(), 
            Messages.ClientDisconnectFromAutAgentTitle,
                null,
                Messages.ClientDisconnectFromAutAgentMessage,
                MessageDialog.QUESTION, new String[] {
                    Messages.DialogMessageButton_YES,
                    Messages.DialogMessageButton_NO
                }, 0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog;
    }

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        if (isJobRunning()) {
            MessageDialog dialog = getConfirmDialog();
            if (dialog.getReturnCode() != Window.OK) {
                return null;
            }
            m_jobManager.cancel(m_jobFamily);
        }
        TestExecutionGUIController.disconnectFromServer();
        return null;
    }
}
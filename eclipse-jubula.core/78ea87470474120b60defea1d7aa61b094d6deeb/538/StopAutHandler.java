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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionContributor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Handler for "Stop AUT" command.
 *
 * @author BREDEX GmbH
 * @created Feb 11, 2010
 */
public class StopAutHandler extends AbstractHandler {
       
    /** The eclipse job manager */
    private IJobManager m_jobManager = Job.getJobManager();
    /** The job familiy */
    private String m_jobFamily = Messages.ClientCollectingInformation;
 
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) throws ExecutionException {
        ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);
        if (sel instanceof IStructuredSelection) {
            IStructuredSelection structSel = (IStructuredSelection)sel;
            Set<AutIdentifier> autsToStop = new HashSet<AutIdentifier>();
            for (Object selectedObj : structSel.toArray()) {
                if (selectedObj instanceof AutIdentifier) {
                    autsToStop.add((AutIdentifier)selectedObj);
                }
            }
            if (!autsToStop.isEmpty() 
                    && Plugin.getDefault().getPreferenceStore().
                        getBoolean(Constants.ASKSTOPAUT_KEY)) {
                          
                MessageDialog dialog = getConfirmDialog();
                if (dialog.getReturnCode() != Window.OK) {
                    if (isJobRunning()) {
                        m_jobManager.cancel(m_jobFamily);
                    }
                    return null;
                }
            }
            
            for (AutIdentifier autId : autsToStop) {
                TestExecutionContributor.getInstance().stopAUT(autId);
            }
        }
        return null;
    }

    /**
     * @return a confirm Dialog
     */
    private MessageDialog getConfirmDialog() {
        String questionText;
        if (isJobRunning()) {
            questionText = Messages.StopAUTActionQuestionTextIfcollecting;
        } else {
            questionText = Messages.StopAUTActionQuestionText;
        }
        MessageDialog dialog = new MessageDialog(getActiveShell(),
                Messages.StopAUTActionShellTitle,
                null, questionText, MessageDialog.QUESTION, new String[] {
                    Messages.DialogMessageButton_YES,
                    Messages.DialogMessageButton_NO }, 0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog;
    }
    /**
     * Checks whether a monitoring job is running or not.
     * @return true if jobs are running, or false if no monitoring job is running
     */
    private boolean isJobRunning() {
                
        Job[] jobs = m_jobManager.find(m_jobFamily);
        return jobs.length > 0;

    }
}

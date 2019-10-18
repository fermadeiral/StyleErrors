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
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.db.TestJobBP;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * @author BREDEX GmbH
 * @created Mar 16, 2010
 */
public class NewTestJobHandler extends AbstractNewHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        newTestJob(event);
        return null;
    }

    /**
     * create a new test job
     * @param event the event
     */
    private void newTestJob(ExecutionEvent event) {
        InputDialog dialog = newTestJobPopUp();
        if (dialog.getReturnCode() == Window.CANCEL) {
            return;
        }
        
        if (Window.OK == dialog.getReturnCode()) {
            addCreatedNode(NodeMaker.createTestJobPO(dialog.getName()),
                event);            
        }
    }

    /**
     * @return a test job popup dialog
     */
    private InputDialog newTestJobPopUp() {
        final IProjectPO project = GeneralStorage.getInstance().getProject();
        int testJobCount = TestJobBP.getListOfTestJobs(project).size();
        String str = StringConstants.EMPTY;
        if (testJobCount > 0) {
            str = str + testJobCount;
        }
        str = InitialValueConstants.DEFAULT_TEST_JOB_NAME + str;
        InputDialog dialog = new InputDialog(getActiveShell(), 
                Messages.NewTestJobTJTitle,
                str, Messages.NewTestJobTJMessage,
                Messages.NewTestJobTJLabel,
                Messages.NewTestJobTJError,
                Messages.NewTestJobDoubleTJName,
                IconConstants.NEW_TJ_DIALOG_STRING, 
                Messages.NewTestJobTJShell,
                false) {
            protected boolean isInputAllowed() {
                String newName = getInputFieldText();
                return !ProjectPM.doesTestJobExists(project.getId(), newName);
            }
        };
        // set help link
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        // set help id
        Plugin.getHelpSystem().setHelp(dialog.getShell(),
                ContextHelpIds.DIALOG_TJ_NEW);
        dialog.open();
        return dialog;
    }

}

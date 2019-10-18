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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.views.TestresultSummaryView;
import org.eclipse.ui.IWorkbenchPart;


/**
 * handler for deleting testresults in testresult summary view
 *
 * @author BREDEX GmbH
 * @created Mar 12, 2010
 */
public class DeleteTestresultsHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = Plugin.getActivePart();
        if (activePart instanceof TestresultSummaryView) {
            TestresultSummaryView summary = (TestresultSummaryView)activePart;
            int returnCode = showDeleteTestresultsDialog();
            if (returnCode == Window.OK) {
                summary.deleteTestresults(summary.getSelectedTestrunIds());
            }
        }

        return null;
    }
    
    /**
     * Shows information dialog, that selected testresults will be deleted
     * @return returnCode of Dialog
     */
    private int showDeleteTestresultsDialog() {
        MessageDialog dialog = new MessageDialog(getActiveShell(), 
            Messages.TestresultSummaryDeleteTestrunDialogTitle,
                null,
                Messages.TestresultSummaryDeleteTestrunDialogMessage,
                MessageDialog.QUESTION, new String[] {
                    Messages.DialogMessageButton_YES,
                    Messages.DialogMessageButton_NO }, 0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog.getReturnCode();
    }

}

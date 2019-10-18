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
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
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
 * @created 03.07.2009
 */
public class NewTestSuiteHandler extends AbstractNewHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        newTestSuite(event);
        return null;
    }

    /**
     * Creates a new TestSuite.
     * @param event the execution event
     */
    public void newTestSuite(ExecutionEvent event) {
        InputDialog dialog = newTestSuitePopUp();
        if (dialog.getReturnCode() == Window.CANCEL) {
            return;
        }
        
        if (Window.OK == dialog.getReturnCode()) {
            ITestSuitePO suite = NodeMaker.createTestSuitePO(dialog
                    .getName());
            setDefaultValuesToTestSuite(suite);
            addCreatedNode(suite, event);
        }
    }

    /**
     * Sets a default AUT and a default AUTConfig to the new TestSuite (when
     * only one AUT / AUTConfig is defined in the Project.
     * 
     * @param testSuite
     *            the new test suite.
     */
    private void setDefaultValuesToTestSuite(ITestSuitePO testSuite) {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        // set default AUTMainPO to testSuite
        int autListSize = project.getAutMainList().size();
        if (autListSize == 0 || autListSize > 1) {
            return;
        }
        IAUTMainPO aut = (IAUTMainPO)(project.getAutMainList().toArray())[0];
        testSuite.setAut(aut);

        // set default AUTConfigPO to testSuite
        int autConfigListLength = aut.getAutConfigSet().size();
        if (autConfigListLength == 0 || autConfigListLength > 1) {
            return;
        }
    }

    /**
     * Opens the dialog for creating a new TestSuite.
     * 
     * @return the dialog.
     */
    private InputDialog newTestSuitePopUp() {
        final IProjectPO project = GeneralStorage.getInstance().getProject();
        int testSuiteCount = TestSuiteBP.getListOfTestSuites(project).size();
        String str = StringConstants.EMPTY;
        if (testSuiteCount > 0) {
            str = str + testSuiteCount;
        }
        str = InitialValueConstants.DEFAULT_TEST_SUITE_NAME + str;
        InputDialog dialog = new InputDialog(getActiveShell(), 
                Messages.NewTestSuiteActionTSTitle,
                str, Messages.NewTestSuiteActionTSMessage,
                Messages.NewTestSuiteActionTSLabel,
                Messages.NewTestSuiteActionTSError,
                Messages.NewTestSuiteActionDoubleTSName,
                IconConstants.NEW_TS_DIALOG_STRING, 
                Messages.NewTestSuiteActionTSShell,
                false) {
            protected boolean isInputAllowed() {
                String newName = getInputFieldText();
                return !ProjectPM.doesTestSuiteExists(project.getId(), newName);
            }
        };
        // set help link
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        // set help id
        Plugin.getHelpSystem().setHelp(dialog.getShell(),
                ContextHelpIds.DIALOG_TS_NEW);
        dialog.open();
        return dialog;
    }
}

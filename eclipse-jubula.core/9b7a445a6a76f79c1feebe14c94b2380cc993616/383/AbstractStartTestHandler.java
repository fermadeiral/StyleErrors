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

import java.net.URL;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.State;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.IClientTest;
import org.eclipse.jubula.client.core.businessprocess.ITestExecutionEventListener;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.TestResultBP;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.CompletenessBP;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;


/**
 * @author BREDEX GmbH
 * @created Mar 22, 2010
 */
public abstract class AbstractStartTestHandler extends AbstractHandler {

    /**
     * run before test execution
     * 
     * @return true if preparation has been successful, false otherwise
     */
    public static boolean prepareTestExecution() {
        final IClientTest clientTest = ClientTest.instance();
        final IPreferenceStore preferenceStore = Plugin.getDefault()
            .getPreferenceStore();
        if (preferenceStore.getBoolean(Constants.GENERATEREPORT_KEY)) {
            URL xslUrl = TestResultBP.getInstance().getXslFileURL();

            if (xslUrl == null) {
                Plugin.getDefault().handleError(
                    new JBException(Messages.FileNotFoundFormatXsl,
                        MessageIDs.E_FILE_NOT_FOUND));
                return false;
            }
            clientTest.setGenerateMonitoringReport(preferenceStore
                    .getBoolean(Constants.GENERATE_MONITORING_REPORT_KEY));
            clientTest.setLogPath(preferenceStore
                .getString(Constants.RESULTPATH_KEY));
            clientTest.setLogStyle(preferenceStore
                .getString(Constants.REPORTGENERATORSTYLE_KEY));
        } else {
            clientTest.setLogPath(null);
            clientTest.setLogStyle(null);
        }
        return true;
    }

    /**
     * init the GUI test execution part
     * 
     * @param event
     *            the execution event
     * 
     * @return whether initialization has been successful
     */
    protected boolean initTestExecution(ExecutionEvent event) {
        return initPauseTestExecutionState(event);
    }

    /**
     * @param event
     *            the execution event
     * @return true if init has been successful
     */
    private boolean initPauseTestExecutionState(ExecutionEvent event) {
        ICommandService cmdService = HandlerUtil
            .getActiveWorkbenchWindow(event).getService(
                ICommandService.class);
        if (cmdService != null) {
            final Command command = cmdService
                    .getCommand(RCPCommandIDs.PAUSE_TEST_SUITE);
            if (command != null) {
                final Display display = Plugin.getDisplay();
                ITestExecutionEventListener l = new 
                    ITestExecutionEventListener() {
                    public void endTestExecution() {
                        display.syncExec(new Runnable() {
                            public void run() {
                                State state = command
                                        .getState(RegistryToggleState.STATE_ID);
                                state.setValue(false);
                            }
                        });
                        ClientTest.instance()
                                .removeTestExecutionEventListener(this);
                    }

                    public void stateChanged(final TestExecutionEvent tee) {
                        display.syncExec(new Runnable() {
                            public void run() {
                                State state = command
                                        .getState(RegistryToggleState.STATE_ID);
                                boolean newToggleStateValue = 
                                        tee.getState() 
                                         == org.eclipse.jubula.client.core
                                         .businessprocess.TestExecutionEvent
                                         .State.TEST_EXEC_PAUSED;
                                state.setValue(newToggleStateValue);
                            }
                        });
                    }

                    @Override
                    public void receiveExecutionNotification(
                            String notification) {
                        // empty
                    }
                };
                ClientTest.instance()
                        .addTestExecutionEventListener(l);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether there are dirty editors.
     *      If yes, the user is notified and can save them.
     *      Once saved, the user has to restart test execution manually.
     * @return whether there were dirty editors
     */
    boolean canStartTestExecution() {
        if (!Plugin.getDefault().anyDirtyStar()) {
            return true;
        }
        // we ask the user to save the editors, and then
        // restart the test manually
        Plugin.getDefault().showSaveEditorDialogWithMessage(
                Messages.DirtyEditorBeforeTestExec, getActiveShell());
        // CC is not automatically started in this case...
        CompletenessBP.getInstance().completeProjectCheck();
        return false;
    }

}

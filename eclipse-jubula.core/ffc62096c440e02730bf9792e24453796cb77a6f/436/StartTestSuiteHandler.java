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

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ChooseTestSuiteBP;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ChooseTestSuiteBP.TestSuiteState;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;


/**
 * @author BREDEX GmbH
 * @created Feb 1, 2010
 */
public class StartTestSuiteHandler extends AbstractStartTestHandler 
        implements IElementUpdater {

    /** ID of command parameter for Test Suite to start */
    public static final String TEST_SUITE_TO_START = 
        "org.eclipse.jubula.client.ui.rcp.commands.StartTestSuiteCommand.parameter.testSuiteToStart"; //$NON-NLS-1$
    
    /** ID of command parameter for Running AUT to test */
    public static final String RUNNING_AUT = 
        "org.eclipse.jubula.client.ui.rcp.commands.StartTestSuiteCommand.parameter.runningAut"; //$NON-NLS-1$

    /** ID of command state for most recently started Test Suite */
    public static final String LAST_STARTED_TEST_SUITE =
        "org.eclipse.jubula.client.ui.rcp.commands.StartTestSuiteCommand.state.lastStartedSuite"; //$NON-NLS-1$

    /** ID of command state for most recently tested Running AUT */
    public static final String LAST_TESTED_RUNNING_AUT =
        "org.eclipse.jubula.client.ui.rcp.commands.StartTestSuiteCommand.state.lastRunningAut"; //$NON-NLS-1$
    
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(final ExecutionEvent event) {
        if (!canStartTestExecution()) {
            return null;
        }
        Object testSuiteToStartObj = null;
        Object runningAutObj = null;
        ITestSuitePO testSuiteToStart = null;
        AutIdentifier runningAut = null;
        try {
            testSuiteToStartObj = 
                event.getObjectParameterForExecution(TEST_SUITE_TO_START);
            runningAutObj = 
                event.getObjectParameterForExecution(RUNNING_AUT);
        } catch (ExecutionException ee) {
            // Parameters could not be found or parsed.
            // Not a problem. We'll try later to use the current command
            // state to find out which Test Suite to start.
        }
        
        Command command = event.getCommand();
        State lastStartedTestSuiteState = command
                .getState(LAST_STARTED_TEST_SUITE);
        State lastTestedRunningAutState = command
                .getState(LAST_TESTED_RUNNING_AUT);
        
        if (testSuiteToStartObj instanceof ITestSuitePO
                && runningAutObj instanceof AutIdentifier) {
            testSuiteToStart = (ITestSuitePO)testSuiteToStartObj;
            runningAut = (AutIdentifier)runningAutObj;
            
        } else {
            if (lastStartedTestSuiteState != null
                    && lastTestedRunningAutState != null) {

                Object testSuiteStateValue = 
                    lastStartedTestSuiteState.getValue();
                Object runningAutStateValue = 
                    lastTestedRunningAutState.getValue();
                if (testSuiteStateValue instanceof String
                        && runningAutStateValue instanceof AutIdentifier) {
                    String testSuiteGUIDtoStart = (String) testSuiteStateValue;
                    List<ITestSuitePO> listOfTS = TestSuiteBP
                            .getListOfTestSuites();
                    for (ITestSuitePO ts : listOfTS) {
                        if (testSuiteGUIDtoStart.equals(ts.getGuid())) {
                            testSuiteToStart = ts;
                            break;
                        }
                    }
                    runningAut = (AutIdentifier) runningAutStateValue;
                }
            }
        }

        if (testSuiteToStart != null && runningAut != null
                && initTestExecution(event)) {
            final boolean autoScreenshots = Plugin.getDefault()
                    .getPreferenceStore()
                    .getBoolean(Constants.AUTO_SCREENSHOT_KEY);
            final int iterMax = Plugin.getDefault()
                    .getPreferenceStore()
                    .getInt(Constants.MAX_ITERATION_KEY);
            runTestSuite(testSuiteToStart, runningAut, autoScreenshots,
                    iterMax);

            // Update command state
            if (lastStartedTestSuiteState != null
                    && lastTestedRunningAutState != null) {
                lastStartedTestSuiteState.setValue(testSuiteToStart.getGuid());
                lastTestedRunningAutState.setValue(runningAut);
            }
        }

        return null;
    }
    
    /**
     * convenience method to save editors and start an incomplete or complete testsuite
     * with changing to execution perspective
     * @param ts testsuite to run
     * @param autId The ID of the Running AUT on which the test will take place.
     * @param autoScreenshot
     *            whether screenshots should be automatically taken in case of
     *            test execution errors
     * @param iterMax the maximum number of iterations
     */
    public void runTestSuite(ITestSuitePO ts, AutIdentifier autId,
            boolean autoScreenshot, int iterMax) {
        TestSuiteState state = validateSaveState(ts);
        if (state != TestSuiteState.incomplete) {
            ChooseTestSuiteBP.getInstance().executeTestSuite(ts, autId,
                    autoScreenshot, iterMax);
        }
    }

    /**
     * @param ts
     *            testsuite to validate
     * @return executable state of testsuite
     */
    public TestSuiteState validateSaveState(ITestSuitePO ts) {
        if (Plugin.getDefault().anyDirtyStar()) {
            boolean isSaved = Plugin.getDefault().showSaveEditorDialog(
                    getActiveShell());
            if (isSaved) {
                SortedSet<ITestSuitePO> allTestSuites = ChooseTestSuiteBP
                        .getInstance().getAllTestSuites();
                if (allTestSuites.contains(ts)) {
                    return TestSuiteState.complete;
                }
            }
            return TestSuiteState.incomplete;
        }
        return TestSuiteState.unchanged;
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateElement(UIElement element, Map parameters) {
        boolean check = false;
        Object testSuiteToStart = parameters.get(TEST_SUITE_TO_START);
        Object autToUse = parameters.get(RUNNING_AUT);
        ChooseTestSuiteBP ctsBP = ChooseTestSuiteBP.getInstance();

        ITestSuitePO lastUsedTestSuite = ctsBP.getLastUsedTestSuite();
        if (lastUsedTestSuite != null
                && lastUsedTestSuite.getId().toString()
                        .equals(testSuiteToStart)) {
            AutIdentifier lastUsedAUT = ctsBP.getLastUsedAUT();
            if (lastUsedAUT != null
                    && lastUsedAUT.getExecutableName().equals(autToUse)) {
                check = true;
            }
        }
        element.setChecked(check);
    }
}

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
package org.eclipse.jubula.client.ui.rcp.sourceprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.businessprocess.ITestExecutionEventListener;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.ui.ISources;


/**
 * Provides variables related to the running status of tests.
 *
 * @author BREDEX GmbH
 * @created Feb 8, 2010
 */
public class TestExecutionSourceProvider extends AbstractJBSourceProvider
    implements ITestExecutionEventListener {

    /** 
     * ID of variable that indicates whether an test is currently running
     */
    public static final String IS_TEST_RUNNING = 
        "org.eclipse.jubula.client.ui.rcp.variable.isTestRunning"; //$NON-NLS-1$
    
    /** 
     * ID of variable that indicates whether an test is currently paused
     */
    public static final String IS_TEST_PAUSED = 
        "org.eclipse.jubula.client.ui.rcp.variable.isTestPaused"; //$NON-NLS-1$

    /** value for variable indicating whether a test is currently running */
    private boolean m_isTestRunning = false;
    
    /** value for variable indicating whether a test is currently running */
    private boolean m_isTestPaused = false;
    
    /**
     * Constructor
     */
    public TestExecutionSourceProvider() {
        ClientTest.instance().addTestExecutionEventListener(this);
    }


    /**
     * {@inheritDoc}
     */
    public void dispose() {
        ClientTest.instance()
            .removeTestExecutionEventListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentState = 
            new HashMap<String, Object>();

        currentState.put(IS_TEST_RUNNING, m_isTestRunning);
        currentState.put(IS_TEST_PAUSED, m_isTestPaused);
        return currentState;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String [] {IS_TEST_RUNNING, IS_TEST_PAUSED};
    }

    /**
     * {@inheritDoc}
     */
    public void endTestExecution() {
        m_isTestRunning = false;
        m_isTestPaused = false;
        gdFireSourceChanged(ISources.WORKBENCH, 
                IS_TEST_RUNNING, m_isTestRunning);
        gdFireSourceChanged(ISources.WORKBENCH, 
                IS_TEST_PAUSED, m_isTestPaused);
    }


    /**
     * {@inheritDoc}
     */
    public void stateChanged(TestExecutionEvent event) {
        switch (event.getState()) {
            case TEST_EXEC_START:
            case TEST_EXEC_RESTART:
                m_isTestPaused = false;
                m_isTestRunning = true;
                break;
            case TEST_EXEC_PAUSED:
                m_isTestRunning = true;
                m_isTestPaused = true;
                break;
            case TEST_EXEC_ERROR:
            case TEST_EXEC_STOP:
            case TEST_EXEC_FAILED:
            case TEST_EXEC_FINISHED:
            case TEST_EXEC_OK:
            case TEST_EXEC_COMPONENT_FAILED:
                m_isTestRunning = false;
                m_isTestPaused = false;
                break;
            default:
                break;
        }
        
        gdFireSourceChanged(ISources.WORKBENCH, IS_TEST_RUNNING, 
                m_isTestRunning);
        gdFireSourceChanged(ISources.WORKBENCH, IS_TEST_PAUSED, 
                m_isTestPaused);
    }


    @Override
    public void receiveExecutionNotification(String notification) {
        // nothing
        
    }

}

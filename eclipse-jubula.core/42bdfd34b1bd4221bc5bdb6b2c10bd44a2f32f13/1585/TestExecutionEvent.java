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
package org.eclipse.jubula.client.core.businessprocess;

/**
 * @author BREDEX GmbH
 * @created 07.10.2004
 */
public class TestExecutionEvent {
    /** @author BREDEX GmbH */
    public static enum State {
        /** Signals that TestExecution is started */
        TEST_EXEC_START,
        /** Signals that TestExecution is stopped */
        TEST_EXEC_STOP,
        /** Signals that TestExecution fails */
        TEST_EXEC_FAILED,
        /** Signals that the cap has an error */
        TEST_EXEC_ERROR,
        /** Signals that the cap is ok */
        TEST_EXEC_OK,
        /** The result tree is ready for showing */
        TEST_EXEC_RESULT_TREE_READY,
        /** Test execution ready */
        TEST_EXEC_FINISHED,
        /** Signals that TestExecution fails, when the component name is wrong. */
        TEST_EXEC_COMPONENT_FAILED,
        /** Signals that TestExecution should be paused. */
        TEST_EXEC_PAUSED,
        /** Signals that TestExecution resumed. */
        TEST_EXEC_RESUMED,
        /** Signals that TestExecution updated. */
        TEST_EXEC_UPDATE,
        /** Signals that TestExecution restarted. */
        TEST_EXEC_RESTART,
        /** Signals that a test run with incomplete data failed */
        TEST_RUN_INCOMPLETE_TESTDATA_ERROR,
        /** Signals that a test run with incomplete object mapping failed */
        TEST_RUN_INCOMPLETE_OBJECTMAPPING_ERROR
    }

    /**
     * The state of TestExecution
     */
    private State m_state;

    /**
     * occurred Exception
     */
    private Exception m_exception;

    /**
     * Constructor that sets the state
     * 
     * @param state
     *            The state of TestExecution
     */
    public TestExecutionEvent(State state) {
        m_state = state;
    }

    /**
     * Constructor that sets the state
     * 
     * @param state
     *            The state of TestExecution
     * @param e
     *            Exception that occurred
     */
    public TestExecutionEvent(State state, Exception e) {
        m_state = state;
        m_exception = e;
    }

    /**
     * Gets the state
     * 
     * @return the state
     */
    public State getState() {
        return m_state;
    }

    /**
     * @return occurred Exception
     */
    public Exception getException() {
        return m_exception;
    }
}

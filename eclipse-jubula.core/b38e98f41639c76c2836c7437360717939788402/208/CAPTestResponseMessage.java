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
package org.eclipse.jubula.communication.internal.message;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * The response message for CAPTestMessage. This message is sent by
 * <code>CAPTestCommand</code>. <br>
 * The message contains an error event only if the test step fails.
 * {@inheritDoc}
 * 
 * @author BREDEX GmbH
 * @created 01.09.2004
 */
public class CAPTestResponseMessage extends Message {
    /** If test ok. */
    public static final int TEST_OK = 0;

    /** general test failure */
    public static final int TEST_FAILED = 1;

    /** test failed: security exception */
    public static final int FAILURE_SECURITY = 2;

    /** test failed: accessibility exception */
    public static final int FAILURE_ACCESSIBILITY = 3;

    /** test failed: no implementation class found */
    public static final int FAILURE_INVALID_IMPLEMENTATION_CLASS = 10;

    /** test failed: method was not found */
    public static final int FAILURE_METHOD_NOT_FOUND = 11;

    /** test failed: invalid arguments (parameter) */
    public static final int FAILURE_INVALID_PARAMETER = 12;

    /** test failed: method throws an exception */
    public static final int FAILURE_STEP_EXECUTION = 13;

    /** test failed: component not supported */
    public static final int FAILURE_UNSUPPORTED_COMPONENT = 20;

    /** test failed: component not found in the AUT */
    public static final int FAILURE_COMPONENT_NOT_FOUND = 21;

    /** constant to signal to pause the execution */
    public static final int PAUSE_EXECUTION = 31;
    
    /** test was skipped */
    public static final int TEST_SKIP = 40;
    
    /** test was successful but only contained skipped children */
    public static final int TEST_SUCCESS_ONLY_SKIPPED = 41;

    /** The state of test */
    private int m_state = TEST_OK;

    /** The error event. */
    private TestErrorEvent m_testErrorEvent;

    /** The CAP message data. */
    private MessageCap m_messageCap;

    /** The type of the returnValue that returns to the clientTest. */
    private String m_returnType = null;

    /** The value that returns to the clientTest. */
    private String m_returnValue = null;

    /**
     * Default constructor.
     */
    public CAPTestResponseMessage() {
        super();
    }

    /**
     * @return Returns the returnValue or an empty String if no return value is
     *         available.
     */
    public String getReturnValue() {
        return m_returnValue != null ? m_returnValue : StringConstants.EMPTY;
    }

    /**
     * @param returnValue
     *            The returnValue to set.
     */
    public void setReturnValue(String returnValue) {
        m_returnValue = returnValue;
    }

    /**
     * @return Returns the returnType.
     */
    public String getReturnType() {
        return m_returnType;
    }

    /**
     * @param returnType
     *            The returnType to set.
     */
    public void setReturnType(String returnType) {
        m_returnType = returnType;
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.CAP_TEST_RESPONSE_COMMAND;
    }

    /**
     * @return Returns the state.
     */
    public int getState() {
        return m_state;
    }

    /**
     * @param state
     *            The state to set.
     */
    public void setState(int state) {
        m_state = state;
    }

    /**
     * @return <code>true</code> if the message contains an error event,
     *         <code>false</code> otherwise.
     */
    public boolean hasTestErrorEvent() {
        return m_testErrorEvent != null;
    }

    /**
     * @return The error event (maybe <code>null</code>).
     */
    public TestErrorEvent getTestErrorEvent() {
        return m_testErrorEvent;
    }

    /**
     * Sets the error event.
     * 
     * @param testErrorEvent
     *            The error event.
     */
    public void setTestErrorEvent(TestErrorEvent testErrorEvent) {
        m_testErrorEvent = testErrorEvent;
        m_state = TEST_FAILED;
    }

    /**
     * Gets the CAP message data.
     * 
     * @return The message data.
     */
    public MessageCap getMessageCap() {
        return m_messageCap;
    }

    /**
     * Sets the CAP message data (required by Betwixt).
     * 
     * @param messageCap
     *            The message data
     */
    public void setMessageCap(MessageCap messageCap) {
        m_messageCap = messageCap;
    }
}
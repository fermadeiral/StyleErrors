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
package org.eclipse.jubula.tools.internal.objects.event;


/**
 * This factory creates test error events. The event represents a failure
 * condition during a test step execution.
 *
 * @author BREDEX GmbH
 * @created 06.04.2005
 */
public class EventFactory {
    /**
     * Default constructor.
     */
    private EventFactory() {
        // Nothing to be done
    }
    
    /**
     * Creates an event which represents a verification failure. Usually, this
     * event is created by the implementation classes if the verification fails
     * (because the expected and the actual values are not equal).
     * 
     * @param expected
     *            The expected value.
     * @param actual
     *            The actual value
     * @return The event. The expected and actual values are stored as
     *         properties <code>guidancerPattern</code> and
     *         <code>guidancerActualValue</code>, respectively.
     */
    public static TestErrorEvent createVerifyFailed(String expected,
        String actual) {
        TestErrorEvent event = new TestErrorEvent(
            TestErrorEvent.ID.VERIFY_FAILED);
        event.addProp(TestErrorEvent.Property.PATTERN_KEY, expected);
        event.addProp(TestErrorEvent.Property.ACTUAL_VALUE_KEY, actual);
        return event;
    }
    /**
     * Creates an event which represents a verification failure. Usually, this
     * event is created by the implementation classes if the verification fails.
     * 
     * @param actual
     *          actual value
     * @param pattern
     *          a pattern
     * @param operator
     *          an operator
     * @return
     *          the event
     */
    public static TestErrorEvent createVerifyFailed(String actual,
            String pattern, String operator) {
        
        TestErrorEvent event = new TestErrorEvent(
                TestErrorEvent.ID.VERIFY_FAILED);
        event.addProp(TestErrorEvent.Property.ACTUAL_VALUE_KEY, actual);
        event.addProp(TestErrorEvent.Property.PATTERN_KEY, pattern);
        event.addProp(TestErrorEvent.Property.OPERATOR_KEY, operator);
        return event;
    }
    /**
     * Creates an event representing any kind of error during the test step
     * execution, that means during the execution of an action.
     * 
     * @return The event.
     */
    public static TestErrorEvent createActionError() {
        return new TestErrorEvent(TestErrorEvent.ID.ACTION_ERROR);
    }

    /**
     * Creates an event representing any kind of error during the test step
     * execution, that means during the execution of an action.
     *
     * @param descriptionKey
     *      key of the error description
     * @return
     *      The event.
     */
    public static TestErrorEvent createActionError(String descriptionKey) {
        return createActionError(descriptionKey, new Object[0]);
    }

    /**
     * Creates an event representing any kind of error during the test step
     * execution, that means during the execution of an action.
     *
     * @param descriptionKey
     *      key of the error description
     * @param parameter
     *      parameter of the error description
     * @return
     *      The event.
     */
    public static TestErrorEvent createActionError(String descriptionKey,
            Object[] parameter) {
        TestErrorEvent event = new TestErrorEvent(
                TestErrorEvent.ID.ACTION_ERROR);
        event.addProp(TestErrorEvent.Property.DESCRIPTION_KEY, descriptionKey);
        event.addProp(TestErrorEvent.Property.PARAMETER_KEY, parameter);
        return event;
    }
    
    /**
     * if an action is not supported by a toolkit this event should be returned.
     * @return the event
     */
    public static TestErrorEvent createUnsupportedActionError() {
        return createActionError(
            TestErrorEvent.UNSUPPORTED_OPERATION_IN_TOOLKIT_ERROR);        
    }
    
    /**
     * if there was a security problem found in the toolkit. This is used
     * in the Web toolkit to signal security problems.
     * @return the event
     */
    public static TestErrorEvent createSecurityError() {
        return createActionError(
                TestErrorEvent.SECURITY_PROBLEM_IN_TOOLKIT_ERROR);
    }
    
    
    /**
     * @return A new error event with a configuration error ID.
     */
    public static TestErrorEvent createConfigErrorEvent() {
        return new TestErrorEvent(TestErrorEvent.ID.CONFIGURATION_ERROR);
    }

    /**
     * Creates an event representing an error that occurred in the testing
     * framework.
     *
     * @param descriptionKey
     *      key of the error description
     * @return
     *      The event.
     */
    public static TestErrorEvent createConfigErrorEvent(String descriptionKey) {
        return createConfigErrorEvent(descriptionKey, new Object[0]);
    }
    
    /**
     * Creates an event representing an error that occurred in the testing
     * framework.
     *
     * @param descriptionKey
     *      key of the error description
     * @param parameters
     *      parameter of the error description
     * @return
     *      The event.
     */
    public static TestErrorEvent createConfigErrorEvent(
            String descriptionKey, Object[] parameters) {
        TestErrorEvent event = createConfigErrorEvent();
        event.addProp(TestErrorEvent.Property.DESCRIPTION_KEY, descriptionKey);
        event.addProp(TestErrorEvent.Property.PARAMETER_KEY, parameters);
        return event;
    }

    /**
     * @return A new error event with an action error ID.
     */
    public static TestErrorEvent createImplClassErrorEvent() {
        return new TestErrorEvent(TestErrorEvent.ID.ACTION_ERROR);
    }

    /**
     * @return a new error event with the ID COMPONENT_NOT_FOUND.
     */
    public static TestErrorEvent createComponentNotFoundErrorEvent() {
        return new TestErrorEvent(TestErrorEvent.ID.COMPONENT_NOT_FOUND);
    }
}

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
package org.eclipse.jubula.rc.common.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.exception.StepVerifyFailedException;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;


/**
 * Utilities to verify values. It is used by the implementation classes in their
 * verify methods, e.g. to check if a graphics text field contains the expected
 * text.
 * 
 * @author BREDEX GmbH
 * @created 06.04.2005
 */
public class Verifier {
    /**
     * Default constructor.
     */
    private Verifier() {
    // Nothing to be done.
    }
    /**
     * Throws a <code>StepVerifyFailedException</code> containing
     * an error event.
     * 
     * @param expected The expected value.
     * @param actual The actual value.
     */
    public static void throwVerifyFailed(String expected, String actual) {
        throw new StepVerifyFailedException("Expected '" + expected //$NON-NLS-1$
            + "' but was '" + actual + "'", EventFactory.createVerifyFailed(//$NON-NLS-1$ //$NON-NLS-2$
                expected, actual));
    }
    /**
     * Checks if the passed strings are equal. The method uses
     * {@link StringUtils#equals(java.lang.String, java.lang.String)}. If the
     * values are not equal, an <code>StepVerifyFailedException</code>
     * will be thrown. It contains an error event with ID
     * <code>VERIFY_FAILED</code>.
     * 
     * @param expected
     *            The expected value.
     * @param actual
     *            The actual value.
     * @throws StepVerifyFailedException
     *             If the values are not equal.
     */
    public static void equals(String expected, String actual)
        throws StepVerifyFailedException {
        if (!StringUtils.equals(expected, actual)) {
            throwVerifyFailed(expected, actual);
        }
    }
    /**
     * Checks if the passed boolean values are equal.
     * 
     * @param expected
     *            The expected value.
     * @param actual
     *            The actual value.
     * @throws StepVerifyFailedException
     *             If the values are not equal.
     */
    public static void equals(boolean expected, boolean actual)
        throws StepVerifyFailedException {
        if (expected != actual) {
            throwVerifyFailed(String.valueOf(expected), String.valueOf(actual));
        }
    }
    
    /**
     * Checks if the passed integer values are equal.
     * 
     * @param expected
     *            The expected value.
     * @param actual
     *            The actual value.
     * @throws StepVerifyFailedException
     *             If the values are not equal.
     */
    public static void equals(int expected, int actual)
        throws StepVerifyFailedException {
        if (expected != actual) {
            throwVerifyFailed(String.valueOf(expected), String.valueOf(actual));
        }
    }
    
    /**
     * Checks if the passed integer values are equal.
     * 
     * @param expected The expected value.
     * @param actual The actual value.
     * @param shouldEqual If the values should be equal or not.
     * @throws StepVerifyFailedException If the values are not equal.
     */
    public static void equals(int expected, int actual, boolean shouldEqual)
        throws StepVerifyFailedException {
        
        final boolean equals = (expected == actual);
        equals(shouldEqual, equals);
    }
    
    /**
     * Checks if <code>text</code> matches <code>pattern</code> with a given
     * operation
     * 
     * @param text
     *          a text
     * @param pattern
     *          a pattern
     * @param operator
     *          a operator
     * @throws StepVerifyFailedException
     *          if verifying fails
     * @throws StepExecutionException
     *          if execution fails for example if the operator is unknown
     */
    public static void match(String text, String pattern, String operator)
        throws StepVerifyFailedException, StepExecutionException {
        
        match(text, pattern, operator, true);
    }
    
    /**
     * Checks if <code>text</code> matches <code>pattern</code> with a given
     * operation
     * 
     * @param text a text
     * @param pattern a pattern
     * @param operator a operator
     * @param shouldMatch true if the matcher should match, false if the matcher
     * should be successful if the match is false.  
     * @throws StepVerifyFailedException if verifying fails
     * @throws StepExecutionException if execution fails for example if 
     * the operator is unknown
     */
    public static void match(String text, String pattern, String operator, 
            boolean shouldMatch)
        throws StepVerifyFailedException, StepExecutionException {
        
        if (shouldMatch 
                == !MatchUtil.getInstance().match(text, pattern, operator)) {
            throw new StepVerifyFailedException("verification failed", //$NON-NLS-1$
                    EventFactory.createVerifyFailed(text, pattern, operator));
        }
    }
    
    
    /**
     * Checks if <code>text</code> matches <code>pattern</code> with a given
     * operation
     * 
     * @param text
     *          a text
     * @param pattern
     *          an array of patterns
     * @param operator
     *          a operator
     * @throws StepVerifyFailedException
     *          if verifying fails
     * @throws StepExecutionException
     *          if execution fails for example if the operator is unknown
     */
    public static void match(String text, String[] pattern, String operator)
        throws StepVerifyFailedException, StepExecutionException {

        if (!MatchUtil.getInstance().match(text, pattern, operator)) {
            throw new StepVerifyFailedException("verification failed", //$NON-NLS-1$
                EventFactory.createVerifyFailed(text, pattern.toString(),
                    operator));
        }
    }
}

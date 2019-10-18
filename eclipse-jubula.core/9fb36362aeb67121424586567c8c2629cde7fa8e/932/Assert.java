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
package org.eclipse.jubula.tools.internal.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an assert mechanism. A condition can be passed
 * to the this class via the assert method. If the condition is true,
 * nothing happens, otherwise an AssertException is thrown.
 * This class is only for debugging purpose.
 *
 * @author BREDEX GmbH
 * @created 08.10.2004
 */
public class Assert {
    /** standard logging */
    private static Logger log = LoggerFactory.getLogger(Assert.class);
    
    /**
     *  not to use
     */
    private Assert() {
        // not to use
    }
    
    /** Checks if the given condition is true.
     * if it is, nothing happens and the method returns without any
     * action. Otherwise an AssertException is thrown.
     * @param b result of verify
     */
    public static void verify(boolean b) {
        verify(b, "Assertion failed"); //$NON-NLS-1$
    }
    /** Checks if the given condition is true.
     * if it is, nothing happens and the method returns without any
     * action. Otherwise an AssertException with the given
     * message is thrown.
     * @param b result of verify
     * @param msg message for Exception
     */
    public static void verify(boolean b, String msg) {
        if (!b) {
            log.error(msg);
            throw new AssertException(msg);
        }
    }
    
    /** Throws always an AssertException. Use this method for
     * code segments which are not expected to be reached.
     */
    public static void notReached() {
        notReached("Code reached that was not " + //$NON-NLS-1$
                " expected to be reached"); //$NON-NLS-1$
    }
    
    /** Throws always an AssertException. Use this method for
     * exceptions that must be caught, but never expected to be thrown
     * @param t exception or error
     */
    public static void notThrown(Throwable t) {
        
        log.error("Unexpected Exception\n", t); //$NON-NLS-1$
        notReached();
    }
        
    /** Throws always an AssertException with the given message.
     * Use this method for  code segments which are not expected
     * to be reached.
     * @param msg message for exception
     */
    public static void notReached(String msg) {
        verify(false, msg);
    }
    
}


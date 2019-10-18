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
package org.eclipse.jubula.client.core.businessprocess.progress;

import org.eclipse.core.runtime.OperationCanceledException;

/**
 * Utility class for identifying and dealing with runtime exceptions caused
 * by canceling an operation.
 *
 * @author BREDEX GmbH
 * @created Apr 30, 2009
 */
public class OperationCanceledUtil {

    /**
     * Private constructor for utility class.
     */
    private OperationCanceledUtil() {
        // Nothing to initialize
    }

    /**
     * Throws an <code>OperationCanceledException</code> if the given exception's
     * root cause is an <code>OperationCanceledException</code>. Otherwise
     * does nothing.
     * 
     * @param rte The exception to check.
     * @throws OperationCanceledException if the root cause of the given 
     *                              exception is an 
     *                              <code>OperationCanceledException</code>.
     */
    public static void checkForOperationCanceled(RuntimeException rte) 
        throws OperationCanceledException {
        
        OperationCanceledException oce = getOperationCanceledException(rte);
        if (oce != null) {
            throw oce;
        }
    }

    /**
     * Throws an <code>OperationCanceledException</code> if the given exception's
     * root cause is an <code>OperationCanceledException</code>. Otherwise
     * does nothing.
     * 
     * @param rte The exception to check.
     * 
     * @return <code>true</code> if the root cause of the given exception is 
     *         an <code>OperationCanceledException</code>. 
     *         Otherwise, <code>false</code>.
     */
    public static boolean isOperationCanceled(RuntimeException rte) {
        
        return getOperationCanceledException(rte) != null;
    }

    /**
     * 
     * @param rte The exception to check.
     * @return the root cause of the given exception if that root cause is
     *         an <code>OperationCanceledException</code>. 
     *         Otherwise, <code>null</code>.
     */
    private static OperationCanceledException getOperationCanceledException(
            RuntimeException rte) {
     
        Throwable cause = rte;
        while (cause != null 
                && cause != cause.getCause()
                && !(cause instanceof OperationCanceledException)) {
            cause = cause.getCause();
        }
        
        
        return (cause instanceof OperationCanceledException) 
            ? (OperationCanceledException)cause : null;
        
    }
}

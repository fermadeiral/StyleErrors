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

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * This class converts user indices to implementation indices and vice versa
 *
 * @author BREDEX GmbH
 * @created 11.11.2005
 */
public class IndexConverter {
    
    /**
     * standard constructor - defined to forbid construction
     */
    private IndexConverter() {
        // forbids construction
    }
    
    /**
     * Converts a implementation index to a user index.
     * user indices are 1, 2, 3, ...
     * implementation indices are 0, 1, 2, ...
     * @param idx implementation index
     * @return user index
     */
    public static int toUserIndex(int idx) {
        return idx + 1;
    }
    
    /**
     * Converts a user index to a implementation index (idx - 1)
     * @param idx user index
     * @return implementation index
     */
    public static int toImplementationIndex(int idx) {
        return idx - 1;
    }

    /**
     * converts an array of implementation indices to an array of
     * user indices
     * @param indices implementation indices
     * @return user indices
     */
    public static int[] toUserIndices(int[] indices) {
        int[] res = new int[indices.length];
        for (int i = 0; i < res.length; ++i) {
            res[i] = indices[i] + 1;
        }
        return res;
    }
    
    /**
     * converts an array of user indices to an array of
     * implementation indices
     * @param indices user indices
     * @return implementation indices
     */
    public static int[] toImplementationIndices(int[] indices) {
        int[] res = new int[indices.length];
        for (int i = 0; i < res.length; ++i) {
            res[i] = indices[i] - 1;
        }
        return res;
    }
    
    /**
     * converts an array of implementation indices to an array of
     * user indices
     * @param indices implementation indices
     * @return user indices
     */
    public static Integer[] toUserIndices(Integer[] indices) {
        Integer[] res = new Integer[indices.length];
        for (int i = 0; i < res.length; ++i) {
            res[i] = new Integer(indices[i].intValue() + 1);
        }
        return res;
    }
    
    /**
     * converts an array of user indices to an array of
     * implementation indices
     * @param indices user indices
     * @return implementation indices
     */
    public static Integer[] toImplementationIndices(Integer[] indices) {
        Integer[] res = new Integer[indices.length];
        for (int i = 0; i < res.length; ++i) {
            res[i] = new Integer(indices[i].intValue() - 1);
        }
        return res;
    }

    /**
     * @param integerString
     *            the string to parse to an int
     * @return the parsed int or throws a <code>StepExecutionException</code> in
     *         case of a nested number format exception
     */
    public static int intValue(String integerString) {
        try {
            return Integer.parseInt(integerString);
        } catch (NumberFormatException e) {
            throw new StepExecutionException(
                "Index '" + integerString + "' is not an integer.", //$NON-NLS-1$ //$NON-NLS-2$
                    EventFactory.createActionError(
                        TestErrorEvent.INVALID_INDEX));
        }
    }
}

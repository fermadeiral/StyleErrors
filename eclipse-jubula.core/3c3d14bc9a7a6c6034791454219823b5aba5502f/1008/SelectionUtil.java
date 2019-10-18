/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.util;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * Utility methods to check whether a given selection is valid or not.
 * 
 * @author BREDEX GmbH
 * @created Aug 06, 2013
 * 
 */
public class SelectionUtil {
    /** private constructor */
    private SelectionUtil() {
        // do nothing
    }

    /**
     * Checks if there is any selection on the widget
     * 
     * @param array
     *            an array of Objects to be validated
     * @throws StepExecutionException
     *             thrown if no or empty selection given
     */
    public static void validateSelection(Object[] array)
        throws StepExecutionException {
        if (ArrayUtils.isEmpty(array)) {
            throw new StepExecutionException("No selection found", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.NO_SELECTION));
        }
    }

    /**
     * Checks if there is any selection on the widget
     * 
     * @param array
     *            an array of integers to be validated
     * @throws StepExecutionException
     *             thrown if no or empty selection given
     */
    public static void validateSelection(int[] array)
        throws StepExecutionException {
        if (ArrayUtils.isEmpty(array)) {
            throw new StepExecutionException("No selection found", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.NO_SELECTION));
        }
    }
}

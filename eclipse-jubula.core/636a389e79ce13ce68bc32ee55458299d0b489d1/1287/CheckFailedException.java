/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.exceptions;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.Result;

/** @author BREDEX GmbH */
public class CheckFailedException extends ExecutionException {
    /** the actualValue */
    private String m_actualValue;

    /**
     * Constructor
     * 
     * @param result
     *            the result
     * @param actualValue
     *            the actual value
     * @param message
     *            the message
     */
    public CheckFailedException(Result result, 
        @Nullable String message,
        String actualValue) {
        super(result, message);
        m_actualValue = actualValue;
    }

    /**
     * @return the actualValue
     */
    public String getActualValue() {
        return m_actualValue;
    }
}
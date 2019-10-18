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

/**
 * Base class for all exceptions happening when executing {@link org.eclipse.jubula.communication.CAP CAPs} 
 * @author BREDEX GmbH 
 */
public abstract class ExecutionException extends RuntimeException {
    /** the result */
    private Result m_result;

    /**
     * Constructor
     * 
     * @param result
     *            the result
     * @param message
     *            the message
     */
    public ExecutionException(Result result, 
        @Nullable String message) {
        super(message);
        m_result = result;
    }

    /**
     * @return the result
     */
    public Result getResult() {
        return m_result;
    }
}
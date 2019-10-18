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
package org.eclipse.jubula.client.internal.impl;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.Result;
import org.eclipse.jubula.client.exceptions.ExecutionException;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.tools.internal.utils.StringParsing;

/**
 * @author BREDEX GmbH
 * @param <T>
 *            the payload type
 */
public class ResultImpl<T> implements Result<T> {
    /** the payload */
    private T m_payload;
    /** the cap */
    @NonNull
    private CAP m_cap;
    /** the result flag */
    private boolean m_isOK = false;
    /** the exception */
    private ExecutionException m_exception;
    
    /** the return value to store variables for the API */
    private String m_returnValue;

    /**
     * Constructor
     * 
     * @param cap
     *            the CAP
     * @param payload
     *            the payload to use
     */
    public ResultImpl(@NonNull CAP cap, @Nullable T payload) {
        Validate.notNull(cap, "The CAP must not be null."); //$NON-NLS-1$

        m_cap = cap;
        m_payload = payload;
    }

    /** {@inheritDoc} */
    @Nullable
    public T getPayload() {
        return m_payload;
    }

    /** {@inheritDoc} */
    @NonNull
    public CAP getCAP() {
        return m_cap;
    }

    /** {@inheritDoc} */
    public boolean isOK() {
        return m_isOK;
    }

    /**
     * @param isOK
     *            the isOK to set
     */
    void setOK(boolean isOK) {
        m_isOK = isOK;
    }

    /** {@inheritDoc} */
    @Nullable
    public ExecutionException getException() {
        return m_exception;
    }

    /**
     * @param exception the exception to set
     */
    void setException(ExecutionException exception) {
        m_exception = exception;
    }
    
    /**
     * @return the stored variable value
     */
    public String getReturnValue() {
        return m_returnValue;
    }
    
    /**
     * @param value the value to store
     */
    public void setReturnValue(String value) {
        m_returnValue = value;
    }

   /** {@inheritDoc} */
    public Map<String, String> getReturnValueAsMap() {
        return StringParsing.convertToMap(m_returnValue);
    }
}
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
package org.eclipse.jubula.client;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.exceptions.ExecutionException;
import org.eclipse.jubula.communication.CAP;

/**
 * Representing the result of a remotely executed {@link org.eclipse.jubula.communication.CAP CAP}
 * 
 * @author BREDEX GmbH
 * @param <T>
 *            the payload type
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface Result<T> {
    /**
     * @return the payload
     */
    @Nullable
    T getPayload();

    /**
     * @return the execution problem
     */
    @Nullable
    ExecutionException getException();

    /**
     * @return the CAP
     */
    CAP getCAP();

    /**
     * @return whether the {@link org.eclipse.jubula.communication.CAP CAP} has been executed successfully or not
     */
    boolean isOK();
    
    /**
     * @return the stored value, mainly for use in the API
     * @since 3.2
     */
    String getReturnValue(); 
    
    /**
     * @param value the value to store, mainly for use in the API
     * @since 3.2
     */
    void setReturnValue(String value);
    
    /**
     * @return the stored value interpreted as a map
     * @since 3.3
     */
    Map<String, String> getReturnValueAsMap();
}
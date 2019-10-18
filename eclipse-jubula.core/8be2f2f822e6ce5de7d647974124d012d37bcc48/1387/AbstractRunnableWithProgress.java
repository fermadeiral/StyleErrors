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
package org.eclipse.jubula.client.core.progress;

import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * An {@link IRunnableWithProgress} with a return result.
 *
 * @param <T> The type of the return result.
 * 
 * @author BREDEX GmbH
 * @created Jun 4, 2010
 */
public abstract class AbstractRunnableWithProgress<T> implements
        IRunnableWithProgress {

    /** the return result */
    private T m_result;

    /**
     * 
     * @param result the result to use.
     */
    protected void setResult(T result) {
        m_result = result;
    }
    
    /**
     * 
     * @return the result.
     */
    public T getResult() {
        return m_result;
    }
}

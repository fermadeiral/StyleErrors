/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.functions;

import org.eclipse.jubula.tools.internal.exception.InvalidDataException;

/**
 * Interface for functions that can be used in test data.
 */
public interface IFunctionEvaluator {

    /**
     * 
     * @param arguments The arguments for the function evaluation.
     * @return the result of the function evaluation.
     * @throws InvalidDataException if an error prevents the evaluation from
     *                              completing successfully.
     */
    public String evaluate(String[] arguments) throws InvalidDataException;
    
}

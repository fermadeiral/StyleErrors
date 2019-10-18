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
 * Function that subtracts number from another.
 */
public class SubtractFunctionEvaluator extends AbstractFunctionEvaluator {

    /**
     * {@inheritDoc}
     */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 2);
        double minuend = Double.parseDouble(arguments[0]);
        double subtrahend = Double.parseDouble(arguments[1]);
        
        return String.valueOf(minuend - subtrahend);
    }

}

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


/**
 * Function that multiplies 0..n numbers.
 */
public class MultiplyFunctionEvaluator implements IFunctionEvaluator {

    /**
     * 
     * {@inheritDoc}
     */
    public String evaluate(String[] arguments) {
        double result = 1;

        for (String arg : arguments) {
            result *= Double.parseDouble(arg);
        }
        
        return String.valueOf(result);
    }

}

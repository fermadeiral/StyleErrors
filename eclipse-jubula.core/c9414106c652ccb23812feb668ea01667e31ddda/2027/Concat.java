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
 * Concatenate two or more strings.
 */
public class Concat implements IFunctionEvaluator {

    @Override
    public String evaluate(String[] arguments) {
        
        String result = "";  //$NON-NLS-1$
        
        for (String args : arguments) {
            result = result + args;
        }
        return result;
    }
}

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
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * Returns a substring from a given String.
 */
public class Substring extends AbstractFunctionEvaluator {

    @Override
    public String evaluate(String[] arguments) throws InvalidDataException {
        try {
            validateParamCount(arguments, 3);
            String string = arguments[0];
            int from = Integer.parseInt(arguments[1]);
            int to = Integer.parseInt(arguments[2]);
            
            return string.substring(from, to);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(e.getLocalizedMessage(),
                    MessageIDs.E_WRONG_PARAMETER_VALUE);
        }
    }
}

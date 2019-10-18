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
 * Trim deletes all whitespaces from start and end of a String.
 */
public class Trim extends AbstractFunctionEvaluator {

    @Override
    public String evaluate(String[] arguments) throws InvalidDataException {
        try {
            validateParamCount(arguments, 1);
            String string = arguments[0];
            
            return string.trim();
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(e.getLocalizedMessage(),
                    MessageIDs.E_FUNCTION_EVAL_ERROR);
        }
    }
}

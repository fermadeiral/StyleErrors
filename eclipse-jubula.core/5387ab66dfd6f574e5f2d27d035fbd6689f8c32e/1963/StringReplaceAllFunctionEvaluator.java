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

import java.util.regex.PatternSyntaxException;

import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * Function that performs a string replaceAll
 */
public class StringReplaceAllFunctionEvaluator 
    extends AbstractFunctionEvaluator {
    /** {@inheritDoc} */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 3);
        final String string = arguments[0];
        final String regex = arguments[1];
        final String replacement = arguments[2];

        String replacedString = string;
        try {
            replacedString = string.replaceAll(regex, replacement);
        } catch (PatternSyntaxException e) {
            throw new InvalidDataException(e.getLocalizedMessage(),
                    MessageIDs.E_FUNCTION_EVAL_ERROR);
        }
        return replacedString;
    }
}

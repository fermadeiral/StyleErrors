/*******************************************************************************
 * Copyright (c) 2016 Markus Tiede
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Tiede - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.functions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * Encodes a given String for usage within URLs
 */
public class URLEncode extends AbstractFunctionEvaluator {

    @Override
    public String evaluate(String[] arguments) throws InvalidDataException {
        try {
            validateParamCount(arguments, 1);
            String string = arguments[0];
            
            return URLEncoder.encode(string, "UTF-8"); //$NON-NLS-1$
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {
            throw new InvalidDataException(e.getLocalizedMessage(),
                    MessageIDs.E_FUNCTION_EVAL_ERROR);
        }
    }
}
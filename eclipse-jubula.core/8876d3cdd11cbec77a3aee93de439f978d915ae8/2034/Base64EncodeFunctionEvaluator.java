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

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;

/**
 * Function that encodes a given string into a base 64 string
 */
public class Base64EncodeFunctionEvaluator extends AbstractFunctionEvaluator {
    /** {@inheritDoc} */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 1);
        return new String(Base64.encodeBase64(arguments[0].getBytes()));
    }
}

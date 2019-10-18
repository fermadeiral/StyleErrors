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
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;

/**
 * Returns system property with propertyName as name.
 */
public class TestUDVEvaluator extends AbstractFunctionEvaluator {

    /**
     * {@inheritDoc}
     */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 1);
        try {
            String key = arguments[0];
            return EnvironmentUtils.getProcessOrSystemProperty(key);
        } catch (SecurityException se) {
            throw new InvalidDataException(se.getLocalizedMessage(),
                    MessageIDs.E_WRONG_PARAMETER_VALUE);
        }
    }
}

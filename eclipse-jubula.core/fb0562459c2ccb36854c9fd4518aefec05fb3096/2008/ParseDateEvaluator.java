/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.functions;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * @author BREDEX GmbH
 * 
 */
public final class ParseDateEvaluator extends AbstractFunctionEvaluator {

    /**
     * {@inheritDoc}
     */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 2);
        try {
            Date result = DateUtils.parseDate(arguments[0],
                    new String[] { arguments[1] });
            return String.valueOf(result.getTime());
        } catch (ParseException e) {
            throw new InvalidDataException("parsing failed, reason: " //$NON-NLS-1$
                    + e.getMessage(), MessageIDs.E_WRONG_PARAMETER_VALUE);
        }
    }

}

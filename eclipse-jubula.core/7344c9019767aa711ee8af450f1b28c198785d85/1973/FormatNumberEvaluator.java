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

import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * Function that formats a given String number and trims the decimal places to
 * the given amount. It can therefore convert a Double to an Integer if the
 * amount of decimal places is specified to be 0. The numbers will be rounded.
 * 
 * @author BREDEX GmbH
 * @created 14.12.2016
 *
 */
public class FormatNumberEvaluator extends AbstractFunctionEvaluator {

    @Override
    /**
     * {@inheritDoc}
     */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 2);
        try {
            Double number = Double.parseDouble(arguments[0]);
            Integer decimals = Integer.parseInt(arguments[1]);

            if (decimals == 0) {
                return String.format("%.0f", Math.floor(number)); //$NON-NLS-1$
            } else if (decimals > 0) {
                double formatted = (long) (number * Math.pow(10, decimals)) 
                        / Math.pow(10, decimals);
                return Double.toString(formatted);
            }
            throw new InvalidDataException("Invalid decimals parameter: " //$NON-NLS-1$
                    + arguments[1], MessageIDs.E_NEG_VAL);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Invalid parameter(s) for formatNumber function", //$NON-NLS-1$
                    MessageIDs.E_PARAMETER_ERROR);
        }
    }
}

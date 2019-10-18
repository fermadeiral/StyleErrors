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

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * @author BREDEX GmbH
 * 
 */
public final class ModifyDateEvaluator extends AbstractFunctionEvaluator {

    /**
     * 
     * {@inheritDoc}
     */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 2);
        try {
            long dateTime = Long.parseLong(arguments[0]);
            if (dateTime < 0) {
                throw new InvalidDataException("value to small: " + dateTime, //$NON-NLS-1$
                        MessageIDs.E_TOO_SMALL_VALUE);
            }
            String opString = arguments[1];
            int opStringLength = opString.length();
            if (opStringLength < 2) {
                throw new InvalidDataException("illegal value: " + opString, //$NON-NLS-1$
                        MessageIDs.E_WRONG_PARAMETER_VALUE);
            }
            String op = opString.substring(opStringLength - 1, opStringLength);
            String offsetString = opString.substring(0, opStringLength - 1);
            try {
                int offset = Integer.parseInt(offsetString);
                Date date = new Date(dateTime);
                Date result = null;
                if (op.equalsIgnoreCase("d")) { //$NON-NLS-1$
                    result = DateUtils.addDays(date, offset);
                } else if (op.equalsIgnoreCase("m")) { //$NON-NLS-1$
                    result = DateUtils.addMonths(date, offset);
                } else if (op.equalsIgnoreCase("y")) { //$NON-NLS-1$
                    result = DateUtils.addYears(date, offset);
                } else if (op.equalsIgnoreCase("h")) { //$NON-NLS-1$
                    result = DateUtils.addHours(date, offset);
                } else if (op.equalsIgnoreCase("i")) { //$NON-NLS-1$
                    result = DateUtils.addMinutes(date, offset);
                } else if (op.equalsIgnoreCase("s")) { //$NON-NLS-1$
                    result = DateUtils.addSeconds(date, offset);
                } else if (op.equalsIgnoreCase("j")) { //$NON-NLS-1$
                    result = DateUtils.addMilliseconds(date, offset);
                } else {
                    throw new InvalidDataException("illegal offset format: " //$NON-NLS-1$
                            + arguments[1], MessageIDs.E_WRONG_PARAMETER_VALUE);
                }
                return String.valueOf(result.getTime());
            } catch (NumberFormatException e) {
                throw new InvalidDataException("illegal offset format: " //$NON-NLS-1$
                        + arguments[1], MessageIDs.E_WRONG_PARAMETER_VALUE);
            }

        } catch (NumberFormatException e) {
            throw new InvalidDataException("not an integer: " + arguments[0], //$NON-NLS-1$
                    MessageIDs.E_BAD_INT);
        }

    }

}

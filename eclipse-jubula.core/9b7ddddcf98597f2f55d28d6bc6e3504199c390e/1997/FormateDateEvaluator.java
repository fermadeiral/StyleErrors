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

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * @author BREDEX GmbH
 * 
 */
public final class FormateDateEvaluator extends AbstractFunctionEvaluator {

    /**
     * 
     * {@inheritDoc}
     */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 2);
        try {
            Long dateTime = Long.valueOf(arguments[0]);
            if (dateTime < 0) {
                throw new InvalidDataException("value to small: " + dateTime, //$NON-NLS-1$
                        MessageIDs.E_TOO_SMALL_VALUE);
            }
            Date date = new Date(dateTime);

            return DateFormatUtils.format(date, arguments[1]);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("not an integer: " + arguments[0], //$NON-NLS-1$
                    MessageIDs.E_BAD_INT);

        }
    }

}

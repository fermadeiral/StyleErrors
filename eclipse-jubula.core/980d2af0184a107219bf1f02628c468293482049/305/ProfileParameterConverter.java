/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.command.parameters;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;
/**
 * Profile converter
 *
 * @author BREDEX GmbH
 * @created 21.12.2015
 */
public class ProfileParameterConverter extends AbstractParameterValueConverter {

    @Override
    public Object convertToObject(String parameterValue)
            throws ParameterValueConversionException {
        ProfileTypeParameter ptp = new ProfileTypeParameter();
        ptp.setType(parameterValue);
        if (ptp.getType() == null) {
            throw new ParameterValueConversionException("Not a Profile!"); //$NON-NLS-1$
        }
        return ptp;
    }

    @Override
    public String convertToString(Object parameterValue)
            throws ParameterValueConversionException {
        if (!(parameterValue instanceof ProfileTypeParameter)) {
            throw new ParameterValueConversionException("Wrong Object!"); //$NON-NLS-1$
        }
        return ((ProfileTypeParameter)parameterValue).getType();
    }
    

}

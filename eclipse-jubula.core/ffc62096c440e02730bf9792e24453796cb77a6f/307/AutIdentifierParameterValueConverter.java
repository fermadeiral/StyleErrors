/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
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
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;


/**
 * Converts AUT Identifiers to Strings and back again. The String 
 * representation of an AUT Identifier for the purposes of conversion is 
 * the serialization of the AUT Identifier object.
 *
 * @author BREDEX GmbH
 * @created Feb 2, 2010
 */
public class AutIdentifierParameterValueConverter extends
        AbstractParameterValueConverter {

    /**
     * {@inheritDoc}
     */
    public AutIdentifier convertToObject(String parameterValue)
        throws ParameterValueConversionException {

        if (parameterValue == null) {
            throw new ParameterValueConversionException(
                    Messages.CouldNotConvertParameterValue
                    + StringConstants.COLON + StringConstants.SPACE
                    + parameterValue);
        }
        
        try {
            return AutIdentifier.decode(parameterValue);
        } catch (IllegalArgumentException iae) {
            throw new ParameterValueConversionException(
                    Messages.CouldNotConvertParameterValue
                    + StringConstants.COLON + StringConstants.SPACE
                    + parameterValue, iae);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String convertToString(Object parameterValue)
        throws ParameterValueConversionException {

        ParameterValueConverterUtil.checkType(
                parameterValue, AutIdentifier.class);
        return ((AutIdentifier)parameterValue).encode();
    }

}

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
package org.eclipse.jubula.client.core.utils;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;


/**
 * validates values set for parameters of type "Variable", therefore 
 * parameter for storeValue actions
 *
 * @author BREDEX GmbH
 * @created 26.11.2007
 */
public class VariableParamValueValidator implements IParamValueValidator {

    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueValidator#validateInput(java.util.List)
     */
    public ConvValidationState validateInput(List<IParamValueToken> tokens) {
        ConvValidationState state = ConvValidationState.valid;
        for (IParamValueToken token : tokens) {
            if (token instanceof SimpleValueToken) {
                final String wordRegex = "[0-9a-z_A-Z]{1,}"; //$NON-NLS-1$
                if (Pattern.matches(wordRegex, token.getGuiString())) {
                    state = ConvValidationState.valid;                
                } else {
                    state = ConvValidationState.invalid;
                    token.setErrorKey(MessageIDs.E_PARSE_NAME_ERROR);
                    break;
                }
            }
        }
        return state;
    }

}

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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created 27.11.2007
 */
public class ComboParamValidator implements IParamValueValidator {
    
    /** permitted values for ComboBox */
    private String[] m_values;
    
    /** whether combinations of the supplied values are allowed */
    private boolean m_valuesAreCombinable;
    
    /**
     * @param values permitted values in combo box
     * @param valuesAreCombinable
     *            whether combinations of the supplied values are allowed
     */
    public ComboParamValidator(String[] values, boolean valuesAreCombinable) {
        m_values = values;
        m_valuesAreCombinable = valuesAreCombinable;
    }

    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueValidator#validateInput(java.util.List)
     */
    public ConvValidationState validateInput(List<IParamValueToken> tokens) {
        ConvValidationState state = ConvValidationState.valid;
        if (m_values.length == 0) {
            return state;
        }
        StringBuilder evalBuilder = new StringBuilder();
        for (IParamValueToken token : tokens) {
            if (token instanceof RefToken 
                    || token instanceof VariableToken
                    || token instanceof FunctionToken) {
                // Since we don't want to evaluate the reference/variable, 
                // we can't verify whether or not it creates a valid value.
                // Just assume that the value is fine.
                return ConvValidationState.valid;
            } else if (token instanceof LiteralToken) {
                String value = token.getGuiString();
                evalBuilder.append(value.substring(1, value.length() - 1));
            } else if (token instanceof SimpleValueToken) {
                evalBuilder.append(token.getGuiString());
            }
        }
        
        String evalString = evalBuilder.toString();
        String [] evalArray;
        if (m_valuesAreCombinable) {
            evalArray = 
                evalString.split(TestDataConstants.COMBI_VALUE_SEPARATOR);
        } else {
            evalArray = new String [] {evalString};
        }
        state = ConvValidationState.valid;
        for (String val : evalArray) {
            if (!Arrays.asList(m_values).contains(val)) {
                if (tokens.size() > 0) {
                    tokens.get(0).setErrorKey(MessageIDs.E_NOT_SUPP_COMBO_ITEM);
                }
                state = ConvValidationState.undecided;
                boolean couldMatch = false;
                for (String possibleValue : m_values) {
                    couldMatch |= possibleValue.startsWith(val);
                }
                if (!couldMatch) {
                    return ConvValidationState.invalid;
                }
            }
        }

        return state;
    }
        

}

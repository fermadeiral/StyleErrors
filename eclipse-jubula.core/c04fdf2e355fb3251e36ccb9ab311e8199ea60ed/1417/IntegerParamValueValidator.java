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

import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;


/**
 * validator for parameter values of Type Integer
 *
 * @author BREDEX GmbH
 * @created 23.11.2007
 */
public class IntegerParamValueValidator implements IParamValueValidator {

    /** minus sign */
    private static final String MINUS = "-"; //$NON-NLS-1$
    /** lower value */
    private int m_minVal;
    /** upper value */
    private int m_maxVal;
    /** optional constraint for allowed values */
    private int[] m_valuesSet;

    /**
     * constructor
     * @param minVal minimum value considered valid
     * @param maxVal maximun value considered valid
     * @param valuesSet optional constraint for allowed values, null if there
     * are no constraints
     * 
     */
    public IntegerParamValueValidator(int minVal, int maxVal, 
            String[] valuesSet) {
        m_minVal = minVal;
        m_maxVal = maxVal;
        if ((valuesSet != null) && (valuesSet.length > 0)) {
            m_valuesSet = new int[valuesSet.length];
            for (int i = 0; i < valuesSet.length; i++) {
                try {
                    m_valuesSet[i] = Integer.parseInt(valuesSet[i]);
                } catch (NumberFormatException e) {
                    m_valuesSet[i] = Integer.MIN_VALUE;
                }
            }
        } else {
            m_valuesSet = null;
        }
    }
    /**
     * {@inheritDoc}
     */
    public ConvValidationState validateInput(List<IParamValueToken> tokens) {
        ConvValidationState val = ConvValidationState.valid;
        if (tokens.size() == 1) {
            IParamValueToken token = tokens.get(0);
            if (token instanceof SimpleValueToken) {
                String value = token.getGuiString();
                if (value.startsWith(MINUS) && value.length() == 1) {
                    val = ConvValidationState.undecided;
                    return val;
                }
                try {
                    int n = Integer.parseInt(value);
                    if (n >= m_minVal && n <= m_maxVal) {
                        boolean valuesOK;
                        if (m_valuesSet != null) {
                            valuesOK = false;
                            for (int i = 0; i < m_valuesSet.length; i++) {
                                int candidate = m_valuesSet[i];
                                if (n == candidate) {
                                    valuesOK = true;
                                    break;
                                }                                
                            }
                        } else {
                            valuesOK = true;
                        }
                        if (valuesOK) {
                            val = ConvValidationState.valid;
                        } else {
                            val = ConvValidationState.invalid;
                            token.setErrorKey(MessageIDs.E_NOT_SUPP_COMBO_ITEM);
                        }
                    } else if (n > m_maxVal) {
                        val = ConvValidationState.invalid;
                        token.setErrorKey(MessageIDs.E_TOO_BIG_VALUE);
                    } else {
                        val = ConvValidationState.invalid;
                        token.setErrorKey(MessageIDs.E_TOO_SMALL_VALUE);
                    }
                } catch (NumberFormatException exc) {
                    val = ConvValidationState.invalid;
                    token.setErrorKey(MessageIDs.E_BAD_INT);
                }
            }
        } else {
            // no spaces in integers allowed
            val = ConvValidationState.invalid;
            if (tokens.size() != 0) {
                tokens.get(0).setErrorKey(MessageIDs.E_BAD_INT);
            }
        }

        return val;
    }
}

/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
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
import java.util.Locale;

/**
 * Creates tokens with our self defined sablecc lexer. There is only validation
 * if the String could be parsed into tokens. 
 * <br>
 * <b>This should only be used if you want to work with the tokens!</b>
 * 
 * @author BREDEX GmbH
 */
public class SimpleStringConverter extends ParamValueConverter {

    /**
     * actual state
     */
    private ConvValidationState m_actualState;

    /**
     * hint: the string could be null.
     * 
     * @param guiString
     *            to convert
     */
    public SimpleStringConverter(String guiString) {
        init(guiString);
    }

    /**
     * Returns the actual state
     * 
     * @return actualState
     */
    public ConvValidationState getState() {
        return m_actualState;
    }

    /**
     * @param guiString
     *            to convert
     */
    protected void init(String guiString) {
        setGuiString(guiString);
        createTokens();
    }

    /**
     * Controls the error list.
     * 
     * @return The state of the errors
     */
    public ConvValidationState getErrorStatus() {
        ConvValidationState val = ConvValidationState.valid;
        List<TokenError> errors = getErrors();
        if (errors.isEmpty()) {
            return val;
        }
        for (TokenError error : errors) {
            if (error.getValidationState() == ConvValidationState.invalid) {
                return ConvValidationState.invalid;
            } else if (error.getValidationState() 
                    == ConvValidationState.undecided) {
                val = ConvValidationState.undecided;
            }
        }
        return val;
    }

    /**
     * No validation of the tokens is done
     */
    public void validateSingleTokens() {
        // this class only generates tokens
    }

    /**
     * @param stack
     *            ignored
     * @param locale
     *            ignored
     * @return <code>null</code>
     */
    public String getExecutionString(List<ExecObject> stack, Locale locale) {
        return null;
    }
    
    /** {@inheritDoc} */
    public boolean isGUI() {
        return true;
    }
}

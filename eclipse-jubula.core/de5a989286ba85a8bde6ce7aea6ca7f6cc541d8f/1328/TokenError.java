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

import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * class to describe errors results from syntactical or semantical validation of
 * tokens
 *
 * @author BREDEX GmbH
 * @created 21.08.2007
 */
public class TokenError {
    /** entire string */
    private String m_input = StringConstants.EMPTY;

    /** I18N Key for error message */
    private Integer m_i18NErrorKey = null;

    /** validationState */
    private ConvValidationState m_convValidationState = 
        ConvValidationState.notSet;
    

    
    /**
     * constructor
     * @param input value to the token
     * @param i18NMessageId messageId for error message
     * @param state validationState
     */
    public TokenError(String input, 
            Integer i18NMessageId, ConvValidationState state) {
        m_input = input;
        m_i18NErrorKey = i18NMessageId;
        m_convValidationState = state;
    }  
    
    /**
     * @return Returns the i18NErrorKey.
     */
    public Integer getI18NErrorKey() {
        return m_i18NErrorKey;
    }

    /**
     * @return Returns the value.
     */
    public String getInput() {
        return m_input;
    }

    /**
     * @return Returns the validationState.
     */
    public ConvValidationState getValidationState() {
        return m_convValidationState;
    }


}

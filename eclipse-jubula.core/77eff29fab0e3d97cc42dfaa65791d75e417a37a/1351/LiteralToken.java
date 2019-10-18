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
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;


/**
 * token to represent a string starts end ends with a single quote
 */
public class LiteralToken extends AbstractParamValueToken {

    /**
     * @param s string represents the token
     * @param pos index of first character of token in entire string
     */
    public LiteralToken(String s, int pos) {
        super(s, pos, null);
    }

   
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#validate(org.eclipse.jubula.client.core.model.INodePO)
     */
    public ConvValidationState validate() {
        return ConvValidationState.notSet;
    }


    /**
     * validates, if this token must be internationalized
     * 
     * @return true, if the token needs consideration of locale
     */
    public boolean isI18Nrelevant() {
        return true;
    }

    /**
     * @return the current value for this token
     */
    public String getGuiString() {
        return getValue();
    }


    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getExecutionString(int, org.eclipse.jubula.client.core.utils.Traverser, java.util.Locale)
     */
    @SuppressWarnings("unused")
    public String getExecutionString(List<ExecObject> stack) 
            throws InvalidDataException {
        
        String execString = StringConstants.EMPTY;
        // remove quotes
        if (getValue() != null && getValue().length() > 1) {
            execString = getValue().substring(1, getValue().length() - 1);
        }
        return execString;
    }


    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getModelString()
     */
    public String getModelString() {
        return getValue();
    }

}

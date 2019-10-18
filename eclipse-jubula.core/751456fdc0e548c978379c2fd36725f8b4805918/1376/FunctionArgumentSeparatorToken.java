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
package org.eclipse.jubula.client.core.utils;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;

/**
 * Token that represents a separator in a list of Function arguments.
 */
public class FunctionArgumentSeparatorToken extends AbstractParamValueToken {

    /**
     * Constructor
     * 
     * @param s the entire token
     * @param pos index of first character of token in entire string
     * @param desc param description belonging to currently edited parameter value
     */
    public FunctionArgumentSeparatorToken(String s, int pos,
            IParamDescriptionPO desc) {
        super(s, pos, desc);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public ConvValidationState validate() {
        return ConvValidationState.valid;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getExecutionString(List<ExecObject> stack) {
        return StringUtils.EMPTY;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getGuiString() {
        return getValue();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getModelString() {
        return getValue();
    }

}

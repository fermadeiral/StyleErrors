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

import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created 14.08.2007
 */
public class VariableToken extends AbstractParamValueToken {
    
    /**
     * represents the actual name of the variable (without the additional 
     * pre- and post-fix information contained in <code>m_value</code>).
     */
    private String m_variableName = null;

    /**
     * @param s string represents the token
     * @param pos index of first character of token in entire string
     * @param variableName The name of the variable represented by the token
     *                     (without the pre- and post-fix text included in 
     *                     <code>s</code>).
     * @param desc param description belonging to currently edited parameter value
     */
    public VariableToken(String s, int pos, String variableName, 
            IParamDescriptionPO desc) {

        super(s, pos, desc);
        m_variableName = variableName;

    }

    /**
     * only runtime validation possible
     * {@inheritDoc}
     * @see IParamValueToken#validate(INodePO)
     */
    public ConvValidationState validate() {
        ConvValidationState state = ConvValidationState.notSet;
        if (getParamDescription() == null 
                || VARIABLE.equals(getParamDescription().getType())) {
            state = ConvValidationState.invalid;
            setErrorKey(MessageIDs.E_INVALID_VAR_NAME);
        }
        return state;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getValue()
     */
    public String getGuiString() {
        return getValue();
    }

    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getExecutionString(int, org.eclipse.jubula.client.core.utils.Traverser, java.util.Locale)
     */
    public String getExecutionString(List<ExecObject> stack) 
        throws InvalidDataException {
        String  resolvedVar = TestExecution.getInstance()
            .getVariableStore().getValue(m_variableName);
        if (resolvedVar == null) {
            throw new InvalidDataException(Messages.VariableWithName 
                + StringConstants.SPACE + getValue() 
                + StringConstants.SPACE + Messages.IsNotResolvable, 
                MessageIDs.E_UNRESOLV_VAR_ERROR);
        }
        return resolvedVar;
    }
    
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getModelString()
     */
    public String getModelString() {
        return getValue();
    }
    
    /**
     * Gets the variable without $ or braces
     * @return variable name
     */
    public String getVariableString() {
        return m_variableName;
    }

}

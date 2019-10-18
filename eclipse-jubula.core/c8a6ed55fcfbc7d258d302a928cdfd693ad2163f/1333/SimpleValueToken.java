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
public class SimpleValueToken extends AbstractParamValueToken {

    /** Starting symbol for variables */
    public static final char VARIABLE_START = '$';

    /** Starting symbol for parameters */
    public static final char PARAMETER_START = '=';

    /** Starting symbol for delimiter */
    public static final char DELIMITER_START = '{';

    /** Ending symbol for delimiter */
    public static final char DELIMITER_END = '}';

    /**
     * @param s string represents the token
     * @param pos index of first character of token in entire string
     * @param desc param description belonging to currently edited parameter value
     */
    public SimpleValueToken(String s, int pos, IParamDescriptionPO desc) {
        super(s, pos, desc);
    }


    /**
     * {@inheritDoc}
     * @see IParamValueToken#validate(INodePO)
     */
    public ConvValidationState validate() {
        ConvValidationState state = ConvValidationState.notSet;
        if (VARIABLE.equals(getParamDescription().getType())) {
            final String wordRegex = "[0-9a-z_A-Z]{1,}"; //$NON-NLS-1$
            if (Pattern.matches(wordRegex, getValue())) {
                state = ConvValidationState.valid;                
            } else {
                state = ConvValidationState.invalid;
                setErrorKey(MessageIDs.E_PARSE_NAME_ERROR);
            }
        }
        return state;
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
    public String getExecutionString(List<ExecObject> stack) 
        throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        do {
            char c = getValue().charAt(index);
            if (c == '\\') {
                if (index + 1 < getValue().length()) {
                    index++;
                    c = getValue().charAt(index);
                    char[] validChars = {'\\', PARAMETER_START,
                        DELIMITER_START, DELIMITER_END, VARIABLE_START, '\''};
                    boolean isValid = false;
                    for (char validChar : validChars) {
                        if (validChar == c) {
                            builder.append(c);
                            isValid = true; 
                            index++;
                            break;
                        }   
                    }
                    if (!isValid) {
                        StringBuilder msg = new StringBuilder();
                        msg.append(Messages.InvalidCharacter);
                        msg.append(StringConstants.SPACE);
                        msg.append(c);
                        msg.append(StringConstants.SPACE);
                        msg.append(Messages.AfterBackslashIn);
                        msg.append(StringConstants.SPACE);
                        msg.append(getValue());
                        throw new InvalidDataException(msg.toString(), 
                            MessageIDs.E_SYNTAX_ERROR);
                    }            
                } else {
                    throw new InvalidDataException(
                        Messages.NotAllowedToSetSingleBackslashIn 
                        + StringConstants.SPACE + getValue(), 
                        MessageIDs.E_SYNTAX_ERROR);
                }            
            } else {
                builder.append(c);
                index++;
            }
        } while (index < getValue().length());
        return builder.toString();
    }
    
    /** 
     * 
     * {@inheritDoc}
     */
    public String getModelString() {
        return getValue();
    }

}

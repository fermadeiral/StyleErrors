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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;


/**
 * @author BREDEX GmbH
 * @created 31.10.2007
 */
public class GuiParamValueConverter extends ParamValueConverter {

    /**
     * hint: the string could be null.
     * @param guiString to convert
     * @param currentNode node with parameter for this parameterValue
     *                      can be null, but in this case all node-related tokens will be invalid (currently RefToken)
     * @param desc param description associated with current string (parameter value)
     * @param validator to use for special validations
     */
    public GuiParamValueConverter(String guiString,
            IParameterInterfacePO currentNode,
            IParamDescriptionPO desc, IParamValueValidator validator) {
        super(currentNode, desc, validator);
        init(guiString);
    }
    
    /**
     * default constructor
     */
    protected GuiParamValueConverter() {
        // do nothing
    }

    /**
     * @param guiString to convert
     */
    protected void init(String guiString) {
        setGuiString(guiString);
        createTokens();
    }
    
    /**
     * 
     */
    void validateSingleTokens() {
        // validates each token
        for (IParamValueToken token : getTokens()) {
            ConvValidationState state = token.validate();
            createTokenError(state, token);
        }
        // validates whole expression
        if (!containsErrors()) {
            ConvValidationState state = 
                getValidator().validateInput(getTokens());
            for (IParamValueToken token : getTokens()) {
                Integer errorKey = token.getErrorKey();
                if (errorKey != null && state == ConvValidationState.invalid) {
                    createTokenError(state, token);
                    return;
                } else if (state == ConvValidationState.undecided) {
                    createTokenError(state, token);
                }
            } 
        }
    }
    

    /**
     * @param parent parent
     * @return list of parameter names to add
     */
    public Set<String> getParametersToAdd(ISpecTestCasePO parent) {
        List<String> newRefs = getNamesForReferences();
        // remove multiple entries
        Set<String> refs = new HashSet<String>(newRefs);
        if (!refs.isEmpty()) {
            for (IParamDescriptionPO desc : parent.getParameterList()) {
                refs.remove(desc.getName());
            }
        }
        return refs;
    }
    
    /**
     * @return model representation of string
     */
    public String getModelString() {
        // replace reference names with GUIDs
        if (super.getModelString() == null) {
            StringBuilder builder = new StringBuilder();
            for (IParamValueToken token : getTokens()) {
                String modelString = token.getModelString();
                if (modelString == null) {
                    return null;
                }
                builder.append(token.getModelString());
            }
            setModelString(builder.toString());
        }
        return super.getModelString();
    }
    
    /** {@inheritDoc} */
    public boolean isGUI() {
        return true;
    }
}

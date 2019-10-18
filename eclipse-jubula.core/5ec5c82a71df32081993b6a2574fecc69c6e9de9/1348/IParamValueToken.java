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
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;


/**
 * @author BREDEX GmbH
 * @created 14.08.2007
 */
public interface IParamValueToken {
    /**
     * validates the semantical correctness of this token
     * @return state of current token
     */
    public abstract ConvValidationState validate();

    /**
     * @return I18NKey for error message
     */
    public abstract Integer getErrorKey();
    
    /**
     * @param errorKey The errorKey to set
     */
    public abstract void setErrorKey(Integer errorKey);

    /**
     * get the real values for this token, e.g. for testexecution or
     * completeness check
     * 
     * @param stack Current execution stack. Value is ignored by
     *     Literal and SimpleValueToken!
     * @return the resolved token for given locale
     */
    public abstract String getExecutionString(List<ExecObject> stack) 
            throws InvalidDataException;

    /**
     * @return the current value in gui representation for this token
     */
    public abstract String getGuiString();
    
    /**
     * @return model representation of string
     */
    public String getModelString();
    
   
    /**
     * @return index of first character of tokenvalue in entire string
     */
    public abstract int getStartIndex();
    
    /**
     * @return index of last character of tokenvalue in entire string
     */
    public abstract int getEndIndex();
}
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


/**
 * @author BREDEX GmbH
 * @created 23.11.2007
 */
public interface IParamValueValidator {

    /**
     * @param tokens tokens to validate
     * @return validation state
     */
    public abstract ConvValidationState validateInput(
        List<IParamValueToken> tokens);

}
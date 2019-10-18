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
public class NullValidator implements IParamValueValidator {

    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueValidator#validateInput(java.util.List)
     */
    public ConvValidationState validateInput(List<IParamValueToken> tokens) {
        return ConvValidationState.valid;
    }

}

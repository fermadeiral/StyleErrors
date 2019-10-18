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

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;

/**
 * This is a default implementation of the IParamRefSymbolsProvider.
 * The symbols are read from TestDataConstants.
 *
 * @author BREDEX GmbH
 * @created Nov 27, 2006
 */
public class DefaultParamRefSymbolsProvider 
    implements IParamRefSymbolsProvider {

    /**
     * {@inheritDoc}
     */
    public String getRefSymbol() {
        return String.valueOf(TestDataConstants.REFERENCE_CHAR_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getEscSymbol() {
        return String.valueOf(TestDataConstants.ESCAPE_CHAR_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getFuncSymbol() {
        return String.valueOf(TestDataConstants.FUNCTION_CHAR_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getVarSymbol() {
        return String.valueOf(TestDataConstants.VARIABLE_CHAR_DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getEmptySymbol() {
        return StringConstants.EMPTY;
    }

}

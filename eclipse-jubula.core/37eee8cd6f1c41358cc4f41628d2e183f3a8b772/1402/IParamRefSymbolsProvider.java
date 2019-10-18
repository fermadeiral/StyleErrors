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

/**
 * This interface is implemented by classes that provide actual values
 * for the reference and escape symbols, e.g. by reading them from the
 * preferences pages of the Jubula Client.
 *
 * @author BREDEX GmbH
 * @created 01.09.2005
 */
public interface IParamRefSymbolsProvider {
    /**
     * @return The reference symbol, e.g. <code>=</code>
     */
    public String getRefSymbol();
    /**
     * @return The escape symbol, e.g. <code>\</code>
     */
    public String getEscSymbol();

    /**
     * @return The function symbol, e.g. <code>?</code>
     */
    public String getFuncSymbol();
    
    /**
     * @return The variable symbol, e.g. <code>$</code>
     */
    public String getVarSymbol();
    
    /**
     * @return The GUI-symbol for an empty string, e.g <code>\</code>
     */
    public String getEmptySymbol();

}

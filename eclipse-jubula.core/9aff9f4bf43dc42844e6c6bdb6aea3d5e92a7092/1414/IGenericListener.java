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

import org.eclipse.jubula.tools.internal.exception.UnexpectedGenericTypeException;


/**
 * @author BREDEX GmbH
 * @created 23.10.2006
 */
public interface IGenericListener {
    /**
     * generic callback method
     * @param params list of parameters
     */
    public void eventOccurred(List< ? extends Object> params);
    
    /**
     * Method to check the generic types of the list-elements of the callbackMethod.
     * @param params list of parameters
     * @throws UnexpectedGenericTypeException When another generic type was expected.
     */
    public void checkGenericListElementType(List< ? extends Object> params) 
        throws UnexpectedGenericTypeException;
}
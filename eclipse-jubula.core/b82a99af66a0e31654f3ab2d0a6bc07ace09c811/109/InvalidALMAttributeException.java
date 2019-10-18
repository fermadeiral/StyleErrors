/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.mylyn.exceptions;

/**
 * @author BREDEX GmbH
 */
public class InvalidALMAttributeException extends Exception {
    /** serialVersionUID */
    private static final long serialVersionUID = 8269674417361658951L;

    /**
     * Constructor
     * 
     * @param message
     *            the message
     */
    public InvalidALMAttributeException(String message) {
        super(message);
    }
}
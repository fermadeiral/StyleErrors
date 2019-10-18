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

import org.eclipse.jubula.tools.internal.exception.JBRuntimeException;

/**
 * Indicates that a semantic error was encountered during parsing.
 * 
 * @author BREDEX GmbH
 * @created 13.01.2011
 */
public class SemanticParsingException extends JBRuntimeException {

    /** index within the input string at which the error occurred */
    private int m_position;

    /**
     * Constructor
     * 
     * @param message The message.
     * @param id An ErrorMessage.ID.
     * @param position Index within the input string at which 
     *                 the error occurred.
     */
    public SemanticParsingException(String message, Integer id, int position) {
        super(message, id);
        m_position = position;
    }
    
    /**
     * 
     * @return the index within the input string at which the error occurred.
     */
    public int getPosition() {
        return m_position;
    }
    
}

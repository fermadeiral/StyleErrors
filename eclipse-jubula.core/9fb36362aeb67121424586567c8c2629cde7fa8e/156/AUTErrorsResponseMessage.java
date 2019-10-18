/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.communication.internal.message;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * Response message from the AUT to the client, containing errors and warnings
 * which occurred during the connection setup between client and AUT
 * 
 * @author BREDEX GmbH
 * @created 21.7.2015
 */
public class AUTErrorsResponseMessage extends Message {
    
    /** error list */
    private List<String> m_errors = new ArrayList<String>();
    
    /** warning list */
    private List<String> m_warnings = new ArrayList<String>();

    /** default constructor */
    public AUTErrorsResponseMessage() {
        super();
    }
    
    /**
     * 
     * @param errors list of errors 
     * @param warnings list of warnings
     */
    public AUTErrorsResponseMessage(List<String> errors,
            List<String> warnings) {
        m_errors = errors;
        m_warnings = warnings;
    }

    @Override
    public String getCommandClass() {
        return CommandConstants.AUT_ERRORS_RESPONSE_COMMAND;
    }
    
    /**
     * 
     * @return list of errors
     */
    public List<String> getErrors() {
        return m_errors;
    }
    
    /**
     * 
     * @return list of warnings
     */
    public List<String> getWarnings() {
        return m_warnings;
    }
    
}

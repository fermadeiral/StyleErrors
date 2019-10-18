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
package org.eclipse.jubula.autagent.common.commands;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.StartAUTServerStateMessage;
import org.eclipse.jubula.tools.internal.constants.AUTStartResponse;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * Implementors of this interface start an AUT.
 *
 * @author BREDEX GmbH
 * @created Jul 6, 2007
 * 
 */
public interface IStartAut {
    /** Default error message when the AUT cannot be started */
    public static final Message ERROR_MESSAGE = new StartAUTServerStateMessage(
        AUTStartResponse.ERROR, "Unexpected error, no detail available."); //$NON-NLS-1$
    
    /** <code>RC_DEBUG</code> */
    public static final String RC_DEBUG = System.getProperty("RC_DEBUG"); //$NON-NLS-1$

    /** <code>PATH_SEPARATOR</code> */
    public static final String PATH_SEPARATOR = System.getProperty("path.separator"); //$NON-NLS-1$
    
    /** <code>FILE_SEPARATOR</code> */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$
    
    /** Delimiter for key and value of properties (key=value) */
    public static final String PROPERTY_DELIMITER = 
        StringConstants.EQUALS_SIGN;
    
    /** Whitespace delimiter */
    public static final String WHITESPACE_DELIMITER = 
        StringConstants.SPACE;
    
    /** The separator used when composing the classpath in the AUT Configuration */
    public static final String CLIENT_PATH_SEPARATOR = 
        StringConstants.SEMICOLON;

    /**
     * Starts the AUT with the given parameters.
     * @param parameters The parameters for starting the AUT.
     * @return a <code>StartAutServerStateMessage</code> which either describes an error
     * condition or just tells the originator that the AUT was started correctly.
     * @throws IOException if an I/O error occurs.
     */
    public StartAUTServerStateMessage startAut(Map<String, String> parameters) 
        throws IOException;
}

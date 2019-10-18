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

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * Message from the client to the AUT, containing the demand for a list of
 * errors and warnings which happened during the connection setup on the AUT
 * side.
 * 
 * @author BREDEX GmbH
 * @created 21.7.2015
 */
public class AUTErrorsMessage extends Message {

    /** default constructor */
    public AUTErrorsMessage() {
        super();
    }
    
    @Override
    public String getCommandClass() {
        return CommandConstants.AUT_ERRORS_COMMAND;
    }

}

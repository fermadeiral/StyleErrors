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
package org.eclipse.jubula.communication.internal.message;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * This message is send from the JubulaClient to the AUTserver to start the AUT
 * (invoking the main-method()). <br>
 * It contains no further data. The response message is a
 * <code>AUTStateMessage</code> containing information about the state of the
 * AUT.
 * 
 * @author BREDEX GmbH
 * @created 23.07.2004
 */
public class AUTStartMessage extends Message {
    /**
     * Default constructor. Do nothing.
     */
    public AUTStartMessage() {
        super();
    }

    /**
     * Returns the name of the command class for this message
     * 
     * @return a <code>String</code> value
     */
    public String getCommandClass() {
        return CommandConstants.AUT_START_COMMAND;
    }
}
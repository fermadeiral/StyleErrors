/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
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
 * Message sent to AUT Server to retrieve the name of its assigned 
 * keyboard layout.
 * 
 * @author BREDEX GmbH
 * @created Aug 2, 2011
 */
public class GetKeyboardLayoutNameMessage extends Message {
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.GET_KEYBOARD_LAYOUT_NAME_COMMAND;
    }
}

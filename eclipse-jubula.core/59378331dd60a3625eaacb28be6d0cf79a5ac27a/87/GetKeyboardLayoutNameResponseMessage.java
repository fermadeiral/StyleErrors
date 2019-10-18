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
 * Response message sent to Client. Contains the name of the AUT's 
 * assigned keyboard layout.
 * 
 * @author BREDEX GmbH
 * @created Aug 2, 2011
 */
public class GetKeyboardLayoutNameResponseMessage extends Message {
    /** the name of the assigned keyboard layout */
    private String m_keyboardLayoutName = null;

    /**
     * No-args constructor for XStream.
     * 
     * @deprecated
     */
    public GetKeyboardLayoutNameResponseMessage() {
        // currently empty 
    }
    
    /**
     * Constructor
     * 
     * @param keyboardLayoutName The name of the assigned keyboard layout.
     */
    public GetKeyboardLayoutNameResponseMessage(String keyboardLayoutName) {
        m_keyboardLayoutName = keyboardLayoutName;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.GET_KEYBOARD_LAYOUT_NAME_RESPONSE_COMMAND;
    }

    /**
     * 
     * @return the keyboard layout name contained in this message.
     */
    public String getKeyboardLayoutName() {
        return m_keyboardLayoutName;
    }
}

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
package org.eclipse.jubula.client.ui.rcp.utils;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;

/**
 * Provides default options for content assist.
 *
 * @author BREDEX GmbH
 * @created Apr 7, 2010
 */
public final class ContentAssistUtil {

    /** default characters that will trigger content assist */
    private static char[] triggerChars;

    /** default keystroke that will trigger content assist */
    private static KeyStroke triggerKeyStroke;
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ContentAssistUtil() {
        // Nothing to initialize.
    }

    /**
     * 
     * @return a default set of characters that will trigger content assistance.
     *         The goal is to provide content assistance as the user types, so
     *         this can be summarized as [a-zA-Z0-9_BACKSPACE=$ ].
     */
    public static char[] getTriggerChars() {
        if (triggerChars == null) {
            triggerChars = new char[2 * 26 + 10 + 5];
            int index = 0;
            for (char c = 'a'; c <= 'z'; ++c) {
                triggerChars[index++] = c;
                triggerChars[index++] = Character.toUpperCase(c);
            }
            for (int i = 0; i < 10; ++i) {
                triggerChars[index++] = Character.forDigit(i, 10);
            }
            triggerChars[index++] = '_';
            triggerChars[index++] = '\b';
            triggerChars[index++] = '=';
            triggerChars[index++] = '$';
            triggerChars[index++] = ' ';
        }
        return triggerChars;
    }

    /**
     * 
     * @return the default keystroke for triggering content assist (Ctrl+SPACE). 
     */
    public static KeyStroke getTriggerKeyStroke() {
        if (triggerKeyStroke == null) {
            try {
                triggerKeyStroke = KeyStroke.getInstance("Ctrl+Space"); //$NON-NLS-1$
            } catch (ParseException e) {
                // ignore until you want to play with the constant above
            }
        }
        
        return triggerKeyStroke;
    }
}

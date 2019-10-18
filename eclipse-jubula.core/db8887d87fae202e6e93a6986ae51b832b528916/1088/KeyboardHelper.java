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
package org.eclipse.jubula.rc.swt.driver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.swt.SWT;


/**
 * @author BREDEX GmbH
 * @created 12.07.2007
 */
public class KeyboardHelper {
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        KeyboardHelper.class);

    /**
     * The delimiter (+).
     */
    private static final String DELIMITER = "+"; //$NON-NLS-1$
    
    /**
     * shift
     */
    private static final String SHIFT = "shift"; //$NON-NLS-1$
    
    /**
     * ctrl
     */
    private static final String CTRL = "ctrl"; //$NON-NLS-1$
    
    /**
     * alt
     */
    private static final String ALT = "alt"; //$NON-NLS-1$
    
    /** 
     * The keyboard mapping.
     * Key = character
     * Value = KeyStroke
     */
    private Map<Character, KeyStroke> m_mapping = 
        new HashMap<Character, KeyStroke>();

    /**
     * Constructor
     * 
     * @param layout The keyboard layout.
     */
    public KeyboardHelper(Properties layout) {
        initKeyboardMapping(layout);
    }
    
    /**
     * Inits keyboard mapping
     * @param layout The keyboard layout.
     */
    private void initKeyboardMapping(Properties layout) {
        final Iterator charsIter = layout.keySet().iterator();
        while (charsIter.hasNext()) {
            final String origCharStr = (String)charsIter.next();
            if (origCharStr.length() < 1) {
                final String msg = "Could not parse keyboard layout."; //$NON-NLS-1$
                log.error(msg, new RobotException(new IOException(msg)));
            }
            final char origChar = origCharStr.charAt(0);
            final String mapping = 
                ((String)layout.get(origCharStr)).toLowerCase();
            final StringTokenizer tok = new StringTokenizer(mapping, DELIMITER);
            final char nativeChar = mapping.charAt(mapping.length() - 1);
            final KeyStroke keyStroke = new KeyStroke(nativeChar);
            while (tok.hasMoreTokens()) {
                final String modifier = tok.nextToken();
                final int mod = getModifier(modifier);
                if (mod != 0) {
                    keyStroke.addModifier(mod);
                }
            }
            m_mapping.put(new Character(origChar), keyStroke);
        }
    }
    
    /**
     * 
     * @param modifier the modifier
     * @return the SWT constant of the given modifier or 0 if no SWT-modifier.
     */
    private int getModifier(String modifier) {
        int mod = 0;
        if (CTRL.equals(modifier)) {
            mod = SWT.CTRL;
        } else if (SHIFT.equals(modifier)) {
            mod = SWT.SHIFT;
        } else if (ALT.equals(modifier)) {
            mod = SWT.ALT;
        }
        return mod;
    }
    
    /**
     * 
     * @param character the charater
     * @return the KeyStroke for the given character.
     */
    public KeyStroke getKeyStroke(char character) {
        KeyStroke keyStroke = null;
        
        if (Character.isUpperCase(character)) {
            final char lowChar = Character.toLowerCase(character);
            keyStroke = new KeyStroke(lowChar);
            keyStroke.addModifier(SWT.SHIFT);
            return keyStroke;            
        }
        if (isSingleKey(character)) {
            return new KeyStroke(character);
        }
        keyStroke = m_mapping.get(new Character(character));
        
        // if no KeyStroke was found, return a KeyStroke with the given 
        // character and log in debug.
        // It is not necessarily an error, see method isSingleKey(char)!
        if (keyStroke == null && log.isDebugEnabled()) {
            log.debug("No keyboard-mapping found for character '"  //$NON-NLS-1$
                + String.valueOf(character) + "'!"); //$NON-NLS-1$
        }
        return keyStroke != null ? keyStroke : new KeyStroke(character);
    }



    /**
     * @param character a char value
     * @return true if the given character can be entered via single keyboard
     * key (without modifiers), false otherwise.
     */
    private boolean isSingleKey(char character) {
        // Some characters e.g. ,.-+# on de_DE-layout do not match here.
        // Is there a way to find it out?
        return Character.isLowerCase(character) 
            || Character.isWhitespace(character)
            || (Character.isDigit(character));
    }
    
    /**
     * KeyStroke
     * @author BREDEX GmbH
     * @created 13.07.2007
     */
    public static class KeyStroke {
        
        /** The character */
        private char m_char = '0';
        
        /** The List of modifiers */
        private List<Integer> m_modifiers = new ArrayList<Integer>(3);

        /**
         * Constructor.
         * @param character the character without the modifiers.
         */
        public KeyStroke(char character) {
            m_char = character;
        }
        
        /**
         * 
         * @return the character without modifiers.
         */
        public char getChar() {
            return m_char;
        }
        
        /**
         * 
         * @return the modifier
         */
        public Integer[] getModifiers() {
            return m_modifiers.toArray(
                new Integer[m_modifiers.size()]);
        }
        
        /**
         * Adds the given modifier to the KeyStroke.
         * @param modifier the modifier.
         */
        public void addModifier(int modifier) {
            m_modifiers.add(new Integer(modifier));
        }
    }
}
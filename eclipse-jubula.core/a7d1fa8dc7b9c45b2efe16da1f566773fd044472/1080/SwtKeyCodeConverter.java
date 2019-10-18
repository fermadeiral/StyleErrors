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
package org.eclipse.jubula.rc.swt.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.swt.SWT;


/**
 * @author BREDEX GmbH
 * @created 12.04.2007
 */
public class SwtKeyCodeConverter {

    /** The logging. */
    private static AutServerLogger log = 
        new AutServerLogger(SwtKeyCodeConverter.class);

    /** Map for translating keycodes from the ITE 
     * (see Reference Manual -> Application -> Key Stroke) to SWT key char
     * (see org.eclipse.swt.events.KeyEvent). Entities should be added to this 
     * map, and not KEY_MAP, if (and only if) the corresponding KeyEvent needs
     * the data in the character field rather than the keyCode field.
     * String <=> Character
     */
    private static final Map<String, Character> CHAR_MAP = 
        new HashMap<String, Character>();

    static {
        // Value entered by tester <=> SWT Key Code
        CHAR_MAP.put("SPACE", new Character(' '));  //$NON-NLS-1$
    }
    
    /** Map for translating keycodes from the ITE  
     * (see Reference Manual -> Application -> Key Stroke) to SWT key code
     * (see org.eclipse.swt.SWT).
     * String <=> Integer
     */
    private static final Map<String, Integer> KEY_MAP = 
        new HashMap<String, Integer>();
    
    static {
        // Value entered by tester <=> SWT Key Code
        KEY_MAP.put("ENTER", new Integer(SWT.CR));  //$NON-NLS-1$
        KEY_MAP.put("TAB", new Integer(SWT.TAB));  //$NON-NLS-1$
        KEY_MAP.put("ESCAPE", new Integer(SWT.ESC));  //$NON-NLS-1$
        KEY_MAP.put("BACK_SPACE", new Integer(SWT.BS));  //$NON-NLS-1$
        KEY_MAP.put("F1", new Integer(SWT.F1));  //$NON-NLS-1$
        KEY_MAP.put("F2", new Integer(SWT.F2));  //$NON-NLS-1$
        KEY_MAP.put("F3", new Integer(SWT.F3));  //$NON-NLS-1$
        KEY_MAP.put("F4", new Integer(SWT.F4));  //$NON-NLS-1$
        KEY_MAP.put("F5", new Integer(SWT.F5));  //$NON-NLS-1$
        KEY_MAP.put("F6", new Integer(SWT.F6));  //$NON-NLS-1$
        KEY_MAP.put("F7", new Integer(SWT.F7));  //$NON-NLS-1$
        KEY_MAP.put("F8", new Integer(SWT.F8));  //$NON-NLS-1$
        KEY_MAP.put("F9", new Integer(SWT.F9));  //$NON-NLS-1$
        KEY_MAP.put("F10", new Integer(SWT.F10));  //$NON-NLS-1$
        KEY_MAP.put("F11", new Integer(SWT.F11));  //$NON-NLS-1$
        KEY_MAP.put("F12", new Integer(SWT.F12));  //$NON-NLS-1$
        KEY_MAP.put("HOME", new Integer(SWT.HOME));  //$NON-NLS-1$
        KEY_MAP.put("END", new Integer(SWT.END));  //$NON-NLS-1$
        KEY_MAP.put("INSERT", new Integer(SWT.INSERT));  //$NON-NLS-1$
        KEY_MAP.put("DELETE", new Integer(SWT.DEL)); //$NON-NLS-1$
        KEY_MAP.put("PAGE_UP", new Integer(SWT.PAGE_UP));  //$NON-NLS-1$
        KEY_MAP.put("PAGE_DOWN", new Integer(SWT.PAGE_DOWN));  //$NON-NLS-1$
        KEY_MAP.put("DOWN", new Integer(SWT.ARROW_DOWN));  //$NON-NLS-1$
        KEY_MAP.put("UP", new Integer(SWT.ARROW_UP));  //$NON-NLS-1$
        KEY_MAP.put("LEFT", new Integer(SWT.ARROW_LEFT));  //$NON-NLS-1$
        KEY_MAP.put("RIGHT", new Integer(SWT.ARROW_RIGHT));  //$NON-NLS-1$
        KEY_MAP.put("NUMPAD_0", new Integer(SWT.KEYPAD_0));  //$NON-NLS-1$
        KEY_MAP.put("NUMPAD_1", new Integer(SWT.KEYPAD_1));  //$NON-NLS-1$
        KEY_MAP.put("NUMPAD_2", new Integer(SWT.KEYPAD_2));  //$NON-NLS-1$
        KEY_MAP.put("NUMPAD_3", new Integer(SWT.KEYPAD_3));  //$NON-NLS-1$
        KEY_MAP.put("NUMPAD_4", new Integer(SWT.KEYPAD_4));  //$NON-NLS-1$
        KEY_MAP.put("NUMPAD_5", new Integer(SWT.KEYPAD_5));  //$NON-NLS-1$
        KEY_MAP.put("NUMPAD_6", new Integer(SWT.KEYPAD_6));  //$NON-NLS-1$
        KEY_MAP.put("NUMPAD_7", new Integer(SWT.KEYPAD_7));  //$NON-NLS-1$
        KEY_MAP.put("NUMPAD_8", new Integer(SWT.KEYPAD_8));  //$NON-NLS-1$
        KEY_MAP.put("NUMPAD_9", new Integer(SWT.KEYPAD_9));  //$NON-NLS-1$
    }
    
    /** Map for translating modifiers from the ITE 
     * (see Reference Manual -> Application -> Key Stroke) to SWT key code
     * (see org.eclipse.swt.SWT).
     * String <=> Integer
     */
    private static final Map<String, Integer> MOD_MAP = 
        new HashMap<String, Integer>();
    
    static {
        MOD_MAP.put("SHIFT", new Integer(SWT.SHIFT));  //$NON-NLS-1$
        MOD_MAP.put("CTRL", new Integer(SWT.CTRL));  //$NON-NLS-1$
        MOD_MAP.put("ALT", new Integer(SWT.ALT));  //$NON-NLS-1$
    }
    
    
    /** Map for translating keycodes from SWT keyevents to the ITE 
     *  Integer <=> String
     */
    private static final Map<Integer, String> KEYCODE_MAP = 
        new HashMap<Integer, String>();

    static {
        int swtSpace = 32;
        // Value entered by tester <=> SWT Key Code
        KEYCODE_MAP.put(new Integer(SWT.CR), new String("ENTER"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_CR), new String("ENTER"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(swtSpace), new String("SPACE"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.BS), new String("BACK_SPACE"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.TAB), new String("TAB"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.ESC), new String("ESCAPE"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.END), new String("END"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.HOME), new String("HOME"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.INSERT), new String("INSERT"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.DEL), new String("DELETE")); //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.ARROW_UP), new String("UP"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.ARROW_DOWN), new String("DOWN"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.ARROW_LEFT), new String("LEFT"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.ARROW_RIGHT), new String("RIGHT"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.PAGE_UP), new String("PAGE_UP"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.PAGE_DOWN), new String("PAGE_DOWN"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F1), new String("F1"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F2), new String("F2"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F3), new String("F3"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F4), new String("F4"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F5), new String("F5"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F6), new String("F6"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F7), new String("F7"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F8), new String("F8"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F9), new String("F9"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F10), new String("F10"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F11), new String("F11"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.F12), new String("F12"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_0), new String("NUMPAD0"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_1), new String("NUMPAD1"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_2), new String("NUMPAD2"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_3), new String("NUMPAD3"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_4), new String("NUMPAD4"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_5), new String("NUMPAD5"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_6), new String("NUMPAD6"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_7), new String("NUMPAD7"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_8), new String("NUMPAD8"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_9), new String("NUMPAD9"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_ADD), new String("ADD"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_DIVIDE), new String("DIVIDE"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_MULTIPLY), new String("MULTIPLY"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_SUBTRACT), new String("SUBTRACT"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.KEYPAD_DECIMAL), new String("DECIMAL"));  //$NON-NLS-1$        
        
        KEYCODE_MAP.put(new Integer(SWT.NUM_LOCK), new String("NUM_LOCK"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.SCROLL_LOCK), new String("SCROLL_LOCK"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.PAUSE), new String("PAUSE"));  //$NON-NLS-1$
        //KEYCODE_MAP.put(new Integer(SWT.SEPARATOR), new String("SEPARATOR"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.PRINT_SCREEN), new String("PRINT_SCREEN"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(SWT.CAPS_LOCK), new String("CAPS_LOCK"));  //$NON-NLS-1$

    }
    
    /** Map for translating modifiers from swt keyevents to the ITE 
     *  Integer <=> String
     */
    private static final Map<Integer, String> MODIFIER_MAP = 
        new HashMap<Integer, String>();

    static {
        // Value entered by tester <=> SWT Key Code        
        MODIFIER_MAP.put(new Integer(SWT.SHIFT), new String("shift"));  //$NON-NLS-1$
        MODIFIER_MAP.put(new Integer(SWT.ALT), new String("alt"));  //$NON-NLS-1$
        MODIFIER_MAP.put(new Integer(SWT.CTRL), new String("control"));  //$NON-NLS-1$
        
        MODIFIER_MAP.put(new Integer(SWT.CTRL | SWT.SHIFT), new String("control shift"));  //$NON-NLS-1$
        MODIFIER_MAP.put(new Integer(SWT.CTRL | SWT.ALT), new String("control alt"));  //$NON-NLS-1$
    }


    /**
     * Private constructor
     */
    private SwtKeyCodeConverter() {
        // Utility class
    }

    /**
     * @param keyCodeName The name of a key code, e.g. <code>TAB</code> for a tabulator key code
     * @return The key code or <code>-1</code>, if the key code name doesn't exist in the <code>KEY_MAP</code>
     * @throws StepExecutionException If the key code name cannot be converted to a key code due to the reflection call
     */
    public static int getKeyCode(String keyCodeName) 
        throws StepExecutionException {
        
        int code = KEY_MAP.containsKey(keyCodeName) 
            ? KEY_MAP.get(keyCodeName).intValue() : -1;

        if (code == -1 && log.isInfoEnabled()) {
            log.info("The key expression '" + keyCodeName //$NON-NLS-1$
                + "' is not a key code. Returning -1."); //$NON-NLS-1$
        }
        
        return code;
    }
    
    /**
     * @param modifierName The name of a modifier, e.g. SHIFT
     * @return The key code or <code>-1</code>, if the modifier name doesn't exist in the <code>MOD_MAP</code>
     * @throws StepExecutionException If the modifier name cannot be converted to a key code due to the reflection call
     */
    public static int getModifierCode(String modifierName) 
        throws StepExecutionException {
        
        int code = MOD_MAP.containsKey(modifierName) 
            ? MOD_MAP.get(modifierName).intValue() : -1;

        if (code == -1 && log.isInfoEnabled()) {
            log.info("The key expression '" + modifierName //$NON-NLS-1$
                + "' is not a modifier. Returning -1."); //$NON-NLS-1$
        }        
        return code;
    }
    
    /**
     * @param keycode keycode of keyevent
     * @return The keyname or <code>null</code>, if the keycode doesn't exist in the <code>KEYCODE_MAP</code>
     * @throws StepExecutionException If the key code cannot be converted to a keycode name due to the reflection call
     */
    public static String getKeyName(int keycode)
        throws StepExecutionException {

        String keyname = KEYCODE_MAP.containsKey(new Integer(keycode)) 
            ? (String)KEYCODE_MAP.get(new Integer(keycode)) : null;

        if (keyname == null && log.isInfoEnabled()) {
            log.info("The keycode '" + keycode //$NON-NLS-1$
                + "' is not a key expression. Returning null."); //$NON-NLS-1$
        }
            
        return keyname;
    }
    
    /**
     * @param modifier modifier of keyevent
     * @return The modifier or <code>null</code>, if the modifier doesn't exist in the <code>MODIFIER_MAP</code>
     * @throws StepExecutionException If the modifier cannot be converted to a modifier name due to the reflection call
     */
    public static String getModifierName(int modifier)
        throws StepExecutionException {

        String modname = MODIFIER_MAP.containsKey(new Integer(modifier)) 
            ? (String)MODIFIER_MAP.get(new Integer(modifier)) : null;

        if (modname == null && log.isInfoEnabled()) {
            log.info("The keycode '" + modifier //$NON-NLS-1$
                + "' is not a key expression. Returning null."); //$NON-NLS-1$
        }
            
        return modname;
    }

    /**
     * @param keyCharName The name of a key character, e.g. <code>SPACE</code> 
     *                    for <code>' '</code>.
     * @return The key character, or the first character of the given
     *         <code>keyCodeName</code> if the key code name doesn't exist in 
     *         the <code>CHAR_MAP</code>
     */
    public static Character getKeyChar(String keyCharName) 
        throws StepExecutionException {
        
        Character character = CHAR_MAP.containsKey(keyCharName) 
            ? ((Character)CHAR_MAP.get(keyCharName)) 
                    : new Character(keyCharName.charAt(0));

        return character;
    }

}

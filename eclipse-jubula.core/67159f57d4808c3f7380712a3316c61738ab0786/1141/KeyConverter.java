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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.eclipse.swt.SWT;

/**
 * Converts SWT key codes in Swing key codes and the other way round.
 * 
 * @author BREDEX GmbH
 * @created 08.05.2006
 */
public class KeyConverter {
    
    /**
     * private utility constructor
     */
    private KeyConverter() {
        // emtpy
    }
    
    /**
     * @param stateMask The swt key down state mask.
     * @return swing state mask.
     */
    public static int convertSwtStateMask(int stateMask) {
        if (stateMask == SWT.CTRL) {
            return InputEvent.CTRL_DOWN_MASK;
        } else  if (stateMask == (SWT.CTRL | SWT.SHIFT)) {
            return InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
        } else if (stateMask == (SWT.CTRL | SWT.ALT)) {
            return InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK;
        } else if (stateMask == SWT.ALT) {
            return InputEvent.ALT_DOWN_MASK;
        } else if (stateMask == (SWT.ALT | SWT.SHIFT)) {
            return InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
        }
        return -1;
    }
    
    /**
     * @param stateMask The swing key down state mask.
     * @return a swt state mask.
     */
    public static int convertSwingStateMask(int stateMask) {
        if (stateMask == InputEvent.CTRL_DOWN_MASK) {
            return SWT.CTRL;
        } else  if (stateMask 
                == (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) {
            return SWT.CTRL | SWT.SHIFT;
        } else if (stateMask
                == (InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK)) {
            return SWT.CTRL | SWT.ALT;
        } else if (stateMask == InputEvent.ALT_DOWN_MASK) {
            return SWT.ALT;
        } else if (stateMask
                == (InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) {
            return SWT.ALT | SWT.SHIFT;
        } else if (stateMask == 0) {
            return 0; // no modifier
        }
        return -1;
    }

    /**
     * @param character SWT key event character
     * @return swing character
     */
    static int swtCharacter(char character) {
        switch (character) {
            case SWT.BS:
                return KeyEvent.VK_BACK_SPACE;
            case SWT.CR:
                return KeyEvent.VK_ENTER;
            case SWT.DEL:
                return KeyEvent.VK_DELETE;
            case SWT.ESC:
                return KeyEvent.VK_ESCAPE;
            case SWT.TAB:
                return KeyEvent.VK_TAB;
            default:
                return character; 
        }
    }
    
    /**
     * @param keyCode the swt key code
     * @return swing key code 
     */
    static int swtKeyboardMasks(int keyCode) {
        switch (keyCode) {
            /* Keyboard Masks */
            case SWT.ALT:
                return KeyEvent.VK_ALT;
            case SWT.SHIFT:
                return KeyEvent.VK_SHIFT;
            case SWT.CONTROL:
                return KeyEvent.VK_CONTROL;
            case SWT.COMMAND:
                return KeyEvent.VK_WINDOWS;
            default:
                return swtNonNumericKeyPadKeys(keyCode);
        }            
    }
    
    /**
     * @param keyCode the swt key code
     * @return swing key code
     */
    static int swtNonNumericKeyPadKeys(int keyCode) {
        switch (keyCode) {
            /* Non-Numeric Keypad Keys */
            case SWT.ARROW_UP:
                return KeyEvent.VK_UP;
            case SWT.ARROW_DOWN:
                return KeyEvent.VK_DOWN;
            case SWT.ARROW_LEFT:
                return KeyEvent.VK_LEFT;
            case SWT.ARROW_RIGHT:
                return KeyEvent.VK_RIGHT;
            case SWT.PAGE_UP:
                return KeyEvent.VK_PAGE_UP;
            case SWT.PAGE_DOWN:
                return KeyEvent.VK_PAGE_DOWN;
            case SWT.HOME:
                return KeyEvent.VK_HOME;
            case SWT.END:
                return KeyEvent.VK_END;
            case SWT.INSERT:
                return KeyEvent.VK_INSERT;
            default:
                return swtVirtualAndAsciiKeys(keyCode);
        }
    }
    
    /**
     * @param keyCode the swt key code
     * @return swing key code
     */
    static int swtVirtualAndAsciiKeys(int keyCode) {
        switch (keyCode) {
            /* Virtual and Ascii Keys */
            case SWT.BS:
                return KeyEvent.VK_BACK_SPACE;
            case SWT.CR:
                return KeyEvent.VK_ENTER;
            case SWT.DEL:
                return KeyEvent.VK_DELETE;
            case SWT.ESC:
                return KeyEvent.VK_ESCAPE;
            case SWT.TAB:
                return KeyEvent.VK_TAB;
            default:
                return swtFunctionKeys(keyCode);
        }
    }
    
    /**
     * @param keyCode the swt key code
     * @return swing key code
     */
    static int swtFunctionKeys(int keyCode) {
        switch (keyCode) {
            /* Functions Keys */
            case SWT.F1:
                return KeyEvent.VK_F1;
            case SWT.F2:
                return KeyEvent.VK_F2;
            case SWT.F3:
                return KeyEvent.VK_F3;
            case SWT.F4:
                return KeyEvent.VK_F4;
            case SWT.F5:
                return KeyEvent.VK_F5;
            case SWT.F6:
                return KeyEvent.VK_F6;
            case SWT.F7:
                return KeyEvent.VK_F7;
            case SWT.F8:
                return KeyEvent.VK_F8;
            case SWT.F9:
                return KeyEvent.VK_F9;
            case SWT.F10:
                return KeyEvent.VK_F10;
            case SWT.F11:
                return KeyEvent.VK_F11;
            case SWT.F12:
                return KeyEvent.VK_F12;
            case SWT.F13:
                return KeyEvent.VK_F13;
            case SWT.F14:
                return KeyEvent.VK_F14;
            case SWT.F15:
                return KeyEvent.VK_F15;
            default:
                return swtNumericKeyPadKeys(keyCode);
        }
    }
    
    /**
     * @param keyCode the swt key code
     * @return swing key code
     */
    static int swtNumericKeyPadKeys(int keyCode) {
        switch (keyCode) {
            /* Numeric Keypad Keys */
            case SWT.KEYPAD_ADD:
                return KeyEvent.VK_ADD;
            case SWT.KEYPAD_SUBTRACT:
                return KeyEvent.VK_SUBTRACT;
            case SWT.KEYPAD_MULTIPLY:
                return KeyEvent.VK_MULTIPLY;
            case SWT.KEYPAD_DIVIDE:
                return KeyEvent.VK_DIVIDE;
            case SWT.KEYPAD_DECIMAL:
                return KeyEvent.VK_DECIMAL;
            case SWT.KEYPAD_CR:
                return KeyEvent.VK_ENTER;
            case SWT.KEYPAD_0:
                return KeyEvent.VK_NUMPAD0;
            case SWT.KEYPAD_1:
                return KeyEvent.VK_NUMPAD1;
            case SWT.KEYPAD_2:
                return KeyEvent.VK_NUMPAD2;
            case SWT.KEYPAD_3:
                return KeyEvent.VK_NUMPAD3;
            case SWT.KEYPAD_4:
                return KeyEvent.VK_NUMPAD4;
            case SWT.KEYPAD_5:
                return KeyEvent.VK_NUMPAD5;
            case SWT.KEYPAD_6:
                return KeyEvent.VK_NUMPAD6;
            case SWT.KEYPAD_7:
                return KeyEvent.VK_NUMPAD7;
            case SWT.KEYPAD_8:
                return KeyEvent.VK_NUMPAD8;
            case SWT.KEYPAD_9:
                return KeyEvent.VK_NUMPAD9;
            case SWT.KEYPAD_EQUAL:
                return KeyEvent.VK_EQUALS;
            default:
                return swtOtherKeys(keyCode);
        }
    }
    
    /**
     * @param keyCode the swt key code
     * @return swing key code 
     */
    static int swtOtherKeys(int keyCode) {
        switch (keyCode) {
            /* Other keys */
            case SWT.CAPS_LOCK:
                return KeyEvent.VK_CAPS_LOCK;
            case SWT.NUM_LOCK:
                return KeyEvent.VK_NUM_LOCK;
            case SWT.SCROLL_LOCK:
                return KeyEvent.VK_SCROLL_LOCK;
            case SWT.PAUSE:
                return KeyEvent.VK_PAUSE;
            case SWT.BREAK:
                return KeyEvent.VK_STOP;
            case SWT.PRINT_SCREEN:
                return KeyEvent.VK_PRINTSCREEN;
            case SWT.HELP:
                return KeyEvent.VK_HELP;
            case 32:
                return KeyEvent.VK_SPACE;
            default:
                return swtCharacter((char)keyCode);
        }
    }
    
    /**
     * @param character swing key event character
     * @return swt character
     */
    static int swingCharacter(char character) {
        switch (character) {
            case KeyEvent.VK_BACK_SPACE:
                return SWT.BS;
            case KeyEvent.VK_ENTER:
                return SWT.CR;
            case KeyEvent.VK_DELETE:
                return SWT.DEL;
            case KeyEvent.VK_ESCAPE:
                return SWT.ESC;
            case KeyEvent.VK_TAB:
                return SWT.TAB;
            default:
                return character; 
        }
    }
    
    /**
     * @param keyCode the swing key code
     * @return swt key code 
     */
    static int swingKeyboardMasks(int keyCode) {
        switch (keyCode) {
            /* Keyboard Masks */
            case KeyEvent.VK_ALT:
                return SWT.ALT;
            case KeyEvent.VK_SHIFT:
                return SWT.SHIFT;
            case KeyEvent.VK_CONTROL:
                return SWT.CONTROL;
            case KeyEvent.VK_WINDOWS:
                return SWT.COMMAND;
            default:
                return swingNonNumericKeyPadKeys(keyCode);
        }            
    }
    
    /**
     * @param keyCode the swing key code
     * @return swt key code
     */
    static int swingNonNumericKeyPadKeys(int keyCode) {
        switch (keyCode) {
            /* Non-Numeric Keypad Keys */
            case KeyEvent.VK_UP:
                return SWT.ARROW_UP;
            case KeyEvent.VK_DOWN:
                return SWT.ARROW_DOWN;
            case KeyEvent.VK_LEFT:
                return SWT.ARROW_LEFT;
            case KeyEvent.VK_RIGHT:
                return SWT.ARROW_RIGHT;
            case KeyEvent.VK_PAGE_UP:
                return SWT.PAGE_UP;
            case KeyEvent.VK_PAGE_DOWN:
                return SWT.PAGE_DOWN;
            case KeyEvent.VK_HOME:
                return SWT.HOME;
            case KeyEvent.VK_END:
                return SWT.END;
            case KeyEvent.VK_INSERT:
                return SWT.INSERT;
            default:
                return swingVirtualAndAsciiKeys(keyCode);
        }
    }
    
    /**
     * @param keyCode the swing key code
     * @return swt key code
     */
    static int swingVirtualAndAsciiKeys(int keyCode) {
        switch (keyCode) {
            /* Virtual and Ascii Keys */
            case KeyEvent.VK_BACK_SPACE:
                return SWT.BS;
            case KeyEvent.VK_ENTER:
                return SWT.CR;
            case KeyEvent.VK_DELETE:
                return SWT.DEL;
            case KeyEvent.VK_ESCAPE:
                return SWT.ESC;
            case KeyEvent.VK_TAB:
                return SWT.TAB;
            default:
                return swingFunctionKeys(keyCode);
        }
    }
    
    /**
     * @param keyCode the swing key code
     * @return swt key code
     */
    static int swingFunctionKeys(int keyCode) {
        switch (keyCode) {
            /* Functions Keys */
            case KeyEvent.VK_F1:
                return SWT.F1;
            case KeyEvent.VK_F2:
                return SWT.F2;
            case KeyEvent.VK_F3:
                return SWT.F3;
            case KeyEvent.VK_F4:
                return SWT.F4;
            case KeyEvent.VK_F5:
                return SWT.F5;
            case KeyEvent.VK_F6:
                return SWT.F6;
            case KeyEvent.VK_F7:
                return SWT.F7;
            case KeyEvent.VK_F8:
                return SWT.F8;
            case KeyEvent.VK_F9:
                return SWT.F9;
            case KeyEvent.VK_F10:
                return SWT.F10;
            case KeyEvent.VK_F11:
                return SWT.F11;
            case KeyEvent.VK_F12:
                return SWT.F12;
            case KeyEvent.VK_F13:
                return SWT.F13;
            case KeyEvent.VK_F14:
                return SWT.F14;
            case KeyEvent.VK_F15:
                return SWT.F15;
            default:
                return swingNumericKeyPadKeys(keyCode);
        }
    }
    
    /**
     * @param keyCode the swing key code
     * @return swt key code
     */
    static int swingNumericKeyPadKeys(int keyCode) {
        switch (keyCode) {
            /* Numeric Keypad Keys */
            case KeyEvent.VK_ADD:
                return SWT.KEYPAD_ADD;
            case KeyEvent.VK_SUBTRACT:
                return SWT.KEYPAD_SUBTRACT;
            case KeyEvent.VK_MULTIPLY:
                return SWT.KEYPAD_MULTIPLY;
            case KeyEvent.VK_DIVIDE:
                return SWT.KEYPAD_DIVIDE;
            case KeyEvent.VK_DECIMAL:
                return SWT.KEYPAD_DECIMAL;
            case KeyEvent.VK_ENTER:
                return SWT.KEYPAD_CR;
            case KeyEvent.VK_NUMPAD0:
                return SWT.KEYPAD_0;
            case KeyEvent.VK_NUMPAD1:
                return SWT.KEYPAD_1;
            case KeyEvent.VK_NUMPAD2:
                return SWT.KEYPAD_2;
            case KeyEvent.VK_NUMPAD3:
                return SWT.KEYPAD_3;
            case KeyEvent.VK_NUMPAD4:
                return SWT.KEYPAD_4;
            case KeyEvent.VK_NUMPAD5:
                return SWT.KEYPAD_5;
            case KeyEvent.VK_NUMPAD6:
                return SWT.KEYPAD_6;
            case KeyEvent.VK_NUMPAD7:
                return SWT.KEYPAD_7;
            case KeyEvent.VK_NUMPAD8:
                return SWT.KEYPAD_8;
            case KeyEvent.VK_NUMPAD9:
                return SWT.KEYPAD_9;
            case KeyEvent.VK_EQUALS:
                return SWT.KEYPAD_EQUAL;
            default:
                return swingOtherKeys(keyCode);
        }
    }
    
    /**
     * @param keyCode the swing key code
     * @return swt key code 
     */
    static int swingOtherKeys(int keyCode) {
        switch (keyCode) {
            /* Other keys */
            case KeyEvent.VK_CAPS_LOCK:
                return SWT.CAPS_LOCK;
            case KeyEvent.VK_NUM_LOCK:
                return SWT.NUM_LOCK;
            case KeyEvent.VK_SCROLL_LOCK:
                return SWT.SCROLL_LOCK;
            case KeyEvent.VK_PAUSE:
                return SWT.PAUSE;
            case KeyEvent.VK_STOP:
                return SWT.BREAK;
            case KeyEvent.VK_PRINTSCREEN:
                return SWT.PRINT_SCREEN;
            case KeyEvent.VK_HELP:
                return SWT.HELP;
            case KeyEvent.VK_SPACE:
                return 32;
            default:
                return swingCharacter((char)keyCode);
        }
    }

    /**
     * @param keyCode the swing key code
     * @return swt key code
     */
    public static int convertSwingToSwt(int keyCode) {
        return swingKeyboardMasks(keyCode);
    }

    /**
     * @param keyCode the swt key code
     * @return swing key code
     */
    public static int convertSwtToSwing(int keyCode) {
        return swtKeyboardMasks(keyCode);
    }
}
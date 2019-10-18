/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.listener;

import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;

/**
 * This class is responsible for accepting an InputEvent as the (combination of
 * the) key(s)/mouse button for picking a component in the OBJECT_MAPPING mode. <br>
 *
 * @author BREDEX GmbH
 * @created 18.10.2013
 */
public class KeyAcceptor {

    /**
     * <code>MAPPING_KEY_COMB</code>
     */
    public static final int MAPPING_KEY_COMB = 1;

    /**
     * <code>CHECKMODE_KEY_COMB</code>
     */
    public static final int CHECKMODE_KEY_COMB = 2;

    /**
     * <code>CHECKCOMP_KEY_COMB</code>
     */
    public static final int CHECKCOMP_KEY_COMB = 3;
    
    /**
     * <code>MAPPING_KEY_COMB</code>
     */
    public static final int MAPPING_WITH_PARENTS_KEY_COMB = 4;

    /**
     * private Constructor
     */
    private KeyAcceptor() {
        // private Constructor
    }

    /**
     * The method queried by the <code>MappingListener</code> or
     * <code>RecordListener</code>.
     *
     * @param event
     *            the occurred key event
     * @return true if the combination Ctrl+Alt+<a> or Ctrl+Shift+<a> was
     *         pressed, false otherwise
     */
    public static int accept(InputEvent event) {
        int keyMod = 0;

        int inputCode = 0;
        if (event instanceof KeyEvent) {
            KeyEvent kEvent = (KeyEvent) event;

            inputCode = getAWTKeyCode(kEvent);

            if (kEvent.isAltDown()) {
                keyMod = keyMod | 1 << 9;
            }
            if (kEvent.isShiftDown()) {
                keyMod = keyMod | 1 << 6;
            }
            if (kEvent.isControlDown()) {
                keyMod = keyMod | 1 << 7;
            }
        } else if (event instanceof MouseEvent) {
            MouseEvent mEvent = (MouseEvent) event;
            MouseButton button = mEvent.getButton();

            switch (button) {
                case PRIMARY:
                    inputCode = InteractionMode.primary.rcIntValue();
                    break;
                case MIDDLE:
                    inputCode = InteractionMode.tertiary.rcIntValue();
                    break;
                case SECONDARY:
                    inputCode = InteractionMode.secondary.rcIntValue();
                    break;
                default:
                    break;
            }
        }
        if (inputCode == AUTServerConfiguration.getInstance().getMappingKey()
                || inputCode == AUTServerConfiguration.getInstance()
                        .getMappingMouseButton()
                && keyMod == AUTServerConfiguration.getInstance()
                    .getMappingKeyMod()) {
            return MAPPING_KEY_COMB;
        }
        if (inputCode == AUTServerConfiguration.getInstance().getCheckModeKey()
                && keyMod == AUTServerConfiguration.getInstance()
                        .getCheckModeKeyMod()) {
            return CHECKMODE_KEY_COMB;
        }
        if (inputCode == AUTServerConfiguration.getInstance().getCheckCompKey()
                && keyMod == AUTServerConfiguration.getInstance()
                        .getCheckCompKeyMod()) {
            return CHECKCOMP_KEY_COMB;
        }
        if (inputCode == AUTServerConfiguration.getInstance()
                .getMappingWithParentsKey()
                || inputCode == AUTServerConfiguration.getInstance()
                        .getMappingWithParentsMouseButton()
                && keyMod == AUTServerConfiguration.getInstance()
                    .getMappingWithParentsKeyMod()) {
            return MAPPING_WITH_PARENTS_KEY_COMB;
        }
        return 0;
    }

    /**
     *
     * @param event
     *            the event
     * @return the awt key code
     */
    public static int getAWTKeyCode(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code.isLetterKey()) {
            return java.awt.event.KeyEvent.getExtendedKeyCodeForChar(code
                    .getName().charAt(0));
        } else if (code.isFunctionKey()) {
            switch (code) {
                case F1:
                    return java.awt.event.KeyEvent.VK_F1;
                case F2:
                    return java.awt.event.KeyEvent.VK_F2;
                case F3:
                    return java.awt.event.KeyEvent.VK_F3;
                case F4:
                    return java.awt.event.KeyEvent.VK_F4;
                case F5:
                    return java.awt.event.KeyEvent.VK_F5;
                case F6:
                    return java.awt.event.KeyEvent.VK_F6;
                case F7:
                    return java.awt.event.KeyEvent.VK_F7;
                case F8:
                    return java.awt.event.KeyEvent.VK_F8;
                case F9:
                    return java.awt.event.KeyEvent.VK_F9;
                case F10:
                    return java.awt.event.KeyEvent.VK_F10;
                case F11:
                    return java.awt.event.KeyEvent.VK_F1;
                case F12:
                    return java.awt.event.KeyEvent.VK_F12;
                default:
                    // can not happen
                    break;
            }
        }
        // NUMPAD currently doesn't differ from other keys
        return 0;
    }
}

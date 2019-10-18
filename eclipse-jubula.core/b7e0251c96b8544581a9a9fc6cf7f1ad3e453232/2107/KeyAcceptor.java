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
package org.eclipse.jubula.rc.swing.listener;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.SortedSet;

import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;


/**
 * This class is responsible for accepting a KeyEvent as the (combination of
 * the) key(s) for picking a component in the OBJECT_MAPPING mode. <br>
 * 
 * This implementation accepts <Ctrl>+<Alt>+ <a>.
 * 
 * @author BREDEX GmbH
 * @created 24.08.2004
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
     * The method queried by the <code>MappingListener</code> 
     * or <code>RecordListener</code>.
     * 
     * @param event
     *            the occurred key event
     * @return true if the combination Ctrl+Alt+<a> or Ctrl+Shift+<a> was pressed, false
     *         otherwise
     */
    public int accept(InputEvent event) {
        int keyMod = 0;
        if (event.isAltDown()) {
            keyMod = keyMod | 1 << 9;
        }
        if (event.isShiftDown()) {
            keyMod = keyMod | 1 << 6;
        }
        if (event.isControlDown()) {
            keyMod = keyMod | 1 << 7;
        }
        if (event.isAltGraphDown()) {
            keyMod = keyMod | 1 << 13;
        }
        int inputCode = 0;
        if (event instanceof KeyEvent) {
            inputCode = ((KeyEvent)event).getKeyCode();
        } else if (event instanceof MouseEvent) {
            int button = ((MouseEvent)event).getButton();
            switch (button) {
                case MouseEvent.BUTTON1:
                    inputCode = InteractionMode.primary.rcIntValue();
                    break;
                case MouseEvent.BUTTON2:
                    inputCode = InteractionMode.tertiary.rcIntValue();
                    break;
                case MouseEvent.BUTTON3:
                    inputCode = InteractionMode.secondary.rcIntValue();
                    break;
                default:
                    break;
            }
        }
        if ((inputCode 
                == AUTServerConfiguration.getInstance().getMappingKey()
                || inputCode 
                    == AUTServerConfiguration.getInstance()
                        .getMappingMouseButton()) 
            && keyMod 
                == AUTServerConfiguration.getInstance().getMappingKeyMod()) {
            return MAPPING_KEY_COMB;            
        }
        if (inputCode 
                == AUTServerConfiguration.getInstance().getCheckModeKey()
            && keyMod 
                == AUTServerConfiguration.getInstance().getCheckModeKeyMod()) {
            return CHECKMODE_KEY_COMB;            
        }
        if (inputCode 
                == AUTServerConfiguration.getInstance().getCheckCompKey()
            && keyMod 
                == AUTServerConfiguration.getInstance().getCheckCompKeyMod()) {
            return CHECKCOMP_KEY_COMB;            
        }
        return 0;
    }
    
    /**
     * @param event the occurred key event
     * @return true if the pressed key combination is a singleLineTrigger, false
     *         otherwise
     */
    public boolean isSingleLineTrigger(KeyEvent event) {
        boolean isSingleTrigger = false;
        int keycode = event.getKeyCode();
        int modifier = event.getModifiers();
        SortedSet singleTrigger = AUTServerConfiguration.getInstance()
            .getSingleLineTrigger();
        isSingleTrigger = isTrigger(singleTrigger, keycode, modifier);
        return isSingleTrigger;
    }
    
    /**
     * @param event the occurred key event
     * @return true if the pressed key combination is a multiLineTrigger, false
     *         otherwise
     */
    public boolean isMultiLineTrigger(KeyEvent event) {
        boolean isMultiTrigger = false;
        int keycode = event.getKeyCode();
        int modifier = event.getModifiers();
        SortedSet multiTrigger = AUTServerConfiguration.getInstance()
            .getMultiLineTrigger();
        isMultiTrigger = isTrigger(multiTrigger, keycode, modifier);
        return isMultiTrigger;
    }
    
    /**
     * @param triggerSet set of Triggers
     * @param key int
     * @param modifiers int
     * @return true if the pressed key combination is a trigger, false
     *         otherwise
     */
    public boolean isTrigger (SortedSet triggerSet, int key, int modifiers) {
        boolean isTrigger = false;
        int triggerCode = 0;
        int triggerModMask = 0;
        Iterator it = triggerSet.iterator();
        while (it.hasNext()) {
            String trigger = it.next().toString().toUpperCase();
            if (trigger.indexOf("+") != -1) { //$NON-NLS-1$
                String[] keys = trigger.split("\\+"); //$NON-NLS-1$
                switch (keys.length) {
                    case 2:
                        triggerModMask = getModifierMask(keys[0]);
                        triggerCode = getKeyCode(keys[1]);
                        break;
                    case 3:
                        triggerModMask = getModifierMask(keys[0]) 
                            | getModifierMask(keys[1]);
                        triggerCode = getKeyCode(keys[2]);
                        break;
                    case 4:
                        triggerModMask = getModifierMask(keys[0]) 
                            | getModifierMask(keys[1])
                            | getModifierMask(keys[2]);
                        triggerCode = getKeyCode(keys[3]);
                        break;
                    default:
                }
            } else {
                triggerModMask = 0;
                triggerCode = getKeyCode(trigger);  
            }
            
            if (key == triggerCode
                    && modifiers == triggerModMask) {
                isTrigger = true;
            }
        }
        return isTrigger;
    }
        
    /**
     * @param keyCodeName
     *            The name of a key code, e.g. <code>TAB</code> for a
     *            tabulator key code
     * @return The key code or <code>-1</code>, if the key code name doesn't
     *         exist in the <code>KeyEvent</code> class
     *             If the key code name cannot be converted to a key code due to
     *             the reflection call
     */
    public int getKeyCode(String keyCodeName) {
        int code = -1;
        String codeName = "VK_" + keyCodeName; //$NON-NLS-1$
        if (codeName.equals("VK_CTRL")) { //$NON-NLS-1$
            codeName = "VK_CONTROL"; //$NON-NLS-1$
        }
        try {
            code = KeyEvent.class.getField(codeName).getInt(KeyEvent.class);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return code;
    }
    
    /**
     * @param modifierName
     *            The name of a modifier, e.g. SHIFT
     * @return The modifier mask or <code>-1</code>, if the modifier name doesn't
     *         exist in the <code>InputEvent</code> class
     *             If the modifier name cannot be converted to a mofifier mask due to
     *             the reflection call
     */
    public int getModifierMask(String modifierName) {
        int code = -1;
        String codeName = modifierName + "_MASK"; //$NON-NLS-1$
        try {
            code = InputEvent.class.getField(codeName).getInt(InputEvent.class);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return code;
    }
}

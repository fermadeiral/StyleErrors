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
package org.eclipse.jubula.rc.swt.listener;

import java.util.Iterator;
import java.util.SortedSet;

import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.swt.utils.SwtKeyCodeConverter;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;


/**
 * This class is responsible for accepting a KeyEvent as the (combination of
 * the) key(s) for picking a component in the OBJECT_MAPPING mode. <br>
 * 
 * This implementation accepts <Ctrl>+<Alt>+ <a>. (as default, but configurable in preferences)
 * 
 * @author BREDEX GmbH
 * @created 19.04.2006
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
     * The method queried by the <code>MappingListener</code>. 
     * @param event the occurred key event
     * @return 1, if the combination Ctrl+Alt+A or Ctrl+Shift+A was pressed<p>
     * 2, if Ctrl+Alt+S was pressed <p>
     * 0, otherwise
     */
    public int accept(Event event) {
        int keyMod = event.stateMask;
        int eventCode = 0;
        switch (event.button) {
            case 1:
                eventCode = InteractionMode.primary.rcIntValue();
                keyMod = keyMod & (~SWT.BUTTON1);
                break;
            case 2:
                eventCode = InteractionMode.tertiary.rcIntValue();
                keyMod = keyMod & (~SWT.BUTTON2);
                break;
            case 3:
                eventCode = InteractionMode.secondary.rcIntValue();
                keyMod = keyMod & (~SWT.BUTTON3);
                break;
            default:
                eventCode = event.keyCode;
                break;
        }
        if (eventCode == SWT.ALT) {
            keyMod = keyMod | 1 << 16;
        }
        if (eventCode == SWT.SHIFT) {
            keyMod = keyMod | 1 << 17;
        }
        if (eventCode == SWT.CTRL) {
            keyMod = keyMod | 1 << 18;
        }
        // the event gets an "a", but we need an "A" (for the complete alphabet)
        if (eventCode >= 97 && eventCode <= 122) {
            eventCode = eventCode - 32;  
        }
        if ((eventCode == AUTServerConfiguration.getInstance().getMappingKey()
                || eventCode 
                    == AUTServerConfiguration.getInstance()
                    .getMappingMouseButton())
            && keyMod == AUTServerConfiguration.getInstance()
                    .getMappingKeyMod()) {
            
            return MAPPING_KEY_COMB;
        }
        if (eventCode == AUTServerConfiguration.getInstance().getCheckModeKey()
            && keyMod == AUTServerConfiguration.getInstance()
                .getCheckModeKeyMod()) {
                
            return CHECKMODE_KEY_COMB;
        }
        if (eventCode == AUTServerConfiguration.getInstance().getCheckCompKey()
                && keyMod == AUTServerConfiguration.getInstance()
                    .getCheckCompKeyMod()) {
                    
            return CHECKCOMP_KEY_COMB;
        }
        return 0;
    }
    
    /**
     * @param event the occurred key event
     * @return true if the pressed key combination is a singleLineTrigger, false
     *         otherwise
     */
    public boolean isSingleLineTrigger(Event event) {
        boolean isSingleTrigger = false;
        int keycode = event.keyCode;
        char character = RecordHelperSWT.topKey(event);
        int modifier = event.stateMask;
        SortedSet singleTrigger = AUTServerConfiguration.getInstance()
            .getSingleLineTrigger();
        isSingleTrigger = isTrigger(
                singleTrigger, keycode, modifier, character);
        return isSingleTrigger;
    }
    
    /**
     * @param event the occurred key event
     * @return true if the pressed key combination is a multiLineTrigger, false
     *         otherwise
     */
    public boolean isMultiLineTrigger(Event event) {
        boolean isMultiTrigger = false;
        int keycode = event.keyCode;
        char character = RecordHelperSWT.topKey(event);
        int modifier = event.stateMask;
        SortedSet multiTrigger = AUTServerConfiguration.getInstance()
            .getMultiLineTrigger();
        isMultiTrigger = isTrigger(multiTrigger, keycode, modifier, character);
        return isMultiTrigger;
    }
    
    /**
     * @param triggerSet set of Triggers
     * @param key int
     * @param modifier int
     * @param character char
     * @return true if the pressed key combination is a trigger, false
     *         otherwise
     */
    public boolean isTrigger (SortedSet triggerSet, int key, int modifier,
            char character) {
        boolean isTrigger = false;
        int triggerCode = 0;
        char triggerChar = ' ';
        int triggerMod = 0;
        Iterator it = triggerSet.iterator();
        while (it.hasNext()) {
            String trigger = it.next().toString().toUpperCase();
            if (trigger.indexOf("+") != -1) { //$NON-NLS-1$
                String[] keys = trigger.split("\\+"); //$NON-NLS-1$
                switch (keys.length) {
                    case 2:
                        triggerMod = 
                            SwtKeyCodeConverter.getModifierCode(keys[0]);
                        triggerCode = SwtKeyCodeConverter.getKeyCode(keys[1]);
                        triggerChar = keys[1].charAt(0);
                        break;
                    case 3:
                        triggerMod = 
                            SwtKeyCodeConverter.getModifierCode(keys[0])
                            | SwtKeyCodeConverter.getModifierCode(keys[1]);
                        triggerCode = SwtKeyCodeConverter.getKeyCode(keys[2]);
                        triggerChar = keys[2].charAt(0);
                        break;
                    case 4:
                        triggerMod = 
                            SwtKeyCodeConverter.getModifierCode(keys[0])
                            | SwtKeyCodeConverter.getModifierCode(keys[1])
                            | SwtKeyCodeConverter.getModifierCode(keys[2]);
                        triggerCode = SwtKeyCodeConverter.getKeyCode(keys[3]);
                        triggerChar = keys[3].charAt(0);
                        break;
                    default:                    
                }
            } else {
                triggerMod = 0;
                triggerCode = SwtKeyCodeConverter.getKeyCode(trigger);
                triggerChar = SwtKeyCodeConverter
                    .getKeyChar(trigger).charValue();
            }
            if ((key == triggerCode || character == triggerChar)
                    && modifier == triggerMod) {
                isTrigger = true;
            }
        }
        return isTrigger;
    }
}
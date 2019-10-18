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
package org.eclipse.jubula.rc.swing.driver;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.swing.utils.SwingUtils;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;


/**
 * Utility Class to convert key descriptions into the virtual key codes.
 *
 * @author BREDEX GmbH
 * @created Jan 30, 2008
 */
public class KeyCodeConverter {
    
    /**
     * The Converter Map.
     */
    private static Map<String, Integer> converterTable = null;
    
    static {
        converterTable = new HashMap<String, Integer>();
        converterTable.put(ValueSets.Modifier.none.rcValue(), new Integer(-1));
        converterTable.put(ValueSets.Modifier.shift.rcValue(), 
                new Integer(KeyEvent.VK_SHIFT));
        converterTable.put(ValueSets.Modifier.control.rcValue(), 
                new Integer(KeyEvent.VK_CONTROL));
        converterTable.put(ValueSets.Modifier.alt.rcValue(), 
                new Integer(KeyEvent.VK_ALT));
        converterTable.put(ValueSets.Modifier.meta.rcValue(), 
                new Integer(KeyEvent.VK_META));
        converterTable.put(ValueSets.Modifier.cmd.rcValue(), 
                new Integer(KeyEvent.VK_META));
        converterTable.put(ValueSets.Modifier.mod.rcValue(),
                new Integer(SwingUtils.getSystemDefaultModifier()));
    }
    

    /**
     * Utility Constructor.
     */
    private KeyCodeConverter() {
        // nothing
    }
    
    /**
     * Gets the Virtual-Key-Code of the given key.
     * @param key a description of the key, e.g. "control", "alt", etc.
     * @return the Virtual-Key-Code of the given key or -1 if key is "none".
     */
    public static int getKeyCode(String key) {
        if (key == null) {
            throw new RobotException("Key is null!", //$NON-NLS-1$
                    EventFactory.createConfigErrorEvent());
        }
        final Integer keyCode = converterTable.get(key.toLowerCase());
        if (keyCode == null) {
            throw new RobotException("No KeyCode found for key '" + key + "'", //$NON-NLS-1$//$NON-NLS-2$
                    EventFactory.createConfigErrorEvent());
        }
        return keyCode.intValue();
    }
    
}

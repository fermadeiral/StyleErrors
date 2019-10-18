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
package org.eclipse.jubula.rc.swing.utils;

import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.UIManager;

/**
 * @author BREDEX GmbH
 * @created Jul 2, 2009
 */
public class SwingUtils {
    /**
     * ID of Metal Look and Feel.
     */
    private static final String METAL_LAF_ID = "Metal"; //$NON-NLS-1$
    
    /** 
     * Utility constructor
     */
    private SwingUtils() {
        // do nothing
    }
    
    /**
     * gives default modifier of the current OS.
     * 
     * @return meta (command) for OSX, control for Windows/Linux etc 
     */
    public static int getSystemDefaultModifier() {
        if (!(UIManager.getLookAndFeel().getID().equals(METAL_LAF_ID))) {
            int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            if (mask == Event.META_MASK) {
                return KeyEvent.VK_META;
            } else if (mask == Event.ALT_MASK) {
                return KeyEvent.VK_ALT;
            }
        }
        return KeyEvent.VK_CONTROL;
    }
    
    /**
     * @return the second system modifier
     */
    public static int getSystemModifier2() {
        return KeyEvent.VK_SHIFT;
    }

    /**
     * @return the third system modifier
     */
    public static int getSystemModifier3() {
        return KeyEvent.VK_ALT;
    }

    /**
     * @return the fourth system modifier; only available on Mac OS X
     */
    public static int getSystemModifier4() {
        return KeyEvent.VK_CONTROL;
    }
}

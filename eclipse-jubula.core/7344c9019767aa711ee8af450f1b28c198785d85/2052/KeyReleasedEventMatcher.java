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
import java.util.List;

import org.eclipse.jubula.rc.common.logger.AutServerLogger;


/**
 * Matches AWT key released events. Fallback matching for this matcher is always
 * true because its possible that we won't receive a key released event if, 
 * for example, the window that received the key pressed event was closed by 
 * the key press.
 *
 * @author BREDEX GmbH
 * @created Oct 31, 2007
 */
public class KeyReleasedEventMatcher extends DefaultAwtEventMatcher {

    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(KeyReleasedEventMatcher.class);
    
    /**
     * @param eventId
     */
    public KeyReleasedEventMatcher() {
        super(KeyEvent.KEY_RELEASED);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(
        List eventObjects, Object graphicsComponent) {
        // If the key press closes a window, then we receive no key released
        // event. As a result, we are forced to assume that the event occurred.
        log.warn("No event received for a key up event. This is most likely caused by a keystroke that closed a window"); //$NON-NLS-1$
        return true;
    }

}

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
package org.eclipse.jubula.rc.javafx.driver;

import java.util.List;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.KeyEvent;

import org.eclipse.jubula.rc.common.logger.AutServerLogger;

/**
 * Event matcher for key events.
 *
 * @author BREDEX GmbH
 * @created 1.11.2013
 */
public class KeyJavaFXEventMatcher extends DefaultJavaFXEventMatcher<KeyEvent> {
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            KeyJavaFXEventMatcher.class);
    /**
     * Creates a new matcher
     *
     * @param keyEvent
     *            the key event type that will be checked
     */
    public KeyJavaFXEventMatcher(EventType<KeyEvent> keyEvent) {
        super(keyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(List eventObjects,
            Object graphicsComponent) {
        for (Object object : eventObjects) {
            Event e = (Event) object;
            if (e instanceof KeyEvent) {
                log.warn("Key event matching fallback used."); //$NON-NLS-1$
                return true;
            }
        }
        return false;
    }
}
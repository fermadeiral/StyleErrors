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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.rc.swing.listener.ComponentHandler;


/**
 * Event matcher for key events. This implementation accepts any key event as
 * fallback. 
 *
 * @author BREDEX GmbH
 * @created 16.11.2005
 */
public class KeyAwtEventMatcher extends DefaultAwtEventMatcher {

    /**
     * Creates a new matcher 
     * @param eventId id of the event
     */
    public KeyAwtEventMatcher(int eventId) {
        super(eventId);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(List eventObjects, 
            Object graphicsComponent) {
        
        try {
            // checks if the component is visible (= in hierarchy conatiner)
            // and if the key-released event occurred
            if ((ComponentHandler.getAutHierarchy().getHierarchyContainer(
                            (Component)graphicsComponent) == null)
                && (KeyEvent.KEY_RELEASED == getEventId())) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            if (KeyEvent.KEY_RELEASED == getEventId()) {
                return true;
            }
        }
        for (Iterator it = eventObjects.iterator(); it.hasNext(); ) {
            AWTEvent event = (AWTEvent)it.next();
            if (isKeyEvent(event.getID())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the id belongs to a key event 
     * @param eventId the event id
     * @return <code>true</code> if the id belongs to a key event
     */
    private boolean isKeyEvent(int eventId) {
        return eventId >= KeyEvent.KEY_FIRST && eventId <= KeyEvent.KEY_LAST;
    } 
} 
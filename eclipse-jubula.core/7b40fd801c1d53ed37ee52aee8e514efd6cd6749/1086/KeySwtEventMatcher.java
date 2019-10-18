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
package org.eclipse.jubula.rc.swt.driver;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.rc.swt.listener.ComponentHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;


/**
 * Event matcher for key events. This implementation accepts any key event as
 * fallback. 
 *
 * @author BREDEX GmbH
 * @created 26.07.2006
 */
public class KeySwtEventMatcher extends DefaultSwtEventMatcher {

    /**
     * Creates a new matcher
     * @param eventId type of the event
     */
    public KeySwtEventMatcher(int eventId) {
        super(eventId);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(List eventObjects, 
            Object graphicsComponent) {
        
        try {
            // checks if the component is visible (= in hierarchy container)
            // and if the key-released event occurred
            if (graphicsComponent instanceof Combo) {
                // FIXME zeb Need some way of confirming keypresses on Combo Box with opened list
                return true;
            } else if (
                (ComponentHandler.getAutHierarchy().getHierarchyContainer(
                    (Control)graphicsComponent) == null)
                    && (SWT.KeyUp == getEventId())) {
        
                return true; 
            }
        } catch (IllegalArgumentException e) {
            if (SWT.KeyUp == getEventId()) {
                return true;
            }
        }
        for (Iterator it = eventObjects.iterator(); it.hasNext(); ) {
            Event event = (Event)it.next();
            if (isKeyEvent(event.type)) {
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
        return eventId >= SWT.KeyUp && eventId <= SWT.KeyUp;
    }
}
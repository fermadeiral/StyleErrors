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
package org.eclipse.jubula.rc.common.driver;

import java.util.List;

/**
 * Implementing classes check whether a Graphics API specific event object
 * matches the expected event conditions and properties. The event matcher
 * is used by the {@link org.eclipse.jubula.rc.common.driver.IRobotEventConfirmer}
 * to confirm an intercepted event which has been caused by a Robot
 * mouse click ore move.
 *
 * @author BREDEX GmbH
 * @created 21.03.2005
 */
public interface IEventMatcher {
    
    /** @return The Graphics API specific event ID. */
    public int getEventId();
    
    /**
     * Checks if the given event object matches the expected properties.
     * @param eventObject The Graphics API specific event object
     * @return <code>true</code> if the event matches, otherwise
     * <code>false</code>.
     */
    public boolean isMatching(Object eventObject);
    /**
     * Checks if the event list contains an event that doesn't match the rules
     * of <code>isMatching()</code>, but is valid as a fall back. Example:
     * Usually, in the Robot AWT implementation, the mouse click matches if the
     * mouse event is of type <code>MOUSE_CLICKED</code>. But in some
     * circumstances (e.g. if a click on a button disposes its parent window),
     * the <code>MOUSE_CLICKED</code> will never be dispatched by AWT, which
     * is correct. In this case of a mouse click, the matcher accepts the event
     * type <code>MOUSE_RELEASED</code>.
     * @param eventObjects A list of Graphics API specific event objects
     * @param graphicsComponent the current component
     * @return <code>true</code> if the matcher could find a fall back event,
     *         <code>false</code> otherwise
     */
    public boolean isFallBackEventMatching(List eventObjects, 
            Object graphicsComponent);
}
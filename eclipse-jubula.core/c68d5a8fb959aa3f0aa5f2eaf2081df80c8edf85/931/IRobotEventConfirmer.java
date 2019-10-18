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

import org.eclipse.jubula.rc.common.exception.RobotException;

/**
 * The event confirmer confirms an intercepted event which has been caused
 * by a Robot mouse click or move. Implementing classes examine the
 * Graphics API specific event queue to perform this operation.
 * 
 * @author BREDEX GmbH
 * @created 17.03.2005
 */
public interface IRobotEventConfirmer {
    /**
     * Waits for an event to occur by listening on the Graphics API specific
     * event queue. This method stops the current thread until the
     * expected event can be confirmed or a timeout happens.
     * 
     * @param eventTarget The graphics component the Robot event is
     * dispatched to.
     * @param matcher The event matcher to check whether the event matches
     * the expected properties.
     * @throws RobotException If a timeout occurs.
     */
    public void waitToConfirm(Object eventTarget, IEventMatcher matcher)
        throws RobotException;

    /**
     * Waits for an event to occur by listening on the Graphics API specific
     * event queue. This method stops the current thread until the
     * expected event can be confirmed or a timeout happens.
     * 
     * @param eventTarget The graphics component the Robot event is
     * dispatched to.
     * @param matcher The event matcher to check whether the event matches
     * the expected properties.
     * @param timeout The amount of time to wait (in milliseconds). If the 
     * expected event does not occur within this time, a timeout happens.
     * @throws RobotException If a timeout occurs.
     */
    public void waitToConfirm(Object eventTarget, IEventMatcher matcher, 
        long timeout) throws RobotException;

}

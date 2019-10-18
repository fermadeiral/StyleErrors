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

import org.eclipse.jubula.rc.common.driver.IEventMatcher;

/**
 * Eventmatcher should extend this class.
 *
 * @param <T>
 *            event type
 * @author BREDEX GmbH
 * @created 31.10.2013
 */
public class DefaultJavaFXEventMatcher<T extends Event> implements
        IEventMatcher {

    /** The JavaFX Event that is used as control */
    private EventType<T> m_eventToCheck;

    /**
     * Creates a new matcher which checks AWT events against the given event ID.
     *
     * @param eventType
     *            The JavaFX Event that is used as control.
     */
    public DefaultJavaFXEventMatcher(EventType<T> eventType) {
        m_eventToCheck = eventType;
    }

    @Override
    public int getEventId() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMatching(Object event) {
        return ((Event) event).getEventType() == m_eventToCheck;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(List eventObjects,
            Object graphicsComponent) {

        return false;
    }
}
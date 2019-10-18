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

import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

import org.eclipse.jubula.rc.common.driver.ClickOptions;

/**
 * This event matcher checks whether a mouse click event matches the requested
 * properties. The properties are defined by a <code>ClickOptions</code>
 * instance.
 *
 * @author BREDEX GmbH
 * @created 31.10.2013
 */
public class ClickJavaFXEventMatcher extends
        DefaultJavaFXEventMatcher<MouseEvent> {

    /** The click options. */
    private ClickOptions m_clickOptions;

    /**
     * Creates a new matcher instance.
     *
     * @param clickOptions
     *            The click options containing the properties the event is
     *            checked against.
     */
    public ClickJavaFXEventMatcher(ClickOptions clickOptions) {
        super(getClickOptionType(clickOptions));
        m_clickOptions = clickOptions;
    }

    /**
     * Converts the click type to the corresponding AWT event ID.
     *
     * @param cOpt
     *            The click options.
     * @return The event ID.
     */
    private static EventType<MouseEvent> getClickOptionType(ClickOptions cOpt) {
        if (cOpt.getClickType() == ClickOptions.ClickType.CLICKED) {

            return MouseEvent.MOUSE_CLICKED;
        }
        return MouseEvent.MOUSE_RELEASED;
    }

    /**
     * @param eventObject
     *            the AWT event
     * @return The click count if the event is a mouse event, <code>-1</code>
     *         otherwise
     */
    private int getClickCount(Object eventObject) {
        int count = -1;
        if (eventObject instanceof MouseEvent) {
            MouseEvent e = (MouseEvent) eventObject;
            count = e.getClickCount();
        }
        return count;
    }

    /**
     * @param eventObject
     *            the AWT event
     * @return <code>true</code> if the click count matches
     */
    private boolean isClickCountMatching(Object eventObject) {
        return getClickCount(eventObject) == m_clickOptions.getClickCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMatching(Object event) {
        return isClickCountMatching(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFallBackEventMatching(List eventObjects,
            Object graphicsComponent) {

        List<MouseEvent> caughtEvents = new ArrayList<MouseEvent>();
        for (Object object : eventObjects) {
            Event e = (Event)object;
            if (e != null && e.getEventType().
                    equals(MouseEvent.MOUSE_CLICKED)) {
                caughtEvents.add((MouseEvent) e);
            }
        }
        for (int i = 0; i < caughtEvents.size(); i++) {
            int clickEventCount = getClickCount(caughtEvents.get(i));
            for (int j = (i + 1); j < caughtEvents.size(); j++) {
                if (caughtEvents.get(i).getSource()
                        .equals(caughtEvents.get(j).getSource())) {
                    clickEventCount += getClickCount(caughtEvents.get(j));  
                }
            }
            if (clickEventCount == m_clickOptions.getClickCount()) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        String str = this.getClass().getName() + " ClickOptions: " //$NON-NLS-1$
                + m_clickOptions.toString();
        return str;
    }

}
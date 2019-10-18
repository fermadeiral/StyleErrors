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
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.swing.listener.ComponentHandler;


/**
 * This event matcher checks whether a mouse click event matches the
 * requested properties. The properties are defined by a
 * <code>ClickOptions</code> instance.
 * @author BREDEX GmbH
 * @created 21.03.2005
 */
public class ClickAwtEventMatcher extends DefaultAwtEventMatcher {
    
    /** The click options. */
    private ClickOptions m_clickOptions;
    
    /**
     * Creates a new matcher instance. 
     * @param clickOptions  The click options containing the properties the event is checked against.
     */
    public ClickAwtEventMatcher(ClickOptions clickOptions) {
        super(getMouseEventId(clickOptions));
        m_clickOptions = clickOptions;
    }
    
    /**
     * Converts the click type to the corresponding AWT event ID. 
     * @param clickOptions The click options.
     * @return The event ID.
     */
    private static int getMouseEventId(ClickOptions clickOptions) {
        return (clickOptions.getClickType()
                == ClickOptions.ClickType.CLICKED) ? MouseEvent.MOUSE_CLICKED
                    : MouseEvent.MOUSE_RELEASED;
    }
    
    /**
     * @param eventObject the AWT event
     * @return The click count if the event is a mouse event, <code>-1</code> otherwise
     */
    private int getClickCount(Object eventObject) {
        int count = -1;
        if (eventObject instanceof MouseEvent) {
            MouseEvent e = (MouseEvent)eventObject;
            count = e.getClickCount();
        }
        return count;
    }
    
    /**
     * @param eventObject the AWT event
     * @return <code>true</code> if the click count matches
     */
    private boolean isClickCountMatching(Object eventObject) {
        return getClickCount(eventObject) == m_clickOptions.getClickCount();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isMatching(Object eventObject) {
        return super.isMatching(eventObject)
            ? isClickCountMatching(eventObject)
            : false;
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
                && (m_clickOptions.getClickType() == ClickOptions.ClickType
                        .RELEASED || m_clickOptions.getClickCount() == 0)) {
            
                return true;
            }
        } catch (IllegalArgumentException e) {
            if (m_clickOptions.getClickType() == ClickOptions.ClickType.RELEASED
                    || m_clickOptions.getClickCount() == 0) {
                    
                return true;
            }
        }
        for (Iterator it = eventObjects.iterator(); it.hasNext();) {
            AWTEvent event = (AWTEvent)it.next();
            if (isClickCountMatching(event)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        String str = this.getClass().getName() + " ClickOptions: "  //$NON-NLS-1$
            + m_clickOptions.toString();
        return str;
    }
    
    
}
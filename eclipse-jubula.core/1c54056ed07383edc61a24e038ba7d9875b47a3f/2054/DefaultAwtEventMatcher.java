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
import java.util.List;

import org.eclipse.jubula.rc.common.driver.IEventMatcher;


/**
 * This matcher checks whether the AWT event has a given event ID.
 *
 * @author BREDEX GmbH
 * @created 21.03.2005
 */
public class DefaultAwtEventMatcher implements IEventMatcher {
    
    /** The AWT event ID. */
    private int m_eventId;
    
    /**
     * Creates a new matcher which checks AWT events against the given event ID.
     * @param eventId The AWT event ID.
     */
    public DefaultAwtEventMatcher(int eventId) {
        m_eventId = eventId;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getEventId() {
        return m_eventId;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isMatching(Object eventObject) {
        return ((AWTEvent)eventObject).getID() == getEventId();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(List eventObjects, 
            Object graphicsComponent) {
        
        return false;
    }
}
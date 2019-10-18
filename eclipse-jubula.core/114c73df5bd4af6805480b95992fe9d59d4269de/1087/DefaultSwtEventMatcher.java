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

import java.util.List;

import org.eclipse.jubula.rc.common.driver.IEventMatcher;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


/**
 * This matcher checks whether the SWT event has a given event ID.
 *
 * @author BREDEX GmbH
 * @created 25.07.2006
 */
public class DefaultSwtEventMatcher implements IEventMatcher {
    
    /** Constant for matching all events */
    // Set to SWT.None because it wouldn't make sense to listen for NO events
    public static final int ALL_EVENTS = SWT.None;
    
    /** The SWT event type. */
    private int m_eventId;
    
    /**
     * Creates a new matcher which checks SWT events against the given event type.
     * @param eventId The SWT event type.
     */
    public DefaultSwtEventMatcher(int eventId) {
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
        return ALL_EVENTS == getEventId() 
            || ((Event)eventObject).type == getEventId();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(List eventObjects, Object comp) {
        if (comp instanceof CCombo) {
            // special handling for http://eclip.se/436663#c3
            CCombo combo = (CCombo) comp;
            for (Object o : eventObjects) {
                Event e = (Event) o;
                Widget w = e.widget;
                if (w instanceof Control) {
                    final Control c = (Control) w;
                    Composite parent = new EventThreadQueuerSwtImpl().invokeAndWait("getParent", new IRunnable<Composite>() { //$NON-NLS-1$
                            public Composite run() {
                                return c.getParent();
                            }
                        });
                    if (parent == combo && isMatching(e)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
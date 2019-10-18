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
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.eclipse.jubula.rc.common.driver.IMouseMotionTracker;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;


/**
 * The AWT mouse motion tracker stores the last mouse motion AWT event.
 * Therefor, it adds an {@link java.awt.event.AWTEventListener} to the AWT event
 * queue using the <code>AWTEvent.MOUSE_MOTION_EVENT_MASK</code>.
 * 
 * @author BREDEX GmbH
 * @created 18.03.2005
 */
public class MouseMotionTrackerAwtImpl implements IMouseMotionTracker {
    /**
     * The logger.
     */
    private static AutServerLogger log = 
        new AutServerLogger(MouseMotionTrackerAwtImpl.class);
    /**
     * The last mouse motion event.
     */
    private static MouseEvent lastMouseEvent;
    /**
     * The last mouse motion event point in absolute coordinates.
     */
    private static Point lastMousePointOnScreen;

    static {
        Toolkit.getDefaultToolkit().addAWTEventListener(new MyEventListener(),
            AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }
    /**
     * The AWT listener implementation.
     */
    private static class MyEventListener implements AWTEventListener {
        /**
         * {@inheritDoc}
         */
        public void eventDispatched(AWTEvent event) {
            if (event instanceof MouseEvent
                && event.getID() == MouseEvent.MOUSE_MOVED) {
                synchronized (MouseMotionTrackerAwtImpl.class) {
                    lastMouseEvent = (MouseEvent)event;
                    lastMousePointOnScreen = new Point(lastMouseEvent
                        .getPoint());
                    SwingUtilities.convertPointToScreen(lastMousePointOnScreen,
                        (Component)lastMouseEvent.getSource());
                    if (log.isDebugEnabled()) {
                        log.debug("MouseEvent tracked: " + lastMouseEvent); //$NON-NLS-1$
                        log.debug("Screen point      : " + //$NON-NLS-1$
                            lastMousePointOnScreen);
                    }
                }
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public Object getLastMouseMotionEvent() {
        synchronized (MouseMotionTrackerAwtImpl.class) {
            return lastMouseEvent;
        }
    }
    /**
     * {@inheritDoc}
     */
    public Point getLastMousePointOnScreen() {
        return lastMousePointOnScreen;
    }
}

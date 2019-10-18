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
package org.eclipse.jubula.rc.javafx.tester.util;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;

/**
 * Utility class for converting an AWT event type to an JavaFX event type
 *
 */
public class JavaFXEventConverter {
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            JavaFXEventConverter.class);

    /**
     * private Constructor
     */
    private JavaFXEventConverter() {
        // private Constructor
    }

    /**
     * Converts a given AWT Event-Mask to a JavaFX event
     *
     * @param eventMask
     *            the AWT Event-Mask
     * @return a JavaFX event Type or null if the AWT Event-Mask is not handled.
     *         This shouldn't happen and will be logged.
     */
    public static EventType<? extends Event> awtToFX(long eventMask) {

        EventType<? extends Event> fxEvent = null;
        if (java.awt.AWTEvent.ACTION_EVENT_MASK == eventMask) {
            fxEvent = ActionEvent.ACTION;
        } else if (java.awt.AWTEvent.MOUSE_EVENT_MASK == eventMask) {
            fxEvent = MouseEvent.ANY;
        } else if (java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK == eventMask) {
            if (DragAndDropHelper.getInstance().isDragMode()) {
                fxEvent = MouseEvent.MOUSE_DRAGGED;
            } else {
                fxEvent = MouseEvent.MOUSE_MOVED;
            }
        } else if (java.awt.AWTEvent.MOUSE_WHEEL_EVENT_MASK == eventMask) {
            fxEvent = ScrollEvent.SCROLL;
        } else if (java.awt.AWTEvent.KEY_EVENT_MASK == eventMask) {
            fxEvent = KeyEvent.ANY;
        }
        if (fxEvent == null) {
            if (log.isInfoEnabled()) {
                log.info("Could not find a JavaFX event for: " //$NON-NLS-1$
                        + eventMask);
            }
        }
        return fxEvent;
    }
}

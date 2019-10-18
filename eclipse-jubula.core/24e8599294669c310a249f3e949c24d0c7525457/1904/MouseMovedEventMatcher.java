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

import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

/**
 * Matches MOUSE_MOVED or MOUSE_DRAGGED.
 *
 * @author BREDEX GmbH
 * @created 1.11.2013
 */
public class MouseMovedEventMatcher extends
        DefaultJavaFXEventMatcher<MouseEvent> {

    /**
     * Constructor.
     *
     * @param event
     *            the move event type that will be checked
     */
    public MouseMovedEventMatcher(EventType<MouseEvent> event) {
        super(event);
    }

}

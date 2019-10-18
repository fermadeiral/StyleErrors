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
import java.awt.event.MouseEvent;


/**
 * Matches whether MOUSE_MOVED or MOUSE_DRAGGED.
 *
 * @author BREDEX GmbH
 * @created Jan 30, 2008
 */
public class MouseMovedAwtEventMatcher extends DefaultAwtEventMatcher {

    /**
     * Constructor.
     */
    public MouseMovedAwtEventMatcher() {
        super(MouseEvent.MOUSE_MOVED | MouseEvent.MOUSE_DRAGGED);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isMatching(Object eventObject) {
        final int id = ((AWTEvent)eventObject).getID();
        return MouseEvent.MOUSE_MOVED == id 
            || MouseEvent.MOUSE_DRAGGED == id;
    }

}

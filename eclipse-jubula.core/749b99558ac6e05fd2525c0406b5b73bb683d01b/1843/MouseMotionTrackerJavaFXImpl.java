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

import java.awt.MouseInfo;
import java.awt.Point;

import org.eclipse.jubula.rc.common.driver.IMouseMotionTracker;

/**
 * Doesn't really track the mouse, because this is not necessary and would cause
 * an unnecessary performance overhead. To get the mouse position
 * MouseInfo.getPointerInfo().getLocation() is used.
 *
 * @author BREDEX GmbH
 * @created 1.11.2013
 */
public class MouseMotionTrackerJavaFXImpl implements IMouseMotionTracker {
    /**
     * Add a new Instance of a Mouse Move tracker to all Stages in the Hierarchy
     */
    public MouseMotionTrackerJavaFXImpl() {

    }

    /**
     * This is not implemented, because it is currently not used!
     *
     * @return null
     */
    public Object getLastMouseMotionEvent() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Point getLastMousePointOnScreen() {
        return MouseInfo.getPointerInfo().getLocation();
    }
}

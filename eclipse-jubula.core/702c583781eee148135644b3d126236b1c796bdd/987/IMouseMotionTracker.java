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
package org.eclipse.jubula.rc.common.driver;

import java.awt.Point;

/**
 * Implementing classes track the Graphics API specific mouse motion events.
 * They store the latest event.
 *
 * @author BREDEX GmbH
 * @created 18.03.2005
 */
public interface IMouseMotionTracker {
    /**
     * @return The latest stored mouse motion event or <code>null</code> if
     * there was no event.
     */
    public Object getLastMouseMotionEvent();
    /**
     * @return The latest stored mouse motion event point. The point contains
     *         absolute screen coordinates. It's allright to use the AWT Point
     *         type here, as this class is part of the Java Standard Edition
     *         API.
     */
    public Point getLastMousePointOnScreen();
}

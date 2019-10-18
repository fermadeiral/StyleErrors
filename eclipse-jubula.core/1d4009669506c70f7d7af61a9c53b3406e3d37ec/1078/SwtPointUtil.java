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
package org.eclipse.jubula.rc.swt.utils;

import org.eclipse.jubula.rc.common.util.PointUtil;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


/**
 * @author BREDEX GmbH
 * @created 01.09.2009
 */
public class SwtPointUtil extends PointUtil {
    /**
     * Returns an SWT point with the same coordinates as the specified AWT
     * point.
     * 
     * @param p
     *            the AWT point (<code>null</code> not permitted).
     * 
     * @return An SWT point with the same coordinates as <code>p</code>.
     */
    public static Point toSwtPoint(java.awt.Point p) {
        return new Point(p.x, p.y);
    }

    /**
     * @param constraints
     *            The rectangle to move to
     * @param xPos
     *            xPos in component
     * @param yPos
     *            yPos in component
     * @param xAbsolute
     *            true if x-position should be absolute
     * @param yAbsolute
     *            true if y-position should be absolute
     * @return the point to go for the robot
     */
    public static Point calculatePointToGo(final int xPos,
            final boolean xAbsolute, final int yPos, final boolean yAbsolute,
            final Rectangle constraints) {
        return toSwtPoint(calculateAwtPointToGo(xPos, xAbsolute, yPos,
                yAbsolute, toAwtRectangle(constraints)));
    }

    /**
     * Returns an <code>org.eclipse.swt.graphics.Rectangle</code> with the same
     * attributes as the given <code>java.awt.Rectangle</code>.
     * @param awtRect   the <code>java.awt.Rectangle</code>
     * @return  an <code>org.eclipse.swt.graphics.Rectangle</code> that 
     *          matches the <code>awtRect</code>
     */
    public static Rectangle toSwtRectangle(java.awt.Rectangle awtRect) {
        Rectangle swtRect = new Rectangle(
                awtRect.x, 
                awtRect.y, 
                awtRect.width, 
                awtRect.height);
    
        return swtRect;
    }

    /**
     * Returns a <code>java.awt.Rectangle</code> with the same
     * attributes as the given <code>org.eclipse.swt.graphics.Rectangle</code>.
     * @param swtRect   the <code>org.eclipse.swt.graphics.Rectangle</code>
     * @return  a <code>java.awt.Rectangle</code> that 
     *          matches the <code>swtRect</code>
     */
    public static java.awt.Rectangle toAwtRectangle(Rectangle swtRect) {
        java.awt.Rectangle awtRect = 
            new java.awt.Rectangle(
                swtRect.x, 
                swtRect.y, 
                swtRect.width, 
                swtRect.height);
    
        return awtRect;
    }
}

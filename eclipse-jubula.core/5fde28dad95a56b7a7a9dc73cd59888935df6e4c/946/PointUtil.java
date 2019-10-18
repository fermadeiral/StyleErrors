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
package org.eclipse.jubula.rc.common.util;

import java.awt.Point;
import java.awt.Rectangle;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;


/**
 * @author BREDEX GmbH
 * @created 01.09.2009
 */
public class PointUtil {
    
    /** Constructor */
    protected PointUtil() {
    // empty
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
    public static Point calculateAwtPointToGo(final int xPos,
            final boolean xAbsolute, final int yPos, final boolean yAbsolute,
            final Rectangle constraints) {
        Point pointToGo = new Point(constraints.x, constraints.y);
        if (xAbsolute) {
            pointToGo.x += xPos;
        } else {
            int x = (int)((constraints.width / 100.0) * xPos);
            pointToGo.x += x;
        }
        if (yAbsolute) {
            pointToGo.y += yPos;
        } else {
            int y = (int)((constraints.height / 100.0) * yPos);
            pointToGo.y += y;
        }
        pointToGo = adjustPointToGo(pointToGo, constraints);
        if (!constraints.contains(pointToGo)) {
            throw new StepExecutionException(
                    TestErrorEvent.CLICKPOINT_INVALID,
                    EventFactory.createActionError(
                            TestErrorEvent.CLICKPOINT_INVALID));
        }
        return pointToGo;
    }
    
    /**
     * @param sRectangle
     *            a string formatted like x:y:width:height representing a
     *            rectangle
     * @return a rectangle representing the given string rectangle
     */
    public static Rectangle stringAsRectangle(String sRectangle) {
        String[] aBounds = StringUtils.split(sRectangle, ':');
        if (aBounds.length != 4) {
            throw new IllegalArgumentException(
                    "non-well formatted sRectangle: " //$NON-NLS-1$
                            + sRectangle);
        }
        int x = Integer.valueOf(aBounds[0]).intValue();
        int y = Integer.valueOf(aBounds[1]).intValue();
        int width = Integer.valueOf(aBounds[2]).intValue();
        int height = Integer.valueOf(aBounds[3]).intValue();
        return new Rectangle(x, y, width, height);
    }
    
    /**
     * Adjusts the point to go if the coordinate is exactly on an edge of the
     * widgetbounds.
     * 
     * @param pointToGo
     *            the point to go.
     * @param widgetBounds
     *            the widget bounds.
     * @return the corrected pointToGo.
     */
    private static Point adjustPointToGo(Point pointToGo, 
        Rectangle widgetBounds) {
        final int x = widgetBounds.x + widgetBounds.width;
        final int y = widgetBounds.y + widgetBounds.height;
        if (pointToGo.x == x) {
            pointToGo.x--;
        }
        if (pointToGo.y == y) {
            pointToGo.y--;
        }
        if (pointToGo.x == widgetBounds.x) {
            pointToGo.x++;
        }
        if (pointToGo.y == widgetBounds.y) {
            pointToGo.y++;
        }

        return pointToGo;
    }
}

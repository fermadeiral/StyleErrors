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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;

/**
 * Encapsulates the pathing for the mouse pointer during testing. 
 *
 * @author BREDEX GmbH
 * @created Oct 13, 2008
 */
public class MouseMovementStrategy {
    
    /** */
    private static boolean isMouseMovementDisabled = false;
    /** */
    private static String disableMouseMovement = "TEST_DISABLE_MOUSE_MOVEMENT"; //$NON-NLS-1$

    
    static {
        String value = EnvironmentUtils
                .getProcessOrSystemProperty(disableMouseMovement);
        isMouseMovementDisabled = Boolean.valueOf(value).booleanValue();
    }
    /**
     * Private constructor for utility class
     */
    private MouseMovementStrategy() {
        // Do nothing
    }
       
    /**
     * 
     * @param from The point from which the mouse pointer is being moved.
     *             May not be <code>null</code>. Coordinates must be 
     *             non-negative.
     * @param to The point to which the mouse pointer is being moved.
     *           May not be <code>null</code>. Coordinates must be 
     *           non-negative.
     * @param isMoveInSteps <code>true</code> if the movement strategy should 
     *                      be executed in steps. Otherwise, <code>false</code>.
     * @param firstHorizontal <code>true</code> if the movement strategy should
     *                      be executed using first the x axis. Otherwise, 
     *                      <code>false</code>.
     * @return an array of <code>Point</code>s indicating the path the pointer
     *         should follow in order to reach the destination point 
     *         <code>to</code>. This path includes the point <code>to</code>, 
     *         but does not contain <code>from</code>.
     */
    public static Point [] getMovementPath(Point from, Point to, 
            boolean isMoveInSteps, boolean firstHorizontal) {
        Validate.notNull(to, "End point must not be null."); //$NON-NLS-1$
        Validate.isTrue(to.x >= 0, "End x-coordinate must not be negative."); //$NON-NLS-1$
        Validate.isTrue(to.y >= 0, "End y-coordinate must not be negative."); //$NON-NLS-1$
        if (isMouseMovementDisabled) {
            return new Point[] {to};
        }
        Validate.notNull(from, "Initial point must not be null."); //$NON-NLS-1$
        Validate.isTrue(from.x >= 0, "Initial x-coordinate must not be negative."); //$NON-NLS-1$
        Validate.isTrue(from.y >= 0, "Initial y-coordinate must not be negative."); //$NON-NLS-1$
        
        if (!isMoveInSteps) {
            // Adjacent point followed by target point
            return new Point [] {new Point(to.x - 1, to.y - 1), to};
        }
        
        List<Point> path = new ArrayList<Point>();
        int [] xCoords = getMovementPath(from.x, to.x);
        int [] yCoords = getMovementPath(from.y, to.y);
        if (firstHorizontal) {
            for (int i = 0; i < xCoords.length; i++) {
                path.add(new Point(xCoords[i], from.y));
            }
            for (int i = 0; i < yCoords.length; i++) {
                path.add(new Point(to.x, yCoords[i]));
            }            
        } else {
            for (int i = 0; i < yCoords.length; i++) {
                path.add(new Point(from.x, yCoords[i]));
            }
            for (int i = 0; i < xCoords.length; i++) {
                path.add(new Point(xCoords[i], to.y));
            }
        }


        if (path.isEmpty() || !to.equals(path.get(path.size() - 1))) {
            path.add(new Point(to));
        }
        
        return path.toArray(new Point [path.size()]);
    }

    /**
     * 
     * @param from The number at which we are starting.
     * @param to The number to which the path must lead.
     * @return a path between the given numbers.
     */
    private static int [] getMovementPath(int from, int to) {
        int diff = to - from;
        
        // Should the movement be in the positive, or the negative "direction"
        int direction = diff != 0 ? diff / Math.abs(diff) : 1;

        int current = from;
        int [] retVal = new int [Math.abs(diff)];

        for (int i = 0; i < retVal.length; i++) {
            current += direction;
            retVal[i] = current;
        }

        return retVal;
    }
}

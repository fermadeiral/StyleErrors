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

import java.awt.Rectangle;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Helperclass for checking node bounds
 */
public class NodeBounds {

    /**
     * Private Constructor
     */
    private NodeBounds() {
        // Empty
    }

    /**
     * Must be called from FX Thread.
     * 
     * Checks if the given point with coordinates relative to the scene is in
     * the given Node.
     *
     * @param point
     *            the Point
     * @param n
     *            the Node
     * @return true if the Point is in the Node, false if not.
     */
    public static boolean checkIfContains(Point2D point, Node n) 
        throws IllegalStateException {
        
        EventThreadQueuerJavaFXImpl.checkEventThread();
        
        if (n.getScene() == null) {
            return false;
        }
        
        Point2D nodePos = n.localToScreen(0, 0);

        // A null value here means that the Node is not in a Window, so its 
        // (non-existent) bounds cannot contain the given point.
        if (nodePos == null) {
            return false;
        }
        return n.contains(n.screenToLocal(point.getX(), point.getY()));
    }

    /**
     * Must be called from FX Thread.
     * 
     * @param node The component for which to get the bounds.
     * @return the absolute (screen) bounds of the given component.
     * 
     * @throws NullPointerException if <code>node</code> is not in a 
     *                              {@link Window}.
     * @throws IllegalStateException if not called from the FX Thread. 
     */
    public static Rectangle getAbsoluteBounds(Node node) 
        throws NullPointerException, IllegalStateException {
        
        EventThreadQueuerJavaFXImpl.checkEventThread();

        Bounds bounds = node.localToScreen(node.getBoundsInLocal());
        return new Rectangle(
                Rounding.round(bounds.getMinX()), 
                Rounding.round(bounds.getMinY()), 
                Rounding.round(bounds.getWidth()), 
                Rounding.round(bounds.getHeight()));
    }
    
    /**
     * Must be called from FX Thread.
     * 
     * @param node The component for which to get the bounds.
     * @param relativeTo The component to use as a base location.
     * @return the bounds of <code>node</code>, relative to the location of 
     *         <code>relativeTo</code>.
     *         
     * @throws NullPointerException if <code>node</code> or 
     *                              <code>relativeTo</code> is not in a 
     *                              {@link Window}.
     * @throws IllegalStateException if not called from the FX Thread. 
     */
    public static Rectangle getRelativeBounds(Node node, Node relativeTo) 
        throws NullPointerException, IllegalStateException {
        
        EventThreadQueuerJavaFXImpl.checkEventThread();
        
        Rectangle bounds = getAbsoluteBounds(node);
        Rectangle otherAbsoluteBounds = getAbsoluteBounds(relativeTo);
        bounds.translate(-otherAbsoluteBounds.x, -otherAbsoluteBounds.y);
        
        return bounds;
    }
}

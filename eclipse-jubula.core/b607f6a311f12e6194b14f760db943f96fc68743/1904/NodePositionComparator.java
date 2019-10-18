/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
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
import java.util.Comparator;

import javafx.scene.Node;

/**
 * Compares the positions of Nodes. The order is first compare y-coordinates and
 * if they are equal compare the x-coordinates. In both cases the smaller value
 * leads to the corresponding to be ordered before the other.
 * 
 * @author BREDEX GmbH
 * @created 25.03.2014
 */
public class NodePositionComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        Rectangle o1Bounds = NodeBounds.getAbsoluteBounds(o1);
        Rectangle o2Bounds = NodeBounds.getAbsoluteBounds(o2);
        // First check which node is on top
        int compareY = Integer.compare(o1Bounds.y, o2Bounds.y);
        // They are at the same height, check which node is the leftmost node
        if (compareY == 0) {
            return Integer.compare(o1Bounds.x, o2Bounds.x);
        }
        return compareY;
    }

}

/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextComponent;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Utility class for finding instances of a certain type in the hierarchy below
 * a given node
 * 
 * @author BREDEX GmbH
 * @created 25.03.2014
 */
public class NodeTraverseHelper {
    
    /**
     * Constructor
     */
    private NodeTraverseHelper() {
        //private
    }

    /**
     * Finds instances of a certain type in the hierarchy below a given node
     * 
     * @param <T> the type
     * @param parent the parent
     * @param type the type
     * @param r the result
     * @return the result
     */
    private static <T> List<T> findInstancesOf(Parent parent,
            Class<T> type, List<T> r) {
        List<T> result = r;
        for (Node object : parent.getChildrenUnmodifiable()) {
            if (type.isAssignableFrom(object.getClass())) {
                result.add((T) object);
            }
            if (object instanceof Parent) {
                result = findInstancesOf((Parent) object, type, result);
            }
        }
        return result;
    }

    /**
     * Gives instances of a certain type in the hierarchy below a given node
     * 
     * @param <T> the type
     * @param parent the parent
     * @param type the type
     * @return returns all instances of the given type which are below the
     *         parent in the hierarchy
     */
    public static <T> List<T> getInstancesOf(Parent parent, 
            Class<T> type) {
        return findInstancesOf(parent, type, new ArrayList<T>());
    }

    /**
     * Checks if the given node is under the given parent in the scene graph
     * 
     * @param node the possible child node
     * @param parent the parent
     * @return true if the given node is related to the given parent, false if
     *         not
     */
    public static boolean isChildOf(Node node, Parent parent) {
        boolean result = false;
        for (Node n : parent.getChildrenUnmodifiable()) {
            if (!result) {
                if (n == node) {
                    return true;
                }
                if (n instanceof Parent) {
                    result = isChildOf(node, (Parent) n);
                }
            }
        }
        return result;
    }
    
    /**
     * Checks if a given Node is Visible by checking if all parent nodes are visible
     * @param node the node 
     * @return true if visible, false otherwise
     */
    public static boolean isVisible(Node node) {
        if (node == null) {
            return false;
        }
        Node tmp = node;
        while (tmp != null) {
            if (!tmp.isVisible()) {
                return false;
            }
            tmp = tmp.getParent();
        }
        return true;
    }
    
    /**
     * Find and collect strings under the given parent. This is done by looking
     * for a ITextComponent Adapter for each child and then call getText. <br>
     * <b>1. Commandment:</b> <br> Thou shall not call this method from a child for its parent.
     * 
     * This would lead to an endless recursion and doom mankind to an endless
     * struggle with debugging.
     * 
     * @param parent
     *            the parent
     * @return List containing the strings
     */
    public static List<String> findStrings(Parent parent) {
        ArrayList<String> renderedStrings = new ArrayList<>();
        List<Node> children = new ArrayList<>();
        
        for (Node n : new ArrayList<Node>(parent.getChildrenUnmodifiable())) {
            // Only add children which are part of a rendered scene and when
            // bounds calculation is possible
            if (n.localToScreen(n.getBoundsInLocal()) != null) {
                children.add(n);
            }
        }
        children.sort(new NodePositionComparator());
        for (Node n : children) {
            IComponent adapter = (IComponent) AdapterFactoryRegistry
                    .getInstance().getAdapter(IComponent.class, n);
            if (adapter != null && adapter instanceof ITextComponent) {
                String text = ((ITextComponent) adapter).getText();
                if (text != null) {
                    renderedStrings.add(text);
                } else if (text == null && n instanceof Parent) {
                    renderedStrings.addAll(findStrings((Parent) n));
                }
            } else if (n instanceof Parent) {
                renderedStrings.addAll(findStrings((Parent) n));
            }
        }
        return renderedStrings;
    }
    
}

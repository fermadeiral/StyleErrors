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
package org.eclipse.jubula.rc.javafx.components;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.stage.Window;

/**
 * This class handles the receiving of Parents from different components from a
 * Hierarchical point of view.
 *
 * @author BREDEX GmbH
 * @created 25.10.2013
 */
public class ParentGetter {

    /**
     * Private Constructor
     */
    private ParentGetter() {
        // private
    }

    /**
     * Returns the Parent of a given Object. Will return null if the given
     * Object has no Parent or isn't handled.
     *
     * @param o
     *            the object
     * @return the Parent as Object.
     */
    public static EventTarget get(EventTarget o) {
        EventTarget result = null;

        if (o instanceof Menu) {
            result = getFrom((Menu) o);
        } else if (o instanceof Node) {
            result = getFrom((Node) o);
        } else if (o instanceof Scene) {
            result = getFrom((Scene) o);
        } else if (o instanceof ContextMenu) {
            result = getFrom((ContextMenu) o);
        }
        return result;
    }

    /**
     * Gets the Node which is the owner of this ContextMenu
     * @param o the ContextMenu
     * @return the owner Node
     */
    private static Node getFrom(ContextMenu o) {
        return o.getOwnerNode();
    }

    /**
     *
     * @param scene
     *            the Scene
     * @return the Parent of the Scene a Window.
     */
    private static Window getFrom(Scene scene) {
        return scene.getWindow();
    }

    /**
     *
     * @param node
     *            the Node
     * @return the Parent of the Node or the Scene if the given Node is the Root
     *         node.
     */
    private static EventTarget getFrom(Node node) {
        EventTarget parent = node.getParent();
        if (parent == null) {
            parent = node.getScene();
        }
        return parent;
    }

    /**
     *
     * @param menu
     *            the Menu
     * @return Returns the parent Menu if the given Menu is a sub Menu or the
     *         owner Node
     */
    private static EventTarget getFrom(Menu menu) {
        EventTarget parent = menu.getParentMenu();
        if (parent == null) {
            parent = menu.getParentPopup().getOwnerNode();
        }
        return parent;
    }
}